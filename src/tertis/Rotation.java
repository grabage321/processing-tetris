package tertis;

/**
 * Enum for rotation directions.
 */
public enum Rotation {
    CLOCKWISE("CW"),
    COUNTERCLOCKWISE("CCW"),
    ONEEIGHTY("180");

    private final String abbreviation;

    Rotation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    @Override
    public String toString() {
        return abbreviation;
    }
}
