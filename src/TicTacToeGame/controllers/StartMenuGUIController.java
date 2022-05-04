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
 * The StartMenuGUI controller handles actions within the start menu.
 * 
 * @author Nika Daroui
 * @author Matt De Binion
 * @author Elihas Temori
 */
public class StartMenuGUIController {
    
    @FXML
    Button localGame, creditButton, quitButton;
    
    @FXML public void SetGameMode(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("../fxml/SetPlayers.fxml"));
        Stage window = (Stage) localGame.getScene().getWindow();
        window.setScene(new Scene(root));
    }

    @FXML public void switchToCredits(ActionEvent event) throws IOException {
        
    }

    @FXML public void logout(ActionEvent event) throws IOException {
        Stage window = (Stage) quitButton.getScene().getWindow();
        window.close();
    }
}
