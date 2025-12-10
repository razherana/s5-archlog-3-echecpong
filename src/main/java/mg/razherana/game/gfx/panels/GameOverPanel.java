package mg.razherana.game.gfx.panels;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;

public class GameOverPanel extends JPanel {
  private String loserName;

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);

    // Enable anti-aliasing for smoother text
    Graphics2D g2d = (Graphics2D) g;

    {
      Graphics2D g_ = (Graphics2D) g2d.create();
      g_.setColor(new Color(3, 0, 53, 220));
      g_.fillRect(0, 0, getWidth(), getHeight());
      g_.dispose();
    }

    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_ON);
    g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

    // Draw "GAME OVER" centered
    g2d.setFont(new Font("Arial", Font.BOLD, 48));
    g2d.setColor(Color.RED);

    FontMetrics fm1 = g2d.getFontMetrics();
    String gameOverText = "GAME OVER";
    int gameOverWidth = fm1.stringWidth(gameOverText);
    int gameOverX = (getWidth() - gameOverWidth) / 2;
    int gameOverY = (getHeight() / 2) - 20; // Adjusted for two lines

    g2d.drawString(gameOverText, gameOverX, gameOverY);

    // Draw "Press SPACE to restart" centered
    g2d.setFont(new Font("Arial", Font.PLAIN, 16));
    g2d.setColor(Color.WHITE);

    FontMetrics fm2 = g2d.getFontMetrics();
    String restartText = loserName + " lost. Press SPACE to restart";
    int restartWidth = fm2.stringWidth(restartText);
    int restartX = (getWidth() - restartWidth) / 2;
    int restartY = gameOverY + 60; // Position below "GAME OVER"

    g2d.drawString(restartText, restartX, restartY);
  }

  public void setLoserName(String name) {
    this.loserName = name;
  }
}
