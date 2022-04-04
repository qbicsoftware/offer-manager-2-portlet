package life.qbic.business.offers.policies.taxes;

import java.math.BigDecimal;

/**
 * <h1>Outside Germany Tax Policy</h1>
 * <br>
 * <p>Policy describing how taxes are applied for affiliations located outside of germany</p>
 *
 * @since: 1.3.0
 *
 */
public class OutsideGermanyTaxPolicy implements TaxPolicy {

  /**
   * Returns a OutsideGermanyTaxPolicy for a given country
   * @param country The affiliation country to which the tax policy shall be applied
   * @return a OutsideGermanyTaxPolicy
   */
  protected static OutsideGermanyTaxPolicy of(String country) {
    if (country.equalsIgnoreCase("Germany")) {
      throw new PolicyViolationException(
          "Policy must be applied to countries outside of Germany only.");
    }
    return new OutsideGermanyTaxPolicy();
  }

  private OutsideGermanyTaxPolicy() {
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
