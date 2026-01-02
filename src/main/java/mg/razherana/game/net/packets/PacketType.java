package mg.razherana.game.net.packets;

public enum PacketType {
  INVALID(-1), LOGIN(00), ERROR(99), SNAPSHOT(02), MOVEMENTS_PLATFORM(03), GAME_STATE(04), RANDOM_MOVEMENT(05), POWER_UP(06);

  private int packetId;

  private PacketType(int packetId) {
    this.packetId = packetId;
  }

  public int getPacketId() {
    return packetId;
  }
}