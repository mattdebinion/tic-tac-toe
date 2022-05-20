package TicTacToeGame.controllers;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

/**
 * The SetPlayers controller handles actions within the Start Players scene.
 * 
 * @author Nika Daroui
 * @author Matt De Binion
 * @author Elihas Temori
 */
public class SetGameModeController {
    
    @FXML
    Button onlineMode, localMode, quitButton;
    
    /**
     * ONLINE button will start an Online Play game.
     * @param event
     * @throws IOException
     */
    @FXML public void onlineMode(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("../fxml/OnlinePlay.fxml"));
        Stage window = (Stage) onlineMode.getScene().getWindow();
        window.setScene(new Scene(root));
    }

    /**
     * LOCAL will start as a local coop game.
     * @apiNote THIS IS NOT FUNCTIONING ATM.
     * @param event
     * @throws IOException
     */
    @FXML public void localMode(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("../fxml/LocalPlay.fxml"));
        Stage window = (Stage) localMode.getScene().getWindow();
        window.setScene(new Scene(root));
    }

    /**
     * Logout of the game.
     * @param event
     * @throws IOException
     */
    @FXML public void logout(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("../fxml/StartMenuGUI.fxml"));
        Stage window = (Stage) localMode.getScene().getWindow();
        window.setScene(new Scene(root));
    }
}
