package life.qbic.portal.offermanager.dataresources.products

import life.qbic.business.products.list.ListProductsDataSource
import life.qbic.datamodel.dtos.business.services.Product
import life.qbic.portal.offermanager.communication.EventEmitter
import life.qbic.portal.offermanager.communication.Subscription
import life.qbic.portal.offermanager.dataresources.ResourcesService

/**
 * Product service that holds resources about available products
 *
 * This service holds a list of available products and exposes an
 * event emitter, that can be used to subscribe to any update
 * event of the underlying resource data.
 *
 * @since: 1.0.0
 */
class ProductsResourcesService implements ResourcesService<Product> {

    private final ListProductsDataSource listProductsDataSource
    private final List<Product> products
    /**
     * EventEmitter for products. Fires every time the resources are reloaded
     * @see #reloadResources
     */
    private final EventEmitter<Product> productEventEmitter


    /**
     * Constructor expecting a customer database connector
     * @param listProductsDataSource
     */
    ProductsResourcesService(ListProductsDataSource listProductsDataSource) {
        this.listProductsDataSource = listProductsDataSource
        this.products = new LinkedList<>()
        this.productEventEmitter = new EventEmitter<>()
        populateResources()
    }

    @Override
    void reloadResources() {
        products.clear()

        List<Product> updatedEntries = listProductsDataSource.listProducts()
        updatedEntries.each {
            addToResource(it)
        }
    }

    private void populateResources() {
        this.products.addAll(listProductsDataSource.listProducts())
    }

    @Override
    void addToResource(Product resourceItem) {
        this.products.add(resourceItem)
        productEventEmitter.emit(resourceItem)
    }

    @Override
    void removeFromResource(Product resourceItem) {
        this.products.remove(resourceItem)
        productEventEmitter.emit(resourceItem)
    }

    @Override
    void subscribe(Subscription<Product> subscription) {
        this.productEventEmitter.register(subscription)

    }

    @Override
    void unsubscribe(Subscription<Product> subscription) {
        this.productEventEmitter.unregister(subscription)
    }


    /**
     * @inheritdoc
     *
     * @return An iterator over the list of available products
     */
    @Override
    Iterator<Product> iterator() {
        return new ArrayList<>(this.products).iterator()
    }

    /**
     *
     * @return currently loaded available products
     * @deprecated please use {@link #iterator()} instead
     */
    @Deprecated
    List<Product> getProducts() {
        return new ArrayList<>(this.products)
    }
}
