package life.qbic.business.offers

import life.qbic.business.offers.identifier.OfferId
import life.qbic.business.offers.identifier.TomatoId
import life.qbic.business.offers.identifier.TomatoIdDtoMapper

/**
 * Helper class to convert DTOs in Business Objects and vice versa.
 *
 * This helper class provides some static conversion methods to convert
 * DTOs content into business objects and vice versa.
 *
 * This class can be used anywhere in the application domain code, where information must
 * cross architectural boundaries. Business objects must not leave the domain layer and therefore
 * data needs to be prepared to be exported back into the outer application layers.
 *
 * Feel free to add new converter methods and be careful with the class paths of objects, especially
 * if DTOs and business objects have the same class name. In this case use the full qualified
 * package domain for the DTO class explicitly.
 *
 * @since 1.0.0
 */
class Converter {
    static life.qbic.datamodel.dtos.business.Offer convertOfferToDTO(Offer offer) {
        return OfferDtoMapper.OFFER_TO_DTO.apply(offer)
    }


    @Deprecated
    static TomatoId buildOfferId(life.qbic.datamodel.dtos.business.OfferId id) {
        def offerId = TomatoIdDtoMapper.DTO_TO_TOMATO_ID.apply(id)
        return offerId
    }

    @Deprecated
    static life.qbic.datamodel.dtos.business.OfferId convertIdToDTO(TomatoId id) {
        life.qbic.datamodel.dtos.business.OfferId offerIdDto = TomatoIdDtoMapper.TOMATO_ID_TO_OFFER_ID_DTO.apply(id)
        return offerIdDto
    }

    static Offer convertDTOToOffer(life.qbic.datamodel.dtos.business.Offer offer) {
        return OfferDtoMapper.DTO_TO_OFFER.apply(offer)
    }
}
