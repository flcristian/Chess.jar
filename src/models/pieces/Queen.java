package models.pieces;

import enums.PieceColor;
import enums.PieceType;
import models.utils.Position;

public class Queen extends Piece {
    public Queen(PieceColor color, int x, int y) {
        super(color, PieceType.QUEEN, x, y);
    }

    public Queen(PieceColor color, Position position) {
        super(color, PieceType.QUEEN, position);
    }

    @Override
    public boolean isValidMove(Position position) {
        return !Position.equals(position) && position.x >= 0 && position.x < 8 && position.y >= 0 && position.y < 8 &&
                (Position.x == position.x || Position.y == position.y ||  Math.abs(Position.x - position.x) == Math.abs(Position.y - position.y));
    }
}
