package life.qbic.portal.offermanager.components.product

import groovy.beans.Bindable
import life.qbic.datamodel.dtos.business.services.Product
import life.qbic.datamodel.dtos.general.Person
import life.qbic.portal.offermanager.communication.EventEmitter
import life.qbic.portal.offermanager.dataresources.products.ProductsResourcesService


/**
 * <h1>A ViewModel holding data that is presented in a
 * {@link life.qbic.portal.offermanager.components.product.MaintainProductsView}</h1>
 *
 * <p>This class holds all specific fields that are mutable in the view
 * Whenever values change it should be reflected in the corresponding view. This class can be used
 * for UI unit testing purposes.</p>
 *
 * <p>This class can contain JavaBean objects to enable views to listen to changes in the values.</p>
 *
 * @since 1.0.0
 *
 */
class MaintainProductsViewModel {

    ObservableList products = new ObservableList(new ArrayList<Product>())

    @Bindable Optional<Product> selectedProduct

    final ProductsResourcesService productsResourcesService
    EventEmitter<Product> productUpdate

    MaintainProductsViewModel(ProductsResourcesService productsResourcesService, EventEmitter<Product> productUpdate) {
        this.productsResourcesService = productsResourcesService
        this.productUpdate = productUpdate
        fetchProducts()
        subscribe()
    }

    private void fetchProducts(){
        products.addAll(productsResourcesService.iterator())
    }

    private void subscribe(){
        productsResourcesService.subscribe({ product ->
            refreshList()
        })
    }

    private void refreshList(){
        products.clear()
        products.addAll(productsResourcesService.iterator().toList())
    }

}