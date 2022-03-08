package TicTacToeGame;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Runs the Tic Tac Toe game for our CS 4A class
 * 
 * @author Matt
 * @author Elihas
 * @author Nika
 * 
 * @please-read-this-note I am not sure if changing the main class will cause an error for you guys but in my case, make sure to change
 * your main class launch to TicTacToeGame.run instead of TicTacToeGame.TicTacToe
 */
public class run extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {

        Parent root = FXMLLoader.load(getClass().getResource("fxml/StartMenuGUI.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("stylesheets/style.css").toExternalForm());



        stage.setTitle("Tic Tac Toe");
        stage.setScene(scene);
        stage.show();
    }
}
