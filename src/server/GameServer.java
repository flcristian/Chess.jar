package server;

import java.io.*;
import java.net.*;
import java.util.*;

import models.pieces.Piece;
import models.utils.Position;
import controllers.PieceController;
import enums.PieceColor;
import utils.ColorLogger;

public class GameServer {
    private final ColorLogger logger;

    private static final int MAX_CLIENTS = 2;
    private static final int PORT = 8888;

    private final PieceController gameLogic;
    private final List<ClientHandler> clients = new ArrayList<>();
    private PieceColor currentColor;

    public GameServer() {
        logger = new ColorLogger(GameServer.class);

        gameLogic = new PieceController();
        currentColor = PieceColor.WHITE;
    }

    public void start() {
        try {
            // Get the local IP address
            String localIpAddress = getLocalIPv4Address();
            ServerSocket serverSocket = new ServerSocket(PORT);

            logger.debug(String.format("Chess Server started on %s:%d", localIpAddress, PORT));

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
            logger.severe(e.getMessage());
        }
    }

    private String getLocalIPv4Address() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                    continue;
                }

                for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
                    InetAddress addr = interfaceAddress.getAddress();
                    if (addr instanceof Inet4Address && !addr.isLoopbackAddress()) {
                        return addr.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            logger.severe("Could not retrieve local IP address: " + e.getMessage());
        }
        return "Unknown";
    }

    private class ClientHandler implements Runnable {
        private final String clientId;
        private final PieceColor clientColor;
        private final ObjectOutputStream out;
        private final ObjectInputStream in;

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
                out.writeObject(new ServerUpdate(gameLogic.getPieceList(), null, gameLogic.PossibleMoves, clientColor, gameLogic.getTurnColor()));

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
                logger.severe(e.getMessage());
            }
        }

        private void broadcastServerUpdate() throws IOException {
            var pieceList = gameLogic.getPieceList().stream()
                    .map(Piece::clone)
                    .toList();

            var possibleMovesList = gameLogic.PossibleMoves.stream()
                    .map(Position::clone)
                    .toList();

            for (ClientHandler client : clients) {
                client.out.writeObject(new ServerUpdate(
                        pieceList,
                        gameLogic.getMovingPiece(),
                        possibleMovesList,
                        client.clientColor,
                        gameLogic.getTurnColor()
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