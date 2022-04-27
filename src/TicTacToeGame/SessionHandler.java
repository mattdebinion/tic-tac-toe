package TicTacToeGame;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

/**
 * The SessionHandler class handles communication between two players in a game.
 * 
 * @author Elihas Temori
 * @author Matt De Binion
 */
public class SessionHandler implements Runnable {
    public static ArrayList<SessionHandler> sessionHandlers = new ArrayList<>();

    // Socket for a connection, buffer reader and writer for receiving and sending data respectively.
    private Socket socket;
    private ObjectOutputStream dOut;
    private ObjectInputStream dIn;
    private SessionData sessionInformation;

    /**
     * Create the client handler from the socket the server passes.
     * @param socket Socket containing IP address and port number.
     * @throws ClassNotFoundException
     */
    public SessionHandler(Socket socket) throws ClassNotFoundException {
        try {
            this.socket = socket;
            this.dOut = new ObjectOutputStream(socket.getOutputStream());
            dOut.flush();
            this.dIn = new ObjectInputStream(socket.getInputStream());
            SessionData sessionData = (SessionData) dIn.readObject();
            this.sessionInformation = sessionData;        // This assumes data received from input stream will be SessionDatas


            // Add the new client handler to the array so they can receive messages from others.
            sessionHandlers.add(this);
            
        } catch (IOException e) {
            // Close everything more gracefully.
            closeEverything(socket, dIn, dOut);
        }
    }



    // Helper method to close everything so you don't have to repeat yourself.
    private void closeEverything(Socket socket, ObjectInputStream dIn, ObjectOutputStream dOut) {
        // The client disconnected or an error occurred so remove them from the list so no message is broadcasted.
        removeClientHandler();
        try {
            if (dIn != null) {
                dIn.close();
            }
            if (dOut != null) {
                dOut.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Send a message through each client handler thread so that everyone gets the message.
     * Basically each client handler is a connection to the client. So far any message that
     * is received, loop through each connection and send it down it.
     * @param messageToSend
     */
    private void broadcastMessage(SessionData dataToSend) {
        for (SessionHandler session : sessionHandlers) {
            try {
                session.dOut.writeObject(dataToSend);
                session.dOut.flush();
            } catch (IOException e) {
                // Gracefully close everything.
                closeEverything(socket, dIn, dOut);
            }
        }
    }

    public void sendMessage(SessionHandler clientHandler, SessionData data) {
        try {
            clientHandler.dOut.writeObject(data);
            clientHandler.dOut.flush();
        } catch (IOException e) {
            // Gracefully close everything.
            closeEverything(socket, dIn, dOut);
        }
    }

    // If the client disconnects for any reason remove them from the list so a message isn't sent down a broken connection.
    public void removeClientHandler() {
       sessionHandlers.remove(this);
       System.out.println("A client disconnected");
    }

    /**
     * Everything in this method is run on a separate thread. We want to listen for messages
     * on a separate thread because listening (bufferedReader.readLine()) is a blocking operation.
     * A blocking operation means the caller waits for the callee to finish its operation.
     */
    @Override
    public void run() {
        // Continue to listen for messages while a connection with the client is still established.
        while (socket.isConnected()) {
            try {

                SessionData receivedSessionData = (SessionData) dIn.readObject();

                if(receivedSessionData instanceof SessionData)
                    broadcastMessage(receivedSessionData);
                
            } catch (IOException | ClassNotFoundException e) {
                // Close everything gracefully.
                closeEverything(socket, dIn, dOut);
                break;
            }
        }
    }
}
