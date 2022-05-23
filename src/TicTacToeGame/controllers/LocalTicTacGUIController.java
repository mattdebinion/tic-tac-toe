package TicTacToeGame.controllers;

import TicTacToeGame.MiniMax;
import TicTacToeGame.PlayerObject;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ResourceBundle;
import java.util.concurrent.ThreadLocalRandom;

public class LocalTicTacGUIController implements Initializable {

    @FXML Label PlayerDisplay1, PlayerDisplay2, gameStatus;
    @FXML Button menuBtn2, restartBtn2, quitGame, square11, square12, square13, square21, square22, square23, square31, square32, square33;
    @FXML GridPane GameBoard;
    PlayerObject p1;
    PlayerObject p2;
    char[][] boardState;
    int squaresFilled = 0;
    char currentPawn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        boardState = new char[3][3];
        for(int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                boardState[i][j] = ' ';
            }
        }

        setPlayers(LocalPlayController.getP1Name(), LocalPlayController.getP2name());

        boolean boolean_rand = ThreadLocalRandom.current().nextBoolean();

        if(boolean_rand) {
            gameStatus.setText(p1.getName() + " goes first!");
            currentPawn = 'X';
        }
        else {
            gameStatus.setText(p2.getName() + " goes first!");
            currentPawn = 'O';
        }

    }

    public void checkCurrentButton(ActionEvent event) {

        Button but = (Button) event.getSource();
        System.out.println(but.getId() + " was clicked");
        int xCoord = 0, yCoord = 0;

        String btn = but.getId(); // Get current button ID and chop off the number portion of the ID. This always assume the button ID format has two numbers at the end of the string.
        int buttonID = Integer.parseInt(btn.substring(btn.length()-2, btn.length()));

        // Find equivalent row and column from parsed buttonID.
        xCoord = (int) Math.floor(buttonID / 10) - 1;
        yCoord = (buttonID - 11) % 10;

        // Check board state to see if square is empty
        if(boardState[xCoord][yCoord] == ' ') {

            boardState[xCoord][yCoord] = currentPawn;

            Font f = Font.font("Bookman Old Style", FontWeight.EXTRA_BOLD, 64);
            but.setFont(f);
            but.setStyle("-fx-text-fill: red");
            but.setText(Character.toString(currentPawn));
            but.setDisable(true);

            if(currentPawn == 'X') {
                currentPawn = 'O';
                gameStatus.setText("It's " + p2.getName() + "'s turn!");
            }
            else {
                currentPawn = 'X';
                gameStatus.setText("It's " + p1.getName() + "'s turn!");
            }
            checkStatus();
        }
    }

    public void checkStatus() {
        if(MiniMax.checkCharBoardForWin(boardState)) {
            changeBoardLock(true);
            if(MiniMax.checkWin(boardState) == p1.getPawn()) {
                gameStatus.setText(p1.getName() + " HAS WON!");
            }
            else {
                gameStatus.setText(p2.getName() + " HAS WON!");
            }
            restartBtn2.setDisable(false);
        }
        if(MiniMax.checkCharBoardForStalemate(boardState))
            gameStatus.setText("IT'S A STALEMATE!");
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

    public void logout(ActionEvent event) {
        Stage window = (Stage) quitGame.getScene().getWindow();
        window.close();
    }

    public void newGame(ActionEvent event) {
        resetBoard();
    }

    public void goToMainMenu(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("../fxml/StartMenuGUI.fxml"));
        Stage window = (Stage) menuBtn2.getScene().getWindow();
        window.setScene(new Scene(root));
    }

    public void setPlayers(String p1Name, String p2Name) {
        PlayerDisplay1.setText("Player 1: " + p1Name + " ( X )");
        PlayerDisplay2.setText("Player 1: " + p2Name + " ( O )");
        try {
            p1 = new PlayerObject(LocalPlayController.getP1Name(), 'X', true);
            p2 = new PlayerObject(LocalPlayController.getP2name(), 'O', true);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    private void resetBoard() {
        boardState = new char[3][3];
        for(int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                boardState[i][j] = ' ';
            }
        }
        square11.setText("");
        square12.setText("");
        square13.setText("");
        square21.setText("");
        square22.setText("");
        square23.setText("");
        square31.setText("");
        square32.setText("");
        square33.setText("");

        restartBtn2.setDisable(true);
        gameStatus.setText("'X' goes first!");
        currentPawn = p1.getPawn();
        changeBoardLock(false);
    }
}
