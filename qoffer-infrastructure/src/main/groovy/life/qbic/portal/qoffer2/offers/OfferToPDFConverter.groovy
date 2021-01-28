package life.qbic.portal.qoffer2.offers

import groovy.util.logging.Log4j2
import life.qbic.datamodel.dtos.business.AcademicTitle
import life.qbic.datamodel.dtos.business.Affiliation
import life.qbic.datamodel.dtos.business.Customer
import life.qbic.datamodel.dtos.business.Offer
import life.qbic.datamodel.dtos.business.ProductItem
import life.qbic.datamodel.dtos.business.ProjectManager
import life.qbic.portal.portlet.offers.Currency
import life.qbic.portal.portlet.offers.OfferExporter
import org.jsoup.nodes.Document
import org.jsoup.parser.Parser

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.text.DateFormat
import java.text.SimpleDateFormat


/**
 * Handles the conversion of offers to pdf files
 *
 * Implements {@link OfferExporter} and is responsible for converting an offer into PDF Format
 *
 * @since: 1.0.0
 * @author: Jennifer Bödker
 *
 */
@Log4j2
class OfferToPDFConverter implements OfferExporter {

    private final Offer offer

    private final Path tempDir

    private final Document htmlContent

    private final Path createdOffer

    private final Path createdOfferPdf

    private final Path newOfferImage

    private final Path newOfferStyle

    private static final Path OFFER_HTML_TEMPLATE =
            Paths.get(OfferToPDFConverter.class.getClassLoader()
                    .getResource("offer-template/offer.html")
                    .toURI())
    private static final Path OFFER_HEADER_IMAGE =
            Paths.get(OfferToPDFConverter.class.getClassLoader()
                    .getResource("offer-template/offer_header.png")
                    .toURI())
    private static final Path OFFER_STYLESHEET =
            Paths.get(OfferToPDFConverter.class.getClassLoader()
                    .getResource("offer-template/stylesheet.css")
                    .toURI())

    OfferToPDFConverter(Offer offer) {
        this.offer = Objects.requireNonNull(offer, "Offer object must not be a null reference")
        this.tempDir = Files.createTempDirectory("offer")
        this.createdOffer = Paths.get(tempDir.toString(), "offer.html")
        this.newOfferImage = Paths.get(tempDir.toString(), "offer_header.png")
        this.newOfferStyle = Paths.get(tempDir.toString(), "stylesheet.css")
        this.createdOfferPdf = Paths.get(tempDir.toString(), "offer.pdf")
        importTemplate()
        this.htmlContent = Parser.xmlParser().parseInput(new File(this.createdOffer.toUri()).text, "")
        fillTemplateWithOfferContent()
        writeHTMLContentToFile(this.createdOffer, this.htmlContent)
        generatePDF()
    }

    InputStream getOfferAsPdf() {
        return new BufferedInputStream(new FileInputStream(new File(createdOfferPdf.toString())))
    }

    private void importTemplate() {
        Files.copy(OFFER_HTML_TEMPLATE, createdOffer, StandardCopyOption.REPLACE_EXISTING)
        Files.copy(OFFER_HEADER_IMAGE, newOfferImage, StandardCopyOption.REPLACE_EXISTING)
        Files.copy(OFFER_STYLESHEET, newOfferStyle, StandardCopyOption.REPLACE_EXISTING)
    }

    private static void writeHTMLContentToFile(Path fileLocation, Document htmlContent) {
        new File(fileLocation.toUri()).withWriter {
            it.write(htmlContent.toString())
            it.flush()
        }
    }

    private void fillTemplateWithOfferContent() {
        setProjectInformation()
        setCustomerInformation()
        setManagerInformation()
        setSelectedItems()
        setPrices()
        setQuotationDetails()
    }

    private void generatePDF() {
        PdfPrinter pdfPrinter = new PdfPrinter(createdOffer)
        pdfPrinter.print(createdOfferPdf)
    }

    private void setProjectInformation() {
        htmlContent.getElementById("project-title").text(offer.projectTitle)
        htmlContent.getElementById("project-description").text(offer.projectDescription)
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

    void setSelectedItems() {
        // Let's clear the existing item template content first
        htmlContent.getElementById("product-items-1").empty()
        //and remove the footer on the first page
        htmlContent.getElementById("grid-table-footer").remove()
        // Set the start offer position
        def itemPos = 1
        // max number of table items per page
        def maxTableItems = 10
        //
        def tableNum = 1
        def elementId = "product-items"+"-"+tableNum
        // Create the items in html in the overview table
        List<ProductItem> items = offer.items + offer.items + offer.items + offer.items + offer.items + offer.items + offer.items + offer.items

        items.each { item ->

            if (itemPos % maxTableItems == 0) //start (next) table
            {
                elementId = "product-items"+"-"+ ++tableNum
                htmlContent.getElementById("items-container-table").append(ItemPrintout.tableInHTML(elementId))
            }
            htmlContent.getElementById(elementId)
                    .append(ItemPrintout.itemInHTML(itemPos++, item))

        }

        //create the footer only for the last page containing a table
        htmlContent.getElementById(elementId)
                .append(ItemPrintout.tableEnd())
    }

    void setPrices() {
        final totalPrice = Currency.getFormatterWithoutSymbol().format(offer.totalPrice)
        final taxes = Currency.getFormatterWithoutSymbol().format(offer.taxes)
        final netPrice = Currency.getFormatterWithoutSymbol().format(offer.netPrice)
        final netPrice_withSymbol = Currency.getFormatterWithSymbol().format(offer.netPrice)


        htmlContent.getElementById("total-costs-net").text(netPrice_withSymbol)

        htmlContent.getElementById("total-cost-value-net").text(netPrice)
        htmlContent.getElementById("vat-cost-value").text(taxes)
        htmlContent.getElementById("final-cost-value").text(totalPrice)
    }

    void setQuotationDetails() {
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG)

        htmlContent.getElementById("offer-identifier").text(offer.identifier.toString())
        htmlContent.getElementById("offer-expiry-date").text(offer.expirationDate.toLocalDate().toString())
        htmlContent.getElementById("offer-date").text(dateFormat.format(offer.modificationDate))
    }

    /**
     * Small helper class to handle the HTML to PDF conversion.
     */
    class PdfPrinter {

        final Path sourceFile

        final String chromeAlias

        PdfPrinter(Path sourceFile) {
            this.sourceFile = sourceFile
            this.chromeAlias = "chromium"
        }

        PdfPrinter(Path sourceFile, String alias) {
            this.sourceFile = sourceFile
            this.chromeAlias = alias
        }

        void print(Path outputFile) {
            final Path output = outputFile
            ProcessBuilder builder = new ProcessBuilder()
            builder.command(chromeAlias,
                    "--headless",
                    "--disable-gpu",
                    "--aggressive-cache-discard",
                    "--print-to-pdf-no-header",
                    "--print-to-pdf=${output.toString()}",
                    "${sourceFile}")
            builder.directory(new File(sourceFile.getParent().toString()))
            builder.redirectErrorStream(true)
            Process process = builder.start()
            process.waitFor()
            process.getInputStream().eachLine {log.info(it)}
            if (! new File(output.toString()).exists()) {
                throw new RuntimeException("Offer PDF has not been generated.")
            }
        }
    }

    private static class ItemPrintout {

        static String itemInHTML(int offerPosition, ProductItem item) {
            return """<div class="row_ product-item">
                        <div class="col-1">${offerPosition}</div>
                        <div class="col-4 ">${item.product.productName}</div>
                        <div class="col-1 price-value">${item.quantity}</div>
                        <div class="col-2 text-center">${item.product.unit}</div>
                        <div class="col-2 price-value">${Currency.getFormatterWithoutSymbol().format(item.product.unitPrice)}</div>
                        <div class="col-2 price-value">${Currency.getFormatterWithoutSymbol().format(item.quantity * item.product.unitPrice)}</div>
                    </div>
                    <div class="row_ product-item">
                        <div class="col-1"></div>
                        <div class="col-4 item-description">${item.product.description}</div>
                        <div class="col-7"></div>
                    </div>"""
        }

        static String tableInHTML(String elementId){
            //1. add pagebreak
            //2. create empty table for elementId
            return """<div class="pagebreak"> </div>
                             <div class="grid-container-table">
                                 <div id="grid-table-header">
                                     <div class="row_ table-header" id="grid-table-header">
                                         <div class="col-1">Pos.</div>
                                         <div class="col-4">Service Description</div>
                                         <div class="col-1 price-value">Amount</div>
                                         <div class="col-2 text-center">Unit</div>
                                         <div class="col-2 price-value">Price/Unit (€)</div>
                                         <div class="col-2 price-value">Total (€)</div>
                                    </div>
                                 </div>
                                 <div class="product-items" id="${elementId}">
                                 </div>
                             </div>"""
        }

        static String tableEnd(){
            return """<div id="grid-table-footer">
                                     <div class="row_ row-lines-upper total-costs" id = "offer-net">
                                         <div class="col-6"></div>
                                         <div class="col-4 cost-summary-field">Estimated total (net):</div>
                                         <div class="col-2 price-value" id="total-cost-value-net">12,500.00</div>
                                         </div>
                                     <div class="row_ row-lines-upper total-costs" id = "offer-vat">
                                         <div class="col-10 cost-summary-field">VAT (19%):</div>
                                         <div class="col-2 price-value" id="vat-cost-value">0.00</div>
                                     </div>
                                     <div class="row_ row-lines-upper total-costs" id ="offer-total">
                                         <div class="col-10 cost-summary-field">Estimtated total (VAT included)</div>
                                         <div class="col-2 price-value" id="final-cost-value">12,500.00</div>
                                     </div>
                                 </div>"""
        }

    }
}