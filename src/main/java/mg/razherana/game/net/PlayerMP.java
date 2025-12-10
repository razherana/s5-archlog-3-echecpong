package mg.razherana.game.net;

import java.awt.Color;
import java.net.InetAddress;
import java.util.List;

import mg.razherana.game.logic.objects.chesspiece.ChessPiece;
import mg.razherana.game.logic.players.Player;

public class PlayerMP extends Player {
  private InetAddress ipAddress;
  private int port;

  public PlayerMP(String name, Color primaryColor, Color secondaryColor, List<ChessPiece> chessPieces,
      InetAddress ipAddress, int port) {
    super(name, primaryColor, secondaryColor, chessPieces);
    this.ipAddress = ipAddress;
    this.port = port;
  }

  /**
   * @return the ipAddress
   */
  public InetAddress getIpAddress() {
    return ipAddress;
  }

  /**
   * @param ipAddress the ipAddress to set
   */
  public void setIpAddress(InetAddress ipAddress) {
    this.ipAddress = ipAddress;
  }

  /**
   * @return the port
   */
  public int getPort() {
    return port;
  }

  /**
   * @param port the port to set
   */
  public void setPort(int port) {
    this.port = port;
  }

}
