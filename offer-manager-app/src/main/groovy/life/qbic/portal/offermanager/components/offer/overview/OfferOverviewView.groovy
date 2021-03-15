package life.qbic.portal.offermanager.components.offer.overview

import com.vaadin.data.provider.DataProvider
import com.vaadin.data.provider.ListDataProvider
import com.vaadin.icons.VaadinIcons
import com.vaadin.server.FileDownloader
import com.vaadin.server.StreamResource
import com.vaadin.shared.data.sort.SortDirection
import com.vaadin.ui.Button
import com.vaadin.ui.FormLayout
import com.vaadin.ui.Grid
import com.vaadin.ui.HorizontalLayout
import com.vaadin.ui.Label
import com.vaadin.ui.ProgressBar
import com.vaadin.ui.UI
import com.vaadin.ui.VerticalLayout
import com.vaadin.ui.components.grid.HeaderRow
import com.vaadin.ui.themes.ValoTheme
import groovy.util.logging.Log4j2
import life.qbic.datamodel.dtos.business.Offer
import life.qbic.portal.offermanager.components.offer.overview.projectcreation.CreateProjectView
import life.qbic.portal.offermanager.dataresources.offers.OfferOverview
import life.qbic.business.offers.Currency
import life.qbic.portal.offermanager.components.GridUtils

import java.time.LocalDate

/**
 * A basic offer overview user interface.
 *
 * It presents basic available offer information, from which
 * the user can select and download one in PDF.
 *
 * @since 1.0.0
 */
@Log4j2
class OfferOverviewView extends FormLayout {

    final private OfferOverviewModel model

    final private OfferOverviewController offerOverviewController

    final private Grid<OfferOverview> overviewGrid

    final private Button downloadBtn

    final Button updateOfferBtn

    final private ProgressBar downloadSpinner

    private FileDownloader fileDownloader

    private CreateProjectView createProjectView

    private Button createProjectButton

    OfferOverviewView(OfferOverviewModel model,
                      OfferOverviewController offerOverviewController,
                      CreateProjectView createProjectView) {
        this.model = model
        this.offerOverviewController = offerOverviewController
        this.overviewGrid = new Grid<>()
        this.downloadBtn = new Button(VaadinIcons.DOWNLOAD)
        this.updateOfferBtn = new Button(VaadinIcons.EDIT)
        this.createProjectButton =  new Button("Create Project", VaadinIcons.PLUS_CIRCLE)
        this.downloadSpinner = new ProgressBar()
        this.createProjectView = createProjectView

        initLayout()
        setupGrid()
        setupListeners()
    }

    private void initLayout() {
        /*
        We start with the header, that contains a descriptive
        title of what the view is about.
         */
        final VerticalLayout headerRow = new VerticalLayout()
        final Label label = new Label("Available Offers")

        label.addStyleName(ValoTheme.LABEL_HUGE)
        headerRow.addComponent(label)
        headerRow.setMargin(false)
        this.addComponent(headerRow)

        /*
        Below the header, we create content row with two components.
        The left component will be the offer overview, the
        right component will be the offer download button.
         */
        final HorizontalLayout activityContainer = new HorizontalLayout()
        downloadBtn.setStyleName(ValoTheme.BUTTON_LARGE)
        downloadBtn.setEnabled(false)
        downloadBtn.setDescription("Download offer")
        updateOfferBtn.setStyleName(ValoTheme.BUTTON_LARGE)
        updateOfferBtn.setEnabled(false)
        updateOfferBtn.setDescription("Update offer")
        createProjectButton.setEnabled(false)
        createProjectButton.setStyleName(ValoTheme.BUTTON_LARGE)
        // Makes the progress bar a spinner
        downloadSpinner.setIndeterminate(true)
        downloadSpinner.setVisible(false)
        // Add a button to create a project from an offer
        activityContainer.addComponents(
                downloadBtn,
                updateOfferBtn,
                createProjectButton,
                downloadSpinner)

        activityContainer.setMargin(false)
        headerRow.addComponents(activityContainer,overviewGrid)
        headerRow.setSizeFull()

        this.setMargin(false)
        this.setSpacing(false)

        this.setWidthFull()
    }

    private DataProvider setupDataProvider() {
        def dataProvider = new ListDataProvider(model.offerOverviewList)
        overviewGrid.setDataProvider(dataProvider)
        return dataProvider
    }

    private void setupGrid() {
        def dateColumn = overviewGrid.addColumn({ overview -> overview.getModificationDate() })
                .setCaption("Creation Date").setId("CreationDate")
        overviewGrid.addColumn({overview -> overview.offerId.toString()})
                .setCaption("Offer ID").setId("OfferId")
        overviewGrid.addColumn({overview -> overview.getProjectTitle()})
                .setCaption("Project Title").setId("ProjectTitle")
        overviewGrid.addColumn({overview -> overview.getCustomer()})
                .setCaption("Customer").setId("Customer")
        // fix formatting of price
        overviewGrid.addColumn({overview -> Currency.getFormatterWithSymbol().format(overview.getTotalPrice())}).setCaption("Total Price")
        overviewGrid.sort(dateColumn, SortDirection.DESCENDING)
        overviewGrid.setWidthFull()

        def offerOverviewDataProvider = setupDataProvider()

        setupFilters(offerOverviewDataProvider)
    }

    private void setupFilters(ListDataProvider<OfferOverview> offerOverviewDataProvider) {
        HeaderRow customerFilterRow = overviewGrid.appendHeaderRow()
        GridUtils.setupColumnFilter(offerOverviewDataProvider,
                overviewGrid.getColumn("OfferId"),
                customerFilterRow)
        GridUtils.setupColumnFilter(offerOverviewDataProvider,
                overviewGrid.getColumn("ProjectTitle"),
                customerFilterRow)
        GridUtils.setupColumnFilter(offerOverviewDataProvider,
                overviewGrid.getColumn("Customer"),
                customerFilterRow)
    }

    private void setupListeners() {
        setupGridListeners()
        updateOfferBtn.addClickListener({
            model.offerEventEmitter.emit(model.getSelectedOffer())
        })
        createProjectButton.addClickListener({
            this.setVisible(false)
            createProjectView.setVisible(true)
            createProjectView.model.startedFromView = Optional.of(this)
            createProjectView.model.selectedOffer = Optional.of(model.selectedOffer)
        })
    }

    private void setupGridListeners() {
        overviewGrid.addSelectionListener(
                { selection ->
                    selection.firstSelectedItem.ifPresent({overview ->
                        UI.getCurrent().setPollInterval(50)
                        downloadSpinner.setVisible(true)
                        new LoadOfferInfoThread(UI.getCurrent(), overview).start()
                    })
                })
    }

    private void createResourceForDownload() {
        removeExistingResources()

        StreamResource offerResource =
                new StreamResource((StreamResource.StreamSource res) -> {
                    return model.getOfferAsPdf()
                }, FileNameFormatter.getFileNameForOffer(model.getSelectedOffer()))
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

            Optional<OfferOverview> selectedOffer = Optional.empty()
            ui.access(() -> {
                downloadSpinner.setVisible(true)
                overviewGrid.setEnabled(false)
                selectedOffer = overviewGrid.getSelectionModel().getFirstSelectedItem()
                overviewGrid.setSelectionMode(Grid.SelectionMode.NONE)
                downloadBtn.setEnabled(false)
                updateOfferBtn.setEnabled(false)
                createProjectButton.setEnabled(false)
            })
                offerOverviewController.fetchOffer(offerOverview.offerId)
                createResourceForDownload()

                ui.access(() -> {
                    downloadSpinner.setVisible(false)
                    overviewGrid.setSelectionMode(Grid.SelectionMode.SINGLE)
                    // After we have set the single mode to NONE, the listeners seem to be gone
                    // So we set them again
                    // IMPORTANT: the selection must be set before we attach the listener,
                    // otherwise the selection listener gets triggered (LOOP!)
                    overviewGrid.select(selectedOffer.get())
                    setupGridListeners()
                    overviewGrid.setEnabled(true)
                    downloadBtn.setEnabled(true)
                    updateOfferBtn.setEnabled(true)
                    createProjectButton.setEnabled(true)
                    ui.setPollInterval(-1)
                })
        }
    }

    private static class FileNameFormatter {

        /**
         * Returns an offer file name in this schema:
         *
         * Q_<year>_<month>_<day>_<project-conserved-part>_<random-id-part>_v<offer-version>.pdf
         * @param offer
         * @return
         */
        static String getFileNameForOffer(Offer offer) {
            LocalDate date = offer.modificationDate.toLocalDate()
            String dateString = createDateString(date)
            return "Q_${dateString}_" +
                    "${offer.identifier.projectConservedPart}_${offer.identifier.randomPart}_" +
                    "v${offer.identifier.version}.pdf"
        }

        private static String createDateString(LocalDate date) {
            return String.format("%04d_%02d_%02d", date.getYear(), date.getMonthValue(), date.getDayOfMonth())
        }

    }
}
