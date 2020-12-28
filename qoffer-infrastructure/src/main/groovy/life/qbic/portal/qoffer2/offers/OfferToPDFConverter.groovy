package life.qbic.portal.qoffer2.offers

import groovy.util.logging.Log4j2
import life.qbic.datamodel.dtos.business.Affiliation
import life.qbic.datamodel.dtos.business.Customer
import life.qbic.datamodel.dtos.business.Offer
import life.qbic.datamodel.dtos.business.ProjectManager
import life.qbic.portal.portlet.offers.OfferExporter
import org.jsoup.nodes.Document
import org.jsoup.parser.Parser

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption


/**
 * Handles the conversion of offers to pdf files
 *
 * Implements {@link OfferExporter} and is responsible for converting an offer into PDF Format
 *
 * @since: 1.0.0
 * @author: Jennifer Bödker
 *
 */
@Log4j2
class OfferToPDFConverter implements OfferExporter{

    final private Offer offer

    final private Path tempDir

    final private Document htmlContent

    private Path createdOffer

    final private Path OFFER_HTML_TEMPLATE =
            Paths.get(getClass().getClassLoader()
                    .getResource("offer-template/offer.html")
                    .toURI())
    final private Path OFFER_HEADER_IMAGE =
            Paths.get(getClass().getClassLoader()
                    .getResource("offer-template/offer_header.png")
                    .toURI())
    final private Path OFFER_STYLESHEET =
            Paths.get(getClass().getClassLoader()
                    .getResource("offer-template/stylesheet.css")
                    .toURI())

    OfferToPDFConverter(){
        this.tempDir = Files.createTempDirectory("offer")
        this.createdOffer = Paths.get(tempDir.toString(), "offer.html")
        copyTemplate()
        this.htmlContent = Parser.xmlParser().parseInput(new File(this.createdOffer.toUri()).text, "")
    }

    private void copyTemplate() {
        Path newOfferImage = Paths.get(tempDir.toString(), "offer_header.png")
        Path newOfferStyle = Paths.get(tempDir.toString(), "stylesheet.css")
        Files.copy(OFFER_HTML_TEMPLATE, createdOffer, StandardCopyOption.REPLACE_EXISTING)
        Files.copy(OFFER_HEADER_IMAGE, newOfferImage, StandardCopyOption.REPLACE_EXISTING)
        Files.copy(OFFER_STYLESHEET, newOfferStyle, StandardCopyOption.REPLACE_EXISTING)
    }

    private void writeContentToFile() {
        new File(this.createdOffer.toUri()).withWriter {
            it.write(this.htmlContent.toString())
            it.flush()
        }
    }

    OfferToPDFConverter(Offer offer) {
        this()
        this.offer = offer
        fillTemplateWithOfferContent()
        writeContentToFile()
    }

    private void fillTemplateWithOfferContent() {
        setProjectInformation()
        setCustomerInformation()
        setManagerInformation()
        setSelectedItems()
        setPrices()
    }

    InputStream getHTMLOutputStream() {
        return new BufferedInputStream(new FileInputStream(new File(this.createdOffer.toUri())))
    }

    void setProjectInformation() {
        htmlContent.getElementById("project-title").text(offer.projectTitle)
        htmlContent.getElementById("project-objective").text(offer.projectDescription)
    }

    void setCustomerInformation() {
        final Customer customer = offer.customer
        final Affiliation affiliation = offer.selectedCustomerAffiliation
        htmlContent.getElementById("cName").text(String.format(
                "%s %s %s",
                customer.title,
                customer.firstName,
                customer.lastName))
        htmlContent.getElementById("cOrganisation").text(affiliation.organisation)
        htmlContent.getElementById("cStreet").text(affiliation.street)
        htmlContent.getElementById("cPostalCode").text(affiliation.postalCode)
        htmlContent.getElementById("cCity").text(affiliation.city)
        htmlContent.getElementById("cCountry").text(affiliation.country)
    }

    void setManagerInformation() {
        final ProjectManager pm = offer.projectManager
        final Affiliation affiliation = pm.affiliations.get(0)
        htmlContent.getElementById("pmName").text(String.format(
                "%s %s %s",
                pm.title,
                pm.firstName,
                pm.lastName))
        htmlContent.getElementById("pmStreet").text(affiliation.street)
        htmlContent.getElementById("pmCity").text("${affiliation.postalCode} ${affiliation.city}")
        htmlContent.getElementById("pmEmail").text(pm.emailAddress)
    }

    void setSelectedItems() {}

    void setPrices() {}
}
