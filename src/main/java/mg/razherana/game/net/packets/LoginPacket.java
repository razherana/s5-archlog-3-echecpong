package mg.razherana.game.net.packets;

import java.awt.Color;

public class LoginPacket extends Packet {
  String username;
  private int whiteOrBlack;
  private Color color1;
  private Color color2;

  /**
   * Received from client
   * 
   * @param data
   */
  public LoginPacket(byte[] data) {
    super(00);
    String data_ = readData(data);
    String[] parts = data_.split(",");

    if (parts.length < 4) {
      throw new IllegalArgumentException("Invalid login packet data");
    }

    this.username = parts[0].trim();
    this.whiteOrBlack = Integer.parseInt(parts[1].trim());
    this.color1 = new Color(Integer.parseInt(parts[2].trim()));
    this.color2 = new Color(Integer.parseInt(parts[3].trim()));
  }

  /**
   * To send to the server
   * 
   * @param username
   * @param secondaryColor
   * @param primaryColor
   */
  public LoginPacket(String username, int whiteOrBlack, Color primaryColor, Color secondaryColor) {
    super(00);
    this.username = username;
    this.whiteOrBlack = whiteOrBlack;
    this.color1 = primaryColor;
    this.color2 = secondaryColor;
  }

  public String getUsername() {
    return username;
  }

  @Override
  public byte[] getData() {
    return ("00" + username + "," + whiteOrBlack + "," + color1.getRGB() + "," + color2.getRGB()).getBytes();
  }

  /**
   * @return the color1
   */
  public Color getColor1() {
    return color1;
  }

  /**
   * @return the color2
   */
  public Color getColor2() {
    return color2;
  }

  /**
   * @param username the username to set
   */
  public void setUsername(String username) {
    this.username = username;
  }

  /**
   * @param color1 the color1 to set
   */
  public void setColor1(Color color1) {
    this.color1 = color1;
  }

  /**
   * @param color2 the color2 to set
   */
  public void setColor2(Color color2) {
    this.color2 = color2;
  }

  /**
   * @return the whiteOrBlack
   */
  public int getWhiteOrBlack() {
    return whiteOrBlack;
  }

}
