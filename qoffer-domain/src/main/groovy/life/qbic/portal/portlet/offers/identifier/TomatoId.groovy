package life.qbic.portal.portlet.offers.identifier
/**
 * <class short description - 1 Line!>
 *
 * <More detailed description - When to use, what it solves, etc.>
 *
 * @since <versiontag>
 */
class TomatoId {

    protected RandomPart randomPart

    protected ProjectPart projectPart

    protected Version version

    TomatoId(RandomPart randomPart, ProjectPart projectPart, Version version) {
        this.randomPart = randomPart
        this.projectPart = projectPart
        this.version = version
    }

    void increaseVersion() {
        this.version.increaseVersion()
    }

    @Override
    String toString() {
        return "${randomPart}-${projectPart}-${version}"
    }
}
