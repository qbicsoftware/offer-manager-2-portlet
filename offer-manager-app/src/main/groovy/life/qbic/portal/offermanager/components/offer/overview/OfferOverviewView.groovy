package life.qbic.portal.offermanager.components.offer.overview

import com.vaadin.data.provider.DataProvider
import com.vaadin.data.provider.ListDataProvider
import com.vaadin.event.selection.SelectionEvent
import com.vaadin.icons.VaadinIcons
import com.vaadin.server.FileDownloader
import com.vaadin.server.StreamResource
import com.vaadin.shared.data.sort.SortDirection
import com.vaadin.ui.*
import com.vaadin.ui.Grid.Column
import com.vaadin.ui.components.grid.HeaderRow
import com.vaadin.ui.renderers.DateRenderer
import com.vaadin.ui.renderers.TextRenderer
import com.vaadin.ui.themes.ValoTheme
import groovy.util.logging.Log4j2
import life.qbic.business.offers.Currency
import life.qbic.portal.offermanager.OfferFileNameFormatter
import life.qbic.portal.offermanager.components.GridUtils
import life.qbic.portal.offermanager.components.offer.overview.projectcreation.CreateProjectView
import life.qbic.portal.offermanager.components.project.ProjectIdContainsString
import life.qbic.portal.offermanager.dataresources.offers.OfferOverview

/**
 * A basic offer overview user interface.
 *
 * It presents basic available offer information, from which
 * the user can select and download one in PDF.
 *
 * @since 1.0.0
 */
@Log4j2
class OfferOverviewView extends VerticalLayout {

    final private OfferOverviewModel model

    final private OfferOverviewController offerOverviewController

    final private Grid<OfferOverview> overviewGrid

    final private Button downloadBtn

    final Button updateOfferBtn

    final private ProgressBar downloadSpinner

    private FileDownloader fileDownloader

    private CreateProjectView createProjectView

    private Button createProjectButton

    private FormLayout defaultContent

    OfferOverviewView(OfferOverviewModel model,
                      OfferOverviewController offerOverviewController,
                      CreateProjectView createProjectView) {
        this.model = model
        this.offerOverviewController = offerOverviewController
        this.overviewGrid = new Grid<>()
        this.downloadBtn = new Button("Download Offer", VaadinIcons.DOWNLOAD)
        this.updateOfferBtn = new Button("Update Offer", VaadinIcons.EDIT)
        this.createProjectButton = new Button("Create Project", VaadinIcons.PLUS_CIRCLE)
        this.downloadSpinner = new ProgressBar()
        this.createProjectView = createProjectView

        initLayout()
        setupGrid()
        setupListeners()
    }

    private void initLayout() {
        this.setMargin(false)
        this.setSpacing(false)
        this.addComponent(createProjectView)
        createProjectView.setVisible(false)
        defaultContent = generateDefaultLayout()
        this.addComponent(defaultContent)
        defaultContent.setVisible(true)
    }

    private FormLayout generateDefaultLayout() {
        FormLayout defaultContent = new FormLayout()
        /*
        We start with the header, that contains a descriptive
        title of what the view is about.
         */
        final VerticalLayout headerRow = new VerticalLayout()
        final Label label = new Label("Available Offers")

        label.addStyleName(ValoTheme.LABEL_HUGE)
        headerRow.addComponent(label)
        headerRow.setMargin(false)
        defaultContent.addComponent(headerRow)

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
        headerRow.addComponents(activityContainer, overviewGrid)
        headerRow.setSizeFull()

        defaultContent.setMargin(false)
        defaultContent.setSpacing(false)

        defaultContent.setWidthFull()
        return defaultContent
    }


    private DataProvider setupDataProvider() {
        def dataProvider = new ListDataProvider(model.offerOverviewList)
        overviewGrid.setDataProvider(dataProvider)
        return dataProvider
    }

    private void setupGrid() {
        Column<OfferOverview, Date> dateColumn = overviewGrid.addColumn({ overview ->
            overview
                    .getModificationDate()
        }).setCaption("Creation Date").setId("CreationDate")
        dateColumn.setRenderer(date -> date, new DateRenderer('%1$tY-%1$tm-%1$td'))
        overviewGrid.addColumn({ overview -> overview.offerId.toString() })
                .setCaption("Offer ID").setId("OfferId")
        overviewGrid.addColumn({ overview -> overview.getProjectTitle() })
                .setCaption("Project Title").setId("ProjectTitle")
        overviewGrid.addColumn({ overview -> overview.getCustomer() })
                .setCaption("Customer").setId("Customer")
        overviewGrid.addColumn({ overview -> overview.getProjectManager() })
                .setCaption("ProjectManager").setId("ProjectManager")
        overviewGrid.addColumn({ overview -> overview.getAssociatedProject() })
                .setCaption("Project ID").setId("ProjectID")
                .setRenderer({ maybeIdentifier -> maybeIdentifier.isPresent() ? maybeIdentifier.get().toString() : "-" }, new TextRenderer())

        // Format price by using a column renderer. This way the sorting will happen on the underlying double values, leading to expected behaviour.
        Column<OfferOverview, Double> priceColumn = overviewGrid.addColumn({ overview -> overview.getTotalPrice() }).setCaption("Total Price")
        priceColumn.setRenderer(price -> Currency.getFormatterWithSymbol().format(price), new TextRenderer())

        overviewGrid.sort(dateColumn, SortDirection.DESCENDING)
        overviewGrid.setWidthFull()

        def offerOverviewDataProvider = setupDataProvider()

        setupFilters(offerOverviewDataProvider)
    }

    private void setupFilters(ListDataProvider<OfferOverview> offerOverviewDataProvider) {
        HeaderRow headerFilterRow = overviewGrid.appendHeaderRow()

        GridUtils.setupColumnFilter(offerOverviewDataProvider,
                overviewGrid.getColumn("OfferId"),
                headerFilterRow)
        GridUtils.setupColumnFilter(offerOverviewDataProvider,
                overviewGrid.getColumn("ProjectTitle"),
                headerFilterRow)
        GridUtils.setupColumnFilter(offerOverviewDataProvider,
                overviewGrid.getColumn("Customer"),
                headerFilterRow)
        GridUtils.setupColumnFilter(offerOverviewDataProvider,
                overviewGrid.getColumn("ProjectManager"),
                headerFilterRow)
        GridUtils.setupDateColumnFilter(offerOverviewDataProvider,
                overviewGrid.getColumn("CreationDate"),
                headerFilterRow)
        GridUtils.setupColumnFilter(offerOverviewDataProvider,
                overviewGrid.getColumn("ProjectID"), new ProjectIdContainsString(),
                headerFilterRow)
    }

    private void setupListeners() {
        setupGridListeners()
        updateOfferBtn.addClickListener({
            model.offerEventEmitter.emit(model.getSelectedOffer())
        })
        createProjectButton.addClickListener({
            defaultContent.setVisible(false)
            createProjectView.setVisible(true)
            createProjectView.model.startedFromView = Optional.of(defaultContent)
            createProjectView.model.selectedOffer = Optional.of(model.selectedOffer)
        })
    }

    private void setupGridListeners() {
        overviewGrid.addSelectionListener({ selection ->handleSelection(selection)}
        )
    }

    private void handleSelection(SelectionEvent<OfferOverview> selection) {
        selection.firstSelectedItem.ifPresent(this::selectOfferOverview)
        if (!selection.firstSelectedItem.isPresent()) {
            deselectOfferOverview()
        }
    }

    private void deselectOfferOverview() {
        updateOfferBtn.setEnabled(false)
        downloadBtn.setEnabled(false)
        createProjectButton.setEnabled(false)
    }

    private void selectOfferOverview(OfferOverview overview) {
        UI.getCurrent().setPollInterval(50)
        downloadSpinner.setVisible(true)
        new LoadOfferInfoThread(UI.getCurrent(), overview).start()
        downloadBtn.setEnabled(true)
        updateOfferBtn.setEnabled(true)
        checkProjectCreationAllowed(overview)
    }

    private void checkProjectCreationAllowed(OfferOverview overview) {
        if (overview.associatedProject.isPresent()) {
            createProjectButton.setEnabled(false)
        } else {
            createProjectButton.setEnabled(true)
        }
    }

    private void createResourceForDownload() {
        removeExistingResources()

        StreamResource offerResource =
                new StreamResource((StreamResource.StreamSource res) -> {
                    return model.getOfferAsPdf()
                }, OfferFileNameFormatter.getFileNameForOffer(model.getSelectedOffer()))
        fileDownloader = new FileDownloader(offerResource)
        fileDownloader.extend(downloadBtn)
    }

    private void removeExistingResources() {
        Optional.ofNullable(fileDownloader).ifPresent({
            if (downloadBtn.extensions.contains(fileDownloader)) {
                downloadBtn.removeExtension(fileDownloader)
            }
        })
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
            })
            offerOverviewController.fetchOffer(offerOverview.offerId)
            createResourceForDownload()

            ui.access(() -> {
                downloadSpinner.setVisible(false)
                overviewGrid.setEnabled(true)
                ui.setPollInterval(-1)
            })
        }
    }

}
