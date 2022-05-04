package TicTacToeGame.exceptions;

/**
 * The GameNotStartedException occurs when there is no connected PlayerObject within a game.
 * 
 * To resolve this, ensure you are connected to a game.
 */
public class GameNotStartedException extends Exception {
    
    public GameNotStartedException(String message) {
        super(message);
    }
}
