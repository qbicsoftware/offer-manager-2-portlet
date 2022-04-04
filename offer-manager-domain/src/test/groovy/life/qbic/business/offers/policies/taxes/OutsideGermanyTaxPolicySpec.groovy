package life.qbic.business.offers.policies.taxes

import life.qbic.business.persons.affiliation.AffiliationCategory
import spock.lang.Specification

/**
 * <b><class short description - 1 Line!></b>
 *
 * <p><More detailed description - When to use, what it solves, etc.></p>
 *
 * @since <version tag>
 */
class OutsideGermanyTaxPolicySpec extends Specification {

    def "Given an customer outside of Germany, apply no taxes"() {
        given:
        TaxPolicy taxPolicy = OutsideGermanyTaxPolicy.of(affiliationCategory, "France")
        BigDecimal value = new BigDecimal("10")

        when:
        def taxes = taxPolicy.calculateTaxes(value)

        then:
        taxes == BigDecimal.ZERO

        where:
        affiliationCategory << [AffiliationCategory.EXTERNAL_ACADEMIC, AffiliationCategory.EXTERNAL]
    }

    def "The policy must not be applied to a customer from Germany"() {
        when:
        OutsideGermanyTaxPolicy.of(affiliationCategory, "Germany")

        then:
        thrown(PolicyViolationException)

        where:
        affiliationCategory << [AffiliationCategory.INTERNAL, AffiliationCategory.EXTERNAL_ACADEMIC, AffiliationCategory.EXTERNAL]
    }

}
