package TicTacToeGame;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;


/**
 * A PlayerObject class is a serializable object that contains the information about a player that is connected to the server.
 * Once a PlayerObject is created, it cannot be modified.
 * 
 * @author Matt De Binion
 */
public class PlayerObject implements Serializable {
    
    // PLAYER INFORMATION \\
    private String name;            // The name of the player object.
    private boolean isAI = false;   // Flag to determine if Controller should handle this PlayerObject as an AI player.
    private char pawn;              // The character representing the player's pawn.
    private InetAddress playerIP;   // The local IP address of this player.

    /**
     * Creates a new PlayerObject with a name, pawn, and if player is human.
     * @param name [String] The name of the player.
     * @param pawn [char] The character representing the player's pawn.
     * @param isHuman [boolean] True if the player is human, false if the player is an AI.
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
     * @return [String] The name of the player.
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieve the flag to determine if the player is an AI.
     * @return [boolean] True if the player is an AI, false if the player is human.
     */
    public boolean AIflag() {
        return isAI;
    }

    /**
     * Retrieve the character representing the player's pawn.
     * @return [char] The character representing the player's pawn.
     */
    public char getPawn() {
        return pawn;
    }

    /**
     * Retrieve the local IP address of the player. NOTE: This is only useful for LAN games.
     * @return [String] The local IP address of the player.
     */
    public String getLocalPlayerIP() {
        return playerIP.getHostAddress();
    }
}
