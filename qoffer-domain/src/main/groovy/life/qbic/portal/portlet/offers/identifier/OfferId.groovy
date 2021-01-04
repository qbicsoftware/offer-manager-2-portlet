package life.qbic.portal.portlet.offers.identifier

/**
 * <class short description - 1 Line!>
 *
 * <More detailed description - When to use, what it solves, etc.>
 *
 * @since <versiontag>
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
