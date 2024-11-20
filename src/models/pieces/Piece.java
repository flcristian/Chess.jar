package models.pieces;

import enums.PieceColor;
import enums.PieceType;
import models.utils.Position;

import java.io.Serializable;

public abstract class Piece implements Cloneable, Serializable {
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
        Position = new Position(position.x, position.y);
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
        return switch (Type) {
            case KING -> new King(Color, Position);
            case QUEEN -> new Queen(Color, Position);
            case BISHOP -> new Bishop(Color, Position);
            case KNIGHT -> new Knight(Color, Position);
            case ROOK -> new Rook(Color, Position);
            case PAWN -> new Pawn(Color, Position);
        };
    }

    @Override
    public String toString() {
        return Type + " " + Position;
    }
}
