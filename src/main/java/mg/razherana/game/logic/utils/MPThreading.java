package mg.razherana.game.logic.utils;

import mg.razherana.game.net.Client;
import mg.razherana.game.net.Server;

public class MPThreading {
  private MPThreading() {
  }

  public static Thread generateClientThread(Client client, Config.Key key, Runnable callback) {
    return new Thread(() -> {
      while (client.isConnected() && client.isRunning()) {
        try {
          Thread.sleep(1000 / Integer.parseInt(client.getGame().getConfig().getProperty(key)));
        } catch (InterruptedException e) {
          e.printStackTrace();
        }

        if (client.isConnected() && client.isRunning()) {
          callback.run();
        }
      }
    });
  }

  public static Thread generateServerThread(Server server, Config.Key key, Runnable callback) {
    return new Thread(() -> {
      while (server.isRunning()) {
        try {
          Thread.sleep(1000 / Integer
              .parseInt(server.getGame().getConfig().getProperty(Config.Key.SERVER_SNAPSHOT_RATE)));
        } catch (InterruptedException e) {
          e.printStackTrace();
        }

        // Send snapshot to all clients
        if (server.isRunning())
          callback.run();
      }
    });
  }
}
