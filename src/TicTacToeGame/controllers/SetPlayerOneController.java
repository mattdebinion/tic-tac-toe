package TicTacToeGame.controllers;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class SetPlayerOneController {
    
    @FXML TextField PlayerOneNameInput;
    static String item;

    @FXML public void getPlayerOneName(ActionEvent event) throws IOException {

        Parent root = FXMLLoader.load(getClass().getResource("../fxml/TicTacGUI.fxml"));
        Stage window = (Stage) PlayerOneNameInput.getScene().getWindow();
        item = PlayerOneNameInput.getText();
        window.setScene(new Scene(root));

        // FXMLLoader loader = new FXMLLoader(getClass().getResource("../fxml/TicTacGUI.fxml"));
        // Parent root = loader.load();
        // Stage window = (Stage) PlayerOneNameInput.getScene().getWindow();
        // window.setScene(new Scene(root));
    }

    public static String getInputText() {
        return item;
    }
}
