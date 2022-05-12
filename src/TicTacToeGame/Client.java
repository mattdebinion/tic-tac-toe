package TicTacToeGame;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import TicTacToeGame.controllers.SetPlayerOneController;
import TicTacToeGame.controllers.TicTacBoardController;
import TicTacToeGame.exceptions.InvalidMoveException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

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
    private PlayerObject ai;
    private PlayerObject player1;                        // Holds player 1 within the game
    private PlayerObject player2;                        // Holds player 2 within the game

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
     * @param gameController
     * @param clientInfo
     */
    public Client(TicTacBoardController gameController, PlayerObject clientInfo) {

        controller = gameController;
        me = clientInfo;

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

    public Client(TicTacBoardController gameController, PlayerObject clientInfo, PlayerObject ai) {

        controller = gameController;
        me = clientInfo;
        this.ai = ai;

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
        dataToSend.setSender(me);

        System.out.println("Sending move at " + row + ", " + col);
        objOut.writeObject(dataToSend);
        objOut.flush();

        controller.boardLocked(true);
        controller.updateBoardAt(row, col, me);
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
                            me.setName(SetPlayerOneController.getInputText());
                            me.setPawn('X');
                            me.setID(1);
                            player1 = me;   // Assign player 1 with my object.

                            System.out.println(player1.getName() + " is player 1 with pawn " + player1.getPawn());
                            updatePlayers(player1, player2);

                        } else if (playerInfo.getName().equals("PLAYER 2")) {
                            System.out.println("I'm player two!");
                            me.setName(SetPlayerOneController.getInputText());
                            me.setPawn('O');
                            me.setID(2);
                            player2 = me;   // Assign player 2 with my object.

                            System.out.println(player2.getName() + " is player 2 with pawn " + player2.getPawn());
                            updatePlayers(player1, player2);
                        }
                    }

                    // SessionData objects keep track of game progress!
                    if(dataReceived instanceof SessionData) {
                        SessionData decodedData = (SessionData) dataReceived;
                        SessionData newData = decodedData;

                        System.out.println("RECEIVED:");
                        decodedData.debugSessionData();

                        // The initial sent message will be a null SessionData object This lets the client know to send about me to other client.
                        if(!decodedData.isRunning()) {
                            
                            newData.setXPos(-1);
                            newData.setYPos(-1);
                            if(decodedData.getPlayer1() != null && decodedData.getPlayer2() != null) {
                                newData.startGame();
                                newData.setSender(me);
                                updatePlayers(decodedData.getPlayer1(), decodedData.getPlayer2());
                                controller.updateStatusLabel("The game has started! Waiting for opponent to move...");
                                sendInfo(newData);

                            } else if(me.getID() == 1) {
                                newData.setPlayer1(me);
                                newData.setSender(me);
                                updatePlayers(newData.getPlayer1(), newData.getPlayer2());
                                sendInfo(newData);
                            } else if(me.getID() == 2) {
                                newData.setPlayer2(me);
                                newData.setSender(me);
                                updatePlayers(newData.getPlayer1(), newData.getPlayer2());
                                sendInfo(newData);
                            }


                        } else {
                            // If the associated sender is not my name, then it's my turn. Unlock the board.
                            if(!decodedData.getSender().getName().equals(me.getName())) {
                                controller.updateBoardAt(decodedData.getXPos(), decodedData.getYPos(), decodedData.getSender());
                                controller.boardLocked(false);
                                
                                // If the given sender is the same as the client, it's the other player's turn.
                                if(decodedData.getSender().getName().equals(decodedData.getPlayer1().getName())) {
                                    controller.updateStatusLabel(decodedData.getPlayer2().getName() + "! It's your turn.");
                                } else {
                                    controller.updateStatusLabel(decodedData.getPlayer1().getName() + "! It's your turn.");
                                }
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
     * Update player names within the UI and the client.
     * @param player1
     * @param player2
     */
    private void updatePlayers(PlayerObject player1, PlayerObject player2) {

        if(player1 != null) {
            this.player1 = player1;
            controller.SetPlayer1(player1);
        }
        if(player2 != null) {
            controller.SetPlayer2(player2);
            this.player2 = player2;
        }
    }

    public PlayerObject getPlayer1() {
        return player1;
    }

    public PlayerObject getPlayer2() {
        return player2;
    }

    public static TicTacBoardController getController() {
        return controller;
    }


}
