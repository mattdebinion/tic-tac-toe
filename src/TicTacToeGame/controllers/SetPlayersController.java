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
 * The StartPlayers controller handles actions within the Start Players scene.
 * 
 * @author Nika Daroui
 * @author Matt De Binion
 * @author Elihas Temori
 */
public class SetPlayersController {
    
    @FXML
    Button onePlayerMode, twoPlayerMode, quitButton;
    static boolean onePlayer = false;
    
    /**
     * PlayerOneMode will start as a Local LAN game.
     * @param event
     * @throws IOException
     */
    @FXML public void PlayerOneMode(ActionEvent event) throws IOException {
        onePlayer = true;
        Parent root = FXMLLoader.load(getClass().getResource("../fxml/SetPlayerOne.fxml"));
        Stage window = (Stage) onePlayerMode.getScene().getWindow();
        window.setScene(new Scene(root));
    }

    /**
     * PlayerTwoMode will start as a local coop game.
     * @apiNote AT THE MOMENT, this will launch a local LAN game!
     * @param event
     * @throws IOException
     */
    @FXML public void PlayerTwoMode(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("../fxml/SetPlayerOne.fxml"));
        Stage window = (Stage) onePlayerMode.getScene().getWindow();
        window.setScene(new Scene(root));
    }

    /**
     * Logout of the game.
     * @param event
     * @throws IOException
     */
    @FXML public void logout(ActionEvent event) throws IOException {
        Stage window = (Stage) quitButton.getScene().getWindow();
        window.close();
    }

    public static boolean isOnePlayerGame() {
        return onePlayer;
    }
}
