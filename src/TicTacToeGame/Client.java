package TicTacToeGame;

import javafx.scene.image.Image;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Observable;
import java.util.Observer;

// Each client sends messages to the server & the server spawns a thread to communicate with the client.
// Each communication with a client is added to an array list so any message sent gets sent to every other client
// by looping through it.

public class Client extends Observable implements Observer, Runnable {

    private Socket socket; // Socket for the client to connect to
    private ObjectOutputStream dOut; // Output stream
    private ObjectInputStream dIn; // Input stream
    private String username; // Username

    /**
     * Creates a client with a specified controller, socket, and username.
     *
     * @param socket     A socket
     * @param username   A username for the client.
     */
    public Client(Socket socket, String username) {
        try {
            this.socket = socket;
            this.username = username;
            this.dOut = new ObjectOutputStream(socket.getOutputStream());
            this.dIn = new ObjectInputStream(socket.getInputStream());

        } catch (IOException e) {
            closeEverything(socket, dIn, dOut);
        }
    }

    /**
     * Send a message with optional image over the ObjectOutputStream.
     * 
     * @param msg A message to be sent.
     */
    public void sendMessage(Message messageToSend) {
        try {
            dOut.writeObject(messageToSend);
            dOut.flush();
        } catch (IOException e) {
            closeEverything(socket, dIn, dOut);
        }
    }

    /**
     * Listen for a message over the socket to display within the JavaFX
     * application.
     */
    public void listenForMessage() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                // While there is still a connection with the server, continue to listen for
                // messages on a separate thread.
                while (socket.isConnected()) {
                    try {
                        Message receivedMsg = (Message) dIn.readObject();

                        if(receivedMsg instanceof GameStartingMsg gameStartingMsg)
                        {
                            System.out.println("Starting game created by " + gameStartingMsg.gameCreator);
                        }

                    } catch (Exception e) {
                        //closeEverything(socket, dIn, dOut);
                    }
                }
            }
        }).start();
    }

    public void startGameTest()
    {
        CreateGameMsg msg = new CreateGameMsg();
        msg.gameName = "testGameWhoo!";
        msg.gameCreatorUsername = username;
        System.out.println(msg.gameName);
        sendMessage(msg);
        System.out.println("sent!");
    }

    public void joinGameTest()
    {
        JoinGameMsg msg = new JoinGameMsg();
        msg.gameToJoinName = "testGameWhoo!";
        sendMessage(msg);
    }

    /**
     * Helper method to close everything.
     * 
     * @param socket
     * @param dIn
     * @param dOut
     */
    public void closeEverything(Socket socket, ObjectInputStream dIn, ObjectOutputStream dOut) {
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
     * Gets the username associated with the client.
     * 
     * @return String
     */
    public String getUsername() {
        return username;
    }

    @Override
    public void run() {

    }

    @Override
    public void update(Observable o, Object arg) {

    }
}
