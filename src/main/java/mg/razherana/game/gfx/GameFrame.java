package mg.razherana.game.gfx;

import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import mg.razherana.game.Game;
import mg.razherana.game.gfx.menubar.GameMenubar;

public class GameFrame extends JFrame {
  private final Game game;
  private GameMenubar gameMenuBar;

  public GameFrame(Game game) {
    super("FF48 - Andriamanitra Fitiavana Ianao");

    this.game = game;

    // Handle window closing event
    addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        game.gracefulExit();
      }
    });

    setResizable(true);
    setSize(1280, 1000);
    setLocationRelativeTo(null);

    getContentPane().setBackground(new Color(0x00000011));

    this.gameMenuBar = new GameMenubar(this);
  }

  public void init(GamePanel panel) {
    getContentPane().add(panel);
    setVisible(true);
  }

  /**
   * @return the game
   */
  public Game getGame() {
    return game;
  }

  /**
   * @return the gameMenuBar
   */
  public GameMenubar getGameMenuBar() {
    return gameMenuBar;
  }

  /**
   * @param gameMenuBar the gameMenuBar to set
   */
  public void setGameMenuBar(GameMenubar gameMenuBar) {
    this.gameMenuBar = gameMenuBar;
  }
}
