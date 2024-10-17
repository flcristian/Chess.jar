package controllers;

import enums.PieceColor;
import models.pieces.*;

import java.util.ArrayList;
import java.util.List;

public class PieceController {
    private List<Piece> pieceList;

    PieceController() {
        pieceList = new ArrayList<>();
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

    // PRIVATE METHODS

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
