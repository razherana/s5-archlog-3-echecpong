package mg.razherana.game.gfx;

import java.awt.Color;

import javax.swing.JFrame;

import mg.razherana.game.Game;

public class GameFrame extends JFrame {
  private final Game game;

  public GameFrame(Game game) {
    super("FF48 - Andriamanitra Fitiavana Ianao");

    this.game = game;
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setResizable(true);
    setSize(1280, 720);
    setLocationRelativeTo(null);

    getContentPane().setBackground(new Color(0x00000011));
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
}
