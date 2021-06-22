package life.qbic.portal.offermanager.offergeneration

import life.qbic.datamodel.dtos.business.AffiliationCategory
import life.qbic.datamodel.dtos.business.ProductItem
import org.jsoup.nodes.Document

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
 * @since 1.1.0
 *
*/
class QuotationDetails {


    /**
     * Product group mapping
     *
     * This map represents the grouping of the different product categories in the offer pdf
     *
     */
    private final Map<ProductGroups, List> productGroupClasses = [:]

    /**
     * Map ProductItems to Productgroup
     *
     * This map represents the grouping of the productItems in the offer to the productGroupClasses
     *
     */
    private Map<ProductGroups, List<ProductItem>> productItemsMap = [:]

    private final List<ProductItem> dataGenerationItems

    private final List<ProductItem> dataAnalysisItems

    private final List<ProductItem> dataManagementItems

    /**
     * Variable used to count the number of Items added to a page
     */
    private static int pageItemsCount

    /**
     * Variable used to count the number of generated productTables in the Offer PDF
     */
    private static int tableCount

    QuotationDetails(Document htmlContent, List <ProductItem> offerItems){
        //1. group the product items
        //2. calculate net prices
        // add final prices
        //3. add page spacing

    }

    private void groupProductItems(List<ProductItem> offerItems){


    }

    /**
     * This class generates the HTML tds for an item, they only need to be put into a table
     * @return
     */
    String generateProductItemsHTML(ProductItem item){

        return ""
    }

    private double calculateNet(){
        return 0
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