package mg.razherana.game.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;

import mg.razherana.game.Game;
import mg.razherana.game.GameState;
import mg.razherana.game.logic.utils.Config;
import mg.razherana.game.net.packets.ErrorPacket;
import mg.razherana.game.net.packets.GameStatePacket;
import mg.razherana.game.net.packets.LoginPacket;
import mg.razherana.game.net.packets.Packet;
import mg.razherana.game.net.packets.PacketType;
import mg.razherana.game.net.packets.RandomMovementPacket;
import mg.razherana.game.net.packets.MovementsPacket;
import mg.razherana.game.net.packets.SnapshotPacket;

public class Server extends Thread {
  private DatagramSocket socket;
  public Game game;
  private ServerSnapshotThread snapshotThread;
  private RandomMovementThread randomMovementThread;

  class RandomMovementThread extends Thread {
    @Override
    public void run() {
      while (running && isRunning()) {
        try {
          Thread.sleep(1000 / Integer
              .parseInt(game.getConfig().getProperty(Config.Key.SERVER_RANDOM_RATE)));
        } catch (InterruptedException e) {
          e.printStackTrace();
        }

        // Send snapshot to all clients
        if (running && isRunning())
          sendRandomMovementToAllClients();
      }
    }
  }

  class ServerSnapshotThread extends Thread {
    @Override
    public void run() {
      while (running && isRunning()) {
        try {
          Thread.sleep(1000 / Integer
              .parseInt(game.getConfig().getProperty(Config.Key.SERVER_SNAPSHOT_RATE)));
        } catch (InterruptedException e) {
          e.printStackTrace();
        }

        // Send snapshot to all clients
        if (running && isRunning())
          sendSnapshotToAllClients();
      }
    }

  }

  private ServerPlatformThread platformThread;

  class ServerPlatformThread extends Thread {
    @Override
    public void run() {
      while (running && isRunning()) {
        try {
          Thread.sleep(1000 / Integer
              .parseInt(game.getConfig().getProperty(Config.Key.SERVER_PLATFORM_SNAPSHOT_RATE)));
        } catch (InterruptedException e) {
          e.printStackTrace();
        }

        // Send platform snapshot to all clients
        if (running && isRunning())
          sendMovementsSnapshotToAllClients();
      }
    }
  }

  private ArrayList<PlayerMP> connectedPlayers = new ArrayList<>();

  private volatile boolean running = false;

  public Server(Game game) {
    this.game = game;
    try {
      this.socket = new DatagramSocket(Integer.parseInt(game.getConfig().getProperty(Config.Key.SERVER_PORT)));
    } catch (SocketException e) {
      e.printStackTrace();
    }
  }

  public void sendRandomMovementToAllClients() {
    RandomMovementPacket movementPacket = new RandomMovementPacket(game);
    sendDataToAllClients(movementPacket.getData());
  }

  public void sendMovementsSnapshotToAllClients() {
    MovementsPacket movementsPacket = new MovementsPacket(game);
    sendDataToAllClients(movementsPacket.getData());
  }

  public void sendSnapshotToAllClients() {
    SnapshotPacket snapshotPacket = new SnapshotPacket(game);
    sendDataToAllClients(snapshotPacket.getData());
  }

  public boolean isRunning() {
    return socket != null && !socket.isClosed();
  }

  public ArrayList<PlayerMP> getConnectedPlayers() {
    return connectedPlayers;
  }

  public void setConnectedPlayers(ArrayList<PlayerMP> connectedPlayers) {
    this.connectedPlayers = connectedPlayers;
  }

  @Override
  public void run() {
    running = true;

    snapshotThread = new ServerSnapshotThread();
    snapshotThread.start();

    platformThread = new ServerPlatformThread();
    platformThread.start();

    randomMovementThread = new RandomMovementThread();
    randomMovementThread.start();

    System.out.println("[MP/Server] : Server started on port " + socket.getLocalPort());

    while (running) {
      byte[] data = new byte[512];
      DatagramPacket packet = new DatagramPacket(data, data.length);
      try {
        socket.receive(packet);
      } catch (IOException e) {
        if (!running || socket.isClosed()) {
          System.out.println("[MP/Server] : Socket closed, stopping server.");
          break;
        }
        e.printStackTrace();
      }
      parsePacket(packet.getData(), packet.getAddress(), packet.getPort());
    }
  }

  public void addConnection(PlayerMP playerMP, LoginPacket loginPacket) {
    boolean alreadyConnected = false;

    for (PlayerMP player : connectedPlayers) {
      if (player.getIpAddress() == null && player.getPort() == -1) {
        // Set IP address and port
        player.setPort(playerMP.getPort());
        player.setIpAddress(playerMP.getIpAddress());

        alreadyConnected = true;
      } else {
        sendData(loginPacket.getData(), player.getIpAddress(), player.getPort());

        // Rehefa tafiditra
        LoginPacket loginPacket2 = new LoginPacket(player.getName(), player.getWhiteOrBlack(), player.getPrimaryColor(),
            player.getSecondaryColor());
        sendData(loginPacket2.getData(), playerMP.getIpAddress(), playerMP.getPort());
      }
    }

    int i = 0;

    if (!alreadyConnected) {
      i = 1;
      connectedPlayers.add(playerMP);
    }

    // Modify the login packet to set the correct whiteOrBlack
    LoginPacket modifiedLoginPacket = new LoginPacket(loginPacket.getUsername(),
        // Check if first or second player
        (connectedPlayers.size() - i + 1) % 2,
        loginPacket.getColor1(), loginPacket.getColor2());

    // Send login packet to the new player
    sendData(modifiedLoginPacket.getData(), playerMP.getIpAddress(), playerMP.getPort());
  }

  public void parsePacket(byte[] data, InetAddress address, int port) {
    String message = new String(data).trim();

    if (message.length() < 2) {
      System.out.println("[MP/Server] : Invalid packet");
      return;
    }

    PacketType packetType = Packet.lookupPacket(message.substring(0, 2));

    switch (packetType) {
      case LOGIN:
        System.out.println("[MP/Server] : Login packet received. Handling ...");

        LoginPacket loginPacket = new LoginPacket(data);

        // Verify if the player are full
        if (connectedPlayers.size() >= 2) {
          sendData(new ErrorPacket("Server is full").getData(), address, port);
          return;
        }

        // Add the client
        PlayerMP playerMP = new PlayerMP(loginPacket.getUsername(), loginPacket.getColor1(), loginPacket.getColor2(),
            new ArrayList<>(), address, port);

        // Set the whiteOrBlack based on current connections

        // If first player, white should be (1), else black (0)
        playerMP.setWhiteOrBlack((connectedPlayers.size() + 1) % 2);

        game.initPlayerAndObjects(
            playerMP,
            (connectedPlayers.size() + 1) % 2, false);

        game.getPlayers().add(playerMP);

        addConnection(playerMP, loginPacket);

        break;

      case MOVEMENTS_PLATFORM:
        MovementsPacket platformPacket = new MovementsPacket(data);
        platformPacket.setBall(null); // Ignore ball data from client

        game.updateMovements(platformPacket);

        break;

      case GAME_STATE:
        // Handle game state packet
        GameStatePacket gameStatePacket = new GameStatePacket(data);

        game.setGameState(GameState.valueOf(gameStatePacket.getState()));

        sendDataToAllClients(data);

        break;

      default:
        System.out.println("[MP/Server] : Invalid packet");
        break;
    }
  }

  public void sendData(byte[] data, InetAddress ipAddress, int port) {
    // If server == client
    if (ipAddress == null || port == -1) {
      game.getClient().parsePacket(data, ipAddress, port);
      return;
    }

    System.out.println("[MP/Server] : Sending packet to " + ipAddress.getHostAddress() + ":" + port + " via server: "
        + new String(data));

    DatagramPacket packet = new DatagramPacket(data, data.length, ipAddress, port);
    try {
      socket.send(packet);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void sendDataToAllClients(byte[] data) {
    for (PlayerMP playerMP : connectedPlayers)
      sendData(data, playerMP.getIpAddress(), playerMP.getPort());
  }

  public void stopServer() {
    if (socket != null && !socket.isClosed()) {
      socket.close();
      running = false;

      this.interrupt();

      System.out.println("[MP/Server] : Server stopped.");
    }
  }
}
