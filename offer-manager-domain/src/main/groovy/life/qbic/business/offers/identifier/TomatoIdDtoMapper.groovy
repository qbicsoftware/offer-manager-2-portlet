package life.qbic.business.offers.identifier

/**
 * <b>Maps IDs and the DTOs defined in `life.qbic.datamodel.dtos.business`</b>
 *
 * @since 1.2.2
 */
class TomatoIdDtoMapper {

    /**
     * Maps an external TomatoId DTO to a TomatoId
     * @param tomatoId the external dto to be converted
     * @return a TomatoId based on values from the external DTO
     * @since 1.2.2
     */
    public static <T extends life.qbic.datamodel.dtos.business.TomatoId> TomatoId tomatoIdFrom(T tomatoId) {
        String projectPart = tomatoId.projectConservedPart
        String randomPart = tomatoId.randomPart
        int version = Integer.parseInt(tomatoId.getVersion())

        return new TomatoId(projectPart, randomPart, version)
    }

    /**
     * Maps a TomatoId to an external TomatoId DTO
     * @param tomatoId the identifier to be converted
     * @return an external DTO representing the identifier provided
     * @since 1.2.2
     */
    public static life.qbic.datamodel.dtos.business.OfferId toOfferIdDto(TomatoId tomatoId) {
        String projectConservedPart = tomatoId.projectPart
        String randomPart = tomatoId.randomPart
        String version = tomatoId.version.toString()

        return new life.qbic.datamodel.dtos.business.OfferId(projectConservedPart, randomPart, version)
    }

}
