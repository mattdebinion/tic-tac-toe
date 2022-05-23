package TicTacToeGame;
import java.io.Serializable;


/**
 * A SessionData object contains information about a game session. It contains the sender, the session ID,
 * and the location pressed on the Game Board.
 * 
 * @author Matt De Binion
 */
public class SessionData implements Serializable {

    private static final long serialVersionUID = 1L;

    private PlayerObject player1;            // Sender of the SessionData
    private PlayerObject player2;            // The receiver for the object.
    private PlayerObject lastTurn;          // The previous player's turn.
    private PlayerObject turn;               // The player whose turn it is.  
    private int xPos;                       // X position of the move
    private int yPos;                       // Y position of the move

    private PlayerObject winner;             // A winner of the game (if applicable).
    private boolean stalemate = false;       // Whether or not the game is a stalemate.
    private boolean gameRunning = false;     // Whether or not the game is running.
    private boolean reset = false;           // Let the server know that the game wants to been reset.

    /**
     * Creates an empty SessionData object.
     */
    public SessionData() {}

    /**
     * Creates a SessionData object for the sole purpose of passing it to the server.
     * @param me
     * @param x
     * @param y
     */
    public SessionData(PlayerObject me, int x, int y) {
        this.turn = me;
        this.xPos = x;
        this.yPos = y;
        this.gameRunning = true;
    }

    /**
     * Called by the {@linkplain GameHandler} to change appropriate flags in this {@linkplain SessionData} object.
     * <p> Sets {@code gameRunning} to true and the {@code turn} to player1. Also ensures {@code winner} is null, and 
     * {@code stalemate} is false.
     */
    public void startGame() {

        if(gameRunning)
            return;
        
        this.gameRunning = true;
        this.turn = player1;             // NOTE: Here, we can add the ability to randomly choose starting player.
        this.winner = null;
        this.stalemate = false;
    }

    /**
     * Checks if the game is running
     * @return true if the game is running, false otherwise
     */
    public boolean isRunning() {
        return gameRunning;
    }

    /**
     * Checks if the game needs to be reset
     * @return true if reset needs to happen, false otherwise.
     */
    public boolean checkForReset() {
        return reset;
    }

    /**
     * Gets Player1 in the game.
     * @return
     */
    public PlayerObject getPlayer1() {
        return player1;
    }

    /**
     * Gets Player2 in the game.
     * @return
     */
    public PlayerObject getPlayer2() {
        return player2;
    }

    /**
     * Gets the current player's turn.
     * @return
     */
    public PlayerObject getCurrentTurn() {
        return turn;
    }

    /**
     * Returns the last player's turn.
     * @return
     */
    public PlayerObject getLastTurn() {
        return lastTurn;
    }

    /**
     * Sets Player1 in the game.
     * @param player
     */
    public void setPlayer1(PlayerObject player) {
        this.player1 = player;
    }

    /**
     * Sets Player2 in the game.
     * @param player
     */
    public void setPlayer2(PlayerObject player) {
        this.player2 = player;
    }

    /**
     * Sets the current player's turn.
     * @param player
     */
    public void setCurrentTurn(PlayerObject player) {
        lastTurn = turn;
        this.turn = player;
    }

    /**
     * Gets the X position of the move
     * @return
     */
    public int getXPos() {
        return xPos;
    }

    /**
     * Gets the Y position of the move
     * @return
     */
    public int getYPos() {
        return yPos;
    }

    /**
     * Sets the X position of the move
     * @param x
     */
    public void setXPos(int x) {
        this.xPos = x;
    }

    /**
     * Sets the Y position of the move
     * @param y
     */
    public void setYPos(int y) {
        this.yPos = y;
    }

    /**
     * Gets the winner of the game, if any.
     * @return A {@linkplain PlayerObject} if there is a winner, null otherwise.
     */
    public PlayerObject getWinner() {
        return winner;
    }

    /**
     * Checks if there is a stalemate within the game
     * @return true if there is a stalemate, false otherwise.
     */
    public boolean seeIfStalemate() {
        return stalemate;
    }

    /**
     * Set the winner of the game.
     * @param winner
     */
    public void setWinner(PlayerObject winner) {
        lastTurn = winner;
        this.winner = winner;
    }

    /**
     * Sets the stalemate flag to true.
     */
    public void setStalemate() {
        stalemate = true;
    }

    /**
     * Updates the reset flag to true to let all clients know to clear their boards and lock the reset button.
     */
    public void resetGame() {
        this.reset = true;
    }
    
    /**
     * Outputs to terminal what is inside this session data object.
     */
    public void debugSessionData() {
        System.out.println("\n========== SESSION DATA OBJECT INFORMATION ==========");
        if(player1 == null) {
            System.out.print("Player 1: NULL");
        } else {
            System.out.print("Player 1: " + player1.getName());
        }
        System.out.print(" vs ");
        if(player2 == null) {
            System.out.println("Player 2: NULL");
        } else {
            System.out.println("Player 2: " + player2.getName());
        }

        if(lastTurn == null) {
            System.out.println("Last Turn: NULL");
        } else {
            System.out.println("Last Turn: " + lastTurn.getName());
        }

        if(turn == null) {
            System.out.println("Turn: NULL");
        } else {
            System.out.println("Turn: " + turn.getName());
        }
        System.out.println("Game running status: " + gameRunning);
        System.out.println("Stalemate status: " + stalemate);
        System.out.println("Associated coordinates: " + xPos + ", " + yPos);

        if(winner == null) {
            System.out.println("Winner: none");
        } else {
            System.out.println("Winner: " + winner.getName());
        }
        System.out.println("=====================================================\n");
    }

}