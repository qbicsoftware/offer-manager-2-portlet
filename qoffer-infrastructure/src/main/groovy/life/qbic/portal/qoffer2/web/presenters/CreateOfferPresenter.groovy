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

import life.qbic.portal.qoffer2.web.viewmodel.CreateOfferViewModel
import life.qbic.portal.qoffer2.web.viewmodel.ProductItemViewModel
import life.qbic.portal.qoffer2.web.viewmodel.ViewModel
import life.qbic.portal.qoffer2.web.viewmodel.create.offer.OfferOverviewViewModel
import life.qbic.portal.qoffer2.web.viewmodel.create.offer.SelectItemsViewModel

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
    private final SelectItemsViewModel selectItemsViewModel
    private final OfferOverviewViewModel overviewViewModel

    CreateOfferPresenter(ViewModel viewModel, CreateOfferViewModel createOfferViewModel, SelectItemsViewModel selectItemsViewModel, OfferOverviewViewModel offerOverviewViewModel) {
        this.viewModel = viewModel
        this.createOfferViewModel = createOfferViewModel
        this.selectItemsViewModel = selectItemsViewModel
        this.overviewViewModel = offerOverviewViewModel
    }

    @Override
    void createdNewOffer(Offer createdOffer) {
        //TODO implement
        throw new Exception("Method not implemented.")
    }

    @Override
    void calculatedPrice(double price) {
        this.overviewViewModel.offerPrice = price
    }

    @Override
    void failNotification(String notification) {
        //TODO implement
        throw new Exception("Method not implemented.")
    }

    @Override
    void showAvailableProducts(List<Product> availableProducts) {

        availableProducts.each {
            ProductItemViewModel product = new ProductItemViewModel(0, it)

            if (it instanceof Sequencing && !contains(this.selectItemsViewModel.sequencingProducts,it)) {
                this.selectItemsViewModel.sequencingProducts.add(product)
            }
            else if (it instanceof ProjectManagement && !contains(this.selectItemsViewModel.managementProducts,it)) {
                this.selectItemsViewModel.managementProducts.add(product)
            }
            else if (it instanceof PrimaryAnalysis && !contains(this.selectItemsViewModel.primaryAnalysisProducts,it)) {
                this.selectItemsViewModel.primaryAnalysisProducts.add(product)
            }
            else if (it instanceof SecondaryAnalysis && !contains(this.selectItemsViewModel.secondaryAnalysisProducts,it)) {
                this.selectItemsViewModel.secondaryAnalysisProducts.add(product)
            }
            else if (it instanceof DataStorage && !contains(this.selectItemsViewModel.storageProducts,it)) {
                this.selectItemsViewModel.storageProducts.add(product)
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
