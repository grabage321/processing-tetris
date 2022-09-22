package tertis;

import processing.core.*;
import java.util.HashMap;

public class Key extends PApplet {
    public static HashMap<String, Boolean> keys = new HashMap<>();
    public static HashMap<String, Integer> keyFrame = new HashMap<>();
    public static HashMap<String, String> keyBind = new HashMap<>();

    public static float das = 100; // Delayed auto shift
    public static float arr = 16.66667f; // Auto repeat rate

    // TI swear this is also temporary
    public static String controlPreset = ":)"; // "me", "alex", "jstris", "jonah"

    /**
     * For returning a key's initial input, along with when the key's input should be repeated based
     * on the DAS and ARR.
     *
     * @return {@code true} on frames where they key is pressed/repeated
     */
    public static boolean repeat(String keyCode, int frameCount) {
        if (keys.get(keyCode) != null && keys.get(keyCode) && frame(keyCode) != null) {
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
                    || ((frameCount - frame(keyCode) - das / Board.MSPF >= 0)
                    && (int) ((frameCount - frame(keyCode) - das / Board.MSPF) % (arr / Board.MSPF)) == 0);
        } else {
            return false;
        }
    }

    /**
     * I swear this function is temporary
     */
    public static void beyKindings() {
        String LEFT = "37";
        String UP = "38";
        String RIGHT = "39";
        String DOWN = "40";
        String F4 = "115";
        String F5 = "116";

        switch (controlPreset) {
            case "me" -> controlPreset = "alex";
            case "alex" -> controlPreset = "jstris";
            case "jstris" -> controlPreset = "jonah";
            default -> controlPreset = "me";
        }

        // Janky control preset switch
        switch (controlPreset) {
            case "me" -> {
                // My preferred controls
                keyBind.put("move_up", "w");
                keyBind.put("move_down", DOWN);
                keyBind.put("move_left", LEFT);
                keyBind.put("move_right", RIGHT);
                keyBind.put("move_drop", UP);
                keyBind.put("move_hold", " ");
                keyBind.put("rot_ccw", "z");
                keyBind.put("rot_180", "x");
                keyBind.put("rot_cw", "c");
                setDas(80);
                setArr(16.66666f);
            }

            case "jstris" -> {
                // Jstris default
                keyBind.put("move_up", "w");
                keyBind.put("move_down", DOWN);
                keyBind.put("move_left", LEFT);
                keyBind.put("move_right", RIGHT);
                keyBind.put("move_drop", " ");
                keyBind.put("move_hold", "c");
                keyBind.put("rot_ccw", UP);
                keyBind.put("rot_180", "a");
                keyBind.put("rot_cw", "z");
                setDas(133);
                setArr(16);
            }

            case "jonah" -> {
                // Jonah controls
                keyBind.put("move_up", "w");
                keyBind.put("move_down", DOWN);
                keyBind.put("move_left", LEFT);
                keyBind.put("move_right", RIGHT);
                keyBind.put("move_drop", UP);
                keyBind.put("move_hold", " ");
                keyBind.put("rot_ccw", "c");
                keyBind.put("rot_180", "b");
                keyBind.put("rot_cw", "v");
                setDas(133.33333f);
                setArr(8.33333f);
            }

            case "alex" -> {
                // Alex controls
                keyBind.put("move_up", null);
                keyBind.put("move_down", DOWN);
                keyBind.put("move_left", LEFT);
                keyBind.put("move_right", RIGHT);
                keyBind.put("move_drop", " ");
                keyBind.put("move_hold", "s");
                keyBind.put("rot_ccw", "a");
                keyBind.put("rot_180", "d");
                keyBind.put("rot_cw", UP);
                setDas(133);
                setArr(10);
            }
        }

        keyBind.put("misc_reset", F4);
        keyBind.put("misc_loadfile", F4);
        keyBind.put("misc_keys", F5);

        System.out.println("Now using the \"" + controlPreset + "\" control scheme");
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

    public static void setDas(float newDas) {
        das = newDas;
    }

    public static void setArr(float newArr) {
        arr = newArr;
    }
}
