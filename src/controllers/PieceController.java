package controllers;

import enums.PieceColor;
import enums.PieceType;
import menus.PromotionDialog;
import models.pieces.*;
import models.utils.Position;
import panels.BoardPanelSingleton;
import utils.ColorLogger;
import windows.GameWindow;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.IntStream;

public class PieceController {
    private final ColorLogger logger = new ColorLogger(PieceController.class);

    private List<Piece> pieceList;
    private List<Piece> hasMoved;
    private Piece movingPiece;
    private PieceColor turnColor;
    private List<MovingPieceChangeListener> movingPieceChangeListeners;
    private Position enPassantTarget;

    public List<Position> possibleMoves;

    public PieceController() {
        initializePieceController();
    }

    // LISTENER LOGIC

    public interface MovingPieceChangeListener {
        void onMovingPieceChanged(Piece newMovingPiece);
    }

    public void addMovingPieceChangeListener(MovingPieceChangeListener listener) {
        movingPieceChangeListeners.add(listener);
    }

    public void removeMovingPieceChangeListener(MovingPieceChangeListener listener) {
        movingPieceChangeListeners.remove(listener);
    }

    private void notifyMovingPieceChangeListeners() {
        movingPieceChangeListeners.forEach(listener -> listener.onMovingPieceChanged(movingPiece));
    }

    // ACCESSORS

    public List<Piece> getPieceList() {
        return pieceList;
    }

    // PUBLIC METHODS

    public void tryMovePiece(Position position) {
        Optional<Piece> targetPiece = getPieceAtPosition(position);

        if (movingPiece == null) {
            handlePieceSelection(targetPiece);
        } else {
            handlePieceMovement(position, targetPiece);
        }
    }

    public PieceColor detectCheckmate() {
        for (PieceColor kingColor : PieceColor.values()) {
            if (isKingInCheckmate(kingColor)) {
                return kingColor;
            }
        }
        return null;
    }

    public PieceColor detectStalemate() {
        for (PieceColor color : PieceColor.values()) {
            if (isStalemate(color)) {
                return color;
            }
        }
        return null;
    }

    // HANDLERS

    private void handlePieceSelection(Optional<Piece> targetPiece) {
        targetPiece.ifPresent(piece -> {
            if (piece.Color.equals(turnColor)) {
                movingPiece = piece;
                notifyMovingPieceChangeListeners();
                calculatePossibleMoves(movingPiece);
                if (possibleMoves.isEmpty()) {
                    movingPiece = null;
                    logger.warning("This piece can't move.");
                } else {
                    logger.info("New Moving Piece: " + movingPiece);
                }
            } else {
                logger.warning("Wrong Piece Color.");
            }
        });
    }

    private void handlePieceMovement(Position position, Optional<Piece> targetPiece) {
        if (targetPiece.isPresent() && movingPiece.Color.equals(targetPiece.get().Color)) {
            cancelMovement();
        } else if (isValidMove(movingPiece, position)) {
            if (simulateMove(movingPiece, position)) {
                if (isEnPassantCapture(movingPiece, position)) {
                    capturePieceAtPosition(new Position(position.x, movingPiece.Position.y));
                } else {
                    targetPiece.ifPresent(this::capturePiece);
                }

                if (isCastlingMove(movingPiece, position)) {
                    performCastling(movingPiece, position);
                }

                updateEnPassantTarget(movingPiece, position);
                movePiece(movingPiece, position);

                if (!hasMoved.contains(movingPiece)) {
                    hasMoved.add(movingPiece);
                }
            } else {
                logger.warning("This move doesn't resolve the check.");
            }
        } else {
            logger.warning("Invalid move or path obstructed.");
            cancelMovement();
        }
    }

    private void handlePawnPromotion(Pawn pawn, Position newPosition) {
        boolean isAtEndRow = (pawn.Color == PieceColor.WHITE && newPosition.y == 0) ||
                (pawn.Color == PieceColor.BLACK && newPosition.y == 7);

        if (isAtEndRow) {
            logger.info(pawn + " can promote.");

            PromotionDialog dialog = new PromotionDialog(GameWindow.Window, pawn.Color);
            dialog.setVisible(true);

            PieceType selectedType = dialog.getSelectedPieceType();
            if (selectedType != null) {
                Piece newPiece = getPromotionPiece(pawn, newPosition, selectedType);

                pieceList.remove(pawn);
                pieceList.add(newPiece);

                notifyMovingPieceChangeListeners();
            }
        }
    }

    // PRIVATE METHODS

    private static Piece getPromotionPiece(Pawn pawn, Position newPosition, PieceType selectedType) {
        Piece newPiece;
        switch (selectedType) {
            case QUEEN -> newPiece = new Queen(pawn.Color, newPosition.x, newPosition.y);
            case ROOK -> newPiece = new Rook(pawn.Color, newPosition.x, newPosition.y);
            case BISHOP -> newPiece = new Bishop(pawn.Color, newPosition.x, newPosition.y);
            case KNIGHT -> newPiece = new Knight(pawn.Color, newPosition.x, newPosition.y);
            default -> throw new IllegalStateException("Unexpected piece type: " + selectedType);
        }
        return newPiece;
    }

    private boolean isCastlingMove(Piece piece, Position newPosition) {
        if (!(piece instanceof King) || hasMoved.contains(piece)) {
            return false;
        }

        int deltaX = newPosition.x - piece.Position.x;
        return Math.abs(deltaX) == 2 && newPosition.y == piece.Position.y;
    }

    private boolean isValidMove(Piece piece, Position targetPosition) {
        Optional<Piece> targetPiece = getPieceAtPosition(targetPosition);

        if (targetPiece.isPresent() && targetPiece.get().Color == piece.Color) {
            return false;
        }

        boolean isValidPieceMove;
        if (piece instanceof King) {
            boolean isNormalKingMove = piece.isValidMove(targetPosition);

            if (!isNormalKingMove && !hasMoved.contains(piece)) {
                isValidPieceMove = isValidCastling(piece, targetPosition);
            } else {
                isValidPieceMove = isNormalKingMove;
            }
        } else if (piece instanceof Pawn pawn) {
            boolean isCapture = targetPiece.isPresent() || isEnPassantCapture(piece, targetPosition);
            if (isCapture) {
                isValidPieceMove = pawn.isValidTakeMove(targetPosition, enPassantTarget);
            } else {
                isValidPieceMove = pawn.isValidMove(targetPosition);
            }
        } else {
            isValidPieceMove = piece.isValidMove(targetPosition);
        }

        return isValidPieceMove && isPathClear(piece, targetPosition) && !moveExposesKing(piece, targetPosition);
    }

    private boolean isValidCastling(Piece king, Position targetPosition) {
        if (!(king instanceof King) || hasMoved.contains(king)) {
            return false;
        }

        boolean isWhiteKing = king.Color == PieceColor.WHITE && king.Position.y == 7 && king.Position.x == 4;
        boolean isBlackKing = king.Color == PieceColor.BLACK && king.Position.y == 0 && king.Position.x == 4;
        if (!isWhiteKing && !isBlackKing) {
            return false;
        }

        if (isKingInCheck(king.Color)) {
            return false;
        }

        int dx = targetPosition.x - king.Position.x;
        if (Math.abs(dx) != 2 || targetPosition.y != king.Position.y) {
            return false;
        }

        int rookX = dx > 0 ? 7 : 0;
        Optional<Piece> rook = getPieceAtPosition(new Position(rookX, king.Position.y));

        if (rook.isEmpty() || !(rook.get() instanceof Rook) || hasMoved.contains(rook.get())) {
            return false;
        }

        int direction = dx > 0 ? 1 : -1;
        for (int x = king.Position.x + direction; x != rookX; x += direction) {
            if (getPieceAtPosition(new Position(x, king.Position.y)).isPresent()) {
                return false;
            }
        }

        Position intermediatePosition = new Position(king.Position.x + direction, king.Position.y);
        Position originalPosition = king.Position;

        king.Position = intermediatePosition;
        boolean passesThroughCheck = isKingInCheck(king.Color);
        king.Position = originalPosition;

        return !passesThroughCheck;
    }

    private void performCastling(Piece king, Position newPosition) {
        int rookX = newPosition.x > king.Position.x ? 7 : 0;
        int newRookX = newPosition.x > king.Position.x ? newPosition.x - 1 : newPosition.x + 1;

        Optional<Piece> rook = getPieceAtPosition(new Position(rookX, king.Position.y));
        if (rook.isPresent() && rook.get() instanceof Rook) {
            rook.get().Position = new Position(newRookX, king.Position.y);
            hasMoved.add(rook.get());
        }
    }

    private void updateEnPassantTarget(Piece piece, Position newPosition) {
        enPassantTarget = null;
        if (piece.Type.equals(PieceType.PAWN)) {
            int startRow = piece.Color == PieceColor.WHITE ? 6 : 1;
            if (piece.Position.y == startRow && Math.abs(newPosition.y - startRow) == 2) {
                enPassantTarget = new Position(newPosition.x, (newPosition.y + piece.Position.y) / 2);
            }
        }
        logger.info("En Passant Target: " + enPassantTarget);
    }

    private void cancelMovement() {
        movingPiece = null;
        possibleMoves.clear();
        notifyMovingPieceChangeListeners();
        logger.info("Cancelled movement.");
    }

    private void capturePiece(Piece capturedPiece) {
        pieceList.remove(capturedPiece);
        logger.info(capturedPiece.Color + " " + capturedPiece.Type + " captured.");
    }

    private void movePiece(Piece piece, Position position) {
        piece.Position = position;
        if (piece instanceof Pawn pawn) {
            handlePawnPromotion(pawn, position);
        }
        movingPiece = null;
        possibleMoves.clear();
        notifyMovingPieceChangeListeners();
        turnColor = turnColor.equals(PieceColor.BLACK) ? PieceColor.WHITE : PieceColor.BLACK;
    }

    private void capturePieceAtPosition(Position position) {
        getPieceAtPosition(position).ifPresent(this::capturePiece);
    }

    private boolean isPathClear(Piece piece, Position targetPosition) {
        if (piece instanceof Knight) return true;

        int dx = Integer.compare(targetPosition.x, piece.Position.x);
        int dy = Integer.compare(targetPosition.y, piece.Position.y);
        Position checkedPosition = new Position(piece.Position.x + dx, piece.Position.y + dy);

        while (!checkedPosition.equals(targetPosition)) {
            if (getPieceAtPosition(checkedPosition).isPresent()) return false;
            checkedPosition = new Position(checkedPosition.x + dx, checkedPosition.y + dy);
        }

        return true;
    }

    private boolean simulateMove(Piece piece, Position newPosition) {
        Position originalPosition = piece.Position;
        Optional<Piece> capturedPiece = getPieceAtPosition(newPosition);

        if (capturedPiece.isPresent() && capturedPiece.get() instanceof King) return false;

        capturedPiece.ifPresent(pieceList::remove);
        piece.Position = newPosition;

        boolean kingStillInCheck = isKingInCheck(piece.Color);

        piece.Position = originalPosition;
        capturedPiece.ifPresent(pieceList::add);

        return !kingStillInCheck;
    }

    private boolean isKingInCheck(PieceColor kingColor) {
        Optional<Position> kingPosition = pieceList.stream()
                .filter(p -> p.Type == PieceType.KING && p.Color == kingColor)
                .map(p -> p.Position)
                .findFirst();

        if (kingPosition.isEmpty()) {
            throw new IllegalStateException("King not found on the board");
        }

        return pieceList.stream()
                .filter(p -> p.Color != kingColor)
                .anyMatch(p -> canAttack(p, kingPosition.get()));
    }

    private boolean canAttack(Piece attacker, Position targetPosition) {
        if (attacker instanceof Pawn pawn) {
            return pawn.isValidTakeMove(targetPosition, enPassantTarget);
        }
        return attacker.isValidMove(targetPosition) && isPathClear(attacker, targetPosition);
    }

    private boolean isEnPassantCapture(Piece piece, Position targetPosition) {
        if (!(piece instanceof Pawn) || enPassantTarget == null) {
            return false;
        }

        int forwardDirection = (piece.Color == PieceColor.WHITE) ? -1 : 1;
        return targetPosition.equals(enPassantTarget) &&
                Math.abs(piece.Position.x - targetPosition.x) == 1 &&
                targetPosition.y - piece.Position.y == forwardDirection;
    }

    private boolean moveExposesKing(Piece piece, Position newPosition) {
        Position originalPosition = piece.Position;
        Optional<Piece> capturedPiece = getPieceAtPosition(newPosition);

        piece.Position = newPosition;
        capturedPiece.ifPresent(pieceList::remove);

        boolean kingInCheck = isKingInCheck(piece.Color);

        piece.Position = originalPosition;
        capturedPiece.ifPresent(pieceList::add);

        return kingInCheck;
    }

    public void calculatePossibleMoves(Piece piece) {
        possibleMoves.clear();

        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                Position targetPosition = new Position(x, y);
                if (!targetPosition.equals(piece.Position) && isValidMove(piece, targetPosition)) {
                    possibleMoves.add(targetPosition);
                }
            }
        }
    }

    private boolean isKingInCheckmate(PieceColor kingColor) {
        if (!isKingInCheck(kingColor)) {
            return false;
        }

        Piece king = pieceList.stream()
                .filter(p -> p.Type == PieceType.KING && p.Color == kingColor)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("King not found on the board"));

        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) continue;

                Position newPosition = new Position(king.Position.x + dx, king.Position.y + dy);

                if (newPosition.x < 0 || newPosition.x > 7 || newPosition.y < 0 || newPosition.y > 7) {
                    continue;
                }

                if (isValidMove(king, newPosition) && simulateMove(king, newPosition)) {
                    return false;
                }
            }
        }

        for (Piece piece : pieceList) {
            if (piece.Color == kingColor && piece.Type != PieceType.KING) {
                calculatePossibleMoves(piece);
                for (Position move : possibleMoves) {
                    if (simulateMove(piece, move)) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    private boolean isStalemate(PieceColor color) {
        if (isKingInCheck(color)) {
            return false;
        }

        for (Piece piece : pieceList) {
            if (piece.Color == color) {
                for (int x = 0; x < 8; x++) {
                    for (int y = 0; y < 8; y++) {
                        Position targetPosition = new Position(x, y);
                        if (!targetPosition.equals(piece.Position) &&
                                isValidMove(piece, targetPosition) &&
                                simulateMove(piece, targetPosition)) {
                            return false;
                        }
                    }
                }
            }
        }

        return true;
    }

    private Optional<Piece> getPieceAtPosition(Position position) {
        return pieceList.stream()
                .filter(p -> p.Position.equals(position))
                .findFirst();
    }

    private void initializePieceController() {
        pieceList = new ArrayList<>();
        hasMoved = new ArrayList<>();
        possibleMoves = new ArrayList<>();
        movingPiece = null;
        turnColor = PieceColor.WHITE;
        movingPieceChangeListeners = new ArrayList<>();
        initializePieces();
    }

    private void initializePieces() {
        initializePiecesForColor(PieceColor.BLACK, 0, 1);
        initializePiecesForColor(PieceColor.WHITE, 7, 6);
    }

    private void initializePiecesForColor(PieceColor color, int backRow, int pawnRow) {
        IntStream.range(0, 8).forEach(i -> pieceList.add(new Pawn(color, i, pawnRow)));
        BiConsumer<Integer, Piece> addPiece = (col, piece) -> pieceList.add(piece);

//        addPiece.accept(0, new Rook(color, 0, backRow));
//        addPiece.accept(7, new Rook(color, 7, backRow));
//        addPiece.accept(1, new Knight(color, 1, backRow));
//        addPiece.accept(6, new Knight(color, 6, backRow));
//        addPiece.accept(2, new Bishop(color, 2, backRow));
//        addPiece.accept(5, new Bishop(color, 5, backRow));
//        addPiece.accept(3, new Queen(color, 3, backRow));
        addPiece.accept(4, new King(color, 4, backRow));
    }
}