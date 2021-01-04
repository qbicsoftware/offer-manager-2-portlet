package life.qbic.portal.portlet.offers.identifier

/**
 * <class short description - 1 Line!>
 *
 * <More detailed description - When to use, what it solves, etc.>
 *
 * @since <versiontag>
 */
class RandomPart {

    private String value

    RandomPart () {
        // TODO implement a real random part
        this.value = "abcd"
    }

    RandomPart (String preAssignedValue) {
        this.value = preAssignedValue
    }

    String getValue() {
        return this.value
    }
}
