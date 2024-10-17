import constants.Globals;
import controllers.PieceController;
import controllers.PieceControllerSingleton;
import models.utils.Position;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

public class GameWindow {
    public void RenderWindow(){
        PieceController pieceController = PieceControllerSingleton.getInstance();

        JFrame frame = new JFrame("Chess.jar");
        BoardPanel gamePanel = new BoardPanel();

        frame.setContentPane(gamePanel);

        gamePanel.setPreferredSize(new Dimension(Globals.WINDOW_SIZE, Globals.WINDOW_SIZE));
        frame.pack();

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        gamePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int x = e.getX() / 112;
                int y = e.getY() / 112;
                if(pieceController.tryMovePiece(new Position(x, y))) {
                    gamePanel.repaint();
                }
            }
        });

        frame.setVisible(true);
    }
}