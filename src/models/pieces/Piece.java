package models.pieces;

import enums.PieceColor;
import enums.PieceType;
import models.utils.Position;

import java.io.Serializable;

public abstract class Piece implements Cloneable, Serializable {
    public final PieceColor Color;
    public final PieceType Type;
    public Position Position;

    protected Piece(PieceColor color, PieceType type, int x, int y) {
        Color = color;
        Type = type;
        Position = new Position(x, y);
    }

    protected Piece(PieceColor color, PieceType type, Position position) {
        Color = color;
        Type = type;
        Position = new Position(position.x(), position.y());
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
    public int hashCode() {
        return Position.hashCode() + Color.hashCode() + Type.hashCode();
    }

    public abstract Piece clone();

    @Override
    public String toString() {
        return Type + " " + Position;
    }
}
