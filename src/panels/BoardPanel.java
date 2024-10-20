package panels;

import controllers.PieceController;
import controllers.PieceControllerSingleton;
import models.pieces.Piece;
import renderers.BoardRenderer;
import renderers.BoardRendererSingleton;
import renderers.PieceRenderer;
import renderers.PieceRendererSingleton;

import javax.swing.*;
import java.awt.*;

public class BoardPanel extends JPanel {
    public Graphics2D G2D;

    BoardPanel() {}

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        G2D = (Graphics2D) g;

        BoardRenderer boardRenderer = BoardRendererSingleton.getInstance();
        boardRenderer.RenderBoard();
        PieceRenderer pieceRenderer = PieceRendererSingleton.getInstance();

        PieceController pieceController = PieceControllerSingleton.getInstance();
        for(Piece piece : pieceController.getPieceList()) {
            pieceRenderer.renderPiece(G2D, piece.Type, piece.Color, piece.Position.x, piece.Position.y);
        }
    }
}
