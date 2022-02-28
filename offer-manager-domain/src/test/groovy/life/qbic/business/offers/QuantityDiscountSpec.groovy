package life.qbic.business.offers

import spock.lang.Shared
import spock.lang.Specification

/**
 * <p>Verifies that the QuantityDiscount function works as expected</p>
 *
 * @since 1.1.0
 */
class QuantityDiscountSpec extends Specification {
    @Shared
    private final String DISCOUNT_FILE_NAME = "discount_per_sample_size.csv"
    @Shared
    private Map<Integer, BigDecimal> discountMap = readDiscountMapFromFile()

    def "Apply leads to correct outputs for #sampleCount samples"() {
        when:"a discount is applied to a discountable price"
        def function = new QuantityDiscount()
        BigDecimal result = function.apply(sampleCount, discountablePrice)
        then: "the discount is applied correctly"
        result == ( 1 - discountMap.get(sampleCount) )* discountablePrice
        where: "the number of samples and the discountable price are as follows"
        sampleCount << discountMap.keySet().iterator()
        discountablePrice = new BigDecimal(42.5)
    }

    def "Apply leads Exception in case of unspecified behaviour"() {
        when:"a discount is applied to a discountable price"
        def function = new QuantityDiscount()
        double result = function.apply(sampleCount, discountablePrice)
        then: "the discount is applied correctly"
        thrown(Exception)
        where: "the number of samples and the discountable price are as follows"
        sampleCount << [-1, 0]
        discountablePrice = new BigDecimal(42.5)
    }

    def "Apply leads to no discount for #sampleCount samples"() {
        when:"a discount is applied to a discountable price"
        def function = new QuantityDiscount()
        double result = function.apply(sampleCount, discountablePrice)
        then: "the discount is applied correctly"
        result == BigDecimal.ZERO
        where: "the number of samples and the discountable price are as follows"
        sampleCount << [0, 0.1, 0.5, 0.9]
        discountablePrice = new BigDecimal(42.5)
    }

    def "Apply leads to minimum discount for sample count greater #maxDefinedCount"() {
        when:"a discount is applied to a discountable price"
        def function = new QuantityDiscount()
        double result = function.apply(sampleCount, discountablePrice)
        double largestDefined = function.apply(maxDefinedCount, discountablePrice)
        then: "the discount is equal to the discount for #maxDefinedCount samples"
        result == largestDefined
        where: "the number of samples is greater then the highest number defined"
        sampleCount << [1000, 1001, 1002, 200000]
        discountablePrice = new BigDecimal(42.5)
        maxDefinedCount = 1000
    }

    private Map<Integer, BigDecimal> readDiscountMapFromFile() {
        URL discountUrl = QuantityDiscountSpec.class.getClassLoader().getResource(DISCOUNT_FILE_NAME)
        File discountFile = new File(discountUrl.getFile())
        Map<Integer, BigDecimal> resultingMap = new HashMap<>()
        discountFile.withReader {reader ->
            def lines = reader.readLines()
            for (String line : lines) {
                try {
                    def fields = line.split(",")
                    Integer number = new Integer(fields[0].trim())
                    BigDecimal discount = new BigDecimal(fields[1].trim())
                    resultingMap.put(number, discount)
                } catch (NumberFormatException ignored) {
                    continue
                }
            }
        }
        return resultingMap
    }
}
