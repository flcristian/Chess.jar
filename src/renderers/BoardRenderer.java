package renderers;

import constants.Globals;
import controllers.PieceController;
import controllers.PieceControllerSingleton;
import models.utils.Position;

import java.awt.*;

public class BoardRenderer {
    private final Graphics2D g2d;

    public BoardRenderer(Graphics2D g2d) {
        this.g2d = g2d;
    }

    public void RenderBoard() {
        boolean colorSwitcher = true;
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                g2d.setColor(colorSwitcher ? Globals.BOARD_COLOR_WHITE : Globals.BOARD_COLOR_BLACK);
                g2d.fillRect(i * Globals.SQUARE_SIZE, j * Globals.SQUARE_SIZE, Globals.SQUARE_SIZE, Globals.SQUARE_SIZE);
                colorSwitcher = !colorSwitcher;
            }
            colorSwitcher = !colorSwitcher;
        }

        PieceController pieceController = PieceControllerSingleton.getInstance();
        for(Position position : pieceController.PossibleMoves) {
            g2d.setColor(new Color(0, 255, 0, (int)(0.3 * 255)));
            g2d.fillRect(position.x * Globals.SQUARE_SIZE, position.y * Globals.SQUARE_SIZE, Globals.SQUARE_SIZE, Globals.SQUARE_SIZE);
        }
    }
}
