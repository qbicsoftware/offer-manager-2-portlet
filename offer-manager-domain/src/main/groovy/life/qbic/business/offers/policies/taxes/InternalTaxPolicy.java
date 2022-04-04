package life.qbic.business.offers.policies.taxes;

import java.math.BigDecimal;
import life.qbic.business.persons.affiliation.AffiliationCategory;

public class InternalTaxPolicy implements TaxPolicy {

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
