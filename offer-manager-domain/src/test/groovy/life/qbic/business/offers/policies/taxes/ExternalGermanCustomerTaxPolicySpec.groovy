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
class ExternalGermanCustomerTaxPolicySpec extends Specification {

    def "Given an customer within Germany has an external affiliation, apply taxes"() {
        given:
        TaxPolicy policy = new ExternalGermanCustomerTaxPolicy(affiliationCategory, "Germany")
        BigDecimal value = new BigDecimal("1")

        when:
        def priceWithTaxes = policy.calculateTaxes(value)

        then:
        priceWithTaxes == new BigDecimal("0.19")

        where:
        affiliationCategory << [AffiliationCategory.EXTERNAL, AffiliationCategory.EXTERNAL_ACADEMIC]
    }

    def "The policy must not be applied to internal customers"() {
        when:
        ExternalGermanCustomerTaxPolicy.of(affiliationCategory, "Germany")

        then:
        thrown(PolicyViolationException)

        where:
        affiliationCategory << [AffiliationCategory.INTERNAL]
    }

    def "The policy must not be applied to customers outside of Germany"() {
        when:
        ExternalGermanCustomerTaxPolicy.of(affiliationCategory, "France")

        then:
        thrown(PolicyViolationException.class)

        where:
        affiliationCategory << [AffiliationCategory.EXTERNAL, AffiliationCategory.EXTERNAL_ACADEMIC]
    }
}
