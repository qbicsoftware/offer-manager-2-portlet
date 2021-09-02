package life.qbic.business.offers

import spock.lang.Shared
import spock.lang.Specification

/**
 * Tests for the {@link DataStorageDiscount} class.
 *
 * @since 1.1.0
 */
class DataStorageDiscountSpec extends Specification{

    @Shared
    DataStorageDiscount discount = new DataStorageDiscount()

    def "Full discount for positive data management cost values"() {
        given: "A positive cost value"
        BigDecimal dataManagementCosts = 1049.99

        when: "we calculate the discount"
        BigDecimal discountAmount = discount.apply(dataManagementCosts)

        then: "we get a full discount amount"
        assert discountAmount == 1.0 * dataManagementCosts

    }

    def "Raise an illegal argument exception, if the input parameter for the cost is negative"() {
        given: "A positive cost value"
        BigDecimal dataManagementCosts = -1049.99

        when: "we calculate the discount"
        discount.apply(dataManagementCosts)

        then: "we get a full discount amount"
        thrown(IllegalArgumentException)
    }
}
