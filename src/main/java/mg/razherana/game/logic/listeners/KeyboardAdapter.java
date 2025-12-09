package mg.razherana.game.logic.listeners;

import java.util.List;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import mg.razherana.game.Game;

public class KeyboardAdapter extends KeyAdapter {
  private final Set<Integer> keys = new HashSet<>();

  private final Game game;

  private final List<GameKeyListener> gameKeyListeners = new ArrayList<>();

  private final Object keysLock = new Object();

  private final Object gameKeyListenersLock = new Object();

  /**
   * @param game
   */
  public KeyboardAdapter(Game game) {
    this.game = game;
  }

  @Override
  public void keyPressed(KeyEvent e) {
    keys.add(e.getKeyCode());

    synchronized (gameKeyListenersLock) {
      for (GameKeyListener listener : gameKeyListeners)
        listener.onKeyPressed(e.getKeyCode(), this);
    }
  }

  @Override
  public void keyReleased(KeyEvent e) {
    keys.remove(e.getKeyCode());

    synchronized (gameKeyListenersLock) {
      for (GameKeyListener listener : gameKeyListeners)
        listener.onKeyReleased(e.getKeyCode(), this);
    }
  }

  public boolean hasKey(int key) {
    synchronized (keysLock) {
      return keys.contains(key);
    }
  }

  public void addListener(GameKeyListener e) {
    synchronized (gameKeyListenersLock) {
      gameKeyListeners.add(e);
    }
  }

  /**
   * @return the game
   */
  public Game getGame() {
    return game;
  }
}
