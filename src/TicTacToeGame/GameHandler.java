package TicTacToeGame;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * The GameHandler class (an observable) handles the sessions and game logic that is sent through the server.
 */
public class GameHandler implements Runnable {

    private Socket player1;
    private PlayerObject player1Object;
    private Socket player2;
    private PlayerObject player2Object;

    private ObjectInputStream fromPlayer1;
    private ObjectOutputStream toPlayer1;
    private ObjectInputStream fromPlayer2;
    private ObjectOutputStream toPlayer2;

    private int[][] board = new int[3][3];

    /**
     * Creates a new game handler with two player sockets and their output streams.
     * @param player1 The first player's socket.
     * @param toPlayer1 The first player's output stream.
     * @param player2 The second player's socket.
     * @param toPlayer2 The second player's output stream.
     * @throws IOException Occurs when not connected to the Internet.
     */
    public GameHandler(Socket player1, ObjectOutputStream toPlayer1, Socket player2, ObjectOutputStream toPlayer2) throws IOException {

        this.player1 = player1;
        this.toPlayer1 = toPlayer1;

        this.player2 = player2;
        this.toPlayer2 = toPlayer2;
    }

    @Override
    public void run() {

        try {
            fromPlayer1 = new ObjectInputStream(player1.getInputStream());
            fromPlayer2 = new ObjectInputStream(player2.getInputStream());

            broadcastSessionData(new SessionData());        // Broadcast an empty session data object to all listeners as the game is about to start.

            // Infinitely loop here, reading SessionData objects from both players and passing them to the other player.
            while(true) {

                // ==============================
                // HANDLE PLAYER ONE.
                // ==============================
                Object player1ReceivedData = fromPlayer1.readObject();      // Receive the data from Player 1.

                // IF player 1 has sent a player object, this means they are newly connected to the server.
                if(player1ReceivedData instanceof PlayerObject) {
                    PlayerObject player = (PlayerObject) player1ReceivedData;
                    System.out.print("The player " + player.getName() + " has connected! ");

                    // Assign player to next available slot that is empty within this handler.
                    if(player1Object == null) {
                        player1Object = new PlayerObject();
                        player1Object.setName(player.getName());
                        player1Object.setPawn('X');
                        player1Object.setID(1);
                        System.out.println(" They are player " + player1Object.getID() + " with the pawn " + player1Object.getPawn());

                    } else if (player2Object == null) {
                        player2Object = new PlayerObject();
                        player2Object.setName(player.getName());
                        player2Object.setPawn('O');
                        player2Object.setID(2);
                        System.out.println(" They are player " + player2Object.getID() + " with the pawn " + player2Object.getPawn());
                    }

                    if(player1Object != null && player2Object != null) {
                        startGame();
                    }
                }


                if(player1ReceivedData instanceof SessionData) {
                    SessionData dataToReceive = (SessionData) player1ReceivedData;
                    System.out.println("SessionData received from player 1:");
                    dataToReceive.debugSessionData();

                    if(dataToReceive.isRunning()) {
                        dataToReceive = verifyMove(dataToReceive);
                    }

                    System.out.print("SessionData sending to player 2:");
                    dataToReceive.debugSessionData();
                    sendSessionData(toPlayer2, dataToReceive);
                }

                // ==============================
                // HANDLE PLAYER TWO.
                // ==============================
                Object player2ReceivedData = fromPlayer2.readObject();      // Receive the data from Player 1.

                // IF player 1 has sent a player object, this means they are newly connected to the server.
                if(player2ReceivedData instanceof PlayerObject) {
                    PlayerObject player = (PlayerObject) player2ReceivedData;
                    System.out.print("The player " + player.getName() + " has connected! ");

                    // Assign player to next available slot that is empty within this handler.
                    if(player1Object == null) {
                        player1Object = new PlayerObject();
                        player1Object.setName(player.getName());
                        player1Object.setPawn('X');
                        player1Object.setID(1);
                        System.out.println(" They are player " + player1Object.getID() + " with the pawn " + player1Object.getPawn());

                    } else if (player2Object == null) {
                        player2Object = new PlayerObject();
                        player2Object.setName(player.getName());
                        player2Object.setPawn('O');
                        player2Object.setID(2);
                        System.out.println(" They are player " + player2Object.getID() + " with the pawn " + player2Object.getPawn());
                    }

                    if(player1Object != null && player2Object != null) {
                        startGame();
                    }
                }

                if(player2ReceivedData instanceof SessionData) {
                    SessionData dataToReceive = (SessionData) player1ReceivedData;
                    System.out.println("SessionData received from player 2:");
                    dataToReceive.debugSessionData();

                    if(dataToReceive.isRunning()) {
                        dataToReceive = verifyMove(dataToReceive);
                    }

                    System.out.println("SessionData sending to player 1");
                    dataToReceive.debugSessionData();
                    sendSessionData(toPlayer2, dataToReceive);
                }

            }

        } catch (Exception e) {
            System.out.println("An error occured within the GameHandler!");
            e.printStackTrace();
        }
        
    }

    /**
     * Sends given session data to the given output stream.
     * @param destination
     * @param dataToSend
     * @throws IOException
     */
    private void sendSessionData(ObjectOutputStream destination, SessionData dataToSend) throws IOException {
        destination.writeObject(dataToSend);
        destination.flush();
    }

    /**
     * Sends a session data object to all output streams.
     * @throws IOException Occurs if not connected to the Internet.
     */
    private void broadcastSessionData(SessionData dataToSend) throws IOException {

        toPlayer1.writeObject(dataToSend);
        toPlayer2.writeObject(dataToSend);
        toPlayer1.flush();
        toPlayer2.flush();
    }

    /**
     * Starts the game by broadcasting to both players that the game has started and lets player 1 know to go first.
     * @throws IOException
     */
    private void startGame() throws IOException {
        SessionData startGameData = new SessionData();
        startGameData.setPlayer1(player1Object);
        startGameData.setPlayer2(player2Object);
        startGameData.setCurrentTurn(player1Object);
        startGameData.setXPos(-1);
        startGameData.setYPos(-1);
        startGameData.startGame();

        broadcastSessionData(startGameData);
    }

    /**
     * Given a SessionData object, switch the current turn to the other player.
     * @param data
     * @return
     */
    private void switchTurns(SessionData data) {

        String currentTurn = data.getCurrentTurn().getName();
        if(player1Object.getName().equals(currentTurn)) {
            data.setCurrentTurn(player2Object);
        } else {
            data.setCurrentTurn(player1Object);
        }
    }

    /**
     * Given a name, returns the appropriate {@linkplain PlayerObject} if they exist.
     * @param name Name of the player
     * @return PlayerObject if the player is in the game, null otherwise.
     */
    private PlayerObject nameToPlayerObject(String name) {
        if(player1Object.getName().equals(name)) {
            return player1Object;
        } else if (player2Object.getName().equals(name)) {
            return player2Object;
        }

        return null;
    }

    /**
     * Given a SessionData object, determines if the sending player made a winning or stalemate move.
     * @param dataToVerify The SessionData object to verify.
     * @return The same SessionData object if no winning or stalemate move was made, otherwise a new SessionData object indicating winner.
     */
    private SessionData verifyMove(SessionData dataToVerify) {
        System.out.println("Verifying move for " + dataToVerify.getCurrentTurn().getName());
        PlayerObject player = nameToPlayerObject(dataToVerify.getCurrentTurn().getName());     // Get player to check.
        System.out.println("They have an ID of " + player.getID() + " and a pawn of " + player.getPawn());

        // Since clients return only the moves and their turn, we need to reconstruct the SessionData object.
        SessionData dataToReturn = dataToVerify;
        dataToReturn.setPlayer1(player1Object);
        dataToReturn.setPlayer2(player2Object);

        // If a session data object is passed with -1 X and Y positions.
        if(dataToReturn.getXPos() == -1 && dataToReturn.getYPos() == -1) {
            System.out.println("IGNORE SESSION DATA");
            return dataToReturn;
        }

        board[dataToReturn.getXPos()][dataToReturn.getYPos()] = player.getID();

        System.out.println("Checking for vertical win...");
        // Check for a vertical win (if X1, X2, X3 are the same)
        for(int col = 0; col < 3; col++) {
            if(board[0][col] == player.getID() && board[1][col] == player.getID() && board[2][col] == player.getID()) {
                System.out.println("VERT WIN");
                dataToReturn.setWinner(player);
                return dataToReturn;
            }
        }

        System.out.println("Checking for horizontal win...");
        // Check for horizontal win (if 1X, 2X, 3X are the same)
        for(int row = 0; row < 3; row++) {
            if(board[row][0] == player.getID() && board[row][1] == player.getID() && board[row][2] == player.getID()) {
                System.out.println("HORIZ WIN");
                dataToReturn.setWinner(player);
                return dataToReturn;
            }
        }

        System.out.println("Checking for diagonal win...");
        // Check for both diagonal orientations (if 11, 22, 33 OR 31, 22, 13 are the same)
        if(board[0][0] == player.getID() && board[1][1] == player.getID() && board[2][2] == player.getID()) {
            System.out.println("DIAG WIN");
            dataToReturn.setWinner(player);
            return dataToReturn;
        }

        if(board[0][2] == player.getID() && board[1][1] == player.getID() && board[2][0] == player.getID()) {
            System.out.println("DIAG WIN");
            dataToReturn.setWinner(player);
            return dataToReturn;
        }

        System.out.println("Checking for stalemate...");
        // Check if the entire board is filled. If so, return a stalemate.
        for(int row = 0; row < 3; row++) {
            for(int col = 0; col < 3; col++) {

                // If there is a zero at any position, then it is unfilled. Return but make sure it's the other player's turn.
                if(board[row][col] == 0) {

                    System.out.println("No passing move, switching turns...");
                    switchTurns(dataToReturn);
                    return dataToReturn;
                }
            }
        }
        
        System.out.println("STALEMATE.");
        dataToReturn.setStalemate();
        return dataToReturn;    // Return a stalemate otherwise.
    }
}
