package life.qbic.portal.qoffer2.web.presenters

import life.qbic.datamodel.dtos.business.Offer
import life.qbic.datamodel.dtos.business.services.DataStorage
import life.qbic.datamodel.dtos.business.services.PrimaryAnalysis
import life.qbic.datamodel.dtos.business.services.Product
import life.qbic.datamodel.dtos.business.services.ProjectManagement
import life.qbic.datamodel.dtos.business.services.SecondaryAnalysis
import life.qbic.datamodel.dtos.business.services.Sequencing
import life.qbic.portal.portlet.offers.create.CreateOfferOutput
import life.qbic.portal.portlet.products.ListProductsOutput
import life.qbic.portal.qoffer2.services.OfferService
import life.qbic.portal.qoffer2.web.viewmodel.CreateOfferViewModel
import life.qbic.portal.qoffer2.web.viewmodel.ProductItemViewModel
import life.qbic.portal.qoffer2.web.viewmodel.ViewModel

/**
 * Presenter for the CreateOffer
 *
 * This presenter handles the output of the CreateOffer use case and prepares it for a view.
 *
 * @since: 1.0.0
 */
class CreateOfferPresenter implements CreateOfferOutput, ListProductsOutput{

    private final ViewModel viewModel
    private final CreateOfferViewModel createOfferViewModel
    private final OfferService offerService

    CreateOfferPresenter(ViewModel viewModel, CreateOfferViewModel createOfferViewModel,
                         OfferService offerService){
        this.viewModel = viewModel
        this.createOfferViewModel = createOfferViewModel
    }

    @Override
    void createdNewOffer(Offer createdOffer) {
        this.viewModel.successNotifications.add("Created offer with title\n" +
                "\'${createdOffer.projectTitle}\'\nsuccessfully")
        this.offerService.offerCreatedEvent.emit(createdOffer)
    }

    @Override
    void calculatedPrice(double price) {
        this.createOfferViewModel.offerPrice = price
    }

    @Override
    void calculatedPrice(double netPrice, double taxes, double overheads, double totalPrice) {
        this.createOfferViewModel.netPrice = netPrice
        this.createOfferViewModel.taxes = taxes
        this.createOfferViewModel.overheads = overheads
        this.createOfferViewModel.totalPrice = totalPrice
    }

    @Override
    void failNotification(String notification) {
       this.viewModel.failureNotifications.add(notification)
    }

    @Override
    void showAvailableProducts(List<Product> availableProducts) {

        availableProducts.each {
            ProductItemViewModel product = new ProductItemViewModel(0, it)

            if (it instanceof Sequencing && !contains(this.createOfferViewModel.sequencingProducts,it)) {
                this.createOfferViewModel.sequencingProducts.add(product)
            }
            else if (it instanceof ProjectManagement && !contains(this.createOfferViewModel.managementProducts,it)) {
                this.createOfferViewModel.managementProducts.add(product)
            }
            else if (it instanceof PrimaryAnalysis && !contains(this.createOfferViewModel.primaryAnalysisProducts,it)) {
                this.createOfferViewModel.primaryAnalysisProducts.add(product)
            }
            else if (it instanceof SecondaryAnalysis && !contains(this.createOfferViewModel.secondaryAnalysisProducts,it)) {
                this.createOfferViewModel.secondaryAnalysisProducts.add(product)
            }
            else if (it instanceof DataStorage && !contains(this.createOfferViewModel.storageProducts,it)) {
                this.createOfferViewModel.storageProducts.add(product)
            }
        }
    }

    /**
     * Tests if the product is already contained in a list
     *
     * @param list containing a list of {@link ProductItemViewModel}
     * @param product which should tested if already contained in list
     * @return boolean value to determine if product is in list
     */
    private static boolean contains(List<ProductItemViewModel> list, Product product){
        boolean contains = list.any {
            it.product == product
        }
        return contains
    }
}
