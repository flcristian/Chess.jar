package windows;

import constants.Globals;
import controllers.PieceController;
import controllers.PieceControllerSingleton;
import enums.PieceColor;
import models.utils.Position;
import panels.BoardPanelSingleton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GameWindow {
    public static JFrame Window;

    public GameWindow() {
        Window = new JFrame("Chess.jar");
    }

    private PieceController pieceController;

    public void RenderWindow() {
        JPanel gamePanel;
        pieceController = PieceControllerSingleton.getInstance();
        gamePanel = BoardPanelSingleton.getInstance();

        Window.setContentPane(gamePanel);

        gamePanel.setPreferredSize(new Dimension(Globals.WINDOW_SIZE, Globals.WINDOW_SIZE));
        Window.pack();

        Window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        gamePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int x = e.getX() / 112;
                int y = e.getY() / 112;
                pieceController.tryMovePiece(new Position(x, y));
            }
        });

        pieceController.addMovingPieceChangeListener(newMovingPiece -> {
            PieceColor loserColor = pieceController.detectCheckmate();
            if(loserColor != null) {
                System.out.println((loserColor.equals(PieceColor.BLACK) ? "White" : "Black") + " won!");
            }

            if(pieceController.detectStalemate() != null) {
                System.out.println("It's a stalemate!");
            }

            gamePanel.repaint();
        });

        Window.setVisible(true);
    }
}