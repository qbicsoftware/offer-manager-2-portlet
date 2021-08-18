package life.qbic.portal.offermanager.offergeneration

import life.qbic.business.offers.Currency
import life.qbic.business.offers.OfferContent
import life.qbic.business.offers.OfferItem
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

import java.text.DateFormat
import java.text.DecimalFormat

/**
 * <b>An offer template that can be filled with an OfferContent</b>
 *
 * <p>This class represents a html document structure. It provides filled versions of the same document.</p>
 *
 * @since 1.1.0
 */
class OfferTemplate {

    private Document document
    protected static final DecimalFormat PERCENTAGE_FORMATTER = new DecimalFormat("#%")

    /**
     * Creates a new OfferTemplate using the provided document as template document.
     * <p>Please note: It is important that the template document provides the following css selectors</p>
     * <ul>
     *  <li>.currency-symbol</li>
     *  <li>.vat-percentage</li>
     *  <li>.overheads-percentage</li>
     *  <li>#customer-name</li>
     *  <li>#customer-organisation</li>
     *  <li>#customer-street</li>
     *  <li>#customer-postal-code</li>
     *  <li>#customer-city</li>
     *  <li>#customer-country</li>
     *  <li>#qbic-ad</li>
     *  <li>#project-manager-name</li>
     *  <li>#project-manager-street</li>
     *  <li>#project-manager-city</li>
     *  <li>#project-manager-email</li>
     *  <li>#doctype-desc</li>
     *  <li>#offer-date</li>
     *  <li>#offer-identifier</li>
     *  <li>#offer-expiry-date</li>
     *  <li>#project-title</li>
     *  <li>#project-description</li>
     *  <li>#experimental-design</li>
     *  <li>.overheads  .data-generation  > .costs</li>
     *  <li>.overheads  .data-analysis  > .costs</li>
     *  <li>.overheads  .data-management > .costs</li>
     *  <li>.overheads  > .totals-section  > .costs</li>
     *  <li>.total-costs .net > .costs</li>
     *  <li>.total-costs .discounts > .costs</li>
     *  <li>.total-costs .overheads > .costs</li>
     *  <li>.total-costs .vat > .costs</li>
     *  <li>.total-costs > .totals-section > .costs</li>
     *  <li>#data-generation-items</li>
     *  <li>#data-generation-items > tfoot .costs</li>
     *  <li>#data-analysis-items</li>
     *  <li>#data-analysis-items > tfoot .costs</li>
     *  <li>#data-management-items</li>
     *  <li>#data-management-items > tfoot .costs</li>
     * </ul>
     * @param document the template document
     * @since 1.0.0
     */
    OfferTemplate(Document document) {
        this.document = document
    }

    /**
     * Fills the template with the specified content
     * @param content the content to be parsed and replaced in the offer
     * @return a document filled with the provided content based on this template
     * @since 1.1.0
     */
    Document fill(OfferContent content) {
        // make a copy of the template
        Document result = this.document.clone()
        fillCustomerInformation(result, content)
        fillProjectManagerInformation(result, content)
        // fill offer information
        fillOfferInformation(result, content)
        // fill cost summaries
        fillCostSummary(result, content)
        fillDataGenerationItems(result, content)
        fillDataAnalysisItems(result, content)
        fillDataManagementItems(result, content)
        // fill general information
        fillGeneralInformation(result, content)
        return result
    }

    private static void fillGeneralInformation(Document document, OfferContent offer) {
        document.getElementsByClass("currency-symbol").each {
            it.text(Currency.SYMBOL)
        }
        document.select("#front-page-summary .cost-summary .costs").each {
            it.appendText(" " + Currency.SYMBOL)
        }
        document.getElementsByClass("vat-percentage").each {
            String vatPercentage = PERCENTAGE_FORMATTER.format(offer.getVatRatio())
            it.text(vatPercentage)
        }
        document.getElementsByClass("overheads-percentage").each {
            String overheadPercentage = PERCENTAGE_FORMATTER.format(offer.getOverheadRatio())
            it.text(overheadPercentage)
        }
    }

    private static void fillCustomerInformation(Document document, OfferContent offer) {
        String customerName = String.format(
                "%s %s %s",
                offer.getCustomerTitle(),
                offer.getCustomerFirstName(),
                offer.getCustomerLastName())
        document.getElementById("customer-name").text(customerName)
        document.getElementById("customer-organisation").text(offer.getCustomerOrganisation())
        document.getElementById("customer-street").text(offer.getCustomerStreet())
        document.getElementById("customer-postal-code").text(offer.getCustomerPostalCode())
        document.getElementById("customer-city").text(offer.getCustomerCity())
        document.getElementById("customer-country").text(offer.getCustomerCountry())
    }

    private static void fillProjectManagerInformation(Document document, OfferContent offer) {
        String projectManagerName = String.format(
                "%s %s %s",
                offer.getProjectManagerTitle(),
                offer.getProjectManagerFirstName(),
                offer.getProjectManagerLastName())
        document.getElementById("project-manager-name").text(projectManagerName)
        document.getElementById("project-manager-street").text(offer.getProjectManagerStreet())
        document.getElementById("project-manager-postal-code").text(offer.getProjectManagerPostalCode())
        document.getElementById("project-manager-city").text(offer.getProjectManagerCity())
        document.getElementById("project-manager-email").text(offer.getProjectManagerEmail())
    }

    private static void fillOfferInformation(Document document, OfferContent offer) {
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG, Locale.US)
        document.getElementById("doctype-desc").text("Quotation")
        document.getElementById("offer-date").text(dateFormat.format(offer.getCreationDate()))
        document.getElementById("offer-identifier").text(offer.getOfferIdentifier())
        document.getElementById("offer-expiry-date").text(dateFormat.format(offer.getExpirationDate()))
        document.getElementById("project-title").text(offer.getProjectTitle())
        document.getElementById("project-description").text(offer.getProjectObjective())
        if (offer.getExperimentalDesign()) {
            document.getElementById("experimental-design").text(offer.getExperimentalDesign())
        }
    }

    private static void fillCostSummary(Document document, OfferContent offer) {
        // the overhead costs
        document.select(".overheads  .data-generation  > .costs").each {element ->
            element.text(Currency.format(offer.getOverheadsDataGeneration()))
        }
        document.select(".overheads  .data-analysis  > .costs").each {element ->
            element.text(Currency.format(offer.getOverheadsDataAnalysis()))
        }
        document.select(".overheads  .data-management  > .costs").each {element ->
            element.text(Currency.format(offer.getOverheadsProjectManagementAndDataStorage()))
        }
        document.select(".overheads  > .totals-section  > .costs").each {element ->
            element.text(Currency.format(offer.getOverheadTotal()))
        }
        // other fields
        document.select(".total-costs .net > .costs").each {element ->
            element.text(Currency.format(offer.getNetCost()))
        }
        document.select(".total-costs .discounts > .costs").each {element ->
            element.text(Currency.format(offer.getTotalDiscountAmount() * (-1)))
        }
        document.select(".total-costs .overheads > .costs").each {element ->
            element.text(Currency.format(offer.getOverheadTotal()))
        }
        document.select(".total-costs .vat > .costs").each {element ->
            element.text(Currency.format(offer.getTotalVat()))
        }
        document.select(".total-costs > .totals-section > .costs").each {element ->
            element.text(Currency.format(offer.getTotalCost()))
        }
    }

    private static void fillDataGenerationItems(Document document, OfferContent offer) {
        Element dataGenerationTable = document.getElementById("data-generation-items")

        // remove table if no items are available
        if (offer.getDataGenerationItems().isEmpty()) {
            dataGenerationTable.remove()
            return
        }
        // clear the table
        dataGenerationTable.select("tbody > tr").each {element ->
            element.remove()
        }
        // fill the table
        int position = 1
        Element tableBody = dataGenerationTable.selectFirst("tbody")
        for (OfferItem offerItem in offer.getDataGenerationItems()) {
            OfferPosition offerPosition = OfferPosition.createProductItem(position, offerItem)
            tableBody.append(offerPosition.outerHtml())
            position++
            if (offerItem.getQuantityDiscount() > 0) {
                OfferPosition discountPosition = OfferPosition.createDiscount(position, position - 1, offerItem)
                tableBody.append(discountPosition.outerHtml())
                position++
            }
        }
        // set the footer
        String netCosts = Currency.format(offer.getNetDataGeneration())
        dataGenerationTable.select("> tfoot .costs").first().text(netCosts)
    }
    private static void fillDataAnalysisItems(Document document, OfferContent offer) {
        Element dataAnalysisTable = document.getElementById("data-analysis-items")
        // remove table if no items are available
        if (offer.getDataAnalysisItems().isEmpty()) {
            dataAnalysisTable.remove()
            return
        }
        // clear the table
        dataAnalysisTable.select("tbody > tr").each {element ->
            element.remove()
        }
        // fill the table
        int position = 1
        Element tableBody = dataAnalysisTable.selectFirst("tbody")
        for (OfferItem offerItem in offer.getDataAnalysisItems()) {
            OfferPosition offerPosition = OfferPosition.createProductItem(position, offerItem)
            tableBody.append(offerPosition.outerHtml())
            position++
            if (offerItem.getQuantityDiscount() > 0) {
                OfferPosition discountPosition = OfferPosition.createDiscount(position, position - 1, offerItem)
                tableBody.append(discountPosition.outerHtml())
                position++
            }
        }
        // set the footer
        String netCosts = Currency.format(offer.getNetDataAnalysis())
        dataAnalysisTable.select(" > tfoot .costs").first().text(netCosts)
    }
    private static void fillDataManagementItems(Document document, OfferContent offer) {
        Element dataManagementTable = document.getElementById("data-management-items")
        // remove table if no items are available
        if (offer.getDataManagementItems().isEmpty()) {
            dataManagementTable.remove()
            return
        }
        // clear the table
        dataManagementTable.select("tbody > tr").each {element ->
            element.remove()
        }
        // fill the table
        int position = 1
        Element tableBody = dataManagementTable.selectFirst("tbody")
        for (OfferItem offerItem in offer.getDataManagementItems()) {
            OfferPosition offerPosition = OfferPosition.createProductItem(position, offerItem)
            tableBody.append(offerPosition.outerHtml())
            position++
            if (offerItem.getQuantityDiscount() > 0) {
                OfferPosition discountPosition = OfferPosition.createDiscount(position, position - 1, offerItem)
                tableBody.append(discountPosition.outerHtml())
                position++
            }
        }
        // set the footer
        String netCosts = Currency.format(offer.getNetPMandDS())
        document.select("#data-management-items > tfoot .costs").first().text(netCosts)
    }

    private static class OfferPosition {
        private final int position
        private final String name
        private final String description
        private final double quantity
        private final double unitPrice
        private final double total
        private final String unit

        static OfferPosition createProductItem(int position, OfferItem offerItem) {
            String name = offerItem.getProductName()
            String description = createProductItemDescription(offerItem.getProductDescription(), offerItem.getServiceProvider())
            double quantity = offerItem.getQuantity()
            double unitPrice = offerItem.getUnitPrice()
            double total = offerItem.getItemTotal()
            String unit = offerItem.getUnit()
            return new OfferPosition(position, name, description, quantity, unitPrice, total, unit)
        }

        static OfferPosition createDiscount(int position, int discountedPosition, OfferItem offerItem) {
            String name = "Discount"
            double quantity = offerItem.getQuantity()
            double unitPrice = offerItem.getDiscountPerUnit() * (-1)
            double total = offerItem.getQuantityDiscount() * (-1)
            String unit = offerItem.getUnit()
            double discountPercentage = offerItem.getDiscountPercentage()
            String description = createDiscountDescription(discountedPosition, quantity, unit, discountPercentage)
            return new OfferPosition(position, name, description, quantity, unitPrice, total, unit)
        }

        private OfferPosition(int position, String name, String description, double quantity, double unitPrice, double total, String unit) {
            this.position = position
            this.name = name
            this.description = description
            this.quantity = quantity
            this.unitPrice = unitPrice
            this.total = total
            this.unit = unit
        }

        /**
         * Generates a html representation of this offer position
         * @return a html string representing this position
         * @since 1.1.0
         */
        String outerHtml() {
            String quantity = this.quantity.toString()
            String unitPrice = Currency.format(this.unitPrice)
            String total = Currency.format(this.total)
            String html = """\
            <tr>
              <td class="item-number-column">${position}</td>
              <td class="item-description-column">
                <div class="item-title">${name}</div>
              </td>
              <td class="item-amount-column">${quantity}</td>
              <td class="item-unit-column">${unit}</td>
              <td class="item-unit-price-column">${unitPrice}</td>
              <td class="item-total-column">${total}</td>
            </tr>
            <tr>
              <td class="item-number-column"></td>
              <td class="item-description-column" colspan="3">
                <div class="item-description">
                  ${description}
                </div>
              </td>
            </tr>\
            """.stripIndent()
            return html
        }

        private static String createProductItemDescription(String productDescription, String serviceProvider) {
            String description = """\
                <p>${productDescription}</p>
                <p>Service Provider: ${serviceProvider}</p>
                \
                """.stripIndent()
            return description
        }
        private static String createDiscountDescription(int discountedPosition, double quantity, String unit, double discountPercentage) {
            String unitName = unit.toString().toLowerCase()
            unitName = (quantity != 1)?  unitName + "s" : unitName
            return "Discount on ${quantity.toString()} ${unitName} based on item no ${discountedPosition}. ${discountPercentage}% discount applied."
        }

        /**
         * Returns a HTML representation of the position as a table row
         * @return a HTML representation of the position
         * @see #outerHtml
         * @since 1.1.0
         */
        @Override
        String toString() {
            return outerHtml()
        }
    }
}
