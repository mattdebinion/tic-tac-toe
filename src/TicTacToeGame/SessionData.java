package TicTacToeGame;
import java.io.Serializable;


/**
 * A SessionData object contains information about a game session. It contains the sender, the session ID,
 * and the location pressed on the Game Board.
 * 
 * @author Matt De Binion
 */
public class SessionData implements Serializable {

    private PlayerObject player1;            // Sender of the SessionData
    private PlayerObject player2;           // The receiver for the object.
    private PlayerObject winner;            // A winner of the game (if applicable).
    private PlayerObject sender;            // The sender of this SessionData.
    private boolean gameRunning = false;

    private int xPos;                       // X position of the move
    private int yPos;                       // Y position of the move

    private int[][] senderBoardState;      // The board state of the sender.

    // Creates a new, empty SessionData object.
    public SessionData() {}

    public SessionData(PlayerObject player1, PlayerObject player2, int xPos, int yPos, boolean running) {
        this.player1 = player1;
        this.player2 = player2;
        this.xPos = xPos;
        this.yPos = yPos;
        this.gameRunning = running;
    }

    public PlayerObject getPlayer1() {
        return player1;
    }

    public PlayerObject getPlayer2() {
        return player2;
    }

    public PlayerObject getWinner() {
        return winner;
    }

    public int getXPos() {
        return xPos;
    }

    public void setXPos(int x) {
        xPos = x;
    }

    public int getYPos() {
        return yPos;
    }

    public void setYPos(int y) {
        yPos = y;
    }

    public int[][] getSenderBoardState() {
        return senderBoardState;
    }

    public void setBoardState(int[][] board) {
        senderBoardState = board;
    }

    public void setPlayer1(PlayerObject player1) {
        this.player1 = player1;
    }

    public void setPlayer2(PlayerObject player2) {
        this.player2 = player2;
    }

    public void setWinner(PlayerObject winner) {
        this.winner = winner;
    }

    public boolean isRunning() {
        return gameRunning;
    }

    public void startGame() {
        gameRunning = true;
    }

    public void setSender(PlayerObject sender) {
        this.sender = sender;
    }

    public PlayerObject getSender() {
        return sender;
    }

}