package server;

import models.pieces.Piece;

import java.io.Serializable;
import java.util.List;

public class ServerUpdate implements Serializable {
    public List<Piece> pieceList;
    public Piece selectedPiece;

    public ServerUpdate(List<Piece> pieceList, Piece selectedPiece) {
        this.pieceList = pieceList;
        this.selectedPiece = selectedPiece;
    }
}
