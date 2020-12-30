package life.qbic.portal.qoffer2.web.views

import com.vaadin.icons.VaadinIcons
import com.vaadin.server.FileDownloader
import com.vaadin.server.StreamResource
import com.vaadin.shared.data.sort.SortDirection
import com.vaadin.ui.Button
import com.vaadin.ui.Grid
import com.vaadin.ui.HorizontalLayout
import com.vaadin.ui.Label
import com.vaadin.ui.VerticalLayout
import com.vaadin.ui.themes.ValoTheme
import groovy.util.logging.Log4j2
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

    private FileDownloader fileDownloader

    OverviewView(OfferOverviewModel model) {
        this.model = model
        this.overviewGrid = new Grid<>()
        this.downloadBtn = new Button(VaadinIcons.DOWNLOAD)
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
        final def headerRow = new HorizontalLayout()
        final def label = new Label("Available Offers")
        label.addStyleName(ValoTheme.LABEL_HUGE)
        headerRow.addComponent(label)
        this.addComponent(headerRow)

        /*
        Below the header, we create content row with two components.
        The left component will be the offer overview, the
        right component will be the offer download button.
         */
        final def overviewRow = new HorizontalLayout()
        downloadBtn.setStyleName(ValoTheme.BUTTON_LARGE)
        downloadBtn.setEnabled(false)
        overviewRow.addComponents(overviewGrid, downloadBtn)
        this.addComponent(overviewRow)

        overviewRow.setWidthFull()
        this.setWidthFull()
    }

    private void setupDataProvider() {
        overviewGrid.setItems(model.offerOverviewList)
    }

    private void setupGrid() {
        def dateColumn = overviewGrid.addColumn({ overview -> overview.getModificationDate() })
                .setCaption("Date")
        overviewGrid.addColumn({overview -> overview.getProjectTitle()}).setCaption("Title")
        overviewGrid.addColumn({overview -> overview.getCustomer()}).setCaption("Customer")
        overviewGrid.addColumn({overview -> overview.getTotalPrice()}).setCaption("Total Price")
        overviewGrid.sort(dateColumn, SortDirection.DESCENDING)
        overviewGrid.setWidthFull()
    }

    private void setupListeners() {
        overviewGrid.addSelectionListener(
                { selection ->
                    selection.firstSelectedItem.ifPresentOrElse({overview ->
                        model.selectedOffer = Optional.of(overview)
                        downloadBtn.setEnabled(true)
                        createResourceForDownload()
                    }, {
                        model.selectedOffer = Optional.empty()
                        downloadBtn.setEnabled(false)})
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
}
