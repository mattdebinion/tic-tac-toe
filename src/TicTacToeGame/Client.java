package TicTacToeGame;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import TicTacToeGame.controllers.TicTacBoardController;
import TicTacToeGame.exceptions.InvalidMoveException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * The Client class, formally the Player class, connects to the TicTacToe game server
 * 
 * @author Matt De Binion
 * @author Elihas Temori
 * @author Nika Daroui
 */
public class Client extends Application {

    private static PlayerObject me;            // The player object for this client.

    private static Socket socket;              // Socket connection to the opponent.
    private static ObjectOutputStream objOut;  // Object output stream, send to other client
    private static ObjectInputStream objIn;    // Object input stream, receive from other client.
    private static TicTacBoardController controller;     // Controller for tic tac toe board screen.
    private static Scene scene;                 // The scene

    /**
     * Runs the client program.
     * @param args [String[]] The command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Starts the stage.
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
     * Connects to the server.
     * @throws IOException
     * @throws UnknownHostException
     */
    public static void connect(TicTacBoardController boardController) {

        controller = boardController;

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    socket = new Socket("localhost", 60);
                    objOut = new ObjectOutputStream(socket.getOutputStream());
                    objOut.flush();
                    objIn = new ObjectInputStream(socket.getInputStream());
                    objOut.writeObject(new SessionData(me, 0, 0, 0));

                    listenForData();
                } catch (Exception e) {
                    System.out.println("An error occured trying to connect to the server!");
                    e.printStackTrace();
                }
            }
            
        }).start();
    }

    /**
     * Disconnect from the server.
     * @throws IOException
     */
    public static void disconnect() throws IOException {
        socket.close();
    }

    /**
     * Send a move through the socket.
     * @param x [int] The x coordinate of the move.
     * @param y [int] The y coordinate of the move.
     * @throws InvalidMoveException Occurs when move is invalid (not between 0 and 1)
     * @throws IOException Occurs when there is an error sending the move.
     */
    public static void sendMove(int x, int y) throws InvalidMoveException, IOException {
        SessionData dataToSend = new SessionData(me, 0, x, y);
        objOut.writeObject(dataToSend);
        objOut.flush();

        controller.updateBoardAt(x, y, me);

    }

    /**
     * Listen for moves (SessionData objects) from the server.
     */
    public static void listenForData() {
        new Thread(() -> {
            while(socket.isConnected()) {
                try {
                    SessionData dataReceived = (SessionData) objIn.readObject();

                    if(dataReceived instanceof SessionData) {
                        controller.updateBoardAt(dataReceived.getMovePair().getKey(), dataReceived.getMovePair().getValue(), dataReceived.getSender());
                    }

                } catch (Exception e) {
                    System.out.println("Error occured trying to listen for data within Client!");
                    e.printStackTrace();
                }
            }
            
        }).start();
    }

    /**
     * Set the name of this client.
     * @param name [String] The name of this user
     * @throws UnknownHostException
     */
    public static void setName(String name) throws UnknownHostException {
        me = new PlayerObject(name, name.charAt(0), true);

    }

    /**
     * Return this client's PlayerObject.
     * @return
     */
    public static PlayerObject getMe() {
        return me;
    }

    
}
