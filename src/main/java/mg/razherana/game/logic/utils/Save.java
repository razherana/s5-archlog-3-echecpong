package mg.razherana.game.logic.utils;

import java.awt.Color;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Properties;
import java.util.TreeSet;

import mg.razherana.game.Game;
import mg.razherana.game.GameState;
import mg.razherana.game.logic.objects.ball.Ball;
import mg.razherana.game.logic.objects.chesspiece.ChessPiece;
import mg.razherana.game.logic.objects.platform.Platform;
import mg.razherana.game.net.packets.GameStatePacket;
import mg.razherana.game.net.packets.MovementsPacket;
import mg.razherana.game.net.packets.RandomMovementPacket;
import mg.razherana.game.net.packets.SnapshotPacket;

public class Save {
  public record BallRecord(float[] position, float[] velocity, float[] baseVelocity, float damage) {
  }

  public record PlayerRecord(String name,
      String color1,
      String color2,
      PlatformRecord platform,
      PieceRecord[] pieces) {
  }
  public record PlatformRecord(
      float[] platformPos,
      float[] platformSize,
      float[] platformVelocity) {
  }
  public record PieceRecord(String type, float[] position, float lifeMax, float life) {
  }

  public static Save loadFromPropertiesFile(String filePath) throws IOException {
    return loadFromPropertiesFile(Paths.get(filePath));
  }
  public static Save loadFromPropertiesFile(Path filePath) throws IOException {
    Properties properties = new Properties();
    try (Reader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
      properties.load(reader);
    }

    String packetGameState = properties.getProperty("packet.gameState", "");
    String packetSnapshot = properties.getProperty("packet.snapshot", "");
    String packetMovements = properties.getProperty("packet.movements", "");
    String packetRandomMovements = properties.getProperty("packet.randomMovements", "");

    String gameState = properties.getProperty("game.state", "");

    int playersCount = parseInt(properties.getProperty("players.count", "0"));
    List<PlayerRecord> players = new ArrayList<>(Math.max(0, playersCount));
    for (int i = 0; i < playersCount; i++) {
      String base = "players." + i + ".";

      String name = properties.getProperty(base + "name", "");
      String color1 = properties.getProperty(base + "color1", "0");
      String color2 = properties.getProperty(base + "color2", "0");

      PlatformRecord platform = null;
      String platformNullMarker = properties.getProperty(base + "platform", null);
      if (platformNullMarker == null) {
        float[] pos = parseFloatPair(properties.getProperty(base + "platform.pos", "0,0"));
        float[] size = parseFloatPair(properties.getProperty(base + "platform.size", "0,0"));
        float[] vel = parseFloatPair(properties.getProperty(base + "platform.vel", "0,0"));
        platform = new PlatformRecord(pos, size, vel);
      }

      int piecesCount = parseInt(properties.getProperty(base + "pieces.count", "0"));
      PieceRecord[] pieces = new PieceRecord[Math.max(0, piecesCount)];
      for (int j = 0; j < pieces.length; j++) {
        String pBase = base + "pieces." + j + ".";
        String type = properties.getProperty(pBase + "type", "");
        float[] pos = parseFloatPair(properties.getProperty(pBase + "pos", "0,0"));
        float lifeMax = parseFloat(properties.getProperty(pBase + "lifeMax", "0"));
        float life = parseFloat(properties.getProperty(pBase + "life", "0"));
        pieces[j] = new PieceRecord(type, pos, lifeMax, life);
      }

      players.add(new PlayerRecord(name, color1, color2, platform, pieces));
    }

    BallRecord ball = null;
    String ballNullMarker = properties.getProperty("ball", null);
    if (ballNullMarker == null) {
      float[] pos = parseFloatPair(properties.getProperty("ball.pos", "0,0"));
      float[] vel = parseFloatPair(properties.getProperty("ball.vel", "0,0"));
      float[] baseVel = parseFloatPair(properties.getProperty("ball.baseVel", "0,0"));
      float damage = parseFloat(properties.getProperty("ball.damage", "0"));
      ball = new BallRecord(pos, vel, baseVel, damage);
    }

    int frCount = parseInt(properties.getProperty("futureRandoms.count", "0"));
    float[][] futureRandoms = new float[Math.max(0, frCount)][2];
    for (int i = 0; i < futureRandoms.length; i++) {
      float[] pair = parseFloatPair(properties.getProperty("futureRandoms." + i, "0,0"));
      futureRandoms[i][0] = pair[0];
      futureRandoms[i][1] = pair[1];
    }

    return new Save(
        players,
        gameState,
        ball,
        futureRandoms,
        packetGameState,
        packetSnapshot,
        packetMovements,
        packetRandomMovements);
  }
  private static int parseInt(String value) {
    try {
      return Integer.parseInt(value.trim());
    } catch (Exception e) {
      return 0;
    }
  }
  private static float parseFloat(String value) {
    try {
      return Float.parseFloat(value.trim());
    } catch (Exception e) {
      return 0f;
    }
  }

  private static float[] parseFloatPair(String value) {
    if (value == null)
      return new float[] { 0f, 0f };

    String[] parts = value.trim().split(",", 2);
    if (parts.length < 2)
      return new float[] { parseFloat(value), 0f };

    return new float[] { parseFloat(parts[0]), parseFloat(parts[1]) };
  }

  private static String escapePropertyValue(String value) {
    if (value == null)
      return "";

    String escaped = value
        .replace("\\", "\\\\")
        .replace("\n", "\\n")
        .replace("\r", "\\r")
        .replace("\t", "\\t");

    if (!escaped.isEmpty() && escaped.charAt(0) == ' ') {
      escaped = "\\ " + escaped.substring(1);
    }
    return escaped;
  }


  private final List<PlayerRecord> players;

  private final String gameState;

  private final BallRecord ball;

  private final float[][] futureRandoms;

  private final String packetGameState;

  private final String packetSnapshot;

  private final String packetMovements;

  private final String packetRandomMovements;

  public Save(Game game) {
    var snapshotPacket = new SnapshotPacket(game);
    var movementsPacket = new MovementsPacket(game);
    var randomMovementPacket = new RandomMovementPacket(game);
    var gameStatePacket = new GameStatePacket(game);

    this.packetSnapshot = new String(snapshotPacket.getData(), StandardCharsets.UTF_8);
    this.packetMovements = new String(movementsPacket.getData(), StandardCharsets.UTF_8);
    this.packetRandomMovements = new String(randomMovementPacket.getData(), StandardCharsets.UTF_8);
    this.packetGameState = new String(gameStatePacket.getData(), StandardCharsets.UTF_8);

    this.gameState = game.getGameState().name();

    synchronized (game.getGameObjectsLock()) {
      // Players + their objects
      this.players = new ArrayList<>(game.getPlayers().size());
      for (var player : game.getPlayers()) {
        Platform platform = player.getPlatform();
        PlatformRecord platformRecord = null;

        if (platform != null) {
          platformRecord = new PlatformRecord(
              new float[] { platform.getPosition().x, platform.getPosition().y },
              new float[] { platform.getSize().x, platform.getSize().y },
              new float[] { platform.getVelocity().x, platform.getVelocity().y });
        }

        var pieces = player.getChessPieces();
        PieceRecord[] pieceRecords = new PieceRecord[pieces.size()];
        for (int i = 0; i < pieces.size(); i++) {
          ChessPiece piece = pieces.get(i);
          pieceRecords[i] = new PieceRecord(
              piece.getType().name(),
              new float[] { piece.getPosition().x, piece.getPosition().y },
              piece.getLifeMax(),
              piece.getLife());
        }

        this.players.add(new PlayerRecord(
            player.getName(),
            Integer.toString(player.getPrimaryColor().getRGB()),
            Integer.toString(player.getSecondaryColor().getRGB()),
            platformRecord,
            pieceRecords));
      }

      // Ball
      Ball gameBall = game.getGameBall();
      if (gameBall != null) {
        this.ball = new BallRecord(
            new float[] { gameBall.getPosition().x, gameBall.getPosition().y },
            new float[] { gameBall.getVelocity().x, gameBall.getVelocity().y },
            new float[] { gameBall.getBaseVelocity().x, gameBall.getBaseVelocity().y },
            gameBall.getDamage());
      } else {
        this.ball = null;
      }

      // Future randoms (copy for immutability)
      Deque<float[]> fr = game.getFutureRandoms();
      var frList = new ArrayList<float[]>(fr);
      this.futureRandoms = new float[frList.size()][2];
      for (int i = 0; i < frList.size(); i++) {
        float[] pair = frList.get(i);
        this.futureRandoms[i][0] = pair[0];
        this.futureRandoms[i][1] = pair[1];
      }
    }
  }

  private Save(
      List<PlayerRecord> players,
      String gameState,
      BallRecord ball,
      float[][] futureRandoms,
      String packetGameState,
      String packetSnapshot,
      String packetMovements,
      String packetRandomMovements) {
    this.players = players;
    this.gameState = gameState;
    this.ball = ball;
    this.futureRandoms = futureRandoms;
    this.packetGameState = packetGameState;
    this.packetSnapshot = packetSnapshot;
    this.packetMovements = packetMovements;
    this.packetRandomMovements = packetRandomMovements;
  }

  public void saveToPropertiesFile(String filePath) throws IOException {
    saveToPropertiesFile(Paths.get(filePath));
  }

  public void saveToPropertiesFile(Path filePath) throws IOException {
    var properties = toProperties();

    try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8)) {
      for (String key : new TreeSet<>(properties.keySet())) {
        writer.write(key);
        writer.write('=');
        writer.write(escapePropertyValue(properties.get(key)));
        writer.newLine();
      }
    }
  }

  /**
   * @return the players
   */
  public List<PlayerRecord> getPlayers() {
    return players;
  }

  public String getGameState() {
    return gameState;
  }

  public BallRecord getBall() {
    return ball;
  }

  public float[][] getFutureRandoms() {
    return futureRandoms;
  }

  private java.util.Map<String, String> toProperties() {
    java.util.Map<String, String> props = new java.util.LinkedHashMap<>();

    // Packet-encoded values (reused from multiplayer, as requested)
    props.put("packet.gameState", packetGameState);
    props.put("packet.snapshot", packetSnapshot);
    props.put("packet.movements", packetMovements);
    props.put("packet.randomMovements", packetRandomMovements);

    // Explicit fields for easier restore/debug
    props.put("game.state", gameState);

    props.put("players.count", Integer.toString(players.size()));
    for (int i = 0; i < players.size(); i++) {
      PlayerRecord p = players.get(i);
      String base = "players." + i + ".";
      props.put(base + "name", p.name());
      props.put(base + "color1", p.color1());
      props.put(base + "color2", p.color2());

      PlatformRecord platform = p.platform();
      if (platform != null) {
        props.put(base + "platform.pos", platform.platformPos()[0] + "," + platform.platformPos()[1]);
        props.put(base + "platform.size", platform.platformSize()[0] + "," + platform.platformSize()[1]);
        props.put(base + "platform.vel", platform.platformVelocity()[0] + "," + platform.platformVelocity()[1]);
      } else {
        props.put(base + "platform", "null");
      }

      props.put(base + "pieces.count", Integer.toString(p.pieces().length));
      for (int j = 0; j < p.pieces().length; j++) {
        PieceRecord piece = p.pieces()[j];
        String pBase = base + "pieces." + j + ".";
        props.put(pBase + "type", piece.type());
        props.put(pBase + "pos", piece.position()[0] + "," + piece.position()[1]);
        props.put(pBase + "lifeMax", Float.toString(piece.lifeMax()));
        props.put(pBase + "life", Float.toString(piece.life()));
      }
    }

    if (ball != null) {
      props.put("ball.pos", ball.position()[0] + "," + ball.position()[1]);
      props.put("ball.vel", ball.velocity()[0] + "," + ball.velocity()[1]);
      props.put("ball.baseVel", ball.baseVelocity()[0] + "," + ball.baseVelocity()[1]);
      props.put("ball.damage", Float.toString(ball.damage()));
    } else {
      props.put("ball", "null");
    }

    props.put("futureRandoms.count", Integer.toString(futureRandoms.length));
    for (int i = 0; i < futureRandoms.length; i++) {
      props.put("futureRandoms." + i, futureRandoms[i][0] + "," + futureRandoms[i][1]);
    }

    return props;
  }
  
  public void loadIntoGame(Game game) {
    if (game == null)
      return;

    synchronized (game.getGameObjectsLock()) {
      // In single-player, rebuild a known-good baseline (controls, base objects)
      if (!game.isMultiplayer() && !game.isServerRunning()) {
        game.clearEverything();
        // Mimic restartAfterGameOver's approach to avoid duplicated base objects
        game.getGameObjects().clear();
        game.initGameObjects();

        // Align existing local players to the saved ones (names/colors)
        int localCount = Math.min(game.getPlayers().size(), players.size());
        for (int i = 0; i < localCount; i++) {
          var gp = game.getPlayers().get(i);
          var sp = players.get(i);

          gp.setName(sp.name());
          gp.setPrimaryColor(parseColor(sp.color1()));
          gp.setSecondaryColor(parseColor(sp.color2()));
        }

        // Register any extra saved players (as non-local)
        for (int i = game.getPlayers().size(); i < players.size(); i++) {
          var sp = players.get(i);
          int whiteOrBlack = (i % 2) + 1;
          game.registerOtherPlayer(sp.name(), whiteOrBlack, parseColor(sp.color1()), parseColor(sp.color2()));
        }
      } else {
        // Multiplayer or server: ensure all saved player names exist
        for (var sp : players) {
          boolean exists = game.getPlayers().stream().anyMatch(p -> p.getName().equals(sp.name()));
          if (!exists) {
            int whiteOrBlack = 1;
            game.registerOtherPlayer(sp.name(), whiteOrBlack, parseColor(sp.color1()), parseColor(sp.color2()));
          }
        }
      }

      // Restore future randoms first (affects gameplay randomness)
      game.setFutureRandoms(futureRandoms);

      // Restore game state
      try {
        if (gameState != null && !gameState.isBlank()) {
          game.setGameState(GameState.valueOf(gameState.trim()));
        }
      } catch (Exception ignored) {
        // Keep current game state if saved value is invalid
      }

      // Reuse packet parsing logic (same as multiplayer)
      if (packetSnapshot != null && !packetSnapshot.isBlank()) {
        SnapshotPacket snapshotPacket = new SnapshotPacket(packetSnapshot.getBytes(StandardCharsets.UTF_8));
        game.updateGameStateFromSnapshot(snapshotPacket);
      }

      if (packetMovements != null && !packetMovements.isBlank()) {
        MovementsPacket movementsPacket = new MovementsPacket(packetMovements.getBytes(StandardCharsets.UTF_8));
        game.updateMovements(movementsPacket);
      }

      if (packetRandomMovements != null && !packetRandomMovements.isBlank()) {
        RandomMovementPacket randomPacket = new RandomMovementPacket(packetRandomMovements.getBytes(StandardCharsets.UTF_8));
        game.setFutureRandoms(randomPacket.getRandomMovements());
      }

      if (packetGameState != null && !packetGameState.isBlank()) {
        try {
          GameStatePacket gsPacket = new GameStatePacket(packetGameState.getBytes(StandardCharsets.UTF_8));
          game.setGameState(GameState.valueOf(gsPacket.getState().trim()));
        } catch (Exception ignored) {
          // Keep current game state
        }
      }

      // Restore platform sizes (not present in movements packet)
      for (var sp : players) {
        PlatformRecord pr = sp.platform();
        if (pr == null)
          continue;

        for (var player : game.getPlayers()) {
          if (!player.getName().equals(sp.name()))
            continue;
          Platform platform = player.getPlatform();
          if (platform == null)
            continue;

          platform.setSize(new Vector2(pr.platformSize()[0], pr.platformSize()[1]));
          platform.getPosition().set(pr.platformPos()[0], pr.platformPos()[1]);
          platform.getVelocity().set(pr.platformVelocity()[0], pr.platformVelocity()[1]);
        }
      }

      // Restore ball extras (damage/baseVel) and ensure position/velocity match saved values
      if (ball != null) {
        Ball gameBall = game.getGameBall();
        if (gameBall != null) {
          gameBall.setPosition(new Vector2(ball.position()[0], ball.position()[1]));
          gameBall.setVelocity(new Vector2(ball.velocity()[0], ball.velocity()[1]));
          gameBall.setBaseVelocity(new Vector2(ball.baseVelocity()[0], ball.baseVelocity()[1]));
          gameBall.setDamage(ball.damage());
        }
      }

      // Restore chess piece life values by matching (playerName, type, position)
      final float eps = 0.001f;
      for (var sp : players) {
        for (var gp : game.getPlayers()) {
          if (!gp.getName().equals(sp.name()))
            continue;

          for (ChessPiece piece : gp.getChessPieces()) {
            PieceRecord match = findMatchingPiece(sp.pieces(), piece, eps);
            if (match != null) {
              piece.setLifeMax(match.lifeMax());
              piece.setLife(match.life());
            }
          }
        }
      }
    }
  }

  private static Color parseColor(String rgbIntString) {
    try {
      return new Color(Integer.parseInt(rgbIntString.trim()), true);
    } catch (Exception e) {
      try {
        return new Color(Integer.parseInt(rgbIntString.trim()));
      } catch (Exception ignored) {
        return new Color(0);
      }
    }
  }

  private static PieceRecord findMatchingPiece(PieceRecord[] candidates, ChessPiece piece, float eps) {
    if (candidates == null || piece == null)
      return null;

    String type = piece.getType().name();
    float x = piece.getPosition().x;
    float y = piece.getPosition().y;

    for (PieceRecord cand : candidates) {
      if (cand == null)
        continue;
      if (!type.equals(cand.type()))
        continue;

      float dx = Math.abs(cand.position()[0] - x);
      float dy = Math.abs(cand.position()[1] - y);
      if (dx <= eps && dy <= eps)
        return cand;
    }
    return null;
  }
}
