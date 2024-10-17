package models.pieces;

import enums.PieceColor;
import enums.PieceType;
import models.utils.Position;

public class Bishop extends Piece {
    public Bishop(PieceColor color, int x, int y) {
        super(color, PieceType.BISHOP, x, y);
    }

    @Override
    public boolean isValidMove(models.utils.Position position) {
        return false;
    }
}
