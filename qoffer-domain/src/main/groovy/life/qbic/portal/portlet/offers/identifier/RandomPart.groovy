package life.qbic.portal.portlet.offers.identifier

/**
 * <class short description - 1 Line!>
 *
 * <More detailed description - When to use, what it solves, etc.>
 *
 * @since <versiontag>
 */
class RandomPart {

    protected String value

    RandomPart () {
        // TODO implement a real random part
        this.value = "abcd"
    }

    RandomPart (RandomPart randomPart) {
        // TODO implement a real random part
        this.value = randomPart.getRawValue()
    }

    RandomPart (String preAssignedValue) {
        this.value = preAssignedValue
    }

    protected getRawValue() {
        return this.value
    }

    String getValue() {
        return this.value
    }
}
