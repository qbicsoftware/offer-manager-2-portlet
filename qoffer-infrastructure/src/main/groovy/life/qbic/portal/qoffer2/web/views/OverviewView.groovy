package life.qbic.portal.qoffer2.web.views

import com.vaadin.icons.VaadinIcons
import com.vaadin.server.FileDownloader
import com.vaadin.server.StreamResource
import com.vaadin.shared.data.sort.SortDirection
import com.vaadin.ui.Button
import com.vaadin.ui.Grid
import com.vaadin.ui.HorizontalLayout
import com.vaadin.ui.Label
import com.vaadin.ui.ProgressBar
import com.vaadin.ui.UI
import com.vaadin.ui.VerticalLayout
import com.vaadin.ui.themes.ValoTheme
import groovy.util.logging.Log4j2
import life.qbic.portal.qoffer2.services.OfferUpdateService
import life.qbic.portal.qoffer2.shared.OfferOverview
import life.qbic.portal.qoffer2.web.viewmodel.OfferOverviewModel

/**
 * A basic offer overview user interface.
 *
 * It presents basic available offer information, from which
 * the user can select and download one in PDF.
 *
 * @since 1.0.0
 */
@Log4j2
class OverviewView extends VerticalLayout {

    final private OfferOverviewModel model

    final private Grid<OfferOverview> overviewGrid

    final private Button downloadBtn

    final Button updateOfferBtn

    final private ProgressBar downloadSpinner

    private FileDownloader fileDownloader

    final private OfferUpdateService offerUpdateService

    OverviewView(OfferOverviewModel model, OfferUpdateService offerUpdateService) {
        this.model = model
        this.overviewGrid = new Grid<>()
        this.downloadBtn = new Button(VaadinIcons.DOWNLOAD)
        this.updateOfferBtn = new Button(VaadinIcons.EDIT)
        this.downloadSpinner = new ProgressBar()
        this.offerUpdateService = offerUpdateService

        initLayout()
        setupDataProvider()
        setupGrid()
        setupListeners()
    }

    private void initLayout() {
        /*
        We start with the header, that contains a descriptive
        title of what the view is about.
         */
        final HorizontalLayout headerRow = new HorizontalLayout()
        final Label label = new Label("Available Offers")

        label.addStyleName(ValoTheme.LABEL_HUGE)
        headerRow.addComponent(label)
        this.addComponent(headerRow)

        /*
        Below the header, we create content row with two components.
        The left component will be the offer overview, the
        right component will be the offer download button.
         */
        final HorizontalLayout overviewRow = new HorizontalLayout()
        final VerticalLayout activityContainer = new VerticalLayout()
        downloadBtn.setStyleName(ValoTheme.BUTTON_LARGE)
        downloadBtn.setEnabled(false)
        downloadBtn.setDescription("Download offer")
        updateOfferBtn.setStyleName(ValoTheme.BUTTON_LARGE)
        updateOfferBtn.setEnabled(false)
        updateOfferBtn.setDescription("Update offer")
        // Makes the progress bar a spinner
        downloadSpinner.setIndeterminate(true)
        downloadSpinner.setVisible(false)
        activityContainer.addComponents(downloadBtn, updateOfferBtn, downloadSpinner)

        activityContainer.setMargin(false)
        overviewRow.addComponents(overviewGrid, activityContainer)
        overviewRow.setSizeFull()
        overviewRow.setWidthFull()
        overviewRow.setExpandRatio(overviewGrid,0.95f)
        overviewRow.setExpandRatio(activityContainer, 0.05f)

        this.addComponent(overviewRow)
        this.setWidthFull()
    }

    private void setupDataProvider() {
        overviewGrid.setItems(model.offerOverviewList)
    }

    private void setupGrid() {
        def dateColumn = overviewGrid.addColumn({ overview -> overview.getModificationDate() })
                .setCaption("Date")
        dateColumn.setMinimumWidth(50)
        def idColumn = overviewGrid.addColumn({overview -> overview.offerId.toString()})
                .setCaption("Offer ID")
        idColumn.setMinimumWidth(50)
        def titleColumn = overviewGrid.addColumn({overview -> overview.getProjectTitle()}).setCaption("Title")
        titleColumn.setMinimumWidth(50)
        def customerColumn = overviewGrid.addColumn({overview -> overview.getCustomer()}).setCaption("Customer")
        customerColumn.setMinimumWidth(50)
        def priceColumn = overviewGrid.addColumn({overview -> overview.getTotalPrice()}).setCaption("Total Price")
        priceColumn.setWidth(110)

        overviewGrid.sort(dateColumn, SortDirection.DESCENDING)
        overviewGrid.setSizeFull()
    }

    private void setupListeners() {
        overviewGrid.addSelectionListener(
                { selection ->
                    selection.firstSelectedItem.ifPresentOrElse({overview ->
                        UI.getCurrent().setPollInterval(50)
                        downloadSpinner.setVisible(true)
                        new LoadOfferInfoThread(UI.getCurrent(), overview).start()
                    }, {})
                })
        updateOfferBtn.addClickListener({
            offerUpdateService.offerForUpdateEvent.emit(model.getSelectedOffer())
        })
    }

    private void createResourceForDownload() {
        removeExistingResources()
        StreamResource offerResource =
                new StreamResource((StreamResource.StreamSource res) -> {
                    return model.getOfferAsPdf()
                }, "myoffer.pdf")
        fileDownloader = new FileDownloader(offerResource)
        fileDownloader.extend(downloadBtn)
    }

    private void removeExistingResources() {
        if (fileDownloader) {
            downloadBtn.removeExtension(fileDownloader)
        }
    }

    private class LoadOfferInfoThread extends Thread {

        final private OfferOverview offerOverview

        final private UI ui

        LoadOfferInfoThread(UI ui, OfferOverview offerOverview) {
            this.ui = ui
            this.offerOverview = offerOverview
        }

        @Override
        void run() {
            ui.access(() -> {
                downloadSpinner.setVisible(true)
                overviewGrid.setEnabled(false)
                downloadBtn.setEnabled(false)
                updateOfferBtn.setEnabled(false)
            })

            model.setSelectedOffer(offerOverview)
            createResourceForDownload()

            ui.access(() -> {
                downloadSpinner.setVisible(false)
                overviewGrid.setEnabled(true)
                downloadBtn.setEnabled(true)
                updateOfferBtn.setEnabled(true)
                ui.setPollInterval(-1)
            })
        }
    }
}
