package mg.razherana.game.gfx;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import mg.razherana.game.Game;
import mg.razherana.game.logic.GameObject;

public class GamePanel extends JPanel {
  private final Game game;

  public GamePanel(Game game) {
    // Sets the panel to be double buffered
    super(true);

    this.game = game;

    setBackground(new Color(0x00000011));

    setFocusable(true);
    requestFocusInWindow();

    setVisible(true);
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);

    class InnerGamePanel {
      int old = -999;
    }

    final InnerGamePanel inner = new InnerGamePanel();

    for (GameObject obj : game.getGameObjects()) {
      if (inner.old > obj.getPriority()) {
        System.err.println("Rendering order error: object with lower priority rendered after higher priority");
      }

      inner.old = obj.getPriority();

      Graphics2D g2d = (Graphics2D) g.create();
      obj.render(g2d);

      // Dispose the graphics context to free up resources
      if (g2d != null)
        g2d.dispose();
    }
  }

  /**
   * @return the game
   */
  public Game getGame() {
    return game;
  }

}
