package tertis;

import processing.core.*;
import java.util.HashMap;

public class Key extends PApplet {
    public static HashMap<String, Boolean> keys = new HashMap<>();
    public static HashMap<String, Integer> keyFrame = new HashMap<>();

    public static float arr = 16.66667f;
    public static float das = 100;

    /**
     * For calculating a key's initial input, along with when the key's input should be repeated
     * based on the DAS and ARR.
     *
     * @return {@code true} on frames where they key is pressed/repeated
     */
    public static boolean keyRepeat(String keyCode, int frameCount) {
        if (Key.keys.get(keyCode) != null && Key.keys.get(keyCode) && keyFrame.get(keyCode) != null) {
            //System.out.println(keyFrame.get(keyCode) + ", " + frameCount);
            return keyFrame.get(keyCode) == frameCount - 1
                    || ((frameCount - keyFrame.get(keyCode) - das / Board.mspf >= 0)
                    && (frameCount - keyFrame.get(keyCode) - das / Board.mspf) % ((int) (arr / Board.mspf)) == 0);
        } else {
            return false;
        }
    }
}
