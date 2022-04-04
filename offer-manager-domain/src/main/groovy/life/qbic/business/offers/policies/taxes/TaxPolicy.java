package life.qbic.business.offers.policies.taxes;

import java.math.BigDecimal;

/**
 * <h1>Tax Policy Interface</h1>
 * <br>
 * <p> Tax policies describe how taxes are applied</p>
 *
 * @since: 1.3.0
 *
 */
public interface TaxPolicy {

  /**
   * Calculates the taxes for a given price value
   * @param value A price value onto which taxes need to be applied
   * @return value with applied taxes
   */
  BigDecimal calculateTaxes(BigDecimal value);

  /**
   * Returns the vat ratio of an implementing class
   * @return vat ratio for a given tax policy
   */
  BigDecimal getVatRatio();
}
