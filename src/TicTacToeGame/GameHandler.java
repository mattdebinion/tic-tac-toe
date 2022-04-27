package TicTacToeGame;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import TicTacToeGame.controllers.TicTacBoardController;

/**
 * The game logic.
 */
public class GameHandler {

    private static SessionData latestData;
    private static TicTacBoardController controller;

    /**
     * Listen for moves (SessionData objects) from the server.
     * @throws IOException
     * @throws UnknownHostException
     */
    public static void listenForData() throws UnknownHostException, IOException {

        Socket socket = new Socket("localhost", 60);
        ObjectOutputStream objOut = new ObjectOutputStream(socket.getOutputStream());
        objOut.flush();
        ObjectInputStream objIn = new ObjectInputStream(socket.getInputStream());

        new Thread(() -> {
            while(socket.isConnected()) {
                try {
                    SessionData dataReceived = (SessionData) objIn.readObject();

                    if(dataReceived instanceof SessionData) {

                        if(!dataReceived.checkVerification()) {
                            System.out.println("Unverified data received! Verifying and emitting again.");
                        } 

                        controller.updateBoardAt(dataReceived.getMovePair().getKey(), dataReceived.getMovePair().getValue(), dataReceived.getSender());
                    }

                } catch (Exception e) {
                    System.out.println("Error occured trying to listen for data within GameHandler!");
                    e.printStackTrace();
                }
            }
            
        }).start();
    }
}
