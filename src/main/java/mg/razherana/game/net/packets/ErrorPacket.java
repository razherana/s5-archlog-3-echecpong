package mg.razherana.game.net.packets;

public class ErrorPacket extends Packet {

  private String message;

  public ErrorPacket(byte[] data) {
    super(99);

    message = readData(data);

    if (message.isBlank())
      message = "Empty";
  }

  public ErrorPacket(String message) {
    super(99);
    this.message = message;
  }

  @Override
  public byte[] getData() {
    return (99 + message).getBytes();
  }

  /**
   * @return the message
   */
  public String getMessage() {
    return message;
  }

  /**
   * @param message the message to set
   */
  public void setMessage(String message) {
    this.message = message;
  }
}
