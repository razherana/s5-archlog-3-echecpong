package mg.razherana.game.net.packets;

import java.util.ArrayList;

import mg.razherana.game.Game;

public class RandomMovementPacket extends Packet {

  float[][] randomMovements;

  public RandomMovementPacket(byte[] data) {
    super(PacketType.RANDOM_MOVEMENT.getPacketId());

    String message = new String(data);
    String[] parts = message.split(";");

    randomMovements = new float[parts.length - 1][2];

    for (int i = 1; i < parts.length; i++) {
      if (parts[i].isBlank())
        continue;

      String[] coords = parts[i].trim().split(",");
      randomMovements[i - 1][0] = Float.parseFloat(coords[0].trim());
      randomMovements[i - 1][1] = Float.parseFloat(coords[1].trim());
    }
  }

  public RandomMovementPacket(Game game) {
    super(PacketType.RANDOM_MOVEMENT.getPacketId());

    float[][] futureRandoms = new float[game.getFutureRandoms().size()][2];

    int i = 0;
    for (float[] fs : new ArrayList<>(game.getFutureRandoms())) {
      futureRandoms[i] = fs;
      i++;
    }

    this.randomMovements = futureRandoms;
  }

  @Override
  public byte[] getData() {
    StringBuilder data = new StringBuilder();
    data.append("05");

    for (float[] fs : randomMovements) {
      data.append(";").append(fs[0]).append(",").append(fs[1]);
    }

    return data.toString().getBytes();
  }

  public float[][] getRandomMovements() {
    return this.randomMovements;
  }

}
