package enums;

import constants.Globals;

import java.awt.*;

public enum PieceColor {
    WHITE, BLACK;

    public Color getColor() {
        return this == WHITE ? Globals.COLOR_WHITE_PIECE : Globals.COLOR_BLACK_PIECE;
    }

    public Color getOutlineColor() {
        return this == BLACK ? Globals.COLOR_WHITE_PIECE : Globals.COLOR_BLACK_PIECE;
    }
}
