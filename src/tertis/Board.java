package tertis;

import processing.core.*;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Queue;

import static java.util.Collections.swap;

public class Board extends PApplet {
    // Maybe put these somewhere else?
    public static final double fps = 180;
    public static final double mspf = 1000 / fps;

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

    public static void generateBag() {
        ArrayList<Character> pieces = new ArrayList<>();

        pieces.add('I');
        pieces.add('O');
        pieces.add('T');
        pieces.add('S');
        pieces.add('Z');
        pieces.add('J');
        pieces.add('L');

        for (int i = 0; i < pieces.size() - 2; i++) {
            int j = floor(i + (float) (Math.random() * (pieces.size() - i)));
            swap(pieces, i, j);
        }

        for (Character i : pieces) {
            System.out.print(i + ", ");
            next.offer(i);
        }

        System.out.println();
    }

    private static int fullLine() {
        for (int i = 0; i < boardHeight; i++) {
            boolean lineFull = true;

            for (int j = 0; j < boardWidth; j++)
                lineFull &= board[i][j] == 1;

            if (lineFull)
                return i;
        }

        return -1;
    }

    public static void lineClear() {
        if (fullLine() != -1)
            System.out.println(fullLine());
        while (fullLine() >= 0) {
            for (int i = fullLine(); i < boardHeight - 1; i++) {
                arrayCopy(board[i + 1], board[i]);
                arrayCopy(displayBoard[i + 1], displayBoard[i]);
            }

            for (int i = 0; i < boardWidth; i++) {
                board[boardHeight - 1][i] = 0;
                displayBoard[boardHeight - 1][i] = null;
            }
        }
    }
}
