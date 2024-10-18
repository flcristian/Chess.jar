package controllers;

import enums.PieceColor;
import enums.PieceType;
import models.pieces.*;
import models.utils.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.IntStream;

public class PieceController {
    private List<Piece> pieceList;
    private List<Piece> capturedPieceList;
    private Piece movingPiece;
    private PieceColor turnColor;
    private List<MovingPieceChangeListener> movingPieceChangeListeners;
    public List<Position> PossibleMoves;

    public PieceController() {
        initializePieceController();
    }

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

    public List<Piece> getPieceList() {
        return pieceList;
    }

    public void tryMovePiece(Position position) {
        Optional<Piece> targetPiece = getPieceAtPosition(position);

        if (movingPiece == null) {
            handlePieceSelection(targetPiece);
        } else {
            handlePieceMovement(position, targetPiece);
        }
    }

    private void handlePieceSelection(Optional<Piece> targetPiece) {
        targetPiece.ifPresent(piece -> {
            if (piece.Color.equals(turnColor)) {
                movingPiece = piece;
                notifyMovingPieceChangeListeners();
                PossibleMoves = calculatePossibleMoves(movingPiece);
                if (PossibleMoves.isEmpty()) {
                    movingPiece = null;
                    System.out.println("This piece can't move.");
                } else {
                    System.out.println("New Moving Piece: " + movingPiece.Position);
                }
            } else {
                System.out.println("Wrong Piece Color.");
            }
        });
    }

    private void handlePieceMovement(Position position, Optional<Piece> targetPiece) {
        if (targetPiece.isPresent() && movingPiece.Color.equals(targetPiece.get().Color)) {
            cancelMovement();
        } else if (isValidMove(movingPiece, position)) {
            if (simulateMove(movingPiece, position)) {
                targetPiece.ifPresent(this::capturePiece);
                movePiece(movingPiece, position);
            } else {
                System.out.println("This move doesn't resolve the check.");
            }
        } else {
            System.out.println("Invalid move or path obstructed.");
            cancelMovement();
        }
    }

    private void cancelMovement() {
        movingPiece = null;
        PossibleMoves.clear();
        notifyMovingPieceChangeListeners();
        System.out.println("Cancelled movement.");
    }

    private void movePiece(Piece piece, Position position) {
        piece.Position = position;
        movingPiece = null;
        PossibleMoves.clear();
        notifyMovingPieceChangeListeners();
        turnColor = turnColor.equals(PieceColor.BLACK) ? PieceColor.WHITE : PieceColor.BLACK;
    }

    private void capturePiece(Piece capturedPiece) {
        capturedPieceList.add(capturedPiece);
        pieceList.remove(capturedPiece);
        System.out.println(capturedPiece.Color + " " + capturedPiece.Type + " captured.");
    }

    private boolean isPathObstructed(Piece piece, Position targetPosition) {
        if (piece instanceof Knight) return false;

        int dx = Integer.compare(targetPosition.x, piece.Position.x);
        int dy = Integer.compare(targetPosition.y, piece.Position.y);
        Position checkedPosition = new Position(piece.Position.x + dx, piece.Position.y + dy);

        while (!checkedPosition.equals(targetPosition)) {
            if (getPieceAtPosition(checkedPosition).isPresent()) return true;
            checkedPosition = new Position(checkedPosition.x + dx, checkedPosition.y + dy);
        }

        return false;
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
                .anyMatch(p -> isValidMove(p, kingPosition.get()));
    }

    public List<Position> calculatePossibleMoves(Piece piece) {
        List<Position> possibleMoves = new ArrayList<>();

        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                Position targetPosition = new Position(x, y);
                if (!targetPosition.equals(piece.Position) && isValidMove(piece, targetPosition)) {
                    possibleMoves.add(targetPosition);
                }
            }
        }

        return possibleMoves;
    }

    private boolean isValidMove(Piece piece, Position targetPosition) {
        Optional<Piece> targetPiece = getPieceAtPosition(targetPosition);

        if (targetPiece.isPresent() && targetPiece.get().Color == piece.Color) {
            return false;
        }

        if (piece instanceof Pawn) {
            Pawn pawn = (Pawn) piece;
            boolean isValidPawnMove = pawn.isValidMove(targetPosition);
            boolean isValidPawnTakeMove = pawn.isValidTakeMove(targetPosition) && targetPiece.isPresent() && targetPiece.get().Color != piece.Color;

            return (isValidPawnMove || isValidPawnTakeMove) && !isPathObstructed(piece, targetPosition) && simulateMove(piece, targetPosition);
        } else {
            return piece.isValidMove(targetPosition) && !isPathObstructed(piece, targetPosition) && simulateMove(piece, targetPosition);
        }
    }

    private Optional<Piece> getPieceAtPosition(Position position) {
        return pieceList.stream()
                .filter(p -> p.Position.equals(position))
                .findFirst();
    }

    private void initializePieceController() {
        pieceList = new ArrayList<>();
        capturedPieceList = new ArrayList<>();
        PossibleMoves = new ArrayList<>();
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

        addPiece.accept(0, new Rook(color, 0, backRow));
        addPiece.accept(7, new Rook(color, 7, backRow));
        addPiece.accept(1, new Knight(color, 1, backRow));
        addPiece.accept(6, new Knight(color, 6, backRow));
        addPiece.accept(2, new Bishop(color, 2, backRow));
        addPiece.accept(5, new Bishop(color, 5, backRow));
        addPiece.accept(3, new Queen(color, 3, backRow));
        addPiece.accept(4, new King(color, 4, backRow));
    }
}