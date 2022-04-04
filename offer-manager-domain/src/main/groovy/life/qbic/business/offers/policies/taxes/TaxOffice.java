package life.qbic.business.offers.policies.taxes;

import life.qbic.business.persons.affiliation.AffiliationCategory;


/**
 * <b><class short description - 1 Line!></b>
 *
 * <p><More detailed description - When to use, what it solves, etc.></p>
 *
 * @since <version tag>
 */
public class TaxOffice {

  public static TaxPolicy policyFor(AffiliationCategory category, String country) {
    if (!country.equalsIgnoreCase("germany")) {
      return OutsideGermanyTaxPolicy.of(category, country);
    }
    if (category == AffiliationCategory.INTERNAL) {
      return InternalTaxPolicy.of(category, country);
    }
    // External but customer within Germany is our default policy when not
    // specified else
    return ExternalGermanCustomerTaxPolicy.of(category, country);
  }

}
