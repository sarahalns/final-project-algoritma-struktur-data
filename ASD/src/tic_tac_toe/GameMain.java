package tic_tac_toe;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Stack;

/**
 * Tic-Tac-Toe: Two-player Graphic version with better OO design.
 * The Board and Cell classes are separated into their own classes.
 */
public class GameMain extends JPanel {
    private static final long serialVersionUID = 1L; // to prevent serializable warning

    // Define named constants for the drawing graphics
    public static final String TITLE = "Tic Tac Toe";
    public static final Color COLOR_BG = Color.WHITE;
    public static final Color COLOR_BG_STATUS = new Color(216, 216, 216);
    public static final Color COLOR_CROSS = new Color(218, 239, 80);  // Red #EF6950
    public static final Color COLOR_NOUGHT = new Color(192, 0, 255); // Blue #409AE1
    public static final Font FONT_STATUS = new Font(Font.SANS_SERIF, Font.PLAIN, 14);

    // Variables to track the number of wins for X and O
    private int xWins = 0;
    private int oWins = 0;

    // Define game objects
    private final Board board;         // the game board
    private State currentState;  // the current state of the game
    private Seed currentPlayer;  // the current player
    private final JLabel statusBar;    // for displaying status message

    // Define object to store moves
    public record Move(int row, int col, Seed player) {
    }

    // Stack to store moves for undo functionality
    private final Stack<Move> moveStack = new Stack<>();

    /** Constructor to set up the UI and game components */
    public GameMain() {

        // This JPanel fires MouseEvent
        super.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {  // mouse-clicked handler
                int mouseX = e.getX();
                int mouseY = e.getY();
                // Get the row and column clicked
                int row = mouseY / Cell.SIZE;
                int col = mouseX / Cell.SIZE;

                if (currentState == State.PLAYING) {
                    if (row >= 0 && row < Board.ROWS && col >= 0 && col < Board.COLS
                            && board.cells[row][col].content == Seed.NO_SEED) {
                        // Update cells[][] and return the new game state after the move
                        currentState = board.stepGame(currentPlayer, row, col);
                        // Push the move to the stack
                        moveStack.push(new Move(row, col, currentPlayer));
                        // Switch player
                        currentPlayer = (currentPlayer == Seed.CROSS) ? Seed.NOUGHT : Seed.CROSS;
                    }
                } else {        // game over
                    newGame();  // restart the game
                }
                // Refresh the drawing canvas
                repaint();  // Callback paintComponent().
            }
        });

        // Set up the status bar (JLabel) to display status message
        statusBar = new JLabel();
        statusBar.setFont(FONT_STATUS);
        statusBar.setBackground(COLOR_BG_STATUS);
        statusBar.setOpaque(true);
        statusBar.setPreferredSize(new Dimension(300, 30));
        statusBar.setHorizontalAlignment(JLabel.LEFT);
        statusBar.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 12));

        super.setLayout(new BorderLayout());
        super.add(statusBar, BorderLayout.PAGE_END); // same as SOUTH
        super.setPreferredSize(new Dimension(Board.CANVAS_WIDTH, Board.CANVAS_HEIGHT + 30));
        // account for statusBar in height
        super.setBorder(BorderFactory.createLineBorder(COLOR_BG_STATUS, 2, false));

        // Set up Game
        board = new Board();  // allocate the game-board
        initGame();
        newGame();

        // Add buttons to the panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());

        // Add "New Game" button with ActionListener
        JButton newGameButton = new JButton("New Game");
        newGameButton.addActionListener(e -> {
            newGame();
            repaint();
        });

        // Add "Reset Total" button with ActionListener
        JButton resetTotalButton = new JButton("Reset Total");
        resetTotalButton.addActionListener(e -> resetTotal());

        // Add "Undo" button with ActionListener
        JButton undoButton = new JButton("Undo");
        undoButton.addActionListener(e -> undoMove());

        // Add buttons to the button panel
        buttonPanel.add(newGameButton);
        buttonPanel.add(resetTotalButton);
        buttonPanel.add(undoButton);

        // Add the button panel to the main panel
        super.add(buttonPanel, BorderLayout.PAGE_START);
    }

    private void undoMove() {
        if (!moveStack.isEmpty()) {
            Move lastMove = moveStack.pop();
            int row = lastMove.row();
            int col = lastMove.col();
            board.cells[row][col].content = Seed.NO_SEED; // Undo the move on the game board
            currentState = State.PLAYING; // Set the game state back to playing
            currentPlayer = lastMove.player(); // Switch back to the previous player
            repaint();
        }
    }

    private void resetTotal() {
        xWins = 0;
        oWins = 0;
        repaint();  // Ensure the view is updated
    }

    /** Initialize the game (run once) */
    public void initGame() {
        // Nothing to initialize for now
    }

    /** Reset the game-board contents and the current-state, ready for a new game */
    public void newGame() {
        for (int row = 0; row < Board.ROWS; ++row) {
            for (int col = 0; col < Board.COLS; ++col) {
                board.cells[row][col].content = Seed.NO_SEED; // all cells empty
            }
        }
        currentPlayer = Seed.CROSS;    // cross plays first
        currentState = State.PLAYING;  // ready to play
        moveStack.clear();  // clear the move stack
    }

    /** Custom painting codes on this JPanel */
    @Override
    public void paintComponent(Graphics g) {  // Callback via repaint()
        super.paintComponent(g);
        setBackground(COLOR_BG); // set its background color

        board.paint(g);  // ask the game board to paint itself

        // Print status-bar message
        if (currentState == State.PLAYING) {
            statusBar.setForeground(Color.BLACK);
            statusBar.setText((currentPlayer == Seed.CROSS) ? "X's Turn" : "O's Turn");
        } else if (currentState == State.DRAW) {
            statusBar.setForeground(Color.RED);
            statusBar.setText("It's a Draw! Click to play again.");
            showGameOverMessage("It's a Draw!");
        } else if (currentState == State.CROSS_WON) {
            statusBar.setForeground(Color.RED);
            statusBar.setText("'X' Won! Click to play again.");
            xWins++;
            showGameOverMessage("'X' Won!");
        } else if (currentState == State.NOUGHT_WON) {
            statusBar.setForeground(Color.RED);
            statusBar.setText("'O' Won! Click to play again.");
            oWins++;
            showGameOverMessage("'O' Won!");
        }

        // Add win information to the status bar
        statusBar.setText((currentPlayer == Seed.CROSS) ? "X's Turn" : "O's Turn"
                + " | X Wins: " + xWins + " | O Wins: " + oWins);
    }

    /** The entry "main" method */
    public static void main(String[] args) {
        // Run GUI construction codes in Event-Dispatching thread for thread safety
        javax.swing.SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame(TITLE);
            // Set the content-pane of the JFrame to an instance of the main JPanel
            frame.setContentPane(new GameMain());
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setLocationRelativeTo(null); // center the application window
            frame.setVisible(true);            // show it
        });
    }

    private void showGameOverMessage(String message) {
        int option = JOptionPane.showConfirmDialog(this, message + "\nDo you want to play again?", "Game Over", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);

        if (option == JOptionPane.YES_OPTION) {
            newGame(); // Start a new game if the user chooses "Yes"
            repaint();
        } else {
            System.exit(0); // Exit the application if the user chooses "No"
        }
    }
}
