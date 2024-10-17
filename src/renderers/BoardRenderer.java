package renderers;

import constants.Globals;
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
    }
}
