package TicTacToeGame;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;

// A server listens for and establishes connections between clients using a server socket.
public class Server {

    private final ServerSocket serverSocket;
    private int numActiveSessions = 0;

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void startServer() throws ClassNotFoundException {
        try {
            // Infinite loop to continuously look for clients wanting to establish connection
            while (!serverSocket.isClosed()) {

                // This socket is closed in ClientHandler class
                Socket socket = serverSocket.accept();
                System.out.println("A new client has connected!");
                SessionHandler sessionHandler = new SessionHandler(socket);
                Thread thread = new Thread(sessionHandler);

                // Call run() method of ClientHandler to start a new thread to handle each client
                thread.start();
            }
        } catch (IOException e) {
            closeServerSocket();
        }
    }

    // Close the server socket.
    public void closeServerSocket() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Run the server.
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        String systemIPAddress ="";

        try {
            // Amazon will return the IP address of this machine when visiting this link.
            URL url = new URL("https://checkip.amazonaws.com/");

            BufferedReader sc = new BufferedReader(new InputStreamReader(url.openStream()));
            systemIPAddress = sc.readLine().trim();

            // Start the server on unassigned port 60.
            ServerSocket serverSocket = new ServerSocket(60);
            Server server = new Server(serverSocket);

            System.out.println("The server has started successfully and can be connected to at " + systemIPAddress + ":60");
            server.startServer();

        } catch (Exception e) {
            System.out.println("Error: Unable to get IP address of this machine.");
        }
    }
}
