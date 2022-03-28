package tertis;

import processing.core.*;

import java.util.ArrayDeque;
import java.util.Queue;

public class Tertis extends PApplet {
    public void setup() {
        frameRate((float) Board.fps);

        //temp.testPieceSetup();

        Key.keyBind.put("move_up",    "w");
        Key.keyBind.put("move_down",  "s");
        Key.keyBind.put("move_left",  "a");
        Key.keyBind.put("move_right", "d");
        Key.keyBind.put("move_drop",  " ");
        Key.keyBind.put("move_hold",  "q");
        Key.keyBind.put("rot_ccw",    "37");
        Key.keyBind.put("rot_180",    "38");
        Key.keyBind.put("rot_cw",     "39");

        // Jonah controls
//        Key.keyBind.put("move_up",    "w");
//        Key.keyBind.put("move_down",  "40");
//        Key.keyBind.put("move_left",  "37");
//        Key.keyBind.put("move_right", "39");
//        Key.keyBind.put("move_drop",  "38");
//        Key.keyBind.put("move_hold",  " ");
//        Key.keyBind.put("rot_ccw",    "c");
//        Key.keyBind.put("rot_180",    "b");
//        Key.keyBind.put("rot_cw",     "v");
    }

    public void keyPressed() {
        String keyString;

        // Set keyString to either the key's text or the keyCode as a string
        if (key == CODED)
            keyString = String.valueOf(keyCode);
        else
            keyString = String.valueOf(key).toLowerCase();

        if (Key.keys.get(keyString) != null && Key.keys.get(keyString)) {
            // Nothing if the key is still being held
        } else {
            // Set keys value to true, and put the current frame number in keyFrame
            Key.keyFrame.put(keyString, frameCount);
            Key.keys.put(keyString, Boolean.TRUE);
        }
    }

    public void keyReleased() {
        String keyString;

        // Set keyString to either the key's text or the keyCode as a string
        if (key == CODED)
            keyString = String.valueOf(keyCode).toLowerCase();
        else
            keyString = String.valueOf(key).toLowerCase();

        // Set keys value to false
        Key.keys.put(keyString, Boolean.FALSE);
    }

    public void draw() {
        background(0);

        // If the queue has less than 7 pieces, add a new bag's worth of pieces
        if (Board.next.size() < Board.bagSize)
            Board.generateBag();

        // Hold
        if (Key.frame(Key.keyBind.get("move_hold")) != null && Key.frame(Key.keyBind.get("move_hold")) == frameCount - 1 && !Piece.heldThisRound) {
            Piece.testPiece = Piece.hold(Piece.testPiece);
            Piece.heldThisRound = true;
        }

        if (Piece.testPiece == null) {
            //System.out.println("Generating new " + Board.next.peek() + " piece");
            Piece.testPiece = new Piece(Board.next.remove());
            Piece.heldThisRound = false;
        }

        Piece p = Piece.testPiece;

        boolean lockPiece = false;

        // Directional moves
        if (Key.repeat(Key.keyBind.get("move_up"), frameCount))
            p.move(Direction.UP);
        if (Key.repeat(Key.keyBind.get("move_left"), frameCount))
            p.move(Direction.LEFT);
        if (Key.repeat(Key.keyBind.get("move_right"), frameCount))
            p.move(Direction.RIGHT);
        // Soft drop
        if (Key.repeat(Key.keyBind.get("move_down"), frameCount))
            while (p.move(Direction.DOWN));
        // Hard drop
        if (Key.frame(Key.keyBind.get("move_drop")) != null && Key.frame(Key.keyBind.get("move_drop")) == frameCount - 1) {
            while (p.move(Direction.DOWN));
            lockPiece = true;
        }

        // Rotations
        if (Key.frame(Key.keyBind.get("rot_ccw")) != null && Key.frame(Key.keyBind.get("rot_ccw")) == frameCount - 1)
            p.rotate(Rotation.COUNTERCLOCKWISE);
        if (Key.frame(Key.keyBind.get("rot_180")) != null && Key.frame(Key.keyBind.get("rot_180")) == frameCount - 1)
            p.rotate(Rotation.ONEEIGHTY);
        if (Key.frame(Key.keyBind.get("rot_cw")) != null && Key.frame(Key.keyBind.get("rot_cw")) == frameCount - 1)
            p.rotate(Rotation.CLOCKWISE);

        Piece.renderGhost(p);
        p.display();

        if (lockPiece) {
            Piece.lock(Piece.testPiece);
            Piece.testPiece = null;
        }

        Board.lineClear();
        drawBoard();
        keyTestDraw();
    }

    public void keyTestDraw() {
        float keyWidth = 50;

        float keyOverlayX = 90;
        float keyOverlayY = 350;

        // Fill each key in the overlay if it is pressed
        rectMode(CORNER);

        noStroke();
        fill(255);
        if (Key.keys.get("q") != null && Key.keys.get("q"))
            rect(keyOverlayX, keyOverlayY, keyWidth - 2, keyWidth - 2);
        if (Key.keys.get("w") != null && Key.keys.get("w"))
            rect(keyOverlayX + keyWidth, keyOverlayY, keyWidth  - 2, keyWidth - 2);
        if (Key.keys.get("a") != null && Key.keys.get("a"))
            rect(keyOverlayX, keyOverlayY + keyWidth, keyWidth - 2, keyWidth - 2);
        if (Key.keys.get("s") != null && Key.keys.get("s"))
            rect(keyOverlayX + keyWidth, keyOverlayY + keyWidth, keyWidth - 2, keyWidth - 2);
        if (Key.keys.get("d") != null && Key.keys.get("d"))
            rect(keyOverlayX + keyWidth * 2, keyOverlayY + keyWidth, keyWidth - 2, keyWidth - 2);
        if (Key.keys.get(" ") != null && Key.keys.get(" "))
            rect(keyOverlayX, keyOverlayY + keyWidth * 2, keyWidth * 3  - 2, keyWidth - 2);
        if (Key.keys.get("38") != null && Key.keys.get("38"))
            rect(keyOverlayX + keyWidth, keyOverlayY + keyWidth * 4, keyWidth - 2, keyWidth - 2);
        if (Key.keys.get("37") != null && Key.keys.get("37"))
            rect(keyOverlayX, keyOverlayY + keyWidth * 5, keyWidth - 2, keyWidth - 2);
        if (Key.keys.get("40") != null && Key.keys.get("40"))
            rect(keyOverlayX + keyWidth, keyOverlayY + keyWidth * 5, keyWidth - 2, keyWidth - 2);
        if (Key.keys.get("39") != null && Key.keys.get("39"))
            rect(keyOverlayX + keyWidth * 2, keyOverlayY + keyWidth * 5, keyWidth - 2, keyWidth - 2);

        // Key overlay squares
        strokeWeight(1);
        stroke(128);
        noFill();

        rect(keyOverlayX - 1,                keyOverlayY - 1,                keyWidth - 1, keyWidth - 1);
        rect(keyOverlayX + keyWidth - 1,     keyOverlayY - 1,                keyWidth - 1, keyWidth - 1);
        rect(keyOverlayX - 1,                keyOverlayY + keyWidth - 1,     keyWidth - 1, keyWidth - 1);
        rect(keyOverlayX + keyWidth - 1,     keyOverlayY + keyWidth - 1,     keyWidth - 1, keyWidth - 1);
        rect(keyOverlayX + keyWidth * 2 - 1, keyOverlayY + keyWidth - 1,     keyWidth - 1, keyWidth - 1);
        rect(keyOverlayX - 1,                keyOverlayY + keyWidth * 2 - 1, keyWidth * 3 - 1, keyWidth - 1);
        rect(keyOverlayX + keyWidth - 1,     keyOverlayY + keyWidth * 4 - 1, keyWidth - 1, keyWidth - 1);
        rect(keyOverlayX - 1,                keyOverlayY + keyWidth * 5 - 1, keyWidth - 1, keyWidth - 1);
        rect(keyOverlayX + keyWidth - 1,     keyOverlayY + keyWidth * 5 - 1, keyWidth - 1, keyWidth - 1);
        rect(keyOverlayX + keyWidth * 2 - 1, keyOverlayY + keyWidth * 5 - 1, keyWidth - 1, keyWidth - 1);

        // Key overlay text
        fill(128);

        text("Q", keyOverlayX, keyOverlayY + keyWidth - 5);
        text("W", keyOverlayX + keyWidth, keyOverlayY + keyWidth - 5);
        text("A", keyOverlayX, keyOverlayY + keyWidth * 2 - 5);
        text("S", keyOverlayX + keyWidth, keyOverlayY + keyWidth * 2 - 5);
        text("D", keyOverlayX + keyWidth * 2, keyOverlayY + keyWidth * 2 - 5);
        text("Space", keyOverlayX, keyOverlayY + keyWidth * 3 - 5);
        text("↑", keyOverlayX + keyWidth, keyOverlayY + keyWidth * 5 - 5);
        text("←", keyOverlayX, keyOverlayY + keyWidth * 6 - 5);
        text("↓", keyOverlayX + keyWidth, keyOverlayY + keyWidth * 6 - 5);
        text("→", keyOverlayX + keyWidth * 2, keyOverlayY + keyWidth * 6 - 5);
    }

    public void drawBoard() {
        float boardX = 480;
        float boardY = 670;

        // Draw grid dots? I think?
        for (int i = 0; i <= Board.displayBoardHeight; i++) {
            for (int j = 0; j <= Board.boardWidth; j++) {
                strokeWeight(2);
                stroke(128);
                point((boardX - Board.tileSize * ((Board.boardWidth / 2.0f) - 0.5f) + j * Board.tileSize) - Board.tileSize / 2, (boardY - i * Board.tileSize) + Board.tileSize / 2);
            }
        }

        rectMode(CENTER);
        for (int i = 0; i < Board.displayBoardHeight; i++) {
            for (int j = 0; j < Board.boardWidth; j++) {
                // Draw square if square exists
                if (Board.displayBoard[i][j] != null) {
                    fill(Board.displayBoard[i][j].getR(), Board.displayBoard[i][j].getG(), Board.displayBoard[i][j].getB(), Board.displayBoard[i][j].getA());
                    noStroke();
                    rect(boardX - Board.tileSize * ((Board.boardWidth / 2.0f) - 0.5f) + j * Board.tileSize, boardY - i * Board.tileSize, Board.tileSize, Board.tileSize);
                }

                // Draw points at center of all cells
                //strokeWeight(5);
                //stroke(255);
                //point(boardX - Board.tileSize * ((boardWidth / 2.0) - 0.5 ) + j * Board.tileSize, boardY - i * Board.tileSize);
            }
        }

        // Black out the space above the grid and draw the border around the play area
        rectMode(CORNERS);
        fill(0);
        rect(0, 0, width, boardY - (Board.displayBoardHeight - Board.displayBoardPad) * Board.tileSize);
        strokeWeight(3);
        stroke(128);
        fill(0, 0, 0, 0);
        rect(boardX + Board.tileSize * Board.boardWidth / 2 + 1, boardY + Board.tileSize / 2 + 1,
             boardX - Board.tileSize * Board.boardWidth / 2 - 2, boardY - (Board.displayBoardHeight - Board.displayBoardPad) * Board.tileSize);

        // Draw preview pieces + hold piece
        stroke(0, 255, 0);
        strokeWeight(5);
        Queue<Character> nextCopy = new ArrayDeque<>(Board.next);
        for (int i = 0; i < Board.previews; i++)
            drawPreview(700, 160 + i * 3 * Board.tileSize, nextCopy.remove());
        if (Piece.hold != null)
            drawPreview(200, 160, Piece.hold, Piece.heldThisRound);
    }

    public void drawPreview(float x, float y, char type) {
        drawPreview(x, y, type, false);
    }

    public void drawPreview(float x, float y, char type, boolean heldThisRound) {
        // Initialize grid used for storing next queue piece tiles
        DisplayColor[][] nextDisplay = new DisplayColor[Board.previewRows][Board.previewCols];

        switch (type) {
            /*
             * Each case sets the cells in nextDisplay based on the given piece
             */

            case 'I' -> {
                nextDisplay[0][0] = DisplayColor.I;
                nextDisplay[0][1] = DisplayColor.I;
                nextDisplay[0][2] = DisplayColor.I;
                nextDisplay[0][3] = DisplayColor.I;
            }

            case 'O' -> {
                nextDisplay[1][1] = DisplayColor.O;
                nextDisplay[1][2] = DisplayColor.O;
                nextDisplay[0][1] = DisplayColor.O;
                nextDisplay[0][2] = DisplayColor.O;
            }

            case 'T' -> {
                nextDisplay[1][1] = DisplayColor.T;
                nextDisplay[0][0] = DisplayColor.T;
                nextDisplay[0][1] = DisplayColor.T;
                nextDisplay[0][2] = DisplayColor.T;
            }

            case 'S' -> {
                nextDisplay[1][1] = DisplayColor.S;
                nextDisplay[1][2] = DisplayColor.S;
                nextDisplay[0][0] = DisplayColor.S;
                nextDisplay[0][1] = DisplayColor.S;
            }

            case 'Z' -> {
                nextDisplay[1][0] = DisplayColor.Z;
                nextDisplay[1][1] = DisplayColor.Z;
                nextDisplay[0][1] = DisplayColor.Z;
                nextDisplay[0][2] = DisplayColor.Z;
            }

            case 'J' -> {
                nextDisplay[1][0] = DisplayColor.J;
                nextDisplay[0][0] = DisplayColor.J;
                nextDisplay[0][1] = DisplayColor.J;
                nextDisplay[0][2] = DisplayColor.J;
            }

            case 'L' -> {
                nextDisplay[1][2] = DisplayColor.L;
                nextDisplay[0][0] = DisplayColor.L;
                nextDisplay[0][1] = DisplayColor.L;
                nextDisplay[0][2] = DisplayColor.L;
            }
        }

        try {
            rectMode(CENTER);
            for (int i = 0; i < Board.previewRows; i++) {
                for (int j = 0; j < Board.previewCols; j++) {
                    // Draw each square if it should be drawn, and then clear it out after drawing
                    if (nextDisplay[i][j] != null) {
                        if (!heldThisRound)
                            fill(nextDisplay[i][j].getR(), nextDisplay[i][j].getG(), nextDisplay[i][j].getB());
                        else
                            fill(DisplayColor.HOLD.getR(), DisplayColor.HOLD.getG(), DisplayColor.HOLD.getB());

                        noStroke();
                        rect(x + j * Board.tileSize, y - i * Board.tileSize, Board.tileSize, Board.tileSize);
                        nextDisplay[i][j] = null;
                    }
                }
            }
        } catch (NullPointerException ignored) {} // Weird NullPointerException idk
    }

    public void settings() {
        size(960, 720);
    }

    public static void main(String[] args) {
        PApplet.main("tertis.Tertis");
    }
}
