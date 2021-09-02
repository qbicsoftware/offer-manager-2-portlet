package life.qbic.business.offers

import java.util.function.Function

/**
 * <b>Function for data storage discount calculation</b>
 *
 * <p>Applies a data storage discount to a given data storage cost value.</p>
 *
 * @since 1.1.0
 */
class DataStorageDiscount implements Function<BigDecimal, BigDecimal> {

    // 100% discount
    private final static BigDecimal DISCOUNT_RATIO = 1.0

    /**
     * <p>Calculates the absolute discount amount for a non-negative given data store cost value.</p>
     *
     * <p>For example a data management cost value of 20.00€ will return the discount amount in xx.xx€.</p>
     * @param dataStoragePrice the storage price value the discount shall be calculated for, must be not negative
     * @return The discount amount value
     * @since 1.1.0
     */
    @Override
    BigDecimal apply(BigDecimal dataStoragePrice) {
        if (dataStoragePrice < 0) {
            throw new IllegalArgumentException("Cannot determine discount for negative price values.")
        }
        return DISCOUNT_RATIO * dataStoragePrice
    }
}
