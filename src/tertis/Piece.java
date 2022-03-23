package tertis;

import processing.core.*;

public class Piece extends PApplet {
    public static Piece testPiece;

    public int row;
    public int col;
    public int rot;
    public DisplayColor dc;
    private Tile[] tiles = new Tile[4];

    public Piece(char type) {
        switch (type) {
            case 'i', 'I' -> {
                // abCd
            }
            case 't', 'T' -> {
                //  a
                // bCd
                //

                this.row = 20;
                this.col = 4;
                this.rot = 0;
                this.dc = DisplayColor.T;
                tiles[0] = new Tile(row + 1, col);
                tiles[1] = new Tile(row, col - 1);
                tiles[2] = new Tile(row, col);
                tiles[3] = new Tile(row, col + 1);
            }
        }
    }

    public void display() {
        for (Tile i : tiles) {
            //System.out.println("Display: (" + i.getRow() + ", " + i.getCol() + ")");
            Board.displayBoard[i.getRow()][i.getCol()] = this.dc;
        }
    }

    public void rotateCW() {
        // Allow rotation if the piece isn't an O
        boolean canRot = this.dc != DisplayColor.O;

        for (Tile i : tiles) {
            // Initialize row and col variables in the rotated spots
            int r = this.row + (this.col - i.getCol());
            int c = this.col - (this.row - i.getRow());

            //System.out.print("(" + i.getRow() + ", " + i.getCol() + ") -> ");
            //System.out.print("(" + (this.col - i.getCol()) + ", " + (this.row - i.getRow()) + ") -> ");
            //System.out.println("(" + r + ", " + c + ")");

            if (r >= 0 && r <= Board.boardHeight - 1 && c >= 0 && c <= Board.boardWidth - 1)
                canRot &= Board.board[r][c] != 1;
            else {
                //println("failed");
                canRot = false;
            }

            /*if (canRot) {
                Board.displayBoard[i.getRow()][i.getCol()] = null;
                i.setRow(r);
                i.setCol(c);
            }*/
        }

        if (canRot) {
            //println("test passed");
            //println("========");
            for (Tile i : tiles) {
                Board.displayBoard[i.getRow()][i.getCol()] = null;

                int r = this.row + (this.col - i.getCol());
                int c = this.col - (this.row - i.getRow());
                i.setRow(r);
                i.setCol(c);
                //println("(" + r + ", " + c + ")");
            }
        }
        //println("================");
    }

    // Test function
    public void moveUp() {
        boolean canMove = true;
        for (Tile i : tiles) {
            if (i.getRow() != Board.displayBoardHeight - 1)
            canMove &= Board.board[i.getRow() + 1][i.getCol()] != 1;
            else
            canMove = false;
        }

        if (canMove) {
            for (Tile i : tiles)
                Board.displayBoard[i.getRow()][i.getCol()] = null;
            for (Tile i : tiles)
                i.setRow(i.getRow() + 1);
            this.row += 1;
        }
    }

    public void moveDown() {
        boolean canMove = true;
        for (Tile i : tiles) {
            if (i.getRow() != 0)
                canMove &= Board.board[i.getRow() - 1][i.getCol()] != 1;
            else
                canMove = false;
        }

        if (canMove) {
            for (Tile i : tiles)
                Board.displayBoard[i.getRow()][i.getCol()] = null;
            for (Tile i : tiles)
                i.setRow(i.getRow() - 1);
            this.row -= 1;
        }
    }

    public void moveLeft() {
        boolean canMove = true;
        for (Tile i : tiles) {
            if (i.getCol() != 0)
                canMove &= Board.board[i.getRow()][i.getCol() - 1] != 1;
            else
                canMove = false;
        }

        if (canMove) {
            for (Tile i : tiles)
                Board.displayBoard[i.getRow()][i.getCol()] = null;
            for (Tile i : tiles)
                i.setCol(i.getCol() - 1);
            this.col -= 1;
        }
    }

    public void moveRight() {
        boolean canMove = true;
        for (Tile i : tiles) {
            if (i.getCol() != Board.boardWidth - 1)
                canMove &= Board.board[i.getRow()][i.getCol() + 1] != 1;
            else
                canMove = false;
        }

        if (canMove) {
            for (Tile i : tiles)
                Board.displayBoard[i.getRow()][i.getCol()] = null;
            for (Tile i : tiles)
                i.setCol(i.getCol() + 1);
            this.col += 1;
        }
    }
}
