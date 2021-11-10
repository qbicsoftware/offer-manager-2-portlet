package life.qbic.business.offers.identifier

/**
 * Describes the offer identifier
 *
 * Contains the random part, the project conserved part and the version from which the offer identifier is build
 *
 * @since 0.1.0
 */
@Deprecated
class OfferId extends TomatoId{

    @Deprecated
    OfferId(RandomPart randomPart, ProjectPart projectPart, Version version) {
        super(randomPart, projectPart, version)
    }

    @Deprecated
    OfferId(OfferId offerId) {
        super(new RandomPart(offerId.randomPart),
                new ProjectPart(offerId.projectPart),
                new Version(offerId.version))
    }

    @Override
    @Deprecated
    String toString() {
        return "O-${super.projectPart}-${super.randomPart}-${super.version}"
    }
}
