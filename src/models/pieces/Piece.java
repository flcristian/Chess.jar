package models.pieces;

import enums.PieceColor;
import enums.PieceType;
import models.utils.Position;

public abstract class Piece {
    public PieceColor Color;
    public PieceType Type;
    public Position Position;

    public Piece(PieceColor color, PieceType type, int x, int y) {
        Color = color;
        Type = type;
        Position = new Position(x, y);
    }

    abstract boolean isValidMove(Position position);
}
