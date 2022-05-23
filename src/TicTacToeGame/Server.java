package TicTacToeGame;

// import java.io.BufferedReader;
// import java.io.InputStreamReader;
// import java.net.URL;
import java.io.ObjectOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Listens for and establishes connections between clients using a Server Socket.
 * 
 * @author Matt De Binion
 * @author Elihas Temori
 */
public class Server {

    /**
     * Starts the server.
     * @param args Command line arguments.
     */
    public static void main(String[] args) {

        try {
            // Amazon will return the IP address of this machine when visiting this link.
            URL url = new URL("https://checkip.amazonaws.com/");

            BufferedReader sc = new BufferedReader(new InputStreamReader(url.openStream()));
            String systemIPAddress = sc.readLine().trim();

            // Start the server on unassigned port 60.
            ServerSocket serverSocket = new ServerSocket(60);
            Server server = new Server(serverSocket);

            System.out.println("Your IP address is " + systemIPAddress);

            server.startServer();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private final ServerSocket SOCKET;
    private final static DateTimeFormatter SERVER_TIME_FORMAT = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm:ss a");

    /**
     * Performs the same action as starting from main. Allows a client to start a server on their machine.
     * <p> This operation is a blocking operation. Run in a thread!
     */
    public static void initializeHost() {
        try {
            // Amazon will return the IP address of this machine when visiting this link.
            URL url = new URL("https://checkip.amazonaws.com/");

            BufferedReader sc = new BufferedReader(new InputStreamReader(url.openStream()));
            String systemIPAddress = sc.readLine().trim();

            // Start the server on unassigned port 60.
            ServerSocket serverSocket = new ServerSocket(60);
            Server server = new Server(serverSocket);

            System.out.println("Your IP address is " + systemIPAddress);

            server.startServer();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    /**
     * Creates a server given a ServerSocket object.
     * @param socket
     */
    public Server(ServerSocket socket) {
        SOCKET = socket;
    }

    /**
     * Spawns a new thread with two connected clients to play games.
     */
    private void startServer() {
        System.out.println(getMachineTime() + "Starting the server...");

        try {

            while(!SOCKET.isClosed()) {
                SessionData newSessionInfo = new SessionData();

                Socket player1 = SOCKET.accept();
                System.out.println(getMachineTime() + "A new client is connected. This player's IP address is " + player1.getInetAddress().getHostAddress());

                PlayerObject player1Obj = new PlayerObject("PLAYER 1", 'X', false);
                ObjectOutputStream player1Out = new ObjectOutputStream(player1.getOutputStream());      // Notify player 1 of their player object.
                player1Out.writeObject(player1Obj);
                newSessionInfo.setPlayer1(player1Obj); // Set this player as player 1 in the session data.

                Socket player2 = SOCKET.accept();
                System.out.println(getMachineTime() + "A new client is connected. This player's IP address is " + player1.getInetAddress().getHostAddress());
                
                PlayerObject player2Obj = new PlayerObject("PLAYER 2", 'O', false);
                ObjectOutputStream player2Out = new ObjectOutputStream(player2.getOutputStream());      // Notify player 2 of their player object.
                player2Out.writeObject(player2Obj);
                newSessionInfo.setPlayer2(player2Obj); // Set this player as player 2 in the session data.
                
                System.out.println("Starting a new session with 2 recent players!");
                new Thread(new GameHandler(player1, player1Out, player2, player2Out)).start();
            }

        } catch (Exception e) {
            System.out.println(getMachineTime() + e.getMessage());
        }
    }

    /**
     * Closes the server socket. Before calling, ensure you let all players in the game know the game is being terminated.
     * @throws IOException Occurs when you disconnect from the internet.
     */
    public void terminateServer() throws IOException {
        SOCKET.close();
        System.out.println(getMachineTime() + "Server terminated!");
    }

    private String getMachineTime() {
        return "[" + LocalDateTime.now().format(SERVER_TIME_FORMAT) + "] ";
    }
}