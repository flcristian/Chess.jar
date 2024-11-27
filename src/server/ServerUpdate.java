package server;

import enums.PieceColor;
import models.pieces.Piece;
import models.utils.Position;

import java.io.Serializable;
import java.util.List;

public record ServerUpdate(
        List<Piece> PieceList,
        Piece SelectedPiece,
        List<Position> PossibleMoves,
        PieceColor ClientColor,
        PieceColor TurnColor,
        PieceColor PromotionColor
) implements Serializable {
}