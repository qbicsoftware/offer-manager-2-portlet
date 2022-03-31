package life.qbic.business.offers;

import java.math.BigDecimal;
import java.util.function.Function;

/**
 * Computes a quantity discount rate depending on the sample quantity. It is defined for all
 * positive natural numbers greater than 0. If the provided quantity is not a natural number,
 * the discount rate is returned for floor(quantity).
 * <p>The function returns the discount rate for the discountable price. For a discount of 20%, the return value would be 0.2</p>
 * @since 1.3.0
 */
public class QuantityDiscountFactor implements Function<BigDecimal, BigDecimal> {

  /**
   * Computes a quantity discount rate depending on the sample quantity. It is defined for all
   * positive natural numbers greater than 0. If the provided quantity is not a natural number,
   * the discount rate is returned for floor(quantity).
   * <p>The function returns the discount rate for the discountable price. For a discount of 20%, the return value would be 0.2</p>
   *
   * @param quantity the quantity for which to calculate a discount for. This value is rounded down.
   * @return the discount rate
   * @since 1.3.0
   */
  @Override
  public BigDecimal apply(BigDecimal quantity) {
    return getDiscountFactor(quantity);
  }

  private static BigDecimal getDiscountFactor(BigDecimal quantsampleCounty) {
    int sampleCount = quantsampleCounty.intValue();
    if (sampleCount < 0) {
      throw new UndefinedFunctionException(quantsampleCounty);
    } else if (sampleCount == 0) {
      return BigDecimal.ZERO;
    } else if (sampleCount == 1) {
      return BigDecimal.ZERO;
    } else if (sampleCount == 2) {
      return BigDecimal.ONE.subtract(BigDecimal.valueOf(0.98));
    } else if (sampleCount == 3) {
      return BigDecimal.ONE.subtract(BigDecimal.valueOf(0.95));
    } else if (sampleCount == 4) {
      return BigDecimal.ONE.subtract(BigDecimal.valueOf(0.89));
    } else if (sampleCount == 5) {
      return BigDecimal.ONE.subtract(BigDecimal.valueOf(0.84));
    } else if (sampleCount == 6) {
      return BigDecimal.ONE.subtract(BigDecimal.valueOf(0.79));
    } else if (sampleCount == 7) {
      return BigDecimal.ONE.subtract(BigDecimal.valueOf(0.76));
    } else if (sampleCount == 8) {
      return BigDecimal.ONE.subtract(BigDecimal.valueOf(0.72));
    } else if (sampleCount == 9) {
      return BigDecimal.ONE.subtract(BigDecimal.valueOf(0.69));
    } else if (sampleCount == 10) {
      return BigDecimal.ONE.subtract(BigDecimal.valueOf(0.67));
    } else if (sampleCount == 11) {
      return BigDecimal.ONE.subtract(BigDecimal.valueOf(0.64));
    } else if (sampleCount == 12) {
      return BigDecimal.ONE.subtract(BigDecimal.valueOf(0.62));
    } else if (sampleCount == 13) {
      return BigDecimal.ONE.subtract(BigDecimal.valueOf(0.6));
    } else if (sampleCount == 14) {
      return BigDecimal.ONE.subtract(BigDecimal.valueOf(0.59));
    } else if (sampleCount == 15) {
      return BigDecimal.ONE.subtract(BigDecimal.valueOf(0.57));
    } else if (sampleCount == 16) {
      return BigDecimal.ONE.subtract(BigDecimal.valueOf(0.56));
    } else if (sampleCount == 17) {
      return BigDecimal.ONE.subtract(BigDecimal.valueOf(0.55));
    } else if (sampleCount == 18) {
      return BigDecimal.ONE.subtract(BigDecimal.valueOf(0.54));
    } else if (sampleCount == 19) {
      return BigDecimal.ONE.subtract(BigDecimal.valueOf(0.53));
    } else if (sampleCount == 20) {
      return BigDecimal.ONE.subtract(BigDecimal.valueOf(0.52));
    } else if (sampleCount == 21) {
      return BigDecimal.ONE.subtract(BigDecimal.valueOf(0.51));
    } else if (sampleCount == 22) {
      return BigDecimal.ONE.subtract(BigDecimal.valueOf(0.5));
    } else if (sampleCount == 23 || sampleCount == 24) {
      return BigDecimal.ONE.subtract(BigDecimal.valueOf(0.49));
    } else if (sampleCount == 25) {
      return BigDecimal.ONE.subtract(BigDecimal.valueOf(0.48));
    } else if (sampleCount == 26 || sampleCount == 27) {
      return BigDecimal.ONE.subtract(BigDecimal.valueOf(0.47));
    } else if (sampleCount == 28) {
      return BigDecimal.ONE.subtract(BigDecimal.valueOf(0.46));
    } else if (sampleCount == 29 || sampleCount == 30) {
      return BigDecimal.ONE.subtract(BigDecimal.valueOf(0.45));
    } else if (sampleCount == 31 || sampleCount == 32) {
      return BigDecimal.ONE.subtract(BigDecimal.valueOf(0.44));
    } else if (sampleCount <= 35) {
      return BigDecimal.ONE.subtract(BigDecimal.valueOf(0.43));
    } else if (sampleCount <= 37) {
      return BigDecimal.ONE.subtract(BigDecimal.valueOf(0.42));
    } else if (sampleCount <= 40) {
      return BigDecimal.ONE.subtract(BigDecimal.valueOf(0.41));
    } else if (sampleCount <= 43) {
      return BigDecimal.ONE.subtract(BigDecimal.valueOf(0.4));
    } else if (sampleCount <= 46) {
      return BigDecimal.ONE.subtract(BigDecimal.valueOf(0.39));
    } else if (sampleCount <= 50) {
      return BigDecimal.ONE.subtract(BigDecimal.valueOf(0.38));
    } else if (sampleCount <= 55) {
      return BigDecimal.ONE.subtract(BigDecimal.valueOf(0.37));
    } else if (sampleCount <= 59) {
      return BigDecimal.ONE.subtract(BigDecimal.valueOf(0.36));
    } else if (sampleCount <= 65) {
      return BigDecimal.ONE.subtract(BigDecimal.valueOf(0.35));
    } else if (sampleCount <= 72) {
      return BigDecimal.ONE.subtract(BigDecimal.valueOf(0.34));
    } else if (sampleCount <= 79) {
      return BigDecimal.ONE.subtract(BigDecimal.valueOf(0.33));
    } else if (sampleCount <= 88) {
      return BigDecimal.ONE.subtract(BigDecimal.valueOf(0.32));
    } else if (sampleCount <= 98) {
      return BigDecimal.ONE.subtract(BigDecimal.valueOf(0.31));
    } else if (sampleCount <= 111) {
      return BigDecimal.ONE.subtract(BigDecimal.valueOf(0.3));
    } else if (sampleCount <= 125) {
      return BigDecimal.ONE.subtract(BigDecimal.valueOf(0.29));
    } else if (sampleCount <= 143) {
      return BigDecimal.ONE.subtract(BigDecimal.valueOf(0.28));
    } else if (sampleCount <= 164) {
      return BigDecimal.ONE.subtract(BigDecimal.valueOf(0.27));
    } else if (sampleCount <= 191) {
      return BigDecimal.ONE.subtract(BigDecimal.valueOf(0.26));
    } else if (sampleCount <= 225) {
      return BigDecimal.ONE.subtract(BigDecimal.valueOf(0.25));
    } else if (sampleCount <= 267) {
      return BigDecimal.ONE.subtract(BigDecimal.valueOf(0.24));
    } else if (sampleCount <= 323) {
      return BigDecimal.ONE.subtract(BigDecimal.valueOf(0.23));
    } else if (sampleCount <= 398) {
      return BigDecimal.ONE.subtract(BigDecimal.valueOf(0.22));
    } else if (sampleCount <= 496) {
      return BigDecimal.ONE.subtract(BigDecimal.valueOf(0.21));
    } else if (sampleCount <= 643) {
      return BigDecimal.ONE.subtract(BigDecimal.valueOf(0.2));
    } else if (sampleCount <= 840) {
      return BigDecimal.ONE.subtract(BigDecimal.valueOf(0.19));
    } else if (sampleCount <= 1000) {
      return BigDecimal.ONE.subtract(BigDecimal.valueOf(0.18));
    } else {
      return BigDecimal.ONE.subtract(BigDecimal.valueOf(0.18));
    }
  }

  /**
   * This function is not defined for the arguments provided.
   *
   * @since 1.3.0
   */
  private static class UndefinedFunctionException extends IllegalArgumentException {

    UndefinedFunctionException(BigDecimal sampleCount) {
      super(String.format("Cannot determine discount for %s samples.", sampleCount));
    }
  }
}
