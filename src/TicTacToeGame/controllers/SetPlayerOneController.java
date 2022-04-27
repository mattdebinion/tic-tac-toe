package TicTacToeGame.controllers;
import java.io.IOException;

import TicTacToeGame.Client;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class SetPlayerOneController {
    
    @FXML TextField PlayerOneNameInput;

    @FXML public void getPlayerOneName(ActionEvent event) throws IOException {
        Client.setName(PlayerOneNameInput.getText());

        FXMLLoader loader = new FXMLLoader(getClass().getResource("../fxml/TicTacGUI.fxml"));
        Parent root = loader.load();
        
        System.out.println("Attempting connection...");
        Client.connect(loader.getController());

        Stage window = (Stage) PlayerOneNameInput.getScene().getWindow();
        window.setScene(new Scene(root));
    }
}
