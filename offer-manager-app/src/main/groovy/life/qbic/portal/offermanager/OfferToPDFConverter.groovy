package life.qbic.portal.offermanager

import groovy.util.logging.Log4j2
import life.qbic.business.offers.OfferContent
import life.qbic.business.offers.OfferExporter
import life.qbic.business.offers.content.CreateOfferContent
import life.qbic.business.offers.content.CreateOfferContentOutput
import life.qbic.business.offers.fetch.FetchOfferDataSource
import life.qbic.datamodel.dtos.business.*
import life.qbic.portal.offermanager.offergeneration.OfferHTMLDocument

import org.jsoup.nodes.Document
import org.jsoup.parser.Parser

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

import java.util.concurrent.TimeUnit

/**
 * Handles the conversion of offers to pdf files
 *
 * Implements {@link OfferExporter} and is responsible for converting an offer into PDF Format
 *
 * @since 1.0.0
 * @author Jennifer Bödker
 *
 */
@Log4j2
class OfferToPDFConverter implements OfferExporter, CreateOfferContentOutput {

    /**
     * The environment variable that is resolved when
     * the chromium browser needs to be executed
     * for the PDF conversion.
     *
     * This variable's value must contain the chromium browser
     * alias that can be executed from the system's command
     * line.
     */
    static final CHROMIUM_EXECUTABLE = "CHROMIUM_EXECUTABLE"

    private final Offer offer

    private final CreateOfferContent createOfferContent

    private final Path tempDir

    private final Document htmlContent

    private final Path createdOffer

    private final Path createdOfferPdf

    private final Path newOfferImage

    private final Path newOfferStyle

    private static final Path OFFER_HTML_TEMPLATE =
            Paths.get(OfferToPDFConverter.class.getClassLoader()
                    .getResource("offer-template/offer.html")
                    .toURI())
    private static final Path OFFER_HEADER_IMAGE =
            Paths.get(OfferToPDFConverter.class.getClassLoader()
                    .getResource("offer-template/offer_header.png")
                    .toURI())
    private static final Path OFFER_STYLESHEET =
            Paths.get(OfferToPDFConverter.class.getClassLoader()
                    .getResource("offer-template/stylesheet.css")
                    .toURI())

    @Deprecated
    OfferToPDFConverter(Offer offer) {
        this.offer = Objects.requireNonNull(offer, "Offer object must not be a null reference")
        this.tempDir = Files.createTempDirectory("offer")
        this.createdOffer = Paths.get(tempDir.toString(), "offer.html")
        this.newOfferImage = Paths.get(tempDir.toString(), "offer_header.png")
        this.newOfferStyle = Paths.get(tempDir.toString(), "stylesheet.css")
        this.createdOfferPdf = Paths.get(tempDir.toString(), "offer.pdf")
        importTemplate()
        this.htmlContent = Parser.xmlParser().parseInput(new File(this.createdOffer.toUri()).text, "")
        fillTemplateWithOfferContent()
        writeHTMLContentToFile(this.createdOffer, this.htmlContent)
        generatePDF()
    }

    OfferToPDFConverter(FetchOfferDataSource ds) {
        this.createOfferContent = new CreateOfferContent(this,ds)
        this.tempDir = Files.createTempDirectory("offer")
        this.createdOffer = Paths.get(tempDir.toString(), "offer.html")
        this.newOfferImage = Paths.get(tempDir.toString(), "offer_header.png")
        this.newOfferStyle = Paths.get(tempDir.toString(), "stylesheet.css")
        this.createdOfferPdf = Paths.get(tempDir.toString(), "offer.pdf")
        importTemplate()
    }

    /**
     * Converts an offer to a pdf based on its offerid
     * @param offerId which identifies the offer to be converted
     * @return returns the pdf as an Inputstream
     */
    InputStream convertOfferToPDF(OfferId offerId){
        createOfferContent.createOfferContent(offerId)
        return getOfferAsPdf()
    }

    InputStream getOfferAsPdf() {
        return new BufferedInputStream(new FileInputStream(new File(createdOfferPdf.toString())))
    }

    private void importTemplate() {
        Files.copy(OFFER_HTML_TEMPLATE, createdOffer, StandardCopyOption.REPLACE_EXISTING)
        Files.copy(OFFER_HEADER_IMAGE, newOfferImage, StandardCopyOption.REPLACE_EXISTING)
        Files.copy(OFFER_STYLESHEET, newOfferStyle, StandardCopyOption.REPLACE_EXISTING)
    }

    private static void writeHTMLContentToFile(Path fileLocation, Document htmlContent) {
        new File(fileLocation.toUri()).withWriter {
            it.write(htmlContent.toString())
            it.flush()
        }
    }

    @Deprecated
    private void fillTemplateWithOfferContent() {
        OfferHTMLDocument htmlDocument = new OfferHTMLDocument(htmlContent, offer)
        htmlDocument.fillTemplateWithOfferContent()
    }

    private void fillTemplateWithOfferContent(Document htmlContent, OfferContent offerContent) {
        OfferHTMLDocument htmlDocument = new OfferHTMLDocument(htmlContent, offer)//todo transfer the offercontent here
        htmlDocument.fillTemplateWithOfferContent()
    }

    private void generatePDF() {
        PdfPrinter pdfPrinter = new PdfPrinter(createdOffer)
        pdfPrinter.print(createdOfferPdf)
    }

    @Override
    void createdOfferContent(OfferContent offerContent) {
        Document htmlContent = Parser.xmlParser().parseInput(new File(this.createdOffer.toUri()).text, "")
        fillTemplateWithOfferContent(htmlContent, offerContent)
        writeHTMLContentToFile(this.createdOffer, htmlContent)
        generatePDF()
    }

    @Override
    void failNotification(String notification) {

    }
/**
     * Small helper class to handle the HTML to PDF conversion.
     */
    class PdfPrinter {

        final Path sourceFile

        final String chromeAlias

        PdfPrinter(Path sourceFile) {
            this.sourceFile = sourceFile
            this.chromeAlias = Objects.requireNonNull(System.getenv(CHROMIUM_EXECUTABLE),
                    "CHROMIUM_EXECUTABLE environment was not set.")
        }

        PdfPrinter(Path sourceFile, String alias) {
            this.sourceFile = sourceFile
            this.chromeAlias = alias
        }

        void print(Path outputFile) {
            final Path output = outputFile
            ProcessBuilder builder = new ProcessBuilder()
            builder.command(chromeAlias,
                    "--headless",
                    "--disable-gpu",
                    "--aggressive-cache-discard",
                    "--print-to-pdf-no-header",
                    "--print-to-pdf=${output.toString()}",
                    "${sourceFile}")
            builder.directory(new File(sourceFile.getParent().toString()))
            builder.redirectErrorStream(true)
            Process process = builder.start()
            process.waitFor(10, TimeUnit.SECONDS)
            process.getInputStream().eachLine { log.info(it) }
            if (!new File(output.toString()).exists()) {
                throw new RuntimeException("Offer PDF has not been generated.")
            }
        }
    }
}
