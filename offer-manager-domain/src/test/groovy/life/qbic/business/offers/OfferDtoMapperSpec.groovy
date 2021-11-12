package life.qbic.business.offers

import life.qbic.business.offers.identifier.OfferId
import life.qbic.datamodel.dtos.business.Affiliation
import life.qbic.datamodel.dtos.business.Customer
import life.qbic.datamodel.dtos.business.ProjectManager
import spock.lang.Specification

/**
 * <b>Tests the mapping of Offer entity object to Offer dto and vice versa.</b>
 *
 * @since 1.2.2
 */
class OfferDtoMapperSpec extends Specification {

    static Customer customer = new Customer.Builder("Heinz", "Blöd", "flunkern@captn.blau").build()
    static ProjectManager projectManager = new ProjectManager.Builder("Captn", "Blaubär", "captn@captn.blau").build()
    static Affiliation affiliation = new Affiliation.Builder("Lach und Sachgeschichten", "Irgendwo", "1111", "Im Boot").build()
    static Date creationDate = new Date()

    static Offer offer = generateOffer()
    static life.qbic.datamodel.dtos.business.Offer offerDto = generateOfferDto()
    static life.qbic.datamodel.dtos.business.Offer filledDto = generateOfferDto(offer.checksum(), offer.getExpirationDate(), offer.getOverheadRatio())


    def "DTO_TO_OFFER works"() {
        when:
        def mappedOffer = OfferDtoMapper.DTO_TO_OFFER.apply(offerDto)
        then:
        mappedOffer.checksum() == offer.checksum()
    }

    def "OFFER_TO_DTO works"() {
        when:
        def mappedOfferDto = OfferDtoMapper.OFFER_TO_DTO.apply(offer)
        then:
        mappedOfferDto == filledDto
    }

    private static Offer generateOffer() {
        def builder = new Offer.Builder(customer, projectManager, "projectTitle", "projectObjective", [], affiliation)
        builder.identifier(new OfferId("projectPart", "randomPart", 1))
        builder.creationDate(creationDate)
        return builder.build()
    }

    private static life.qbic.datamodel.dtos.business.Offer generateOfferDto() {
        def builder = new life.qbic.datamodel.dtos.business.Offer.Builder(customer, projectManager, "projectTitle", "projectObjective", affiliation)
        builder.identifier(new life.qbic.datamodel.dtos.business.OfferId("projectPart", "randomPart", "1"))
        builder.modificationDate(creationDate)
        return builder.build()
    }

    private static life.qbic.datamodel.dtos.business.Offer generateOfferDto(String checksum, Date expirationdate, def overheadRatio) {
        def builder = new life.qbic.datamodel.dtos.business.Offer.Builder(customer, projectManager, "projectTitle", "projectObjective", affiliation)
        builder.identifier(new life.qbic.datamodel.dtos.business.OfferId("projectPart", "randomPart", "1"))
        builder.modificationDate(creationDate)
        builder.expirationDate(expirationdate)
        builder.checksum(checksum)
        builder.overheadRatio(overheadRatio)
        return builder.build()
    }

}
