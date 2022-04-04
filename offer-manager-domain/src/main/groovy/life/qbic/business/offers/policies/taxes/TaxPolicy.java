package life.qbic.business.offers.policies.taxes;

import java.math.BigDecimal;
import life.qbic.business.persons.affiliation.AffiliationCategory;

/**
 * <b><class short description - 1 Line!></b>
 *
 * <p><More detailed description - When to use, what it solves, etc.></p>
 *
 * @since <version tag>
 */
public abstract class TaxPolicy {

    private final AffiliationCategory targetAffiliationCategory;

    private final String country;

    protected TaxPolicy(
        AffiliationCategory targetAffiliationCategory, String country) {
        this.targetAffiliationCategory = targetAffiliationCategory;
        this.country = country;
    }

    abstract public BigDecimal calculateTaxes(BigDecimal value);

    abstract public BigDecimal getVatRatio();
}
