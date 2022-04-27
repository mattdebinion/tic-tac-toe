package TicTacToeGame;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * The Client class, formally the Player class, connects to the TicTacToe game server
 * 
 * @author Matt De Binion
 */
public class Client extends Application {

    private boolean waitTurn = true;    // When false, it is this client's turn to play.
    private static PlayerObject me;            // The player object for this client.
    private static PlayerObject opponent;      // The opponent object.

    private Socket socket;              // Socket connection
    private ObjectOutputStream objOut;  // Object output stream, send to other clients
    private ObjectInputStream objIn;    // Object input stream, receive from other clients.

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
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("stylesheets/style.css").toExternalForm());

        stage.setTitle("Tic Tac Toe");
        stage.setScene(scene);
        stage.show();
        
    }

    public static void setName(String name) throws UnknownHostException {
        me = new PlayerObject(name, name.charAt(0), true);
    }
    
}
