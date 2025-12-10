package mg.razherana.game.net.packets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import mg.razherana.game.Game;

public class SnapshotPacket extends Packet {
  private static final String[][] TYPE_MAPPINGS = {
      { "PAWN_WHITE", "11" },
      { "PAWN_BLACK", "01" },
      { "ROOK_WHITE", "15" },
      { "ROOK_BLACK", "05" },
      { "KNIGHT_WHITE", "13" },
      { "KNIGHT_BLACK", "03" },
      { "BISHOP_WHITE", "14" },
      { "BISHOP_BLACK", "04" },
      { "QUEEN_WHITE", "19" },
      { "QUEEN_BLACK", "09" },
      { "KING_WHITE", "10" },
      { "KING_BLACK", "00" }
  };

  private static final Map<String, String> TYPE_TO_CODE;
  private static final Map<String, String> CODE_TO_TYPE;

  static {
    TYPE_TO_CODE = new HashMap<>();
    CODE_TO_TYPE = new HashMap<>();

    for (String[] mapping : TYPE_MAPPINGS) {
      TYPE_TO_CODE.put(mapping[0], mapping[1]);
      CODE_TO_TYPE.put(mapping[1], mapping[0]);
    }
  }

  // Example :
  // p:razherana,200,-142,400,200;faniry,120,-80,122,233|pc:razherana>11(120,93)&(150,160)~10(175,190);faniry>00(209,90)|gs:PAUSED|pl:razherana,1,12308712,12718321;faniry,2,12837123,12938123
  String gameState;
  ChessPieceDTO[] chessPieces;
  PlayerDTO[] players;

  public record PlayerDTO(String name, int whiteOrBlack, int color1, int color2) {
  }

  public record ChessPieceDTO(String type, float x, float y, String playerName) {
  }

  public SnapshotPacket(byte[] data) {
    super(PacketType.SNAPSHOT.getPacketId());

    String completeState = readData(data);

    String[] segments = completeState.split("\\|");

    // Parse chess pieces
    String chessPieceData = segments[1].trim().substring(3); // Remove "pc:"
    List<ChessPieceDTO> pieceBuffer = new ArrayList<>();

    if (!chessPieceData.isBlank()) {
      if (chessPieceData.contains(">")) {
        String[] playerGroups = chessPieceData.split(";");

        for (String playerGroup : playerGroups) {
          if (playerGroup.isBlank())
            continue;

          String[] playerParts = playerGroup.split(">", 2);
          if (playerParts.length < 2)
            continue;

          String playerName = playerParts[0].trim();
          String typeSection = playerParts[1].trim();
          if (typeSection.isEmpty())
            continue;

          String[] typeGroups = typeSection.split("~");
          for (String typeGroup : typeGroups) {
            typeGroup = typeGroup.trim();
            if (typeGroup.isEmpty())
              continue;

            int parenIndex = typeGroup.indexOf('(');
            if (parenIndex < 0)
              continue;

            String typeToken = typeGroup.substring(0, parenIndex).trim();
            String resolvedType = CODE_TO_TYPE.getOrDefault(typeToken, typeToken);
            String positionsSection = typeGroup.substring(parenIndex).trim();
            if (positionsSection.isEmpty())
              continue;

            String[] positionTokens = positionsSection.split("&");
            for (String positionToken : positionTokens) {
              positionToken = positionToken.trim();
              if (positionToken.isEmpty())
                continue;

              if (!positionToken.startsWith("(") || !positionToken.endsWith(")"))
                continue;

              String coordinates = positionToken.substring(1, positionToken.length() - 1);
              String[] xy = coordinates.split(",");
              if (xy.length < 2)
                continue;

              float x = Float.parseFloat(xy[0].trim());
              float y = Float.parseFloat(xy[1].trim());
              pieceBuffer.add(new ChessPieceDTO(resolvedType, x, y, playerName));
            }
          }
        }
      } else {
        String[] chessPieceStrings = chessPieceData.split(";");

        for (String pieceString : chessPieceStrings) {
          if (pieceString.isBlank())
            continue;

          String[] parts = pieceString.split("\\(", 2);
          if (parts.length < 2)
            continue;

          String typeToken = parts[0].trim();
          String resolvedType = CODE_TO_TYPE.getOrDefault(typeToken, typeToken);

          String payload = parts[1].trim();
          if (!payload.endsWith(")"))
            continue;

          String[] posAndPlayer = payload.substring(0, payload.length() - 1).split(",");
          if (posAndPlayer.length < 3)
            continue;

          float x = Float.parseFloat(posAndPlayer[0].trim());
          float y = Float.parseFloat(posAndPlayer[1].trim());
          String playerName = posAndPlayer[2].trim();

          pieceBuffer.add(new ChessPieceDTO(resolvedType, x, y, playerName));
        }
      }
    }

    chessPieces = pieceBuffer.toArray(new ChessPieceDTO[0]);

    // Parse game state
    String gameStateData = segments[2].trim().substring(3); // Remove "gs:"
    gameState = gameStateData;

    // Parse players
    String playerData = segments[3].trim().substring(3); // Remove "pl:"
    String[] playerStrings = playerData.split(";");
    players = new PlayerDTO[playerStrings.length];

    for (int i = 0; i < playerStrings.length; i++) {
      if (playerStrings[i].isBlank())
        continue;
      
      String[] parts = playerStrings[i].trim().split(",");

      String name = parts[0].trim();
      int whiteOrBlack = Integer.parseInt(parts[1].trim());
      int color1 = Integer.parseInt(parts[2].trim());
      int color2 = Integer.parseInt(parts[3].trim());

      players[i] = new PlayerDTO(name, whiteOrBlack, color1, color2);
    }
  }

  public SnapshotPacket(Game game) {
    super(PacketType.SNAPSHOT.getPacketId());

    // Gather chess pieces
    chessPieces = game.getPlayers().stream()
        .flatMap(player -> player.getChessPieces().stream()
            .map(piece -> new ChessPieceDTO(piece.getType().name(), piece.getPosition().x, piece.getPosition().y,
                player.getName())))
        .toArray(ChessPieceDTO[]::new);

    // Gather game state
    gameState = game.getGameState().name();

    // Gather players
    players = new PlayerDTO[game.getPlayers().size()];

    for (int i = 0; i < game.getPlayers().size(); i++) {
      var player = game.getPlayers().get(i);
      players[i] = new PlayerDTO(player.getName(),
          (i + 1) % 2,
          player.getPrimaryColor().getRGB(),
          player.getSecondaryColor().getRGB());
    }
  }

  @Override
  public byte[] getData() {
    // Generate the data string
    StringBuilder data = new StringBuilder();
    data.append("02");

    data.append("p:|pc:");

    Map<String, Map<String, List<ChessPieceDTO>>> groupedPieces = new LinkedHashMap<>();

    for (ChessPieceDTO piece : chessPieces) {
      groupedPieces
          .computeIfAbsent(piece.playerName(), key -> new LinkedHashMap<>())
          .computeIfAbsent(piece.type(), key -> new ArrayList<>())
          .add(piece);
    }

    boolean firstPlayer = true;
    for (Map.Entry<String, Map<String, List<ChessPieceDTO>>> playerEntry : groupedPieces.entrySet()) {
      if (!firstPlayer)
        data.append(";");
      firstPlayer = false;

      data.append(playerEntry.getKey()).append(">");

      boolean firstType = true;
      for (Map.Entry<String, List<ChessPieceDTO>> typeEntry : playerEntry.getValue().entrySet()) {
        if (!firstType)
          data.append("~");
        firstType = false;

        String typeCode = TYPE_TO_CODE.getOrDefault(typeEntry.getKey(), typeEntry.getKey());
        data.append(typeCode);

        boolean firstPosition = true;
        for (ChessPieceDTO piece : typeEntry.getValue()) {
          if (!firstPosition)
            data.append("&");
          firstPosition = false;

          data.append("(").append(piece.x()).append(",").append(piece.y()).append(")");
        }
      }
    }

    data.append("|gs:");
    data.append(gameState);

    data.append("|pl:");
    for (int i = 0; i < players.length; i++) {
      PlayerDTO player = players[i];
      data.append(player.name()).append(",").append(player.whiteOrBlack()).append(",").append(player.color1())
          .append(",")
          .append(player.color2());
      if (i < players.length - 1)
        data.append(";");
    }

    return data.toString().getBytes();
  }

  /**
   * @return the gameState
   */
  public String getGameState() {
    return gameState;
  }

  /**
   * @return the chessPieces
   */
  public ChessPieceDTO[] getChessPieces() {
    return chessPieces;
  }

  /**
   * @return the players
   */
  public PlayerDTO[] getPlayers() {
    return players;
  }
}
