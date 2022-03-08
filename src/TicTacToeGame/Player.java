package TicTacToeGame;

/**
 * A player object representing a player within the game.
 * 
 * @author Matt De Binion
 */
public class Player {
    
    protected String playerName;          // String containing a player name
    protected char pawnPiece;             // A char representing pawn piece.
    protected char oppPawnPiece;        // A char representing opponent's pawn piece
    protected boolean AI;               // States if the current player is an AI.
    private int playerID;               // The player ID depending on the global ID (playerID % 2 == 1 is player 1, otherwise player 2)
    private boolean active;             // States if the current player is an active player on the board.
    private static int globalID = -1;    // The global ID which represents how many of these objects have been created.

    /**
     * Creates a new player with default name and default pawn piece.
     */
    Player() {
        this(null);
    }

    /**
     * Creates a player with a custom username and default pawn piece.
     * @param name A username for the Player.
     */
    Player(String name) {
        
        this(name, '0');
    }

    /**
     * Creates a player with a custom username and custom pawn piece.
     * @param name
     * @param pawn
     */
    Player(String name, char pawn) {
        active = true;
        globalID++;

        if(globalID % 2 == 0) {
            playerID = 1;   // Player 1
        } else {
            playerID = 2;   // Player 2
        }

        if(name == null) {
            playerName = Integer.toString(playerID);
        } else {
            playerName = name;
        }

        if(pawn == '0') {
            if(playerID == 1) {
                pawnPiece = 'X';
                oppPawnPiece = 'O';
            } else {
                pawnPiece = 'O';
                oppPawnPiece = 'X';
            }
        } else {
            pawnPiece = pawn;
        }

        System.out.println("Created a player named " + playerName + " which is player number " + playerID + " from global ID " + globalID);
    }

    /**
     * Gets the player ID.
     * @return Returns the player ID, either 1 or 2.
     */
    public int getPlayerID() {
        return playerID;
    }

    /**
     * Gets the player name.
     * @return Player name as string.
     */
    public String getPlayerName() {
        return playerName;
    }

    /**
     * Gets the pawn piece as a String.
     * @return Pawn piece represented as string.
     */
    public String getPawnPiece() {
        return Character.toString(pawnPiece);
    }

    /**
     * Gets the opponent's pawn piece as a String.
     * @return Opponent's pawn piece represented as string.
     */
    public String getOppPawnPiece() {
        return Character.toString(oppPawnPiece);
    }

    /**
     * Determines if current player is active.
     * @return True if active, false if not.
     */
    public boolean isActivePlayer() {
        return active;
    }

    /**
     * Determines if the current player is an AI.
     * @return True is an AI, false if not.
     */
    public boolean isAI() {
        return AI;
    }
    
    /**
     * Deactivate a player. This sets their active state to false, their pawn to null, their ID to -1, and decrements global ID.
     */
    public void deactivatePlayer() {

        active = false;
        pawnPiece = '0';
        playerID = -1;
        globalID--;
    }
}
