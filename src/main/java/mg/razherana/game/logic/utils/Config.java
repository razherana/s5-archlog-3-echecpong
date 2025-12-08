package mg.razherana.game.logic.utils;

import java.io.IOException;
import java.util.Properties;

public class Config {
  private final Properties properties;

  public enum Key {
    IS_SERVER;
  }

  public String getProperty(Key key) {
    return properties.getProperty(key.name().toLowerCase());
  }

  public Config() throws IOException {
    properties = new Properties();
    properties.load(Config.class.getResourceAsStream("/config.properties"));
  }

  public String getProperty(String key) {
    return properties.getProperty(key);
  }
}
