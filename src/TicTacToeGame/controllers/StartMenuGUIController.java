package TicTacToeGame.controllers;

import java.io.IOException;

import javafx.application.Platform;
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

    @FXML private void initialize() {
        // Handle case when user X out the window instead of pressing quit. Add this to runLater as it should be
        // added once stage is initalized.
        Platform.runLater(() -> {
            Stage stage = (Stage) playGame.getScene().getWindow();
            stage.setOnCloseRequest(arg -> {
                try {
                    Platform.exit();
                    System.exit(0);
                } catch (Exception e) {
                    System.out.println("Unable to handle logout." + e.getMessage());
                }
            });
        });
    }
    
    @FXML
    Button playGame, creditButton, quitButton;
    
    @FXML public void SetGameMode(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("../fxml/SetGameMode.fxml"));
        Stage window = (Stage) playGame.getScene().getWindow();
        window.setScene(new Scene(root));
    }

    @FXML public void switchToCredits(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("../fxml/Credits.fxml"));
        Stage window = (Stage) playGame.getScene().getWindow();
        window.setScene(new Scene(root));
    }

    @FXML public void logout(ActionEvent event) throws IOException {
        Stage window = (Stage) quitButton.getScene().getWindow();
        window.close();
    }
}
