package TicTacToeGame;

import TicTacToeGame.exceptions.GameNotStartedException;
import javafx.application.Platform;
import javafx.scene.control.Button;

import java.util.ArrayList;
import java.util.List;

/**
 * An AI player object that uses a minimax algorithm with alpha beta pruning.
 * This AI player also has the ability to interact with the TicTacToe board.
 * @author Matt De Binion
 */
public class AIPlayer extends Player {

    private int difficulty = 0;     // 0 = easy, 1 = medium, 2 = hard.
    private boolean thinking = false;
    private TicTacToeBoard GameLogic = TicTacToeBoard.getInstance();
    private Thread brain;

    public AIPlayer() throws GameNotStartedException {
        this(0);
    }

    public AIPlayer(int difficulty) throws GameNotStartedException {
        super(thinkOfRandomName());
        super.AI = true;    // Specify that this is now an EI player

        // Set difficulty
        if(difficulty >= 0 && difficulty <= 2) {
            this.difficulty = difficulty;
        } else {
            difficulty = 1;
        }
        think(); // One object is created, start thinking.
    }

    /**
     * Determines if the AI is currently thinking.
     * @return A boolean
     */
    public boolean isThinking() {
        return thinking;
    }

    /**
     * 
     */
    private void think() {

        // Create a new Thread that looks to see if it can play the current turn.
        brain = new Thread(new Runnable() {

            @Override
            public void run() {
                
                System.out.println(GameLogic.getAIPlayer().getPlayerName() + ": I am alive!");

                while(!brain.isInterrupted()) {
                    try {
                        Thread.sleep(250);  // Speed control the thread as going too fast breaks the game.
                        if(GameLogic.getCurrentPlayer().isAI() && !GameLogic.gameHasEnded()) {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        performMyNextMove();
                                    } catch (GameNotStartedException e) {
                                        e.printStackTrace();
                                    }
                                }

                            });
                        }
                            
                    } catch (InterruptedException e) {
                        brain.interrupt();
                    }
                }
                
            }
        });

        // If the thread is not running, start the thread as a Dameon thread
        if(!brain.isAlive()) {
            brain.setDaemon(true);  // Tells JVM that this thread is in the background and should be killed once application closes.
            brain.start();
        }
    }
    /**
     * I will determine the next optimal move based on the difficulty that I was created with.
     * @return True if my move advanced the turn. Returns false otherwise.
     * @throws GameNotStartedException Occurs when the started flag is FALSE.
     */
    private boolean performMyNextMove() throws GameNotStartedException {
        System.out.println(this.playerName + ": Trying move...");
        if(difficulty == 0) { 

            // Calculates best possible move to minimize the maximum possible loss
            int[] bestMove = move();
            String squareName = "#square" + (bestMove[0] + 1) + (bestMove[1] + 1);
            Button selectedButton = (Button) GameLogic.getGameScene().lookup(squareName);

            return GameLogic.advanceTurn(selectedButton);
        }

        return false;

    }

    /**
     * I think of a a random number in a specified range.
     * @param min An integer inclusive
     * @param max An integer exclusive
     * @return A number between min and max.
     */
    private static int thinkOfRandomNumber(int min, int max) {
        return (int) (Math.random() * (max - min) + min);
    }

    /**
     * I pick a random name from a list of names.
     */
    private static String thinkOfRandomName() {
        String[] names = {"Matt", "Nika", "Elihas"};
        return "AI " + names[thinkOfRandomNumber(0, names.length)];
    }

    /**
     * Given a board state and 3 consecutive squares, this function scores the squares and returns the score.
     * 1 point is awarded for one AI symbol and is multiplied by a factor of 10 for each additional AI symbol
     * -1 point is awarded for one player symbol and is multiplied by a factor of 10 for each additional player symbol
     * @param board - 2D array representing TTT board with char symbols
     * @param row1 - First square row #
     * @param col1 - First square col #
     * @param row2 - Second square row #
     * @param col2 - Second square col #
     * @param row3 - Third square row #
     * @param col3 - Third square col #
     * @return score of line
     */
    private int evaluateLine(char[][] board, int row1, int col1, int row2, int col2, int row3, int col3) {
        int score = 0;

        // Evaluate a given line for AI symbol (X or O) and score
        // Check first square for AI symbol

        if (board[row1][col1] == pawnPiece)
            score = 1;
        else if (board[row1][col1] == oppPawnPiece)
            score = -1;

        // Check second square for AI symbol
        if (board[row2][col2] == pawnPiece) {
            if (score == 1) {
                score = 10;
            } else if (score == -1) {
                return 0;
            } else {
                score = 1;
            }
        } else if (board[row2][col2] == oppPawnPiece) {
            if (score == -1) {
                score = -10;
            } else if (score == 1) {
                return 0;
            } else {
                score = -1;
            }
        }

        // Check third square for AI symbol
        if (board[row3][col3] == pawnPiece) {
            if (score > 0) {
                score *= 10;
            } else if (score < 0) {
                return 0;
            } else {
                score = 1;
            }
        } else if (board[row3][col3] == oppPawnPiece) {
            if (score < 0) {
                score *= 10;
            } else if (score > 1) {
                return 0;
            } else {
                score = -1;
            }
        }
        return score;
    }

    /**
     * Determines the sum of utility scores of every winnable line in a TTT board
     * @param board
     * @return score of the given board
     */
    private int evaluate(char[][] board) {
        int score = 0;

        // Calculate score for each possible winning line
        score += evaluateLine(board,0,0,0,1,0,2);
        score += evaluateLine(board,1,0,1,1,1,2);
        score += evaluateLine(board,2,0,2,1,2,2);
        score += evaluateLine(board,0,0,1,0,2,0);
        score += evaluateLine(board,0,1,1,1,2,1);
        score += evaluateLine(board,0,2,1,2,2,2);
        score += evaluateLine(board,0,0,1,1,2,2);
        score += evaluateLine(board,0,2,1,1,2,0);

        return score;
    }

    /**
     * Determines and returns a list of square coordinates that are not occupied by a symbol
     * @param board - 2D array representing board
     * @return list of int coordinates representing open squares
     */
    private List<int[]> generateMoves(char[][] board) {
        List<int[]> moves = new ArrayList<int[]>();

        // If game is won, no more moves needed
        if (checkCharBoardForWin(board))
            return moves;

        // Add unoccupied squares to list of moves
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 3; ++col) {
                if (board[row][col] != 'X' && board[row][col] != 'O') {
                    moves.add(new int[] {row, col});
                }
            }
        }

        return moves;
    }

    /**
     * Recursive function to determine best possible move to minimize the maximum loss with alpha beta pruning
     * to reduce the number of nodes that need to be evaluated in the search tree
     * @param depth - number of turns to search
     * @param symbol - 'X' or 'O'
     * @param alpha - best MAX value found for MAX node
     * @param beta - best MIN value found for MIN node
     * @param board - 2d char array representing TTT board
     * @return alpha/beta score, row of best move, col of best move
     */
    private int[] minimax(int depth, char symbol, int alpha, int beta, char[][] board) {
        // Generate possible moves based on non-occupied squares
        List<int[]> nextMoves = generateMoves(board);

        int score;
        int bestRow = -1;
        int bestCol = -1;

        // Maximize pawnPiece and minimize oppPawnPiece
        if (nextMoves.isEmpty() || depth == 0) {
            // End of game or depth has been reached
            score = evaluate(board);
            return new int[] {score, bestRow, bestCol};
        } else {
            for (int[] move : nextMoves) {
                // Try move
                board[move[0]][move[1]] = symbol;
                if(symbol == pawnPiece) {
                    score = minimax(depth - 1, oppPawnPiece, alpha, beta, board)[0];
                    if (score > alpha) {
                        alpha = score;
                        bestRow = move[0];
                        bestCol = move[1];
                    }
                } else {
                    score = minimax(depth - 1, pawnPiece, alpha, beta, board)[0];
                    if (score < beta) {
                        beta = score;
                        bestRow = move[0];
                        bestCol = move[1];
                    }
                }
                // Undo move
                board[move[0]][move[1]] = ' ';

                if (alpha >= beta) break;
            }
            return new int[] {(symbol == pawnPiece) ? alpha : beta, bestRow, bestCol};
        }
    }

    /**
     * Use the minimax algorithm w/ alpha beta pruning to find best move
     * @return row and col of best move
     */
    public int[] move() {
        char[][] board = intBoardToCharBoard();
        int[] result = minimax(4, pawnPiece, Integer.MIN_VALUE, Integer.MAX_VALUE, board);
        System.out.println("SCORE: " + result[0] + "\nROW: " + result[1] + "\nCOL: " + result[2]);
        return new int[] {result[1], result[2]};
    }

    /**
     * Converts boardState from TicTacToeBoard from integer 2D array to char 2D array with X & O symbols
     * @return board
     */
    private char[][] intBoardToCharBoard () {
        int[][] board = new int[GameLogic.getBoardState().length][];
        char[][] charBoard = {{' ', ' ', ' '},
                {' ', ' ', ' '},
                {' ', ' ', ' '}};

        // Copy boardState to new 2D array
        for(int i = 0; i < GameLogic.getBoardState().length; i++) {
            int[] aMatrix = GameLogic.getBoardState()[i];
            int aLength = aMatrix.length;
            board[i] = new int[aLength];
            System.arraycopy(aMatrix, 0, board[i], 0, aLength);
        }
        // Convert integer values to board symbols
        for(int i = 0; i < 3; i++) {
            for(int j = 0; j < 3; j++) {
                if(board[i][j] == 1) {
                    charBoard[i][j] = 'X';
                } else if(board[i][j] == 2) {
                    charBoard[i][j] = 'O';
                }
            }
        }
        return charBoard;
    }

    /**
     * Checks board for horizontal, vertical and diagonal win for either player
     * @param board
     * @return true if a player has won
     */
    private boolean checkCharBoardForWin(char[][] board) {
        //Check for a vertical win (if X1, X2, X3 are the same)
        for(int col = 0; col < 3; col++) {
            if(board[0][col] == 'X'
                    && board[1][col] == 'X'
                    && board[2][col] == 'X')
                return true;
            else if (board[0][col] == 'O'
                    && board[1][col] == 'O'
                    && board[2][col] == 'O')
                return true;
        }
        // Check for horizontal win (if 1X, 2X, 3X are the same)
        for(int row = 0; row < 3; row++) {

            if(board[row][0] == 'X'
                    && board[row][1] == 'X'
                    && board[row][2] == 'X')
                return true;
            else if (board[row][0] == 'O'
                    && board[row][1] == 'O'
                    && board[row][2] == 'O')
                return true;
        }
        // Check for both diagonal orientations (if 11, 22, 33 OR 31, 22, 13 are the same)
        if(board[0][0] == 'X'
                && board[1][1] == 'X'
                && board[2][2] == 'X')
            return true;
        else if (board[0][0] == 'O'
                && board[1][1] == 'O'
                && board[2][2] == 'O')
            return true;

        if(board[2][0] == 'X'
                && board[1][1] == 'X'
                && board[0][2] == 'X')
            return true;
        else if (board[2][0] == 'O'
                && board[1][1] == 'O'
                && board[0][2] == 'O')
            return true;

        return false;
    }

}
