package TicTacToeGame;

import TicTacToeGame.exceptions.GameNotStartedException;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;

/**
 * Implementation for Tic Tac Toe board logic.
 * 
 * @author Matt De Binion
 */
public class TicTacToeBoard {
    
    private static TicTacToeBoard INSTANCE = null;
    private boolean running;
    private boolean ended;
    public boolean localMultiplayer;       // Set to FALSE to enable AI.

    public int turn = 0;
    private int[][] boardState;
    private Player playerOne;
    private Player playerTwo;

    private Scene gameScene;
    private GridPane guiBoardState;
    private Button resetButton;
    private Label statusLabel;
    private Label PlayerNamePlate1;
    private Label PlayerNamePlate2;


    /**
     * Creates an empty Tic Tac Toe board.
     * 
     * To start a game, run the startGame function.
     */
    private TicTacToeBoard() {
        boardState = new int[3][3];
    }
    /**
     * Starts the game. It is recommended to run this before starting the game.
     * @param scene The current game scene
     * @param pOneName The name of player one. Set to null if no name specified.
     * @param pTwoName The name of player two. Set to null if no name specified.
     * @throws GameNotStartedException
     * @throws MaximumPlayersException Occurs when maximum players have been reached (no more than 2).
     */
    public void startGame(Scene scene, String pOneName, String pTwoName, boolean mode) throws GameNotStartedException {
        if (mode)
            localMultiplayer = false; // if player one mode active, activate AI
        else
            localMultiplayer = true; // player two mode
        System.out.println("The game is starting!");
        running = true;                     // Starts the game
        ended = false;
        boardState = new int[3][3];         // Initalize a new empty board.

        // Set the GUI buttons to be globally accessible.
        gameScene = scene;
        guiBoardState = (GridPane) gameScene.lookup("#GameBoard");
        resetButton = (Button) gameScene.lookup("#restartBtn2");
        statusLabel = (Label) gameScene.lookup("#gameStatus");

        // setting custom player names to the board
        if (pOneName != null) {
            if (pOneName.length() >= 1) {
                playerOne = new Player(pOneName);
            } else {
                playerOne = new Player();
            }
        } else {
            playerOne = new Player();
        }

        // If the game is localMultiplayer, set P2 to a regular Player. If not, set P2 to an AIPlayer.
        PlayerNamePlate1 = (Label)gameScene.lookup("#PlayerDisplay1");
        PlayerNamePlate2 = (Label)gameScene.lookup("#PlayerDisplay2");
        if (localMultiplayer) { // if two player mode
            if (pTwoName != null) {
                if (pTwoName.length() >= 1) {
                    playerTwo = new Player(pTwoName);
                } else
                    playerTwo = new Player();
            } else {
                playerTwo = new Player();
            }
        } else{ // one player mode
            playerTwo = new AIPlayer();
    }

        // Display names onto board
        PlayerNamePlate1.setText("PLAYER ONE: " + String.valueOf(playerOne.getPlayerName()));
        PlayerNamePlate2.setText("PLAYER TWO: " + String.valueOf(playerTwo.getPlayerName()));
    }

    /**
     * Advances the current turn. For each turn, we determine:
     * <ol>
     * <li> If the current player can place their X or O in the selected space </li>
     * <li> If the current player makes a winning placement</li>
     * <li> If board has been completely filled (cant advance after that)</li>
     * </ol>
     * 
     * An AIPlayer can advance the turn if it finds a valid square placement.
     * @param currentButton The current button that was pressed.
     * @param event An ActionEvent object.
     * @return True if the turn has been advanced and false otherwise.
     * @throws GameNotStartedException Occurs when the started flag is FALSE.
     */
    public boolean advanceTurn(Button pressedButton) throws GameNotStartedException {

        if(!running)
            throw new GameNotStartedException("The game is not running.");

        // Get button from ActionEvent and see if player can place an X or an O.
        if(pressedButton.getText().length() != 1) {
            statusLabel.setText("");
            
            // If the next player is an AI, disable grid to prevent clicking.
            if(getNextPlayer().isAI()) {
                guiBoardState.setDisable(true);
                statusLabel.setText("");
            } else {
                guiBoardState.setDisable(false);
                statusLabel.setText("");
            }


            if(getCurrentPlayer().getPlayerID() == 1) {
                Font f = Font.font("Bookman Old Style", FontWeight.EXTRA_BOLD, 64);
                pressedButton.setFont(f);
                pressedButton.setText("X");
                pressedButton.setStyle("-fx-text-fill: green");
            }
            else {
                Font f = Font.font("Bookman Old Style", FontWeight.EXTRA_BOLD, 64);
                pressedButton.setFont(f);
                pressedButton.setText("O");
                pressedButton.setStyle("-fx-text-fill: red");
            }

            // Now place current X or O into the boardState.
            boardState[getPositionRow(pressedButton)][getPositionColumn(pressedButton)] = getCurrentPlayer().getPlayerID(); // Place player token into the board state.

            // Check for a win. If a win has been made, return false.
            if(checkForWin(pressedButton) == 1) {

                // strike through line after win for each possible condition - come back to this
                // Line winLine = (Line) gameScene.lookup("#gameStatus");
                //winLine.setDisable(false);

                // Disable the GridPane and declare winner.
                Font winnerFont = Font.font("American Typewriter", FontWeight.EXTRA_BOLD, 22);
                statusLabel.setFont(winnerFont);
                statusLabel.setText("\tPlayer " + getCurrentPlayer().getPlayerName() + " has won the game!");
                guiBoardState.setDisable(true);
                resetButton.setDisable(false);
                ///

                ended = true;
                return false;

            // Check to see if maximum turns have been reached.
            } else if (checkForWin(pressedButton) == 0) {

                // Disable the GridPane.
                Font winnerFont = Font.font("American Typewriter", FontWeight.EXTRA_BOLD, 22);
                statusLabel.setFont(winnerFont);
                statusLabel.setText("\tIt's a draw!");
                guiBoardState.setDisable(true);
                resetButton.setDisable(false);

                ended = true;
                return false;
            }

            // Return true as the turn as been advanced with no declared winner yet.
            turn++;
            return true;
        }
        
        // Return false as player selected a occupied square.
        return false;
    }

    /**
     * Checks if the current player has won the game by getting three X or O in a row in three orientations.
     * They can be vertical, horizontal, or diagonal.
     * @param currentButton The current pressed button.
     * @return 1 if current player won, 0 if maximum turns have been reached, and -1 otherwise.
     * @throws GameNotStartedException Occurs when the started flag is FALSE.
     */
    public int checkForWin(Button currentButton) throws GameNotStartedException {
        
        if(!running)
            throw new GameNotStartedException("The game is not running.");
            
        //Check for a vertical win (if X1, X2, X3 are the same)
        for(int col = 0; col < 3; col++) {

            if(boardState[0][col] == getCurrentPlayer().getPlayerID()
                && boardState[1][col] == getCurrentPlayer().getPlayerID()
                && boardState[2][col] == getCurrentPlayer().getPlayerID())
                    return 1;
        }
        // Check for horizontal win (if 1X, 2X, 3X are the same)
        for(int row = 0; row < 3; row++) {

            if(boardState[row][0] == getCurrentPlayer().getPlayerID()
                && boardState[row][1] == getCurrentPlayer().getPlayerID()
                && boardState[row][2] == getCurrentPlayer().getPlayerID())
                    return 1;
        }
        // Check for both diagonal orentations (if 11, 22, 33 OR 31, 22, 13 are the same)
        if(boardState[0][0] == getCurrentPlayer().getPlayerID()
            && boardState[1][1] == getCurrentPlayer().getPlayerID()
            && boardState[2][2] == getCurrentPlayer().getPlayerID())
                return 1;

        if(boardState[2][0] == getCurrentPlayer().getPlayerID()
            && boardState[1][1] == getCurrentPlayer().getPlayerID()
            && boardState[0][2] == getCurrentPlayer().getPlayerID())
                return 1;

        // Return 0 if the turn advances to 9 as there can only be at most 9 valid moves.
        if(turn >= 8)
            return 0;
        
        // Return -1 otherwise.
        return -1;
    }

    /**
     * Resets the game.
     * @throws GameNotStartedException Occurs when the started flag is FALSE.
     */
    public void resetGame() throws GameNotStartedException {

        if(!running)
            throw new GameNotStartedException("The game is not running.");

        turn = 0;
        boardState = new int[3][3];
        ended = false;

        // Iterate through GridPane and set them all blank.
        for(int m = 0; m < 3; m++) {
            for(int n = 0; n < 3; n++) {
                for(Node node : guiBoardState.getChildren()) {
                    
                    Button btn = (Button)node;
                    btn.setText("");
                }
            }
        }
        
        guiBoardState.setDisable(false);
        resetButton.setDisable(true);
    }

    /**
     * Ends the game by setting the internal board state, playerOne, and playerTwo to null.
     * @throws GameNotStartedException Occurs when the started flag is FALSE.
     */
    public void endGame() throws GameNotStartedException {

        System.out.println("The game is ending!");
        resetGame(); // Reset the game board to a fresh, clean slate.

        running = false;
        ended = true;
        // Set player fields to null to ensure garbage collection.
        playerOne = null;
        playerTwo = null;
    }


    /**
     * WORK IN PROGRESS! Gets the button name equivalent from the board. For example, the square at boardState[0][0] is the same as
     * square11 in the UI.
     * @param row The square's row position in the board matrix
     * @param column The square's column position in the board matrix
     * @return The name of the button. Returns null if the button does not exist.
     */
    public String getSquareName(int row, int column) {

        if(row < 0 && row > 2)
            return null;

        if(column < 0 && column > 2)
            return null;

        return "square" + row + column;
    }

    /**
     * Gets the row position on the board based on the square's name. For example, the square named "square12" wiill return
     * a 0 as its in the 1st row.
     * @param squareButton A button object from the board.
     * @return An int representing the row.
     */
    public int getPositionRow(Button squareButton) {

        // Get current button ID and chop off the number portion of the ID. This always assume the button ID format has two numbers at the end of the string.
        String btn = squareButton.getId();
        int buttonID = Integer.parseInt(btn.substring(btn.length()-2, btn.length()));

        // Find equivalent row
        return (int) Math.floor(buttonID / 10) - 1;
    }

    /**
     * Gets the column position on the board based on the square's name. For example, the square named "square12" will return
     * a 1 as it's in the 2nd column.
     * @param squareButton A button object from the board.
     * @return An int representing the column.
     */
    public int getPositionColumn(Button squareButton) {

        // Get current button ID and chop off the number portion of the ID. This always assume the button ID format has two numbers at the end of the string.
        String btn = squareButton.getId();
        int buttonID = Integer.parseInt(btn.substring(btn.length()-2, btn.length()));

        // Find equivalent column
        return (buttonID - 11) % 10;
    }


    /**
     * Gets the current Player based on the turn of the game.
     * @return A Player object.
     */
    public Player getCurrentPlayer() {

        return (turn % 2 == 0) ? playerOne : playerTwo;
    }

    public Player getNextPlayer() {
        return (turn % 2 == 0) ? playerTwo : playerOne;
    }

    /**
     * Returns the AI player within the game.
     * @return An AIPlayer object.
     */
    public AIPlayer getAIPlayer() {

        if(playerOne.isAI())
            return (AIPlayer) playerOne;

        if(playerTwo.isAI())
            return (AIPlayer) playerTwo;

        return null;
    }

    /**
     * Creates or gets the current instance of the TicTacToeBoard.
     * @return The instance of the TicTacToe board.
     */
    public static TicTacToeBoard getInstance() {

        if(INSTANCE == null)
            INSTANCE = new TicTacToeBoard();

        return INSTANCE;
    }

    /**
     * Gets the Scene of the Tic Tac Toe board.
     * @return A Scene object.
     */
    public Scene getGameScene() {
        return gameScene;
    }

    /**
     * Determines if the game has ended.
     * @return A boolean
     */
    public boolean gameHasEnded() {
        return ended;
    }

    /**
     * Determine if the game has stopped running.
     * @return A boolean
     */
    public boolean gameIsRunning() {
        return running;
    }

    /**
     * Gets instance of boardState
     * @return A 2D array representing the state of the TTT board
     */
    public int[][] getBoardState() {
        return boardState;
    }

}
