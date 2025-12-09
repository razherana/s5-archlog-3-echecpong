package mg.razherana.game.logic.objects.platform;

import java.awt.BasicStroke;
import java.awt.Graphics2D;

import mg.razherana.game.Game;
import mg.razherana.game.logic.GameObject;
import mg.razherana.game.logic.players.Player;
import mg.razherana.game.logic.utils.Vector2;

public class Platform extends GameObject {
  private final Player player;

  public Platform(Game game, Vector2 position, Player player, float width, float height) {
    super(game, position, 4);
    this.player = player;

    setSize(new Vector2(width, height));
  }

  @Override
  public void update(double deltaTime) {
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
