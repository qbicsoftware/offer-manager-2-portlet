package life.qbic.portal.portlet.offers

import java.text.DecimalFormat
import java.text.NumberFormat

/**
 * Defines the currency for prices in the offer context
 *
 * @since: 0.1.0
 *
 */
class Currency {

    @Deprecated
    static final DecimalFormat currencyFormat = new DecimalFormat("#,##0.00 €")

    private final static NumberFormat numberFormat = NumberFormat.getInstance(Locale.US)

    /**
     * This method creates a decimal formatter according to the american style
     *
     * @return number like 5,000.00
     */
    static DecimalFormat getFormatterWithoutSymbol(){
        DecimalFormat dotFormat_withoutSymbol = (DecimalFormat) numberFormat
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
        DecimalFormat dotFormat_withSymbol = (DecimalFormat) numberFormat
        dotFormat_withSymbol.applyPattern("#,##0.00 €")

        return dotFormat_withSymbol
    }


}
