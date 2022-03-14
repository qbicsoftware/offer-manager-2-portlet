package life.qbic.business.offers;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import java.util.function.Supplier;
import life.qbic.business.persons.affiliation.Affiliation;
import life.qbic.business.products.ProductItem;

/**
 * <p>Supplies a checksum for a specific offer. The returned checksum is a SHA-256 checksum on
 * fields of the offer and formatted as a HEX String</p>
 */
public class OfferChecksumSupplier implements
    Supplier<String> {

  private final OfferV2 offer;

  public OfferChecksumSupplier(OfferV2 offer) {
    this.offer = offer;
  }

  @Override
  public String get() {
    String algorithm = "SHA-256";
    try {
      MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
      byte[] checksumBytes = update(messageDigest).digest();
      return getChecksum(checksumBytes);
    } catch (NoSuchAlgorithmException e) {
      // this should never happen as SHA-256 is expected to be there.
      throw new RuntimeException(
          "Could not find implementation for " + algorithm + " checksum calculation.");
    }
  }

  private String getChecksum(byte[] checksumBytes) {
    StringBuilder result = new StringBuilder();
    for (byte checksumByte : checksumBytes) {
      result.append(Integer.toHexString(checksumByte));
    }
    return result.toString();
  }

  private MessageDigest update(MessageDigest digest) {
    return Optional.of(digest)
        .map(this::updateWithTitle)
        .map(this::updateWithObjective)
        .map(this::updateWithExperimentalDesignIfPresent)
        .map(this::updateWithProductItems)
        .map(this::updateWithCustomer)
        .map(this::updateWithProjectManager)
        .map(this::updateWithSelectedAffiliation)
        .orElseThrow(() -> new RuntimeException("Unexpected error updating offer checksum."));
  }

  private MessageDigest updateWithTitle(MessageDigest digest) {
    digest.update(offer.getProjectTitle().getBytes(StandardCharsets.UTF_8));
    return digest;
  }

  private MessageDigest updateWithObjective(MessageDigest digest) {
    digest.update(offer.getProjectObjective().getBytes(StandardCharsets.UTF_8));
    return digest;
  }

  private MessageDigest updateWithExperimentalDesignIfPresent(MessageDigest digest) {
    offer.getExperimentalDesign()
        .ifPresent(it ->
            digest.update(it.getBytes(StandardCharsets.UTF_8)));
    return digest;
  }

  private MessageDigest updateWithProductItems(MessageDigest digest) {
    offer.getItems().forEach(it -> updateWithProductItem(digest, it));
    return digest;
  }

  private MessageDigest updateWithCustomer(MessageDigest digest) {
    digest.update(offer.getCustomer().getLastName().getBytes(StandardCharsets.UTF_8));
    return digest;
  }

  private MessageDigest updateWithProjectManager(MessageDigest digest) {
    digest.update(offer.getProjectManager().getLastName().getBytes(StandardCharsets.UTF_8));
    return digest;
  }

  private MessageDigest updateWithSelectedAffiliation(MessageDigest digest) {
    Affiliation selectedCustomerAffiliation = offer.getSelectedCustomerAffiliation();
    digest.update(selectedCustomerAffiliation.getOrganization().getBytes(StandardCharsets.UTF_8));
    digest.update(selectedCustomerAffiliation.getStreet().getBytes(StandardCharsets.UTF_8));
    return digest;
  }

  private void updateWithProductItem(MessageDigest digest, ProductItem productItem) {
    String productName = productItem.getProduct().getProductName();
    Double quantity = productItem.getQuantity();

    digest.update(productName.getBytes(StandardCharsets.UTF_8));
    digest.update(quantity.toString().getBytes(StandardCharsets.UTF_8));
  }
}
