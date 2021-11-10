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
class TomatoId implements Comparable<TomatoId>{

    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz"

    private String projectPart
    private String randomPart
    private int version

    @Deprecated
    TomatoId(RandomPart randomPart, ProjectPart projectPart, Version version) {
        this.randomPart = randomPart.toString()
        this.projectPart = projectPart.toString()
        this.version = version.getRawValue()
    }

    TomatoId(String projectPart, int version) {
        this.randomPart = generateFourLetterString()
        this.projectPart = projectPart
        this.version = version
    }

    TomatoId(String projectPart, String randomPart, int version) {
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
    int compareTo(TomatoId other) {
        if (this.equals(other)) {
            // we want to be in sync with the equals method
            return 0
        }
        return this.version <=> other.version
    }
}
