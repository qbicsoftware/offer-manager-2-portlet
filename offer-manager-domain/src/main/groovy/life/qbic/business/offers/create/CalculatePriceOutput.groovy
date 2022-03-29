package life.qbic.business.offers.create

/**
 * <b>short description</b>
 *
 * <p>detailed description</p>
 *
 * @since <version tag>
 */
interface CalculatePriceOutput {
  /**
   * Transfers the calculated net price, taxes, overheads and total price
   * to an implementing class
   * @param netPrice The net price calculated for the requested services
   * @param taxes The amount of taxes for the requested services
   * @param overheads The amount of overheads for the requested services
   * @param totalPrice The total price for the requested services, includes taxes and overheads
   * @param totalDiscountAmount The total discount amount applied on the offer
   * @since 1.1.0
   */
  void calculatedPrice(double netPrice, double taxes, double overheads, double totalPrice, double totalDiscountAmount)
}
