package models.pieces;

import enums.PieceColor;
import enums.PieceType;
import models.utils.Position;

public abstract class Piece implements Cloneable {
    public PieceColor Color;
    public PieceType Type;
    public Position Position;

    public Piece(PieceColor color, PieceType type, int x, int y) {
        Color = color;
        Type = type;
        Position = new Position(x, y);
    }

    public Piece(PieceColor color, PieceType type, Position position) {
        Color = color;
        Type = type;
        Position = position;
    }

    public abstract boolean isValidMove(Position position);

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Piece p) {
            return p.Position.equals(Position) && p.Color.equals(Color) && p.Type.equals(Type);
        }
        return false;
    }

    @Override
    public Piece clone() {
        try {
            Piece cloned = (Piece) super.clone();
            cloned.Position = new Position(Position.x, Position.y);
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    @Override
    public String toString() {
        return Type + " " + Position;
    }
}
