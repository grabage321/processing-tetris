package tertis;

import processing.core.*;

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

        // Directional moves
        if (Key.keyRepeat("w", frameCount))
            Piece.testPiece.move(Direction.UP);
        if (Key.keyRepeat("s", frameCount))
            Piece.testPiece.move(Direction.DOWN);
        if (Key.keyRepeat("a", frameCount))
            Piece.testPiece.move(Direction.LEFT);
        if (Key.keyRepeat("d", frameCount))
            Piece.testPiece.move(Direction.RIGHT);
        // Hard drop
        if (Key.keyFrame.get(" ") != null && Key.keyFrame.get(" ") == frameCount - 1)
            while (Piece.testPiece.move(Direction.DOWN));

        // Rotations
        if (Key.keyFrame.get("37") != null && Key.keyFrame.get("37") == frameCount - 1)
            Piece.testPiece.rotate(Rotation.COUNTERCLOCKWISE);
        if (Key.keyFrame.get("38") != null && Key.keyFrame.get("38") == frameCount - 1)
            Piece.testPiece.rotate(Rotation.ONEEIGHTY);
        if (Key.keyFrame.get("39") != null && Key.keyFrame.get("39") == frameCount - 1)
            Piece.testPiece.rotate(Rotation.CLOCKWISE);

        //Piece.testGhost.ghostUpdate(Piece.testPiece);
        //Piece.testGhost.display();

        Piece.testPiece.display();
        Piece.renderGhost(Piece.testPiece);

        keyTestDraw();
        drawBoard();
    }

    public void keyTestDraw() {
        float keyWidth = 50;

        float keyOverlayX = 90;
        float keyOverlayY = 200;

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
        float tileSize = 30;

        //line(480, 0, 480, 720);

        for (int i = 0; i <= Board.displayBoardHeight; i++) {
            for (int j = 0; j <= Board.boardWidth; j++) {
                strokeWeight(2);
                stroke(128);
                point((boardX - tileSize * ((Board.boardWidth / 2.0f) - 0.5f) + j * tileSize) - tileSize / 2, (boardY - i * tileSize) + tileSize / 2);
            }
        }

        for (int i = 0; i < Board.displayBoardHeight; i++) {
            for (int j = 0; j < Board.boardWidth; j++) {
                rectMode(CENTER);

                // Draw square if square exists
                if (Board.displayBoard[i][j] != null) {
                    fill(Board.displayBoard[i][j].getR(), Board.displayBoard[i][j].getG(), Board.displayBoard[i][j].getB(), Board.displayBoard[i][j].getA());
                    noStroke();
                    rect(boardX - tileSize * ((Board.boardWidth / 2.0f) - 0.5f) + j * tileSize, boardY - i * tileSize, tileSize, tileSize);
                }

                // Draw points at center of all cells
                //strokeWeight(5);
                //stroke(255);
                //point(boardX - tileSize * ((boardWidth / 2.0) - 0.5 ) + j * tileSize, boardY - i * tileSize);
            }
        }
    }

    public void settings() {
        size(960, 720);
    }

    public static void main(String[] args) {
        PApplet.main("tertis.Tertis");
    }
}
