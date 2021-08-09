package life.qbic.portal.offermanager.offergeneration


import life.qbic.business.offers.Currency
import life.qbic.business.offers.OfferContent
import life.qbic.business.offers.OfferItem
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
 * Also it returns the HTML elements for tables, product group headings,..</p>
 *
 * @since 1.1.0
 */
class QuotationDetails {
    /**
     * Variable used to count the number of Items added to a page
     */
    private int pageItemsCount = 6

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
    private final int maxPageItems = 17

    private final Document htmlContent
    private final OfferContent offer

    QuotationDetails(Document htmlContent, OfferContent offer) {
        this.htmlContent = Objects.requireNonNull(htmlContent, "htmlContent object must not be a null reference")
        this.offer = offer
    }

    void fillTemplateWithQuotationDetailsContent() {
        addSelectedItems()
        setTotalCostFooter()
    }

    private void addSelectedItems() {
        //We need to remove the page break between the tables in the template
        htmlContent.getElementById("template-page-break").remove()

        //Generate Product Table for each Category
        generateProductTable(offer.getDataGenerationItems(), ProductGroup.DATA_GENERATION, offer.netDataGeneration)
        generateProductTable(offer.getDataAnalysisItems(), ProductGroup.DATA_ANALYSIS, offer.netDataAnalysis)
        generateProductTable(offer.getDataManagementItems(), ProductGroup.PROJECT_AND_DATA_MANAGEMENT, offer.netPMandDS)
    }

    /**
     * Generates the table footer with all prices and moves it to the right position
     */
    private void setTotalCostFooter(){
        //add prices in the template
        setTotalPrices()

        if (isOverflowingPage()) {
            //If current page is full of items generate new table and add total pricing there
            htmlContent.getElementById("item-table-grid").append(ItemPrintout.pageBreak())
            htmlContent.getElementById("item-table-grid").append(ItemPrintout.createNewTable("item-table-grid"))
            htmlContent.getElementById("item-table-grid").append(ItemPrintout.tableHeader())
            //move footer after new table
            Element element = htmlContent.getElementById("grid-table-footer")
            htmlContent.getElementById("item-table-grid").after(element)
        }

    }

    /**
     * Generates the product table for a given list of items and a product group
     * @param items
     * @param productGroup
     * @param subTotal the NET price for the provided product group
     */
    private void generateProductTable(List<OfferItem> items, ProductGroup productGroup, double subTotal) {
        //Create the items in html in the overview table
        //Check if there are ProductItems stored in list
        if (!items) {
            htmlContent.getElementById("${productGroup.toString()}-table").remove()
            return
        }
        //Clear Template content
        String elementId = generateElementID(0, productGroup)
        htmlContent.getElementById(elementId).empty()

        if (isOverflowingPage()) {
            generateHTMLTableOnNextPage("${productGroup.toString()}-table")
            resetPageItemsCount()
        }
        //Append Table Title and Header
        htmlContent.getElementById(elementId).append(ItemPrintout.tableHeader())

        items.each { OfferItem item ->
            generateItemContent(item, elementId)
            if (item.quantityDiscount != 0) {
                String discountDescription = createDiscountDescription(item.quantity,item.unit, item.discountPercentage)
                generateDiscountItemContent(discountDescription, elementId, item)
            }
        }
        //account for spaces of added table elements, footer, totals,...
        pageItemsCount += 4
        htmlContent.getElementById(elementId).append(ItemPrintout.subTableFooter(productGroup))

        // Update footer Prices
        addSubTotalPrices(productGroup, subTotal)
    }

    private String createDiscountDescription(double quantity, String unit, double discountPercentage) {
        String unitName = unit.toString().toLowerCase()
        unitName = quantity != 1 : unitName + "s" : unitName
        return "Discount on ${quantity} ${unitName} based on item no ${itemNumber}. ${discountPercentage}% discount applied"
    }

    /**
     * Appends an item to a element id, if no space is left on the page a new page is created onto which a new tableHeader is added
     * @param item The item place into the HTML document
     * @param elementId The id references where the item is added
     */
    private void generateItemContent(OfferItem item, String elementId){
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

    /**
     * Appends a discount item to a element id, if no space is left on the page a new page is created onto which a new tableHeader is added
     * @param description The item description
     * @param discountAmount The discount amount
     * @param elementId The id references where the item is added
     */
    private void generateDiscountItemContent(String description, String elementId, OfferItem item){
        itemNumber++
        if (isOverflowingPage()) {
            generateHTMLTableOnNextPage(elementId)
            //repeat table header for next page
            htmlContent.getElementById(elementId).append(ItemPrintout.tableHeader())
            resetPageItemsCount()
        }
        htmlContent.getElementById(elementId).append(ItemPrintout.discountItemInHTML(itemNumber, description, item))
        pageItemsCount += determineItemSpace("Discount", description, item.quantityDiscount)
    }

    private static String generateElementID(int tableCount, ProductGroup productGroups){
        return productGroups.toString() + "-" + "product-items" + "-" + ++tableCount}

    private void resetPageItemsCount(){ pageItemsCount = 1}

    /**
     * Creates a new table element after a page break
     * @param elementId The id which will reference the new table
     */
    private void generateHTMLTableOnNextPage(String elementId){
        htmlContent.getElementById(elementId).append(ItemPrintout.pageBreak())
        elementId = "product-items" + "-" + ++tableCount
        htmlContent.getElementById("item-table-grid").append(ItemPrintout.createNewTable(elementId))
    }

    private boolean isOverflowingPage() {
        return pageItemsCount >= maxPageItems
    }

    /**
     * Adds the subtotal NET price for a product group to the HTML template
     * @param productGroup The product group for which the subtotal is added
     * @param subTotal the NET price for the provided product group
     */
    private void addSubTotalPrices(ProductGroup productGroup, double subTotal) {
        final netPrice = Currency.getFormatterWithoutSymbol().format(subTotal)

        htmlContent.getElementById("${productGroup}-net-costs-value").text(netPrice)
    }

    /**
     * Estimates the space of a product item in the final offer template
     * @param item The item for which the spacing should be calculated
     * @return The calculated space in the final offer template that the item requires
     */
    private static int determineItemSpace(OfferItem item) {

        ArrayList<Integer> calculatedSpaces = []

        //Determine amount of spacing necessary from highest itemSpace value of all columns
        calculatedSpaces.add(calculateItemSpace(item.productName, ProductPropertySpacing.PRODUCT_NAME))
        calculatedSpaces.add(calculateItemSpace(item.productDescription, ProductPropertySpacing.PRODUCT_DESCRIPTION))
        calculatedSpaces.add(calculateItemSpace(item.quantity as String, ProductPropertySpacing.PRODUCT_AMOUNT))

        calculatedSpaces.add(calculateItemSpace(item.unit as String, ProductPropertySpacing.PRODUCT_UNIT))
        calculatedSpaces.add(calculateItemSpace(item.unitPrice as String, ProductPropertySpacing.PRODUCT_UNIT_PRICE))
        calculatedSpaces.add(calculateItemSpace(item.getItemTotal() as String, ProductPropertySpacing.PRODUCT_TOTAL))
        calculatedSpaces.add(calculateItemSpace("Service Provider: "+item.getServiceProvider(), ProductPropertySpacing.PRODUCT_FACILITY))

        return calculatedSpaces.max()
    }

    private static int determineItemSpace(String productName, String productDescription, double itemTotalCosts) {

        ArrayList<Integer> calculatedSpaces = []

        //Determine amount of spacing necessary from highest itemSpace value of all columns
        calculatedSpaces.add(calculateItemSpace(productName, ProductPropertySpacing.PRODUCT_NAME))
        calculatedSpaces.add(calculateItemSpace(productDescription, ProductPropertySpacing.PRODUCT_DESCRIPTION))
        calculatedSpaces.add(calculateItemSpace(itemTotalCosts as String, ProductPropertySpacing.PRODUCT_TOTAL))

        return calculatedSpaces.max()
    }

    /**
     * Estimates the space required for a given text
     * @param productProperty A text which should be place on the offer
     * @param productPropertySpacing A spacing factor assigned for that text
     * @return An estimation of the space needed for the given text
     */
    private static int calculateItemSpace(String productProperty, ProductPropertySpacing productPropertySpacing) {
        return Math.ceil(productProperty.length() / productPropertySpacing.getCharsLineLimit())
    }

    /**
     * Adds the prices to the table footer. This will add the overhead summary, the net, vat and total costs
     */
    private void setTotalPrices() {
        DecimalFormat decimalFormat = new DecimalFormat("#%")
        String overheadPercentage = decimalFormat.format(offer.getOverheadRatio())

        // Get prices without currency symbol for detailed price listing
        final overheadPrice = Currency.getFormatterWithoutSymbol().format(offer.getOverheadTotal())
        final netPrice = Currency.getFormatterWithoutSymbol().format(offer.getNetCost())
        final taxesPrice = Currency.getFormatterWithoutSymbol().format(offer.getTotalVat())
        final totalPrice = Currency.getFormatterWithoutSymbol().format(offer.getTotalCost())

        final overheadDataGenerationPrice = Currency.getFormatterWithoutSymbol().format(offer.getOverheadsDataGeneration())
        final overheadDataAnalysisPrice = Currency.getFormatterWithoutSymbol().format(offer.getOverheadsDataAnalysis())
        final overheadDataManagementPrice = Currency.getFormatterWithoutSymbol().format(offer.getOverheadsProjectManagementAndDataStorage())
        final totalDiscountAmount = Currency.getFormatterWithoutSymbol().format(-1 * offer.getTotalDiscountAmount())

        double taxRatio = offer.getVatRatio()
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
        htmlContent.getElementById("total-discount-value").text(totalDiscountAmount)

    }

    /**
     * Possible product groups
     *
     * This enum describes the product groups into which the products of an offer are categorized.
     * It also defines the acronyms used to abbreviate the product groups in the offer listings.
     */
    enum ProductGroup {
        DATA_GENERATION("Data generation", "DG"),
        DATA_ANALYSIS("Data analysis", "DA"),
        PROJECT_AND_DATA_MANAGEMENT("Project management & data storage", "PM & DS")

        private String name
        private String acronym

        ProductGroup(String name, String acronym) {
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
        PRODUCT_TOTAL(15),
        PRODUCT_FACILITY(62)

        private final int charsLineLimit

        ProductPropertySpacing(int charsLineLimit) {
            this.charsLineLimit = charsLineLimit
        }

        int getCharsLineLimit() {
            return this.charsLineLimit
        }
    }

    private static class ItemPrintout {

        /**
         * Translates an product item into a HTML row element that can be added to a table
         * @param offerPosition The item position on the offer
         * @param item The product item that is put on the offer
         * @return returns the HTML code as string
         */
        static String itemInHTML(int offerPosition, OfferItem item) {
            return """<div class="row product-item">
                        <div class="col-1">${offerPosition}</div>
                        <div class="col-4 ">${item.getProductName()}</div>
                        <div class="col-1 price-value">${item.getQuantity()}</div>
                        <div class="col-2 text-center">${item.getUnit()}</div>
                        <div class="col-2 price-value">${Currency.getFormatterWithoutSymbol().format(item.getUnitPrice())}</div>
                        <div class="col-2 price-value">${Currency.getFormatterWithoutSymbol().format(item.getItemTotal())}</div>
                    </div>
                    <div class="row product-item">
                        <div class="col-1"></div>

                        <div class="col-7 item-description">${item.getProductDescription()}</div>
                        <div class="col-4"></div>
                    </div>
                    <div class="row product-item">
                        <div class="col-1"></div>
                        <div class="col-7 item-description"> Service Provider: ${item.getServiceProvider()}</div>
                        <div class="col-4"></div>

                    </div>
                    """
        }

        static String discountItemInHTML(int offerPosition, String description, OfferItem offerItem) {
            return """<div class="row product-item">
                        <div class="col-1">${offerPosition}</div>
                        <div class="col-4 ">Discount</div>
                        <div class="col-1 price-value">${offerItem.getQuantity()}</div>
                        <div class="col-2 text-center">${offerItem.getUnit()}</div>
                        <div class="col-2 price-value">${offerItem.getDiscountPerUnit()}</div>
                        <div class="col-2 price-value">-${Currency.getFormatterWithoutSymbol().format(offerItem.getQuantityDiscount())}</div>
                    </div>
                    <div class="row product-item">
                        <div class="col-1"></div>
                        <div class="col-7 item-description">${description}</div>
                        <div class="col-7"></div>
                    </div>
                    """
        }
        
        /**
         * Creates a page break div
         * @return the page break div as string
         */
        static String pageBreak() {
            return """<div class="pagebreak"></div>
                   """
        }

        /**
         * Creates the table header with the column titles
         * @return a HTML string with the table header content
         */
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

        /**
         * Creates the HTML snippet for a table
         * @param elementId The id which can be used to reference an element in the generated table
         * @return HTML snippet for a new table
         */
        static String createNewTable(String elementId) {
            """<div class="product-items" id="${elementId}"></div>
            """
        }

        /**
         * Creates the HTML snippet for the sub table footer containing the net price of the associated sub table of a product group
         * @param productGroup The product group for which the footer is created. This will be referenced in the footers text
         * @return HTML snippet with the product group footer
         */
        static String subTableFooter(ProductGroup productGroup) {
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
