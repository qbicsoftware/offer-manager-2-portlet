package life.qbic.business.offers.identifier

import groovy.transform.CompileStatic
import groovy.transform.EqualsAndHashCode

/**
 * Represents and identifier for an offer
 *
 * Contains the random part, project conserved part and the version of an identifier
 *
 * @since 0.1.0
 */
@CompileStatic
@EqualsAndHashCode(includeFields = true)
class OfferId implements Comparable<OfferId>{

    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz"

    private String projectPart
    private String randomPart
    private int version

    static OfferId from(String s) {
        def splitId = s.split("_")
        // The first entry [0] contains the id prefix, no need to parse it.
        def projectPart = splitId[1]
        def randomPart = splitId[2]
        def version = Integer.parseInt(splitId[3])
        return new OfferId(projectPart, randomPart, version)
    }

    OfferId(String projectPart, int version) {
        this.randomPart = generateFourLetterString()
        this.projectPart = projectPart
        this.version = version
    }

    OfferId(String projectPart, String randomPart, int version) {
        this.projectPart = projectPart
        this.randomPart = randomPart
        this.version = version
    }

    private static String generateFourLetterString() {
        StringBuilder randomString = new StringBuilder()
        Random random = new Random()
        for (int i = 0; i < 4; i++){
            def randomIndex = random.nextInt(ALPHABET.size())
            def newRandomChar = ALPHABET[randomIndex]
            randomString.append(newRandomChar)
        }
        return randomString.toString()
    }

    void increaseVersion() {
        this.version += 1
    }

    String getProjectPart() {
        return projectPart
    }

    String getRandomPart() {
        return randomPart
    }

    int getVersion() {
        return version
    }

    @Override
    int compareTo(OfferId other) {
        if (this.equals(other)) {
            // we want to be in sync with the equals method
            return 0
        }
        return this.version <=> other.version
    }


    @Override
    String toString() {
        return String.format("O_%s_%s_%s", projectPart, randomPart, version)
    }
}
