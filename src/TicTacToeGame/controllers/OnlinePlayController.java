package TicTacToeGame.controllers;

import java.io.IOException;
import java.io.PrintWriter;

import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Handles Online play functionality.
 * 
 * @author Matt De Binion
 */
public class OnlinePlayController {

    @FXML TextField playerName, ipAddress;
    @FXML Button startButton, leaveButton;
    @FXML Label connectionStatus;
    @FXML CheckBox isHosting;

    @FXML public void initialize() {
        startButton.disableProperty().bind(
            Bindings.createBooleanBinding(() -> ipAddress.getText().trim().isEmpty(), ipAddress.textProperty())
            .or(Bindings.createBooleanBinding(() -> playerName.getText().trim().isEmpty(), playerName.textProperty()))
        );

        LocalPlayController.onePlayerGame = false;
    }

    /**
     * Attempts to establish a connection if one can be made.
     * @param event
     * @throws IOException
     * @throws InterruptedException
     */
    @FXML public void connect(ActionEvent event) throws IOException, InterruptedException {

        connectionStatus.setText("Connecting, please wait...");

        // Write player name to a temporary file as I cannot figure out how to pass data to other controllers within code.
        PrintWriter out = new PrintWriter("./src/TicTacToeGame/PLAYERDATA.txt");
        out.println(playerName.getText());      // Write player name to line 1   
        out.println(ipAddress.getText());       // Write IP address to line 2. (If hosting, this line will be blank).
        out.close();

        // Attempt to load the next scene. The scene will not load if the connection cannot be made.
        try {

            Parent root = FXMLLoader.load(getClass().getResource("../fxml/TicTacGUI.fxml"));
            Stage window = (Stage) startButton.getScene().getWindow();
            window.setScene(new Scene(root));
        } catch (Exception e) {
            connectionStatus.setText("Connection failed.");
            ipAddress.setText("");
        }

    }

    /**
     * When the user checks the isHosting checkbox, it clears entered IP address and disables the text field.
     * Otherwise, it enables the text field.
     * @param event
     */
    @FXML public void setToHost(ActionEvent event) {

        if(isHosting.isSelected()) {
            ipAddress.setText("localhost");
            ipAddress.setStyle("-fx-text-fill: white");
            ipAddress.setDisable(true);
        } else {
            ipAddress.setText("");
            ipAddress.setStyle("-fx-text-fill: black");
            ipAddress.setDisable(false);
        }
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
