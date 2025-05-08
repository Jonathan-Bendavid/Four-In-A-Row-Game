import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;
import javax.swing.*;

public class FourInRow {
    private char[][] board;  // The game board represented as a 2D array
    private static final char PLAYER = 'X';  // Player's symbol
    private static final char AI = 'O';  // AI's symbol
    private static final int BOARD_ROWS = 6;  // Number of rows on the board
    private static final int BOARD_COLS = 7;  // Number of columns on the board
    private Random rand = new Random();  // Random number generator for AI's random moves
    private JFrame frame;  // Main window for the game
    private MyPanel4 panel;  // Custom panel to display the game board

    // Constructor initializes the board, frame, and panel
    public FourInRow() {
        this.board = new char[BOARD_ROWS][BOARD_COLS];  // Initialize empty board
        frame = new JFrame();  // Create a new JFrame
        panel = new MyPanel4(board);  // Create a custom panel to draw the board
        frame.add(panel);  // Add the panel to the frame
        frame.pack();  // Adjust the window size to fit the panel
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // Close the game when the window is closed
        frame.setVisible(true);  // Make the window visible
    }

    // Determines the AI move based on difficulty level
    public int[] aiMove(int diff) throws InterruptedException {
        if (diff == 1) {
            return randomMove();  // Easy: make a random move
        } else {
            int depth = switch (diff) {
                case 2 -> 4;
                case 3 -> 6;
                case 4 -> 8;
                default -> 10;  // For hardest difficulty, use depth of 10
            };
            return bestAiMove(depth);  // Use the best move based on depth
        }
    }

    // Calculates the best move for the AI using Alpha-Beta Pruning
    public int[] bestAiMove(int depth) throws InterruptedException {
        int bestVal = Integer.MIN_VALUE;  // Initialize the best value to a very low number
        int[] bestMove = {-1, -1};  // Array to store the best move's row and column

        // Define column order to prioritize center columns first
        int[] columnOrder = {3, 2, 4, 1, 5, 0, 6};  

        // Loop through each column in the specified order
        for (int j : columnOrder) {
            int i = BOARD_ROWS - 1;  // Start from the bottom of the column
            while (i >= 0 && board[i][j] != '\u0000') {  // Find the first empty row in the column
                i--;
            }
            if (i >= 0) {
                board[i][j] = AI;  // Make a move in the empty space
                int moveVal = alphaBeta(depth - 1, Integer.MIN_VALUE, Integer.MAX_VALUE, false);  // Evaluate the move
                board[i][j] = '\u0000';  // Undo the move after evaluation
                if (moveVal > bestVal) {  // If this move is better, update the best move
                    bestMove[0] = i;
                    bestMove[1] = j;
                    bestVal = moveVal;
                }
            }
        }
        return bestMove;  // Return the best move found
    }

    // Makes a random move for the AI
    public int[] randomMove() {
        int col;
        // Keep generating a random column until we find an empty one
        do {
            col = rand.nextInt(BOARD_COLS);
        } while (board[0][col] != '\u0000');  
        int row = BOARD_ROWS - 1;  // Start from the bottom of the column
        while (board[row][col] != '\u0000') {  // Find the first empty row in the column
            row--;
        }
        return new int[]{row, col};  // Return the row and column of the move
    }

    // Allows the player to make a move by clicking on a column
    private void PlayerMove() throws InterruptedException {
        final boolean[] legal = {false};  // Flag to track if the move is legal
        // Add a mouse listener to the panel to detect clicks
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int mouseX = e.getX();  // Get the X position of the mouse click
                int col = mouseX / (panel.getWidth() / BOARD_COLS);  // Determine the column based on the X position
                int row = BOARD_ROWS - 1;  // Start from the bottom of the column
                while (row >= 0 && board[row][col] != '\u0000') {  // Find the first empty row
                    row--;
                }
                if (row >= 0) {
                    board[row][col] = PLAYER;  // Place the player's piece in the empty row
                    legal[0] = true;  // Mark the move as legal
                }
            }
        });
        // Keep waiting until the player makes a valid move
        while (!legal[0]) {
            Thread.sleep(50);  // Wait for 50ms before checking again
        }
        // Remove the mouse listener after the move
        panel.removeMouseListener(panel.getMouseListeners()[0]);
    }

    // Alpha-Beta pruning to evaluate possible moves and return the best score
    private int alphaBeta(int depth, int alpha, int beta, boolean isAI) throws InterruptedException {
        int score = checkGameState();  // Check if the game is over (win, loss, or draw)
        if (score != 0 || depth <= 0) {  // If the game is over or max depth is reached, return the score
            return score;
        }

        if (isAI) {  // AI's turn (maximize score)
            int maxScore = Integer.MIN_VALUE;
            for (int j = 0; j < BOARD_COLS; j++) {  // Loop through all columns
                int i = BOARD_ROWS - 1;
                while (i >= 0 && board[i][j] != '\u0000') {  // Find an empty row in the column
                    i--;
                }
                if (i >= 0) {
                    board[i][j] = AI;  // Make the AI's move
                    int currentScore = alphaBeta(depth - 1, alpha, beta, false);  // Recursively call alphaBeta for the next depth
                    maxScore = Math.max(maxScore, currentScore);  // Keep track of the maximum score
                    alpha = Math.max(alpha, maxScore);  // Update the alpha value
                    board[i][j] = '\u0000';  // Undo the move
                    if (beta <= alpha) {  // Prune the search tree if the current branch cannot improve the result
                        break;
                    }
                }
            }
            return maxScore;  // Return the best score for AI
        } else {  // Player's turn (minimize score)
            int minScore = Integer.MAX_VALUE;
            for (int j = 0; j < BOARD_COLS; j++) {  // Loop through all columns
                int i = BOARD_ROWS - 1;
                while (i >= 0 && board[i][j] != '\u0000') {  // Find an empty row in the column
                    i--;
                }
                if (i >= 0) {
                    board[i][j] = PLAYER;  // Make the player's move
                    int currentScore = alphaBeta(depth - 1, alpha, beta, true);  // Recursively call alphaBeta for the next depth
                    minScore = Math.min(minScore, currentScore);  // Keep track of the minimum score
                    beta = Math.min(beta, minScore);  // Update the beta value
                    board[i][j] = '\u0000';  // Undo the move
                    if (beta <= alpha) {  // Prune the search tree if the current branch cannot improve the result
                        break;
                    }
                }
            }
            return minScore;  // Return the best score for the player
        }
    }

    // Checks if four connected cells are equal and not empty
    public static boolean equals4(char a, char b, char c, char d) {
        return a == b && b == c && c == d && a != '\u0000';  // All four cells are equal and not empty
    }

    // Checks the game state for a win or draw
    public int checkGameState() {
        // Check horizontal, vertical, and diagonal win conditions
        for (int i = 0; i < BOARD_ROWS; i++) {
            for (int j = 0; j < BOARD_COLS - 3; j++) {
                if (equals4(board[i][j], board[i][j + 1], board[i][j + 2], board[i][j + 3])) {
                    return board[i][j] == AI ? 100 : -100;  // AI or player wins
                }
            }
        }

        for (int j = 0; j < BOARD_COLS; j++) {
            for (int i = 0; i < BOARD_ROWS - 3; i++) {
                if (equals4(board[i][j], board[i + 1][j], board[i + 2][j], board[i + 3][j])) {
                    return board[i][j] == AI ? 100 : -100;  // AI or player wins
                }
            }
        }

        for (int i = 0; i < BOARD_ROWS - 3; i++) {
            for (int j = 0; j < BOARD_COLS - 3; j++) {
                if (equals4(board[i][j], board[i + 1][j + 1], board[i + 2][j + 2], board[i + 3][j + 3])) {
                    return board[i][j] == AI ? 100 : -100;  // AI or player wins
                }
            }
        }

        for (int i = 3; i < BOARD_ROWS; i++) {
            for (int j = 0; j < BOARD_COLS - 3; j++) {
                if (equals4(board[i][j], board[i - 1][j + 1], board[i - 2][j + 2], board[i - 3][j + 3])) {
                    return board[i][j] == AI ? 100 : -100;  // AI or player wins
                }
            }
        }

        // Check if the board is full (draw)
        for (int i = 0; i < BOARD_ROWS; i++) {
            for (int j = 0; j < BOARD_COLS; j++) {
                if (board[i][j] == '\u0000') {
                    return 0; // Continue playing
                }
            }
        }

        return 1; // Draw
    }

    // Main game loop
    public void play4InRow(int diff) throws InterruptedException {
        int gameState = 0;  // Initial game state (0 = ongoing, 1 = draw, -100 = player win, 100 = AI win)
        boolean isPlayerTurn = Math.random() >= 0.5;  // Randomly decide who goes first
        while (gameState == 0) {
            if (isPlayerTurn) {
                PlayerMove();  // Player's turn
            } else {
                int[] aiMove = aiMove(diff);  // AI's turn
                board[aiMove[0]][aiMove[1]] = AI;  // Place AI's move on the board
            }
            updateDisplay();  // Update the display after move
            isPlayerTurn = !isPlayerTurn;  // Switch turns
            gameState = checkGameState();  // Check if the game is over
        }
        endGame(gameState);  // End the game and display the result
    }

    // End game logic (display result)
    private void endGame(int gameState) throws InterruptedException {
        Thread.sleep(1500);  // Wait for 1.5 seconds
        frame.getContentPane().removeAll();  // Remove all components from the frame
        JLabel messageLabel = new JLabel();
        if (gameState == 1) { // Display message depending on result
            messageLabel.setText("It's A Draw");  
        } else if (gameState == -100) {
            messageLabel.setText("You Win!");  
        } else {
            messageLabel.setText("You Lost!");  
        }
        messageLabel.setFont(messageLabel.getFont().deriveFont(Font.PLAIN, 40));  // Set font size
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);  // Center the text horizontally
        messageLabel.setVerticalAlignment(SwingConstants.CENTER);  // Center the text vertically
        frame.getContentPane().add(messageLabel, BorderLayout.CENTER);  // Add the result message to the frame
        frame.revalidate();  // Revalidate the frame to update the display
        frame.repaint();  // Repaint the frame
        Thread.sleep(1600);  // Wait for 1.6 seconds before resetting the game
        frame.getContentPane().removeAll();  // Remove all components
        frame.getContentPane().add(panel, BorderLayout.CENTER);  // Add the game board panel back to the frame
        frame.revalidate();  // Revalidate the frame again
        frame.repaint();  // Repaint the frame
        clearBoard();  // Clear the game board for a new game
    }

    // Clears the game board
    public void clearBoard() {
        for (int i = 0; i < BOARD_ROWS; i++) {
            for (int j = 0; j < BOARD_COLS; j++) {
                board[i][j] = '\u0000';  // Set all cells to empty
            }
        }
        updateDisplay();  // Update the display after clearing the board
    }

    // Updates the display to show the current state of the board
    public void updateDisplay() {
        panel.setBoard(board);  // Set the board state in the panel
        panel.repaint();  // Repaint the panel to show the updated board
    }

    public static void main(String[] args) throws InterruptedException {
        FourInRow f = new FourInRow();  // Create a new game
        while (true) {
            int diff = Integer.parseInt(JOptionPane.showInputDialog("Enter Difficulty: 1 -> Super Easy, 2 -> Easy, 3 -> Medium, 4 -> Hard, 5 -> Impossible"));
            f.play4InRow(diff);  // Start the game with the chosen difficulty
        }
    }
}
