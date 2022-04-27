package TicTacToeGame.controllers;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class TicTacBoardController {
    
    @FXML Label PlayerDisplay1, PlayerDisplay2, gameStatus;
    @FXML Button menuBtn2, restartBtn2, quitGame, square11, square12, square13, square21, square22, square23, square31, square32, square33;
    @FXML GridPane gameBoard;

    /**
     * This will broadcast the button pressed to the server.
     * @param event
     * @throws IOException
     */
    @FXML public void checkCurrentButton(ActionEvent event) throws IOException {
        
    }

    /**
     * End the game and go to main menu.
     * @param event
     * @throws IOException
     */
    @FXML public void goToMainMenu(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("../fxml/SetPlayerOne.fxml"));
        Stage window = (Stage) menuBtn2.getScene().getWindow();
        window.setScene(new Scene(root));
    }

    @FXML public void newGame(ActionEvent event) throws IOException {
        
    }


    /**
     * Logout of the game.
     * @param event
     * @throws IOException
     */
    @FXML public void logout(ActionEvent event) throws IOException {
        Stage window = (Stage) quitGame.getScene().getWindow();
        window.close();
    }
}
