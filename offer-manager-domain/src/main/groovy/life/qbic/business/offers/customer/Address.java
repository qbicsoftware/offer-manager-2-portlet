package life.qbic.business.offers.customer;

import javax.persistence.Column;
import life.qbic.business.persons.affiliation.Affiliation;

/**
 * <b><class short description - 1 Line!></b>
 *
 * <p><More detailed description - When to use, what it solves, etc.></p>
 *
 * @since <version tag>
 */
public class Address {

  public static Address from (Affiliation affiliation) {
    return new Address(affiliation.getStreet());
  }

  @Column(name = "street")
  private String street;

  private Address(String street) {
    this.street = street;
  }

  public String street() {
    return this.street;
  }

}
