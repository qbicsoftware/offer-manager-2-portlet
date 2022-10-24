package life.qbic.business.offers.policies.taxes

import life.qbic.business.persons.affiliation.AffiliationCategory
import spock.lang.Specification

class TaxOfficeSpec extends Specification {

  def "When an offer is issued for an internal customer, the internal tax policy is used" () {
    given:
    AffiliationCategory category = AffiliationCategory.INTERNAL
    String country = "Germany"

    when:
    TaxPolicy policy = TaxOffice.policyFor(category, country)

    then:
    policy.getClass().getCanonicalName().equalsIgnoreCase(InternalTaxPolicy.class.getCanonicalName())
  }

  def "When an offer is issued for an external customer outside of Germany, the Outside Germany Tax policy tax policy is used" () {
    given:
    AffiliationCategory category = affiliationCategory
    String country = "France"

    when:
    TaxPolicy policy = TaxOffice.policyFor(category, country)

    then:
    policy.getClass().getCanonicalName().equalsIgnoreCase(OutsideGermanyTaxPolicy.class.getCanonicalName())

    where:
    affiliationCategory << [AffiliationCategory.EXTERNAL, AffiliationCategory.EXTERNAL_ACADEMIC]
  }

  def "When an offer is issued for an external customer but within Germany, the external customer tax policy is used" () {
    given:
    AffiliationCategory category = affiliationCategory
    String country = "Germany"

    when:
    TaxPolicy policy = TaxOffice.policyFor(category, country)

    then:
    policy.getClass().getCanonicalName().equalsIgnoreCase(ExternalGermanCustomerTaxPolicy.class.getCanonicalName())

    where:
    affiliationCategory << [AffiliationCategory.EXTERNAL, AffiliationCategory.EXTERNAL_ACADEMIC]
  }
}
