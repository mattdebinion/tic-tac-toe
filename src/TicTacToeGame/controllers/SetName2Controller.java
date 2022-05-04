package TicTacToeGame.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class SetName2Controller {
    @FXML Label EnterPlayTwoNameLabel;
    @FXML public TextField PlayerTwoNameInput;
    @FXML public Button submitButton1;
    static String playerTwoName;

    @FXML
    public void getPlayerTwoName(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("../fxml/TicTacGUI.fxml"));
        Stage window = (Stage) PlayerTwoNameInput.getScene().getWindow();
        playerTwoName = PlayerTwoNameInput.getText();
        window.setScene(new Scene(root));
    }

    public static String getPlayerTwoName() {
        return playerTwoName;
    }
}
