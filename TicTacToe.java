import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class TicTacToe extends JFrame implements ActionListener {

    private JButton[][] buttons;
    private boolean player1Turn = true;
    private int turnCount = 0;
    private JLabel statusLabel;
    private final int BOARD_SIZE = 3;
    private final int BUTTON_SIZE = 100;
    private final int WINDOW_SIZE = 300;
    private char[][] board = new char[BOARD_SIZE][BOARD_SIZE];
    private final char PLAYER_1_MARK = 'X';
    private final char PLAYER_2_MARK = 'O';

    public TicTacToe() {
        setTitle("Tic Tac Toe");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(WINDOW_SIZE, WINDOW_SIZE);
        setResizable(false);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(BOARD_SIZE, BOARD_SIZE));

        buttons = new JButton[BOARD_SIZE][BOARD_SIZE];
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(BUTTON_SIZE, BUTTON_SIZE));
                button.addActionListener(this);
                buttonPanel.add(button);
                buttons[i][j] = button;
                board[i][j] = '-';
            }
        }
        add(buttonPanel, BorderLayout.CENTER);

        statusLabel = new JLabel("Player 1's turn");
        add(statusLabel, BorderLayout.SOUTH);

        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        JButton clickedButton = (JButton) e.getSource();
        int row = -1;
        int col = -1;

        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (clickedButton == buttons[i][j]) {
                    row = i;
                    col = j;
                    break;
                }
            }
        }

        if (player1Turn) {
            clickedButton.setText(Character.toString(PLAYER_1_MARK));
            board[row][col] = PLAYER_1_MARK;
            statusLabel.setText("Player 2's turn");
        } else {
            clickedButton.setText(Character.toString(PLAYER_2_MARK));
            board[row][col] = PLAYER_2_MARK;
            statusLabel.setText("Player 1's turn");
        }

        clickedButton.setEnabled(false);
        turnCount++;

        if (checkForWin(row, col)) {
            if (player1Turn) {
                statusLabel.setText("Player 1 wins!");
            } else {
                statusLabel.setText("Player 2 wins!");
            }
            disableAllButtons();
        } else if (turnCount == BOARD_SIZE * BOARD_SIZE) {
            statusLabel.setText("It's a tie!");
        }

        player1Turn = !player1Turn;
        if (!player1Turn) {
            int[] bestMove = findBestMove();
            buttons[bestMove[0]][bestMove[1]].doClick();
        }
    }

    private void disableAllButtons() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                buttons[i][j].setEnabled(false);
            }
        }
    }

    private boolean checkForWin(int row, int col) {
        char mark = board[row][col];

        // Check row
        boolean win = true;
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (board[row][i] != mark) {
                win = false;
                break;
            }
        }
        if (win) {
            return true;
        }

        // Check column
        win = true;
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (board[i][col] != mark) {
                win = false;
                break;
            }
        }
        if (win) {
            return true;
        }

        // Check diagonal
        if (row == col) {
            win = true;
            for (int i = 0; i < BOARD_SIZE; i++) {
                if (board[i][i] != mark) {
                    win = false;
                    break;
                }
            }
            if (win) {
                return true;
            }
        }

        // Check anti-diagonal
        if (row + col == BOARD_SIZE - 1) {
            win = true;
            for (int i = 0; i < BOARD_SIZE; i++) {
                if (board[i][BOARD_SIZE - i - 1] != mark) {
                    win = false;
                    break;
                }
            }
            if (win) {
                return true;
            }
        }

        return false;
    }

    private int evaluate() {
        // Check rows
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (board[i][0] == board[i][1] && board[i][1] == board[i][2]) {
                if (board[i][0] == PLAYER_1_MARK) {
                    return -1;
                } else if (board[i][0] == PLAYER_2_MARK) {
                    return 1;
                }
            }
        }

        // Check columns
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (board[0][i] == board[1][i] && board[1][i] == board[2][i]) {
                if (board[0][i] == PLAYER_1_MARK) {
                    return -1;
                } else if (board[0][i] == PLAYER_2_MARK) {
                    return 1;
                }
            }
        }

        // Check diagonal
        if (board[0][0] == board[1][1] && board[1][1] == board[2][2]) {
            if (board[0][0] == PLAYER_1_MARK) {
                return -1;
            } else if (board[0][0] == PLAYER_2_MARK) {
                return 1;
            }
        }

        // Check anti-diagonal
        if (board[0][2] == board[1][1] && board[1][1] == board[2][0]) {
            if (board[0][2] == PLAYER_1_MARK) {
                return -1;
            } else if (board[0][2] == PLAYER_2_MARK) {
                return 1;
            }
        }

        // No winner
        return 0;
    }

    private boolean isMovesLeft() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (board[i][j] == '-') {
                    return true;
                }
            }
        }
        return false;
    }

    private int miniMax(int depth, boolean isMax) {
        int score = evaluate();

        // If player has won, return score
        if (score == 1 || score == -1) {
            return score;
        }

        // If there are no moves left, return 0
        if (!isMovesLeft()) {
            return 0;
        }

        // If it's max player's turn
        if (isMax) {
            int best = Integer.MIN_VALUE;
            for (int i = 0; i < BOARD_SIZE; i++) {
                for (int j = 0; j < BOARD_SIZE; j++) {
                    if (board[i][j] == '-') {
                        board[i][j] = PLAYER_2_MARK;
                        best = Math.max(best, miniMax(depth + 1, !isMax));
                        board[i][j] = '-';
                    }
                }
            }
            return best;
        }

        // If it's min player's turn
        else {
            int best = Integer.MAX_VALUE;
            for (int i = 0; i < BOARD_SIZE; i++) {
                for (int j = 0; j < BOARD_SIZE; j++) {
                    if (board[i][j] == '-') {
                        board[i][j] = PLAYER_1_MARK;
                        best = Math.min(best, miniMax(depth + 1, !isMax));
                        board[i][j] = '-';
                    }
                }
            }
            return best;
        }
    }

    private int[] findBestMove() {
        int bestVal = Integer.MIN_VALUE;
        int[] bestMove = new int[] { -1, -1 };

        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (board[i][j] == '-') {
                    board[i][j] = PLAYER_2_MARK;
                    int moveVal = miniMax(0, false);
                    board[i][j] = '-';

                    if (moveVal > bestVal) {
                        bestMove[0] = i;
                        bestMove[1] = j;
                        bestVal = moveVal;
                    }
                }
            }
        }

        return bestMove;
    }

    public static void main(String[] args) {
        new TicTacToe();
    }
}