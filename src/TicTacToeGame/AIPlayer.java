package TicTacToeGame;

import TicTacToeGame.exceptions.InvalidMoveException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.net.UnknownHostException;

public class AIPlayer implements Serializable {

    private PlayerObject me;
    private char[][] boardState;

    // CONNECTION INFORMATION
    private static Socket socket;
    private static String host = "localhost";   // The host of the session
    private ObjectOutputStream objOut;          // To server
    private ObjectInputStream objIn;            // From server

    public AIPlayer(String name) throws UnknownHostException {

        me = new PlayerObject(name, 'A', false);
        try {
            socket = new Socket(host, 60);                             // Create a socket to the server
            objIn = new ObjectInputStream(socket.getInputStream());         // Get input stream
            objOut = new ObjectOutputStream(socket.getOutputStream());      // Get output stream
            objOut.flush();

        } catch (Exception e) {
            System.out.println("An error occurred while attempting to connect to the server.");
            e.printStackTrace();
            disconnect();
        }

        boardState = new char[3][3];
        for(int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                boardState[i][j] = ' ';
            }
        }
        listen();
    }

    /**
     * Disconnects this Client from the server.
     */
    public static void disconnect(){
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends a nonspecified object through the output stream.
     * @param object
     */
    public void sendInfo(Object object) {
        try {
            objOut.writeUnshared(object);
            objOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Given and x or y move, send it to the server.
     * @param row The row of the move.
     * @param col The column of the move.
     * @throws InvalidMoveException
     * @throws IOException
     */
    public void sendMove(int row, int col) throws InvalidMoveException, IOException {
        SessionData dataToSend = new SessionData(me, row, col);

        System.out.println("Sending move at " + row + ", " + col);
        objOut.writeUnshared(dataToSend);
        objOut.flush();

    }

    /**
     * Listens to broadcasted messages from the input stream as long as the {@linkplain Socket} is connected.
     * <p>Before invoking the {@code listen()} function, the {@code connect()} function must be called.
     */
    private void listen() {

        System.out.println("GOT TO AI PLAYER LISTEN METHOD");

        new Thread(() -> {

            while(!socket.isClosed()) {
                try {
                    Object dataReceived = objIn.readUnshared();

                    // Send me information to the server.
                    if(dataReceived instanceof PlayerObject) {
                        System.out.println("Sending my name to the server! My name is " + me.getName());
                        sendInfo(me);
                    }

                    // SessionData objects keep track of game progress!
                    if(dataReceived instanceof SessionData) {
                        SessionData decodedData = (SessionData) dataReceived;

                        // Check if decoded data has -1 for row or col. If so, it's prompting to update GUIs players.
                        // Check if decoded data has -2 for row or col. If so, it's prompting to clear GUI and disable reset button.
                        // Check if decoded data has -3 for row or col. If so, the player has left so reflect that.
                        if(decodedData.getXPos() == -3 && decodedData.getYPos() == -3) {
                            resetBoardState();
                            disconnect();
                            break;

                        } else if(decodedData.getXPos() == -2 && decodedData.getYPos() == -2) {
                            System.out.println("Reset called, clearing board!");
                            resetBoardState();

                        } else if(decodedData.getXPos() == -1 && decodedData.getYPos() == -1) {
                            modifyMe(decodedData);
                        }

                        // Check if the game is running.
                        if(decodedData.isRunning()) {
                            if(decodedData.getXPos() >= 0
                                    && decodedData.getYPos() >= 0
                                    && decodedData.getLastTurn() != null) {
                                boardState[decodedData.getXPos()][decodedData.getYPos()] = decodedData.getLastTurn().getPawn();
                            }

                            // If the current turn is my name, then it's my turn!
                            if(decodedData.getCurrentTurn().getName().equals(me.getName())){
                                if(!MiniMax.checkCharBoardForWin(boardState)) {
                                    System.out.println("AI's turn!");
                                    int[] bestMove = MiniMax.move(boardState, me.getPawn());
                                    sendMove(bestMove[0], bestMove[1]);
                                    boardState[bestMove[0]][bestMove[1]] = me.getPawn();
                                    debugAIBoardState();
                                }
                            }
                        }

                    }

                } catch (Exception e) {
                    System.out.println("AIPlayer: An error occured while receiving a message from the server!");
                    e.printStackTrace();
                }
            }

        }).start();
    }

    /**
     * Checks what the server assigns ID and pawn to this client.
     * @param data
     */
    public void modifyMe(SessionData data) {

        if(data.getPlayer1().getName() == null || data.getPlayer2().getName() == null)
            return;

        if(data.getPlayer1().getName().equals(me.getName())){
            me.setID(data.getPlayer1().getID());
            me.setPawn(data.getPlayer1().getPawn());
        }
        else if(data.getPlayer2().getName().equals(me.getName())){
            me.setID(data.getPlayer2().getID());
            me.setPawn(data.getPlayer2().getPawn());
        }

        System.out.println("I AM " + me.getName() + " WITH ID " + me.getID() + " AND PAWN " + me.getPawn());
    }

    private void resetBoardState() {
        for(int i = 0; i < 3; i++) {
            for(int j = 0; j < 3; j++) {
                boardState[i][j] = ' ';
            }
        }
    }

    private void debugAIBoardState() {
        System.out.println("Board state in AI Player:\n");
        System.out.println(boardState[0][0] + " | " + boardState[0][1] + " | " + boardState[0][2] + "\n---------");
        System.out.println(boardState[1][0] + " | " + boardState[1][1] + " | " + boardState[1][2] + "\n---------");
        System.out.println(boardState[2][0] + " | " + boardState[2][1] + " | " + boardState[2][2] + "\n");
    }
}
