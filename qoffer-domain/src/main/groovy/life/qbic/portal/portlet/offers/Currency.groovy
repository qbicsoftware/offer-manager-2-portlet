package life.qbic.portal.portlet.offers

import java.text.DecimalFormat

/**
 * Defines the currency for prices in the offer context
 *
 * @since: 0.1.0
 *
 */
class Currency {

    static final java.util.Currency currency = java.util.Currency.getInstance(Locale.getDefault())
    static final DecimalFormat currencyFormat = new DecimalFormat(currency.symbol+"#,##0.00")

}
