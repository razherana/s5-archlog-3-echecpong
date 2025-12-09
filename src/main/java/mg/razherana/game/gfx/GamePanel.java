package mg.razherana.game.gfx;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JPanel;

import mg.razherana.game.Game;
import mg.razherana.game.logic.GameObject;
import mg.razherana.game.logic.utils.Vector2;

public class GamePanel extends JPanel {
  private final Game game;
  private Vector2 origin = new Vector2(300, 150);

  public GamePanel(Game game) {
    // Sets the panel to be double buffered
    super(true);

    this.game = game;

    setBackground(new Color(0x00000011));

    setFocusable(true);
    requestFocusInWindow();

    addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
          // Toggle pause state
          game.togglePause();
        }
      }
    });

    setVisible(true);
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);

    for (int i = 0; i < game.getGameObjects().size(); i++) {
      GameObject obj = game.getGameObjects().get(i);

      Graphics2D g2d = (Graphics2D) g.create();
      g2d.translate(origin.x, origin.y);
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
