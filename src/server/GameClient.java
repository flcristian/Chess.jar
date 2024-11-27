package server;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.net.*;

import constants.Globals;
import controllers.PieceControllerSingleton;
import models.utils.Position;
import controllers.PieceController;
import panels.BoardPanel;
import panels.BoardPanelSingleton;
import utils.ColorLogger;
import enums.PieceType;
import enums.PieceColor;

public class GameClient {
    private final ColorLogger logger;

    private ObjectOutputStream out;
    private ObjectInputStream in;
    private BoardPanel boardPanel;
    private PieceController pieceController;
    private boolean isPromotionInProgress;

    public GameClient(String serverAddress, int port) {
        logger = new ColorLogger(GameClient.class);

        try {
            Socket socket = new Socket(serverAddress, port);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            initializeGameWindow();
            startListening();
        } catch (IOException e) {
            logger.severe(e.getMessage());
            JOptionPane.showMessageDialog(null,
                    "Connection failed: " + e.getMessage(),
                    "Connection Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initializeGameWindow() {
        JFrame window = new JFrame("Chess Network Client");
        boardPanel = BoardPanelSingleton.getInstance();
        pieceController = PieceControllerSingleton.getInstance();
        isPromotionInProgress = false;

        window.setContentPane(boardPanel);
        window.setSize(Globals.SIZE_BOARD_PANEL + 15, Globals.SIZE_BOARD_PANEL + 30);
        window.setResizable(false);
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        boardPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();

                if (isPromotionInProgress) {
                    handlePromotionSelection(x, y);
                } else {
                    Position position = new Position(x / Globals.SIZE_TILE, y / Globals.SIZE_TILE);
                    sendMoveToServer(position);
                }
            }
        });

        window.setVisible(true);
    }

    private void handlePromotionSelection(int x, int y) {
        int dialogWidth = Globals.SIZE_TILE * 4;
        int panelWidth = Globals.SIZE_BOARD_PANEL;
        int dialogX = (panelWidth - dialogWidth) / 2;
        int dialogY = (Globals.SIZE_BOARD_PANEL - Globals.SIZE_TILE) / 2;

        if (x >= dialogX && x < dialogX + dialogWidth &&
                y >= dialogY && y < dialogY + Globals.SIZE_TILE) {

            int squareClicked = (x - dialogX) / Globals.SIZE_TILE;

            try {
                PieceType selectedType = switch (squareClicked) {
                    case 0 -> PieceType.ROOK;
                    case 1 -> PieceType.KNIGHT;
                    case 2 -> PieceType.BISHOP;
                    case 3 -> PieceType.QUEEN;
                    default -> throw new IllegalArgumentException("Invalid promotion selection");
                };
                out.writeObject(new PromotionUpdate(selectedType));
            } catch (IOException ioException) {
                logger.severe("Error sending promotion: " + ioException.getMessage());
            }
        }
    }

    private void startListening() {
        new Thread(() -> {
            try {
                while (true) {
                    Object received = in.readObject();

                    if (received instanceof ServerUpdate serverUpdate) {
                        updateGameState(serverUpdate);
                    } else if (received instanceof String receivedString) {
                        if (receivedString.startsWith("GAME_OVER")) {
                            handleGameOver(receivedString);
                        } else if (receivedString.startsWith("PROMOTION")) {
                            handleServerPromotion(receivedString);
                        }
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                logger.severe(e.getMessage());
                JOptionPane.showMessageDialog(null,
                        "Connection lost: " + e.getMessage(),
                        "Connection Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }).start();
    }

    private void handleServerPromotion(String promotionMessage) {
        PieceColor promotionColor = promotionMessage.contains("WHITE") ? PieceColor.WHITE : PieceColor.BLACK;

        if (promotionColor == pieceController.getClientColor()) {
            boardPanel.triggerPromotion(promotionColor, null);
        }
    }

    private void sendMoveToServer(Position position) {
        try {
            out.writeObject(position);
        } catch (IOException e) {
            logger.severe(e.getMessage());
        }
    }

    private synchronized void updateGameState(ServerUpdate update) {
        pieceController.setPieceList(update.PieceList());
        pieceController.setMovingPiece(update.SelectedPiece());
        pieceController.setPossibleMoves(update.PossibleMoves());
        pieceController.setClientColor(update.ClientColor());
        pieceController.setTurnColor(update.TurnColor());

        PieceColor promotionColor = update.PromotionColor();
        if (promotionColor != null && promotionColor == update.ClientColor()) {
            isPromotionInProgress = true;
            boardPanel.triggerPromotion(promotionColor, null);
        } else if (promotionColor == null && isPromotionInProgress) {
            boardPanel.closePromotionDialogue();
            isPromotionInProgress = false;
        }

        boardPanel.repaint();
    }

    private void handleGameOver(String gameOverMessage) {
        String result = gameOverMessage.split(":")[1];
        result = result.equals("Stalemate") ? result : result + " won!";
        JOptionPane.showMessageDialog(null, result);
    }

    public static void main(String[] args) {
        String serverAddress = JOptionPane.showInputDialog(
                null,
                "Enter Server IP Address:",
                "Connect to Server",
                JOptionPane.QUESTION_MESSAGE
        );

        if (serverAddress == null || serverAddress.trim().isEmpty()) {
            serverAddress = "localhost";
        }

        new GameClient(serverAddress, 8888);
    }
}