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
     * <p>Calculates the absolute discount amount for a given data store cost value.</p>
     *
     * <p>For example a data management cost value of 20.00€ will return the discount amount in xx.xx€.</p>
     * @param dataStoragePrice the storage price value the discount shall be calculated for
     * @return The discount amount value
     * @since 1.1.0
     */
    @Override
    BigDecimal apply(BigDecimal dataStoragePrice) {
        if (dataStoragePrice < 0) {
            throw new UndefinedFunctionException()
        }
        return DISCOUNT_RATIO * dataStoragePrice
    }

    /**
     * This function is not defined for the arguments provided.
     * @since 1.1.0
     */
    private static class UndefinedFunctionException extends IllegalArgumentException {
        UndefinedFunctionException() {
            super("Cannot determine discount for negative price values.")
        }
    }
}
