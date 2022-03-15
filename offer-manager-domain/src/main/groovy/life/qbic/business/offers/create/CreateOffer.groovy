package life.qbic.business.offers.create

import life.qbic.business.exceptions.DatabaseQueryException
import life.qbic.business.logging.Logger
import life.qbic.business.logging.Logging
import life.qbic.business.offers.OfferCalculus
import life.qbic.business.offers.OfferExistsException
import life.qbic.business.offers.OfferV2
import life.qbic.business.offers.update.UpdateOffer
import life.qbic.business.offers.update.UpdateOfferOutput

/**
 * This class implements logic to create new offers.
 *
 * A PM has received a new project request and uses the offer manager to create a new offer for the customer.
 * Alternatively a new offer is created from an existing offer.
 * @since: 1.0.0
 * @author: Tobias Koch
 */
class CreateOffer implements CreateOfferInput, UpdateOfferOutput{



    private static final Logging log = Logger.getLogger(this.class)

    private CreateOfferDataSource dataSource
    private CreateOfferOutput output
    private UpdateOffer updateOffer //fixme depending on concrete implementation

    CreateOffer(CreateOfferDataSource dataSource, CreateOfferOutput output){
        this.dataSource = dataSource
        this.output = output
        updateOffer = new UpdateOffer(dataSource,this) //fixme depending on concrete implementation
    }
/*
    @Override
    void createOffer(Offer offerContent) {

        if(offerContent.identifier){
            updateOffer.updateOffer(offerContent)
        }else{
            createNewOffer(offerContent)
        }
    }

    private void createNewOffer(Offer offerContent){
        OfferId newOfferId = generateTomatoId(offerContent.customer)

        life.qbic.business.offers.Offer finalizedOffer = new life.qbic.business.offers.Offer.Builder(
                offerContent.customer,
                offerContent.projectManager,
                offerContent.projectTitle,
                offerContent.projectDescription,
                offerContent.items,
                offerContent.selectedCustomerAffiliation)
                .experimentalDesign(offerContent.experimentalDesign)
                .identifier(newOfferId)
                .build()

        storeOffer(finalizedOffer)
    }

    private void storeOffer(life.qbic.business.offers.Offer finalizedOffer) {
        try {
            final offer = ProductClassToCategory.convertOfferToDTO(finalizedOffer)
            dataSource.store(offer)
            output.createdNewOffer(offer)
        } catch (DatabaseQueryException e) {
            output.failNotification(e.message)
        } catch (Exception unexpected) {
            log.error(unexpected.message)
            log.debug(unexpected.message, unexpected)
            output.failNotification("An unexpected during the saving of your offer occurred. " +
                    "Please contact ${Constants.QBIC_HELPDESK_EMAIL}.")
        }
    }

    *//**
     * Method to generate the identifier of an offer with the project conserved part, the random part and the version
     * @param customer which is required for the project conserved part
     * @return
     *//*
    private static OfferId generateTomatoId(Customer customer){
        String projectConservedPart = customer.lastName.toLowerCase()
        int version = 1

        return new OfferId(projectConservedPart, version)
    }

    @Override
    void calculatePrice(List<ProductItem> items, Affiliation affiliation) {
        life.qbic.business.offers.Offer offer = buildOfferForCostCalculation(items, affiliation)
        output.calculatedPrice(
                offer.getTotalNetPrice(),
                offer.getTaxCosts(),
                offer.getOverheadSum(),
                offer.getTotalCosts(),
                offer.getTotalDiscountAmount())
    }
    */

   /**
    * Sends failure notifications that have been
    * recorded during the use case.
    * @param notification containing a failure message
    * @since 1.0.0
    */
    @Override
    void failNotification(String notification) {

    }

    /**
     * Saves an offer in a (persistent) datasource.
     *
     * Developers shall call this method to pass offer content
     * provided from the user in order to trigger the completion
     * of the business use case `Create Offer`,
     * which will apply business policies for offer creation and storage
     * in a pre-configured, optimally persistent data-source.
     *
     * There is no need to set the offer identifier in the passed content,
     * this will be determined and set by the implementation of the use case.
     *
     * If the identifier is passed with the content, it will be ignored.
     *
     * @param offer {@link life.qbic.datamodel.dtos.business.Offer}
     * @since 1.0.0
     */
    @Override
    void createOffer(OfferV2 offer) {
        OfferV2 processedOffer = OfferCalculus.process(offer)
        try {
            dataSource.store(processedOffer)
        } catch (OfferExistsException offerExistsException) {
            String message = "Offer $offer already exists in the database."
            log.error(message, offerExistsException)
            output.failNotification(message)
            return
        } catch (DatabaseQueryException databaseQueryException) {
            String message = "Offer $offer could not be stored."
            log.error(message, databaseQueryException)
            output.failNotification(message)
            return
        }
        output.createdNewOffer(processedOffer)
    }

    /**
     * Confirms the updating of an offer by providing
     * the original offer information including the assigned identifier.
     *
     * @param createdOffer {@link life.qbic.datamodel.dtos.business.Offer}
     * @since 1.0.0
     */
    @Override
    void updatedOffer(OfferV2 createdOffer) {

    }



    /**
    * Builds an offer entity with a dummy customer and a dummy project manager
    * @param items the offer items to be used
    * @param affiliation the affiliation to be used
    * @return an offer object with the given items and affiliation
    *//*
    private static life.qbic.business.offers.Offer buildOfferForCostCalculation(List<ProductItem> items,
                                                                                Affiliation affiliation) {
        final def dummyCustomer = new Customer.Builder("Nobody", "Nobody",
                "nobody@qbic.com").build()
        final def dummyProjectManager = new ProjectManager.Builder("Nobody", "Nobody",
                "nobody@qbic.com").build()
        new life.qbic.business.offers.Offer.Builder(
                dummyCustomer,
                dummyProjectManager,
                "",
                "",
                items,
                affiliation).build()
    }

    @Override
    void updatedOffer(Offer createdOffer) {
        output.createdNewOffer(createdOffer)
    }

    @Override
    void failNotification(String notification) {
        output.failNotification(notification)
    }*/
}
