package mg.razherana.game.gfx;

import java.util.HashMap;
import java.util.Map;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;

import mg.razherana.game.Game;
import mg.razherana.game.gfx.menubar.GameMenubar;

public class GameFrame extends JFrame {
  private final Game game;
  private GameMenubar gameMenuBar;
  private CardLayout cardLayout;
  private JPanel mainPanel;

  private Map<String, JPanel> panels;
  private JPanel visiblePanel;

  public GameFrame(Game game, Map<String, JPanel> panels) {
    super("FF48 - Andriamanitra Fitiavana Ianao");

    this.game = game;
    this.panels = new HashMap<>(panels);

    // Handle window closing event
    addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        game.gracefulExit();
      }
    });

    setResizable(false);
    setSize(320, 380);
    setLocationRelativeTo(null);

    cardLayout = new CardLayout();

    mainPanel = new JPanel(cardLayout);
    mainPanel.setFocusable(true);
    mainPanel.setVisible(true);
    mainPanel.requestFocusInWindow();

    mainPanel.addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        game.onKeyPress(e.getKeyCode(), visiblePanel);
      }
    });

    for (var panelEntry : panels.entrySet()) {
      mainPanel.add(panelEntry.getValue(), panelEntry.getKey());
    }

    add(mainPanel);

    getContentPane().setBackground(new Color(0x00000011));

    this.gameMenuBar = new GameMenubar(this);
  }

  public void changePanel(String panelName) {
    visiblePanel = panels.get(panelName);
    cardLayout.show(mainPanel, panelName);
  }

  public void init(GamePanel panel) {
    panels.put("GAME", panel);

    mainPanel.add(panel, "GAME");

    mainPanel.revalidate();
    mainPanel.repaint();

    changePanel("GAME");
    setVisible(true);
  }

  /**
   * @return the game
   */
  public Game getGame() {
    return game;
  }

  /**
   * @return the gameMenuBar
   */
  public GameMenubar getGameMenuBar() {
    return gameMenuBar;
  }

  /**
   * @param gameMenuBar the gameMenuBar to set
   */
  public void setGameMenuBar(GameMenubar gameMenuBar) {
    this.gameMenuBar = gameMenuBar;
  }

  public JPanel getMainPanel() {
    return mainPanel;
  }
}
