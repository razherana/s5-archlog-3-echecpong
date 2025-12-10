package mg.razherana.game.logic.objects.platform;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import mg.razherana.game.Game;
import mg.razherana.game.logic.GameObject;
import mg.razherana.game.logic.listeners.KeyboardAdapter;
import mg.razherana.game.logic.players.Player;
import mg.razherana.game.logic.utils.Vector2;

public class Platform extends GameObject {
  private final Player player;

  private Vector2 velocity = new Vector2();

  private final float platformSpeed;

  private final int[] platformKeys;

  public Platform(Game game, Vector2 position, Player player, float width, float height, String platformKeysString,
      float platformSpeed) {
    super(game, position, 4);
    this.player = player;
    player.setPlatform(this);

    this.platformSpeed = platformSpeed;

    if (platformKeysString != null) {
      String[] keys = platformKeysString.split(",");
      platformKeys = new int[keys.length];
      for (int i = 0; i < keys.length; i++) {
        try {
          platformKeys[i] = Integer.parseInt(keys[i].trim());
        } catch (Exception e) {
          throw new RuntimeException("Failed to parse control for platform : " + keys[i].trim());
        }
      }
    } else {
      platformKeys = new int[] { -1, -1 };
    }

    setSize(new Vector2(width, height));
  }

  /**
   * @param velocity the velocity to set
   */
  public void setVelocity(Vector2 velocity) {
    this.velocity = velocity;
  }

  /**
   * @return the velocity
   */
  public Vector2 getVelocity() {
    return velocity;
  }

  @Override
  public void onKeyPressed(int keyCode, KeyboardAdapter keyboardAdapter) {
    if (keyboardAdapter.hasKey(platformKeys[0]))
      velocity.x = -platformSpeed;
    else if (keyboardAdapter.hasKey(platformKeys[1]))
      velocity.x = platformSpeed;
  }

  @Override
  public void onKeyReleased(int keyCode, KeyboardAdapter keyboardAdapter) {
    if (!keyboardAdapter.hasKey(platformKeys[0]) && !keyboardAdapter.hasKey(platformKeys[1]))
      velocity.x = 0;
  }

  @Override
  public void update(double deltaTime) {
    Vector2 newPosition = getPosition().add(velocity.multiply((float) deltaTime));

    setPosition(newPosition);
  }

  @Override
  public void render(Graphics2D g2d) {
    var rect = getDefaultBounds();

    g2d.setColor(player.getPrimaryColor());

    g2d.fill(rect);

    g2d.setColor(player.getSecondaryColor());
    g2d.setStroke(new BasicStroke(3));

    g2d.draw(rect);
  }

  /**
   * @return the player
   */
  public Player getPlayer() {
    return player;
  }

}
