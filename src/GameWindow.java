import constants.Globals;
import controllers.PieceController;
import controllers.PieceControllerSingleton;
import enums.PieceColor;
import models.utils.Position;
import models.pieces.Piece;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GameWindow {
    private BoardPanel gamePanel;
    private PieceController pieceController;

    public void RenderWindow() {
        pieceController = PieceControllerSingleton.getInstance();

        JFrame frame = new JFrame("Chess.jar");
        gamePanel = new BoardPanel();

        frame.setContentPane(gamePanel);

        gamePanel.setPreferredSize(new Dimension(Globals.WINDOW_SIZE, Globals.WINDOW_SIZE));
        frame.pack();

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        gamePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int x = e.getX() / 112;
                int y = e.getY() / 112;
                pieceController.tryMovePiece(new Position(x, y));
            }
        });

        pieceController.addMovingPieceChangeListener(newMovingPiece -> gamePanel.repaint());

        frame.setVisible(true);
    }
}