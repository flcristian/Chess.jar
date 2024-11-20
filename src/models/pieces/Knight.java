package models.pieces;

import enums.PieceColor;
import enums.PieceType;
import models.utils.Position;

public class Knight extends Piece {
    public Knight(PieceColor color, int x, int y) {
        super(color, PieceType.KNIGHT, x, y);
    }

    public Knight(PieceColor color, Position position) {
        super(color, PieceType.KNIGHT, position);
    }

    @Override
    public boolean isValidMove(Position position) {
        return position.x() >= 0 && position.x() < 8 && position.y() >= 0 && position.y() < 8 &&
                ((Math.abs(Position.x() - position.x()) == 2 && Math.abs(Position.y() - position.y()) == 1) ||
                        (Math.abs(Position.x() - position.x()) == 1 && Math.abs(Position.y() - position.y()) == 2));
    }

    @Override
    public Piece clone() {
        return new Knight(Color, Position);
    }
}