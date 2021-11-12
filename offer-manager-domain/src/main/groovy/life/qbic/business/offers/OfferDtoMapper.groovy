package life.qbic.business.offers


import life.qbic.business.offers.identifier.OfferId
import life.qbic.business.offers.identifier.OfferIdDtoMapper

import java.util.function.Function

/**
 * <b><short description></b>
 *
 * <p><detailed description></p>
 *
 * @since <version tag>
 */
class OfferDtoMapper {

    public static final Function<? extends life.qbic.datamodel.dtos.business.Offer, Offer> DTO_TO_OFFER = (life.qbic.datamodel.dtos.business.Offer offerDto) -> {
        OfferId offerId = OfferIdDtoMapper.DTO_TO_OFFER_ID.apply(offerDto.getIdentifier())
        def builder = new Offer.Builder(
                offerDto.customer,
                offerDto.projectManager,
                offerDto.projectTitle,
                offerDto.projectDescription,
                offerDto.items,
                offerDto.selectedCustomerAffiliation)
                .identifier(offerId)
        if (offerDto.modificationDate) {
            builder.creationDate(offerDto.modificationDate)
        }
        offerDto.experimentalDesign.ifPresent({
            builder.experimentalDesign(offerDto.experimentalDesign)
        })
        offerDto.associatedProject.ifPresent({
            builder.associatedProject(it)
        })
        return builder.build()
    }

    public static final Function<? extends Offer, life.qbic.datamodel.dtos.business.Offer> OFFER_TO_DTO = (Offer offer) -> {
        life.qbic.datamodel.dtos.business.OfferId offerIdDto = OfferIdDtoMapper.OFFER_ID_TO_DTO.apply(offer.getIdentifier())
        def builder = new life.qbic.datamodel.dtos.business.Offer.Builder(
                offer.customer,
                offer.projectManager,
                offer.projectTitle,
                offer.projectObjective,
                offer.selectedCustomerAffiliation)
                .identifier(offerIdDto)
                .items(offer.getItems())
                .netPrice(offer.getTotalNetPrice())
                .taxes(offer.getTaxCosts())
                .overheads(offer.getOverheadSum())
                .totalPrice(offer.getTotalCosts())
                .totalDiscountPrice(offer.totalDiscountAmount)
                .modificationDate(offer.modificationDate)
                .expirationDate(offer.expirationDate)
                .checksum(offer.checksum())
                .overheadRatio(offer.overheadRatio)
//                .itemsWithOverhead(offer.overheadItems) // This is deprecated and not used. Only required by the tests
//                .itemsWithoutOverhead(offer.noOverheadItems) // This is deprecated and not used. Only required by the tests
//                .itemsWithOverheadNet(offer.overheadItemsNet) // This is deprecated and not used. Only required by the tests
//                .itemsWithoutOverheadNet(offer.noOverheadItemsNet) // This is deprecated and not used. Only required by the tests
        offer.associatedProject.ifPresent({
            builder.associatedProject(it)
        })
        offer.experimentalDesign.ifPresent({
            builder.experimentalDesign(it)
        })
        return builder.build()
    }
}
