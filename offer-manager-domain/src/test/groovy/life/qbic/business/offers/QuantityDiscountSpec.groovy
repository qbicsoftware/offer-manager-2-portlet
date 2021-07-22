package life.qbic.business.offers

import spock.lang.Shared
import spock.lang.Specification

/**
 * <b><short description></b>
 *
 * <p><detailed description></p>
 *
 * @since <versiontag>
 */
class QuantityDiscountSpec extends Specification {
    @Shared
    private final String DISCOUNT_FILE_NAME = "discount_per_sample_size.csv"
    @Shared
    private Map<Integer, Double> discountMap = readDiscountMapFromFile()

    def "Apply leads to correct outputs for #sampleCount samples"() {
        when:"a discount is applied to a discountable price"
        def function = new QuantityDiscount()
        double result = function.apply(sampleCount, discountablePrice)
        then: "the discount is applied correctly"
        result == discountMap.get(sampleCount) * discountablePrice
        where: "the number of samples and the discountable price are as follows"
        sampleCount << discountMap.keySet().iterator()
        discountablePrice = new Double(1.0)
    }

    def "Apply leads Exception in case of unspecified behaviour"() {
        when:"a discount is applied to a discountable price"
        def function = new QuantityDiscount()
        double result = function.apply(sampleCount, discountablePrice)
        then: "the discount is applied correctly"
        thrown(Exception)
        where: "the number of samples and the discountable price are as follows"
        sampleCount << [-1, 0, 1001]
        discountablePrice = new Double(1.0)
    }

    private Map<Integer, Double> readDiscountMapFromFile() {
        URL discountUrl = QuantityDiscountSpec.class.getClassLoader().getResource(DISCOUNT_FILE_NAME)
        File discountFile = new File(discountUrl.getFile())
        Map<Integer, Double> resultingMap = new HashMap<>()
        discountFile.withReader {reader ->
            def lines = reader.readLines()
            for (String line : lines) {
                try {
                    def fields = line.split(",")
                    Integer number = new Integer(fields[0].trim())
                    Double discount = new Double(fields[1].trim())
                    resultingMap.put(number, discount)
                } catch (NumberFormatException ignored) {
                    continue
                }
            }
        }
        return resultingMap
    }
}
