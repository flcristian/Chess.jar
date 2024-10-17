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

    public abstract boolean isValidMove(Position position);

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Piece p) {
            return p.Position.equals(Position) && p.Color.equals(Color) && p.Type.equals(Type);
        }
        return false;
    }
}
