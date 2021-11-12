package life.qbic.business.offers.identifier

import spock.lang.Specification

/**
 * <b><short description></b>
 *
 * <p><detailed description></p>
 *
 * @since <version tag>
 */
class TomatoIdFormatterSpec extends Specification {

    def "FormatAsOfferId works for life.qbic.datamodel.dtos.business.OfferId"() {
        given:
        String projectPart = "projectPart"
        String randomPart = "ilko"

        and: "an offer identifier"
        def offerId = new life.qbic.datamodel.dtos.business.OfferId(projectPart, randomPart, version.toString())

        when: "an offer identifier is formatted"
        String formattedId = TomatoIdFormatter.formatAsOfferId(offerId)

        then: "the formatted string equals O_projectPart_randomPart_versionNumber"
        formattedId == expectedFormatting

        where: "the version string can be different"
        version | expectedFormatting
        "v4" | "O_projectPart_ilko_4"
        "4" | "O_projectPart_ilko_4"
        "0" | "O_projectPart_ilko_0"

    }

    def "FormatAsOfferId works for life.qbic.business.offers.identifier.OfferId"() {
        given:
        String projectPart = "projectPart"
        String randomPart = "ilko"
        int version = 2

        and: "an offer identifier"
        def offerId = new OfferId(projectPart, randomPart, version)

        when: "an offer identifier is formatted"
        String formattedId = TomatoIdFormatter.formatAsOfferId(offerId)

        then: "the formatted string equals O_projectPart_randomPart_versionNumber"
        formattedId == "O_projectPart_ilko_2"
    }



    def "RemoveVersion"() {
    }
}
