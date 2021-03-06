package TicTacToeGame.controllers;

import java.io.IOException;
import java.io.PrintWriter;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


public class LocalPlayController {
    
    @FXML TextField player1name, player2name;
    @FXML CheckBox isAI;
    @FXML Button startButton, leaveButton;

    static boolean onePlayerGame = false;
    static String aiName;
    static String p1Name;
    static String p2Name;

    /**
     * Initializes the controller class.
     */
    @FXML public void initialize() {
        startButton.disableProperty().bind(
            Bindings.createBooleanBinding(() -> player1name.getText().trim().isEmpty(), player1name.textProperty())
            .or(Bindings.createBooleanBinding(() -> player2name.getText().trim().isEmpty(), player2name.textProperty()))
        );
    }

    /**
     * Starts a game locally.
     * @apiNote Still has to be implemented.
     * @param event
     * @throws IOException
     */
    @FXML public void startLocalGame(ActionEvent event) throws IOException {

        // Write player name to a temporary file as I cannot figure out how to pass data to other controllers within code.
        PrintWriter out = new PrintWriter("./src/TicTacToeGame/PLAYERDATA.txt");
        out.println(player1name.getText());      // Write player name to line 1
        out.println();       // Write IP address to line 2. (If hosting, this line will be blank).
        out.close();

        // Attempt to load the next scene. The scene will not load if the connection cannot be made.
        try {
            if(isAI.isSelected()) {
                onePlayerGame = true;
                aiName = player2name.getText();
                Parent root = FXMLLoader.load(getClass().getResource("../fxml/TicTacGUI.fxml"));
                Stage window = (Stage) startButton.getScene().getWindow();
                window.setScene(new Scene(root));
            }
            else {
                p1Name = player1name.getText();
                p2Name = player2name.getText();
                Parent root = FXMLLoader.load(getClass().getResource("../fxml/LocalTicTacGUI.fxml"));
                Stage window = (Stage) startButton.getScene().getWindow();
                window.setScene(new Scene(root));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets player two as AI
     * @param event
     */
    @FXML public void setTwoAsAI(ActionEvent event) {
        String[] names = {"Matt", "Nika", "Elihas"};

        Platform.runLater(() -> {
            if (isAI.isSelected()) {
                player2name.setText("AI " + names[(int) (Math.random() * names.length)]);
                player2name.setDisable(true);
            } else {
                player2name.setText("");
                player2name.setDisable(false);
            }
        });
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

    public static boolean isOnePlayerGame() {
        return onePlayerGame;
    }

    public static String getAiName() {
        return aiName;
    }

    public static String getP1Name() {
        return p1Name;
    }

    public static String getP2name() {
        return p2Name;
    }
}
