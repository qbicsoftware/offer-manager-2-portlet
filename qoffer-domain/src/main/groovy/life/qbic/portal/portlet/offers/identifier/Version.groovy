package life.qbic.portal.portlet.offers.identifier

/**
 * <class short description - 1 Line!>
 *
 * <More detailed description - When to use, what it solves, etc.>
 *
 * @since <versiontag>
 */
class Version {

    private int version

    Version() {
        this.version = 1
    }

    Version(Version preAssignedVersion) {
        this.version = preAssignedVersion.getRawValue()
    }

    Version(int preAssignedVersion) {
        this.version = preAssignedVersion
    }

    protected int getRawValue() {
        return this.version
    }

    String getValue() {
        return version.toString()
    }

    void increaseVersion() {
        this.version += 1
    }
}
