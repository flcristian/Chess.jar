package models.pieces;

import enums.PieceColor;
import enums.PieceType;
import models.utils.Position;

public class Knight extends Piece {
    public Knight(PieceColor color, int x, int y) {
        super(color, PieceType.KNIGHT, x, y);
    }

    @Override
    public boolean isValidMove(models.utils.Position position) {
        return false;
    }
}
