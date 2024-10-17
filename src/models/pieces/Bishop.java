package models.pieces;

import enums.PieceColor;
import enums.PieceType;
import models.utils.Position;

public class Bishop extends Piece {
    public Bishop(PieceColor color, int x, int y) {
        super(color, PieceType.BISHOP, x, y);
    }

    @Override
    public boolean isValidMove(Position position) {
        return !Position.equals(position) && position.x >= 0 && position.x < 8 && position.y >= 0 && position.y < 8 &&
                Math.abs(Position.x - position.x) == Math.abs(Position.y - position.y);
    }
}
