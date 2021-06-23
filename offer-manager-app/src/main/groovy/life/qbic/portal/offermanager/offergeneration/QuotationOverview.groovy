package life.qbic.portal.offermanager.offergeneration

import life.qbic.business.offers.Converter
import life.qbic.business.offers.Currency
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
    private Offer offer
    private life.qbic.business.offers.Offer offerEntity

    QuotationOverview(Document htmlContent, Offer offer){
        this.offer = Objects.requireNonNull(offer, "Offer object must not be a null reference")
        this.htmlContent = Objects.requireNonNull(htmlContent, "htmlContent object must not be a null reference")
        this.offerEntity = Converter.convertDTOToOffer(offer)
        fillTemplateWithQuotationOverviewContent()
    }

    void fillTemplateWithQuotationOverviewContent() {
        setProjectInformation()
        setCustomerInformation()
        setManagerInformation()
        setPriceOverview()
        setTaxationStatement()
        setQuotationDetails()
    }

    private void setProjectInformation() {
        htmlContent.getElementById("project-title").text(offer.projectTitle)
        htmlContent.getElementById("project-description").text(offer.projectDescription)
        if (offer.experimentalDesign.isPresent()) htmlContent.getElementById("experimental-design").text(offer.experimentalDesign.get())
    }

    private void setCustomerInformation() {
        final Customer customer = offer.customer
        final Affiliation affiliation = offer.selectedCustomerAffiliation
        String customerTitle = customer.title == AcademicTitle.NONE ? "" : customer.title
        htmlContent.getElementById("customer-name").text(String.format(
                "%s %s %s",
                customerTitle,
                customer.firstName,
                customer.lastName))
        htmlContent.getElementById("customer-organisation").text(affiliation.organisation)
        htmlContent.getElementById("customer-street").text(affiliation.street)
        htmlContent.getElementById("customer-postal-code").text(affiliation.postalCode)
        htmlContent.getElementById("customer-city").text(affiliation.city)
        htmlContent.getElementById("customer-country").text(affiliation.country)

    }

    private void setManagerInformation() {
        final ProjectManager pm = offer.projectManager
        final Affiliation affiliation = pm.affiliations.get(0)
        String pmTitle = pm.title == AcademicTitle.NONE ? "" : pm.title
        htmlContent.getElementById("project-manager-name").text(String.format(
                "%s %s %s",
                pmTitle,
                pm.firstName,
                pm.lastName))
        htmlContent.getElementById("project-manager-street").text(affiliation.street)
        htmlContent.getElementById("project-manager-city").text("${affiliation.postalCode} ${affiliation.city}")
        htmlContent.getElementById("project-manager-email").text(pm.emailAddress)
    }

    private void setPriceOverview() {
        // Get prices with currency symbol for first page summary
        final totalPriceWithSymbol = Currency.getFormatterWithSymbol().format(offer.totalPrice)
        final taxesWithSymbol = Currency.getFormatterWithSymbol().format(offer.taxes)
        final netPriceWithSymbol = Currency.getFormatterWithSymbol().format(offer.netPrice)
        final overheadPriceWithSymbol = Currency.getFormatterWithSymbol().format(offer.overheads)

        DecimalFormat decimalFormat = new DecimalFormat("#%")
        String overheadPercentage = decimalFormat.format(offer.overheadRatio)

        double taxRatio = offerEntity.determineTaxCost()
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

        htmlContent.getElementById("offer-identifier").text(offer.identifier.toString())
        htmlContent.getElementById("offer-expiry-date").text(offer.expirationDate.toLocalDate().toString())
        htmlContent.getElementById("offer-date").text(dateFormat.format(offer.modificationDate))
    }


    private void setTaxationStatement() {
        if (!offerEntity.isVatCountry()) {
            htmlContent.getElementById("vat-cost-applicable").text("Taxation is not applied to offers outside of ${offerEntity.getCountryWithVat()}.")
        }
    }

}