package tertis;

import processing.core.*;

public class Board extends PApplet {
    // Maybe put these somewhere else?
    public static final double fps = 180;
    public static final double mspf = 1000 / fps;

    public static final int boardHeight = 40;
    public static final int boardWidth = 10;
    public static final int displayBoardPad = 1;
    public static final int displayBoardHeight = 20 + displayBoardPad;

    public static DisplayColor[][] displayBoard = new DisplayColor[boardHeight][boardWidth];
    public static byte[][] board = new byte[boardHeight][boardWidth];
}
