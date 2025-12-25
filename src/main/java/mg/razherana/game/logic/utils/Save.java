package mg.razherana.game.logic.utils;

import java.util.List;

import mg.razherana.game.Game;

public class Save {
  private List<PlayerRecord> players;

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

  public Save(Game game) {
    initRecordsFromGame(game);
  }

  private void initRecordsFromGame(Game game) {
    // this.players = new ArrayList<>();
    

    // var obj = game.getGameObjectsAsList();

    // for (GameObject gameObject : obj) {
    //   // if(gameObject)
    // }
  }

  /**
   * @return the players
   */
  public List<PlayerRecord> getPlayers() {
    return players;
  }

  /**
   * @param players the players to set
   */
  public void setPlayers(List<PlayerRecord> players) {
    this.players = players;
  }
}
