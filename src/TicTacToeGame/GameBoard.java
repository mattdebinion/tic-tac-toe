package TicTacToeGame;

import java.io.Serializable;

/**
 * A GameBoard class is a serializable object that contains the information about the board
 * GameBoard, checks for win, and prints board.
 *
 * @author Nika Daroui
 */
public class GameBoard implements Serializable {

    final int ROW = 3;
    final int COL = 3;
    char[][] gameBoard = new char[ROW][COL];
    private int movesCounter = 0;

    public GameBoard() {
        // initialize board with ' '
        for (int row = 0; row < ROW; row++) {
            for (int col = 0; col < COL; col++) {
                gameBoard[row][col] = ' ';
            }

        }
    }

    public void printBoard() {
        for (int row = 0; row < ROW; row++) {
            for (int col = 0; col < 3; col++) {
                System.out.print(gameBoard[row][col] + " ");
            }
            System.out.println();
        }
    }

    // gets row and column info from TicTacToe Controller and player information and
    // sets the board
    public void setMove(int row, int col, int player){
        if (player == 1){
            gameBoard[row][col] = 1;
            movesCounter++;
        }
        if (player == 2){
            gameBoard[row][col] = 2;
            movesCounter++;
        }
    }

    // check for wins
    //
    // returns: true for win
    public boolean checkWin(int player) {

        // Row Win check
        for (int i = 0; i < 3; i++) {
            if (gameBoard[i][0] == gameBoard[i][1] &&
                    gameBoard[i][1] == gameBoard[i][2] &&
                    gameBoard[i][0] != ' ') {
                if (player == 1) {
                    System.out.println("X player wins!");
                    printBoard();
                    return true;
                } else {
                    System.out.println("O player wins!");
                    printBoard();
                    return true;
                }
            }
        }

        // Column Win Check
        for (int i = 0; i < 3; i++) {
            if (gameBoard[0][i] == gameBoard[1][i] &&
                    gameBoard[1][i] == gameBoard[2][i] &&
                    gameBoard[0][i] != ' ') {
                if (player == 1) {
                    System.out.println("X player wins!");
                    printBoard();
                    return true;
                }
                else {
                    System.out.println("O player wins!");
                    printBoard();
                    return true;
                }
            }
        }

        // Diagonal Win
        if (gameBoard[0][0] == gameBoard[1][1] &&
                gameBoard[1][1] == gameBoard[2][2] &&
                gameBoard[0][0] != ' ') {
            if (player == 1) {
                System.out.println("Player1 wins!");
                printBoard();
                return true;
            }
            else {
                System.out.println("O player wins!");
                printBoard();
                return true;
            }
        }
        if (gameBoard[0][2] == gameBoard[1][1] &&
                gameBoard[1][1] == gameBoard[2][0] &&
                gameBoard[0][2] != ' ') {
            if (player == 1) {
                System.out.println("X player wins!");
                printBoard();
                return true;
            }
            else {
                System.out.println("O player wins!");
                printBoard();
                return true;
            }
        }
        return false;
    }

    // returns total moves to check if stalemate was achieved
    public int getMovesCounter(){
        return movesCounter;
    }
}