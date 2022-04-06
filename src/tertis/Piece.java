package tertis;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Integer.parseInt;

public class Piece {
    public static Piece testPiece;
    public static Character hold = null;
    public static boolean heldThisRound = false;
    public static ArrayList<Tile> prevGhostTiles = new ArrayList<>();

    /**
     * Class just for holding kick tables. Mostly a file reader for srskicks.txt.
     */
    public static class Kicks {
        public static HashMap<String, ArrayList<ArrayList<Integer>>> jlstzKicks = new HashMap<>();
        public static HashMap<String, ArrayList<ArrayList<Integer>>> iKicks = new HashMap<>();
        public static int testCount;
        private static int iHateMyselfForDoingThis = 0;

        static {
            // Initialize the FileReader for srskicks.txt
            Scanner fileIn = null;
            try {
                fileIn = new Scanner(new FileReader("src/tertis/srskicks.txt"));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            // Initialize the line parsing regex and testCount
            Pattern p = kickTableRegex(4);

            // File reading
            while (fileIn != null && fileIn.hasNextLine()) {
                // Trim whitespace from the beginning and end of each line read
                String line = fileIn.nextLine().trim();

                // If the line is not blank and doesn't begin with a comment character, parse it
                if (!line.isEmpty() && line.charAt(0) != '#') {

                    if (line.toUpperCase().startsWith("TEST_COUNT=")) {
                        // If the line begins with "TEST_COUNT=" then parse the following number and
                        // pass it into kickTableRegex()
                        int newTestCount = parseInt(line.substring(11).trim());
                        // Update the line parsing regex and testCount
                        p = kickTableRegex(newTestCount);
                    } else if (line.equalsIgnoreCase("JLSTZ")) {
                        // If the line is "JLSTZ" iHateMyselfForDoingThis to 1
                        iHateMyselfForDoingThis = 1;
                    } else if (line.equalsIgnoreCase("I")) {
                        // If the line is "I" iHateMyselfForDoingThis to 1
                        iHateMyselfForDoingThis = 2;
                    } else {
                        // Set up the matcher
                        Matcher m = p.matcher(line);

                        // Match the regex, and make sure the group count is 2 + the number of
                        // coordinate pairs expected
                        if (m.find() && m.groupCount() == 2 + testCount * 2) {
                            // The key for the HashMap should be the current rotation state (number
                            // 0–3), followed by the rotation's abbreviation ("CW", "CCW", or "180")
                            String key = m.group(1) + m.group(2).toUpperCase();

                            // Create an ArrayList of Integer pairs that represent the offsets for
                            // each kick test
                            ArrayList<ArrayList<Integer>> tests = new ArrayList<>();
                            for (int i = 3; i < 2 + testCount * 2; i += 2) {
                                tests.add(new ArrayList<>(List.of(
                                        parseInt(m.group(i)),
                                        parseInt(m.group(i + 1))
                                )));
                            }

                            // Finally, put the current entry into either the jlstz or i kick table
                            // (depends on which iHateMyselfForDoingThis was set to last)
                            if (iHateMyselfForDoingThis == 1)
                                jlstzKicks.put(key, tests);
                            else if (iHateMyselfForDoingThis == 2)
                                iKicks.put(key, tests);
                        } else {
                            throw new RuntimeException("Improperly formatted kick table");
                        }
                    }
                }
            }
        }

        /**
         * Updates the regex used for parsing lines to match testCount, and updates testCount.
         *
         * @return Pattern for the line parsing regex
         */
        private static Pattern kickTableRegex(int newTestCount) {
            testCount = newTestCount;

            String kickTableRegex = "([0-3])\\s*(CW|CCW|180)" +
                    "\\s*\\(\\s*\\+?(-?[0-9]+),\\s*(-?[0-9]*)\\)".repeat(newTestCount);
            return Pattern.compile(kickTableRegex, Pattern.CASE_INSENSITIVE);
        }
    }

    private int row;
    private int col;
    private int rot; // Clockwise, starting upward: 0 -> 1 -> 2 -> 3 -> 0
    private final char type;
    private DisplayColor dc;
    private Tile[] tiles = new Tile[4];

    static {
        int temp = Kicks.testCount;
    }

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

        // Initialize rotation to be 0
        this.rot = 0;

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
        Board.displayBoard[this.row][this.col] = DisplayColor.DEBUG;
    }

    /**
     * Go through tiles and erase each one from the display board.
     */
    public void clear() {
        for (Tile i : tiles)
            Board.displayBoard[i.getRow()][i.getCol()] = null;
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
            case UP -> {
                // Check that every Tile's moved image is clear
                for (Tile i : this.tiles) {
                    if (i.getRow() != Board.DISPLAY_BOARD_HEIGHT - 1)
                        canMove &= Board.board[i.getRow() + 1][i.getCol()] != 1;
                    else
                        canMove = false;
                }

                // Move the piece if the test passed (and if movePiece is true)
                if (canMove && movePiece) {
                    this.clear();
                    for (Tile i : this.tiles)
                        i.setRow(i.getRow() + 1);
                    this.row += 1;
                }
            }

            case DOWN -> {
                // Check that every Tile's moved image is clear
                for (Tile i : this.tiles) {
                    if (i.getRow() > 0 && i.getCol() < Board.BOARD_WIDTH)
                        canMove &= Board.board[i.getRow() - 1][i.getCol()] != 1;
                    else
                        canMove = false;
                }

                // Move the piece if the test passed (and if movePiece is true)
                if (canMove && movePiece) {
                    this.clear();
                    for (Tile i : this.tiles)
                        i.setRow(i.getRow() - 1);
                    this.row -= 1;
                }
            }

            case LEFT -> {
                // Check that every Tile's moved image is clear
                for (Tile i : this.tiles) {
                    if (i.getCol() != 0)
                        canMove &= Board.board[i.getRow()][i.getCol() - 1] != 1;
                    else
                        canMove = false;
                }

                // Move the piece if the test passed (and if movePiece is true)
                if (canMove && movePiece) {
                    this.clear();
                    for (Tile i : this.tiles)
                        i.setCol(i.getCol() - 1);
                    this.col -= 1;
                }
            }

            case RIGHT -> {
                // Check that every Tile's moved image is clear
                for (Tile i : this.tiles) {
                    if (i.getCol() != Board.BOARD_WIDTH - 1)
                        canMove &= Board.board[i.getRow()][i.getCol() + 1] != 1;
                    else
                        canMove = false;
                }

                // Move the piece if the test passed (and if movePiece is true)
                if (canMove && movePiece) {
                    this.clear();
                    for (Tile i : this.tiles)
                        i.setCol(i.getCol() + 1);
                    this.col += 1;
                }
            }
        }

        // Return whether the piece can move
        return canMove;
    }

    /**
     * Rotates the piece clockwise, counter-clockwise, or 180 degrees. The O and I piece have
     * special offsets.
     *
     * @param rotation direction to rotate
     */
    public void rotate(Rotation rotation) {
        // Allow rotation if the piece isn't an O
        //boolean canRot = Character.toUpperCase(this.type) != 'O';
        boolean canRot = true;

        // Rotation checking process
        for (Tile i : tiles) {
            // Initialize rotated coordinates
            int r = getRotatedRow(i, rotation);
            int c = getRotatedCol(i, rotation);

            if (r >= 0 && r < Board.BOARD_HEIGHT && c >= 0 && c < Board.BOARD_WIDTH) {
                // Check each tile's rotated image for a collision (if its index is within bounds)
                canRot &= Board.board[r][c] != 1;
            } else {
                // If the index is not in bounds, the rotation fails
                canRot = false;
            }
        }

        // Variable used to store the kick result
        int kick = -1;
        if (!canRot)
            kick = this.rotationKick(rotation);
        // Piece rotation (if check was successful)
        if (canRot || kick != -1) {
            // Clear all display tiles before actually rotating
            this.clear();

            // Initalize key based on the current rotation number plus the type of rotation
            String key = String.valueOf(this.rot) + rotation;

            // Update the rotation counter
            switch (rotation) {
                case CLOCKWISE -> this.rot = (this.rot + 1) % 4;
                case COUNTERCLOCKWISE -> this.rot = (this.rot + 3) % 4;
                case ONEEIGHTY -> this.rot = (this.rot + 2) % 4;
            }

            // Update piece's center coordinates if a kick was performed
            if (kick != -1 && this.type == 'I') {
                this.row += Kicks.iKicks.get(key).get(kick).get(1);
                this.col += Kicks.iKicks.get(key).get(kick).get(0);
            } else if (kick != -1) {
                this.row += Kicks.jlstzKicks.get(key).get(kick).get(1);
                this.col += Kicks.jlstzKicks.get(key).get(kick).get(0);
            }

            // Actually rotate
            for (Tile i : tiles) {
                // Initialize rotated coordinates
                int r = getRotatedRow(i, rotation);
                int c = getRotatedCol(i, rotation);

                // Offset by kick amount if a kick was successful
                if (kick != -1) {
                    switch (rotation) {
                        // (-y, +x)
                        case CLOCKWISE -> {
                            if (this.type == 'I') {
                                r -= Kicks.iKicks.get(key).get(kick).get(0);
                                c += Kicks.iKicks.get(key).get(kick).get(1);
                            } else {
                                r -= Kicks.jlstzKicks.get(key).get(kick).get(0);
                                c += Kicks.jlstzKicks.get(key).get(kick).get(1);
                            }
                        }

                        // (+y, -x)
                        case COUNTERCLOCKWISE -> {
                            if (this.type == 'I') {
                                r += Kicks.iKicks.get(key).get(kick).get(0);
                                c -= Kicks.iKicks.get(key).get(kick).get(1);
                            } else {
                                r += Kicks.jlstzKicks.get(key).get(kick).get(0);
                                c -= Kicks.jlstzKicks.get(key).get(kick).get(1);
                            }
                        }

                        // (-x, -y)
                        case ONEEIGHTY -> {
                            if (this.type == 'I') {
                                r -= Kicks.iKicks.get(key).get(kick).get(1);
                                c -= Kicks.iKicks.get(key).get(kick).get(0);
                            } else {
                                r -= Kicks.jlstzKicks.get(key).get(kick).get(1);
                                c -= Kicks.jlstzKicks.get(key).get(kick).get(0);
                            }
                        }
                    }
                }

                // Set this tile's new coordinates
                i.setRow(r);
                i.setCol(c);
            }
        }
    }

    /**
     * Gives row value for the rotated image of a Tile.
     *
     * @param tile the Tile being rotated
     * @param rotation the direction of the rotation
     * @return rotated tile's row value
     */
    private int getRotatedRow(Tile tile, Rotation rotation) {
        // Enable special rotation offset for I piece
        double rotationOffset;
        switch (this.type) {
            case 'I', 'i' -> rotationOffset = 0.5;
            case 'O', 'o' -> rotationOffset = -0.5;
            default -> rotationOffset = 0;
        }

        // Perform rotation math on the row variables based on the rotation chosen
        int r = (int) (this.row + (this.col - tile.getCol() - rotationOffset) - rotationOffset);
        switch (rotation) {
            case COUNTERCLOCKWISE ->
                    r = (int) (this.row - (this.col - tile.getCol() - rotationOffset) - rotationOffset);
            case ONEEIGHTY ->
                    r = (int) (this.row + (this.row - tile.getRow() - rotationOffset) - rotationOffset);
        }

        return r;
    }

    /**
     * Gives column value for the rotated image of a Tile.
     *
     * @param tile the Tile being rotated
     * @param rotation the direction of the rotation
     * @return rotated tile's col value
     */
    private int getRotatedCol(Tile tile, Rotation rotation) {
        // Enable special rotation offset for I piece
        double rotationOffset;
        switch (this.type) {
            case 'I', 'i' -> rotationOffset = 0.5;
            case 'O', 'o' -> rotationOffset = -0.5;
            default -> rotationOffset = 0;
        }

        // Perform rotation math on the row variables based on the rotation chosen
        int c = (int) (this.col - (this.row - tile.getRow() - rotationOffset) - rotationOffset);
        switch (rotation) {
            case COUNTERCLOCKWISE ->
                    c = (int) (this.col + (this.row - tile.getRow() - rotationOffset) - rotationOffset);
            case ONEEIGHTY ->
                    c = (int) (this.col + (this.col - tile.getCol() - rotationOffset) - rotationOffset);
        }

        return c;
    }

    /**
     * Handles wall kicks. Returns the first successful kick number.
     *
     * @param rotation rotation being performed
     * @return first successful kick
     */
    private int rotationKick(Rotation rotation) {
        int i;
        for (i = 0; i < Kicks.testCount; i++) {
            boolean canRot = true;
            String key = String.valueOf(this.rot) + rotation;

            for (Tile j : this.tiles) {
                // Initialize rotated coordinates
                int r = getRotatedRow(j, rotation);
                int c = getRotatedCol(j, rotation);

                // Add offset from kick table
                if (this.type == 'I') {
                    r += Kicks.iKicks.get(key).get(i).get(1);
                    c += Kicks.iKicks.get(key).get(i).get(0);
                } else {
                    r += Kicks.jlstzKicks.get(key).get(i).get(1);
                    c += Kicks.jlstzKicks.get(key).get(i).get(0);
                }

                if (r >= 0 && r < Board.BOARD_HEIGHT && c >= 0 && c < Board.BOARD_WIDTH)
                    // Check each tile's rotated image for a collision (if its index is within bounds)
                    canRot &= Board.board[r][c] != 1;
                else
                    // If the index is not in bounds, the rotation fails
                    canRot = false;
            }

            if (canRot) {
                //System.out.println("Kick success: " + key + " " + i);
                break;
            }
        }

        if (i < Kicks.testCount)
            return i;
        else
            return -1;
    }

    /**
     * Detects if any of this Piece's tiles are overlapped with tiles on the actual board. This
     * should only be possible when topping out, so this function is used to trigger a game over.
     *
     * @return {@code true} if this Piece is clipping
     */
    public boolean isClipping() {
        boolean isClipping = false;
        for (Tile i : this.tiles)
            isClipping |= Board.board[i.getRow()][i.getCol()] == 1;

        return isClipping;
    }

    /**
     * Finds the lowest (or one of the lowest) Tiles in a Piece.
     *
     * @return the first Tile in this Piece's tiles with the lowest row value
     */
    public Tile getLowestTile() {
        Tile lowestTile = this.tiles[0];
        for (Tile i : this.tiles) {
            if (i.getRow() < lowestTile.getRow())
                lowestTile = i;
        }

        return lowestTile;
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
        return new Piece((temp == null)? Board.next.remove() : temp);
    }
}
