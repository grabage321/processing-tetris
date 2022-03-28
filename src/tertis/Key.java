package tertis;

import processing.core.*;
import java.util.HashMap;

public class Key extends PApplet {
    public static HashMap<String, Boolean> keys = new HashMap<>();
    public static HashMap<String, Integer> keyFrame = new HashMap<>();
    public static HashMap<String, String> keyBind = new HashMap<>();

    public static float das = 100; // Delayed auto shift
    public static float arr = 16.66667f; // Auto repeat rate
    /*public static float das = 8 * 16.66667f; // Delayed auto shift
    public static float arr = 0.5f * 16.66667f; // Auto repeat rate*/

    /**
     * For returning a key's initial input, along with when the key's input should be repeated based
     * on the DAS and ARR.
     *
     * @return {@code true} on frames where they key is pressed/repeated
     */
    public static boolean repeat(String keyCode, int frameCount) {
        if (Key.keys.get(keyCode) != null && Key.keys.get(keyCode) && frame(keyCode) != null) {
            /*
             * Key is pressed first frame?
             *
             * OR
             *
             * Key has been pressed for longer than DAS
             * AND
             * Number of frames since DAS ended is divisible by ARR number
             */
            return frame(keyCode) == frameCount - 1
                    || ((frameCount - frame(keyCode) - das / Board.mspf >= 0)
                    && (int) ((frameCount - frame(keyCode) - das / Board.mspf) % (arr / Board.mspf)) == 0);
        } else {
            return false;
        }
    }

    /**
     * Basically just an alias because I'm lazy.
     *
     * @param keyCode Key code to retrieve frame number
     * @return Frame the key was last pressed on
     */
    public static Integer frame(String keyCode) {
        return keyFrame.get(keyCode);
    }
}
