package renderers;

import constants.Globals;
import enums.PieceColor;
import enums.PieceType;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

public class PieceRenderer {
    private final Font chessFont;

    public PieceRenderer() {
        this.chessFont = new Font("Segoe UI Symbol", Font.PLAIN, Globals.PIECE_SIZE);
    }

    public void renderPiece(Graphics2D g2d, PieceType pieceType, PieceColor pieceColor, int boardX, int boardY) {
        int posX = Globals.SQUARE_SIZE / 2 + (boardX * Globals.SQUARE_SIZE);
        int posY = Globals.SQUARE_SIZE / 2 + (boardY * Globals.SQUARE_SIZE);
        char pieceChar = pieceType.getPieceChar();
        String pieceString = String.valueOf(pieceChar);

        Font originalFont = g2d.getFont();
        Color originalColor = g2d.getColor();
        AffineTransform originalTransform = g2d.getTransform();
        Object originalAntialiasing = g2d.getRenderingHint(RenderingHints.KEY_ANTIALIASING);

        g2d.setFont(chessFont);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        FontRenderContext frc = g2d.getFontRenderContext();
        TextLayout textLayout = new TextLayout(pieceString, chessFont, frc);
        Rectangle2D visualBounds = textLayout.getBounds();

        double drawX = posX - visualBounds.getCenterX();
        double drawY = posY - visualBounds.getCenterY();

        Shape outline = textLayout.getOutline(AffineTransform.getTranslateInstance(drawX, drawY));

        Color outlineColor = pieceColor.getOutlineColor();
        g2d.setColor(outlineColor);
        g2d.setStroke(new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.draw(outline);

        g2d.setColor(pieceColor.getColor());
        g2d.fill(outline);

        g2d.setFont(originalFont);
        g2d.setColor(originalColor);
        g2d.setTransform(originalTransform);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, originalAntialiasing);
    }
}