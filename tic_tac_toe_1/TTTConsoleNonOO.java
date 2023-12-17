package tic_tac_toe_1;

import java.util.Scanner;
import java.util.Stack;

class Move {
    int row, col;
    Move(int row, int col) {
        this.row = row;
        this.col = col;
    }
}

public class TTTConsoleNonOO {
    // Konstanta untuk pemain dan isi sel
    public static final int CROSS = 0;
    public static final int NOUGHT = 1;
    public static final int NO_SEED = 2;

    // Ukuran papan permainan
    public static final int ROWS = 3, COLS = 3;
    public static int[][] board = new int[ROWS][COLS];

    // Variabel untuk pemain saat ini dan status permainan
    public static int currentPlayer;
    public static final int PLAYING = 0;
    public static final int DRAW = 1;
    public static final int CROSS_WON = 2;
    public static final int NOUGHT_WON = 3;
    public static int currentState;

    // Scanner untuk input
    public static Scanner in = new Scanner(System.in);

    // Nama pemain
    public static String playerNameX;
    public static String playerNameO;

    // Stack untuk menyimpan langkah
    public static Stack<Move> movesStack = new Stack<>();

    public static void main(String[] args) {
        // Meminta nama pemain
        System.out.print("Masukkan nama pemain untuk 'X': ");
        playerNameX = in.nextLine();
        System.out.print("Masukkan nama pemain untuk 'O': ");
        playerNameO = in.nextLine();

        // Inisialisasi permainan
        initGame();

        // Mainkan permainan
        do {
            stepGame();
            paintBoard();
            if (currentState == CROSS_WON) {
                System.out.println(playerNameX + " menang!\nSampai jumpa!");
            } else if (currentState == NOUGHT_WON) {
                System.out.println(playerNameO + " menang!\nSampai jumpa!");
            } else if (currentState == DRAW) {
                System.out.println("Permainan Seri!\nSampai jumpa!");
            }
            currentPlayer = (currentPlayer == CROSS) ? NOUGHT : CROSS;
        } while (currentState == PLAYING);
    }

    public static void initGame() {
        for (int row = 0; row < ROWS; ++row) {
            for (int col = 0; col < COLS; ++col) {
                board[row][col] = NO_SEED;
            }
        }
        currentPlayer = CROSS;
        currentState = PLAYING;
    }

    public static void stepGame() {
        boolean validInput = false;
        do {
            System.out.print("Apakah Anda ingin membatalkan langkah sebelumnya? (ya/tidak): ");
            String undoResponse = in.nextLine().trim().toLowerCase();
            if (undoResponse.equals("ya")) {
                undoMove();
                paintBoard();
            }

            String currentPlayerName = (currentPlayer == CROSS) ? playerNameX : playerNameO;
            System.out.print("Giliran " + currentPlayerName + ", masukkan langkah Anda (baris[1-3] kolom[1-3]): ");
            int row = in.nextInt() - 1;
            int col = in.nextInt() - 1;
            in.nextLine(); // Mengonsumsi baris input yang tersisa

            if (row >= 0 && row < ROWS && col >= 0 && col < COLS && board[row][col] == NO_SEED) {
                board[row][col] = currentPlayer;
                movesStack.push(new Move(row, col)); // Menyimpan langkah ke stack
                currentState = checkCurrentState(currentPlayer, row, col);
                validInput = true;
            } else {
                System.out.println("Langkah ini di (" + (row + 1) + "," + (col + 1) + ") tidak valid. Coba lagi...");
            }
        } while (!validInput);
    }

    public static int checkCurrentState(int player, int selectedRow, int selectedCol) {
        // Update game board
        board[selectedRow][selectedCol] = player;

        // Check for win condition
        if ((board[selectedRow][0] == player && board[selectedRow][1] == player && board[selectedRow][2] == player) ||
                (board[0][selectedCol] == player && board[1][selectedCol] == player && board[2][selectedCol] == player) ||
                (selectedRow == selectedCol && board[0][0] == player && board[1][1] == player && board[2][2] == player) ||
                (selectedRow + selectedCol == 2 && board[0][2] == player && board[1][1] == player && board[2][0] == player)) {
            return (player == CROSS) ? CROSS_WON : NOUGHT_WON;
        }

        // Check for draw
        for (int row = 0; row < ROWS; ++row) {
            for (int col = 0; col < COLS; ++col) {
                if (board[row][col] == NO_SEED) {
                    return PLAYING;
                }
            }
        }
        return DRAW;
    }

    public static void undoMove() {
        if (!movesStack.isEmpty()) {
            Move lastMove = movesStack.pop();
            board[lastMove.row][lastMove.col] = NO_SEED;
            currentPlayer = (currentPlayer == CROSS) ? NOUGHT : CROSS; // Ganti pemain
        }
    }

    public static void paintBoard() {
        for (int row = 0; row < ROWS; ++row) {
            for (int col = 0; col < COLS; ++col) {
                paintCell(board[row][col]);
                if (col != COLS - 1) {
                    System.out.print("|");
                }
            }
            System.out.println();
            if (row != ROWS - 1) {
                System.out.println("-----------");
            }
        }
        System.out.println();
    }

    public static void paintCell(int content) {
        switch (content) {
            case CROSS:
                System.out.print(" X ");
                break;
            case NOUGHT:
                System.out.print(" O ");
                break;
            case NO_SEED:
                System.out.print("   ");
                break;
        }
    }
}
