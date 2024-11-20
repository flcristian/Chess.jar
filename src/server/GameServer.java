package server;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.stream.Collectors;

import models.pieces.Piece;
import models.utils.Position;
import controllers.PieceController;
import enums.PieceColor;
import utils.ColorLogger;

public class GameServer {
    private final ColorLogger logger;

    private static final int MAX_CLIENTS = 2;
    private static final int PORT = 8888;

    private PieceController gameLogic;
    private List<ClientHandler> clients = new ArrayList<>();
    private PieceColor currentColor;

    public GameServer() {
        logger = new ColorLogger(GameServer.class);

        gameLogic = new PieceController();
        currentColor = PieceColor.WHITE;
    }

    public void start() {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            logger.debug("Chess Server started on port " + PORT);

            while (true) {
                if (clients.size() >= MAX_CLIENTS) {
                    Socket rejectedSocket = serverSocket.accept();
                    rejectedSocket.close();
                    continue;
                }

                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket, currentColor);
                this.currentColor = this.currentColor == PieceColor.WHITE ? PieceColor.BLACK : PieceColor.WHITE;
                clients.add(clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class ClientHandler implements Runnable {
        private final String clientId;
        private final PieceColor clientColor;
        private ObjectOutputStream out;
        private ObjectInputStream in;

        public ClientHandler(Socket socket, PieceColor clientColor) {
            try {
                this.clientId = UUID.randomUUID().toString();
                this.clientColor = clientColor;
                out = new ObjectOutputStream(socket.getOutputStream());
                in = new ObjectInputStream(socket.getInputStream());
                logger.debug("New client connected with internal ID: " + clientId);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void run() {
            try {
                out.writeObject(new ServerUpdate(gameLogic.getPieceList(), null, clientColor));

                while (true) {
                    // Receive move from client
                    Position position = (Position) in.readObject();

                    // Process move
                    if(gameLogic.getTurnColor() == clientColor) {
                        logger.warning("Move received from internal client ID: " + clientId + " with client color " + clientColor);
                        gameLogic.tryMovePiece(position);

                        // Broadcast updated state to all clients
                        broadcastServerUpdate();
                    }
                    else {
                        logger.severe("Move received from internal client ID: " + clientId + " with client color " + clientColor);
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

            for (ClientHandler client : clients) {
                client.out.writeObject(new ServerUpdate(
                        list,
                        gameLogic.getMovingPiece(),
                        clientColor
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