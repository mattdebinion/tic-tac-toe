package TicTacToeGame.controllers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import TicTacToeGame.AIPlayer;
import TicTacToeGame.Client;
import TicTacToeGame.PlayerObject;
import TicTacToeGame.Server;
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
    boolean aiGame = false;
    String aiName;
    
    static Client associatedClient;

    @FXML public void initialize() throws IOException, InterruptedException {

        BufferedReader br = new BufferedReader(new FileReader("./src/TicTacToeGame/PLAYERDATA.txt"));
        try {
            StringBuilder sb1 = new StringBuilder();
            StringBuilder sb2 = new StringBuilder();

            String getName = br.readLine();
            sb1.append(getName);
            String getIP = br.readLine();
            sb2.append(getIP);

            String name = getName.toString();
            String IPaddress = getIP.toString();

            // initializeHost is a blocking operation.
            Thread serverThread = new Thread(() -> {
                Server.initializeHost();
            });

            serverThread.start();

            // Sleep the current GUI thread for 3 second to allow the server to start.
            Thread.sleep(3000);

            System.out.println("Connecting to " + IPaddress + " with nickname " + name);
            associatedClient = new Client(this, new PlayerObject(name, true), IPaddress);
            
            PlayerDisplay1.setText("Player 1: ...");
            PlayerDisplay2.setText("Player 2: ...");
            changeBoardLock(true);
            updateStatusLabel("Waiting for player 2...");

            if(LocalPlayController.isOnePlayerGame()) {
                AIPlayer ai = new AIPlayer(LocalPlayController.getAiName());
            }

        } finally {
            br.close();

            File file = new File("./src/TicTacToeGame/PLAYERDATA.txt");
            file.delete();
        }
        
        // Handle case when user X out the window instead of pressing quit. Add this to runLater as it should be
        // added once stage is initalized.
        Platform.runLater(() -> {
            Stage stage = (Stage) GameBoard.getScene().getWindow();
            stage.setOnCloseRequest(arg -> {
                try {
                    arg.consume();
                    logout();
                    Platform.exit();
                    System.exit(0);
                } catch (Exception e) {
                    System.out.println("Unable to handle logout." + e.getMessage());
                }
            });
        });
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
        updateBoardAt(xCoord, yCoord, associatedClient.getMe());

    }

    /**
     * End the game and go to main menu.
     * @param event
     * @throws IOException
     * @throws InvalidMoveException
     */
    @FXML public void goToMainMenu(ActionEvent event) throws IOException, InvalidMoveException {

        try {
            associatedClient.sendMove(-3, -3); // Send a -3,-3 move that the sending player has left.
            Client.disconnect();                // Disconnect.
        } catch (Exception e) {
            System.out.println("Not connected.");
        }
        
        Parent root = FXMLLoader.load(getClass().getResource("../fxml/StartMenuGUI.fxml"));
        Stage window = (Stage) menuBtn2.getScene().getWindow();
        window.setScene(new Scene(root));
    }

    /**
     * Clears the board and awaits new moves.
     * @param event
     * @throws IOException
     * @throws InvalidMoveException
     */
    @FXML public void newGame(ActionEvent event) throws IOException, InvalidMoveException {
        
        restartBtn2.setDisable(true);
        clearBoard();
        associatedClient.sendMove(-2, -2); // Send a -2,-2 move that the sending player wants to restart the game.
    }

    /**
     * Completely leave the game.
     * @param event
     * @throws IOException
     * @throws InvalidMoveException
     */
    @FXML public void logout(ActionEvent event) throws IOException, InvalidMoveException {
        associatedClient.sendMove(-3, -3); // Send a -3,-3 move that the sending player has left.
        Client.disconnect();                // Disconnect.
        Stage window = (Stage) quitGame.getScene().getWindow();
        window.close();
    }

    /**
     * Completely leave the game.
     * @throws IOException
     * @throws InvalidMoveException
     */
    private void logout() throws InvalidMoveException, IOException {
        associatedClient.sendMove(-3, -3); // Send a -3,-3 move that the sending player has left.
        Client.disconnect();                // Disconnect.
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

        if(x < 0 || y < 0 || x > 2 || y > 2)
            return;
        if(player != null) {
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
    }

    /**
     * Clears the entire board.
     */
    public void clearBoard() {
        Platform.runLater(() -> {
            for(int i = 0; i < 3; i++) {
                for(int j = 0; j < 3; j++) {
                    Button square = (Button) GameBoard.getChildren().get(i*3 + j);
                    square.setText("");
                }
            }
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

    /**
     * Update the reset button lock.
     * @param state
     */
    public void changeResetButton(boolean state) {
        Platform.runLater(() -> {
            restartBtn2.setDisable(state);
        });
    }

    public void SetPlayer1(PlayerObject player) {
        Platform.runLater(() -> {

            if(player != null)
                PlayerDisplay1.setText("Player 1: " + player.getName() + " (" + player.getPawn() + ")");

        });
    }

    public void SetPlayer2(PlayerObject player) {
        Platform.runLater(() -> {

            if(player != null)
                PlayerDisplay2.setText("Player 2: " + player.getName() + " (" + player.getPawn() + ")");
                
        });
    }
}
