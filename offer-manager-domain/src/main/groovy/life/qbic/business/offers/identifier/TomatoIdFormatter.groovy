package life.qbic.business.offers.identifier
/**
 * <b><short description></b>
 *
 * <p><detailed description></p>
 *
 * @since <version tag>
 */
class TomatoIdFormatter {

    static <T extends TomatoId> String formatAsOfferId(T identifier) {
        return generateIdString(identifier.projectPart, identifier.randomPart, identifier.version)
    }

    static <T extends life.qbic.datamodel.dtos.business.TomatoId> String formatAsOfferId(T identifier) {
        TomatoId tomatoId = TomatoIdDtoMapper.DTO_TO_TOMATO_ID.apply(identifier)
        return generateIdString(tomatoId.projectPart, tomatoId.randomPart, tomatoId.version)
    }

    private static String generateIdString(String projectPart, String randomPart, int version) {
        return "O_${projectPart}_${randomPart}_${version}"
    }

}
