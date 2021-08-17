package life.qbic.portal.offermanager

import groovy.util.logging.Log4j2
import life.qbic.business.offers.OfferContent
import life.qbic.business.offers.OfferExporter
import life.qbic.portal.offermanager.offergeneration.OfferTemplate
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.time.LocalDate
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
class OfferToPDFConverter implements OfferExporter {

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

    private final OfferContent offer

    private final Path tempDir

    private final Document htmlContent

    private final Path createdOffer

    private final Path createdOfferPdf

    private final Path newOfferImage

    private final Path newOfferStyle

    private final Path newOfferPrintStyle

    private static final URI OFFER_HTML_TEMPLATE_URI = OfferToPDFConverter.class.getClassLoader()
            .getResource("offer-template/offer.html").toURI()
    private static final Path OFFER_HTML_TEMPLATE =
            Paths.get(OFFER_HTML_TEMPLATE_URI)
    private static final Path OFFER_HEADER_IMAGE =
            Paths.get(OfferToPDFConverter.class.getClassLoader()
                    .getResource("offer-template/offer_header.png")
                    .toURI())
    private static final Path OFFER_STYLESHEET =
            Paths.get(OfferToPDFConverter.class.getClassLoader()
                    .getResource("offer-template/stylesheet.css")
                    .toURI())
    public static final Path OFFER_PRINT_CSS =
            Paths.get(OfferToPDFConverter.class.getClassLoader()
                    .getResource("offer-template/print.css")
                    .toURI())

    private static final OfferTemplate TEMPLATE = new OfferTemplate(Jsoup.parse(new File(OFFER_HTML_TEMPLATE_URI),"UTF-8"))


    OfferToPDFConverter(OfferContent offer) {
        this.offer = Objects.requireNonNull(offer, "Offer object must not be a null reference")
        this.tempDir = Files.createTempDirectory("offer")
        this.createdOffer = Paths.get(tempDir.toString(), "offer.html")
        this.newOfferImage = Paths.get(tempDir.toString(), "offer_header.png")
        this.newOfferStyle = Paths.get(tempDir.toString(), "stylesheet.css")
        this.newOfferPrintStyle = Paths.get(tempDir.toString(), "print.css")
        this.createdOfferPdf = Paths.get(tempDir.toString(), "offer.pdf")

        importTemplateResources()
        this.htmlContent = TEMPLATE.fill(offer)
        this.htmlContent.setBaseUri(this.createdOffer.toUri().toString())
        setHtmlTitle()
        writeHTMLContentToFile(this.createdOffer, this.htmlContent)
        generatePDF()
    }

    private void setHtmlTitle() {
        // TODO this could be replaced by refactoring the OfferFileNameFormatter.
        LocalDate offerDate = offer.getCreationDate().toLocalDate()
        String dateString =String.format("%04d_%02d_%02d", offerDate.getYear(), offerDate.getMonthValue(), offerDate.getDayOfMonth())
        this.htmlContent.title("${dateString}_${offer.getOfferIdentifier()}")
    }

    InputStream getOfferAsPdf() {
        return new BufferedInputStream(new FileInputStream(new File(createdOfferPdf.toString())))
    }

    private void importTemplateResources() {
        Files.copy(OFFER_HEADER_IMAGE, newOfferImage, StandardCopyOption.REPLACE_EXISTING)
        Files.copy(OFFER_STYLESHEET, newOfferStyle, StandardCopyOption.REPLACE_EXISTING)
        Files.copy(OFFER_PRINT_CSS, newOfferPrintStyle, StandardCopyOption.REPLACE_EXISTING)
    }

    private static void writeHTMLContentToFile(Path fileLocation, Document htmlContent) {
        new File(fileLocation.toUri()).withWriter {
            it.write(htmlContent.toString())
            it.flush()
        }
    }

    private void generatePDF() {
        PdfPrinter pdfPrinter = new PdfPrinter(createdOffer)
        pdfPrinter.print(createdOfferPdf)
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
