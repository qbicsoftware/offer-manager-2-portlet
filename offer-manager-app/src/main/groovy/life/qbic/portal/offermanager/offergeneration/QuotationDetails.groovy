package life.qbic.portal.offermanager.offergeneration

import life.qbic.datamodel.dtos.business.ProductItem

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

     */

    /*
     Product group tables
    */
    private final List<ProductItem> dataGenerationItems
    private final List<ProductItem> dataAnalysisItems
    private final List<ProductItem> dataManagementItems

    QuotationDetails(List<ProductItem> offerItems){
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

}