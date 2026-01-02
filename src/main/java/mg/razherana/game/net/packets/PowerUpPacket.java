package mg.razherana.game.net.packets;

import java.util.ArrayList;
import java.util.List;

import mg.razherana.game.Game;
import mg.razherana.game.logic.objects.powerup.PowerUpObject;

public class PowerUpPacket extends Packet {

  public record PacketPowerUpDTO(String id, float x, float y, String simpleClassName, String additionalInfo) {
    @Override
    public final String toString() {
      return id + "," + x + "," + y + "," + simpleClassName + "," + additionalInfo;
    }

    static PacketPowerUpDTO fromString(String part) {
      if (part.isBlank())
        return null;

      part = part.trim();

      String[] parts = part.split(",", 5);
      if (parts.length < 5) {
        System.err.println("[MP/Packet/PowerUpPacket] : Invalid packet, less than 5 parts. Found : " + part);
        return null;
      }

      String id = parts[0].trim();
      float x = Float.parseFloat(parts[1].trim());
      float y = Float.parseFloat(parts[2].trim());
      String simpleClassName = parts[3].trim();
      String additionalInfo = parts[4].trim();

      return new PacketPowerUpDTO(id, x, y, simpleClassName, additionalInfo);
    }
  }

  private List<PacketPowerUpDTO> powerUps;

  public PowerUpPacket(Game game) {
    super(PacketType.POWER_UP.getPacketId());
    powerUps = game.getGameObjectsAsList().stream()
        .filter(
            (gameObj) -> gameObj instanceof PowerUpObject)
        .map((gameObj) -> (PowerUpObject<?>) gameObj)
        .map(
            (gameObj) -> new PacketPowerUpDTO(gameObj.getId(), gameObj.getPosition().x, gameObj.getPosition().y,
                gameObj.getClass().getSimpleName(), gameObj.getMultiplayerAdditionalInformation()))
        .toList();
  }

  public PowerUpPacket(byte[] data) {
    super(PacketType.POWER_UP.getPacketId());

    String message = readData(data);

    String[] parts = message.split(separator);

    powerUps = new ArrayList<>();

    for (String part : parts) {
      var powerUp = PacketPowerUpDTO.fromString(part);
      if (powerUp != null)
        powerUps.add(powerUp);
    }
  }

  final static String separator = ";";

  @Override
  public byte[] getData() {
    StringBuilder stringBuilder = new StringBuilder("06");

    // Loop powerups
    for (PacketPowerUpDTO powerUpDTO : powerUps) {
      stringBuilder.append(separator);
      stringBuilder.append(powerUpDTO.toString());
    }

    return stringBuilder.toString().getBytes();
  }

  /**
   * @return the powerUps
   */
  public List<PacketPowerUpDTO> getPowerUps() {
    return powerUps;
  }

  /**
   * @return the separator
   */
  public static String getSeparator() {
    return separator;
  }

}
