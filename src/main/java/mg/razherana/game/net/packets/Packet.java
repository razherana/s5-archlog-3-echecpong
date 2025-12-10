package mg.razherana.game.net.packets;

import mg.razherana.game.net.Client;
import mg.razherana.game.net.Server;

public abstract class Packet {
  public byte packetId;

  public Packet(int packetId) {
    this.packetId = (byte) packetId;
  }

  /**
   * Server -> Client
   * 
   * @param client
   */
  public void writeData(Client client) {
    System.out.println("[MP/Packet] : Sending packet to server via client : " + new String(getData()));
    client.sendData(getData());
  }

  /**
   * Client -> Server
   * 
   * @param server
   */
  public void writeData(Server server) {
    System.out.println("[MP/Packet] : Sending packet to all clients via server : " + new String(getData()));
    server.sendDataToAllClients(getData());
  }

  /**
   * The data to play with
   */
  public abstract byte[] getData();

  public String readData(byte[] data) {
    String packet = new String(data).trim();
    return packet.substring(2);
  }

  public static PacketType lookupPacket(String id_s) {
    int id = -1;

    try {
      id = Integer.parseInt(id_s);
    } catch (NumberFormatException e) {
      return PacketType.INVALID;
    }

    for (PacketType packetType : PacketType.values())
      if (packetType.getPacketId() == id)
        return packetType;

    return PacketType.INVALID;
  }
}