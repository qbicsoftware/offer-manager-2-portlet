package life.qbic.business.persons;

/**
 * Possible academic titles
 *
 * This enum describes all academic titles known to the qbic infrastructure.
 * Bachelor and Masters titles as well as multiple doctorates are not supported.
 *
 * @since 1.3.0
 */
enum AcademicTitle {

    PROFESSOR("Prof. Dr."),
    DOCTOR("Dr."),
    PHD("PhD"),
    NONE("None");

    /**
     Holds the String text of the enum
     */
    private final String label;

    /**
     * Private constructor to create different AcademicTitle enum items
     * @param label the label for this enum instance
     */
    AcademicTitle(String label) {
        this.label = label;
    }

    /**
     * @return the label for this academic title. For example 'Dr.'
     */
    public String getLabel() {
        return this.label;
    }

    /**
     * {@inheritDoc} <b>This method might change. Please consider using
     * {@link AcademicTitle#getLabel()} instead</b>
     */
    @Override
    public String toString() {
        return getLabel();
    }
}
