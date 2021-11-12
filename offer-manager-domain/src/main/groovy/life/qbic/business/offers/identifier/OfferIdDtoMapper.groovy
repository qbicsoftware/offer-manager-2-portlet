package life.qbic.business.offers.identifier

import java.util.function.Function

/**
 * <b>Maps IDs and the DTOs defined in `life.qbic.datamodel.dtos.business`</b>
 *
 * @since 1.2.2
 */
class OfferIdDtoMapper {

    /**
     * Maps an external OfferId DTO to a OfferId.
     * Removes all non-digit characters from the DTO version
     * @param tomatoId the external dto to be converted
     * @return a OfferId based on values from the external DTO
     * @since 1.2.2
     */
    public static final Function<? extends life.qbic.datamodel.dtos.business.TomatoId, OfferId> DTO_TO_OFFER_ID = (tomatoIdDto) -> {
        String projectPart = tomatoIdDto.projectConservedPart
        String randomPart = tomatoIdDto.randomPart

        String cleanedVersion = tomatoIdDto.version.replaceAll(/\D/, "")
        int version = Integer.parseInt(cleanedVersion)

        return new OfferId(projectPart, randomPart, version)
    }

    /**
     * Maps a OfferId to an external OfferId DTO.
     * @param tomatoId the identifier to be converted
     * @return an external DTO representing the identifier provided
     * @since 1.2.2
     */
    public static final Function<? extends OfferId, life.qbic.datamodel.dtos.business.OfferId> OFFER_ID_TO_DTO = (offerId) -> {
        String projectConservedPart = offerId.projectPart
        String randomPart = offerId.randomPart
        String version = offerId.version.toString()

        return new life.qbic.datamodel.dtos.business.OfferId(projectConservedPart, randomPart, version)
    }
}
