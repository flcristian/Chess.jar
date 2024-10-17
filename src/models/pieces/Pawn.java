package models.pieces;

import enums.PieceColor;
import enums.PieceType;
import models.utils.Position;

public class Pawn extends Piece {
    public Pawn(PieceColor color, int x, int y) {
        super(color, PieceType.PAWN, x, y);
    }

    @Override
    public boolean isValidMove(Position position) {
        if(Position.x != position.x) return false;
        if(Color.equals(PieceColor.BLACK)) {
            return (Position.y == 1 && position.y == Position.y + 2) || position.y == Position.y + 1;
        }
        return (Position.y == 6 && position.y == Position.y - 2) || position.y == Position.y - 1;
    }

    public boolean isValidTakeMove(Position position) {
        if(Math.abs(Position.x - position.x) != 1) {
            return false;
        }
        if(Color.equals(PieceColor.BLACK)) {
            return position.y == Position.y + 1;
        }
        return position.y == Position.y - 1 ;
    }
}
