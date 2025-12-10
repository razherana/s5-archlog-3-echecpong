package mg.razherana.game.logic.utils;

import java.io.IOException;
import java.util.Properties;

public class Config {
  private final Properties properties;

  public enum Key {
    IS_SERVER, BALL_DAMAGE, BALL_SPEED_X, BALL_SPEED_Y, PLATFORM_WIDTH, PLATFORM_HEIGHT, PLATFORM_COMMAND_PLAYER1, PLATFORM_SPEED, PLATFORM_COMMAND_PLAYER2, SERVER_PORT, PLATFORM_POSITION_BLACK, PLATFORM_POSITION_WHITE, SERVER_SNAPSHOT_RATE, SERVER_PLATFORM_SNAPSHOT_RATE;
  }

  public String getProperty(Key key) {
    return properties.getProperty(key.name().toUpperCase());
  }

  public Config() throws IOException {
    properties = new Properties();
    properties.load(Config.class.getResourceAsStream("/config.properties"));
  }

  public String getProperty(String key) {
    return properties.getProperty(key);
  }
}
