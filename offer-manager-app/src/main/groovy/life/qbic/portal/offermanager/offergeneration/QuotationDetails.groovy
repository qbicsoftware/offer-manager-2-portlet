package life.qbic.portal.offermanager.offergeneration

import life.qbic.business.offers.Converter
import life.qbic.business.offers.Currency
import life.qbic.datamodel.dtos.business.AffiliationCategory
import life.qbic.datamodel.dtos.business.Offer
import life.qbic.datamodel.dtos.business.ProductItem
import life.qbic.datamodel.dtos.business.services.DataStorage
import life.qbic.datamodel.dtos.business.services.MetabolomicAnalysis
import life.qbic.datamodel.dtos.business.services.PrimaryAnalysis
import life.qbic.datamodel.dtos.business.services.ProjectManagement
import life.qbic.datamodel.dtos.business.services.ProteomicAnalysis
import life.qbic.datamodel.dtos.business.services.SecondaryAnalysis
import life.qbic.datamodel.dtos.business.services.Sequencing
import org.jsoup.nodes.Document

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
 * @since 1.1.0
 *
*/
class QuotationDetails {

    private final Document htmlContent
    private final Offer offer
    private final life.qbic.business.offers.Offer offerEntity

    /**
     * Product group mapping
     *
     * This map represents the grouping of the different product categories in the offer pdf
     *
     */
    private final Map<ProductGroups, List> productGroupClasses = setProductGroupMapping()

    /**
     * Map ProductItems to ProductGroup
     *
     * This map represents the grouping of the productItems in the offer to the productGroupClasses
     *
     */
    private Map<ProductGroups, List<ProductItem>> productItemsMap = [:]

    private final List<ProductItem> dataGenerationItems

    private final List<ProductItem> dataAnalysisItems

    private final List<ProductItem> dataManagementItems

    QuotationDetails(Document htmlContent, Offer offer){
        this.offer = Objects.requireNonNull(offer, "offer object must not be a null reference")
        this.htmlContent = Objects.requireNonNull(htmlContent, "htmlContent object must not be a null reference")
        this.offerEntity = Converter.convertDTOToOffer(offer)

        groupProductItems(offer.items)
    }

    void fillTemplateWithQuotationDetailsContent(){
        //2. calculate net prices
        // add final prices
        addTotalPrices()
        setTaxationRatioInSummary()
        //3. add page spacing
    }

    /**
     * This class generates the HTML tds for an item, they only need to be put into a table
     * @return
     */
    String generateProductItemsHTML(ProductItem item){

        return ""
    }

    private void addTotalPrices() {

        DecimalFormat decimalFormat = new DecimalFormat("#%")
        String overheadPercentage = decimalFormat.format(offer.overheadRatio)

        // Get prices without currency symbol for detailed price listing
        final overheadPrice = Currency.getFormatterWithoutSymbol().format(offer.overheads)
        final netPrice = Currency.getFormatterWithoutSymbol().format(offer.netPrice)
        final taxesPrice = Currency.getFormatterWithoutSymbol().format(offer.taxes)
        final totalPrice = Currency.getFormatterWithoutSymbol().format(offer.totalPrice)

        final overheadDataGenerationPrice = Currency.getFormatterWithoutSymbol().format(calculateOverheadSum(productItemsMap[ProductGroups.DATA_GENERATION]))
        final overheadDataAnalysisPrice = Currency.getFormatterWithoutSymbol().format(calculateOverheadSum(productItemsMap[ProductGroups.DATA_ANALYSIS]))
        final overheadDataManagementPrice = Currency.getFormatterWithoutSymbol().format(calculateOverheadSum(productItemsMap[ProductGroups.PROJECT_AND_DATA_MANAGEMENT]))

        double taxRatio = offerEntity.determineTaxCost()
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
        double overheadSum
        productItems.each {
            overheadSum += it.quantity * it.product.unitPrice * offer.overheadRatio
        }
        return overheadSum
    }

    private void setTaxationRatioInSummary() {
        DecimalFormat decimalFormat = new DecimalFormat("#%")
        String country = offer.getSelectedCustomerAffiliation().getCountry()
        AffiliationCategory affiliationCategory = offer.getSelectedCustomerAffiliation().getCategory()
        double taxRatio = offerEntity.determineTaxCost()
        String taxPercentage = decimalFormat.format(taxRatio)
        htmlContent.getElementById("total-taxes-ratio").text("VAT (${taxPercentage})")
    }

    private void groupProductItems(List<ProductItem> offerItems){

        List<ProductItem> dataGenerationItems = []
        List<ProductItem> dataAnalysisItems = []
        //Project Management and Data Storage are grouped in the same category in the final Offer PDF
        List<ProductItem> dataManagementItems = []

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

        //Map Lists to the "DataGeneration", "DataAnalysis" and "Project and Data Management"
        productItemsMap[ProductGroups.DATA_GENERATION] = dataGenerationItems
        productItemsMap[ProductGroups.DATA_ANALYSIS] = dataAnalysisItems
        productItemsMap[ProductGroups.PROJECT_AND_DATA_MANAGEMENT] = dataManagementItems

    }

    private static HashMap<ProductGroups,List> setProductGroupMapping() {
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

}