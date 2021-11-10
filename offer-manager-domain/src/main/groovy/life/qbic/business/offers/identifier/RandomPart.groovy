package life.qbic.business.offers.identifier

/**
 * Represents the random part of the offer identifier.
 *
 * This class contains the value of the random part of the offer identifier.
 *
 * @since 0.1.0
 * @Deprecated please use Strings instead
 */
@Deprecated
class RandomPart {

    private static final String alphabet = "abcdefghijklmnopqrstuvwxyz"

    protected String value

    RandomPart () {
        this.value = createRandomFourLetterString()
    }

    RandomPart (RandomPart randomPart) {
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

    private static String createRandomFourLetterString() {
        StringBuilder randomString = new StringBuilder()
        for (int i = 0; i < 4; i++){
            def newRandomChar = alphabet[(int) (Math.random() * 25)]
            randomString.append(newRandomChar)
        }
        return randomString.toString()
    }

    @Override
    String toString() {
        return this.value
    }
}
