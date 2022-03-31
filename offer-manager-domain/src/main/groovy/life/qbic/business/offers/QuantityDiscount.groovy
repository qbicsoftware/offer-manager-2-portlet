package life.qbic.business.offers

import java.util.function.BiFunction

/**
 * <b>Function for quantity discount</b>
 *
 * <p>Applies a quantity discount to the discountable price depending on the number of samples.</p>
 *
 * @since 1.1.0
 */
class QuantityDiscount implements BiFunction<BigDecimal, BigDecimal, BigDecimal> {

    private static final QuantityDiscountFactor quantityDiscountFactor = new QuantityDiscountFactor()

    /**
     * Computes a quantity discount of the full price depending on the number of samples.
     * It is defined for all positive natural numbers greater than 0
     * <p>The function returns the discount for the discountable price. For a discount of 20% and a discountable price of 100$, the return value would be 20$.</p>
     * @param quantity the quantity for which to calculate a discount for
     * @param price the price that should be discounted, before discounts are applied
     * @return the size of the discount
     * @since 1.1.0
     */
    @Override
    BigDecimal apply(BigDecimal quantity, BigDecimal price) {
        return quantityDiscountFactor.apply(quantity) * price
    }
}
