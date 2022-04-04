package life.qbic.business.offers.policies.taxes;

import java.math.BigDecimal;
import life.qbic.business.persons.affiliation.AffiliationCategory;

public class OutsideGermanyTaxPolicy extends TaxPolicy {

  protected static OutsideGermanyTaxPolicy of(AffiliationCategory category, String country) {
    if (country.equalsIgnoreCase("Germany")) {
      throw new PolicyViolationException("Policy must be applied to countries outside of Germany only.");
    }
    return new OutsideGermanyTaxPolicy(category, country);
  }

  private OutsideGermanyTaxPolicy(
      AffiliationCategory targetAffiliationCategory,
      String country) {
    super(targetAffiliationCategory, country);
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
