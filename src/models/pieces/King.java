package models.pieces;

import enums.PieceColor;
import enums.PieceType;
import models.utils.Position;

public class King extends Piece {
    public King(PieceColor color, int x, int y) {
        super(color, PieceType.KING, x, y);
    }

    public King(PieceColor color, Position position) { super(color, PieceType.KING, position); }

    @Override
    public boolean isValidMove(Position position) {
        if (!isWithinBoard(position) || Position.equals(position)) {
            return false;
        }

        int deltaX = Math.abs(Position.x() - position.x());
        int deltaY = Math.abs(Position.y() - position.y());

        if (deltaX <= 1 && deltaY <= 1) {
            return true;
        }

        return deltaY == 0 && deltaX == 2 &&
                ((Color == PieceColor.WHITE && Position.y() == 7 && Position.x() == 4) ||
                        (Color == PieceColor.BLACK && Position.y() == 0 && Position.x() == 4));
    }

    private boolean isWithinBoard(Position position) {
        return position.x() >= 0 && position.x() < 8 && position.y() >= 0 && position.y() < 8;
    }

    @Override
    public Piece clone() {
        return new King(Color, Position);
    }
}
