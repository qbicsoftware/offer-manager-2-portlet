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
      return new OutsideGermanyTaxPolicy(category, country);
    }
    if (category == AffiliationCategory.INTERNAL) {
      return new InternalTaxPolicy(category, country);
    }
    // External but customer within Germany is our default policy when not
    // specified else
    return new ExternalGermanCustomerTaxPolicy(category, country);
  }

}
