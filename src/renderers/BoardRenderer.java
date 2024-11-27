package renderers;

import constants.Globals;
import controllers.PieceController;
import controllers.PieceControllerSingleton;
import models.utils.Position;
import panels.BoardPanelSingleton;

import java.awt.*;

public class BoardRenderer {
    public void renderBoard() {
        Graphics2D g2d = BoardPanelSingleton.getInstance().G2D;

        boolean colorSwitcher = true;
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                g2d.setColor(colorSwitcher ? Globals.COLOR_WHITE_TILE : Globals.COLOR_BLACK_TILE);
                g2d.fillRect(i * Globals.SIZE_TILE, j * Globals.SIZE_TILE, Globals.SIZE_TILE, Globals.SIZE_TILE);
                colorSwitcher = !colorSwitcher;
            }
            colorSwitcher = !colorSwitcher;
        }

        PieceController pieceController = PieceControllerSingleton.getInstance();
        if(pieceController.getTurnColor() == pieceController.getClientColor()) {
            for(Position position : pieceController.PossibleMoves) {
                g2d.setColor(new Color(0, 255, 0, (int)(0.3 * 255)));
                g2d.fillRect(position.x() * Globals.SIZE_TILE, position.y() * Globals.SIZE_TILE, Globals.SIZE_TILE, Globals.SIZE_TILE);
            }
        }
    }
}
