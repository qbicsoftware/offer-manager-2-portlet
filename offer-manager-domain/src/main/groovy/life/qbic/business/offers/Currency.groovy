package life.qbic.business.offers

import java.text.DecimalFormat
import java.text.NumberFormat

/**
 * Defines the currency for prices in the offer context
 *
 * @since: 0.1.0
 *
 */
class Currency {

    final static String SYMBOL = "€"
    private final static NumberFormat NUMBER_FORMAT = NumberFormat.getInstance(Locale.US)

    static String format(double value) {
        DecimalFormat decimalFormat = getFormatterWithoutSymbol()
        return decimalFormat.format(value)
    }

    /**
     * This method creates a decimal formatter according to the american style
     *
     * @return number like 5,000.00
     */
    static DecimalFormat getFormatterWithoutSymbol(){
        DecimalFormat dotFormat_withoutSymbol = (DecimalFormat) NUMBER_FORMAT
        dotFormat_withoutSymbol.applyPattern("#,##0.00")

        return dotFormat_withoutSymbol
    }

    /**
     * This method creates a decimal formatter according to the american style with
     * the european currency symbol
     *
     * @return number like 5,000.00 €
     */
    static DecimalFormat getFormatterWithSymbol(){
        DecimalFormat dotFormat_withSymbol = (DecimalFormat) NUMBER_FORMAT
        dotFormat_withSymbol.applyPattern("#,##0.00 €")

        return dotFormat_withSymbol
    }


}
