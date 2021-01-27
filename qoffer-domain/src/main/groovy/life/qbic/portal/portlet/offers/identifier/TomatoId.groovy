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
        //TODO  https://github.com/qbicsoftware/offer-manager-2-portlet/issues/232
        // query for the latest version first and increase this version
        // make sure the increased version does not already exist
        // this should be done somewhere with access to the database
        // and is part of the business rules
    }

    @Override
    String toString() {
        return "${randomPart}-${projectPart}-${version}"
    }
}
