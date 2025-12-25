package mg.razherana.game;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JColorChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import mg.razherana.game.gfx.GameFrame;
import mg.razherana.game.gfx.GamePanel;
import mg.razherana.game.gfx.panels.GameOverPanel;
import mg.razherana.game.logic.GameObject;
import mg.razherana.game.logic.listeners.KeyboardAdapter;
import mg.razherana.game.logic.objects.ball.Ball;
import mg.razherana.game.logic.objects.board.Board;
import mg.razherana.game.logic.objects.board.BoardBorder;
import mg.razherana.game.logic.objects.chesspiece.ChessPiece;
import mg.razherana.game.logic.objects.chesspiece.ChessPieceType;
import mg.razherana.game.logic.objects.platform.Platform;
import mg.razherana.game.logic.players.Player;
import mg.razherana.game.logic.utils.Assets;
import mg.razherana.game.logic.utils.Config;
import mg.razherana.game.logic.utils.RandomGen;
import mg.razherana.game.logic.utils.Vector2;
import mg.razherana.game.net.Client;
import mg.razherana.game.net.PlayerMP;
import mg.razherana.game.net.Server;
import mg.razherana.game.net.packets.GameStatePacket;
import mg.razherana.game.net.packets.LoginPacket;
import mg.razherana.game.net.packets.MovementsPacket;
import mg.razherana.game.net.packets.SnapshotPacket;
import mg.razherana.game.net.packets.MovementsPacket.PlatformDTO;

/**
 * Contains the game instance and logic.
 */
public class Game {
  private final static int MAX_FUTURE_RANDOMS = 30;

  private Thread gameThread;

  private Deque<float[]> futureRandoms;

  private final KeyboardAdapter keyboardAdapter = new KeyboardAdapter(this);

  private volatile GameState gameState = GameState.PAUSED;

  // Game objects rendered in a priority queue based on their priority
  private List<GameObject> gameObjects = new ArrayList<>();

  // Game objects to be added/removed
  private List<GameObject> gameObjectsToAdd = new ArrayList<>();

  private List<GameObject> gameObjectsToRemove = new ArrayList<>();
  private volatile boolean running = false;

  private final Config config;

  private GameFrame gameFrame;

  private final int pieceNumber;

  // Panels
  private GamePanel gamePanel;
  private GameOverPanel gameOverPanel;

  private final Assets assets = new Assets();

  private List<Player> players = new ArrayList<>();

  // Timing variables
  private final double TARGET_FPS = 144.0;

  private final double UPDATE_CAP = 1.0 / TARGET_FPS; // 60 updates/sec

  private final Object gameObjectsLock = new Object();

  // Multiplayer parts

  private Client client;

  private Server server;

  private boolean multiplayer = false;

  private final float[] randomX;

  private final float[] randomY;

  public Game() {
    // Init config
    try {
      config = new Config();
    } catch (IOException e) {
      throw new RuntimeException("Failed to load config file", e);
    }

    {
      String[] parts = config.getProperty(Config.Key.MVT_SPEED_RANDOM_X).split(",");

      randomX = new float[] {
          Float.parseFloat(parts[0]),
          Float.parseFloat(parts[1]),
      };
    }

    {
      String[] parts = config.getProperty(Config.Key.MVT_SPEED_RANDOM_Y).split(",");

      randomY = new float[] {
          Float.parseFloat(parts[0]),
          Float.parseFloat(parts[1]),
      };
    }

    pieceNumber = Integer.parseInt(config.getProperty(Config.Key.PIECE_NUMBER));

    // Initialize game components here
    gameFrame = new GameFrame(this, Map.of(
        "GAME_OVER", gameOverPanel = new GameOverPanel()));
    gamePanel = new GamePanel(this);

    gameFrame.init(gamePanel);

    initAssets();

    initGameObjects();

    initListeners();

    run();
  }

  /**
   * @return the gameObjectsLock
   */
  public Object getGameObjectsLock() {
    return gameObjectsLock;
  }

  /**
   * @return the keyboardAdapter
   */
  public KeyboardAdapter getKeyboardAdapter() {
    return keyboardAdapter;
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
    synchronized (gameObjectsLock) {
      gameObjectsToAdd.add(obj);
    }
  }

  public void removeGameObject(GameObject obj) {
    synchronized (gameObjectsLock) {
      gameObjectsToRemove.add(obj);
    }
  }

  public void sortGameObjectsByPriority() {
    synchronized (gameObjectsLock) {
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
    synchronized (gameObjectsLock) {
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

  /**
   * @return the objectsToAdd
   */
  public List<GameObject> getGameObjectsToAdd() {
    return gameObjectsToAdd;
  }

  /**
   * @param objectsToAdd the objectsToAdd to set
   */
  public void setGameObjectsToAdd(List<GameObject> objectsToAdd) {
    this.gameObjectsToAdd = objectsToAdd;
  }

  /**
   * @return the objectsToRemove
   */
  public List<GameObject> getGameObjectsToRemove() {
    return gameObjectsToRemove;
  }

  /**
   * @param objectsToRemove the objectsToRemove to set
   */
  public void setGameObjectsToRemove(List<GameObject> objectsToRemove) {
    this.gameObjectsToRemove = objectsToRemove;
  }

  /**
   * @return the running
   */
  public boolean isRunning() {
    return running;
  }

  /**
   * @param running the running to set
   */
  public void setRunning(boolean running) {
    this.running = running;
  }

  /**
   * @param players the players to set
   */
  public void setPlayers(List<Player> players) {
    this.players = players;
  }

  public Player initPlayerAndObjects(Player player, int whiteOrBlack, boolean initCommands) {
    List<ChessPiece> chessPieces = ChessPiece.initDefaultPieces(this, player, whiteOrBlack, pieceNumber);

    chessPieces.forEach(piece -> {
      gameObjects.add(piece);
      player.getChessPieces().add(piece);
    });

    String commands = null;

    if (initCommands) {
      commands = whiteOrBlack == 1 ? config.getProperty(Config.Key.PLATFORM_COMMAND_PLAYER1)
          : config.getProperty(Config.Key.PLATFORM_COMMAND_PLAYER2);
    }

    float w = Float.parseFloat(config.getProperty(Config.Key.PLATFORM_WIDTH));
    float h = Float.parseFloat(config.getProperty(Config.Key.PLATFORM_HEIGHT));
    float speed = Float.parseFloat(config.getProperty(Config.Key.PLATFORM_SPEED));

    Vector2 position = whiteOrBlack == 1
        ? Vector2.from(config.getProperty(Config.Key.PLATFORM_POSITION_WHITE))
        : Vector2.from(config.getProperty(Config.Key.PLATFORM_POSITION_BLACK));

    Platform platform = new Platform(this,
        position,
        player,
        w,
        h,
        commands,
        speed);

    gameObjects.add(platform);

    return player;
  }

  public void initGameObjects() {
    // Init players
    Player player1 = new Player("Player 1", new Color(0x596070), new Color(0x96a2b3), new ArrayList<>());
    Player player2 = new Player("Player 2", new Color(0x96a2b3), new Color(0x596070), new ArrayList<>());

    initPlayerAndObjects(player1, 1, true);
    initPlayerAndObjects(player2, 2, true);

    // Add players to the game
    players.add(player1);
    players.add(player2);

    // Add base objects
    initBase();
  }

  /**
   * @return the gameState
   */
  public GameState getGameState() {
    return gameState;
  }

  /**
   * @param gameState the gameState to set
   */
  public void setGameState(GameState gameState) {
    this.gameState = gameState;
  }

  /**
   * @return the client
   */
  public Client getClient() {
    return client;
  }

  /**
   * @param client the client to set
   */
  public void setClient(Client client) {
    this.client = client;
  }

  /**
   * @return the server
   */
  public Server getServer() {
    return server;
  }

  /**
   * @param server the server to set
   */
  public void setServer(Server server) {
    this.server = server;
  }

  /**
   * @return the multiplayer
   */
  public boolean isMultiplayer() {
    return multiplayer;
  }

  /**
   * @param multiplayer the multiplayer to set
   */
  public void setMultiplayer(boolean multiplayer) {
    this.multiplayer = multiplayer;
  }

  public void setupServer() {
    // Check if server is already running
    if (server != null && server.isRunning()) {
      JOptionPane.showMessageDialog(gamePanel, "Server is already running!", "Error", JOptionPane.ERROR_MESSAGE);
      return;
    }

    // Clean up objects and reset game state
    forceClearEverything();

    // Create server
    server = new Server(this);
    server.start();

    JOptionPane.showMessageDialog(gamePanel, "Server running on port " + config.getProperty(Config.Key.SERVER_PORT),
        "Server Started",
        JOptionPane.INFORMATION_MESSAGE);
  }

  public void setupClient() {
    // Check if client is already connected
    if (client != null && client.isConnected()) {
      JOptionPane.showMessageDialog(gamePanel, "Client is already connected!", "Error", JOptionPane.ERROR_MESSAGE);
      return;
    }

    String username = JOptionPane.showInputDialog(gamePanel, "Enter your username :", "Player");

    Color primaryColor = null;
    Color secondaryColor = null;

    {
      final JColorChooser col = new JColorChooser(new Color(0x96a2b3));
      int res = JOptionPane.showConfirmDialog(gamePanel, col, "Choose your primary color",
          JOptionPane.OK_CANCEL_OPTION);
      if (res == JOptionPane.OK_OPTION) {
        primaryColor = col.getColor();
        System.out.printf("Chosen color: #%06X\n", (primaryColor.getRGB() & 0xFFFFFF));
      } else {
        System.out.println("No color chosen, using default.");
      }
    }

    {
      final JColorChooser col = new JColorChooser(new Color(0x596070));
      int res = JOptionPane.showConfirmDialog(gamePanel, col, "Choose your secondary color",
          JOptionPane.OK_CANCEL_OPTION);
      if (res == JOptionPane.OK_OPTION) {
        secondaryColor = col.getColor();
        System.out.printf("Chosen color: #%06X\n", (secondaryColor.getRGB() & 0xFFFFFF));
      } else {
        System.out.println("No color chosen, using default.");
      }
    }

    String serverAddress = JOptionPane.showInputDialog(gamePanel, "Enter server address and port :",
        "localhost:" + config.getProperty(Config.Key.SERVER_PORT));

    if (serverAddress == null || serverAddress.isEmpty()) {
      JOptionPane.showMessageDialog(gamePanel, "Invalid server address!", "Error", JOptionPane.ERROR_MESSAGE);
      return;
    }

    String[] parts = serverAddress.split(":");
    if (parts.length != 2) {
      JOptionPane.showMessageDialog(gamePanel, "Invalid server address format! Use IP:PORT", "Error",
          JOptionPane.ERROR_MESSAGE);
      return;
    }

    serverAddress = parts[0];
    int port;
    try {
      port = Integer.parseInt(parts[1]);
    } catch (NumberFormatException e) {
      JOptionPane.showMessageDialog(gamePanel, "Invalid port number!", "Error", JOptionPane.ERROR_MESSAGE);
      return;
    }

    if (port < 1 || port > 65535) {
      JOptionPane.showMessageDialog(gamePanel, "Port number must be between 1 and 65535!", "Error",
          JOptionPane.ERROR_MESSAGE);
      return;
    }

    // Create client
    try {
      client = new Client(this, serverAddress, port, username, primaryColor, secondaryColor);
      client.start();
    } catch (Exception e) {
      e.printStackTrace();

      JOptionPane.showMessageDialog(gamePanel, "Failed to connect to server: " + e.getMessage(), "Error",
          JOptionPane.ERROR_MESSAGE);
      return;
    }

    JOptionPane.showMessageDialog(gamePanel, "Connected to server at " + serverAddress, "Client Connected",
        JOptionPane.INFORMATION_MESSAGE);

    PlayerMP playerMP = new PlayerMP(username, primaryColor, secondaryColor, new ArrayList<>(), null, -1);

    // Request initial game state from server
    LoginPacket loginPacket = new LoginPacket(username, -1, primaryColor, secondaryColor);

    if (isServerRunning()) {
      initPlayerAndObjects(playerMP, (server.getConnectedPlayers().size() + 1) % 2, true);

      setMultiplayer(true);

      server.parsePacket(
          new LoginPacket(username, (server.getConnectedPlayers().size() + 1) % 2, primaryColor, secondaryColor)
              .getData(),
          null, -1);

      // Add to server
      server.addConnection(playerMP, new LoginPacket(username, 1, primaryColor, secondaryColor));

      synchronized (gameObjectsLock) {
        getPlayers().add(playerMP);
      }

      return;
    }

    forceClearEverything();

    initPlayerAndObjects(playerMP, 1, true);

    synchronized (gameObjectsLock) {
      getPlayers().add(playerMP);
    }

    loginPacket.writeData(client);

    setMultiplayer(true);
  }

  public void clientDisconnect() {
    // Check if client is connected
    if (client == null || !client.isConnected()) {
      JOptionPane.showMessageDialog(gamePanel, "Client is not connected!", "Error", JOptionPane.ERROR_MESSAGE);
      return;
    }

    // Disconnect client
    client.disconnect();
    client = null;

    if (!isServerRunning()) {
      clearEverything();
      initGameObjects();
      gameState = GameState.PAUSED;
    }
  }

  public void stopServer() {
    // Check if server is running
    if (!isServerRunning()) {
      JOptionPane.showMessageDialog(gamePanel, "Server is not running!", "Error", JOptionPane.ERROR_MESSAGE);
      return;
    }

    // Stop server
    server.stopServer();
    server = null;

    forceClearEverything();
    initGameObjects();
    gameState = GameState.PAUSED;

    JOptionPane.showMessageDialog(gamePanel, "Server stopped.", "Server Stopped", JOptionPane.INFORMATION_MESSAGE);
  }

  public void gracefulExit() {
    // Perform any cleanup operations here
    setRunning(false);

    // Disconnect client if connected
    if (client != null && client.isConnected()) {
      client.disconnect();
    }

    // Stop server if running
    if (server != null && server.isRunning()) {
      server.stopServer();
    }

    // Exit application
    System.exit(0);
  }

  public void clearEverything() {
    // Clean up objects and reset game state if not running server
    if (!isServerRunning())
      forceClearEverything();

    gameState = GameState.PAUSED;
  }

  public boolean isServerRunning() {
    return server != null && server.isRunning();
  }

  public void updateMovements(MovementsPacket packet, String username) {
    synchronized (gameObjectsLock) {
      PlatformDTO[] platforms = packet.getPlatforms();
      int platformCount = platforms.length;
      int gameObjectCount = gameObjects.size();

      for (int i = 0; i < platformCount; i++) {
        PlatformDTO platformDTO = platforms[i];
        if (platformDTO == null)
          continue;

        if (platformDTO.playerName().equals(username)) {
          continue;
        }

        for (int j = 0; j < gameObjectCount; j++) {
          GameObject obj = gameObjects.get(j);
          if (obj instanceof Platform) {
            Platform platform = (Platform) obj;
            if (platform.getPlayer().getName().equals(platformDTO.playerName())) {
              // Reuse vector objects instead of creating new ones
              platform.getPosition().set(platformDTO.x(), platformDTO.y());
              platform.getVelocity().set(platformDTO.vx(), platformDTO.vy());
              break;
            }
          }
        }
      }

      // Update ball position
      if (packet.getBall() != null) {
        for (GameObject obj : gameObjects) {
          if (obj instanceof Ball) {
            Ball ball = (Ball) obj;
            ball.setPosition(new Vector2(packet.getBall().x(), packet.getBall().y()));
            ball.setVelocity(new Vector2(packet.getBall().vx(), packet.getBall().vy()));
          }
        }
      }
    }
  }

  public void updateGameStateFromSnapshot(SnapshotPacket packet) {
    synchronized (gameObjectsLock) {
      // Update game objects based on the snapshot data

      // Update chess pieces
      // Clear existing chess pieces
      gameObjects.removeIf(obj -> obj instanceof ChessPiece);

      HashMap<String, Player> playerMap = new HashMap<>();
      for (Player player : players) {
        playerMap.put(player.getName(), player);
      }

      // Recreate chess pieces from snapshot
      for (var chessPieceDTO : packet.getChessPieces()) {
        if (chessPieceDTO == null)
          continue;

        Player owner = playerMap.get(chessPieceDTO.playerName());

        if (owner != null) {
          ChessPiece piece = new ChessPiece(this,
              new Vector2(chessPieceDTO.x(), chessPieceDTO.y()),
              owner,
              ChessPieceType.valueOf(chessPieceDTO.type()));

          gameObjects.add(piece);
          owner.getChessPieces().add(piece);
        }
      }

      // Update colors of players
      for (var playerDTO : packet.getPlayers()) {
        if (playerDTO == null)
          continue;

        Player player = playerMap.get(playerDTO.name());
        if (player != null) {
          player.setPrimaryColor(new Color(playerDTO.color1()));
          player.setSecondaryColor(new Color(playerDTO.color2()));
        }
      }

      sortGameObjectsByPriority();
    }
  }

  public void registerOtherPlayer(String username, int whiteOrBlack, Color color1, Color color2) {
    Player otherPlayer = new Player(username, color1, color2, new ArrayList<>());

    initPlayerAndObjects(otherPlayer, whiteOrBlack, false);

    synchronized (gameObjectsLock) {
      // Remove existing player with the same username
      getPlayers().stream().filter(e -> e.getName().equals(username)).findAny().ifPresent((p) -> {
        p.getChessPieces().forEach(piece -> getGameObjects().remove(piece));
        p.getPlatform().freeListeners();
        getGameObjects().remove(p.getPlatform());
        getPlayers().remove(p);

        sortGameObjectsByPriority();
      });

      getPlayers().add(otherPlayer);
    }
  }

  public void clearPlayerObjects(Player player) {
    synchronized (gameObjectsLock) {
      // Remove existing player with the same username
      getPlayers().stream().filter(e -> e.getName().equals(player.getName())).findAny().ifPresent((p) -> {
        p.getChessPieces().forEach(piece -> getGameObjects().remove(piece));
        p.getPlatform().freeListeners();
        getGameObjects().remove(p.getPlatform());

        sortGameObjectsByPriority();
      });
    }
  }

  public Ball getGameBall() {
    synchronized (gameObjectsLock) {
      for (GameObject obj : gameObjects) {
        if (obj instanceof Ball) {
          return (Ball) obj;
        }
      }
      return null;
    }
  }

  public void updateMovements(MovementsPacket platformPacket) {
    updateMovements(platformPacket, "");
  }

  public float[] getNextRandom() {
    synchronized (gameObjectsLock) {
      if (futureRandoms == null || futureRandoms.size() <= 0)
        generateFutureRandoms();

      return futureRandoms.poll();
    }
  }

  public float[] peekNextRandom() {
    synchronized (gameObjectsLock) {
      if (futureRandoms == null || futureRandoms.size() <= 0)
        generateFutureRandoms();

      return futureRandoms.peek();
    }
  }

  public Deque<float[]> getFutureRandoms() {
    if (futureRandoms == null || futureRandoms.size() <= 0)
      generateFutureRandoms();

    return futureRandoms;
  }

  public void setFutureRandoms(float[][] futureRandoms) {
    var list = Arrays.asList(futureRandoms);
    this.futureRandoms = new ArrayDeque<>(list);
  }

  public void generateFutureRandoms() {
    synchronized (gameObjectsLock) {
      futureRandoms = new ArrayDeque<>();

      for (int i = 0; i < MAX_FUTURE_RANDOMS; i++) {
        float randX = RandomGen.generateRandomFloat(randomX[0], randomX[1]);
        float randY = RandomGen.generateRandomFloat(randomY[0], randomY[1]);

        float[] randomPair = new float[] { randX, randY };

        futureRandoms.addLast(randomPair);
      }

      if (isMultiplayer() && isServerRunning()) {
        server.sendRandomMovementToAllClients();
      }
    }
  }

  public void onKeyPress(int keyCode, JPanel jPanel) {
    if (keyCode == KeyEvent.VK_SPACE) {
      // Pause if it works
      System.out.println(jPanel.getClass());
      if (jPanel == gamePanel)
        togglePause();

      // Game over restart button
      if (jPanel == gameOverPanel)
        restartAfterGameOver();
    }

  }

  private void initListeners() {
    // Add keyboard listener
    gameFrame.getMainPanel().addKeyListener(keyboardAdapter);
  }

  private void togglePause() {
    if (gameState == GameState.PAUSED)
      this.gameState = GameState.RUNNING;
    else if (gameState == GameState.RUNNING)
      this.gameState = GameState.PAUSED;

    notifyGameStateChange();
  }

  private void notifyGameStateChange() {
    // If server, notify clients
    GameStatePacket packet = new GameStatePacket(this);

    if (isServerRunning()) {
      server.sendDataToAllClients(packet.getData());
    }

    // If client, notify server
    else if (client != null && client.isConnected()) {
      client.sendData(packet.getData());
    }
  }

  private void initBase() {
    // Init board
    Board board = new Board(8, 8, this);
    BoardBorder boardBorder = new BoardBorder(this, board);

    // Create ball
    Ball ball = new Ball(this,
        new Vector2(board.getSize().x / 2 - Ball.RADIUS, board.getSize().y / 2 - Ball.RADIUS),
        Float.parseFloat(config.getProperty(Config.Key.BALL_DAMAGE)),
        Float.parseFloat(config.getProperty(Config.Key.BALL_SPEED_X)),
        Float.parseFloat(config.getProperty(Config.Key.BALL_SPEED_Y)));

    // Add board to game objects
    gameObjects.add(boardBorder);
    gameObjects.add(board);
    gameObjects.add(ball);

    sortGameObjectsByPriority();
  }

  private void initAssets() {
    // Load game assets (images, sounds, etc.)

    assets.setChessPieceSpriteSheet(Assets.loadImage("/sprites/chesspieces.png"));
    assets.setBallSpriteSheet(Assets.loadImage("/sprites/ball.png"));
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

    @SuppressWarnings("unused")
    int frames = 0;

    @SuppressWarnings("unused")
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
        // System.out.printf("[Engine/Loop] Delta: %g, UPS: %d, FPS: %d\n", delta,
        // updates, frames);
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
    synchronized (gameObjectsLock) {
      if (gameState == GameState.RUNNING) {
        // Thread-safe state updates
        gameObjects.forEach(obj -> {
          if (!gameObjectsToRemove.contains(obj)) {
            obj.update(deltaTime);

            // Run timers of the gameObject
            obj.runTimers(deltaTime);
          }
        });

        handleCollisions();

        boolean sort = false;

        // Add new game objects
        if (!gameObjectsToAdd.isEmpty()) {
          gameObjects.addAll(gameObjectsToAdd);
          gameObjectsToAdd.clear();
          sort = true;
        }

        // Remove marked game objects
        if (!gameObjectsToRemove.isEmpty()) {
          gameObjectsToRemove.forEach(obj -> obj.freeListeners());
          gameObjects.removeAll(gameObjectsToRemove);
          gameObjectsToRemove.clear();
          sort = true;
        }

        if (sort)
          sortGameObjectsByPriority();

        // Check for game over condition
        checkGameOver();

        // System.gc();
      }
    }
  }

  private void checkGameOver() {
    for (Player player : players) {
      if (player.getChessPieces().stream()
          .filter(piece -> piece.getType() == ChessPieceType.KING_BLACK || piece.getType() == ChessPieceType.KING_WHITE)
          .count() == 0) {

        gameState = GameState.GAME_OVER;

        // Update font
        gameOverPanel.setLoserName(player.getName());
        getGameFrame().changePanel("GAME_OVER");

        notifyGameStateChange();
      }
    }
  }

  private void handleCollisions() {
    List<GameObject> objects = new ArrayList<>(gameObjects);
    int size = objects.size();

    for (int i = 0; i < size; i++) {
      GameObject objA = objects.get(i);

      // Skip if object is marked for removal
      if (gameObjectsToRemove.contains(objA) || !objA.isCollision())
        continue;

      for (int j = i + 1; j < size; j++) {
        GameObject objB = objects.get(j);

        if (i == j || objA == objB || !objB.isCollision())
          continue;

        // Skip if object is marked for removal
        if (gameObjectsToRemove.contains(objB))
          continue;

        if (objA.isCollidingWith(objB) && objB.isCollidingWith(objA) && objA != objB) {
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

  private void forceClearEverything() {
    // Clean up objects and reset game state
    synchronized (gameObjectsLock) {
      gameObjects.clear();
      gameObjectsToAdd.clear();
      gameObjectsToRemove.clear();
      players.clear();

      initBase();

      gameState = GameState.PAUSED;
    }
  }

  private void restartAfterGameOver() {
    if (gameState == GameState.GAME_OVER) {
      // We restart
      forceClearEverything();
      gameState = GameState.GAME_OVER;

      // Check if server
      if (isServerRunning()) {
        // We reset the player's pieces and platforms
        var players = server.getConnectedPlayers();

        synchronized (gameObjectsLock) {
          for (PlayerMP playerMP : players) {
            initPlayerAndObjects(
                playerMP,
                playerMP.getWhiteOrBlack(),
                false);

            getPlayers().add(playerMP);
          }
        }

        gameState = GameState.PAUSED;

        gameFrame.changePanel("GAME");

        notifyGameStateChange();
      } else if (!isMultiplayer()) {
        // We assume a singleplayer game
        synchronized (gameObjectsLock) {
          forceClearEverything();
          gameObjects.clear();

          initGameObjects();
        }

        gameState = GameState.PAUSED;

        gameFrame.changePanel("GAME");
      }
    }
  }
}
