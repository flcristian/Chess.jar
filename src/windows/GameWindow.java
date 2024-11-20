package windows;

import constants.Globals;
import controllers.PieceController;
import controllers.PieceControllerSingleton;
import enums.PieceColor;
import models.utils.Position;
import panels.BoardPanelSingleton;
import utils.ColorLogger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GameWindow {
    private final ColorLogger logger;

    public static final JFrame WINDOW = new JFrame("Chess.jar");

    public GameWindow() {
        logger = new ColorLogger(GameWindow.class);
    }

    public void RenderWindow() {
        JPanel gamePanel;
        PieceController pieceController = PieceControllerSingleton.getInstance();
        gamePanel = BoardPanelSingleton.getInstance();

        WINDOW.setContentPane(gamePanel);

        WINDOW.setSize(Globals.WINDOW_SIZE, Globals.WINDOW_SIZE + 30);

        WINDOW.setResizable(false);

        WINDOW.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        gamePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int x = e.getX() / Globals.SQUARE_SIZE;
                int y = e.getY() / Globals.SQUARE_SIZE;
                pieceController.tryMovePiece(new Position(x, y));
            }
        });

        pieceController.addMovingPieceChangeListener(newMovingPiece -> {
            pieceController.setClientColor(pieceController.getTurnColor());
            PieceColor loserColor = pieceController.detectCheckmate();
            if(loserColor != null) {
                logger.info((loserColor.equals(PieceColor.BLACK) ? "White" : "Black") + " won!");
            }

            if(pieceController.detectStalemate() != null) {
                logger.info("It's a stalemate!");
            }

            gamePanel.repaint();
        });

        WINDOW.setVisible(true);
    }
}