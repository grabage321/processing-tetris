package tertis;

import java.util.ArrayList;
import java.util.Arrays;

public class Piece {
    public static Piece testPiece;
    public static Character hold = null;
    public static boolean heldThisRound = false;
    public static ArrayList<Tile> prevGhostTiles = new ArrayList<>();

    private int row;
    private int col;
    private final char type;
    private DisplayColor dc;
    public Tile[] tiles = new Tile[4];

    /**
     * Constructs a piece at the location specified within each constructor case. The display color
     * of the piece is set to be either the regular or ghost color, depending on the case of the
     * char given as the type argument.
     *
     * @param type Piece type
     */
    public Piece(char type) {
        switch (type) {
            /*
             * Each case here contains a guide, with the tiles initialized in the order a, b, c, d.
             * The capitalized letter represents where the piece's position is considered to be.
             *
             * The row and column are set to represent the piece's initial spawn location, and then
             * each element in the tiles array is set to a location relative to the "center" tile.
             */

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

        // Finally, update type field
        this.type = type;
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
     * Go through tiles and put each one on the display board.
     */
    public void display() {
        for (Tile i : tiles)
            Board.displayBoard[i.getRow()][i.getCol()] = this.dc;
    }

    /**
     * Go through tiles and erase each one from the display board.
     */
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
        boolean canRot = Character.toUpperCase(this.type) != 'O';

        // Rotation checking process
        for (Tile i : tiles) {
            // Enable special rotation offset for I piece
            double iOffset = (Character.toUpperCase(this.type) == 'I') ? 0.5 : 0;

            // Initialize row and col variables (clockwise rotation)
            int r = (int) (this.row + (this.col - i.getCol() - iOffset) - iOffset);
            int c = (int) (this.col - (this.row - i.getRow() - iOffset) - iOffset);

            // Set row and col variables to counter-clockwise or 180 degree rotations based on what
            // was chosen
            switch (rotation) {
                case COUNTERCLOCKWISE -> {
                    r = (int) (this.row - (this.col - i.getCol() - iOffset) - iOffset);
                    c = (int) (this.col + (this.row - i.getRow() - iOffset) - iOffset);
                }
                case ONEEIGHTY -> {
                    r = (int) (this.row + (this.row - i.getRow() - iOffset) - iOffset);
                    c = (int) (this.col + (this.col - i.getCol() - iOffset) - iOffset);
                }
            }

            if (r >= 0 && r <= Board.boardHeight - 1 && c >= 0 && c <= Board.boardWidth - 1) {
                // Check each tile's rotated image for a collision (if its index is within bounds)
                canRot &= Board.board[r][c] != 1;
            } else {
                // If the index is not in bounds, the rotation fails
                canRot = false;
            }
        }

        // Piece rotation (if check was successful)
        if (canRot) {
            // Clear all display tiles before actually rotating
            this.clear();

            // Actually rotate
            for (Tile i : tiles) {
                // Same coordinate initialization as before
                double iOffset = (Character.toUpperCase(this.type) == 'I') ? 0.5 : 0;
                int r = (int) (this.row + (this.col - i.getCol() - iOffset) - iOffset);
                int c = (int) (this.col - (this.row - i.getRow() - iOffset) - iOffset);

                switch (rotation) {
                    case COUNTERCLOCKWISE -> {
                        r = (int) (this.row - (this.col - i.getCol() - iOffset) - iOffset);
                        c = (int) (this.col + (this.row - i.getRow() - iOffset) - iOffset);
                    }
                    case ONEEIGHTY -> {
                        r = (int) (this.row + (this.row - i.getRow() - iOffset) - iOffset);
                        c = (int) (this.col + (this.col - i.getCol() - iOffset) - iOffset);
                    }
                }

                // Set this tile's new coordinates
                i.setRow(r);
                i.setCol(c);
            }
        }
    }

    /**
     * Checks if the piece can move up, down, left, or right.
     * <p>
     * Basically just an alias for {@code move(Direction, false)}.
     *
     * @param direction direction to check the piece
     * @return {@code true} if the move is possible
     */
    public boolean canMove(Direction direction) {
        return this.move(direction, false);
    }

    /**
     * Moves the piece down, left, or right.
     * <p>
     * It can also do up, but we don't speak of that.
     * <p>
     * Basically just an alias for {@code move(Direction, true)}.
     *
     * @param direction direction to move the piece
     * @return {@code true} if the move was successful
     */
    public boolean move(Direction direction) {
        return this.move(direction, true);
    }

    /**
     * Moves or checks if the piece can move down, left, or right.
     * <p>
     * It can also do up, but we don't speak of that.
     *
     * @param direction direction to move/check the piece
     * @param movePiece whether to actually move the piece. {@code true} if not specified.
     * @return {@code true} if the move is possible / was successful
     */
    public boolean move(Direction direction, boolean movePiece) {
        boolean canMove = true;

        switch (direction) {
            /*
             * Each case can be broken down into two parts: check if the piece can be moved in the
             * given direction, and move the piece in the given direction (if movePiece is true).
             */

            case UP -> {
                for (Tile i : this.tiles) {
                    if (i.getRow() != Board.displayBoardHeight - 1)
                        canMove &= Board.board[i.getRow() + 1][i.getCol()] != 1;
                    else
                        canMove = false;
                }

                if (canMove && movePiece) {
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

                if (canMove && movePiece) {
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

                if (canMove && movePiece) {
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

                if (canMove && movePiece) {
                    this.clear();
                    for (Tile i : this.tiles)
                        i.setCol(i.getCol() + 1);
                    this.col += 1;
                }
            }
        }

        // Return whether the piece could move
        return canMove;
    }

    public boolean isClipping() {
        boolean isClipping = false;

        for (Tile i : this.tiles)
            isClipping |= Board.board[i.getRow()][i.getCol()] == 1;

        return isClipping;
    }

    /**
     * For moving and rendering the ghost piece.
     *
     * @param piece Piece being ghosted lol
     */
    public static void renderGhost(Piece piece) {
        // Create new ghosty version of piece
        Piece ghost = new Piece(Character.toLowerCase(piece.getType()));

        // Clear the tiles from the previous ghost piece, and then reset the array keeping track of
        // ghost piece tiles
        for (Tile i : prevGhostTiles)
            Board.displayBoard[i.getRow()][i.getCol()] = null;
        prevGhostTiles.clear();

        // Copy the real piece's tiles into the ghost piece's tiles
        for (int i = 0; i < piece.tiles.length; i++) {
            ghost.tiles[i].setRow(piece.tiles[i].getRow());
            ghost.tiles[i].setCol(piece.tiles[i].getCol());
        }
        // Update the ghost piece's position to match the given piece
        ghost.row = piece.getRow();
        ghost.col = piece.getCol();

        // Essentially hard drop the ghost piece
        while (ghost.move(Direction.DOWN));

        // Now, add the ghost piece's tiles into the array keeping track of ghost piece tiles
        for (Tile i : ghost.tiles)
            prevGhostTiles.add(new Tile(i.getRow(), i.getCol()));

        // Render it :)
        ghost.display();
    }

    /**
     * For locking a piece (basically just adding it to Board.board).
     *
     * @param piece Piece being locked
     */
    public static void lock(Piece piece) {
        // Add each tile's position to the board (display and actual)
        for (Tile i : piece.tiles) {
            Board.board[i.getRow()][i.getCol()] = 1;
            Board.displayBoard[i.getRow()][i.getCol()] = piece.dc;
        }

        // Clear out the array used for ghost piece tiles
        prevGhostTiles.clear();
    }

    /**
     * For performing the hold action on the current Piece
     *
     * @param piece the current Piece
     * @return new Piece resulting from the swap, or {@code null} if it's the first piece being held
     */
    public static Piece hold(Piece piece) {
        // Add the piece's tiles to the ghost piece tile array, because it's a convenient way to
        // make sure the piece being swapped out is also cleared from the display board
        prevGhostTiles.addAll(Arrays.asList(piece.tiles));

        // Mark that hold has been used
        heldThisRound = true;

        // Essentially swap the held piece and the current piece
        Character temp = hold;
        hold = piece.getType();
        return (temp == null)? null : new Piece(temp);
    }
}
