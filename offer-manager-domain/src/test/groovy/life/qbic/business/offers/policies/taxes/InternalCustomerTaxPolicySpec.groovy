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
class InternalCustomerTaxPolicySpec extends Specification {

    def "For internal customers, no taxes are applied" () {
        given:
        AffiliationCategory category = AffiliationCategory.INTERNAL
        BigDecimal value = new BigDecimal("1.00")

        when:
        TaxPolicy policy = InternalTaxPolicy.of(category, "Germany")

        then:
        policy.vatRatio == BigDecimal.ZERO
        policy.calculateTaxes(value) == BigDecimal.ZERO
    }

    def "The policy must not be applied to non internal customers" () {
        when:
        InternalTaxPolicy.of(category, "Germany")

        then:
        thrown(PolicyViolationException.class)

        where:
        category << [ AffiliationCategory.EXTERNAL_ACADEMIC, AffiliationCategory.EXTERNAL ]
    }

    def "The policy must not be applied to customers outside of Germany" () {
        when:
        InternalTaxPolicy.of(category, "France")

        then:
        thrown(PolicyViolationException.class)

        where:
        category << [ AffiliationCategory.INTERNAL, AffiliationCategory.EXTERNAL_ACADEMIC, AffiliationCategory.EXTERNAL ]
    }

}
