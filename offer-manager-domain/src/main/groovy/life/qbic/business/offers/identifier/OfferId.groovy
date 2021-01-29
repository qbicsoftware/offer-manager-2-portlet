package life.qbic.business.offers.identifier

/**
 * Describes the offer identifier
 *
 * Contains the random part, the project conserved part and the version from which the offer identifier is build
 *
 * @since 0.1.0
 */
class OfferId extends TomatoId{

    OfferId(RandomPart randomPart, ProjectPart projectPart, Version versionTag) {
        super(randomPart, projectPart, versionTag)
    }

    @Override
    String toString() {
        return "O-${super.randomPart}-${super.projectPart}-${super.version}"
    }
}
