package life.qbic.business.offers.policies.taxes;

import java.math.BigDecimal;

public class OutsideGermanyTaxPolicy implements TaxPolicy {

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
