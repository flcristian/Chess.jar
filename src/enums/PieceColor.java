package enums;

import constants.Globals;

import java.awt.*;
import java.io.Serializable;

public enum PieceColor {
    WHITE, BLACK;

    public Color getColor() {
        return this == WHITE ? Globals.PIECE_COLOR_WHITE : Globals.PIECE_COLOR_BLACK;
    }

    public Color getOutlineColor() {
        return this == BLACK ? Globals.PIECE_COLOR_WHITE : Globals.PIECE_COLOR_BLACK;
    }
}
