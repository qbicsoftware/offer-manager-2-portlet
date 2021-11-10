package life.qbic.business.offers.identifier

/**
 * Represents the version of an offer
 *
 * Contains the value of the version of the offer
 *
 * @since 0.1.0
 * @Deprecated please use integer values instead
 */
@Deprecated
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

    @Override
    boolean equals(Object otherVersion) {
        if (otherVersion.is(this)) {
            return true
        }
        if (!(otherVersion instanceof Version)) {
            return false
        }
        def comparedVersion = (Version) otherVersion
        return this.getRawValue() == comparedVersion.getRawValue()
    }

    @Override
    String toString() {
        return "v$version"
    }
}
