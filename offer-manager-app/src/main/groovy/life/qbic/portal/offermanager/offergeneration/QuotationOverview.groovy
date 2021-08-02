package life.qbic.portal.offermanager.offergeneration

import life.qbic.business.offers.Converter
import life.qbic.business.offers.Currency
import life.qbic.business.offers.OfferContent
import life.qbic.datamodel.dtos.business.AcademicTitle
import life.qbic.datamodel.dtos.business.Affiliation
import life.qbic.datamodel.dtos.business.Customer
import life.qbic.datamodel.dtos.business.Offer
import life.qbic.datamodel.dtos.business.ProjectManager
import org.jsoup.nodes.Document

import java.text.DateFormat
import java.text.DecimalFormat

/**
 * <h1>Gives on overview of the quotation</h1>
 *
 * <p>The quotation overview summarizes the most specific information such as project investigator, project manager, project title and further details.
 * This class collects the HTML element ids that reference the information of the first page. Furthermore, this class can be used to generate HTML snippets
 * that are placed into the final offer (within the first section)</p>
 *
 * @since 1.1.0
 *
*/
class QuotationOverview {

    private final Document htmlContent
    private OfferContent offer
    private life.qbic.business.offers.Offer offerEntity

    QuotationOverview(Document htmlContent, OfferContent offer){
        this.offer = Objects.requireNonNull(offer, "Offer object must not be a null reference")
        this.htmlContent = Objects.requireNonNull(htmlContent, "htmlContent object must not be a null reference")
        fillTemplateWithQuotationOverviewContent()
    }

    void fillTemplateWithQuotationOverviewContent() {
        setProjectInformation()
        setCustomerInformation()
        setManagerInformation()
        setPriceOverview()
        setTaxationStatement()
        setTaxationRatioInSummary()
        setQuotationDetails()
    }

    private void setProjectInformation() {
        htmlContent.getElementById("project-title").text(offer.projectTitle)
        htmlContent.getElementById("project-description").text(offer.projectObjective)
        if (offer.getExperimentalDesign()) htmlContent.getElementById("experimental-design").text(offer.getExperimentalDesign())
    }

    private void setCustomerInformation() {
        String customerTitle = offer.customerTitle
        htmlContent.getElementById("customer-name").text(String.format(
                "%s %s %s",
                customerTitle,
                offer.getCustomerFirstName(),
                offer.getCustomerLastName()))
        htmlContent.getElementById("customer-organisation").text(offer.getCustomerOrganisation())
        htmlContent.getElementById("customer-street").text(offer.getCustomerStreet())
        htmlContent.getElementById("customer-postal-code").text(offer.getCustomerPostalCode())
        htmlContent.getElementById("customer-city").text(offer.getCustomerCity())
        htmlContent.getElementById("customer-country").text(offer.getCustomerCountry())

    }

    private void setManagerInformation() {
        htmlContent.getElementById("project-manager-name").text(String.format(
                "%s %s %s",
                offer.getProjectManagerTitle(),
                offer.getProjectManagerFirstName(),
                offer.getProjectManagerLastName()))
        htmlContent.getElementById("project-manager-street").text(offer.getProjectManagerStreet())
        htmlContent.getElementById("project-manager-city").text("${offer.getProjectManagerPostalCode()} ${offer.getProjectManagerCity()}")
        htmlContent.getElementById("project-manager-email").text(offer.getProjectManagerEmail())
    }

    private void setPriceOverview() {
        // Get prices with currency symbol for first page summary
        final totalPriceWithSymbol = Currency.getFormatterWithSymbol().format(offer.getTotalCost())
        final taxesWithSymbol = Currency.getFormatterWithSymbol().format(offer.getTotalVat())
        final netPriceWithSymbol = Currency.getFormatterWithSymbol().format(offer.getNetCost())
        final overheadPriceWithSymbol = Currency.getFormatterWithSymbol().format(offer.getOverheadTotal())

        DecimalFormat decimalFormat = new DecimalFormat("#%")
        String overheadPercentage = decimalFormat.format(offer.getOverheadRatio())

        double taxRatio = offer.getVatRatio()
        String taxPercentage = decimalFormat.format(taxRatio)

        htmlContent.getElementById("total-taxes-ratio").text("VAT (${taxPercentage})")

        // First page summary
        htmlContent.getElementById("ratio-costs-overhead").text("Overheads (${overheadPercentage})")
        htmlContent.getElementById("total-costs-net").text(netPriceWithSymbol)
        htmlContent.getElementById("total-costs-overhead").text(overheadPriceWithSymbol)
        htmlContent.getElementById("total-taxes").text(taxesWithSymbol)
        htmlContent.getElementById("total-costs-sum").text(totalPriceWithSymbol)
    }

    private void setQuotationDetails() {
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG, Locale.US)

        htmlContent.getElementById("offer-identifier").text(offer.getOfferIdentifier().toString())
        htmlContent.getElementById("offer-expiry-date").text(dateFormat.format(offer.getExpirationDate()))
        htmlContent.getElementById("offer-date").text(dateFormat.format(offer.getCreationDate()))
    }

    private void setTaxationStatement() {
        if (!(offer.getCustomerCountry().toLowerCase() == "germany")) {
            htmlContent.getElementById("vat-cost-applicable").text("Taxation is not applied to offers outside of ${"Germany"}.")
        }
    }

    private void setTaxationRatioInSummary() {
        DecimalFormat decimalFormat = new DecimalFormat("#%")

        double taxRatio = offer.getVatRatio()
        String taxPercentage = decimalFormat.format(taxRatio)

        htmlContent.getElementById("total-taxes-ratio").text("VAT (${taxPercentage})")
    }

}