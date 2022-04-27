package TicTacToeGame;

import TicTacToeGame.exceptions.GameNotStartedException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import javax.naming.Name;
import java.io.IOException;
import java.net.Socket;

public class TicTacGUIController {

    public static String NamePlayerOne;
    public static String NamePlayerTwo;
    public Label PlayerDisplay1;
    public Label PlayerDisplay2;
    private Stage stage;
    private Scene scene;
    private Parent root;
    public static boolean PlayerOneMode = false;
    public static boolean OnlinePlayMode = false;

    public boolean localMultiplayer = false;       // Set to FALSE to enable AI.

    public static boolean TESTCreatingGame = false;
    public static boolean TESTJoiningGame = false;

    @FXML
    public Button submitButton;
    @FXML
    private TextField PlayerOneNameInput;
    @FXML
    private Label EnterPlayOneNameLabel;
    @FXML
    public Button submitButton1;
    @FXML
    private TextField PlayerTwoNameInput;
    @FXML
    private Label EnterPlayTwoNameLabel;
    @FXML
    private Button  quitButton;
    @FXML
    private AnchorPane startMenu;

    @FXML
    public void SetGameMode(ActionEvent event) throws IOException{
        root = FXMLLoader.load(getClass().getResource("fxml/SetPlayers.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void PlayerOneMode (ActionEvent event) throws IOException {
        PlayerOneMode = true;
        root = FXMLLoader.load(getClass().getResource("fxml/SetPlayerOne.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void PlayerTwoMode (ActionEvent event) throws IOException {
        PlayerOneMode = false;
        localMultiplayer = true;
        root = FXMLLoader.load(getClass().getResource("fxml/SetPlayerTwo.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void getPlayerOneName (ActionEvent event) throws IOException, GameNotStartedException {
        NamePlayerOne = PlayerOneNameInput.getText();
        System.out.println(NamePlayerOne);
        if (PlayerOneMode)
        {
            root = FXMLLoader.load(getClass().getResource("fxml/TicTacGUI.fxml"));

        } else if(OnlinePlayMode)
        {
            // start an online client obj and connect to server
            String ip = "localhost";
            int port = 60;

            // make a client connection
            Socket socket = new Socket(ip, port);

            Client client = new Client(socket, NamePlayerOne);

            // send client name !!!
            ClientInformationMsg msg = new ClientInformationMsg();
            msg.username = NamePlayerOne;
            client.sendMessage(msg);

            client.listenForMessage(); // start listening thread

            // do test button thingies
            if(TESTCreatingGame)
                client.startGameTest();
            else if(TESTJoiningGame)
                client.joinGameTest();

            root = FXMLLoader.load(getClass().getResource("fxml/TicTacGUI.fxml"));
        }
        else
        {
            root = FXMLLoader.load(getClass().getResource("fxml/SetName2.fxml"));
        }
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

        // Ensure scene is not null when being passed
        if(PlayerOneMode == true) {
            Logic GameLogic = Logic.getInstance();
            GameLogic.startGame(scene, NamePlayerOne, null, PlayerOneMode);  // Start the game with custom name for player 1 and default player 2 name.

        }

    }

    @FXML
    public void getPlayerTwoName (ActionEvent event) throws IOException, GameNotStartedException {

        PlayerOneMode = false;
        NamePlayerTwo = PlayerTwoNameInput.getText();
        root = FXMLLoader.load(getClass().getResource("fxml/TicTacGUI.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

        Logic GameLogic = Logic.getInstance();
        GameLogic.startGame(scene, NamePlayerOne, NamePlayerTwo, PlayerOneMode); // Start game with custom names.

    }

    /// temp switches to board until credits are implemented
    @FXML
    public void switchToCredits(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("fxml/Credits.fxml"));
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    // quit game
    @FXML
    public void logout(ActionEvent event) throws IOException {
        stage = (Stage) startMenu.getScene().getWindow();
        stage.close();
    }

    /**
     * Restarts the game when the restart button is pressed.
     * @param event An action event
     * @throws Exception
     */
    @FXML
    private void newGame(ActionEvent event) throws Exception {
        Logic GameLogic = Logic.getInstance();
        GameLogic.resetGame();
    }

    /**
     * Goes to the main menu when the button is pressed.
     * @param event
     * @throws Exception
     */
    @FXML
    private void goToMainMenu(ActionEvent event) throws Exception {
        Logic GameLogic = Logic.getInstance();
        GameLogic.endGame();   // Ends the game.

        root = FXMLLoader.load(getClass().getResource("fxml/StartMenuGUI.fxml"));
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Gets the current button and checks if there is an existing "X" or "O"
     * If there is, place a corresponding X or O and increment TicTacToeBoard.turn.
     * If not, do nothing
     * @param event An action event (must be a Button)
     * @throws Exception
     */
    @FXML
    private void checkCurrentButton(ActionEvent event) throws Exception {

        Logic GameLogic = Logic.getInstance();
        // goto implementation to see new advancedTurn documentation
        GameLogic.advanceTurn((Button)event.getSource());
    }


    public void moveButtonTest(MouseEvent event) // rename to create game test button
    {
        System.out.println("Button was clicked");

        try {
            // launch online mode and collect username scene
            OnlinePlayMode = true;
            TESTCreatingGame = true;
            root = FXMLLoader.load(getClass().getResource("fxml/SetPlayerOne.fxml"));
            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

            //client.sendMessage("", null);   // empty message notifies server that client is connected.
        } catch (IOException e) {
            //handler.displayInternalMessage("Could not connect to server!");
        }


    }

    public void joinGameClicked(MouseEvent event) {
        try {
            // launch online mode and collect username
            OnlinePlayMode = true;
            TESTJoiningGame = true;
            root = FXMLLoader.load(getClass().getResource("fxml/SetPlayerOne.fxml"));
            stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

            //client.sendMessage("", null);   // empty message notifies server that client is connected.
        } catch (IOException e) {
            //handler.displayInternalMessage("Could not connect to server!");
        }
    }
}