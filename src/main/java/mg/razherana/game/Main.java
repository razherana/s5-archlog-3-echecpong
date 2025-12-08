package mg.razherana.game;

import javax.swing.SwingUtilities;

public class Main {
  public static void main(String[] args) {
    System.out.println("Welcome to the Game!");

    SwingUtilities.invokeLater(() -> {
      new Game();
    });
  }
}
