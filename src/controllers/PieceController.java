package controllers;

import enums.PieceColor;
import models.pieces.*;
import models.utils.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class PieceController {
    private List<Piece> pieceList;
    private Piece movingPiece;

    PieceController() {
        pieceList = new ArrayList<>();
        movingPiece = null;
    }

    // ACCESSORS

    public List<Piece> getPieceList() {
        return pieceList;
    }

    // PUBLIC METHODS

    public void initializePieces() {
        initializePawns();
        initializeRooks();
        initializeKnights();
        initializeBishops();
        initializeQueens();
        initializeKings();
    }

    public boolean tryMovePiece(Position position) {
        AtomicBoolean moved = new AtomicBoolean(false);
        pieceList.stream()
                .filter(piece -> piece.Position.equals(position))
                .findFirst()
                .ifPresentOrElse(
                        piece -> {
                            if(movingPiece == null ) {
                                movingPiece = piece;
                                System.out.println("New Moving Piece: " + movingPiece.Position);
                            }
                            else {
                                movingPiece = null;
                                System.out.println("Cancelled movement.");
                            }
                        },
                        () -> {
                            if(movingPiece != null) {
                                movePiece(movingPiece, position);
                                moved.set(true);
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
                return;
            }
        }
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
