package server;

import java.io.*;
import java.net.*;
import java.util.*;

import enums.PieceType;
import exceptions.ClientHandlerInitializationException;
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
                throw new ClientHandlerInitializationException("Error initializing ClientHandler", e);
            }
        }

        @Override
        public void run() {
            try {
                out.writeObject(new ServerUpdate(
                        gameLogic.getPieceList(),
                        null,
                        gameLogic.PossibleMoves,
                        clientColor,
                        gameLogic.getTurnColor(),
                        null
                ));

                while (true) {
                    Object received = in.readObject();

                    if (gameLogic.isPromotionInProgress()) {
                        broadcastServerUpdate();
                    }

                    if (received instanceof PromotionUpdate(PieceType promotedPieceType)) {
                        logger.warning("Promotion selection received: " + clientId + " with client color " + clientColor);
                        handlePromotion(promotedPieceType);

                        broadcastServerUpdate();

                        if (checkAndBroadcastEndGame()) { break; }

                        continue;
                    }

                    if(received instanceof Position position) {
                        if(gameLogic.getTurnColor() == clientColor) {
                            logger.warning("Move received from internal client ID: " + clientId + " with client color " + clientColor);
                            gameLogic.tryMovePiece(position);

                            broadcastServerUpdate();

                            if (checkAndBroadcastEndGame()) { break; }
                        }
                        else {
                            logger.severe("Move received from internal client ID: " + clientId + " with client color " + clientColor);
                        }
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                logger.severe(e.getMessage());
            }
        }

        private boolean checkAndBroadcastEndGame() throws IOException {
            PieceColor loserColor = gameLogic.detectCheckmate();
            if (loserColor != null) {
                broadcastGameOver((loserColor == PieceColor.BLACK) ? "White" : "Black");
                return true;
            }

            PieceColor stalemateColor = gameLogic.detectStalemate();
            if (stalemateColor != null) {
                broadcastGameOver("Stalemate");
                return true;
            }

            return false;
        }

        private void handlePromotion(PieceType promotedPieceType) throws IOException {
            gameLogic.completePawnPromotion(promotedPieceType);
            broadcastServerUpdate();
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
                        gameLogic.getTurnColor(),
                        gameLogic.isPromotionInProgress() ? gameLogic.getPromotionColor() : null
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