package life.qbic.business.offers.identifier

import groovy.transform.CompileStatic

/**
 * Represents and identifier for an offer
 *
 * Contains the random part, project conserved part and the version of an identifier
 *
 * @since 0.1.0
 */
@CompileStatic
class TomatoId implements Comparable<TomatoId>{

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
        return "${randomPart.toString()}-${projectPart.toString()}-${version.toString()}"
    }

    @Override
    int compareTo(TomatoId other) {
        /*
        -1: current Id version lower than other
        0: current Id version equal to other
        1: current Id version higher than other
         */
        int returnValue = 0
        if(this.version.rawValue < other.version.rawValue) {
            returnValue = -1
        } else if(this.version.rawValue == other.version.rawValue) {
            returnValue = 0
        } else {
            returnValue = 1
        }
        return returnValue
    }
}
