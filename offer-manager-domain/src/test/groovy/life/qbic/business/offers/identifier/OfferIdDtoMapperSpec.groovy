package life.qbic.business.offers.identifier


import spock.lang.Specification

/**
 * <b>Tests the mapping of OfferId entity object to OfferId dto and vice versa.</b>
 *
 * @since 1.2.2
 */
class OfferIdDtoMapperSpec extends Specification {

    static OfferId offerId = new OfferId("projectPart", "random", 2)
    static life.qbic.datamodel.dtos.business.OfferId offerIdDto = new life.qbic.datamodel.dtos.business.OfferId("projectPart", "random", "2")

    def "DTO_TO_OFFER_ID works"() {
        expect:
        OfferIdDtoMapper.DTO_TO_OFFER_ID.apply(offerIdDto) == offerId
    }

    def "OFFER_ID_TO_DTO works"() {
        expect:
        OfferIdDtoMapper.OFFER_ID_TO_DTO.apply(offerId) == offerIdDto

    }
}
