package tertis;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Good ol' temp class.
 */
public class temp {
    public static void testPieceSetup() {
        //Board.displayBoard[0][9] = DisplayColor.I;
        //Board.displayBoard[1][9] = DisplayColor.I;
        //Board.displayBoard[2][9] = DisplayColor.I;
        //Board.displayBoard[3][9] = DisplayColor.I;

        //Board.board[0][9] = 1;
        //Board.board[1][9] = 1;
        //Board.board[2][9] = 1;
        //Board.board[3][9] = 1;

        //Board.displayBoard[0][0] = DisplayColor.O;
        //Board.displayBoard[0][1] = DisplayColor.O;
        //Board.displayBoard[1][0] = DisplayColor.O;
        //Board.displayBoard[1][1] = DisplayColor.O;

        //Board.board[0][0] = 1;
        //Board.board[0][1] = 1;
        //Board.board[1][0] = 1;
        //Board.board[1][1] = 1;


        //displayBoard[0][0] = displayColor.T;
        //displayBoard[0][1] = displayColor.T;
        //displayBoard[0][2] = displayColor.T;
        //displayBoard[1][1] = displayColor.T;

        //displayBoard[0][3] = displayColor.GS;
        //displayBoard[0][4] = displayColor.GS;
        //displayBoard[1][4] = displayColor.GS;
        //displayBoard[1][5] = displayColor.GS;

        //displayBoard[19][3] = displayColor.S;
        //displayBoard[19][4] = displayColor.S;
        //displayBoard[20][4] = displayColor.S;
        //displayBoard[20][5] = displayColor.S;

        //Piece.testPiece = new Piece('I');
    }

    public class kicks {
        public static HashMap<String, ArrayList<ArrayList<Integer>>> JLSTZ = new HashMap<>();

        static {
            /*JLSTZ.put("0CW", new ArrayList<>(List.of(
                    new ArrayList<>(List.of(-1, 0)),
                    new ArrayList<>(List.of(-1, 1)),
                    new ArrayList<>(List.of( 0,-2)),
                    new ArrayList<>(List.of(-1,-2))
            )));
            JLSTZ.put("1CW", new ArrayList<>(List.of(
                    new ArrayList<>(List.of( 1, 0)),
                    new ArrayList<>(List.of(-1, 1)),
                    new ArrayList<>(List.of( 0,-2)),
                    new ArrayList<>(List.of(-1,-2))
            )));*/

            Scanner fileIn;
            try {
                fileIn = new Scanner(new FileReader("src/tertis/srskicks.txt"));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {

    }
}
