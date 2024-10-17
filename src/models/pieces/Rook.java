package models.pieces;

import enums.PieceColor;
import enums.PieceType;

public class Rook extends Piece {
    public Rook(PieceColor color, int x, int y) {
        super(color, PieceType.ROOK, x, y);
    }

    @Override
    public boolean isValidMove(models.utils.Position position) {
        return false;
    }
}
