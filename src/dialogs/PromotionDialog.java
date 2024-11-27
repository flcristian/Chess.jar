package dialogs;

import constants.Globals;
import enums.PieceColor;
import enums.PieceType;
import renderers.PieceRenderer;
import renderers.PieceRendererSingleton;
import utils.ColorLogger;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class PromotionDialog {
    private final ColorLogger logger;

    public PromotionDialog() {
        logger = new ColorLogger(PromotionDialog.class);
    }

    public void showInGamePromotion(Graphics2D g2d, JPanel panel, PieceColor color) {
        int x = (Globals.SIZE_BOARD_PANEL - Globals.SIZE_TILE * 4) / 2;
        int y = (Globals.SIZE_BOARD_PANEL - Globals.SIZE_TILE) / 2;
        int width = Globals.SIZE_TILE * 4;
        int height = Globals.SIZE_TILE;
        int cornerRadius = 32;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

        Path2D roundedRect = new Path2D.Double();
        roundedRect.moveTo(x + cornerRadius, y);
        roundedRect.lineTo(x + width - cornerRadius, y);
        roundedRect.quadTo(x + width, y, x + width, y + cornerRadius);
        roundedRect.lineTo(x + width, y + height - cornerRadius);
        roundedRect.quadTo(x + width, y + height, x + width - cornerRadius, y + height);
        roundedRect.lineTo(x + cornerRadius, y + height);
        roundedRect.quadTo(x, y + height, x, y + height - cornerRadius);
        roundedRect.lineTo(x, y + cornerRadius);
        roundedRect.quadTo(x, y, x + cornerRadius, y);
        roundedRect.closePath();

        g2d.setStroke(new BasicStroke(8f));
        g2d.setColor(Color.BLACK);
        g2d.draw(roundedRect);

        g2d.clip(roundedRect);

        try {
            BufferedImage texture = ImageIO.read(new File(Globals.TEXTURE_WOOD_PATH));

            for(int i = 0; i < 4; i++) {
                g2d.drawImage(texture, x + i * Globals.SIZE_TILE, y,
                        Globals.SIZE_TILE, Globals.SIZE_TILE, panel);
            }
        } catch (IOException e) {
            logger.severe(e.getMessage());
            boolean colorSwitch = true;
            for(int i = 0; i < 4; i++) {
                g2d.setColor(colorSwitch ? Globals.COLOR_WHITE_TILE : Globals.COLOR_BLACK_TILE);
                g2d.fillRect(x + i * Globals.SIZE_TILE, y, Globals.SIZE_TILE, Globals.SIZE_TILE);
                colorSwitch = !colorSwitch;
            }
        }

        PieceRenderer pieceRenderer = PieceRendererSingleton.getInstance();
        pieceRenderer.renderPieceAtWindowCoordinates(g2d, PieceType.ROOK, color, x + Globals.SIZE_TILE / 2, y + Globals.SIZE_TILE / 2);
        pieceRenderer.renderPieceAtWindowCoordinates(g2d, PieceType.KNIGHT, color, x + Globals.SIZE_TILE + Globals.SIZE_TILE / 2, y + Globals.SIZE_TILE / 2);
        pieceRenderer.renderPieceAtWindowCoordinates(g2d, PieceType.BISHOP, color, x + Globals.SIZE_TILE * 2 + Globals.SIZE_TILE / 2, y + Globals.SIZE_TILE / 2);
        pieceRenderer.renderPieceAtWindowCoordinates(g2d, PieceType.QUEEN, color, x + Globals.SIZE_TILE * 3 + Globals.SIZE_TILE / 2, y + Globals.SIZE_TILE / 2);
    }
}