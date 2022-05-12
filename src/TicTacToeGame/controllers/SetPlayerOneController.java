package TicTacToeGame.controllers;
import java.io.IOException;
import java.io.PrintWriter;

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

        PrintWriter out = new PrintWriter("./src/TicTacToeGame/PLAYERDATA.txt");
        out.print(PlayerOneNameInput.getText());
        out.close();
        
        Parent root = FXMLLoader.load(getClass().getResource("../fxml/TicTacGUI.fxml"));
        Stage window = (Stage) PlayerOneNameInput.getScene().getWindow();
        window.setScene(new Scene(root));
    }

}
