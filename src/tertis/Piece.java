package tertis;

public class Piece {
    public static Piece testPiece;
    //public static Piece testGhost;

    public int row;
    public int col;
    public int rot;
    public DisplayColor dc;
    private Tile[] tiles = new Tile[4];

    public Piece(char type) {
        switch (type) {
            case 'I', 'i' -> {
                // abCd

                this.dc = (type == 'I') ? DisplayColor.I : DisplayColor.GI;

                this.row = 20;
                this.col = 5;
                this.rot = 0;
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
                this.rot = 0;
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
                this.rot = 0;
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
                this.rot = 0;
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
                this.rot = 0;
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
                this.rot = 0;
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
                this.rot = 0;
                tiles[0] = new Tile(row + 1, col + 1);
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
                for (Tile i : tiles) {
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
     * For moving and rendering the ghost piece... But it's not working!
     *
     * @param piece Piece being ghosted lol
     */
    public static void renderGhost(Piece piece) {
        Piece ghost = new Piece('t');

        /*int dr = piece.row - ghost.row;
        int dc = piece.col - ghost.col;

        for (Tile i : ghost.tiles) {
            i.setRow(i.getRow() + dr);
            i.setCol(i.getCol() + dc);
        }*/
        System.arraycopy(piece.tiles, 0, ghost.tiles, 0, piece.tiles.length);

        ghost.row = piece.row;
        ghost.col = piece.col;

        // TODO: figure out why this don't worka
        //ghost.move(Direction.DOWN);

        ghost.display();
    }
}
