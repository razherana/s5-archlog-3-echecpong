package mg.razherana.game.logic.utils;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Properties;

public class Config {
  private final Properties properties;

  private final String baseUrl = "http://localhost:8080/api/configurations";
  private final HttpClient httpClient;

  public enum Key {
    BALL_DAMAGE, BALL_SPEED_X, BALL_SPEED_Y, PLATFORM_WIDTH, PLATFORM_HEIGHT, PLATFORM_COMMAND_PLAYER1, PLATFORM_SPEED,
    PLATFORM_COMMAND_PLAYER2, SERVER_PORT, PLATFORM_POSITION_BLACK, PLATFORM_POSITION_WHITE, SERVER_SNAPSHOT_RATE,
    SERVER_PLATFORM_SNAPSHOT_RATE, MVT_SPEED_RANDOM_X, MVT_SPEED_RANDOM_Y, SERVER_RANDOM_RATE, PIECE_NUMBER;
  }

  public String getProperty(Key key) {
    return getProperty(key.name().toUpperCase());
  }

  public Config() throws IOException {
    properties = new Properties();
    properties.load(Config.class.getResourceAsStream("/config.properties"));

    this.httpClient = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(5))
        .build();
  }

  public String getProperty(String key) {
    // try {
    //   String encodedName = URLEncoder.encode(key, StandardCharsets.UTF_8.toString());
    //   String url = baseUrl + "?name=" + encodedName;

    //   HttpRequest request = HttpRequest.newBuilder()
    //       .uri(URI.create(url))
    //       .timeout(Duration.ofSeconds(5))
    //       .GET()
    //       .build();

    //   HttpResponse<String> response = httpClient.send(
    //       request, HttpResponse.BodyHandlers.ofString());

    //   if (response.statusCode() == 200) {
    //     System.out.println("[Config/API] : " + key + "=" + response.body().trim());
    //     return response.body().trim();
    //   } else {
    //     System.err.println("API request failed with status: " + response.statusCode());
    //   }
    // } catch (Exception e) {
    //   System.err.println("Failed to fetch from API for key: " + key);
    //   e.printStackTrace();
    // }

    // Fallback to local properties
    System.out.println("[Config/Properties] : Failed and local : " + key + "=" + properties.getProperty(key));
    return properties.getProperty(key);
  }
}
