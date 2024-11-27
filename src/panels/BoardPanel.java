package panels;

import constants.Globals;
import controllers.PieceController;
import controllers.PieceControllerSingleton;
import dialogs.PromotionDialog;
import models.pieces.Piece;
import models.utils.Position;
import renderers.BoardRenderer;
import renderers.BoardRendererSingleton;
import renderers.PieceRenderer;
import renderers.PieceRendererSingleton;
import enums.PieceColor;
import enums.PieceType;
import utils.ColorLogger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

public class BoardPanel extends JPanel {
    private final ColorLogger logger;

    public Graphics2D G2D;

    private final PieceController pieceController;
    private final PromotionDialog promotionDialog;
    private PieceColor promotionColor;
    private Consumer<PieceType> promotionCallback;
    private boolean isPromotionInProgress;

    public BoardPanel() {
        logger = new ColorLogger(BoardPanel.class);

        pieceController = PieceControllerSingleton.getInstance();
        promotionDialog = new PromotionDialog();
        setOpaque(false);
        isPromotionInProgress = false;

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();

                if (e.getButton() == MouseEvent.BUTTON1) {
                    if (isPromotionInProgress) {
                        handlePromotionSelection(x, y);
                    }
                    else {
                        pieceController.tryMovePiece(new Position(x / Globals.SIZE_TILE, y / Globals.SIZE_TILE));
                    }
                }
                else if (e.getButton() == MouseEvent.BUTTON3) {
                    pieceController.cancelMovement();
                }
            }
        });

        pieceController.addMovingPieceChangeListener(newMovingPiece -> {
            pieceController.setClientColor(pieceController.getTurnColor());
            PieceColor loserColor = pieceController.detectCheckmate();
            if(loserColor != null) {
                logger.info((loserColor.equals(PieceColor.BLACK) ? "White" : "Black") + " won!");
            }

            if(pieceController.detectStalemate() != null) {
                logger.info("It's a stalemate!");
            }

            repaint();
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        G2D = (Graphics2D) g;

        BoardRenderer boardRenderer = BoardRendererSingleton.getInstance();
        boardRenderer.renderBoard();

        PieceRenderer pieceRenderer = PieceRendererSingleton.getInstance();

        PieceController pieceController = PieceControllerSingleton.getInstance();
        for(Piece piece : pieceController.getPieceList()) {
            pieceRenderer.renderPiece(G2D, piece.Type, piece.Color, piece.Position.x(), piece.Position.y());
        }

        if(isPromotionInProgress) {
            promotionDialog.showInGamePromotion(G2D, this, promotionColor);
        }
    }

    public void triggerPromotion(PieceColor pawnColor, Consumer<PieceType> callback) {
        promotionColor = pawnColor;
        promotionCallback = callback;
        isPromotionInProgress = true;
        repaint();
    }

    public void closePromotionDialogue() {
        isPromotionInProgress = false;
        promotionColor = null;
        promotionCallback = null;

        repaint();
    }

    public void handlePromotionSelection(int x, int y) {
        if (!isPromotionInProgress) return;

        int dialogWidth = Globals.SIZE_TILE * 4;
        int panelWidth = Globals.SIZE_BOARD_PANEL;
        int dialogX = (panelWidth - dialogWidth) / 2;
        int dialogY = (Globals.SIZE_BOARD_PANEL - Globals.SIZE_TILE) / 2;

        if (x >= dialogX && x < dialogX + dialogWidth &&
                y >= dialogY && y < dialogY + Globals.SIZE_TILE) {

            int squareClicked = (x - dialogX) / Globals.SIZE_TILE;

            PieceType promotedPieceType = switch (squareClicked) {
                case 0 -> PieceType.ROOK;
                case 1 -> PieceType.KNIGHT;
                case 2 -> PieceType.BISHOP;
                case 3 -> PieceType.QUEEN;
                default -> throw new IllegalArgumentException("Invalid promotion selection");
            };

            if (promotionCallback != null) {
                promotionCallback.accept(promotedPieceType);
            }

            isPromotionInProgress = false;
            repaint();
        }
    }
}