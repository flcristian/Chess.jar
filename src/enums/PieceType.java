package enums;

public enum PieceType {
    PAWN, KNIGHT, BISHOP, ROOK, QUEEN, KING;

    public char getPieceChar(PieceColor pieceColor) {
        boolean isWhite = pieceColor.equals(PieceColor.WHITE);
        return switch (this) {
            case KING -> '♚';
            case QUEEN -> '♛';
            case ROOK -> '♜';
            case BISHOP -> '♝';
            case KNIGHT -> '♞';
            case PAWN -> '♟';
        };
    }
}