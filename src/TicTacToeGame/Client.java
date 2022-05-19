package TicTacToeGame;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import TicTacToeGame.controllers.TicTacBoardController;
import TicTacToeGame.exceptions.InvalidMoveException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * The Client class (an observer).
 */
public class Client extends Application {

    /**
     * Runs the client.
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        launch(args);
    }

    // GAME INFORMATION
    private PlayerObject me;                 // Holds the information about the player from the GUI.

    // CONNECTION INFORMATION
    private static Socket socket;
    private static String host;              // The host of the session
    private ObjectOutputStream objOut;        // To server
    private ObjectInputStream objIn;          // From server


    // GUI INTERACTION INFORMATION
    private TicTacBoardController controller;
    private Scene scene;


    
    /**
     * Starts a new client and launches the GUI to interact with the game.
     */
    @Override
    public void start(Stage stage) throws Exception {
        
        Parent root = FXMLLoader.load(getClass().getResource("fxml/StartMenuGUI.fxml"));
        scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("stylesheets/style.css").toExternalForm());

        stage.setTitle("Tic Tac Toe");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Creates an empty client.
     */
    public Client(){}

    /**
     * Connects to the server assuming the server is online.
     * <p>It's best to call this function before the Tic Tac Toe board is loaded.
     * @param boardController
     * @param me
     * @param IPAddress 
     */
    public Client(TicTacBoardController gameController, PlayerObject clientInfo, String IPAddress) {

        controller = gameController;
        me = clientInfo;
        host = IPAddress;

        try {
            socket = new Socket(host, 60);                            // Create a socket to the server
            objIn = new ObjectInputStream(socket.getInputStream());         // Get input stream
            objOut = new ObjectOutputStream(socket.getOutputStream());      // Get output stream
            objOut.flush();

        } catch (Exception e) {
            System.out.println("An error occurred while attempting to connect to the server.");
            e.printStackTrace();
            disconnect();
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

        controller.changeBoardLock(true);
        controller.updateBoardAt(row, col, me);
    }


    
    /**
     * Listens to broadcasted messages from the input stream as long as the {@linkplain Socket} is connected.
     * <p>Before invoking the {@code listen()} function, the {@code connect()} function must be called.
     */
    private void listen() {
        
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
                            controller.updateStatusLabel("Opponent has left.");
                            controller.clearBoard();
                            controller.changeResetButton(false);
                            
                            disconnect();
                            break;

                        } else if(decodedData.getXPos() == -2 && decodedData.getYPos() == -2) {
                            System.out.println("Reset called, clearing board!");
                            controller.clearBoard();
                            controller.changeResetButton(true);

                        } else if(decodedData.getXPos() == -1 && decodedData.getYPos() == -1) {
                            modifyMe(decodedData);
                        }

                        controller.updateStatusLabel(decodedData.getCurrentTurn().getName() + "'s turn!");

                        // Check if the game is running.
                        if(decodedData.isRunning()) {
                            updatePlayers(decodedData);
                            controller.updateBoardAt(decodedData.getXPos(), decodedData.getYPos(), decodedData.getLastTurn());

                            // Display message for stalemate
                            if(decodedData.seeIfStalemate()) {
                                controller.updateStatusLabel("Stalemate!");
                                controller.changeResetButton(false);
                            }

                            // Display winner.
                            if(decodedData.getWinner() != null) {
                                controller.updateStatusLabel(decodedData.getWinner().getName() + " won!");
                                controller.changeBoardLock(true);
                                controller.changeResetButton(false);
                            }

                            // If the current turn is my name, then it's my turn!
                            if(decodedData.getCurrentTurn().getName().equals(me.getName())){
                                System.out.println("It's my turn!");
                                controller.changeBoardLock(false);      // Set disable false.
                            }
                        }

                    }

                } catch (Exception e) {
                    System.out.println("An error occured while receiving a message from the server!");
                    e.printStackTrace();
                }
            }

        }).start();
    }

    /**
     * Update player names within the UI given a SessionData object.
     * @param player1
     * @param player2
     */
    private void updatePlayers(SessionData data) {

        if(data.getPlayer1() != null)
            controller.SetPlayer1(data.getPlayer1());

        if(data.getPlayer2() != null)
            controller.SetPlayer2(data.getPlayer2());
    }

    /**
     * Returns the PlayerObject associated with this client.
     * @return
     */
    public PlayerObject getMe() {
        return me;
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
    
}
