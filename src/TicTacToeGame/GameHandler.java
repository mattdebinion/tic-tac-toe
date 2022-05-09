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
    private Socket player2;

    private ObjectInputStream fromPlayer1;
    private ObjectOutputStream toPlayer1;
    private ObjectInputStream fromPlayer2;
    private ObjectOutputStream toPlayer2;

    private int[][] board;

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

            toPlayer1.writeObject(new SessionData());   // Write something to player 1 to notify them that there are now two players.

            while(true) {

                SessionData dataToReceive = (SessionData) fromPlayer1.readObject();

                if(dataToReceive instanceof SessionData) {
                    System.out.println("Received data from player1, sending it to player 2...");

                    if(dataToReceive.isRunning()) {
                        dataToReceive = verifyMove(dataToReceive);
                        sendSessionData(toPlayer2, dataToReceive);
                    }
                    sendSessionData(toPlayer2, dataToReceive);
                }

                SessionData dataToReceive2 = (SessionData) fromPlayer2.readObject();

                if(dataToReceive2 instanceof SessionData) {
                    System.out.println("Received data from player2, sending it to player 1...");

                    if(dataToReceive2.isRunning()) {
                        dataToReceive2 = verifyMove(dataToReceive2);
                        sendSessionData(toPlayer1, dataToReceive2);
                    } else {
                        sendSessionData(toPlayer1, dataToReceive2);
                    }
                    
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
     * Given a SessionData object, determines if the sending player made a winning or stalemate move.
     * @param dataToVerify The SessionData object to verify.
     * @return The same SessionData object if no winning or stalemate move was made, otherwise a new SessionData object indicating winner.
     */
    private SessionData verifyMove(SessionData dataToVerify) {

        SessionData dataToReturn = dataToVerify;            // Assign returning object to the object in parameter.
        board = dataToVerify.getSenderBoardState();         // Assign locally
        PlayerObject player = dataToVerify.getSender();     // Get player to check.

        // If a session data object is passed with -1 X and Y positions.
        if(dataToReturn.getXPos() == -1 && dataToReturn.getYPos() == -1) {
            return dataToReturn;
        }
        // Check for a vertical win (if X1, X2, X3 are the same)
        for(int col = 0; col < 3; col++) {
            if(board[0][col] == player.getID() && board[1][col] == player.getID() && board[2][col] == player.getID()) {
                dataToReturn.setWinner(player);
                return dataToReturn;
            }
        }

        // Check for horizontal win (if 1X, 2X, 3X are the same)
        for(int row = 0; row < 3; row++) {
            if(board[row][0] == player.getID() && board[row][1] == player.getID() && board[row][2] == player.getID()) {
                dataToReturn.setWinner(player);
                return dataToReturn;
            }
        }

        // Check for both diagonal orientations (if 11, 22, 33 OR 31, 22, 13 are the same)
        if(board[0][0] == player.getID() && board[1][1] == player.getID() && board[2][2] == player.getID()) {
            dataToReturn.setWinner(player);
            return dataToReturn;
        }

        if(board[0][2] == player.getID() && board[1][1] == player.getID() && board[2][0] == player.getID()) {
            dataToReturn.setWinner(player);
            return dataToReturn;
        }
    }
    ////// Print Board
        for(int i = 0; i < 3; i++) {
        for(int j = 0; j < 3; j++) {
            System.out.print(GameBoard[i][j] + " ");
        }
        System.out.println();
    }
    // If a session data object is passed with -1 X and Y positions.
        if(dataToReturn.getXPos() == -1 && dataToReturn.getYPos() == -1) {
        return dataToReturn;
    }

    // check player 1 Wins
    // horizontal Row 0 player 1 check
        if (GameBoard[0][0] == 1 && GameBoard[0][1] == 1 && GameBoard[0][2] == 1){
        System.out.println("Test: HORIZINTAL ROW 0 PLAYER 1 WIN");
        dataToReturn.setWinner(player);
        return dataToReturn;
    }

    // horizontal row 1
        if (GameBoard[1][0] == 1 && GameBoard[1][1] == 1 && GameBoard[1][2] == 1) {
        System.out.println("Test: horizontal ROW 1 PLAYER 1 WIN");
        dataToReturn.setWinner(player);
        return dataToReturn;
    }
    // horizontal row 2
        if (GameBoard[2][0] == 1 && GameBoard[2][1] == 1 && GameBoard[2][2] == 1) {
        System.out.println("Test: HORIZINTAL ROW 2 PLAYER 1 WIN");
        dataToReturn.setWinner(player);
        return dataToReturn;
    }


    ///////
    // vertical column 0 player 1
        if (GameBoard[0][0] == 1 && GameBoard[1][0] == 1 && GameBoard[2][0] == 1){
        System.out.println("Test: Vertical Column 0 PLAYER 1 WIN");
        dataToReturn.setWinner(player);
        return dataToReturn;
    }

    // vertical column 1 player 1
        if (GameBoard[0][1] == 1 && GameBoard[1][1] == 1 && GameBoard[2][1] == 1){
        System.out.println("Test: Vertical Column 1 PLAYER 1 WIN");
        dataToReturn.setWinner(player);
        return dataToReturn;
    }

    // vertical column 2 player 1
        if (GameBoard[0][2] == 1 && GameBoard[1][2] == 1 && GameBoard[2][2] == 1){
        System.out.println("Test: Vertical Column 2 PLAYER 1 WIN");
        dataToReturn.setWinner(player);
        return dataToReturn;
    }
    ////////
    // Diagonal Left to Right player 1
        if (GameBoard[0][0] == 1 && GameBoard[1][1] == 1 && GameBoard[2][2] == 1){
        System.out.println("Test: Diagonal 1 PLAYER 1 WIN");
        dataToReturn.setWinner(player);
        return dataToReturn;
    }

    // Diagonal Left to Right player 1
        if (GameBoard[2][0] == 1 && GameBoard[1][1] == 1 && GameBoard[0][2] == 1){
        System.out.println("Test: Diagonal 2 PLAYER 1 WIN");
        dataToReturn.setWinner(player);
        return dataToReturn;
    }

    //// PLAYER 2 WIN CHECK

    // check player 2 Wins
    // horizontal Row 0 player 1 check
        if (GameBoard[0][0] == 2 && GameBoard[0][1] == 2 && GameBoard[0][2] == 2){
        System.out.println("Test: HORIZINTAL ROW 0 PLAYER 1 WIN");
        dataToReturn.setWinner(player);
        return dataToReturn;
    }

    // horizontal row 1
        if (GameBoard[1][0] == 2 && GameBoard[1][1] == 2 && GameBoard[1][2] == 2) {
        System.out.println("Test: horizontal ROW 1 PLAYER 1 WIN");
        dataToReturn.setWinner(player);
        return dataToReturn;
    }
    // horizontal row 2
        if (GameBoard[2][0] == 2 && GameBoard[2][1] == 2 && GameBoard[2][2] == 2) {
        System.out.println("Test: HORIZINTAL ROW 2 PLAYER 1 WIN");
        dataToReturn.setWinner(player);
        return dataToReturn;
    }


    ///////
    // vertical column 0 player 1
        if (GameBoard[0][0] == 2 && GameBoard[1][0] == 2 && GameBoard[2][0] == 2){
        System.out.println("Test: Vertical Column 0 PLAYER 1 WIN");
        dataToReturn.setWinner(player);
        return dataToReturn;
    }

    // vertical column 1 player 1
        if (GameBoard[0][1] == 2 && GameBoard[1][1] == 2 && GameBoard[2][1] == 2){
        System.out.println("Test: Vertical Column 1 PLAYER 1 WIN");
        dataToReturn.setWinner(player);
        return dataToReturn;
    }

    // vertical column 2 player 1
        if (GameBoard[0][2] == 2 && GameBoard[1][2] == 2 && GameBoard[2][2] == 2){
        System.out.println("Test: Vertical Column 2 PLAYER 1 WIN");
        dataToReturn.setWinner(player);
        return dataToReturn;
    }
    ////////
    // Diagonal Left to Right player 1
        if (GameBoard[0][0] == 2 && GameBoard[1][1] == 2 && GameBoard[2][2] == 2){
        System.out.println("Test: Diagonal 1 PLAYER 1 WIN");
        dataToReturn.setWinner(player);
        return dataToReturn;
    }

    // Diagonal Left to Right player 1
        if (GameBoard[2][0] == 2 && GameBoard[1][1] == 2 && GameBoard[0][2] == 2){
        System.out.println("Test: Diagonal 2 PLAYER 1 WIN");
        dataToReturn.setWinner(player);
        return dataToReturn;
    }

        // Check if the entire board is filled. If so, return a stalemate.
        for(int row = 0; row < 3; row++) {
            for(int col = 0; col < 3; col++) {

                // If there is a zero at any position, then it is unfilled. Return!
                if(board[row][col] == 0) {
                    return dataToReturn;
                }
            }
        }
        
        dataToReturn.setStalemate();
        return dataToReturn;    // Return a stalemate otherwise.
    }
}
