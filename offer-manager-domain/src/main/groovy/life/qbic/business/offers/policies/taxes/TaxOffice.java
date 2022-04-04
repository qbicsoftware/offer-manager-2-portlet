package life.qbic.business.offers.policies.taxes;

import life.qbic.business.persons.affiliation.AffiliationCategory;


/**
 * <b>Tax Office</b>
 *
 * <p>The central class to apply the correct {@link TaxPolicy} for a given {@link AffiliationCategory} and country</p>
 *
 * @since 1.3.0
 */
public class TaxOffice {

  /**
   * Applies the correct tax policy for a given affiliation category and a country
   * @param category The affilation category onto which the policy will be applied
   * @param country The country of the affilition onto which the policy will be applied
   * @return a {@link TaxPolicy} which returns the correct tax ratio and value
   */
  public static TaxPolicy policyFor(AffiliationCategory category, String country) {
    if (!country.equalsIgnoreCase("germany")) {
      return OutsideGermanyTaxPolicy.of(country);
    }
    if (category == AffiliationCategory.INTERNAL) {
      return InternalTaxPolicy.of(category, country);
    }
    // External but customer within Germany is our default policy when not
    // specified else
    return ExternalGermanCustomerTaxPolicy.of(category, country);
  }

}
