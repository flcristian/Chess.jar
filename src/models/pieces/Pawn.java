package models.pieces;

import enums.PieceColor;
import enums.PieceType;
import models.utils.Position;

public class Pawn extends Piece {
    public Pawn(PieceColor color, int x, int y) {
        super(color, PieceType.PAWN, x, y);
    }

    public Pawn(PieceColor color, Position position) {
        super(color, PieceType.PAWN, position);
    }

    @Override
    public boolean isValidMove(Position position) {
        if(Position.x != position.x) return false;
        if(Color.equals(PieceColor.BLACK)) {
            return (Position.y == 1 && position.y == Position.y + 2) || position.y == Position.y + 1;
        }
        return (Position.y == 6 && position.y == Position.y - 2) || position.y == Position.y - 1;
    }

    public boolean isValidTakeMove(Position position, Position enPassantTarget) {
        if (Math.abs(Position.x - position.x) != 1) {
            return false;
        }

        int direction = (Color.equals(PieceColor.BLACK)) ? 1 : -1;

        if (position.y == Position.y + direction) {
            return true;
        }

        return position.equals(enPassantTarget);
    }
}
