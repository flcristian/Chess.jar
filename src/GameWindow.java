import constants.Globals;
import controllers.PieceController;
import controllers.PieceControllerSingleton;

import javax.swing.*;

public class GameWindow {
    public void RenderWindow(){
        PieceController pieceController = PieceControllerSingleton.getInstance();
        pieceController.initializePieces();

        JFrame frame = new JFrame("Chess.jar");
        BoardPanel gamePanel = new BoardPanel();

        frame.setContentPane(gamePanel);

        frame.setSize(Globals.WINDOW_SIZE, Globals.WINDOW_SIZE);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setVisible(true);
    }
}