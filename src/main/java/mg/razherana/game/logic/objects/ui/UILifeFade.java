package mg.razherana.game.logic.objects.ui;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import mg.razherana.game.Game;
import mg.razherana.game.logic.GameObject;
import mg.razherana.game.logic.utils.Vector2;

public class UILifeFade extends GameObject {
  private final float lifetime;
  private final float percentage;

  static final float border = 2f;
  public static final float HEIGHT = 20f;

  float alpha = 1.0f;
  float timePassed = 0.0f;

  public UILifeFade(Game game, Vector2 position, Vector2 size, float lifetime, /* 0.0 to 1.0 */ float percentage) {
    super(game, position, 100);

    this.lifetime = lifetime;
    this.percentage = percentage;

    setSize(size);
  }

  @Override
  public void update(double deltaTime) {
    timePassed += deltaTime; // increase elapsed time
    alpha = 1.0f - (timePassed / lifetime); // fade linearly
    if (alpha < 0)
      alpha = 0; // clamp to 0

    if (timePassed >= lifetime) {
      // Remove this UI element from the game
      getGame().removeGameObject(this);
    }
  }

  @Override
  public void render(Graphics2D g2d) {
    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

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
