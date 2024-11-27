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
        this.chessFont = new Font("Segoe UI Symbol", Font.PLAIN, Globals.SIZE_PIECE);
    }

    public void renderPiece(Graphics2D g2d, PieceType pieceType, PieceColor pieceColor, int boardX, int boardY) {
        int posX = Globals.SIZE_TILE / 2 + (boardX * Globals.SIZE_TILE);
        int posY = Globals.SIZE_TILE / 2 + (boardY * Globals.SIZE_TILE);
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

    public void renderPieceAtWindowCoordinates(Graphics2D g2d, PieceType pieceType, PieceColor pieceColor, int windowX, int windowY) {
        Font originalFont = g2d.getFont();
        Color originalColor = g2d.getColor();
        AffineTransform originalTransform = g2d.getTransform();
        Object originalAntialiasing = g2d.getRenderingHint(RenderingHints.KEY_ANTIALIASING);

        g2d.setFont(chessFont);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        char pieceChar = pieceType.getPieceChar();
        String pieceString = String.valueOf(pieceChar);

        FontRenderContext frc = g2d.getFontRenderContext();
        TextLayout textLayout = new TextLayout(pieceString, chessFont, frc);
        Rectangle2D visualBounds = textLayout.getBounds();

        double drawX = windowX - visualBounds.getCenterX();
        double drawY = windowY - visualBounds.getCenterY();

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