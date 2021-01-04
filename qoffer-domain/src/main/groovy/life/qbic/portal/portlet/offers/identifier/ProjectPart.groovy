package life.qbic.portal.portlet.offers.identifier

/**
 * <class short description - 1 Line!>
 *
 * <More detailed description - When to use, what it solves, etc.>
 *
 * @since <versiontag>
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
}
