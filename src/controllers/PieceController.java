package controllers;

import enums.PieceColor;
import enums.PieceType;
import models.pieces.*;
import models.utils.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class PieceController {
    private List<Piece> pieceList;
    private List<Piece> capturedPieceList;
    private Piece movingPiece;
    private PieceColor turnColor;

    PieceController() { initializePieceController(); }

    // ACCESSORS

    public List<Piece> getPieceList() {
        return pieceList;
    }

    // PUBLIC METHODS

    public boolean tryMovePiece(Position position) {
        AtomicBoolean moved = new AtomicBoolean(false);
        pieceList.stream()
                .filter(piece -> piece.Position.equals(position))
                .findFirst()
                .ifPresentOrElse(
                        piece -> {
                            if(movingPiece == null) {
                                if(piece.Color.equals(turnColor)) {
                                    movingPiece = piece;
                                    System.out.println("New Moving Piece: " + movingPiece.Position);
                                }
                                else {
                                    System.out.println("Wrong Piece Color.");
                                }
                            }
                            else if(movingPiece.Color.equals(piece.Color)) {
                                movingPiece = null;
                                System.out.println("Cancelled movement.");
                            }
                            else {
                                if((!movingPiece.Type.equals(PieceType.PAWN) && movingPiece.isValidMove(position) && !isPathObstructed(movingPiece, position)) ||
                                        ((Pawn) movingPiece).isValidTakeMove(position)) {
                                    capturePiece(piece);
                                    movePiece(movingPiece, position);
                                    moved.set(true);
                                } else {
                                    System.out.println("Invalid move or path obstructed.");
                                }
                            }
                        },
                        () -> {
                            if(movingPiece != null) {
                                if(movingPiece.isValidMove(position) && !isPathObstructed(movingPiece, position)) {
                                    movePiece(movingPiece, position);
                                    moved.set(true);
                                } else {
                                    System.out.println("Invalid move or path obstructed.");
                                    movingPiece = null;
                                }
                            }
                        }
                );
        return moved.get();
    }

    // PRIVATE METHODS

    private void movePiece(Piece piece, Position position) {
        for (Piece p : pieceList) {
            if (p.equals(piece)) {
                p.Position = position;
                movingPiece = null;
                turnColor = turnColor.equals(PieceColor.BLACK) ? PieceColor.WHITE : PieceColor.BLACK;
                return;
            }
        }
    }

    private void capturePiece(Piece capturedPiece) {
        capturedPieceList.add(capturedPiece);
        pieceList.remove(capturedPiece);
        System.out.println(capturedPiece.Color + " " + capturedPiece.Type + " captured.");
    }

    private boolean isPathObstructed(Piece piece, Position targetPosition) {
        if (piece instanceof Knight) {
            return false;
        }

        int dx = Integer.compare(targetPosition.x, piece.Position.x);
        int dy = Integer.compare(targetPosition.y, piece.Position.y);

        Position checkedPosition = new Position(piece.Position.x, piece.Position.y);

        while (!checkedPosition.equals(targetPosition)) {
            checkedPosition = new Position(checkedPosition.x + dx, checkedPosition.y + dy);

            if (checkedPosition.equals(targetPosition)) {
                break;
            }

            for (Piece otherPiece : pieceList) {
                if (otherPiece.Position.equals(checkedPosition)) {
                    return true;
                }
            }
        }

        return false;
    }

    private void initializePieceController() {
        pieceList = new ArrayList<>();
        capturedPieceList = new ArrayList<>();
        movingPiece = null;
        turnColor = PieceColor.WHITE;
        initializePieces();
    }

    private void initializePieces() {
        initializePawns();
        initializeRooks();
        initializeKnights();
        initializeBishops();
        initializeQueens();
        initializeKings();
    }

    private void initializePawns() {
        for(int i = 0; i < 8; i++) {
            pieceList.add(new Pawn(PieceColor.BLACK, i, 1));
            pieceList.add(new Pawn(PieceColor.WHITE, i, 6));
        }
    }

    private void initializeRooks() {
        pieceList.add(new Rook(PieceColor.BLACK, 0, 0));
        pieceList.add(new Rook(PieceColor.BLACK, 7, 0));
        pieceList.add(new Rook(PieceColor.WHITE, 0, 7));
        pieceList.add(new Rook(PieceColor.WHITE, 7, 7));
    }

    private void initializeKnights() {
        pieceList.add(new Knight(PieceColor.BLACK, 1, 0));
        pieceList.add(new Knight(PieceColor.BLACK, 6, 0));
        pieceList.add(new Knight(PieceColor.WHITE, 1, 7));
        pieceList.add(new Knight(PieceColor.WHITE, 6, 7));
    }

    private void initializeBishops() {
        pieceList.add(new Bishop(PieceColor.BLACK, 2, 0));
        pieceList.add(new Bishop(PieceColor.BLACK, 5, 0));
        pieceList.add(new Bishop(PieceColor.WHITE, 2, 7));
        pieceList.add(new Bishop(PieceColor.WHITE, 5, 7));
    }

    private void initializeQueens() {
        pieceList.add(new Queen(PieceColor.BLACK, 3, 0));
        pieceList.add(new Queen(PieceColor.WHITE, 3, 7));
    }

    private void initializeKings() {
        pieceList.add(new King(PieceColor.BLACK, 4, 0));
        pieceList.add(new King(PieceColor.WHITE, 4, 7));
    }
}
