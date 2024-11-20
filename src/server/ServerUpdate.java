package server;

import enums.PieceColor;
import models.pieces.Piece;

import java.io.Serializable;
import java.util.List;

public class ServerUpdate implements Serializable {
    public List<Piece> PieceList;
    public Piece SelectedPiece;
    public PieceColor ClientColor;

    public ServerUpdate(List<Piece> pieceList, Piece selectedPiece, PieceColor clientColor) {
        PieceList = pieceList;
        SelectedPiece = selectedPiece;
        ClientColor = clientColor;
    }
}
