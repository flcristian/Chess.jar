package models.pieces;

import enums.PieceColor;
import enums.PieceType;
import models.utils.Position;

public class Queen extends Piece {
    public Queen(PieceColor color, int x, int y) {
        super(color, PieceType.QUEEN, x, y);
    }

    @Override
    public boolean isValidMove(models.utils.Position position) {
        return false;
    }
}
