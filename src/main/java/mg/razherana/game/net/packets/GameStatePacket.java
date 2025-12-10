package mg.razherana.game.net.packets;

import mg.razherana.game.Game;

public class GameStatePacket extends Packet {
  private String state;

  public GameStatePacket(Game game) {
    super(PacketType.GAME_STATE.getPacketId());
    this.state = game.getGameState().name();
  }

  public GameStatePacket(byte[] data) {
    super(PacketType.GAME_STATE.getPacketId());
    this.state = readData(data);
  }

  public String getState() {
    return state;
  }

  @Override
  public byte[] getData() {
    return ("04" + state).getBytes();
  }

}
