package mg.razherana.game.net;

import java.awt.Color;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.swing.JOptionPane;

import mg.razherana.game.Game;
import mg.razherana.game.GameState;
import mg.razherana.game.logic.utils.Config;
import mg.razherana.game.net.packets.ErrorPacket;
import mg.razherana.game.net.packets.GameStatePacket;
import mg.razherana.game.net.packets.LoginPacket;
import mg.razherana.game.net.packets.Packet;
import mg.razherana.game.net.packets.PacketType;
import mg.razherana.game.net.packets.MovementsPacket;
import mg.razherana.game.net.packets.SnapshotPacket;

public class Client extends Thread {
  private InetAddress ipAddress;
  private DatagramSocket socket;
  private final Game game;

  private PlatformSnapshotSender platformSnapshotSender;

  class PlatformSnapshotSender extends Thread {
    @Override
    public void run() {
      while (isConnected() && isRunning()) {
        try {
          Thread.sleep(1000 / Integer.parseInt(game.getConfig().getProperty(Config.Key.SERVER_PLATFORM_SNAPSHOT_RATE)));
        } catch (InterruptedException e) {
          e.printStackTrace();
        }

        if (isConnected() && isRunning()) {
          MovementsPacket packet = new MovementsPacket(game);
          sendData(packet.getData());
        }
      }
    }
  }

  private String username;
  private Color primaryColor;
  private Color secondaryColor;

  private volatile boolean running = false;
  private final int port;

  public Client(Game game, String ipAddress, int port, String username, Color primaryColor, Color secondaryColor)
      throws UnknownHostException, SocketException {
    this.game = game;
    this.ipAddress = InetAddress.getByName(ipAddress);
    this.socket = new DatagramSocket();
    this.port = port;
    this.username = username;
    this.primaryColor = primaryColor;
    this.secondaryColor = secondaryColor;
  }

  @Override
  public void run() {
    this.running = true;
    System.out.println("[MP/Client] : Client started on port " + port);

    // Start the platform snapshot sender thread
    platformSnapshotSender = new PlatformSnapshotSender();
    platformSnapshotSender.start();

    while (running) {
      byte[] data = new byte[1500];
      DatagramPacket packet = new DatagramPacket(data, data.length);
      try {
        socket.receive(packet);
      } catch (IOException e) {
        if (isConnected() && running)
          e.printStackTrace();
      }
      if (isConnected() && running)
        parsePacket(packet.getData(), packet.getAddress(), packet.getPort());
    }
  }

  void parsePacket(byte[] data, InetAddress address, int port) {
    String message = new String(data).trim();

    if (message.length() < 2) {
      System.out.println("[MP/Client] : Received invalid packet.");
      return;
    }

    PacketType type = Packet.lookupPacket(message.substring(0, 2));
    Packet packet = null;

    switch (type) {
      case ERROR:
        packet = new ErrorPacket(data);
        handleError((ErrorPacket) packet);
        break;
      case LOGIN:
        // Handle login packet to register the other player
        packet = new LoginPacket(data);
        handleLoginPacket((LoginPacket) packet);
        break;
      case MOVEMENTS_PLATFORM:
        // Handle snapshot platform packet to update platform positions
        packet = new MovementsPacket(data);

        MovementsPacket movementsPacket = (MovementsPacket) packet;

        game.updateMovements(movementsPacket, username);
        break;
      case SNAPSHOT:
        packet = new SnapshotPacket(data);
        game.updateGameStateFromSnapshot((SnapshotPacket) packet);
        break;
      case GAME_STATE:
        // Handle game state packet
        packet = new GameStatePacket(data);
        game.setGameState(GameState.valueOf(((GameStatePacket) packet).getState()));
        break;
      case INVALID:
        System.out.println("[MP/Client] : Received invalid packet.");
        break;
      // Handle other packet types as needed
      default:
        System.out.println("[MP/Client] : Unknown packet type: " + type);
        break;
    }
  }

  private void handleLoginPacket(LoginPacket packet) {
    // Check if the player is myself
    if (packet.getUsername().equals(this.username)) {
      // Set my assigned color (white or black)
      game.getPlayers().stream().filter((e) -> e.getName().equals(username)).findFirst().ifPresent(player -> {
        player.setWhiteOrBlack(packet.getWhiteOrBlack());

        game.clearPlayerObjects(player);

        game.initPlayerAndObjects(player, packet.getWhiteOrBlack(), true);
      });
      return;
    }

    // Register the other player in the game
    game.registerOtherPlayer(packet.getUsername(), packet.getWhiteOrBlack(), packet.getColor1(), packet.getColor2());
  }

  private void handleError(ErrorPacket packet) {
    System.out.println("[MP/Client] : Error from server: " + packet.getMessage());
    JOptionPane.showMessageDialog(null, packet.getMessage(), "Error",
        JOptionPane.ERROR_MESSAGE);

    // Disconnect the client upon receiving an error
    disconnect();

    // Return to single-player mode
    game.setMultiplayer(false);

    // reinitialize player and game objects for single-player if not running server
    if (!game.isServerRunning()) {
      game.clearEverything();
      game.initGameObjects();
    }
  }

  public void sendData(byte[] data) {
    DatagramPacket packet = new DatagramPacket(data, data.length, ipAddress, port);
    try {
      socket.send(packet);
    } catch (IOException e) {
      e.printStackTrace();
    }
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
   * @return the socket
   */
  public DatagramSocket getSocket() {
    return socket;
  }

  /**
   * @param socket the socket to set
   */
  public void setSocket(DatagramSocket socket) {
    this.socket = socket;
  }

  /**
   * @return the game
   */
  public Game getGame() {
    return game;
  }

  public boolean isConnected() {
    return socket != null && !socket.isClosed();
  }

  public void disconnect() {
    running = false;

    if (isConnected())
      socket.close();

    System.out.println("[MP/Client] : Client disconnected from port " + port);
  }

  /**
   * @return the username
   */
  public String getUsername() {
    return username;
  }

  /**
   * @param username the username to set
   */
  public void setUsername(String username) {
    this.username = username;
  }

  /**
   * @return the primaryColor
   */
  public Color getPrimaryColor() {
    return primaryColor;
  }

  /**
   * @param primaryColor the primaryColor to set
   */
  public void setPrimaryColor(Color primaryColor) {
    this.primaryColor = primaryColor;
  }

  /**
   * @return the secondaryColor
   */
  public Color getSecondaryColor() {
    return secondaryColor;
  }

  /**
   * @param secondaryColor the secondaryColor to set
   */
  public void setSecondaryColor(Color secondaryColor) {
    this.secondaryColor = secondaryColor;
  }

  /**
   * @return the running
   */
  public boolean isRunning() {
    return running;
  }

  /**
   * @param running the running to set
   */
  public void setRunning(boolean running) {
    this.running = running;
  }

  /**
   * @return the port
   */
  public int getPort() {
    return port;
  }
}
