package tertis;

import processing.core.*;
import java.util.HashMap;

public class Key extends PApplet {
    private static int lastDirectionalValue = 0;

    public static HashMap<String, Boolean> keys = new HashMap<>();
    public static HashMap<String, Integer> keyFrame = new HashMap<>();
    public static HashMap<String, String> keyBind = new HashMap<>();

    public static float das = 100; // Delayed auto shift
    public static float arr = 16.66667f; // Auto repeat rate

    // TODO: Make this do something
    public static boolean cancelDas = true; // Cancel DAS when chancing directions

    // I swear this is also temporary
    public static String controlPreset = ":)"; // "me", "alex", "jstris", "jonah"

    /**
     * For returning a key's initial input, along with when the key's input should be repeated based
     * on the DAS and ARR.
     *
     * @return {@code 1} on either the first frame of the key press or the frames when the input
     * should repeat.
     * <p>
     * {@code 2} on every frame after the key has been pressed for longer than the number of DAS
     * frames when the ARR is 0 (meaning that the piece movement should be instant).
     */
    public static int repeat(String keyCode, int frameCount) {
        if (keys.get(keyCode) != null && keys.get(keyCode) && frame(keyCode) != null) {
            // Whether it's the first frame the key is being pressed
            boolean isFirstFrame = frame(keyCode) == frameCount - 1;

            // Frames since DAS ended
            int framesPastDas = (int) (frameCount - frame(keyCode) - das / Board.MSPF);
            // Whether the key has been pressed for longer than the number of DAS frames
            boolean isPastDas = framesPastDas >= 0;
            // Whether the number of frames elapsed since the DAS ended is divisible by the ARR
            boolean isRepeatFrame = framesPastDas % (arr / Board.MSPF) == 0;

            if (isFirstFrame || (isPastDas && isRepeatFrame))
                return 1;
            else if (arr == 0 && isPastDas)
                return 2;
        }

        return 0;
    }

    /*
     * I guess I do this in dumb codes?
     *
     * 0: do nothing
     * 1: go left 1
     * 2: go right 1
     * 3: teleport left
     * 4: teleport right
     */
    public static int directionalInput(String leftKeyCode, String rightKeyCode, int frameCount) {
        int dv = directionalValue(leftKeyCode, rightKeyCode);

        if (dv > 0) {
            // Whether it's the first frame the key is being pressed
            boolean isFirstFrameR = frame(rightKeyCode) == frameCount - 1;
            boolean isFirstFrameL = frame(leftKeyCode) == frameCount - 1;
            // Frames since DAS ended
            int framesPastDasR = (int) (frameCount - frame(rightKeyCode) - das / Board.MSPF);
            int framesPastDasL = (int) (frameCount - frame(leftKeyCode) - das / Board.MSPF);
            // Whether the key has been pressed for longer than the number of DAS frames
            boolean isPastDasR = framesPastDasR >= 0;
            boolean isPastDasL = framesPastDasL >= 0;
            // Whether the number of frames elapsed since the DAS ended is divisible by the ARR
            boolean isRepeatFrameR = (int) (framesPastDasR % (arr / Board.MSPF)) == 0;
            boolean isRepeatFrameL = (int) (framesPastDasL % (arr / Board.MSPF)) == 0;

            switch (dv) {
                // Just left key is being held
                case 1 -> {
                    // If the right key was just let go (and the cancelDas setting is on)
                    if (cancelDas && isPastDasL && lastDirectionalValue == 3) {
                        keyFrame.put(leftKeyCode, frameCount - 1);
                        return 0;
                    }

                    // If ARR is instant, and it's past the DAS time
                    if (arr == 0 && isPastDasL)
                        return 3;
                    // If it's the first frame of a keypress or is a repeating frame
                    else if (isFirstFrameL || (isPastDasL && isRepeatFrameL))
                        return 1;
                }

                // Just right key is being held
                case 2 -> {
                    // If the left key was just let go (and the cancelDas setting is on)
                    if (cancelDas && isPastDasR && lastDirectionalValue == 3) {
                        keyFrame.put(rightKeyCode, frameCount - 1);
                        return 0;
                    }

                    // If ARR is instant, and it's past the DAS time
                    if (arr == 0 && isPastDasR)
                        return 4;
                    // If it's the first frame of a keypress or is a repeating frame
                    else if (isFirstFrameR || (isPastDasR && isRepeatFrameR))
                        return 2;
                }

                // Both left and right keys are being held
                case 3 -> {
                    // First frame of one of the key presses
                    if (isFirstFrameL)
                        return 1;
                    if (isFirstFrameR)
                        return 2;

                    // Normal ARR case
                    if (arr > 0 && isRepeatFrameL && frame(leftKeyCode) > frame(rightKeyCode))
                        return 1;
                    if (arr > 0 && isRepeatFrameR && frame(rightKeyCode) > frame(leftKeyCode))
                        return 2;

                    // Instant ARR case
                    if (arr == 0 && isPastDasL && frame(leftKeyCode) > frame(rightKeyCode))
                        return 3;
                    if (arr == 0 && isPastDasR && frame(rightKeyCode) > frame(leftKeyCode))
                        return 4;
                }
            }

        }

        lastDirectionalValue = dv;
        return 0;
    }

    private static boolean isPressed(String keyCode) {
        return keys.get(keyCode) != null && keys.get(keyCode) && frame(keyCode) != null;
    }

    private static int directionalValue(String leftKeyCode, String rightKeyCode) {
        int directionalValue = 0;
        directionalValue += isPressed(leftKeyCode)? 1 : 0;
        directionalValue += isPressed(rightKeyCode)? 2 : 0;

        return directionalValue;
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
                keyBind.put("move_down", "2");
                keyBind.put("move_left", "1");
                keyBind.put("move_right", "3");
                keyBind.put("move_drop", "5");
                keyBind.put("move_hold", " ");
                keyBind.put("rot_ccw", "z");
                keyBind.put("rot_180", "x");
                keyBind.put("rot_cw", "c");
                setDas(100);
                setArr(0);
                setCancelDas(true);
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
                setCancelDas(true);
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
                setCancelDas(true);
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
                setCancelDas(true);
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
        if (keyFrame.get(keyCode) == null)
            return -2;

        return keyFrame.get(keyCode);
    }

    public static void setDas(float newDas) {
        das = newDas;
    }

    public static void setArr(float newArr) {
        arr = newArr;
    }

    public static void setCancelDas(boolean newCancelDas) {
        cancelDas = newCancelDas;
    }
}
