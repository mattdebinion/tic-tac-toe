package TicTacToeGame;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;


/**
 * A PlayerObject class is a serializable object that contains the information about a player that is connected to the server.
 * <p> PlayerObjects, as of now, are immutable.
 * 
 * @author Matt De Binion
 */
public class PlayerObject implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    // PLAYER INFORMATION \\
    private String name;            // The name of the player object.
    private boolean isAI = false;   // Flag to determine if Controller should handle this PlayerObject as an AI player.
    private char pawn;              // The character representing the player's pawn.
    private int playerID;           // THe player ID
    private InetAddress playerIP;   // The local IP address of this player.

    /**
     * Creates a PlayerObject with no information.
     */
    public PlayerObject() {}

    /**
     * Creates a PlayerObject with the given name and associated IP.
     * @param name The name of the player
     * @param isHuman A flag determining if the player is an AI or not.
     * @throws UnknownHostException
     */
    public PlayerObject(String name, boolean isHuman) throws UnknownHostException {
        this.name = name;
        playerIP = InetAddress.getLocalHost();
    }
    
    /**
     * Creates a new PlayerObject with a name, pawn, and if player is human.
     * @param name The name of the player.
     * @param pawn The character representing the player's pawn.
     * @param isHuman True if the player is human, false if the player is an AI.
     * @throws UnknownHostException Occurs when the local IP address cannot be determined.
     */
    public PlayerObject(String name, char pawn, boolean isHuman) throws UnknownHostException {
        this.name = name;
        this.pawn = pawn;
        isAI = !isHuman;
        playerIP = InetAddress.getLocalHost();
    }

    /**
     * Retreive the name of the player.
     * @return The name of the player.
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of this player.
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    public void setID(int id) {
        playerID = id;
    }

    public int getID() {
        return playerID;
    }

    /**
     * Retrieve the flag to determine if the player is an AI.
     * @return True if the player is an AI, false if the player is human.
     */
    public boolean AIflag() {
        return isAI;
    }

    /**
     * Retrieve the character representing the player's pawn.
     * @return The character representing the player's pawn.
     */
    public char getPawn() {
        return pawn;
    }

    public void setPawn(char pawn) {
        this.pawn = pawn;
    }

    /**
     * Retrieve the local IP address of the player. NOTE: This is only useful for LAN games.
     * @return [String] The local IP address of the player.
     */
    public String getLocalPlayerIP() {
        return playerIP.getHostAddress();
    }
}