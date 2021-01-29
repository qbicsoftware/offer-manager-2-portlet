package life.qbic.portal.portlet.offers.identifier

import groovy.transform.CompileStatic

/**
 * Represents and identifier for an offer
 *
 * Contains the random part, project conserved part and the version of an identifier
 *
 * @since 0.1.0
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

    /**
     * TODO documentation
     * @since 1.0.0
     */
    void increaseVersion() {
        this.version.increaseVersion()
    }

    @Override
    String toString() {
        return "${randomPart}-${projectPart}-${version}"
    }
}
