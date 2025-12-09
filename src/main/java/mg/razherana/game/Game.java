package mg.razherana.game;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import mg.razherana.game.gfx.GameFrame;
import mg.razherana.game.gfx.GamePanel;
import mg.razherana.game.logic.GameObject;
import mg.razherana.game.logic.objects.ball.Ball;
import mg.razherana.game.logic.objects.board.Board;
import mg.razherana.game.logic.objects.board.BoardBorder;
import mg.razherana.game.logic.objects.chesspiece.ChessPiece;
import mg.razherana.game.logic.players.Player;
import mg.razherana.game.logic.utils.Assets;
import mg.razherana.game.logic.utils.Config;
import mg.razherana.game.logic.utils.Vector2;

/**
 * Contains the game instance and logic.
 */
public class Game {
  private Thread gameThread;

  // Game objects rendered in a priority queue based on their priority
  private List<GameObject> gameObjects = new ArrayList<>();

  private volatile boolean running = false;

  private final Config config;

  private GameFrame gameFrame;
  private GamePanel gamePanel;

  private final Assets assets = new Assets();

  // Timing variables
  private final double TARGET_FPS = 60.0;

  private final double UPDATE_CAP = 1.0 / TARGET_FPS; // 60 updates/sec

  private final Object lock = new Object();
  private List<Player> players = new ArrayList<>();

  public Game() {
    // Init config
    try {
      config = new Config();
    } catch (IOException e) {
      throw new RuntimeException("Failed to load config file", e);
    }

    // Initialize game components here
    gameFrame = new GameFrame(this);
    gamePanel = new GamePanel(this);

    gameFrame.init(gamePanel);

    initAssets();

    initGameObjects();

    run();
  }

  /**
   * @return the config
   */
  public Config getConfig() {
    return config;
  }

  public List<Player> getPlayers() {
    return players;
  }

  /**
   * @return the assets
   */
  public Assets getAssets() {
    return assets;
  }

  /**
   * @return the gameThread
   */
  public Thread getGameThread() {
    return gameThread;
  }

  /**
   * @param gameThread the gameThread to set
   */
  public void setGameThread(Thread gameThread) {
    this.gameThread = gameThread;
  }

  public void addGameObject(GameObject obj) {
    synchronized (lock) {
      gameObjects.add(obj);
      gameObjects.sort((a, b) -> Integer.compare(a.getPriority(), b.getPriority()));
    }
  }

  public void removeGameObject(GameObject obj) {
    synchronized (lock) {
      gameObjects.remove(obj);
      gameObjects.sort((a, b) -> Integer.compare(a.getPriority(), b.getPriority()));
    }
  }

  /**
   * @return the gameObjects
   */
  public List<GameObject> getGameObjects() {
    return gameObjects;
  }

  public List<GameObject> getGameObjectsAsList() {
    synchronized (lock) {
      return List.copyOf(getGameObjects());
    }
  }

  /**
   * @param gameObjects the gameObjects to set
   */
  public void setGameObjects(List<GameObject> gameObjects) {
    this.gameObjects = gameObjects;
  }

  /**
   * @return the gameFrame
   */
  public GameFrame getGameFrame() {
    return gameFrame;
  }

  /**
   * @param gameFrame the gameFrame to set
   */
  public void setGameFrame(GameFrame gameFrame) {
    this.gameFrame = gameFrame;
  }

  /**
   * @return the gamePanel
   */
  public GamePanel getGamePanel() {
    return gamePanel;
  }

  /**
   * @param gamePanel the gamePanel to set
   */
  public void setGamePanel(GamePanel gamePanel) {
    this.gamePanel = gamePanel;
  }

  private void initGameObjects() {
    // Init players
    Player player1 = new Player("Player 1", new ArrayList<>());
    Player player2 = new Player("Player 2", new ArrayList<>());

    // Init board
    Board board = new Board(8, 8, this);
    BoardBorder boardBorder = new BoardBorder(this, board);

    // Create ball
    Ball ball = new Ball(this, new Vector2(board.getSize().x / 2 - Ball.RADIUS, board.getSize().y / 2 - Ball.RADIUS));

    List<ChessPiece> chessPieces1 = ChessPiece.initDefaultPieces(this, player1, 1);
    List<ChessPiece> chessPieces2 = ChessPiece.initDefaultPieces(this, player2, 2);

    // Add board to game objects
    gameObjects.add(boardBorder);
    gameObjects.add(board);

    // Add chess pieces to game objects and assign to players
    chessPieces1.forEach(piece -> {
      gameObjects.add(piece);
      player1.getChessPieces().add(piece);
    });

    chessPieces2.forEach(piece -> {
      gameObjects.add(piece);
      player2.getChessPieces().add(piece);
    });

    // Add ball to game objects
    // We addGameObject at the end to ensure it has the highest priority and only
    // run it at the end for performance
    addGameObject(ball);

    // Add players to the game
    players.add(player1);
    players.add(player2);
  }

  private void initAssets() {
    // Load game assets (images, sounds, etc.)

    assets.setChessPieceSpriteSheet(Assets.loadImage("/sprites/chesspieces.png"));
  }

  private void run() {
    // Start game loop
    running = true;

    gameThread = new Thread(this::loop);
    gameThread.start();
  }

  private void loop() {
    double frameTime = 0;
    long lastTime = System.nanoTime();
    long timer = System.currentTimeMillis();
    int frames = 0;
    int updates = 0;

    long currentTime;
    double delta;

    while (running) {
      currentTime = System.nanoTime();
      delta = (currentTime - lastTime) / 1_000_000_000.0;
      lastTime = currentTime;
      frameTime += delta;

      // Update game at fixed rate
      while (frameTime >= UPDATE_CAP) {
        update(UPDATE_CAP); // Fixed time step
        frameTime -= UPDATE_CAP;
        updates++;
      }

      // Render as fast as possible (or capped)
      render();
      frames++;

      // FPS counter
      if (System.currentTimeMillis() - timer >= 1_000) {
        timer += 1_000;
        System.out.printf("[Engine/Loop] Delta: %g, UPS: %d, FPS: %d\n", delta, updates, frames);
        updates = 0;
        frames = 0;
      }

      // Sleep to prevent CPU hogging
      try {
        Thread.sleep(1);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    }
  }

  private void update(double deltaTime) {
    // Update game state (physics, AI, etc.)
    synchronized (lock) {
      // Thread-safe state updates
      gameObjects.forEach(obj -> {
        obj.update(deltaTime);
      });

      handleCollisions();
    }
  }

  private void handleCollisions() {
    List<GameObject> objects = new ArrayList<>(gameObjects);
    int size = objects.size();

    for (int i = 0; i < size; i++) {
      GameObject objA = objects.get(i);
      for (int j = i + 1; j < size; j++) {
        GameObject objB = objects.get(j);

        if (objA.isCollidingWith(objB) && objB.isCollidingWith(objA)) {
          objA.onCollision(objB);
          objB.onCollision(objA);
        }
      }
    }
  }

  private void render() {
    // Repaint the game panel
    SwingUtilities.invokeLater(() -> {
      gamePanel.repaint();
    });
  }
}
