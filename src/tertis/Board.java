package tertis;

import processing.core.*;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Queue;

import static java.util.Collections.swap;

public class Board extends PApplet {
    // Maybe put these somewhere else?
    public static final float fps = 180;
    public static final float mspf = 1000 / fps;

    public static final int boardHeight = 40;
    public static final int boardWidth = 10;
    public static final int displayBoardPad = 1;
    public static final int displayBoardHeight = 20 + displayBoardPad;
    public static final float tileSize = 30;
    public static final int bagSize = 7;
    public static final int previewRows = 2;
    public static final int previewCols = 4;
    public static final int previews = 5;

    public static DisplayColor[][] displayBoard = new DisplayColor[boardHeight][boardWidth];
    public static byte[][] board = new byte[boardHeight][boardWidth];

    public static Queue<Character> next = new ArrayDeque<>();
    public static Character hold = null;

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
        for (int i = 0; i < boardHeight; i++) {
            boolean lineFull = true;

            // & all the elements in row i
            for (int j = 0; j < boardWidth; j++)
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
            // Copy all the lines above the full row down 1 (display and actual)
            for (int i = fullLine(); i < boardHeight - 1; i++) {
                arrayCopy(board[i + 1], board[i]);
                arrayCopy(displayBoard[i + 1], displayBoard[i]);
            }

            // Clear out the top row (display and actual)
            for (int i = 0; i < boardWidth; i++) {
                board[boardHeight - 1][i] = 0;
                displayBoard[boardHeight - 1][i] = null;
            }
        }
    }
}
