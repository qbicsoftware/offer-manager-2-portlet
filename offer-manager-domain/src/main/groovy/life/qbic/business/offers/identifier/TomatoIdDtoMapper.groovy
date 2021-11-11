package life.qbic.business.offers.identifier

import java.util.function.Function

/**
 * <b>Maps IDs and the DTOs defined in `life.qbic.datamodel.dtos.business`</b>
 *
 * @since 1.2.2
 */
class TomatoIdDtoMapper {

    /**
     * Maps an external TomatoId DTO to a TomatoId.
     * Removes all non-digit characters from the DTO version
     * @param tomatoId the external dto to be converted
     * @return a TomatoId based on values from the external DTO
     * @since 1.2.2
     */
    public static final Function<? extends life.qbic.datamodel.dtos.business.TomatoId, TomatoId> DTO_TO_TOMATO_ID = (tomatoIdDto) -> {
        String projectPart = tomatoIdDto.projectConservedPart
        String randomPart = tomatoIdDto.randomPart

        String cleanedVersion = tomatoIdDto.version.replaceAll(/\D/, "")
        int version = Integer.parseInt(cleanedVersion)

        return new TomatoId(projectPart, randomPart, version)
    }

    /**
     * Maps a TomatoId to an external OfferId DTO.
     * @param tomatoId the identifier to be converted
     * @return an external DTO representing the identifier provided
     * @since 1.2.2
     */
    public static final Function<? extends TomatoId, life.qbic.datamodel.dtos.business.OfferId> TOMATO_ID_TO_OFFER_ID_DTO = (tomatoId) -> {
        String projectConservedPart = tomatoId.projectPart
        String randomPart = tomatoId.randomPart
        String version = tomatoId.version.toString()

        return new life.qbic.datamodel.dtos.business.OfferId(projectConservedPart, randomPart, version)
    }
}
