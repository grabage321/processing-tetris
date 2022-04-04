package tertis;

/**
 * Enum for the different colors that can be displayed on the board.
 */
public enum DisplayColor {
    I(15, 155, 215),
    O(227, 159, 2),
    T(138, 41, 175),
    S(89, 177, 1),
    Z(215, 15, 55),
    J(33, 65, 198),
    L(227, 91, 2),
    GI(15, 155, 215, true),
    GO(227, 159, 2, true),
    GT(138, 41, 175, true),
    GS(89, 177, 1, true),
    GZ(215, 15, 55, true),
    GJ(33, 65, 198, true),
    GL(227, 91, 2, true),
    LIGHTGRAY(153, 153, 153),
    DARKGRAY(106, 106, 106),
    DEBUG(255, 255, 255);

    private final int r;
    private final int g;
    private final int b;
    private final int a;
    // Repeated number because I don't know what I'm doingQ
    public static final int ghostAlpha = 128;

    // Non-ghost constructor
    DisplayColor(int r, int g, int b) {
        this(r, g, b, false);
    }

    DisplayColor(int r, int g, int b, boolean isGhost) {
        this.r = r;
        this.g = g;
        this.b = b;

        // Set this.a to ghost alpha if isGhost
        if (isGhost)
            this.a = 128;
        else
            this.a = 255;
    }

    public int getR() {
        return this.r;
    }

    public int getG() {
        return this.g;
    }

    public int getB() {
        return this.b;
    }

    public int getA() {
        return this.a;
    }
}
