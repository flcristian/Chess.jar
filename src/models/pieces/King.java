package models.pieces;

import enums.PieceColor;
import enums.PieceType;
import models.utils.Position;

public class King extends Piece {
    public King(PieceColor color, int x, int y) {
        super(color, PieceType.KING, x, y);
    }

    @Override
    public boolean isValidMove(models.utils.Position position) {
        return false;
    }
}
