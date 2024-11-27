package windows;

import screens.MainMenuScreen;

import javax.swing.*;
import java.awt.*;

public class GameWindow extends JFrame {
    public GameWindow() {
        setTitle("Chess.jar");
        setUndecorated(true);
        setExtendedState(Frame.MAXIMIZED_BOTH);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(screenSize);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        add(new MainMenuScreen());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameWindow frame = new GameWindow();
            frame.setVisible(true);
        });
    }
}