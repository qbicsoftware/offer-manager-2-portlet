package life.qbic.portal.offermanager.components.offer.overview

import com.vaadin.data.provider.AbstractBackEndDataProvider
import com.vaadin.data.provider.ConfigurableFilterDataProvider
import com.vaadin.data.provider.DataProvider
import com.vaadin.data.provider.Query
import groovy.beans.Bindable
import life.qbic.business.offers.OfferContent
import life.qbic.datamodel.dtos.business.Offer
import life.qbic.datamodel.dtos.business.OfferId
import life.qbic.portal.offermanager.OfferToPDFConverter
import life.qbic.portal.offermanager.communication.EventEmitter
import life.qbic.portal.offermanager.communication.Observable
import life.qbic.portal.offermanager.components.AppViewModel
import life.qbic.portal.offermanager.dataresources.offers.OfferFilter
import life.qbic.portal.offermanager.dataresources.offers.OfferOverview
import life.qbic.portal.offermanager.dataresources.offers.OverviewService

import java.util.stream.Stream

/**
 * Model for the offer overview view.
 *
 * <p>Holds central properties for the offer overview view. The list of available offers is
 * exposed through a lazy {@link DataProvider} that only loads the currently requested page from
 * the underlying data source, so adding offers does not increase the initial page load time.</p>
 *
 * @since 1.0.0
 */
class OfferOverviewModel extends Observable {

    /**
     * A lazy, filterable data provider with all available latest offer overviews.
     */
    final ConfigurableFilterDataProvider<OfferOverview, Void, OfferFilter> overviewDataProvider

    /**
     * The offer versions that belong to the currently selected offer.
     */
    List<OfferOverview> offerVersionsForSelected

    OfferOverview selectedOverview

    Optional<Offer> offer = Optional.empty()

    Optional<OfferContent> offerContent = Optional.empty()

    private final OverviewService service

    private final AppViewModel viewModel

    @Bindable
    boolean displaySpinner

    EventEmitter offerEventEmitter

    OfferOverviewModel(OverviewService service,
                       AppViewModel viewModel,
                       EventEmitter<Offer> offerEventEmitter) {
        this.service = service
        this.offerVersionsForSelected = new ArrayList<>()
        this.offerContent = Optional.empty()
        this.viewModel = viewModel
        this.displaySpinner = false
        this.offerEventEmitter = offerEventEmitter
        this.overviewDataProvider = new OfferOverviewDataProvider(service).withConfigurableFilter()
        subscribeToOverviewService()
    }

    private void subscribeToOverviewService() {
        service.subscribe({
            overviewDataProvider.refreshAll()
            if (this.selectedOverview != null) {
                this.offerVersionsForSelected.clear()
                this.offerVersionsForSelected.addAll(loadVersionsForSelected())
            }
            this.setChanged()
            this.notifyObservers()
        })
    }

    Offer getSelectedOffer() {
        if (offer.isPresent()) {
            return offer.get()
        } else {
            throw new RuntimeException("No offer is currently selected.")
        }
    }

    void setSelectedOverview(OfferOverview offerOverview) {
        this.selectedOverview = offerOverview
        this.offerVersionsForSelected.clear()
        this.offerVersionsForSelected.addAll(loadVersionsForSelected())
        this.setChanged()
        this.notifyObservers()
    }

    private List<OfferOverview> loadVersionsForSelected() {
        OfferId familyId = this.selectedOverview.getOfferId()
        return service.fetchVersionsOfOffer(familyId)
    }

    /**
     * Acquire the current selected offer in PDF
     * @return The offer PDF
     * @throws RuntimeException if the offer cannot be converted to PDF
     */
    InputStream getOfferAsPdf() throws RuntimeException {
        offerContent.map({
            OfferToPDFConverter converter = new OfferToPDFConverter(it)
            return converter.getOfferAsPdf()
        }).orElseThrow({
            new RuntimeException("The offer content seems to be empty, nothing to " +
                    "convert.")
        })
    }

    /**
     * Lazy data provider that loads a page of latest offer overviews from the data source.
     */
    private static class OfferOverviewDataProvider extends AbstractBackEndDataProvider<OfferOverview, OfferFilter> {

        private final OverviewService service

        OfferOverviewDataProvider(OverviewService service) {
            this.service = service
        }

        @Override
        protected Stream<OfferOverview> fetchFromBackEnd(Query<OfferOverview, OfferFilter> query) {
            OfferFilter filter = query.getFilter().orElse(null)
            List<com.vaadin.data.provider.SortOrder<String>> sortOrders = query.getSortOrders()
            return service.fetchLatestOverviews(query.getOffset(), query.getLimit(), filter, sortOrders).stream()
        }

        @Override
        protected int sizeInBackEnd(Query<OfferOverview, OfferFilter> query) {
            OfferFilter filter = query.getFilter().orElse(null)
            return service.countLatestOverviews(filter)
        }

        @Override
        default boolean isInMemory() {
            return false;
        }
    }

}
