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
import life.qbic.business.offers.identifier.OfferIdFormatter
import life.qbic.portal.offermanager.ExportAllOffers
import life.qbic.portal.offermanager.OfferFileNameFormatter
import life.qbic.portal.offermanager.components.GridUtils
import life.qbic.portal.offermanager.components.offer.overview.projectcreation.CreateProjectView
import life.qbic.portal.offermanager.components.project.ProjectIdContainsString
import life.qbic.portal.offermanager.dataresources.offers.OfferOverview

import java.time.LocalDate
import java.util.function.Supplier

/**
 * A basic offer overview user interface.
 *
 * It presents basic available offer information, from which
 * the user can select and download one in PDF.
 *
 * @since 1.0.0
 */
@Log4j2
class OfferOverviewView extends VerticalLayout implements Observer {

    final private OfferOverviewModel model

    final private OfferOverviewController offerOverviewController

    final private Grid<OfferOverview> overviewGrid

    final private Grid<OfferOverview> overviewVersionsGrid

    final private Button downloadOfferBtn

    final Button updateOfferBtn

    final private ProgressBar downloadSpinner

    private FileDownloader offerFileDownloader

    private CreateProjectView createProjectView

    private Button createProjectButton

    private FormLayout defaultContent

    private Button toggleOverview

    private Button toggleVersions

    private final Button exportAllOffersToTsvButton


    OfferOverviewView(OfferOverviewModel model,
                      OfferOverviewController offerOverviewController,
                      CreateProjectView createProjectView,
                      ExportAllOffers exportAllOffers) {
        this.model = model
        this.offerOverviewController = offerOverviewController
        this.overviewGrid = new Grid<>()
        this.overviewVersionsGrid = new Grid<>()
        this.downloadOfferBtn = new Button("Download Offer", VaadinIcons.DOWNLOAD)
        this.updateOfferBtn = new Button("Update Offer", VaadinIcons.EDIT)
        this.createProjectButton = new Button("Create Project", VaadinIcons.PLUS_CIRCLE)
        this.toggleOverview = new Button("Overview")
        this.toggleVersions = new Button("Versions")
        this.downloadSpinner = new ProgressBar()
        this.createProjectView = createProjectView
        // Register this view to be notified on updates in the model
        this.model.addObserver(this)

        this.exportAllOffersToTsvButton = new Button("Export to TSV", VaadinIcons.DOWNLOAD)
        downloadStreamWhenClickingButton(exportAllOffersToTsvButton, exportAllOffers::exportOffersToTsv)

        initLayout()

        configureOverviewGrid()
        configureOverviewVersionsGrid()

        setupListeners()
        setupToggleView()
    }

    void setupToggleView() {
        this.overviewVersionsGrid.setVisible(false)
        this.toggleVersions.setEnabled(false)
        this.toggleOverview.setEnabled(false)

        this.toggleVersions.addClickListener({
            overviewGrid.setVisible(false)
            overviewVersionsGrid.setVisible(true)
            toggleVersions.setEnabled(false)
            toggleOverview.setEnabled(true)
        })
        this.toggleOverview.addClickListener({
            overviewGrid.setVisible(true)
            overviewVersionsGrid.setVisible(false)
            toggleVersions.setEnabled(true)
            toggleOverview.setEnabled(false)
        })
    }

    void configureOverviewGrid() {
        DataProvider<OfferOverview, ?> dataProvider = new ListDataProvider(model.latestOfferOverviewList)
        setupGrid(this.overviewGrid, dataProvider)
        setupFilters(dataProvider, this.overviewGrid)
    }

    void configureOverviewVersionsGrid() {
        DataProvider<OfferOverview, ?> dataProvider = new ListDataProvider(model.offerVersionsForSelected)
        setupGrid(this.overviewVersionsGrid, dataProvider)
        setupFilters(dataProvider, this.overviewVersionsGrid)
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
        exportAllOffersToTsvButton.setStyleName(ValoTheme.BUTTON_LARGE)
        exportAllOffersToTsvButton.setEnabled(true)
        exportAllOffersToTsvButton.setDescription("Download Offers as TSV")
        downloadOfferBtn.setStyleName(ValoTheme.BUTTON_LARGE)
        downloadOfferBtn.setEnabled(false)
        downloadOfferBtn.setDescription("Download offer")
        updateOfferBtn.setStyleName(ValoTheme.BUTTON_LARGE)
        updateOfferBtn.setEnabled(false)
        updateOfferBtn.setDescription("Update offer")
        createProjectButton.setEnabled(false)
        createProjectButton.setStyleName(ValoTheme.BUTTON_LARGE)
        // Makes the progress bar a spinner
        downloadSpinner.setIndeterminate(true)
        downloadSpinner.setVisible(false)

        HorizontalLayout toggleLayout = new HorizontalLayout(toggleOverview,
                toggleVersions)

        styleToggleLayout(toggleLayout)
        styleToggleButtons()

        // Add a button to create a project from an offer
        activityContainer.addComponents(
                downloadOfferBtn,
                updateOfferBtn,
                createProjectButton,
                exportAllOffersToTsvButton,
                downloadSpinner)

        activityContainer.setMargin(false)
        activityContainer.setComponentAlignment(downloadSpinner, Alignment.MIDDLE_CENTER)

        HorizontalLayout wrapperLayout = new HorizontalLayout(activityContainer, toggleLayout)

        wrapperLayout.setComponentAlignment(toggleLayout, Alignment.MIDDLE_RIGHT)
        wrapperLayout.setWidthFull()

        headerRow.addComponents(wrapperLayout, overviewGrid, overviewVersionsGrid)
        headerRow.setSizeFull()

        defaultContent.setMargin(false)
        defaultContent.setSpacing(false)

        defaultContent.setWidthFull()
        return defaultContent
    }

    private void styleToggleLayout(HorizontalLayout toggleLayout) {
        toggleLayout.setSpacing(false)
        toggleLayout.setStyleName("card")
    }

    private void styleToggleButtons() {
        toggleVersions.setStyleName(ValoTheme.BUTTON_PRIMARY)
        toggleOverview.setStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED)
    }

    private static void setupGrid(Grid<? extends OfferOverview> grid, DataProvider dataProvider) {
        Column<OfferOverview, Date> dateColumn = grid.addColumn({ overview ->
            overview
                    .getModificationDate()
        }).setCaption("Creation Date").setId("CreationDate")
        dateColumn.setRenderer(date -> date, new DateRenderer('%1$tY-%1$tm-%1$td'))
        grid.addColumn({ overview -> OfferIdFormatter.formatAsOfferId(overview.offerId) })
                .setCaption("Offer ID").setId("OfferId")
        grid.addColumn({ overview -> overview.getProjectTitle() })
                .setCaption("Project Title").setId("ProjectTitle")
        grid.addColumn({ overview -> overview.getCustomer() })
                .setCaption("Customer").setId("Customer")
        grid.addColumn({ overview -> overview.getProjectManager() })
                .setCaption("ProjectManager").setId("ProjectManager")
        grid.addColumn({ overview -> overview.getAssociatedProject() })
                .setCaption("Project ID").setId("ProjectID")
                .setRenderer({ maybeIdentifier -> maybeIdentifier.isPresent() ? maybeIdentifier.get().toString() : "-" }, new TextRenderer())

        // Format price by using a column renderer. This way the sorting will happen on the underlying double values, leading to expected behaviour.
        Column<OfferOverview, Double> priceColumn = grid.addColumn({ overview -> overview.getTotalPrice() }).setCaption("Total Price")
        priceColumn.setRenderer(price -> Currency.getFormatterWithSymbol().format(price), new TextRenderer())

        grid.setDataProvider(dataProvider)

        grid.sort(dateColumn, SortDirection.DESCENDING)
        grid.setWidthFull()
    }

    private static void setupFilters(ListDataProvider<OfferOverview> offerOverviewDataProvider, Grid<? extends OfferOverview> grid) {
        HeaderRow headerFilterRow = grid.appendHeaderRow()

        GridUtils.setupColumnFilter(offerOverviewDataProvider,
                grid.getColumn("OfferId"),
                headerFilterRow)
        GridUtils.setupColumnFilter(offerOverviewDataProvider,
                grid.getColumn("ProjectTitle"),
                headerFilterRow)
        GridUtils.setupColumnFilter(offerOverviewDataProvider,
                grid.getColumn("Customer"),
                headerFilterRow)
        GridUtils.setupColumnFilter(offerOverviewDataProvider,
                grid.getColumn("ProjectManager"),
                headerFilterRow)
        GridUtils.setupDateColumnFilter(offerOverviewDataProvider,
                grid.getColumn("CreationDate"),
                headerFilterRow)
        GridUtils.setupColumnFilter(offerOverviewDataProvider,
                grid.getColumn("ProjectID"), new ProjectIdContainsString(),
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

        toggleOverview.addClickListener({
            toggleVersions.setStyleName(ValoTheme.BUTTON_PRIMARY)
            toggleOverview.setStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED)
        })

        toggleVersions.addClickListener({
            toggleOverview.setStyleName(ValoTheme.BUTTON_PRIMARY)
            toggleVersions.setStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED)
        })

    }

    private void setupGridListeners() {
        overviewGrid.addSelectionListener({ selection -> handleSelection(selection) }
        )
        overviewVersionsGrid.addSelectionListener({ selection -> handleSelection(selection) }
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
        downloadOfferBtn.setEnabled(false)
        createProjectButton.setEnabled(false)
        toggleVersions.setEnabled(false)
    }

    private void selectOfferOverview(OfferOverview overview) {
        // Inform the model about the selection
        model.setSelectedOverview(overview)
        UI.getCurrent().setPollInterval(50)
        downloadSpinner.setVisible(true)
        new LoadOfferInfoThread(UI.getCurrent(), overview).start()
        downloadOfferBtn.setEnabled(true)
        updateOfferBtn.setEnabled(true)
        toggleVersions.setEnabled(true)
        checkProjectCreationAllowed(overview)
    }

    private void checkProjectCreationAllowed(OfferOverview overview) {
        if (overview.associatedProject.isPresent()) {
            createProjectButton.setEnabled(false)
        } else {
            createProjectButton.setEnabled(true)
        }
    }

    private void createResourceForOfferPdfDownload() {
        removeExistingResourcesForOfferPdfDownload()

        StreamResource offerResource =
                new StreamResource((StreamResource.StreamSource res) -> {
                    return model.getOfferAsPdf()
                }, OfferFileNameFormatter.getFileNameForOffer(model.getSelectedOffer()))
        offerFileDownloader = new FileDownloader(offerResource)
        offerFileDownloader.extend(downloadOfferBtn)
    }

    private static void downloadStreamWhenClickingButton(AbstractComponent button, Supplier<InputStream> inputStreamSupplier) {
        StreamResource offerTsvResource = new StreamResource((resource) -> inputStreamSupplier.get(),
                LocalDate.now().toString() + ".offer-export" + ".tsv")
        // avoid browser caching file by name
        offerTsvResource.setCacheTime(0)
        new FileDownloader(offerTsvResource).extend(button)
    }

    private void removeExistingResourcesForOfferPdfDownload() {
        Optional.ofNullable(offerFileDownloader).ifPresent({
            if (downloadOfferBtn.extensions.contains(offerFileDownloader)) {
                downloadOfferBtn.removeExtension(offerFileDownloader)
            }
        })
    }

    @Override
    void update(Observable o, Object arg) {
        //todo
        this.overviewVersionsGrid.getDataProvider().refreshAll()
        this.overviewGrid.getDataProvider().refreshAll()
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
            createResourceForOfferPdfDownload()

            ui.access(() -> {
                downloadSpinner.setVisible(false)
                overviewGrid.setEnabled(true)
                ui.setPollInterval(-1)
            })
        }
    }

}
