package life.qbic.business.offers.policies.taxes;

import java.math.BigDecimal;
import life.qbic.business.persons.affiliation.AffiliationCategory;

/**
 * <h1>External Inside Germany Tax Policy</h1>
 * <br>
 * <p> Policy describing how taxes are applied for external affiliations located in germany</p>
 *
 * @since: 1.3.0
 *
 */
public class ExternalGermanCustomerTaxPolicy implements TaxPolicy {

  private static final BigDecimal VAT_RATIO = new BigDecimal("0.19");

  /**
   * Returns a ExternalGermanCustomerTaxPolicy for a given affiliation category and country
   * @param affiliationCategory The affiliation category to which the tax policy shall be applied
   * @param country The affiliation country to which the tax policy shall be applied
   * @return a ExternalGermanCustomerTaxPolicy
   */
  public static ExternalGermanCustomerTaxPolicy of(AffiliationCategory affiliationCategory,
      String country) {
    if (affiliationCategory == AffiliationCategory.INTERNAL) {
      throw new PolicyViolationException("Policy must not be applied to internal customers");
    }
    if (!country.equalsIgnoreCase("Germany")) {
      throw new PolicyViolationException("The Policy must not be applied to customers outside of Germany");
    }
    return new ExternalGermanCustomerTaxPolicy();
  }

  private ExternalGermanCustomerTaxPolicy() {
  }

  @Override
  public BigDecimal calculateTaxes(BigDecimal value) {
    return value.multiply(VAT_RATIO);
  }

  @Override
  public BigDecimal getVatRatio() {
    return VAT_RATIO;
  }
}
