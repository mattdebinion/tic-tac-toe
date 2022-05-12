package TicTacToeGame;

import TicTacToeGame.controllers.SetPlayerOneController;
import TicTacToeGame.exceptions.GameNotStartedException;
import TicTacToeGame.exceptions.InvalidMoveException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.net.UnknownHostException;

public class AIPlayer implements Serializable {

    private PlayerObject player1;
    private PlayerObject player2;
    private PlayerObject ai;
    private char[][] boardState;

    // CONNECTION INFORMATION
    private static Socket socket;
    private static String host = "localhost";   // The host of the session
    private ObjectOutputStream objOut;          // To server
    private ObjectInputStream objIn;            // From server

    public AIPlayer() throws UnknownHostException {
        ai = new PlayerObject(thinkOfRandomName(), 'A', false);
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
        think();
    }

    private void think() {

        // Create a new Thread that looks to see if it can play the current turn.

        new Thread(() -> {
            System.out.println(ai.getName() + ": I am alive!");

            while(socket.isConnected()) {
                try {
                    Object dataReceived = objIn.readObject();

                    // Once the ai connects, it will receive a PlayerObject detailing if it's player 1 or 2.
                    if (dataReceived instanceof PlayerObject) {

                        PlayerObject playerInfo = (PlayerObject) dataReceived;

                        if (playerInfo.getName().equals("PLAYER 1")) {
                            System.out.println("I'm player one!");
                            ai.setPawn('X');
                            ai.setID(1);
                            player1 = ai;   // Assign player 1 with my object.

                            System.out.println(player1.getName() + " is player 1 with pawn " + player1.getPawn());
                            updatePlayers(player1, player2);

                        } else if (playerInfo.getName().equals("PLAYER 2")) {
                            System.out.println("I'm player two!");
                            ai.setPawn('O');
                            ai.setID(2);
                            player2 = ai;   // Assign player 2 with my object.

                            System.out.println(player2.getName() + " is player 2 with pawn " + player2.getPawn());
                            updatePlayers(player1, player2);
                        }
                    }

                    // SessionData objects keep track of game progress!
                    if (dataReceived instanceof SessionData) {
                        SessionData decodedData = (SessionData) dataReceived;
                        SessionData newData = decodedData;

                        System.out.println("RECEIVED:");
                        decodedData.debugSessionData();

                        // The initial sent message will be a null SessionData object This lets the client know to send about me to other client.
                        if (!decodedData.isRunning()) {

                            newData.setXPos(-1);
                            newData.setYPos(-1);
                            if (decodedData.getPlayer1() != null && decodedData.getPlayer2() != null) {
                                newData.startGame();
                                newData.setSender(ai);
                                updatePlayers(decodedData.getPlayer1(), decodedData.getPlayer2());
                                //controller.updateStatusLabel("The game has started! Waiting for opponent to move...");
                                sendInfo(newData);

                            } else if (ai.getID() == 1) {
                                newData.setPlayer1(ai);
                                newData.setSender(ai);
                                updatePlayers(newData.getPlayer1(), newData.getPlayer2());
                                sendInfo(newData);
                            } else if (ai.getID() == 2) {
                                newData.setPlayer2(ai);
                                newData.setSender(ai);
                                updatePlayers(newData.getPlayer1(), newData.getPlayer2());
                                sendInfo(newData);
                            }

                        } else {
                            // If the associated sender is not my name, then it's my turn. Unlock the board.
                            if (!decodedData.getSender().getName().equals(ai.getName())) {
                                //controller.updateBoardAt(decodedData.getXPos(), decodedData.getYPos(), decodedData.getSender());
                                boardState[decodedData.getXPos()][decodedData.getYPos()] = decodedData.getSender().getPawn();
                                Client.getController().boardLocked(false);

                                int[] bestMove = MiniMax.move(boardState, ai.getPawn());

                                sendMove(bestMove[0], bestMove[1]);
                                boardState[bestMove[0]][bestMove[1]] = ai.getPawn();
                                debugAIBoardState();

                            }
                        }

                        Thread.sleep(250);  // Speed control the thread as going too fast breaks the game.

                    }
                } catch(IOException | ClassNotFoundException | InterruptedException | InvalidMoveException e){
                    e.printStackTrace();
                }
            }
        }).start();

    }

    public void sendInfo(Object object) {
        try {
            objOut.writeObject(object);
            objOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Update player names within the UI and the client.
     * @param player1
     * @param player2
     */
    private void updatePlayers(PlayerObject player1, PlayerObject player2) {

        if(player1 != null)
            this.player1 = player1;

        if(player2 != null)
            this.player2 = player2;
    }

    public void sendMove(int row, int col) throws InvalidMoveException, IOException {
        SessionData dataToSend = new SessionData(player1, player2, row, col, true);
        dataToSend.setSender(ai);

        System.out.println("Sending move at " + row + ", " + col);
        objOut.writeObject(dataToSend);
        objOut.flush();

        //Client.getController().boardLocked(true);
        //Client.getController().updateBoardAt(row, col, ai);
    }

    private static int thinkOfRandomNumber(int min, int max) {
        return (int) (Math.random() * (max - min) + min);
    }

    /**
     * I pick a random name from a list of names.
     */
    private static String thinkOfRandomName() {
        String[] names = {"Matt", "Nika", "Elihas"};
        return "AI " + names[thinkOfRandomNumber(0, names.length)];
    }

    /**
     * Disconnects the AI from the server.
     */
    public static void disconnect(){
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public PlayerObject getAi() {
        return ai;
    }

    private void debugAIBoardState() {
        System.out.println("Board state in AI Player:\n");
        System.out.println(boardState[0][0] + " | " + boardState[0][1] + " | " + boardState[0][2] + "\n---------");
        System.out.println(boardState[1][0] + " | " + boardState[1][1] + " | " + boardState[1][2] + "\n---------");
        System.out.println(boardState[2][0] + " | " + boardState[2][1] + " | " + boardState[2][2] + "\n");
    }
}
