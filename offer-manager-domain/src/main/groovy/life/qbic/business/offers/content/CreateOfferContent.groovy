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
import life.qbic.datamodel.dtos.business.services.*

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
    private static List<Class> DATA_GENERATION = [Sequencing, ProteomicAnalysis, MetabolomicAnalysis]
    private static List<Class> DATA_ANALYSIS = [PrimaryAnalysis, SecondaryAnalysis]
    private static List<Class> PROJECT_AND_DATA_MANAGEMENT = [ProjectManagement, DataStorage]
    private static List<Class> EXTERNAL_SERVICES = [ExternalServiceProduct]


    private List<ProductItem> dataGenerationItems
    private List<ProductItem> dataAnalysisItems
    private List<ProductItem> dataManagementItems
    private List<ProductItem> externalServiceItems


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
        Date creationDate = offer.modificationDate
        Date expirationDate = offer.expirationDate
        groupProductItems(offer.items)
        String id = offer.identifier.toString()
        def offerContentBuilder = new OfferContent.Builder(offer.customer,offer.selectedCustomerAffiliation,offer.projectManager,creationDate,expirationDate,offer.projectTitle
        ,offer.projectObjective, offer.experimentalDesign.orElse(""),offer.identifier.toString(), offer.getTotalNetPriceWithOverheads())

        //collect productitems and convert to offeritems
        groupProductItems(offer.items)
        List<OfferItem> dataManagementOfferItems = dataManagementItems.collect{createOfferItem(it)}
        List<OfferItem> dataAnalysisOfferItems = dataAnalysisItems.collect{createOfferItem(it)}
        List<OfferItem> dataGenerationOfferItems = dataGenerationItems.collect{createOfferItem(it)}

        offerContentBuilder.dataGenerationItems(dataGenerationOfferItems)
        .dataAnalysisItems(dataAnalysisOfferItems)
        .dataManagementItems(dataManagementOfferItems)

        double overheadsDA = calculateOverheadSum(dataAnalysisOfferItems)
        double overheadsDG = calculateOverheadSum(dataGenerationOfferItems)
        double overheadsPMandDS = calculateOverheadSum(dataManagementOfferItems)

        offerContentBuilder.overheadsDataAnalysis(overheadsDA)
        .overheadsDataGeneration(overheadsDG)
        .overheadsProjectManagementAndDataStorage(overheadsPMandDS)
        .overheadTotal(offer.overheadSum)
        .overheadRatio(offer.overheadRatio)

        offerContentBuilder.netDataAnalysis(calculateNetSum(dataAnalysisOfferItems))
        .netDataGeneration(calculateNetSum(dataGenerationOfferItems))
        .netProjectManagementAndDataStorage(calculateNetSum(dataManagementOfferItems))
        .netCost(offer.totalNetPrice)

        offerContentBuilder.totalVat(offer.taxCosts)
        .vatRatio(offer.determineTaxCost())
        .totalCost(offer.totalCosts)
        .totalDiscountAmount(offer.totalDiscountAmount)

        OfferContent offerContent = offerContentBuilder.build()

        output.createdOfferContent(offerContent)
    }

    /**
     * Helper method that calculates the NET price for a list of product items
     * @param offerItems The product item list for which the NET is calculated
     *  @return The net value for the given list of items
     */
    private static double calculateNetSum(List<OfferItem> offerItems) {
        if (offerItems.empty) {
            return 0
        } else {
            return offerItems.sum {
                it.itemTotal - it.quantityDiscount
            } as double
        }
    }

    /**
     * Calculates the overhead sum of all product items
     * @param offerItems Items for which the overheads are calculated
     * @return The overhead price for all items
     */
    private double calculateOverheadSum(List<OfferItem> offerItems) {
        if (offerItems.empty) {
            return 0
        } else {
            return offerItems.sum {
                (it.itemTotal - it.quantityDiscount ) * overheadRatio
            } as double
        }
    }


    private OfferItem createOfferItem(ProductItem productItem) {
        Product product = productItem.product
        double unitPrice = (affiliationCategory == AffiliationCategory.INTERNAL) ? product.internalUnitPrice : product.externalUnitPrice
        OfferItem offerItem = new OfferItem.Builder(productItem.quantity, product.description, product.productName, unitPrice, productItem.quantityDiscount,
                calculateDiscountPerUnit(productItem),calculateDiscountPercentage(productItem),product.serviceProvider.getLabel(), product.unit.value, productItem.totalPrice).build()

        return offerItem
    }
    
    
    /**
     * Calculates the discount percentage for a product item. Note that this is not a ratio, but a number between 0 and 100.
     * @param offerItem item for which the discount percentage should be calculated
     * @return the discount percentage based on quantity discount and item total cost
     */
    private double calculateDiscountPercentage(ProductItem productItem) {
        BigDecimal totalPrice = productItem.totalPrice.toBigDecimal()
        if (totalPrice.compareTo(BigDecimal.ZERO) == 0) {
            //avoid division by 0
            //if a product has a total price of 0 set discount percentage to 0
            return 0.doubleValue()
        }
        BigDecimal result = 100.0.toBigDecimal() * productItem.quantityDiscount.toBigDecimal() / totalPrice
        return result.doubleValue()
    }
    
    
    /**
     * Calculates the discount per unit for a product item
     * @param offerItem item for which the discount per unit should be calculated
     * @return the discount per unit, if applicable, 0 otherwise
     */
    private double calculateDiscountPerUnit(ProductItem productItem) {
        BigDecimal quantity = productItem.quantity.toBigDecimal()
        if (quantity.compareTo(BigDecimal.ZERO) == 0) {
            //avoid division by 0
            //if a a productItem has no quantity set discount to 0
            return 0.doubleValue()
        }
        BigDecimal result = productItem.quantityDiscount.toBigDecimal() / quantity
        return result.doubleValue()
    }

    /**
     * Adds the product items to the respective product group list
     * @param offerItems List of productItems contained in offer
     */
    private void groupProductItems(List<ProductItem> offerItems) {

        dataGenerationItems = []
        dataAnalysisItems = []
        dataManagementItems = []
        externalServiceItems = []

        // Sort ProductItems into "DataGeneration", "Data Analysis" and "Project & Data Management"
        offerItems.each {
            switch (it.product.class) {
                case ({it in DATA_GENERATION}):
                    dataGenerationItems.add(it)
                    break
                case ({it in DATA_ANALYSIS}):
                    dataAnalysisItems.add(it)
                    break
                case ({it in PROJECT_AND_DATA_MANAGEMENT}):
                    dataManagementItems.add(it)
                    break
                case ({it in EXTERNAL_SERVICES}):
                    externalServiceItems.add(it)
                    break
            }
        }
    }
}
