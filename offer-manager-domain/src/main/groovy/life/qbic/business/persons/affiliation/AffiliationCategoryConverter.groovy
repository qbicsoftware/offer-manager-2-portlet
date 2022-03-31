package life.qbic.business.persons.affiliation

import javax.persistence.AttributeConverter

/**
 * <b>AffiliationCategoryConverter class</b>
 *
 * <p>Maps affiliation categories to its String representation and vice versa</p>
 *
 * @since 1.3.0
 */
class AffiliationCategoryConverter implements AttributeConverter<AffiliationCategory, String>{

    @Override
    String convertToDatabaseColumn(AffiliationCategory affiliationCategory) {
        return affiliationCategory.getLabel()
    }

    @Override
    AffiliationCategory convertToEntityAttribute(String s) {
        return AffiliationCategory.forLabel(s)
    }
}
