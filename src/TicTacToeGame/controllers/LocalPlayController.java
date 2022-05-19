package TicTacToeGame.controllers;

import java.io.IOException;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LocalPlayController {
    
    @FXML TextField player1name, player2name;
    @FXML CheckBox isAI;
    @FXML Button startButton, leaveButton;

    /**
     * Starts a game locally.
     * @apiNote Still has to be implemented.
     * @param event
     * @throws IOException
     */
    @FXML public void start(ActionEvent event) throws IOException {
        // Parent root = FXMLLoader.load(getClass().getResource("../fxml/TicTacGUI.fxml"));
        // Stage window = (Stage) startButton.getScene().getWindow();
        // window.setScene(new Scene(root));
    }

    /**
     * Sets player two as AI
     * @param event
     */
    @FXML public void setTwoAsAI(ActionEvent event) {
        String[] names = {"Matt", "Nika", "Elihas"};

        Platform.runLater(() -> {
            if (isAI.isSelected()) {
                player2name.setText("AI " + names[(int) (Math.random() * names.length)]);
                player2name.setDisable(true);
            } else {
                player2name.setText("");
                player2name.setDisable(false);
            }
        });
    }

    /**
     * Backs out to the main menu.
     * @param event
     * @throws IOException
     */
    @FXML public void leave(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("../fxml/StartMenuGUI.fxml"));
        Stage window = (Stage) startButton.getScene().getWindow();
        window.setScene(new Scene(root));
    }
}
