package server;

import enums.PieceType;

import java.io.Serializable;

public record PromotionUpdate(
        PieceType promotedPieceType
) implements Serializable {
}
