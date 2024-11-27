package screens;

import constants.Globals;
import panels.BoardPanel;
import panels.BoardPanelSingleton;
import utils.ColorLogger;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class LocalGameScreen extends JPanel {
    private final ColorLogger logger;

    public LocalGameScreen() {
        logger = new ColorLogger(MainMenuScreen.class);

        Font customFont = loadCustomFont();
        BufferedImage backgroundTexture = loadBackgroundTexture();

        setLayout(new BorderLayout());

        JPanel mainPanel = getjPanel(backgroundTexture);
        BoardPanel boardPanel = BoardPanelSingleton.getInstance();

        boardPanel.setPreferredSize(new Dimension(Globals.SIZE_BOARD_PANEL, Globals.SIZE_BOARD_PANEL));

        JPanel boardWrapper = getBoardPanelWrapper();
        boardWrapper.add(boardPanel);

        JPanel spacingPanel = new JPanel(new GridBagLayout());
        spacingPanel.setOpaque(false);
        spacingPanel.add(boardWrapper);

        mainPanel.add(spacingPanel, BorderLayout.CENTER);

        add(mainPanel);
    }

    private JPanel getBoardPanelWrapper() {
        JPanel boardWrapper = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2d.dispose();
            }

            @Override
            public Dimension getPreferredSize() {
                Dimension size = super.getPreferredSize();
                return new Dimension(size.width, size.height);
            }
        };

        boardWrapper.setOpaque(false);
        boardWrapper.setBorder(new LineBorder(Globals.COLOR_DARK_GRAY, 10, true));
        return boardWrapper;
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
