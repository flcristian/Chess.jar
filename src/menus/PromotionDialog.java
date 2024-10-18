package menus;

import constants.Globals;
import enums.PieceColor;
import enums.PieceType;
import renderers.PieceRenderer;
import renderers.PieceRendererSingleton;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class PromotionDialog extends JDialog {
    private PieceType selectedPieceType = null;

    public PromotionDialog(JFrame parent, PieceColor color) {
        super(parent, "Promote Pawn", true);

        JPanel panel = getjPanel(color);
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int selectedX = e.getX() / Globals.SQUARE_SIZE;
                switch (selectedX) {
                    case 0 -> selectedPieceType = PieceType.ROOK;
                    case 1 -> selectedPieceType = PieceType.KNIGHT;
                    case 2 -> selectedPieceType = PieceType.BISHOP;
                    case 3 -> selectedPieceType = PieceType.QUEEN;
                    default -> throw new IllegalStateException("Unexpected value: " + selectedX);
                }
                dispose();
            }
        });

        add(panel);
        pack();
        setLocationRelativeTo(parent);
    }

    private static JPanel getjPanel(PieceColor color) {
        JPanel panel = new JPanel() {
            private final PieceRenderer pieceRenderer = PieceRendererSingleton.getInstance();

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;

                System.out.println("Current working directory: " + new File(".").getAbsolutePath());
                try {
                    BufferedImage texture = ImageIO.read(new File("src/assets/textures/wood.png"));

                    for(int i = 0; i < 4; i++) {
                        g2d.drawImage(texture, i * Globals.SQUARE_SIZE, 0, Globals.SQUARE_SIZE, Globals.SQUARE_SIZE, this);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    boolean colorSwitch = true;
                    for(int i = 0; i < 4; i++) {
                        g2d.setColor(colorSwitch ? Globals.BOARD_COLOR_WHITE : Globals.BOARD_COLOR_BLACK);
                        g2d.fillRect(i * Globals.SQUARE_SIZE, 0, Globals.SQUARE_SIZE, Globals.SQUARE_SIZE);
                        colorSwitch = !colorSwitch;
                    }
                }

                pieceRenderer.renderPiece(g2d, PieceType.ROOK, color, 0, 0);
                pieceRenderer.renderPiece(g2d, PieceType.KNIGHT, color, 1, 0);
                pieceRenderer.renderPiece(g2d, PieceType.BISHOP, color, 2, 0);
                pieceRenderer.renderPiece(g2d, PieceType.QUEEN, color, 3, 0);
            }
        };

        panel.setPreferredSize(new Dimension(Globals.SQUARE_SIZE * 4, Globals.SQUARE_SIZE));
        return panel;
    }

    public PieceType getSelectedPieceType() {
        return selectedPieceType;
    }
}
