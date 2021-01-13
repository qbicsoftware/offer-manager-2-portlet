package life.qbic.portal.portlet.offers.identifier

/**
 * Represents the version of an offer
 *
 * Contains the value of the version of the offer
 *
 * @since 0.1.0
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

    Version(String preAssignedVersion) {
        this.version = Integer.parseInt(preAssignedVersion)
    }

    protected int getRawValue() {
        return this.version
    }

    String getValue() {
        return version
    }

    void increaseVersion() {
        this.version += 1
    }
}
