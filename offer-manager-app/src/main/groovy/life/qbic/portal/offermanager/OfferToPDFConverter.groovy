package life.qbic.portal.offermanager

import groovy.util.logging.Log4j2
import life.qbic.business.offers.Currency
import life.qbic.business.offers.OfferExporter
import life.qbic.datamodel.dtos.business.*
import life.qbic.datamodel.dtos.business.services.*
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.parser.Parser
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.text.DateFormat
import java.text.DecimalFormat
import java.util.concurrent.TimeUnit

/**
 * Handles the conversion of offers to pdf files
 *
 * Implements {@link OfferExporter} and is responsible for converting an offer into PDF Format
 *
 * @since 1.0.0
 * @author Jennifer Bödker
 *
 */
@Log4j2
class OfferToPDFConverter implements OfferExporter {

    /**
     * The environment variable that is resolved when
     * the chromium browser needs to be executed
     * for the PDF conversion.
     *
     * This variable's value must contain the chromium browser
     * alias that can be executed from the system's command
     * line.
     */
    static final CHROMIUM_EXECUTABLE = "CHROMIUM_EXECUTABLE"

    /**
     * Variable used to count the number of productItems in a productTable
     */
    private static int tableItemsCount

    /**
     * Variable used to count the number of generated productTables in the Offer PDF
     */
    private static int tableCount

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

    /**
     * Holds the current VAT rate
     */
    private static final double VAT = 0.19

    /**
     * Holds the Country for which the current VAT rate is applicable
     */
    private static final String countryWithVAT = "Germany"

    /**
     * AffiliationCategory for which no tax cost is applied
     */
    private static final AffiliationCategory noVatCategory = AffiliationCategory.INTERNAL

    /**
     * Possible product groups
     *
     * This enum describes the product groups into which the products of an offer are listed.
     * It also defines the acronyms used to abbreviate the product groups in the offer listings.
     */
    enum ProductGroups {
        DATA_GENERATION("Data generation", "DG"),
        DATA_ANALYSIS("Data analysis", "DA"),
        DATA_MANAGEMENT("Project management and data storage", "PM & DS")

        private String name
        private String acronym

        ProductGroups(String name, String acronym) {
            this.name = name
            this.acronym = acronym
        }

        String getName() {
            return this.name
        }

        String getAcronym() {
            return this.acronym
        }
    }
    /**
     * Product group mapping
     *
     * This map represents the grouping of the different product categories in the offer pdf
     *
     */
    private final Map<ProductGroups, List> productGroupClasses = [:]

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
        setProductGroupMapping()
        setSelectedItems()
        setTotalPrices()
        setTaxationStatement()
        setTaxationRatioInSummary()
        setQuotationDetails()
    }

    private void generatePDF() {
        PdfPrinter pdfPrinter = new PdfPrinter(createdOffer)
        pdfPrinter.print(createdOfferPdf)
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

    void setProductGroupMapping() {

        productGroupClasses[ProductGroups.DATA_GENERATION] = [Sequencing]
        productGroupClasses[ProductGroups.DATA_ANALYSIS] = [PrimaryAnalysis, SecondaryAnalysis, ProteomicAnalysis, MetabolomicAnalysis]
        productGroupClasses[ProductGroups.DATA_MANAGEMENT] = [ProjectManagement, DataStorage]
    }

    void setSelectedItems() {
        // Let's clear the existing item template content first
        htmlContent.getElementById("product-items-1").empty()
        htmlContent.getElementById("product-items-2").empty()
        // To also remove the styling of the div elements they have to be removed
        htmlContent.getElementById("grid-table-footer").empty()
        htmlContent.getElementById("template-page-break").remove()

        List<ProductItem> productItems = offer.items

        //Initialize Number of table
        tableCount = 1


        pageItemsCount = 1
        //The maximum number of items per page
        int maxTableItems = 13

        //Group ProductItems into Data Generation Data Analysis and Data & Project Management Categories
        Map productItemsMap = groupItems(productItems)

        //Generate Product Table for each Category
        generateProductTable(productItemsMap, maxTableItems)
        //Append total cost footer
        String elementId = "product-items" + "-" + tableCount
        if (tableItemsCount > maxTableItems) {
            //If currentTable is filled with Items generate new one and add total pricing there
            ++tableCount
            htmlContent.getElementById(elementId).append(ItemPrintout.pageBreak())
            elementId = "product-items" + "-" + tableCount
            htmlContent.getElementById("item-table-grid").append(ItemPrintout.createNewTable(elementId))
            htmlContent.getElementById(elementId).append(ItemPrintout.tableHeader())
        }
            //Add total pricing information to grid-table-footer div in template
            Element element = htmlContent.getElementById("grid-table-footer").append(ItemPrintout.tableFooter(offer.overheadRatio, offer.getSelectedCustomerAffiliation()))
            //Move template footer div after item-table-grid
            htmlContent.getElementById("item-table-grid").after(element)
    }

    void setTaxationStatement() {
        if (!offer.getSelectedCustomerAffiliation().country.equals(countryWithVAT)) {
            htmlContent.getElementById("vat-cost-applicable").text("Taxation is not applied to offers outside of ${countryWithVAT}")
        }

    }

    void setTaxationRatioInSummary() {
        DecimalFormat decimalFormat = new DecimalFormat("#%")
        String country = offer.getSelectedCustomerAffiliation().getCountry()
        AffiliationCategory affiliationCategory = offer.getSelectedCustomerAffiliation().getCategory()
        double taxRatio = determineTaxCost(country, affiliationCategory)
        String taxPercentage = decimalFormat.format(taxRatio)
        htmlContent.getElementById("total-taxes-ratio").text("VAT (${taxPercentage})")
    }

    // Apply VAT only if the offer originated from Germany and it's affilation category is non-internal
    static double determineTaxCost(String country, AffiliationCategory category) {
        if (country.equals(countryWithVAT) && !category.equals(noVatCategory)) {
            return VAT
        }
        return 0
    }

    void setSubTotalPrices(ProductGroups productGroup, List<ProductItem> productItems) {
        double netSum = calculateNetSum(productItems)
        final netPrice = Currency.getFormatterWithoutSymbol().format(netSum)
        htmlContent.getElementById("${productGroup}-net-costs-value").text(netPrice)
    }

    void setTotalPrices() {

        // Get prices with currency symbol for first page summary
        final totalPriceWithSymbol = Currency.getFormatterWithSymbol().format(offer.totalPrice)
        final taxesWithSymbol = Currency.getFormatterWithSymbol().format(offer.taxes)
        final netPriceWithSymbol = Currency.getFormatterWithSymbol().format(offer.netPrice)
        final overheadPriceWithSymbol = Currency.getFormatterWithSymbol().format(offer.overheads)

        DecimalFormat decimalFormat = new DecimalFormat("#%")
        String overheadPercentage = decimalFormat.format(offer.overheadRatio)

        // First page summary
        htmlContent.getElementById("ratio-costs-overhead").text("Overheads (${overheadPercentage})")

        htmlContent.getElementById("total-costs-net").text(netPriceWithSymbol)
        htmlContent.getElementById("total-costs-overhead").text(overheadPriceWithSymbol)
        htmlContent.getElementById("total-taxes").text(taxesWithSymbol)
        htmlContent.getElementById("total-costs-sum").text(totalPriceWithSymbol)

        // Get prices without currency symbol for detailed price listing
        final overheadPrice = Currency.getFormatterWithoutSymbol().format(offer.overheads)
        final netPrice = Currency.getFormatterWithoutSymbol().format(offer.netPrice)
        final taxesPrice = Currency.getFormatterWithoutSymbol().format(offer.taxes)
        final totalPrice = Currency.getFormatterWithoutSymbol().format(offer.totalPrice)

        // Detailed listing summary
        htmlContent.getElementById("overhead-cost-value").text(overheadPrice)
        htmlContent.getElementById("total-cost-value-net").text(netPrice)
        htmlContent.getElementById("vat-cost-value").text(taxesPrice)
        htmlContent.getElementById("final-cost-value").text(totalPrice)
    }

    void setQuotationDetails() {
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG, Locale.US)

        htmlContent.getElementById("offer-identifier").text(offer.identifier.toString())
        htmlContent.getElementById("offer-expiry-date").text(offer.expirationDate.toLocalDate().toString())
        htmlContent.getElementById("offer-date").text(dateFormat.format(offer.modificationDate))
    }

    double calculateNetSum(List<ProductItem> productItems) {
        double netSum = 0
        productItems.each {
            netSum += it.quantity * it.product.unitPrice
        }
        return netSum
    }

    Map groupItems(List<ProductItem> productItems) {

        Map<ProductGroups, List<ProductItem>> productItemsMap = [:]

        List<ProductItem> dataGenerationItems = []
        List<ProductItem> dataAnalysisItems = []
        //Project Management and Data Storage are grouped in the same category in the final Offer PDF
        List<ProductItem> dataManagementItems = []

        // Sort ProductItems into "DataGeneration", "Data Analysis" and "Project & Data Management"
        productItems.each {
            if (it.product.class in productGroupClasses[ProductGroups.DATA_GENERATION]) {
                dataGenerationItems.add(it)
            }
            if (it.product.class in productGroupClasses[ProductGroups.DATA_ANALYSIS]) {
                dataAnalysisItems.add(it)
            }
            if (it.product.class in productGroupClasses[ProductGroups.DATA_MANAGEMENT]) {
                dataManagementItems.add(it)
            }
        }

            //Map Lists to the "DataGeneration", "DataAnalysis" and "Project and Data Management"
            productItemsMap[ProductGroups.DATA_GENERATION] = dataGenerationItems
            productItemsMap[ProductGroups.DATA_ANALYSIS] = dataAnalysisItems
            productItemsMap[ProductGroups.DATA_MANAGEMENT] = dataManagementItems

            return productItemsMap
        }

    void generateProductTable(Map productItemsMap, int maxTableItems) {
        // Create the items in html in the overview table
        int itemNumber = 0
        productItemsMap.each { ProductGroups productGroup, List<ProductItem> items ->
            //Check if there are ProductItems stored in map entry
            if(items){
                //Each Title will take spacing in the generated table
                tableItemsCount++
                def elementId = "product-items" + "-" + tableCount
                if (tableItemsCount > maxTableItems) {
                    //Start new table on next page
                    ++tableCount
                    htmlContent.getElementById(elementId).append(ItemPrintout.pageBreak())
                    elementId = "product-items" + "-" + tableCount
                    htmlContent.getElementById("item-table-grid").append(ItemPrintout.createNewTable(elementId))
                    tableItemsCount = 1
                }
                //Append Table Title and Header
                htmlContent.getElementById(elementId).append(ItemPrintout.tableTitle(productGroup))
                htmlContent.getElementById(elementId).append(ItemPrintout.tableHeader())
                items.each{ProductItem item ->
                    itemNumber++
                    if (tableItemsCount > maxTableItems) {
                        ++tableCount
                        htmlContent.getElementById(elementId).append(ItemPrintout.pageBreak())
                        elementId = "product-items" + "-" + tableCount
                        htmlContent.getElementById("item-table-grid").append(ItemPrintout.createNewTable(elementId))
                        htmlContent.getElementById(elementId).append(ItemPrintout.tableHeader())
                        tableItemsCount = 1
                    }
                    //add product to current table
                    htmlContent.getElementById(elementId).append(ItemPrintout.itemInHTML(itemNumber, item))
                    tableItemsCount++
                }
                //add subtotal footer to table
                htmlContent.getElementById(elementId).append(ItemPrintout.subTableFooter(productGroup))

                // Update footer Prices
                setSubTotalPrices(productGroup, items)
            }
        }
    }
    /**
     * Small helper class to handle the HTML to PDF conversion.
     */
    class PdfPrinter {

        final Path sourceFile

        final String chromeAlias

        PdfPrinter(Path sourceFile) {
            this.sourceFile = sourceFile
            this.chromeAlias = Objects.requireNonNull(System.getenv(CHROMIUM_EXECUTABLE),
                    "CHROMIUM_EXECUTABLE environment was not set.")
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
            process.waitFor(10, TimeUnit.SECONDS)
            process.getInputStream().eachLine {log.info(it)}
            if (! new File(output.toString()).exists()) {
                throw new RuntimeException("Offer PDF has not been generated.")
            }
        }
    }

    private static class ItemPrintout {

        static String itemInHTML(int offerPosition, ProductItem item) {
            String totalCost = Currency.getFormatterWithoutSymbol().format(item.quantity * item.product.unitPrice)
            return """<div class="row product-item">
                        <div class="col-1">${offerPosition}</div>
                        <div class="col-4 ">${item.product.productName}</div>
                        <div class="col-1 price-value">${item.quantity}</div>
                        <div class="col-2 text-center">${item.product.unit}</div>
                        <div class="col-2 price-value">${Currency.getFormatterWithoutSymbol().format(item.product.unitPrice)}</div>
                        <div class="col-2 price-value">${totalCost}</div>
                    </div>
                    <div class="row product-item">
                        <div class="col-1"></div>
                        <div class="col-4 item-description">${item.product.description}</div>
                        <div class="col-7"></div>
                    </div>
                    """

        }

        static String pageBreak() {

            return """<div class="pagebreak"></div>
                   """
        }

        static String tableHeader() {

            return """<div class="row table-header" id="grid-table-header-${tableCount}">
                                         <div class="col-1">&#8470;</div>
                                         <div class="col-4">Service Description</div>
                                         <div class="col-1 price-value">Amount</div>
                                         <div class="col-2 text-center">Unit</div>
                                         <div class="col-2 price-value">Price/Unit (€)</div>
                                         <div class="col-2 price-value">Total (€)</div>
                                    </div>
                                    """
        }

        static String createNewTable(String elementId) {
            """<div class="product-items" id="${elementId}"></div>
            """
        }

        static String tableTitle(ProductGroups productGroup){

            String tableTitle= productGroup.getName()
            String tableTitleAcronym = productGroup.getAcronym()

            return """<div class = "small-spacer"</div>
                    <h3>${tableTitle} (${tableTitleAcronym})</h3>
                   """
        }

        static String subTableFooter(ProductGroups productGroup){
            //Each footer takes up spacing in the current table
            tableItemsCount++
            String footerTitle = productGroup.getAcronym()
            return """<div id="grid-sub-total-footer-${tableCount}" class="grid-sub-total-footer">
                                      <div class="col-6"></div> 
                                     <div class="row sub-total-costs" id = "${productGroup}-sub-net">
                                         <div class="col-10 cost-summary-field">Net (${footerTitle}):</div>
                                         <div class="col-2 price-value" id="${productGroup}-net-costs-value">12345</div>
                                     </div>
                                 </div>
                                 """
        }

        static String tableFooter(double overheadRatio, Affiliation affiliation){
            DecimalFormat decimalFormat = new DecimalFormat("#%")
            String overheadPercentage = decimalFormat.format(overheadRatio)
            double taxRatio = determineTaxCost(affiliation.country, affiliation.getCategory())
            String taxPercentage = decimalFormat.format(taxRatio)

            return """<div class="col-10 cost-summary-field">Overheads (${overheadPercentage})</div>
                                     <div class="row sub-total-costs" id="DATA_GENERATION-sub-overhead">
                                        <div class="col-10 cost-summary-field">
                                            Data Generation:
                                        </div>
                                        <div class="col-2 price-value" id="DATA_GENERATION-overhead-costs-value">
                                            0.00
                                        </div>
                                     </div>
                                     <div class="row sub-total-costs" id="DATA_ANALYSIS-sub-overhead">
                                        <div class="col-10 cost-summary-field">
                                            Data Analysis:
                                        </div>
                                        <div class="col-2 price-value" id="DATA_ANALYSIS-overhead-costs-value">
                                            0.00
                                        </div>
                                     </div>
                                     <div class="row total-costs single-overscore" id = "offer-overhead">
                                        <div class="col-10 cost-summary-field">
                                            Overhead total:
                                        </div>
                                        <div class="col-2 price-value" id="overhead-cost-value">
                                            0.00
                                        </div>
                                     </div>
                                     <div class="row total-costs" id = "offer-net">
                                        <div class="col-6"></div>
                                        <div class="col-4 cost-summary-field">
                                            Net:
                                        </div>
                                        <div class="col-2 price-value" id="total-cost-value-net">
                                            0.00
                                        </div>
                                     </div>
                                     <div class="row total-costs" id = "offer-vat">
                                        <div class="col-10 cost-summary-field">
                                            VAT (${taxPercentage}):
                                        </div>
                                        <div class="col-2 price-value" id="vat-cost-value">
                                            0.00
                                        </div>
                                     </div>
                                     <div class="row total-costs single-overscore" id ="offer-total">
                                        <div class="col-10 cost-summary-field">
                                            Total:
                                        </div>
                                        <div class="col-2 price-value highlight" id="final-cost-value">
                                            0.00
                                        </div>
                                     </div>
                                 """
        }

        }
}
