package life.qbic.business.offers.identifier

/**
 * Represents the project part conserved of the identifier
 *
 * Contains the value of the project conserved part of the identifier
 *
 * @since 0.1.0
 */
class ProjectPart {

    protected String value

    ProjectPart(String value) {
        this.value = value
    }

    ProjectPart(ProjectPart projectPart) {
        this.value = projectPart.getRawValue()
    }

    protected getRawValue() {
        this.value
    }

    String getValue() {
        this.value
    }

    @Override
    String toString() {
        return this.value
    }
}
