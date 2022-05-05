package tertis;

import processing.core.*;

import java.io.FileNotFoundException;
import java.util.ArrayDeque;
import java.util.Queue;

public class Tertis extends PApplet {
    public void setup() {
        frameRate((float) Board.FPS);

        //temp.testPieceSetup();

        // 4-wide mode
        /*for (int i = 0; i < Board.BOARD_HEIGHT; i++) {
            for (int j = 0; j <Board.BOARD_WIDTH; j++) {
                if (j < 3 || j > 6) {
                    Board.board[i][j] = 1;
                    Board.displayBoard[i][j] = DisplayColor.LIGHTGRAY;
                }
            }
        }
        Board.board[0][3] = 1;
        Board.displayBoard[0][3] = DisplayColor.LIGHTGRAY;
        Board.board[0][4] = 1;
        Board.displayBoard[0][4] = DisplayColor.LIGHTGRAY;
        Board.board[0][5] = 1;
        Board.displayBoard[0][5] = DisplayColor.LIGHTGRAY;*/

        // Loading from file test
        try {
            Board.setFromFile();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Key.beyKindings();

        Board.gameState = GameState.GAMING;
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
            //System.out.println("Pressed key " + keyString);
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

        switch (Board.gameState) {
            case GAMING -> {
                // Reset
                if (Key.frame(Key.keyBind.get("misc_reset")) != null && Key.frame(Key.keyBind.get("misc_reset")) == frameCount - 1)
                    Board.clearBoard();
                // Load from file
                if (Key.frame(Key.keyBind.get("misc_loadfile")) != null && Key.frame(Key.keyBind.get("misc_loadfile")) == frameCount - 1) {
                    try {
                        Board.setFromFile();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                // Cycle keybinds
                if (Key.frame(Key.keyBind.get("misc_keys")) != null && Key.frame(Key.keyBind.get("misc_keys")) == frameCount - 1)
                    Key.beyKindings();

                // If the queue has less than 7 pieces, add a new bag's worth of pieces
                if (Board.next.size() < Board.BAG_SIZE)
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

                boolean lockPiece = false;

                // Rotations
                if (Key.frame(Key.keyBind.get("rot_ccw")) != null && Key.frame(Key.keyBind.get("rot_ccw")) == frameCount - 1)
                    Piece.testPiece.rotate(Rotation.COUNTERCLOCKWISE);
                if (Key.frame(Key.keyBind.get("rot_180")) != null && Key.frame(Key.keyBind.get("rot_180")) == frameCount - 1)
                    Piece.testPiece.rotate(Rotation.ONEEIGHTY);
                if (Key.frame(Key.keyBind.get("rot_cw")) != null && Key.frame(Key.keyBind.get("rot_cw")) == frameCount - 1)
                    Piece.testPiece.rotate(Rotation.CLOCKWISE);

                // Directional moves
                if (Key.repeat(Key.keyBind.get("move_up"), frameCount))
                    Piece.testPiece.move(Direction.UP);
                if (Key.repeat(Key.keyBind.get("move_left"), frameCount))
                    Piece.testPiece.move(Direction.LEFT);
                if (Key.repeat(Key.keyBind.get("move_right"), frameCount))
                    Piece.testPiece.move(Direction.RIGHT);
                // Soft drop
                if (Key.keys.get(Key.keyBind.get("move_down")) != null && Key.keys.get(Key.keyBind.get("move_down")))
                    while (Piece.testPiece.move(Direction.DOWN)) ;
                // Hard drop
                if (Key.frame(Key.keyBind.get("move_drop")) != null && Key.frame(Key.keyBind.get("move_drop")) == frameCount - 1) {
                    while (Piece.testPiece.move(Direction.DOWN)) ;
                    lockPiece = true;
                }

                Piece.renderGhost(Piece.testPiece);
                Piece.testPiece.display();

                boolean lockOut = false;
                if (lockPiece) {
                    Piece.lock(Piece.testPiece);
                    lockOut = Piece.testPiece.getLowestTile().getRow() >= Board.DISPLAY_BOARD_HEIGHT - Board.DISPLAY_BOARD_PAD;
                    Piece.testPiece = null;
                }

                if (!lockPiece && Piece.testPiece.isClipping()) {
                    Board.gameOver();
                } else if (lockPiece && lockOut) {
                    Board.gameOver();
                } else {
                    Board.lineClear();
                    drawBoard();
                    keyTestDraw();
                }
            }

            case GAMEOVER -> {
                drawBoard();
                keyTestDraw();

                // Reset
                if (Key.frame(Key.keyBind.get("misc_reset")) != null && Key.frame(Key.keyBind.get("misc_reset")) == frameCount - 1) {
                    Board.clearBoard();
                    Board.gameState = GameState.GAMING;
                }

                noStroke();
                fill(128);
                textSize(500);
                text("GAME", -150, 350);
                text("OVER", -120, 710);
                fill(255);
                textSize(50);
                text("F4 to restart", 354, 380);
            }
        }
    }

    public void keyTestDraw() {
        float keyWidth = 50;

        float keyOverlayX = 90;
        float keyOverlayY = 350;

        // Fill each key in the overlay if it is pressed
        rectMode(CORNER);
        noStroke();
        fill(255);

        {
        if (Key.keys.get("q") != null && Key.keys.get("q"))
            rect(keyOverlayX, keyOverlayY, keyWidth - 2, keyWidth - 2);
        if (Key.keys.get("w") != null && Key.keys.get("w"))
            rect(keyOverlayX + keyWidth, keyOverlayY, keyWidth - 2, keyWidth - 2);
        if (Key.keys.get("z") != null && Key.keys.get("z"))
            rect(keyOverlayX, keyOverlayY + keyWidth, keyWidth - 2, keyWidth - 2);
        if (Key.keys.get("x") != null && Key.keys.get("x"))
            rect(keyOverlayX + keyWidth, keyOverlayY + keyWidth, keyWidth - 2, keyWidth - 2);
        if (Key.keys.get("c") != null && Key.keys.get("c"))
            rect(keyOverlayX + keyWidth * 2, keyOverlayY + keyWidth, keyWidth - 2, keyWidth - 2);
        if (Key.keys.get(" ") != null && Key.keys.get(" "))
            rect(keyOverlayX, keyOverlayY + keyWidth * 2, keyWidth * 3 - 2, keyWidth - 2);
        if (Key.keys.get("38") != null && Key.keys.get("38"))
            rect(keyOverlayX + keyWidth, keyOverlayY + keyWidth * 4, keyWidth - 2, keyWidth - 2);
        if (Key.keys.get("37") != null && Key.keys.get("37"))
            rect(keyOverlayX, keyOverlayY + keyWidth * 5, keyWidth - 2, keyWidth - 2);
        if (Key.keys.get("40") != null && Key.keys.get("40"))
            rect(keyOverlayX + keyWidth, keyOverlayY + keyWidth * 5, keyWidth - 2, keyWidth - 2);
        if (Key.keys.get("39") != null && Key.keys.get("39"))
            rect(keyOverlayX + keyWidth * 2, keyOverlayY + keyWidth * 5, keyWidth - 2, keyWidth - 2);
        }

        // Key overlay squares
        strokeWeight(1);
        stroke(128);
        noFill();

        {
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
        }

        // Key overlay text
        textSize(20);
        fill(128);

        // TODO: make overlay less janky
        {
        text("Q", keyOverlayX, keyOverlayY + keyWidth - 5);
        text("W", keyOverlayX + keyWidth, keyOverlayY + keyWidth - 5);
        text("Z", keyOverlayX, keyOverlayY + keyWidth * 2 - 5);
        text("X", keyOverlayX + keyWidth, keyOverlayY + keyWidth * 2 - 5);
        text("C", keyOverlayX + keyWidth * 2, keyOverlayY + keyWidth * 2 - 5);
        text("Space", keyOverlayX, keyOverlayY + keyWidth * 3 - 5);
        text("↑", keyOverlayX + keyWidth, keyOverlayY + keyWidth * 5 - 5);
        text("←", keyOverlayX, keyOverlayY + keyWidth * 6 - 5);
        text("↓", keyOverlayX + keyWidth, keyOverlayY + keyWidth * 6 - 5);
        text("→", keyOverlayX + keyWidth * 2, keyOverlayY + keyWidth * 6 - 5);
        }

        text("[F5] Controls: " + Key.controlPreset, keyOverlayX, keyOverlayY - 20);
    }

    public void drawBoard() {
        float boardX = 480;
        float boardY = 670;

        // Draw grid dots? I think?
        for (int i = 0; i <= Board.DISPLAY_BOARD_HEIGHT; i++) {
            for (int j = 0; j <= Board.BOARD_WIDTH; j++) {
                strokeWeight(2);
                stroke(128);
                point((boardX - Board.TILE_SIZE * ((Board.BOARD_WIDTH / 2.0f) - 0.5f) + j * Board.TILE_SIZE) - Board.TILE_SIZE / 2,
                        (boardY - i * Board.TILE_SIZE) + Board.TILE_SIZE / 2);
            }
        }

        rectMode(CENTER);
        for (int i = 0; i < Board.DISPLAY_BOARD_HEIGHT; i++) {
            for (int j = 0; j < Board.BOARD_WIDTH; j++) {
                // Draw square if square exists
                if (Board.displayBoard[i][j] != null) {
                    fill(Board.displayBoard[i][j].getR(), Board.displayBoard[i][j].getG(),
                         Board.displayBoard[i][j].getB(), Board.displayBoard[i][j].getA());
                    noStroke();
                    rect(boardX - Board.TILE_SIZE * ((Board.BOARD_WIDTH / 2.0f) - 0.5f) + j * Board.TILE_SIZE, boardY - i * Board.TILE_SIZE, Board.TILE_SIZE, Board.TILE_SIZE);
                }

                // Draw points at center of all cells
                //strokeWeight(5);
                //stroke(255);
                //point(boardX - Board.tileSize * ((boardWidth / 2.0) - 0.5 ) + j * Board.tileSize, boardY - i * Board.tileSize);
            }
        }

        // Long variable
        float borderTop = boardY - (Board.DISPLAY_BOARD_HEIGHT - Board.DISPLAY_BOARD_PAD) * Board.TILE_SIZE + Board.TILE_SIZE / 3;

        // Black out the space above the grid
        rectMode(CORNERS);
        fill(0);
        rect(0, 0, width, borderTop);

        // Border around the play area
        strokeWeight(3);
        stroke(128);
        fill(0, 0, 0, 0);
        rect(boardX + Board.TILE_SIZE * Board.BOARD_WIDTH / 2 + 1, boardY + Board.TILE_SIZE / 2 + 1,
             boardX - Board.TILE_SIZE * Board.BOARD_WIDTH / 2 - 2, borderTop);

        // Draw preview pieces + hold piece
        stroke(0, 255, 0);
        strokeWeight(5);
        Queue<Character> nextCopy = new ArrayDeque<>(Board.next);
        for (int i = 0; i < Board.PREVIEWS; i++)
            drawPreview(700, 160 + i * 3 * Board.TILE_SIZE, nextCopy.remove());
        if (Piece.hold != null)
            drawPreview(200, 160, Piece.hold, Piece.heldThisRound);
    }

    public void drawPreview(float x, float y, char type) {
        drawPreview(x, y, type, false);
    }

    public void drawPreview(float x, float y, char type, boolean heldThisRound) {
        // Initialize grid used for storing next queue piece tiles
        DisplayColor[][] nextDisplay = new DisplayColor[Board.PREVIEW_ROWS][Board.PREVIEW_COLS];

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
            for (int i = 0; i < Board.PREVIEW_ROWS; i++) {
                for (int j = 0; j < Board.PREVIEW_COLS; j++) {
                    // Draw each square if it should be drawn, and then clear it out after drawing
                    if (nextDisplay[i][j] != null) {
                        if (!heldThisRound)
                            fill(nextDisplay[i][j].getR(), nextDisplay[i][j].getG(), nextDisplay[i][j].getB());
                        else
                            fill(DisplayColor.DARKGRAY.getR(), DisplayColor.DARKGRAY.getG(), DisplayColor.DARKGRAY.getB());

                        noStroke();
                        rect(x + j * Board.TILE_SIZE, y - i * Board.TILE_SIZE, Board.TILE_SIZE, Board.TILE_SIZE);
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
