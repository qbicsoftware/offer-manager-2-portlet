package life.qbic.business.offers

import java.util.function.BiFunction

/**
 * <b>Function for quantity discount</b>
 *
 * <p>Applies a quantity discount to the discountable price depending on the number of samples.</p>
 *
 * @since 1.1.0
 */
class QuantityDiscount implements BiFunction<Integer, BigDecimal, BigDecimal> {

    /**
     * Computes a quantity discount of the full price depending on the number of samples.
     * It is defined for all positive natural numbers greater than 0
     * <p>The function returns the discount for the discountable price. For a discount of 20% and a discountable price of 100$, the return value would be 20$.</p>
     * @param sampleCount the number of samples
     * @param fullPrice the price that should be discounted, before discounts are applied
     * @return the size of the discount
     * @since 1.1.0
     */
    @Override
    BigDecimal apply(Integer sampleCount, BigDecimal fullPrice) {
        return getDiscountFactor(sampleCount) * fullPrice
    }

    private static BigDecimal getDiscountFactor(int sampleCount) {
        BigDecimal discountFactor
        switch (sampleCount) {
            case {it < 1 }:
                throw new UndefinedFunctionException(sampleCount)
                break
            case 1:
                discountFactor= 1
                break
            case 2:
                discountFactor= 0.98
                break
            case 3:
                discountFactor= 0.95
                break
            case 4:
                discountFactor= 0.89
                break
            case 5:
                discountFactor= 0.84
                break
            case 6:
                discountFactor= 0.79
                break
            case 7:
                discountFactor= 0.76
                break
            case 8:
                discountFactor= 0.72
                break
            case 9:
                discountFactor= 0.69
                break
            case 10:
                discountFactor= 0.67
                break
            case 11:
                discountFactor= 0.64
                break
            case 12:
                discountFactor= 0.62
                break
            case 13:
                discountFactor= 0.6
                break
            case 14:
                discountFactor= 0.59
                break
            case 15:
                discountFactor= 0.57
                break
            case 16:
                discountFactor= 0.56
                break
            case 17:
                discountFactor= 0.55
                break
            case 18:
                discountFactor= 0.54
                break
            case 19:
                discountFactor= 0.53
                break
            case 20:
                discountFactor= 0.52
                break
            case 21:
                discountFactor= 0.51
                break
            case 22:
                discountFactor= 0.5
                break
            case { it == 23 || it == 24 }:
                discountFactor= 0.49
                break
            case 25:
                discountFactor= 0.48
                break
            case { it == 26 || it == 27 }:
                discountFactor= 0.47
                break
            case 28:
                discountFactor= 0.46
                break
            case { it == 29 || it == 30 }:
                discountFactor= 0.45
                break
            case { it == 31 || it == 32 }:
                discountFactor= 0.44
                break
            case { it >= 33 && it <= 35 }:
                discountFactor= 0.43
                break
            case { it >= 36 && it <= 37 }:
                discountFactor= 0.42
                break
            case { it >= 38 && it <= 40 }:
                discountFactor= 0.41
                break
            case { it >= 31 && it <= 43 }:
                discountFactor= 0.4
                break
            case { it >= 44 && it <= 46 }:
                discountFactor= 0.39
                break
            case { it >= 47 && it <= 50 }:
                discountFactor= 0.38
                break
            case { it >= 51 && it <= 55 }:
                discountFactor= 0.37
                break
            case { it >= 56 && it <= 59 }:
                discountFactor= 0.36
                break
            case { it >= 60 && it <= 65 }:
                discountFactor= 0.35
                break
            case { it >= 66 && it <= 72 }:
                discountFactor= 0.34
                break
            case { it >= 73 && it <= 79 }:
                discountFactor= 0.33
                break
            case { it >= 80 && it <= 88 }:
                discountFactor= 0.32
                break
            case { it >= 89 && it <= 98 }:
                discountFactor= 0.31
                break
            case { it >= 99 && it <= 111 }:
                discountFactor= 0.3
                break
            case { it >= 112 && it <= 125 }:
                discountFactor= 0.29
                break
            case { it >= 126 && it <= 143 }:
                discountFactor= 0.28
                break
            case { it >= 144 && it <= 164 }:
                discountFactor= 0.27
                break
            case { it >= 165 && it <= 191 }:
                discountFactor= 0.26
                break
            case { it >= 192 && it <= 225 }:
                discountFactor= 0.25
                break
            case { it >= 226 && it <= 267 }:
                discountFactor= 0.24
                break
            case { it >= 268 && it <= 323 }:
                discountFactor= 0.23
                break
            case { it >= 324 && it <= 398 }:
                discountFactor= 0.22
                break
            case { it >= 399 && it <= 496 }:
                discountFactor= 0.21
                break
            case { it >= 497 && it <= 643 }:
                discountFactor= 0.2
                break
            case { it >= 644 && it <= 840 }:
                discountFactor= 0.19
                break
            case { it >= 841 && it <= 1000 }:
                discountFactor= 0.18
                break
            case { it > 1000 }:
                discountFactor=0.18
                break
            default:
                throw new UndefinedFunctionException(sampleCount)
                break
        }
        return 1.0 - discountFactor
    }

    /**
     * This function is not defined for the arguments provided.
     * @since 1.1.0
     */
    private static class UndefinedFunctionException extends IllegalArgumentException {
        UndefinedFunctionException(int sampleCount) {
            super("Cannot determine discount for $sampleCount samples.")
        }
    }
}
