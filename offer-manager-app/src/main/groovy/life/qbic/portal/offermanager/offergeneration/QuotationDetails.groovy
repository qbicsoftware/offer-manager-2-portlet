package life.qbic.portal.offermanager.offergeneration

import life.qbic.business.offers.Converter
import life.qbic.business.offers.Currency
import life.qbic.datamodel.dtos.business.Offer
import life.qbic.datamodel.dtos.business.ProductItem
import life.qbic.datamodel.dtos.business.services.*
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

import java.text.DecimalFormat

/**
 * <h1>Describes the quotation details of an offer</h1>
 *
 * <p>The quotation details section consists of the product items of an offer. It gives an overview of the purchased items and breaks down the total costs.
 * This class solely generates the HTML source code for this section.</p>
 *
 * <p>General idea: group the product items, generate the td elements.
 *
 * Furthermore: This class stores fixed ids (such as QuotationOverview) to quickly access e.g. total costs,....
 * Also it returns the HTML elements for tables, product group headings,..</p>
 *
 * @since 1.1.0*
 */
class QuotationDetails {

    /**
     * Variable used to count the number of Items added to a page
     */
    private int pageItemsCount = 3

    /**
     * Variable used to count the number of generated productTables in the Offer PDF
     */
    private static int tableCount = 0

    /**
     * Counts the number of items and reflects the position item number on the final offer
     */
    private int itemNumber = 0

    /**
     * The maximum number of items per page
     */
    private final int maxPageItems = 25

    private final Document htmlContent
    private final life.qbic.business.offers.Offer offer

    /**
     * Product group mapping
     *
     * This map represents the grouping of the different product categories in the offer pdf
     *
     */
    private final Map<ProductGroups, List> productGroupClasses = setProductGroupMapping()

    private List<ProductItem> dataGenerationItems

    private List<ProductItem> dataAnalysisItems

    private List<ProductItem> dataManagementItems

    QuotationDetails(Document htmlContent, Offer offer) {
        this.htmlContent = Objects.requireNonNull(htmlContent, "htmlContent object must not be a null reference")
        this.offer = Converter.convertDTOToOffer(offer)

        groupProductItems(offer.items)
    }

    void fillTemplateWithQuotationDetailsContent() {
        addTotalPrices()
        setTaxationRatioInSummary()
        setSelectedItems()
    }

    private void setSelectedItems() {
        //We need to remove the page break between the tables in the template
        htmlContent.getElementById("template-page-break").remove()

        //Generate Product Table for each Category
        generateProductTable(dataGenerationItems, ProductGroups.DATA_GENERATION)
        generateProductTable(dataAnalysisItems, ProductGroups.DATA_ANALYSIS)
        generateProductTable(dataManagementItems, ProductGroups.PROJECT_AND_DATA_MANAGEMENT)

        //Append total cost footer
        generateTotalCostFooter()
    }

    private void generateTotalCostFooter(){

        if (isOverflowingPage()) {
            //If currentTable is filled with Items generate new one and add total pricing there
            htmlContent.getElementById("item-table-grid").append(ItemPrintout.pageBreak())
            htmlContent.getElementById("item-table-grid").append(ItemPrintout.createNewTable("item-table-grid"))
            htmlContent.getElementById("item-table-grid").append(ItemPrintout.tableHeader())
        }
        //Add total pricing information to grid-table-footer div in template
        Element element = htmlContent.getElementById("grid-table-footer")
        //Move template footer div after item-table-grid
        htmlContent.getElementById("item-table-grid").after(element)
    }

    private void generateProductTable(List<ProductItem> items, ProductGroups productGroup) {
        // Create the items in html in the overview table
        //Check if there are ProductItems stored in list
        if (!items) {
            htmlContent.getElementById("${productGroup.toString()}-table").remove()
            return
        }
        //Clear Template content
        String elementId = generateElementID(0, productGroup)
        htmlContent.getElementById(elementId).empty()

        if (isOverflowingPage()) {
            generateHTMLTableOnNextPage(elementId)
            resetPageItemsCount()
        }
        //Append Table Title and Header
        htmlContent.getElementById(elementId).append(ItemPrintout.tableHeader())

        items.each { ProductItem item ->
            generateItemContent(item, elementId)
        }
        //account for spaces of added table elements, footer, totals,...
        pageItemsCount += 4
        htmlContent.getElementById(elementId).append(ItemPrintout.subTableFooter(productGroup))

        // Update footer Prices
        addSubTotalPrices(productGroup, items)
    }

    private void generateItemContent(ProductItem item, String elementId){
        itemNumber++
        if (isOverflowingPage()) {
            generateHTMLTableOnNextPage(elementId)
            //repeat table header for next page
            htmlContent.getElementById(elementId).append(ItemPrintout.tableHeader())
            resetPageItemsCount()
        }

        //add product to current table
        htmlContent.getElementById(elementId).append(ItemPrintout.itemInHTML(itemNumber, item))
        pageItemsCount += determineItemSpace(item)
    }

    private static String generateElementID(int tableCount, ProductGroups productGroups){
        return productGroups.toString() + "-" + "product-items" + "-" + ++tableCount}

    private void resetPageItemsCount(){ pageItemsCount = 1}

    private void generateHTMLTableOnNextPage(String elementId){
        htmlContent.getElementById(elementId).append(ItemPrintout.pageBreak())
        elementId = "product-items" + "-" + ++tableCount
        htmlContent.getElementById("item-table-grid").append(ItemPrintout.createNewTable(elementId))
    }

    private boolean isOverflowingPage() {
        return pageItemsCount > maxPageItems
    }

    private void addSubTotalPrices(ProductGroups productGroup, List<ProductItem> productItems) {

        double netSum = calculateNetSum(productItems)
        final netPrice = Currency.getFormatterWithoutSymbol().format(netSum)
        htmlContent.getElementById("${productGroup}-net-costs-value").text(netPrice)
    }

    private static double calculateNetSum(List<ProductItem> productItems) {
        double netSum = 0
        productItems.each {
            netSum += it.quantity * it.product.unitPrice
        }
        return netSum
    }

    private static int determineItemSpace(ProductItem item) {

        ArrayList<Integer> calculatedSpaces = []

        Product product = item.product
        String productTotalCost = item.quantity * item.product.unitPrice

        //Determine amount of spacing necessary from highest itemSpace value of all columns
        calculatedSpaces.add(calculateItemSpace(product.productName, ProductPropertySpacing.PRODUCT_NAME))
        calculatedSpaces.add(calculateItemSpace(product.description, ProductPropertySpacing.PRODUCT_DESCRIPTION))
        calculatedSpaces.add(calculateItemSpace(item.quantity as String, ProductPropertySpacing.PRODUCT_AMOUNT))
        calculatedSpaces.add(calculateItemSpace(product.unit as String, ProductPropertySpacing.PRODUCT_UNIT))
        calculatedSpaces.add(calculateItemSpace(product.unitPrice as String, ProductPropertySpacing.PRODUCT_UNIT_PRICE))
        calculatedSpaces.add(calculateItemSpace(productTotalCost, ProductPropertySpacing.PRODUCT_TOTAL))
        return calculatedSpaces.max()
    }

    private static int calculateItemSpace(String productProperty, ProductPropertySpacing productPropertySpacing) {
        return Math.ceil(productProperty.length() / productPropertySpacing.getCharsLineLimit())
    }

    private void addTotalPrices() {

        DecimalFormat decimalFormat = new DecimalFormat("#%")
        String overheadPercentage = decimalFormat.format(offer.overheadRatio)

        // Get prices without currency symbol for detailed price listing
        final overheadPrice = Currency.getFormatterWithoutSymbol().format(offer.getOverheadSum())
        final netPrice = Currency.getFormatterWithoutSymbol().format(offer.getTotalNetPrice())
        final taxesPrice = Currency.getFormatterWithoutSymbol().format(offer.getTaxCosts())
        final totalPrice = Currency.getFormatterWithoutSymbol().format(offer.getTotalCosts())

        final overheadDataGenerationPrice = Currency.getFormatterWithoutSymbol().format(calculateOverheadSum(dataGenerationItems))
        final overheadDataAnalysisPrice = Currency.getFormatterWithoutSymbol().format(calculateOverheadSum(dataAnalysisItems))
        final overheadDataManagementPrice = Currency.getFormatterWithoutSymbol().format(calculateOverheadSum(dataManagementItems))

        double taxRatio = offer.determineTaxCost()
        String taxPercentage = decimalFormat.format(taxRatio)

        // Set overhead cost values
        htmlContent.getElementById("overhead-percentage-value").text("Overheads (${overheadPercentage})")
        htmlContent.getElementById("DATA_GENERATION-overhead-costs-value").text(overheadDataGenerationPrice)
        htmlContent.getElementById("DATA_ANALYSIS-overhead-costs-value").text(overheadDataAnalysisPrice)
        htmlContent.getElementById("PROJECT_AND_DATA_MANAGEMENT-overhead-costs-value").text(overheadDataManagementPrice)
        htmlContent.getElementById("overhead-cost-value").text(overheadPrice)
        //Set vat, net and total cost value
        htmlContent.getElementById("total-cost-value-net").text(netPrice)
        htmlContent.getElementById("vat-percentage-value").text("VAT (${taxPercentage}):")
        htmlContent.getElementById("vat-cost-value").text(taxesPrice)
        htmlContent.getElementById("final-cost-value").text(totalPrice)

    }

    private double calculateOverheadSum(List<ProductItem> productItems) {
        double overheadSum = 0.0
        productItems.each {
            overheadSum += it.quantity * it.product.unitPrice * offer.getOverheadRatio()
        }
        return overheadSum
    }

    private void setTaxationRatioInSummary() {
        DecimalFormat decimalFormat = new DecimalFormat("#%")

        double taxRatio = offer.determineTaxCost()
        String taxPercentage = decimalFormat.format(taxRatio)

        htmlContent.getElementById("total-taxes-ratio").text("VAT (${taxPercentage})")
    }

    private void groupProductItems(List<ProductItem> offerItems) {

        dataGenerationItems = []
        dataAnalysisItems = []
        //Project Management and Data Storage are grouped in the same category in the final Offer PDF
        dataManagementItems = []

        // Sort ProductItems into "DataGeneration", "Data Analysis" and "Project & Data Management"
        offerItems.each {
            if (it.product.class in productGroupClasses[ProductGroups.DATA_GENERATION]) {
                dataGenerationItems.add(it)
            }
            if (it.product.class in productGroupClasses[ProductGroups.DATA_ANALYSIS]) {
                dataAnalysisItems.add(it)
            }
            if (it.product.class in productGroupClasses[ProductGroups.PROJECT_AND_DATA_MANAGEMENT]) {
                dataManagementItems.add(it)
            }
        }
    }

    private static HashMap<ProductGroups, List> setProductGroupMapping() {
        Map<ProductGroups, List> map = [:]
        map[ProductGroups.DATA_GENERATION] = [Sequencing, ProteomicAnalysis, MetabolomicAnalysis]
        map[ProductGroups.DATA_ANALYSIS] = [PrimaryAnalysis, SecondaryAnalysis]
        map[ProductGroups.PROJECT_AND_DATA_MANAGEMENT] = [ProjectManagement, DataStorage]

        return map
    }

    /**
     * Possible product groups
     *
     * This enum describes the product groups into which the products of an offer are listed.
     * It also defines the acronyms used to abbreviate the product groups in the offer listings.
     */
    enum ProductGroups {
        DATA_GENERATION("Data generation", "DG"),
        DATA_ANALYSIS("Data analysis", "DA"),
        PROJECT_AND_DATA_MANAGEMENT("Project management & data storage", "PM & DS")

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
     * Max number of characters before line breaks in a property column in the productItem table
     *
     * This enum stores the maximum number of characters before a line breaks occurs in product property column in the productItem table
     */
    enum ProductPropertySpacing {
        PRODUCT_NAME(33),
        PRODUCT_DESCRIPTION(62),
        PRODUCT_UNIT(15),
        PRODUCT_UNIT_PRICE(15),
        PRODUCT_AMOUNT(8),
        PRODUCT_TOTAL(15)


        private int charsLineLimit

        ProductPropertySpacing(int charsLineLimit) {
            this.charsLineLimit = charsLineLimit
        }

        int getCharsLineLimit() {
            return this.charsLineLimit
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
                        <div class="col-7 item-description">${item.product.description}</div>
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

        static String subTableFooter(ProductGroups productGroup) {
            //Each footer takes up spacing in the current table
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
    }

}