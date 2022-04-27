package TicTacToeGame;

import java.io.Serializable;

import TicTacToeGame.exceptions.InvalidMoveException;
import javafx.util.Pair;

/**
 * A SessionData object contains information about a game session. It contains the sender, the session ID,
 * and the location pressed on the Game Board.
 * 
 * @author Matt De Binion
 */
public class SessionData implements Serializable {

    private PlayerObject sender;            // Sender of the SessionData
    private PlayerObject receiver = null;   // The receiver for the object.
    private PlayerObject winner = null;     // The winner associated in the game.
    private boolean gameRunning = false;    // Flag to determine if the game is running.
    private boolean verified = false;       // Flag sent that will be set to true when the GameHandler has verified the session data.
    private int sessionID;                  // ID of the current session
    private int xPos;                       // X position of the move
    private int yPos;                       // Y position of the move

    /**
     * Creates a SessionData object that can be passed through the socket. This is read by the Game Handler.
     * @param sender [PlayerObject] The player object that sent the data.
     * @param receiver [PlayerObject] The player object that will be receiving this data.
     * @param sessionID [int] The session ID of the game.
     * @param xPos [int] The x position of the move.
     * @param yPos [int] The y position of the move.
     * @throws InvalidMoveException Occurs when an invalid move is passed in (aka a move that is not between 0 and 2).
     */
    public SessionData(PlayerObject sender, int sessionID, int xPos, int yPos) throws InvalidMoveException {
        this.sender = sender;
        this.sessionID = sessionID;
        this.xPos = xPos;
        this.yPos = yPos;
    }

    /**
     * Returns the PlayerObject that sent the data.
     * @return [PlayerObject] The player object that sent the data.
     */
    public PlayerObject getSender() {
        return sender;
    }

    /**
     * Returns the PlayerObject that will receive the data.
     * @return [PlayerObject] The player object that will receive the data.
     */
    public PlayerObject getReceiver() {
        return receiver;
    }

    /**
     * Sets the receiver for this sessionData.
     * @param receiver
     */
    public void setReceiver(PlayerObject receiver) {
        this.receiver = receiver;
    }

    /**
     * Returns the session ID associated with the session.
     * @return [int] The session ID.
     */
    public int sessionID() {
        return sessionID;
    }

    /**
     * Returns a pair of coordinates that represent the location of the move.
     * @return [Pair<Integer, Integer>] The x and y coordinates of the move.
     */
    public Pair<Integer, Integer> getMovePair() {
        return new Pair<>(xPos, yPos);
    }

    /**
     * The GameHandler will call this method if it determines that the given move is valid.
     */
    protected void verifySessionData() {
        verified = true;
    }

    /**
     * Checks to see if this SessionData has been verified by the Game Handler.
     * @return [boolean] True if verified, false otherwise.
     */
    public boolean checkVerification() {
        return verified;
    }

    /**
     * Gets a current winner of the game.
     * @return [PlayerObject] The winner of the game. Will be NULL if there is currently no winner.
     */
    public PlayerObject getWinner() {
        return winner;
    }
    
    /**
     * Determines if game is running in this session.
     * @return [boolean] True if game is running, false otherwise.
     */
    public boolean isRunning() {
        return gameRunning;
    }

    /**
     * Sets the game to running.
     */
    public void setRunning() {
        gameRunning = true;
    }

}
