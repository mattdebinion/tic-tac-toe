package TicTacToeGame;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * The GameHandler class (an observable) handles the sessions and game logic that is sent through the server.
 */
public class GameHandler implements Runnable {

    private Socket player1;
    private Socket player2;

    private ObjectInputStream fromPlayer1;
    private ObjectOutputStream toPlayer1;
    private ObjectInputStream fromPlayer2;
    private ObjectOutputStream toPlayer2;

    public GameHandler(Socket player1, ObjectOutputStream toPlayer1, Socket player2, ObjectOutputStream toPlayer2) throws IOException {

        this.player1 = player1;
        this.toPlayer1 = toPlayer1;

        this.player2 = player2;
        this.toPlayer2 = toPlayer2;
    }

    @Override
    public void run() {

        try {
            fromPlayer1 = new ObjectInputStream(player1.getInputStream());
            fromPlayer2 = new ObjectInputStream(player2.getInputStream());

            toPlayer1.writeObject(new SessionData());   // Write something to player 1 to notify them that there are now two players.

            while(true) {

                SessionData dataToReceive = (SessionData) fromPlayer1.readObject();

                if(dataToReceive instanceof SessionData) {
                    System.out.println("Received data from player1, sending it to player 2...");
                    sendSessionData(toPlayer2, dataToReceive);
                }

                SessionData dataToReceive2 = (SessionData) fromPlayer2.readObject();

                if(dataToReceive2 instanceof SessionData) {
                    System.out.println("Received data from player2, sending it to player 1...");
                    sendSessionData(toPlayer1, dataToReceive2);
                }
                
                // GAME LOGIC IMPLEMENTATION HERE.
                // Ideas: Depending who the sending player, verify if their move is correct.
                // If their move is correct, send it to the other player. If not, do not send it and give the sender an error!
                // Client should read the SessionData object to display if there was a winner or a draw.
            }

        } catch (Exception e) {
            System.out.println("An error occured within the GameHandler!");
            e.printStackTrace();
        }
        
    }

    private void sendSessionData(ObjectOutputStream destination, SessionData dataToSend) throws IOException {
        destination.writeObject(dataToSend);
        destination.flush();
    }

    
}
