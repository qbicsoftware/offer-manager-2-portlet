package life.qbic.portal.qoffer2.web.viewmodel.create.offer

import groovy.beans.Bindable
import life.qbic.portal.qoffer2.web.viewmodel.ProductItemViewModel

/**
 * The view model for the {@link life.qbic.portal.qoffer2.web.views.create.offer.SelectItemsView}
 *
 * Describes the view components of the SelectItemsView
 *
 * @since: 0.1.0
 *
 */
class SelectItemsViewModel {

    List<ProductItemViewModel> sequencingProducts =  new ObservableList(new ArrayList<ProductItemViewModel>())
    List<ProductItemViewModel> primaryAnalysisProducts =  new ObservableList(new ArrayList<ProductItemViewModel>())
    List<ProductItemViewModel> secondaryAnalysisProducts =  new ObservableList(new ArrayList<ProductItemViewModel>())
    List<ProductItemViewModel> managementProducts =  new ObservableList(new ArrayList<ProductItemViewModel>())
    List<ProductItemViewModel> storageProducts =  new ObservableList(new ArrayList<ProductItemViewModel>())

    @Bindable List<ProductItemViewModel> selectedProductItems
}
