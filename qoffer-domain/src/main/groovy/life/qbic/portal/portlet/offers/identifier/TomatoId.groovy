package life.qbic.portal.portlet.offers.identifier

import groovy.transform.CompileStatic

/**
 * <class short description - 1 Line!>
 *
 * <More detailed description - When to use, what it solves, etc.>
 *
 * @since <versiontag>
 */
@CompileStatic
class TomatoId {

    private RandomPart randomPart

    private ProjectPart projectPart

    private Version version

    TomatoId(RandomPart randomPart, ProjectPart projectPart, Version version) {
        this.randomPart = randomPart
        this.projectPart = projectPart
        this.version = version
    }

    RandomPart getRandomPart() {
        return new RandomPart(randomPart)
    }

    ProjectPart getProjectPart() {
        return new ProjectPart(projectPart)
    }

    Version getVersion() {
        return new Version(version)
    }

    void increaseVersion() {
        this.version.increaseVersion()
    }

    @Override
    String toString() {
        return "${randomPart}-${projectPart}-${version}"
    }
}
