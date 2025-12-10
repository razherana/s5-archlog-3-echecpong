package mg.razherana.game.gfx.menubar;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import mg.razherana.game.Game;
import mg.razherana.game.gfx.GameFrame;

public class GameMenubar extends JMenuBar {
  Game game;
  GameFrame frame;

  public GameMenubar(GameFrame frame) {
    super();

    this.frame = frame;
    this.game = frame.getGame();

    initDefaultMenu();

    initMultiplayerMenu();

    // Add to the frame
    frame.setJMenuBar(this);
  }

  private void initDefaultMenu() {
    // Add exit etc. here
    JMenu fileMenu = new JMenu("File");
    JMenuItem exitItem = new JMenuItem("Exit");
    
    exitItem.addActionListener(e -> {
      game.gracefulExit();
    });

    fileMenu.add(exitItem);
    this.add(fileMenu);
  }

  private void initMultiplayerMenu() {
    JMenu multiplayerMenu = new JMenu("Multiplayer");

    // Add menu items here
    JMenuItem singlePlayerItem = new JMenuItem("Single Player");
    singlePlayerItem.addActionListener(e -> {
      game.setMultiplayer(false);
    });

    JMenuItem hostServerItem = new JMenuItem("Host Server");
    hostServerItem.addActionListener(e -> {
      game.setupServer();
    });

    JMenuItem joinServerItem = new JMenuItem("Join Server");
    joinServerItem.addActionListener(e -> {
      game.setupClient();
    });

    JMenuItem disconnectItem = new JMenuItem("Disconnect");
    disconnectItem.addActionListener(e -> {
      game.clientDisconnect();
    });

    JMenuItem stopServerItem = new JMenuItem("Stop Server");
    stopServerItem.addActionListener(e -> {
      game.stopServer();
    });

    multiplayerMenu.add(singlePlayerItem);
    multiplayerMenu.add(hostServerItem);
    multiplayerMenu.add(joinServerItem);
    multiplayerMenu.add(disconnectItem);
    multiplayerMenu.add(stopServerItem);

    this.add(multiplayerMenu);
  }
}
