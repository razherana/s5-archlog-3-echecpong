package mg.razherana.game.logic.utils;

import java.io.IOException;
import java.util.Properties;

public class Config {
  private final Properties properties;

  public enum Key {
    IS_SERVER, BALL_DAMAGE, BALL_SPEED_X, BALL_SPEED_Y, PLATFORM_WIDTH, PLATFORM_HEIGHT;
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
