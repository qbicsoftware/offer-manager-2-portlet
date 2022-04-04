package life.qbic.business.offers.policies.taxes;

import java.math.BigDecimal;
import life.qbic.business.persons.affiliation.AffiliationCategory;

public class ExternalGermanCustomerTaxPolicy extends TaxPolicy {

  private static final BigDecimal VAT_RATIO = new BigDecimal("0.19");

  public static ExternalGermanCustomerTaxPolicy of(AffiliationCategory affiliationCategory,
      String country) {
    if (affiliationCategory == AffiliationCategory.INTERNAL) {
      throw new PolicyViolationException("Policy must not be applied to internal customers");
    }
    if (!country.equalsIgnoreCase("Germany")) {
      throw new PolicyViolationException("The Policy must not be applied to customers outside of Germany");
    }
    return new ExternalGermanCustomerTaxPolicy(affiliationCategory, country);
  }

  private ExternalGermanCustomerTaxPolicy(
      AffiliationCategory targetAffiliationCategory,
      String country) {
    super(targetAffiliationCategory, country);
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
