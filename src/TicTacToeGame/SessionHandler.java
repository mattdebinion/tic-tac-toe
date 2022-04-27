package TicTacToeGame;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class SessionHandler implements Runnable {
    public static ArrayList<SessionHandler> sessionHandlers = new ArrayList<>();

    // Socket for a connection, buffer reader and writer for receiving and sending data respectively.
    private Socket socket;
    private ObjectOutputStream dOut;
    private ObjectInputStream dIn;
    private String clientUsername;
    private boolean waitingOnPlayerToJoin = false;

    /**
     * Create the client handler from the socket the server passes.
     * @param socket Socket containing IP address and port number.
     * @throws ClassNotFoundException
     */
    public SessionHandler(Socket socket) throws ClassNotFoundException {
        try {
            this.socket = socket;
            this.dOut = new ObjectOutputStream(socket.getOutputStream());
            this.dIn = new ObjectInputStream(socket.getInputStream());
            this.clientUsername = ((ClientInformationMsg) dIn.readObject()).username;

            // Add the new client handler to the array so they can receive messages from others.
            sessionHandlers.add(this);

            //broadcastMessage(new Message("Server", clientUsername + " has entered the chat!", null));
        } catch (IOException e) {
            // Close everything more gracefully.
            closeEverything(socket, dIn, dOut);
        }
    }

    // Helper method to close everything so you don't have to repeat yourself.
    public void closeEverything(Socket socket, ObjectInputStream dIn, ObjectOutputStream dOut) {
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
    public void broadcastMessage(Message messageToSend) {
        for (SessionHandler clientHandler : sessionHandlers) {
            try {
                // You don't want to broadcast the message to the user who sent it.
//                if (!clientHandler.clientUsername.equals(clientUsername)) {
                    clientHandler.dOut.writeObject(messageToSend);
                    clientHandler.dOut.flush();
//                }
            } catch (IOException e) {
                // Gracefully close everything.
                closeEverything(socket, dIn, dOut);
            }
        }
    }

    public void sendMessage(SessionHandler clientHandler, Message messageToSend) {
        try {
            clientHandler.dOut.writeObject(messageToSend);
            clientHandler.dOut.flush();
        } catch (IOException e) {
            // Gracefully close everything.
            closeEverything(socket, dIn, dOut);
        }
    }

    // If the client disconnects for any reason remove them from the list so a message isn't sent down a broken connection.
    public void removeClientHandler() {
//        sessionHandlers.remove(this);
//        broadcastMessage(new Message("Server", clientUsername + " has left the chat!", null));
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
                System.out.println("listening...");
                Message receivedMsg = (Message) dIn.readObject();
                System.out.println("...received a msg!");

                if(receivedMsg instanceof CreateGameMsg createGameMsg)
                {
                    System.out.println("creating game: " + createGameMsg.gameName);
                    waitingOnPlayerToJoin = true;
                }
                if(receivedMsg instanceof JoinGameMsg joinGameMsg)
                {
                    for (SessionHandler session : sessionHandlers) // find first open game
                    {
                        if(session.waitingOnPlayerToJoin)
                        {
                            // join game
                            GameStartingMsg msg = new GameStartingMsg();
                            msg.gameJoiner = clientUsername;
                            msg.gameCreator = session.clientUsername;
                            sendMessage(session, msg);
                            sendMessage(this, msg);
                            System.out.println("game found");
                            break;
                        }
                    }
                }
                if(receivedMsg instanceof GameStartingMsg gameStartingMsg)
                {
                    System.out.println("Starting game created by " + gameStartingMsg.gameCreator);
                }
//                    broadcastMessage(receivedMsg);
            } catch (IOException | ClassNotFoundException e) {
                // Close everything gracefully.
                closeEverything(socket, dIn, dOut);
                break;
            }
        }
    }
}
