package life.qbic.business.offers.policies.taxes;

import java.math.BigDecimal;
import life.qbic.business.persons.affiliation.AffiliationCategory;

/**
 * <h1>Internal Tax Policy</h1>
 * <br>
 * <p> Policy describing how taxes are applied for internal affiliations</p>
 *
 * @since: 1.3.0
 *
 */
public class InternalTaxPolicy implements TaxPolicy {

  /**
   * Returns a InternalTaxPolicy for a given affiliation category and country
   * @param category The affiliation category to which the tax policy shall be applied
   * @param country The affiliation country to which the tax policy shall be applied
   * @return a InternalTaxPolicy
   */
  protected static InternalTaxPolicy of(AffiliationCategory category, String country) {
    if (category != AffiliationCategory.INTERNAL) {
      throw new PolicyViolationException("Cannot apply internal tax policy to affiliation of type " + category);
    }
    if (!country.equalsIgnoreCase("Germany")) {
      throw new PolicyViolationException(
          "Cannot apply internal tax policy to countries outside of Germany: " + country);
    }
    return new InternalTaxPolicy();
  }

  private InternalTaxPolicy() {
  }

  @Override
  public BigDecimal calculateTaxes(BigDecimal value) {
    return BigDecimal.ZERO;
  }

  @Override
  public BigDecimal getVatRatio() {
    return BigDecimal.ZERO;
  }
}
