package screens;

import constants.Globals;
import utils.ColorLogger;
import utils.ComponentCreator;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class MainMenuScreen extends JPanel {
    private final ColorLogger logger;

    public MainMenuScreen() {
        logger = new ColorLogger(MainMenuScreen.class);

        Font customFont = loadCustomFont();
        BufferedImage backgroundTexture = loadBackgroundTexture();

        setLayout(new BorderLayout());

        JPanel mainPanel = getjPanel(backgroundTexture);

        JLabel titleLabel = ComponentCreator.createShadowedLabel("Chess.jar", customFont.deriveFont(Font.BOLD, 64f), Globals.COLOR_LIGHT_GOLD);

        JPanel titleWrapper = new JPanel(new BorderLayout());
        titleWrapper.setOpaque(false);
        titleWrapper.add(titleLabel, BorderLayout.CENTER);
        titleWrapper.setBorder(BorderFactory.createEmptyBorder(250, 0, 0, 0));

        mainPanel.add(titleWrapper, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(50, 500, 300, 500));

        JButton localButton = ComponentCreator.createStyledButton("Local Game", Globals.COLOR_DARK_GRAY, customFont);
        localButton.addActionListener(e -> openLocalGame());
        buttonPanel.add(localButton);

        JButton serverButton = ComponentCreator.createStyledButton("Server List", Globals.COLOR_DARK_GRAY, customFont);
        serverButton.addActionListener(e -> openServerList());
        buttonPanel.add(serverButton);

        JButton exitButton = ComponentCreator.createStyledButton("Exit", Globals.COLOR_DARK_GRAY, customFont);
        exitButton.addActionListener(e -> closeGame());
        buttonPanel.add(exitButton);

        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        add(mainPanel);
    }

    private static JPanel getjPanel(BufferedImage woodTexture) {
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                if (woodTexture != null) {
                    Graphics2D g2d = (Graphics2D) g;
                    int textureWidth = woodTexture.getWidth();
                    int textureHeight = woodTexture.getHeight();

                    for (int y = 0; y < getHeight(); y += textureHeight) {
                        for (int x = 0; x < getWidth(); x += textureWidth) {
                            g2d.drawImage(woodTexture, x, y, null);
                        }
                    }
                } else {
                    g.setColor(new Color(240, 230, 210));
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        mainPanel.setLayout(new BorderLayout());
        return mainPanel;
    }

    private Font loadCustomFont() {
        try {
            return Font.createFont(Font.TRUETYPE_FONT, new File(Globals.FONT_PATH));
        } catch (FontFormatException | IOException e) {
            return new JLabel().getFont();
        }
    }

    private BufferedImage loadBackgroundTexture() {
        try {
            return ImageIO.read(new File(Globals.TEXTURE_WOOD_PATH));
        } catch (IOException e) {
            logger.severe(e.getMessage());
            return null;
        }
    }

    private void openLocalGame() {
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (topFrame != null) {
            topFrame.getContentPane().removeAll();
            topFrame.getContentPane().add(new LocalGameScreen());
            topFrame.revalidate();
            topFrame.repaint();
        }
    }

    private void openServerList() {
    }

    private void closeGame() {
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (topFrame != null) {
            topFrame.dispose();
            System.exit(0);
        }
    }
}