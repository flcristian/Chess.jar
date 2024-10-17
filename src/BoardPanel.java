import controllers.PieceController;
import controllers.PieceControllerSingleton;
import models.pieces.Piece;
import renderers.BoardRenderer;
import renderers.PieceRenderer;

import javax.swing.*;
import java.awt.*;

public class BoardPanel extends JPanel {
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        BoardRenderer boardRenderer = new BoardRenderer(g2d);
        boardRenderer.RenderBoard();
        PieceRenderer pieceRenderer = new PieceRenderer(g2d);

        PieceController pieceController = PieceControllerSingleton.getInstance();
        for(Piece piece : pieceController.getPieceList()) {
            pieceRenderer.renderPiece(piece.Type, piece.Color, piece.Position.x, piece.Position.y);
        }
    }
}
