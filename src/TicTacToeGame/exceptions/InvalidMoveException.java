package TicTacToeGame.exceptions;

/**
 * The InvalidMoveException occurs when an invalid move is passed into the game.
 * 
 * To resolve this, ensure passed parameters are between 0 and 2.
 */
public class InvalidMoveException extends Exception {
    
    public InvalidMoveException(String message) {
        super(message);
    }
}
