package TicTacToeGame.exceptions;

/**
 * The GameNotStartedException occurs when the TicTacToe board flag is not set to TRUE.
 * 
 * To resolve this, run the startGame method
 */
public class GameNotStartedException extends Exception {
    
    public GameNotStartedException(String message) {
        super(message);
    }
}
