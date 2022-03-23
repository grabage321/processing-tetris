package tertis;

import java.util.ArrayList;

public class Piece {
    public static Piece testPiece;
    //public static Piece ghost;
    public static ArrayList<Tile> prevGhostTiles = new ArrayList<>();

    private int row;
    private int col;
    private char type;
    private DisplayColor dc;
    public Tile[] tiles = new Tile[4];

    public Piece(char type) {
        switch (type) {
            case 'I', 'i' -> {
                // abCd

                this.dc = (type == 'I') ? DisplayColor.I : DisplayColor.GI;

                this.row = 20;
                this.col = 5;
                tiles[0] = new Tile(row, col - 2);
                tiles[1] = new Tile(row, col - 1);
                tiles[2] = new Tile(row, col);
                tiles[3] = new Tile(row, col + 1);
            }
            case 'O', 'o' -> {
                // ab
                // Cd

                this.dc = (type == 'O') ? DisplayColor.O : DisplayColor.GO;

                this.row = 20;
                this.col = 4;
                tiles[0] = new Tile(row + 1, col);
                tiles[1] = new Tile(row + 1, col + 1);
                tiles[2] = new Tile(row, col);
                tiles[3] = new Tile(row, col + 1);
            }
            case 'T', 't' -> {
                //  a
                // bCd

                this.dc = (type == 'T') ? DisplayColor.T : DisplayColor.GT;

                this.row = 20;
                this.col = 4;
                tiles[0] = new Tile(row + 1, col);
                tiles[1] = new Tile(row, col - 1);
                tiles[2] = new Tile(row, col);
                tiles[3] = new Tile(row, col + 1);
            }
            case 'S', 's' -> {
                //  ab
                // cD

                this.dc = (type == 'S') ? DisplayColor.S : DisplayColor.GS;

                this.row = 20;
                this.col = 4;
                tiles[0] = new Tile(row + 1, col + 1);
                tiles[1] = new Tile(row + 1, col);
                tiles[2] = new Tile(row, col - 1);
                tiles[3] = new Tile(row, col);
            }
            case 'Z', 'z' -> {
                // ab
                //  Cd

                this.dc = (type == 'Z') ? DisplayColor.Z : DisplayColor.GZ;

                this.row = 20;
                this.col = 4;
                tiles[0] = new Tile(row + 1, col - 1);
                tiles[1] = new Tile(row + 1, col);
                tiles[2] = new Tile(row, col);
                tiles[3] = new Tile(row, col + 1);
            }
            case 'J', 'j' -> {
                // a
                // bCd

                this.dc = (type == 'J') ? DisplayColor.J : DisplayColor.GJ;

                this.row = 20;
                this.col = 4;
                tiles[0] = new Tile(row + 1, col - 1);
                tiles[1] = new Tile(row, col - 1);
                tiles[2] = new Tile(row, col);
                tiles[3] = new Tile(row, col + 1);
            }
            case 'L', 'l' -> {
                //   a
                // bCd

                this.dc = (type == 'L') ? DisplayColor.L : DisplayColor.GL;

                this.row = 20;
                this.col = 4;
                tiles[0] = new Tile(row + 1, col + 1);
                tiles[1] = new Tile(row, col - 1);
                tiles[2] = new Tile(row, col);
                tiles[3] = new Tile(row, col + 1);
            }
        }

        this.type = type;
    }

    public void display() {
        for (Tile i : tiles) {
            //System.out.println("Display: (" + i.getRow() + ", " + i.getCol() + ")");
            Board.displayBoard[i.getRow()][i.getCol()] = this.dc;
        }
    }

    public void clear() {
        for (Tile i : tiles)
            Board.displayBoard[i.getRow()][i.getCol()] = null;
    }

    /**
     * Rotates the piece clockwise, counter-clockwise, or 180 degrees. Unless it's the O.
     *
     * @param rotation direction to rotate
     */
    public void rotate(Rotation rotation) {
        // Allow rotation if the piece isn't an O
        boolean canRot = this.dc != DisplayColor.O;

        for (Tile i : tiles) {
            // Initialize row and col variables in the rotated spots
            int r = this.row + (this.col - i.getCol());
            int c = this.col - (this.row - i.getRow());

            switch (rotation) {
                case COUNTERCLOCKWISE -> {
                    r = this.row - (this.col - i.getCol());
                    c = this.col + (this.row - i.getRow());
                }
                case ONEEIGHTY -> {
                    r = this.row + (this.row - i.getRow());
                    c = this.col + (this.col - i.getCol());
                }
            }

            //System.out.print("(" + i.getRow() + ", " + i.getCol() + ") -> ");
            //System.out.print("(" + (this.row - i.getRow()) + ", " + (this.col - i.getCol()) + ") -> ");
            //System.out.println("(" + r + ", " + c + ")");

            // Check each rotated image for a collision if its index is in bounds
            if (r >= 0 && r <= Board.boardHeight - 1 && c >= 0 && c <= Board.boardWidth - 1)
                canRot &= Board.board[r][c] != 1;
            else {
                canRot = false;
                //System.out.println("Failed");
            }
        }

        if (canRot) {
            this.clear();

            for (Tile i : tiles) {
                // Actually rotate the tiles
                int r = this.row + (this.col - i.getCol());
                int c = this.col - (this.row - i.getRow());

                switch (rotation) {
                    case COUNTERCLOCKWISE -> {
                        r = this.row - (this.col - i.getCol());
                        c = this.col + (this.row - i.getRow());
                    }
                    case ONEEIGHTY -> {
                        r = this.row + (this.row - i.getRow());
                        c = this.col + (this.col - i.getCol());
                    }
                }

                i.setRow(r);
                i.setCol(c);
            }
        }
    }

    /**
     * Moves the piece down, left, or right.
     * <p>
     * It can also do up, but we don't speak of that.
     *
     * @param direction direction to move the piece
     * @return {@code true} if the move was successful
     */
    public boolean move(Direction direction) {
        boolean canMove = true;

        switch (direction) {
            case UP -> {
                for (Tile i : this.tiles) {
                    if (i.getRow() != Board.displayBoardHeight - 1)
                        canMove &= Board.board[i.getRow() + 1][i.getCol()] != 1;
                    else
                        canMove = false;
                }

                if (canMove) {
                    this.clear();
                    for (Tile i : this.tiles)
                        i.setRow(i.getRow() + 1);
                    this.row += 1;
                }
            }
            case DOWN -> {
                for (Tile i : this.tiles) {
                    if (i.getRow() != 0)
                        canMove &= Board.board[i.getRow() - 1][i.getCol()] != 1;
                    else
                        canMove = false;
                }

                if (canMove) {
                    this.clear();
                    for (Tile i : this.tiles)
                        i.setRow(i.getRow() - 1);
                    this.row -= 1;
                }
            }
            case LEFT -> {
                for (Tile i : this.tiles) {
                    if (i.getCol() != 0)
                        canMove &= Board.board[i.getRow()][i.getCol() - 1] != 1;
                    else
                        canMove = false;
                }

                if (canMove) {
                    this.clear();
                    for (Tile i : this.tiles)
                        i.setCol(i.getCol() - 1);
                    this.col -= 1;
                }
            }
            case RIGHT -> {
                for (Tile i : this.tiles) {
                    if (i.getCol() != Board.boardWidth - 1)
                        canMove &= Board.board[i.getRow()][i.getCol() + 1] != 1;
                    else
                        canMove = false;
                }

                if (canMove) {
                    this.clear();
                    for (Tile i : this.tiles)
                        i.setCol(i.getCol() + 1);
                    this.col += 1;
                }
            }
        }

        return canMove;
    }

    /**
     * @return This Piece's row
     */
    public int getRow() {
        return this.row;
    }

    /**
     * @return This Piece's col(umn)
     */
    public int getCol() {
        return this.col;
    }

    /**
     * @return This Piece's type
     * <p>
     * {@code I, i, O, o, T, t, S, s, Z, z, J, j, L, or l}
     */
    public char getType() {
        return type;
    }

    /**
     * For moving and rendering the ghost piece.
     *
     * @param piece Piece being ghosted lol
     */
    public static void renderGhost(Piece piece) {
        // New ghosty version of piece
        Piece ghost = new Piece(Character.toLowerCase(piece.getType()));

        // Clear the tiles from the previous ghost piece, and then reset the array keeping track
        // TODO: Make sure this doesn't run on piece lock
        for (Tile i : prevGhostTiles)
            Board.displayBoard[i.getRow()][i.getCol()] = null;
        prevGhostTiles.clear();

        // Copy the real piece's tiles into the ghost piece, then update the ghost piece's position
        for (int i = 0; i < Piece.testPiece.tiles.length; i++) {
            ghost.tiles[i].setRow(Piece.testPiece.tiles[i].getRow());
            ghost.tiles[i].setCol(Piece.testPiece.tiles[i].getCol());
        }
        ghost.row = Piece.testPiece.getRow();
        ghost.col = Piece.testPiece.getCol();

        // Essentially hard drop the ghost piece
        while (ghost.move(Direction.DOWN));

        // Add the dropped ghost piece's tiles into the array keeping track
        for (Tile i : ghost.tiles)
            Piece.prevGhostTiles.add(new Tile(i.getRow(), i.getCol()));

        // Render it :)
        ghost.display();
    }
}
