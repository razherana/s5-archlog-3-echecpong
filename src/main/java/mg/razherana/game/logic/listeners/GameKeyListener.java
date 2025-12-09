package mg.razherana.game.logic.listeners;

public interface GameKeyListener {
  void onKeyPressed(int keyCode, KeyboardAdapter keyboardAdapter);

  void onKeyReleased(int keyCode, KeyboardAdapter keyboardAdapter);
}
