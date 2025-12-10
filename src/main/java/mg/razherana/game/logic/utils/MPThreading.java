package mg.razherana.game.logic.utils;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import mg.razherana.game.net.Client;
import mg.razherana.game.net.Server;

public class MPThreading {
  private MPThreading() {
  }

  public static ScheduledFuture<?> generateClientThread(
      Client client, Config.Key key, Runnable callback) {

    ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    int delayMs = Integer.parseInt(client.getGame().getConfig().getProperty(key));

    ScheduledFuture<?> future = executor.scheduleAtFixedRate(
        () -> {
          if (client.isConnected() && client.isRunning()) {
            callback.run();
          } else {
            // Stop the scheduler if client is disconnected
            executor.shutdown();
          }
        },
        0, // initial delay
        delayMs,
        TimeUnit.MILLISECONDS);

    // Return the future so you can cancel it if needed
    return future;
  }

  public static ScheduledFuture<?> generateServerThread(
      Server server, Config.Key key, Runnable callback) {

    ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    int delayMs = Integer.parseInt(
        server.getGame().getConfig().getProperty(key));

    ScheduledFuture<?> future = executor.scheduleAtFixedRate(
        () -> {
          if (server.isRunning()) {
            try {
              callback.run();
            } catch (Exception e) {
              // Log any errors but don't stop the thread
              e.printStackTrace();
            }
          } else {
            // Server stopped, shutdown the executor
            executor.shutdown();
          }
        },
        0, // initial delay
        delayMs,
        TimeUnit.MILLISECONDS);

    return future;
  }
}
