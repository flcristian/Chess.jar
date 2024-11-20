package models.pieces;

import enums.PieceColor;
import enums.PieceType;
import models.utils.Position;

public class Rook extends Piece {
    public Rook(PieceColor color, int x, int y) {
        super(color, PieceType.ROOK, x, y);
    }

    public Rook(PieceColor color, Position position) {
        super(color, PieceType.ROOK, position);
    }

    @Override
    public boolean isValidMove(Position position) {
        return !Position.equals(position) && position.x >= 0 && position.x < 8 && position.y >= 0 && position.y < 8 &&
                (Position.x == position.x || Position.y == position.y);
    }
}
