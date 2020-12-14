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

import java.beans.PropertyChangeListener

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

    CreateOfferPresenter(ViewModel viewModel, CreateOfferViewModel createOfferViewModel){
        this.viewModel = viewModel
        this.createOfferViewModel = createOfferViewModel
    }

    @Override
    void createdNewOffer(Offer createdOffer) {
        //TODO implement
        throw new Exception("Method not implemented.")
    }

    @Override
    void calculatedPrice(double price) {
        this.createOfferViewModel.offerPrice = price
    }

    void successNotification(String notification) {
        //TODO implement
        throw new Exception("Method not implemented.")
    }

    @Override
    void failNotification(String notification) {
        //TODO implement
        throw new Exception("Method not implemented.")
    }

    @Override
    void showAvailableProducts(List<Product> availableProducts) {
        this.createOfferViewModel.sequencingProducts.clear()
        this.createOfferViewModel.managementProducts.clear()
        this.createOfferViewModel.primaryAnalysisProducts.clear()
        this.createOfferViewModel.secondaryAnalysisProducts.clear()
        this.createOfferViewModel.storageProducts.clear()

        availableProducts.each {
             ProductItemViewModel product = new ProductItemViewModel(0, it)
             if(it instanceof Sequencing){
                 this.createOfferViewModel.sequencingProducts.add(product)
             }
             else if(it instanceof ProjectManagement){
                 this.createOfferViewModel.managementProducts.add(product)
             }
             else if(it instanceof PrimaryAnalysis){
                 this.createOfferViewModel.primaryAnalysisProducts.add(product)
             }
             else if(it instanceof SecondaryAnalysis){
                 this.createOfferViewModel.secondaryAnalysisProducts.add(product)
             }
             else if(it instanceof DataStorage){
                 this.createOfferViewModel.storageProducts.add(product)
             }
         }
    }
}
