package mg.razherana.game.logic.objects.ui;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import mg.razherana.game.Game;
import mg.razherana.game.logic.utils.Vector2;

public class UILifeFade extends AbstractUIFade {
  private final float percentage;

  static final float border = 2f;
  public static final float HEIGHT = 5f;

  public UILifeFade(Game game, Vector2 position, Vector2 size, float lifetime, /* 0.0 to 1.0 */ float percentage) {
    super(game, position, 100, size, lifetime);

    this.percentage = percentage;
  }

  @Override
  public void render(Graphics2D g2d) {
    setAlpha(g2d);

    // Set outer rectangle
    g2d.setColor(java.awt.Color.BLACK);
    Rectangle2D.Float outerRect = new Rectangle2D.Float(getPosition().x - border / 2, getPosition().y - border / 2,
        getSize().x + border, getSize().y + border);
    g2d.fill(outerRect);

    // Set inner rectangle
    g2d.setColor(java.awt.Color.GREEN);
    Rectangle2D.Float innerRect = new Rectangle2D.Float(getPosition().x, getPosition().y,
        getSize().x * percentage, getSize().y);
    g2d.fill(innerRect);
  }
}
