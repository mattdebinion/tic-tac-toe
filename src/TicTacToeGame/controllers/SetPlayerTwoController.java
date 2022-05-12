package TicTacToeGame.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class SetPlayerTwoController {

    @FXML public TextField PlayerOneNameInput;
    @FXML public Button submitButton;
    static String item;

    @FXML
    public void getPlayerOneName(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("../fxml/TicTacGUI.fxml"));
        Stage window = (Stage) PlayerOneNameInput.getScene().getWindow();
        item = PlayerOneNameInput.getText();
        window.setScene(new Scene(root));
    }

    public static String getInputText() {
        return item;
    }
}
