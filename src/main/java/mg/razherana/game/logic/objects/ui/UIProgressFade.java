package mg.razherana.game.logic.objects.ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import mg.razherana.game.Game;
import mg.razherana.game.logic.utils.Vector2;

public class UIProgressFade extends AbstractUIFade {
  private final float percentage;

  private Color outerColor;
  private Color percentageColor;
  private Color innerColor;

  static final float border = 2f;
  public static final float HEIGHT = 5f;

  public UIProgressFade(Game game, Vector2 position, Vector2 size, float lifetime, /* 0.0 to 1.0 */ float percentage) {
    super(game, position, 100, size, lifetime);

    this.percentage = percentage;

    this.outerColor = java.awt.Color.BLACK;
    this.percentageColor = java.awt.Color.GREEN;
    this.innerColor = java.awt.Color.BLACK;
  }

  public UIProgressFade(Game game, Vector2 position, Vector2 size, float lifetime, /* 0.0 to 1.0 */ float percentage,
      Color color1, Color color2) {
    super(game, position, 100, size, lifetime);

    this.percentage = percentage;

    this.outerColor = color1;
    this.percentageColor = color2;
    this.innerColor = color1;
  }

  public UIProgressFade(Game game, Vector2 position, Vector2 size, float lifetime, /* 0.0 to 1.0 */ float percentage,
      Color outerColor, Color percentageColor, Color innerColor) {
    super(game, position, 100, size, lifetime);

    this.percentage = percentage;

    this.outerColor = outerColor;
    this.percentageColor = percentageColor;
    this.innerColor = innerColor;
  }

  @Override
  public void render(Graphics2D g2d) {
    setAlpha(g2d);

    // Set outer rectangle
    g2d.setColor(outerColor);
    Rectangle2D.Float outerRect = new Rectangle2D.Float(getPosition().x - border / 2, getPosition().y - border / 2,
        getSize().x + border, getSize().y + border);
    g2d.fill(outerRect);

    // Set inner background rectangle
    g2d.setColor(innerColor);
    Rectangle2D.Float backgroundRect = new Rectangle2D.Float(getPosition().x, getPosition().y,
        getSize().x, getSize().y);
    g2d.fill(backgroundRect);

    // Set inner rectangle
    g2d.setColor(percentageColor);
    Rectangle2D.Float innerRect = new Rectangle2D.Float(getPosition().x, getPosition().y,
        getSize().x * percentage, getSize().y);
    g2d.fill(innerRect);
  }
}
