package TicTacToeGame;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import TicTacToeGame.controllers.SetPlayerOneController;
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
    private static PlayerObject guiInformation;                 // Holds the information about the player from the GUI.
    private static PlayerObject player1;                        // Holds player 1 within the game
    private static PlayerObject player2;                        // Holds player 2 within the game
    private static PlayerObject declaredWinner;                 // Holds the winner of the game, null if there is none at the time of play.
    private static boolean gameStarted = false;                 // Determines if game has started.
    private boolean myTurn;                                     // Lets client know it's their turn. Otherwise, wait.
    private int playerID = 1;
    private static int[][] boardState = new int[3][3];          // Holds the current state of the board.

    // CONNECTION INFORMATION
    private static Socket socket;
    private static String host = "localhost";              // The host of the session
    private ObjectOutputStream objOut;        // To server
    private ObjectInputStream objIn;          // From server


    // GUI INTERACTION INFORMATION
    private static TicTacBoardController controller;
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

    public Client(){}
    /**
     * Connects to the server assuming the server is online.
     * <p>It's best to call this function before the Tic Tac Toe board is loaded.
     * @param boardController
     * @param me 
     */
    public Client(TicTacBoardController gameController, PlayerObject clientInfo) {

        controller = gameController;
        guiInformation = clientInfo;

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
            objOut.writeObject(object);
            objOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMove(int row, int col) throws InvalidMoveException, IOException {
        SessionData dataToSend = new SessionData(player1, player2, row, col, true);
        dataToSend.setSender(guiInformation);

        System.out.println("Sending move at " + row + ", " + col);
        objOut.writeObject(dataToSend);
        objOut.flush();

        controller.changeBoardLock(true);
        controller.updateBoardAt(row, col, guiInformation);
    }


    
    /**
     * Listens to broadcasted messages from the input stream as long as the {@linkplain Socket} is connected.
     * <p>Before invoking the {@code listen()} function, the {@code connect()} function must be called.
     */
    private void listen() {
        
        new Thread(() -> {

            while(socket.isConnected()) {
                try {
                    Object dataReceived = objIn.readObject();

                    // Once the client connects, it will receive a PlayerObject detailing if it's player 1 or 2.
                    if(dataReceived instanceof PlayerObject) {
                        PlayerObject playerInfo = (PlayerObject) dataReceived;

                        if(playerInfo.getName().equals("PLAYER 1")) {
                            System.out.println("I'm player one!");
                            guiInformation.setName(SetPlayerOneController.getInputText());
                            guiInformation.setPawn('X');
                            guiInformation.setID(1);
                            player1 = guiInformation;   // Assign player 1 with my object.
                            updatePlayers(player1, player2);

                        } else if (playerInfo.getName().equals("PLAYER 2")) {
                            System.out.println("I'm player two!");
                            guiInformation.setName(SetPlayerOneController.getInputText());
                            guiInformation.setPawn('O');
                            guiInformation.setID(2);
                            player2 = guiInformation;   // Assign player 2 with my object.
                            updatePlayers(player1, player2);
                        }
                    }

                    // SessionData objects keep track of game progress!
                    if(dataReceived instanceof SessionData) {
                        SessionData decodedData = (SessionData) dataReceived;
                        SessionData newData = new SessionData();

                        // System.out.println("Received Session data object! It has the following information: ");


                        //     if(decodedData.getPlayer1() == null) {
                        //         System.out.print("Player 1: NULL");
                        //     } else {
                        //         System.out.print("Player 1: " + decodedData.getPlayer1().getName());
                        //     }
                        //     System.out.print(" vs ");
                        //     if(decodedData.getPlayer2() == null) {
                        //         System.out.println("Player 2: NULL");
                        //     } else {
                        //         System.out.println("Player 2: " + decodedData.getPlayer2().getName());
                        //     }

                        //     if(decodedData.getSender() == null) {
                        //         System.out.println("Sender: NULL");
                        //     } else {
                        //         System.out.println("Sender: " + decodedData.getSender().getName());
                        //     }
                        //     System.out.println("Associated coordinates: " + decodedData.getXPos() + ", " + decodedData.getYPos());
                        //     System.out.println("Game running status: " + decodedData.isRunning());


                        // If the associated sender is not my name, then it's my turn. Unlock the board.
                        if(decodedData.getSender() == null) {
                            controller.changeBoardLock(false);

                        } else if(!decodedData.getSender().getName().equals(guiInformation.getName())) {
                            controller.updateBoardAt(decodedData.getXPos(), decodedData.getYPos(), decodedData.getSender());
                            controller.changeBoardLock(false);

                            controller.updateStatusLabel(decodedData.getSender().getName() + "! It's your turn.");
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
     * Update player names within the UI
     * @param me
     * @param opponent
     */
    private static void updatePlayers(PlayerObject player1, PlayerObject player2) {

        if(player1 != null)
            controller.SetPlayer1(player1);

        if(player2 != null)
            controller.SetPlayer2(player2);
    }

    public PlayerObject getPlayer1() {
        return player1;
    }

    public PlayerObject getPlayer2() {
        return player2;
    }
    
}
