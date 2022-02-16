package life.qbic.business.persons

/**
 * Possible academic titles
 *
 * This enum describes all academic titles known to the qbic infrastructure.
 * Bachelor and Masters titles as well as multiple doctorates are not supported.
 *
 * @since: 1.11.0
 */
enum AcademicTitle {

    PROFESSOR("Prof. Dr."),
    DOCTOR("Dr."),
    PHD("PhD"),
    NONE("None")

    /**
     Holds the String text of the enum
     */
    private final String text

    /**
     * Private constructor to create different AcademicTitle enum items
     * @param value
     */
    private AcademicTitle(String value) {
        this.text = value
    }

    /**
     * Returns a String representation of the enum item
     * @return
     */
    @Override
    String toString() {
        return this.text
    }
}
