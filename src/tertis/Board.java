package tertis;

import processing.core.*;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Queue;

import static java.util.Collections.swap;

public class Board extends PApplet {
    // Maybe put these somewhere else?
    public static final float FPS = 180;
    public static final float MSPF = 1000 / FPS;

    public static final int BOARD_HEIGHT = 40;
    public static final int BOARD_WIDTH = 10;
    public static final int DISPLAY_BOARD_PAD = 1;
    public static final int DISPLAY_BOARD_HEIGHT = 20 + DISPLAY_BOARD_PAD;
    public static final float TILE_SIZE = 30;
    public static final int BAG_SIZE = 7;
    public static final int PREVIEW_ROWS = 2;
    public static final int PREVIEW_COLS = 4;
    public static final int PREVIEWS = 5;

    public static GameState gameState;

    public static DisplayColor[][] displayBoard = new DisplayColor[BOARD_HEIGHT][BOARD_WIDTH];
    public static byte[][] board = new byte[BOARD_HEIGHT][BOARD_WIDTH];

    public static Queue<Character> next = new ArrayDeque<>();

    /**
     * Generates a randomly permuted "bag" of pieces and adds them to the next queue.
     */
    public static void generateBag() {
        // Initialize bag of pieces
        ArrayList<Character> pieces = new ArrayList<>(
                Arrays.asList('I', 'O', 'T', 'S', 'Z', 'J', 'L')
        );

        // Fisher-Yates shuffle I guess? Thanks, Wikipedia.
        for (int i = 0; i < pieces.size() - 2; i++) {
            int j = floor(i + (float) (Math.random() * (pieces.size() - i)));
            swap(pieces, i, j);
        }

        // Add each piece to the queue
        next.addAll(pieces);
    }

    /**
     * @return line number of a full line, {@code -1} if none exist
     */
    private static int fullLine() {
        for (int i = 0; i < BOARD_HEIGHT; i++) {
            boolean lineFull = true;

            // & all the elements in row i
            for (int j = 0; j < BOARD_WIDTH; j++)
                lineFull &= board[i][j] == 1;

            // Return the line number if a full line is found
            if (lineFull)
                return i;
        }

        // Return -1 if no lines were full
        return -1;
    }

    /**
     * Function for handling line clearing logic. If a line is full, it will be deleted and all
     * lines above it will be moved down.
     */
    public static void lineClear() {
        while (fullLine() >= 0) {
            // Copy all the lines above the full line downward by 1 (display and actual)
            for (int i = fullLine(); i < BOARD_HEIGHT - 1; i++) {
                arrayCopy(board[i + 1], board[i]);
                arrayCopy(displayBoard[i + 1], displayBoard[i]);
            }

            // Clear out the top row (display and actual)
            for (int i = 0; i < BOARD_WIDTH; i++) {
                board[BOARD_HEIGHT - 1][i] = 0;
                displayBoard[BOARD_HEIGHT - 1][i] = null;
            }
        }
    }

    /**
     * Game over logic. Basically sets all tiles on the display board to gray and then changes the
     * game state to GAMEOVER.
     */
    public static void gameOver() {
        for (int i = 0; i < BOARD_HEIGHT; i++) {
            for (int j = 0; j < BOARD_WIDTH; j++) {
                if (displayBoard[i][j] != null)
                    displayBoard[i][j] = DisplayColor.DARKGRAY;
            }
        }

        gameState = GameState.GAMEOVER;
    }
}
