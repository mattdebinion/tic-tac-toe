package TicTacToeGame.controllers;

import java.io.IOException;
import java.net.UnknownHostException;

import TicTacToeGame.Client;
import TicTacToeGame.PlayerObject;
import TicTacToeGame.exceptions.InvalidMoveException;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class TicTacBoardController {
    
    @FXML Label PlayerDisplay1, PlayerDisplay2, gameStatus;
    @FXML Button menuBtn2, restartBtn2, quitGame, square11, square12, square13, square21, square22, square23, square31, square32, square33;
    @FXML GridPane GameBoard;
    
    static Client associatedClient;

    @FXML
    public void initialize() throws UnknownHostException {

        System.out.println("Connecting...");
        associatedClient = new Client(this, new PlayerObject("ME", 'M', true));
        PlayerObject player1 = associatedClient.getPlayer1();
        
        PlayerDisplay1.setText("Player 1: ...");
        PlayerDisplay2.setText("Player 2: ...");
        changeBoardLock(true);
        updateStatusLabel("Waiting for player 2...");
    }

    /**
     * This will broadcast the button pressed to the server.
     * @param event
     * @throws IOException
     * @throws InvalidMoveException
     */
    @FXML public void checkCurrentButton(ActionEvent event) throws IOException, InvalidMoveException {
        
        Button squareButton = (Button) event.getSource();

        if(squareButton.isDisabled())
            return;
            
        int xCoord = 0, yCoord = 0;

        String btn = squareButton.getId(); // Get current button ID and chop off the number portion of the ID. This always assume the button ID format has two numbers at the end of the string.
        int buttonID = Integer.parseInt(btn.substring(btn.length()-2, btn.length()));

        // Find equivalent row and column from parsed buttonID.
        xCoord = (int) Math.floor(buttonID / 10) - 1;
        yCoord = (buttonID - 11) % 10;

        associatedClient.sendMove(xCoord, yCoord);
    }

    /**
     * End the game and go to main menu.
     * @param event
     * @throws IOException
     */
    @FXML public void goToMainMenu(ActionEvent event) throws IOException {
        Client.disconnect();
        Parent root = FXMLLoader.load(getClass().getResource("../fxml/StartMenuGUI.fxml"));
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

    /**
     * Given a PlayerObject, update the GUI to reflect the player's place.
     * @param x
     * @param y
     * @param player
     */
    public void updateBoardAt(int x, int y, PlayerObject player) {

        Platform.runLater(() -> {
            Font f = Font.font("Bookman Old Style", FontWeight.EXTRA_BOLD, 64);
            String btn = "square" + (x + 1) + (y + 1);
            Button squareButton = (Button) GameBoard.lookup("#" + btn);
            squareButton.setFont(f);
            squareButton.setStyle("-fx-text-fill: red");
            squareButton.setText(Character.toString(player.getPawn()));
            squareButton.setDisable(true);
        });
    }

    public void updateStatusLabel(String message) {
        Platform.runLater(() -> {
            gameStatus.setText(message);
        });

    }

    /**
     * Updates the board lock, unlocks squares that are empty.
     * @param state
     */
    public void changeBoardLock(boolean state) {
        Platform.runLater(() -> {
            square11.setDisable(square11.getText().length() >= 1 ? true : state);
            square12.setDisable(square12.getText().length() >= 1 ? true : state);
            square13.setDisable(square13.getText().length() >= 1 ? true : state);
            square21.setDisable(square21.getText().length() >= 1 ? true : state);
            square22.setDisable(square22.getText().length() >= 1 ? true : state);
            square23.setDisable(square23.getText().length() >= 1 ? true : state);
            square31.setDisable(square31.getText().length() >= 1 ? true : state);
            square32.setDisable(square32.getText().length() >= 1 ? true : state);
            square33.setDisable(square33.getText().length() >= 1 ? true : state);
        });
    }

    public void SetPlayer1(PlayerObject player) {
        Platform.runLater(() -> {
            PlayerDisplay2.setText("Player 1: " + player.getName());
        });
    }

    public void SetPlayer2(PlayerObject player) {
        Platform.runLater(() -> {
            PlayerDisplay1.setText("Player 2: " + player.getName());
        });
    }
}
