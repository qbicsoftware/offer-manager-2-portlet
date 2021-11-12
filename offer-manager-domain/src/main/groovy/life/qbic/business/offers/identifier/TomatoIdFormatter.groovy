package life.qbic.business.offers.identifier
/**
 * <b>Formats different Ids to String.</b>
 *
 * @since 1.2.2
 */
class TomatoIdFormatter {

    static <T extends OfferId> String formatAsOfferId(T identifier) {
        return generateOfferIdString(identifier.projectPart, identifier.randomPart, identifier.version)
    }

    static <T extends life.qbic.datamodel.dtos.business.TomatoId> String formatAsOfferId(T identifier) {
        OfferId tomatoId = OfferIdDtoMapper.DTO_TO_OFFER_ID.apply(identifier)
        return generateOfferIdString(tomatoId.projectPart, tomatoId.randomPart, tomatoId.version)
    }

    static String removeVersion(String formattedId) {
        int versionDelimiterPosition = formattedId.findLastIndexOf {it == "_"}
        return formattedId.substring(0, versionDelimiterPosition)
    }

    private static String generateOfferIdString(String projectPart, String randomPart, int version) {
        return "O_${projectPart}_${randomPart}_${version}"
    }

}
