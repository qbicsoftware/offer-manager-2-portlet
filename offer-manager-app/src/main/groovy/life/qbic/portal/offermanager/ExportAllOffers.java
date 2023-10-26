package life.qbic.portal.offermanager;

import com.vaadin.server.StreamResource.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import life.qbic.business.offers.OfferV2;

/**
 * Provides export functionality for all offers
 *
 * @since 1.9.0
 */
public class ExportAllOffers implements StreamSource {

  static final List<Column> tsvColumns = setColumns();

  private final ExportOffersDataSource exportOffersDataSource;

  public ExportAllOffers(ExportOffersDataSource exportOffersDataSource) {
    this.exportOffersDataSource = exportOffersDataSource;
  }

  private static List<Column> setColumns() {
    ArrayList<Column> columns = new ArrayList<>();
    //identification of the offer
    columns.add(new Column("versionedOfferId", offerV2 -> offerV2.getIdentifier().toString()));
    columns.add(new Column("offerId", offerV2 -> offerV2.getIdentifier().toStringWithoutVersion()));
    columns.add(new Column("offerIdVersion",
        offerV2 -> String.valueOf(offerV2.getIdentifier().getVersion())));

    // offer properties
    columns.add(new Column("projectTitle", OfferV2::getProjectTitle));
    columns.add(new Column("creationDate", offerV2 -> offerV2.getCreationDate().toString()));
    //customer
    columns.add(new Column("customerName",
        offerV2 -> offerV2.getCustomer().getFirstName() + " " + offerV2.getCustomer()
            .getLastName()));
    columns.add(new Column("customerEmail", offerV2 -> offerV2.getCustomer().getEmail()));
    columns.add(new Column("affiliationOrganisation",
        offerV2 -> offerV2.getSelectedCustomerAffiliation().getOrganization()));
    columns.add(new Column("affiliationAddressAddition",
        offerV2 -> offerV2.getSelectedCustomerAffiliation().getAddressAddition()));
    columns.add(new Column("affiliationCategory",
        offerV2 -> offerV2.getSelectedCustomerAffiliation().getCategory().getLabel()));
    columns.add(new Column("projectmanagerName",
        offerV2 -> offerV2.getProjectManager().getFirstName() + " " + offerV2.getProjectManager()
            .getLastName()));
    columns.add(
        new Column("projectmanagerEmail", offerV2 -> offerV2.getProjectManager().getEmail()));
    //pricing
    columns.add(
        new Column("dataGenerationSalePrice",
            offerV2 -> String.valueOf(offerV2.getDataGenerationSalePrice())));
    columns.add(new Column("dataAnalysisSalePrice",
        offerV2 -> String.valueOf(offerV2.getDataAnalysisSalePrice())));
    columns.add(new Column("ProjectManagementAndDataStorageSalePrice", offerV2 -> String.valueOf(
        offerV2.getDataManagementSalePrice())));
    columns.add(new Column("totalSalePrice", offerV2 -> String.valueOf(offerV2.getSalePrice())));
    // overheads
    columns.add(new Column("overheadRatio", offerV2 -> String.valueOf(
        offerV2.getOverheadRatio())));
    columns.add(
        new Column("dataGenerationOverhead",
            offerV2 -> String.valueOf(offerV2.getDataGenerationOverhead())));
    columns.add(new Column("dataAnalysisOverheads",
        offerV2 -> String.valueOf(offerV2.getDataAnalysisOverhead())));
    columns.add(new Column("ProjectManagementAndDataStorageOverheads", offerV2 -> String.valueOf(
        offerV2.getDataManagementOverhead())));
    columns.add(new Column("overheadTotal", offerV2 -> String.valueOf(offerV2.getOverhead())));
    //tax
    columns.add(new Column("priceBeforeTax", offerV2 -> String.valueOf(offerV2.getPriceBeforeTax())));
    columns.add(new Column("tax", offerV2 -> String.valueOf(offerV2.getTaxAmount())));
    columns.add(new Column("priceAfterTax",
        offerV2 -> String.valueOf(offerV2.getPriceAfterTax())));
    return columns;
  }
  public InputStream exportOffersToTsv() {
    StringBuilder fileContent = new StringBuilder();

    for (int i = 0; i < tsvColumns.size(); i++) {
      String columnName = tsvColumns.get(i).getName();
      if (i + 1 < tsvColumns.size()) {
        columnName += "\t";
      }
      fileContent.append(columnName);
    }
    fileContent.append("\n");
    List<OfferV2> allOffers = exportOffersDataSource.findAllOffers();
    allOffers.stream()
        .map(this::toTsvLine)
        .forEach(fileContent::append);
    return new ByteArrayInputStream(fileContent.toString().getBytes());
  }

  private String toTsvLine(OfferV2 offerV2) {
    StringBuilder line = new StringBuilder();
    for (int i = 0; i < tsvColumns.size(); i++) {
      Column column = tsvColumns.get(i);
      String value = column.getValue(offerV2);
      line.append(value);
      if (i + 1 < tsvColumns.size()) {
        line.append("\t");
      }
    }
    line.append("\n");
    return line.toString();
  }

  private static final class Column {

    private final String name;
    private final Function<OfferV2, String> valueConverter;

    private Column(String name, Function<OfferV2, String> valueConverter) {
      this.name = name;
      this.valueConverter = valueConverter;
    }

    public String getName() {
      return name;
    }

    public String getValue(OfferV2 offerV2) {
      return valueConverter.apply(offerV2);
    }
  }

  @Override
  public InputStream getStream() {
    return exportOffersToTsv();
  }
}
