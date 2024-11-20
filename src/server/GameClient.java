package server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.net.*;

import constants.Globals;
import controllers.PieceControllerSingleton;
import enums.PieceColor;
import models.utils.Position;
import controllers.PieceController;
import panels.BoardPanel;
import panels.BoardPanelSingleton;

public class GameClient {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private BoardPanel boardPanel;
    private PieceController pieceController;

    public GameClient(String serverAddress, int port) {
        try {
            socket = new Socket(serverAddress, port);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            initializeGameWindow();
            startListening();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initializeGameWindow() {
        JFrame window = new JFrame("Chess Network Client");
        boardPanel = BoardPanelSingleton.getInstance();
        pieceController = PieceControllerSingleton.getInstance();

        window.setContentPane(boardPanel);
        window.setPreferredSize(new Dimension(Globals.WINDOW_SIZE, Globals.WINDOW_SIZE));
        window.pack();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        boardPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int x = e.getX() / Globals.SQUARE_SIZE;
                int y = e.getY() / Globals.SQUARE_SIZE;
                sendMoveToServer(new Position(x, y));
            }
        });

        window.setVisible(true);
    }

    private void startListening() {
        new Thread(() -> {
            try {
                while (true) {
                    Object received = in.readObject();

                    if (received instanceof ServerUpdate serverUpdate) {
                        updateGameState(serverUpdate);
                    } else if (received instanceof String && ((String) received).startsWith("GAME_OVER")) {
                        handleGameOver((String) received);
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void sendMoveToServer(Position position) {
        try {
            out.writeObject(position);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private synchronized void updateGameState(ServerUpdate update) {
        pieceController.setPieceList(update.PieceList);
        pieceController.setMovingPiece(update.SelectedPiece);

        if(update.SelectedPiece.Color == update.ClientColor) {
            pieceController.calculatePossibleMoves(update.SelectedPiece);
        }

        boardPanel.repaint();
    }

    private void handleGameOver(String gameOverMessage) {
        String result = gameOverMessage.split(":")[1];
        JOptionPane.showMessageDialog(null, result);
    }

    public static void main(String[] args) {
        new GameClient("localhost", 8888);
    }
}