package mg.razherana.game.net.packets;

import mg.razherana.game.Game;
import mg.razherana.game.logic.objects.platform.Platform;

public class MovementsPacket extends Packet {
  public record BallDTO(float x, float y, float vx, float vy) {
  }
  public record PlatformDTO(String playerName, float x, float y, float vx, float vy) {
  }

  // Example platform + ball :
  // razherana,200,-142,400,200;faniry,120,-80,122,233|91,21,93,23
  PlatformDTO[] platforms;

  BallDTO ball;

  public MovementsPacket(byte[] data) {
    super(PacketType.MOVEMENTS_PLATFORM.getPacketId());

    // Parse platforms and ball from data
    String data_ = readData(data);

    String[] parts_ = data_.split("\\|", 2);

    String platformData = parts_[0];
    String[] platformStrings = platformData.split(";");
    platforms = new PlatformDTO[platformStrings.length];

    for (int i = 0; i < platformStrings.length; i++) {
      if (platformStrings[i].isBlank())
        continue;

      String[] parts = platformStrings[i].trim().split(",", 5);

      String playerName = parts[0].trim();
      float x = Float.parseFloat(parts[1].trim());
      float y = Float.parseFloat(parts[2].trim());
      float vx = Float.parseFloat(parts[3].trim());
      float vy = Float.parseFloat(parts[4].trim());

      platforms[i] = new PlatformDTO(playerName, x, y, vx, vy);
    }

    String ballData = parts_[1];
    if (!ballData.isBlank()) {
      String[] ballParts = ballData.trim().split(",", 4);
      float bx = Float.parseFloat(ballParts[0].trim());
      float by = Float.parseFloat(ballParts[1].trim());
      float bvx = Float.parseFloat(ballParts[2].trim());
      float bvy = Float.parseFloat(ballParts[3].trim());
      ball = new BallDTO(bx, by, bvx, bvy);
    }
  }

  public MovementsPacket(Game game) {
    super(PacketType.MOVEMENTS_PLATFORM.getPacketId());

    // Gather platform positions

    synchronized (game.getGameObjectsLock()) {
      platforms = game.getGameObjects().stream()
          .filter(obj -> obj instanceof Platform)
          .map(obj -> (Platform) obj)
          .map(obj -> new PlatformDTO(obj.getPlayer().getName(), obj.getPosition().x, obj.getPosition().y,
              obj.getVelocity().x,
              obj.getVelocity().y))
          .toArray(PlatformDTO[]::new);

      // Gather ball position
      var gameBall = game.getGameBall();
      ball = new BallDTO(gameBall.getPosition().x, gameBall.getPosition().y, gameBall.getVelocity().x,
          gameBall.getVelocity().y);
    }
  }

  /**
   * @param ball the ball to set
   */
  public void setBall(BallDTO ball) {
    this.ball = ball;
  }

  @Override
  public byte[] getData() {
    StringBuilder data = new StringBuilder();
    data.append("03");

    for (int i = 0; i < platforms.length; i++) {
      PlatformDTO platform = platforms[i];
      data.append(platform.playerName()).append(",").append(platform.x()).append(",").append(platform.y()).append(",")
          .append(platform.vx()).append(",").append(platform.vy());
      if (i < platforms.length - 1)
        data.append(";");
    }

    data.append("|");

    data.append(ball.x()).append(",").append(ball.y()).append(",").append(ball.vx()).append(",").append(ball.vy());

    return data.toString().getBytes();
  }

  /**
   * @return the platforms
   */
  public PlatformDTO[] getPlatforms() {
    return platforms;
  }

  /**
   * @return the ball
   */
  public BallDTO getBall() {
    return ball;
  }

}
