package server;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.stream.Collectors;

import models.pieces.Piece;
import models.utils.Position;
import controllers.PieceController;
import enums.PieceColor;

public class GameServer {
    private static final int PORT = 8888;
    private PieceController gameLogic;
    private List<ClientHandler> clients = new ArrayList<>();

    public GameServer() {
        gameLogic = new PieceController();
    }

    public void start() {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("Chess Server started on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clients.add(clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class ClientHandler implements Runnable {
        private ObjectOutputStream out;
        private ObjectInputStream in;

        public ClientHandler(Socket socket) {
            try {
                out = new ObjectOutputStream(socket.getOutputStream());
                in = new ObjectInputStream(socket.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                // Initial piece list broadcast
                out.writeObject(new ServerUpdate(gameLogic.getPieceList(), null));

                while (true) {
                    // Receive move from client
                    Position movePosition = (Position) in.readObject();

                    // Process move
                    gameLogic.tryMovePiece(movePosition);

                    // Broadcast updated state to all clients
                    broadcastServerUpdate();

                    // Check for game end conditions
                    PieceColor loserColor = gameLogic.detectCheckmate();
                    if (loserColor != null) {
                        broadcastGameOver((loserColor == PieceColor.BLACK) ? "White" : "Black");
                        break;
                    }

                    PieceColor stalemateColor = gameLogic.detectStalemate();
                    if (stalemateColor != null) {
                        broadcastGameOver("Stalemate");
                        break;
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        private void broadcastServerUpdate() throws IOException {
            var list = gameLogic.getPieceList().stream()
                    .map(Piece::clone)
                    .collect(Collectors.toList());
            System.out.println("Output: " + list);

            for (ClientHandler client : clients) {
                client.out.writeObject(new ServerUpdate(
                        list,
                        gameLogic.getMovingPiece()
                ));
            }
        }

        private void broadcastGameOver(String result) throws IOException {
            for (ClientHandler client : clients) {
                client.out.writeObject("GAME_OVER:" + result);
            }
        }
    }

    public static void main(String[] args) {
        new GameServer().start();
    }
}