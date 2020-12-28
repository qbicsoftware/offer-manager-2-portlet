package life.qbic.portal.qoffer2.offers

import groovy.util.logging.Log4j2
import life.qbic.datamodel.dtos.business.Offer
import life.qbic.portal.portlet.offers.OfferExporter
import org.jsoup.nodes.Document
import org.jsoup.parser.Parser

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption


/**
 * Handles the convertion of offers to pdf files
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
        this.offer = null
        this.tempDir = Files.createTempDirectory("offer")
        this.createdOffer = Paths.get(tempDir.toString(), "offer.html")
        copyTemplate()
        this.htmlContent = Parser.xmlParser().parseInput(new File(this.createdOffer.toUri()).text, "")
        makeExampleManipulation()
    }

    private void copyTemplate() {
        Path newOfferImage = Paths.get(tempDir.toString(), "offer_header.png")
        Path newOfferStyle = Paths.get(tempDir.toString(), "stylesheet.css")
        Files.copy(OFFER_HTML_TEMPLATE, createdOffer, StandardCopyOption.REPLACE_EXISTING)
        Files.copy(OFFER_HEADER_IMAGE, newOfferImage, StandardCopyOption.REPLACE_EXISTING)
        Files.copy(OFFER_STYLESHEET, newOfferStyle, StandardCopyOption.REPLACE_EXISTING)
    }

    private void makeExampleManipulation() {
        this.htmlContent.getElementById("project-title").text("My new awesome project")
        new File(this.createdOffer.toUri()).withWriter {
            it.write(this.htmlContent.toString())
        }
    }

    OfferToPDFConverter(Offer offer) {
        this.offer = offer
    }

    InputStream getHTMLOutputStream() {
        return new BufferedInputStream(new FileInputStream(new File(this.createdOffer.toUri())))
    }
}
