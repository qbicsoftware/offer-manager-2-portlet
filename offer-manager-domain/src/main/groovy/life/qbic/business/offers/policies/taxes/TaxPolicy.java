package life.qbic.business.offers.policies.taxes;

import java.math.BigDecimal;

/**
 * <b><class short description - 1 Line!></b>
 *
 * <p><More detailed description - When to use, what it solves, etc.></p>
 *
 * @since <version tag>
 */
public interface TaxPolicy {

  BigDecimal calculateTaxes(BigDecimal value);

  BigDecimal getVatRatio();
}
