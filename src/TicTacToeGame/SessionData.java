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

    private PlayerObject player1 = new PlayerObject();            // Sender of the SessionData
    private PlayerObject player2 = new PlayerObject();            // The receiver for the object.
    private PlayerObject turn = new PlayerObject();               // The player whose turn it is.  
    private int xPos;                       // X position of the move
    private int yPos;                       // Y position of the move

    private PlayerObject winner = new PlayerObject();             // A winner of the game (if applicable).
    private boolean stalemate = false;       // Whether or not the game is a stalemate.
    private boolean gameRunning = false;     // Whether or not the game is running.

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
        turn = me;
        xPos = x;
        yPos = y;
        gameRunning = true;
    }

    /**
     * Called by the {@linkplain GameHandler} to change appropriate flags in this {@linkplain SessionData} object.
     * <p> Sets {@code gameRunning} to true and the {@code turn} to player1. Also ensures {@code winner} is null, and 
     * {@code stalemate} is false.
     */
    public void startGame() {

        if(gameRunning)
            return;
        
        gameRunning = true;
        turn = player1;             // NOTE: Here, we can add the ability to randomly choose starting player.
        winner = null;
        stalemate = false;
    }

    /**
     * Checks if the game is running
     * @return true if the game is running, false otherwise
     */
    public boolean isRunning() {
        return gameRunning;
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
     * Sets Player1 in the game.
     * @param player1
     */
    public void setPlayer1(PlayerObject player) {
        player1.setName(player.getName());
        player1.setID(player.getID());
        player1.setPawn(player.getPawn());
    }

    /**
     * Sets Player2 in the game.
     * @param player2
     */
    public void setPlayer2(PlayerObject player) {
        player2.setName(player.getName());
        player2.setID(player.getID());
        player2.setPawn(player.getPawn());
    }

    /**
     * Sets the current player's turn.
     * @param player
     */
    public void setCurrentTurn(PlayerObject player) {
        turn.setName(player.getName());
        turn.setID(player.getID());
        turn.setPawn(player.getPawn());
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
        xPos = x;
    }

    /**
     * Sets the Y position of the move
     * @param y
     */
    public void setYPos(int y) {
        yPos = y;
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
        this.winner = winner;
    }

    /**
     * Sets the stalemate flag to true.
     */
    public void setStalemate() {
        stalemate = true;
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