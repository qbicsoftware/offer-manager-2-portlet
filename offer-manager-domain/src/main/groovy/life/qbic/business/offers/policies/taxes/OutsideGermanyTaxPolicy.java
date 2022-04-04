package life.qbic.business.offers.policies.taxes;

import java.math.BigDecimal;
import life.qbic.business.persons.affiliation.AffiliationCategory;

public class OutsideGermanyTaxPolicy extends TaxPolicy {

  public static OutsideGermanyTaxPolicy of(AffiliationCategory category, String country) {
    return new OutsideGermanyTaxPolicy(category, country);
  }

  protected OutsideGermanyTaxPolicy(
      AffiliationCategory targetAffiliationCategory,
      String country) {
    super(targetAffiliationCategory, country);
  }

  @Override
  public BigDecimal calculateTaxes(BigDecimal value) {
    return value;
  }

  @Override
  public BigDecimal getVatRatio() {
    return BigDecimal.ZERO;
  }
}
