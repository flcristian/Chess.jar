package models.pieces;

import enums.PieceColor;
import enums.PieceType;

public class Pawn extends Piece {
    public Pawn(PieceColor color, int x, int y) {
        super(color, PieceType.PAWN, x, y);
    }

    @Override
    public boolean isValidMove(models.utils.Position position) {
        return false;
    }
}
