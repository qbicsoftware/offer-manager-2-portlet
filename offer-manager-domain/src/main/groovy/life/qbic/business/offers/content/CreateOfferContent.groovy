package life.qbic.business.offers.content

import life.qbic.business.offers.Converter
import life.qbic.business.offers.OfferContent
import life.qbic.business.offers.OfferItem
import life.qbic.business.offers.fetch.FetchOffer
import life.qbic.business.offers.fetch.FetchOfferDataSource
import life.qbic.business.offers.fetch.FetchOfferInput
import life.qbic.business.offers.fetch.FetchOfferOutput
import life.qbic.datamodel.dtos.business.AffiliationCategory
import life.qbic.datamodel.dtos.business.Offer
import life.qbic.datamodel.dtos.business.OfferId
import life.qbic.datamodel.dtos.business.ProductItem
import life.qbic.datamodel.dtos.business.services.DataStorage
import life.qbic.datamodel.dtos.business.services.MetabolomicAnalysis
import life.qbic.datamodel.dtos.business.services.PrimaryAnalysis
import life.qbic.datamodel.dtos.business.services.Product
import life.qbic.datamodel.dtos.business.services.ProjectManagement
import life.qbic.datamodel.dtos.business.services.ProteomicAnalysis
import life.qbic.datamodel.dtos.business.services.SecondaryAnalysis
import life.qbic.datamodel.dtos.business.services.Sequencing

import java.text.DateFormat

/**
 * <h1>Creates the content for an offer export</h1>
 *
 * <p>This use case aggregates all information that needs to be present in an offer. This use case should be called to forward the offer information
 * </p>
 *
 * @since 1.1.0
 *
*/
class CreateOfferContent implements CreateOfferContentInput, FetchOfferOutput{

    CreateOfferContentOutput output
    FetchOfferInput fetchOfferInput

    private AffiliationCategory affiliationCategory
    private double overheadRatio

    /**
     * Product group mapping
     *
     * This map represents the grouping of the different product categories in the offer pdf
     *
     */
    private final Map<ProductGroup, List> productGroupClasses = setProductGroupMapping()

    private List<ProductItem> dataGenerationItems
    private List<ProductItem> dataAnalysisItems
    private List<ProductItem> dataManagementItems


    CreateOfferContent(CreateOfferContentOutput output, FetchOfferDataSource fetchOfferDataSource){
        this.output = output
        this.fetchOfferInput = new FetchOffer(fetchOfferDataSource,this)
    }

    @Override
    void createOfferContent(OfferId offerId) {
        fetchOfferInput.fetchOffer(offerId)
    }

    @Override
    void failNotification(String notification) {
        output.failNotification(notification)
    }

    @Override
    void fetchedOffer(Offer fetchedOffer) {
        //create the offer business object
        life.qbic.business.offers.Offer offer = Converter.convertDTOToOffer(fetchedOffer)
        affiliationCategory = offer.selectedCustomerAffiliation.category
        overheadRatio = offer.getOverheadRatio()

        //collect the content for the offerpdf
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG, Locale.US)
        String creationDate = dateFormat.format(offer.modificationDate)
        String expirationDate = offer.expirationDate.toLocalDate().toString()
        groupProductItems(offer.items)

        def offerContent = new OfferContent.Builder(offer.customer,offer.selectedCustomerAffiliation,offer.projectManager,creationDate,expirationDate,offer.projectTitle
        ,offer.projectObjective, offer.experimentalDesign.orElse(""),offer.identifier.toString())

        //collect productitems and convert to offeritems
        groupProductItems(offer.items)
        List<OfferItem> dataManagementOfferItems = dataManagementItems.collect{createProductItemToOfferItem(it)}
        List<OfferItem> dataAnalysisOfferItems = dataAnalysisItems.collect{createProductItemToOfferItem(it)}
        List<OfferItem> dataGenerationOfferItems = dataGenerationItems.collect{createProductItemToOfferItem(it)}

        offerContent.dataGenerationItems(dataGenerationOfferItems)
        .dataAnalysisItems(dataAnalysisOfferItems)
        .dataManagementItems(dataManagementOfferItems)

        double overheadsDA = calculateOverheadSum(dataAnalysisOfferItems)
        double overheadsDG = calculateOverheadSum(dataGenerationOfferItems)
        double overheadsPMandDS = calculateOverheadSum(dataManagementOfferItems)

        offerContent.overheadsDataAnalysis(overheadsDA)
        .overheadsDataGeneration(overheadsDG)
        .overheadsProjectManagementAndDataStorage(overheadsPMandDS)
        .overheadTotal(offer.overheadSum)

        offerContent.netDataAnalysis(calculateNetSum(dataAnalysisOfferItems))
        .netDataGeneration(calculateNetSum(dataGenerationOfferItems))
        .netProjectManagementAndDataStorage(calculateNetSum(dataManagementOfferItems))
        .netCost(offer.totalNetPrice)

        offerContent.totalVat(offer.taxCosts)
        .vatRatio(offer.determineTaxCost())
        .totalCost(offer.totalCosts)

        OfferContent offerContentFinal = offerContent.build()

        output.createdOfferContent(offerContentFinal)
    }

    /**
     * Helper method that calculates the NET price for a list of product items
     * @param offerItems The product item list for which the NET is calculated
     *  @return The net value for the given list of items
     */
    private static double calculateNetSum(List<OfferItem> offerItems) {
        double netSum = 0
        offerItems.each {
            netSum += it.quantity * it.unitPrice
        }
        return netSum
    }

    /**
     * Calculates the overhead sum of all product items
     * @param offerItems Items for which the overheads are calculated
     * @return The overhead price for all items
     */
    private double calculateOverheadSum(List<OfferItem> offerItems) {
        double overheadSum = 0.0
        offerItems.each {
            overheadSum += it.quantity * it.unitPrice * overheadRatio
        }
        return overheadSum
    }


    private OfferItem createProductItemToOfferItem(ProductItem productItem){
        Product product = productItem.product
        double unitPrice = (affiliationCategory == AffiliationCategory.INTERNAL) ? product.internalUnitPrice : product.externalUnitPrice

        OfferItem offerItem = new OfferItem.Builder(productItem.quantity, product.description, product.productName, unitPrice, productItem.quantityDiscount,
                product.serviceProvider.name(), product.unit.name(), productItem.totalPrice).build()

        return offerItem
    }


    /**
     * Adds the product items to the respective product group list
     * @param offerItems List of productItems contained in offer
     */
    private void groupProductItems(List<ProductItem> offerItems) {

        dataGenerationItems = []
        dataAnalysisItems = []
        dataManagementItems = []

        // Sort ProductItems into "DataGeneration", "Data Analysis" and "Project & Data Management"
        offerItems.each {
            if (it.product.class in productGroupClasses[ProductGroup.DATA_GENERATION]) {
                dataGenerationItems.add(it)
            }
            if (it.product.class in productGroupClasses[ProductGroup.DATA_ANALYSIS]) {
                dataAnalysisItems.add(it)
            }
            if (it.product.class in productGroupClasses[ProductGroup.PROJECT_AND_DATA_MANAGEMENT]) {
                dataManagementItems.add(it)
            }
        }
    }

    private static HashMap<ProductGroup, List> setProductGroupMapping() {
        Map<ProductGroup, List> map = [:]
        map[ProductGroup.DATA_GENERATION] = [Sequencing, ProteomicAnalysis, MetabolomicAnalysis]
        map[ProductGroup.DATA_ANALYSIS] = [PrimaryAnalysis, SecondaryAnalysis]
        map[ProductGroup.PROJECT_AND_DATA_MANAGEMENT] = [ProjectManagement, DataStorage]

        return map
    }

    /**
     * Possible product groups
     *
     * This enum describes the product groups into which the products of an offer are categorized.
     */
    enum ProductGroup {
        DATA_GENERATION("Data generation"),
        DATA_ANALYSIS("Data analysis"),
        PROJECT_AND_DATA_MANAGEMENT("Project management & data storage")

        private String name

        ProductGroup(String name) {
            this.name = name
        }

        String getName() {
            return this.name
        }
    }
}
