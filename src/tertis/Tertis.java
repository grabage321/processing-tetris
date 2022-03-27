package tertis;

import processing.core.*;

import java.util.ArrayDeque;
import java.util.Queue;

public class Tertis extends PApplet {
    public void setup() {
        frameRate((float) Board.fps);

        temp.testPieceSetup();
    }

    public void keyPressed() {
        String keyString;

        if (key == CODED)
            keyString = String.valueOf(keyCode);
        else
            keyString = String.valueOf(key).toLowerCase();


        if (Key.keys.get(keyString) != null && Key.keys.get(keyString)) {
            //nothing
        } else {
            Key.keyFrame.put(keyString, frameCount);
            Key.keys.put(keyString, Boolean.TRUE);
        }
    }

    public void keyReleased() {
        String keyString;

        if (key == CODED)
            keyString = String.valueOf(keyCode).toLowerCase();
        else
            keyString = String.valueOf(key).toLowerCase();


        Key.keys.put(keyString, Boolean.FALSE);
    }

    public void draw() {
        background(0);

        if (Board.next.size() < Board.bagSize)
            Board.generateBag();

        // Hold
        if (Key.frame("q") != null && Key.frame("q") == frameCount - 1)
            Piece.testPiece = Piece.hold(Piece.testPiece);

        if (Piece.testPiece == null) {
            //System.out.println("Generating new " + Board.next.peek() + " piece");
            Piece.testPiece = new Piece(Board.next.remove());
        }

        Piece p = Piece.testPiece;

        boolean lockPiece = false;

        // Directional moves
        if (Key.repeat("w", frameCount))
            p.move(Direction.UP);
        if (Key.repeat("s", frameCount))
            while (p.move(Direction.DOWN));
            //p.move(Direction.DOWN);
        if (Key.repeat("a", frameCount))
            p.move(Direction.LEFT);
        if (Key.repeat("d", frameCount))
            p.move(Direction.RIGHT);
        // Hard drop
        if (Key.frame(" ") != null && Key.frame(" ") == frameCount - 1) {
            while (p.move(Direction.DOWN));
            lockPiece = true;
        }

        // Rotations
        if (Key.frame("37") != null && Key.frame("37") == frameCount - 1)
            p.rotate(Rotation.COUNTERCLOCKWISE);
        if (Key.frame("38") != null && Key.frame("38") == frameCount - 1)
            p.rotate(Rotation.ONEEIGHTY);
        if (Key.frame("39") != null && Key.frame("39") == frameCount - 1)
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

        // I like spaghetti
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

        // Something 1
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

        // Something 2
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

        // Draw preview pieces + hold
        stroke(0, 255, 0);
        strokeWeight(5);
        Queue<Character> nextCopy = new ArrayDeque<>(Board.next);
        for (int i = 0; i < Board.previews; i++)
            drawPreview(700, 160 + i * 3 * Board.tileSize, nextCopy.remove());
        if (Board.hold != null)
            drawPreview(200, 160, Board.hold);
    }

    public void drawPreview(float x, float y, char type) {
        DisplayColor[][] miniDisplay = new DisplayColor[Board.previewRows][Board.previewCols];

        switch (type) {
            case 'I' -> {
                miniDisplay[0][0] = DisplayColor.I;
                miniDisplay[0][1] = DisplayColor.I;
                miniDisplay[0][2] = DisplayColor.I;
                miniDisplay[0][3] = DisplayColor.I;
            }

            case 'O' -> {
                miniDisplay[1][1] = DisplayColor.O;
                miniDisplay[1][2] = DisplayColor.O;
                miniDisplay[0][1] = DisplayColor.O;
                miniDisplay[0][2] = DisplayColor.O;
            }

            case 'T' -> {
                miniDisplay[1][1] = DisplayColor.T;
                miniDisplay[0][0] = DisplayColor.T;
                miniDisplay[0][1] = DisplayColor.T;
                miniDisplay[0][2] = DisplayColor.T;
            }

            case 'S' -> {
                miniDisplay[1][1] = DisplayColor.S;
                miniDisplay[1][2] = DisplayColor.S;
                miniDisplay[0][0] = DisplayColor.S;
                miniDisplay[0][1] = DisplayColor.S;
            }

            case 'Z' -> {
                miniDisplay[1][0] = DisplayColor.Z;
                miniDisplay[1][1] = DisplayColor.Z;
                miniDisplay[0][1] = DisplayColor.Z;
                miniDisplay[0][2] = DisplayColor.Z;
            }

            case 'J' -> {
                miniDisplay[1][0] = DisplayColor.J;
                miniDisplay[0][0] = DisplayColor.J;
                miniDisplay[0][1] = DisplayColor.J;
                miniDisplay[0][2] = DisplayColor.J;
            }

            case 'L' -> {
                miniDisplay[1][2] = DisplayColor.L;
                miniDisplay[0][0] = DisplayColor.L;
                miniDisplay[0][1] = DisplayColor.L;
                miniDisplay[0][2] = DisplayColor.L;
            }
        }

        try {
            rectMode(CENTER);
            for (int i = 0; i < Board.previewRows; i++) {
                for (int j = 0; j < Board.previewCols; j++) {
                    if (miniDisplay[i][j] != null) {
                        fill(miniDisplay[i][j].getR(), miniDisplay[i][j].getG(), miniDisplay[i][j].getB());
                        noStroke();
                        rect(x + j * Board.tileSize, y - i * Board.tileSize, Board.tileSize, Board.tileSize);
                        miniDisplay[i][j] = null;
                    }
                }
            }
        } catch (NullPointerException ignored) {}
    }

    public void settings() {
        size(960, 720);
    }

    public static void main(String[] args) {
        PApplet.main("tertis.Tertis");
    }
}
