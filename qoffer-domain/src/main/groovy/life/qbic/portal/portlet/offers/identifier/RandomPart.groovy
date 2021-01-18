package life.qbic.portal.portlet.offers.identifier

/**
 * Represents the random part of the offer identifier.
 *
 * This class contains the value of the random part of the offer identifier.
 *
 * @since 0.1.0
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
