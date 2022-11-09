package life.qbic.business.offers.customer;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import life.qbic.business.persons.Person;

/**
 * <b><class short description - 1 Line!></b>
 *
 * <p><More detailed description - When to use, what it solves, etc.></p>
 *
 * @since <version tag>
 */
@Table(name = "customer")
public class Customer {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Integer id;

  @Column(name = "offer")
  private int offer;

  @Column(name = "reference_id")
  private String referenceId;

  @Column(name = "full_name")
  private String fullName;

  @Column(name = "email")
  private String email;

  @Embedded
  private Address address;

  public static Customer from(Person person, Address address) {
    return create(person.getReferenceId(), person.getFirstName() + person.getLastName(),
        person.getEmail(), address);
  }

  public static Customer create(String referenceId, String fullName, String email,
      Address address) {
    Customer customer = new Customer();
    customer.setReferenceId(referenceId);
    customer.setFullName(fullName);
    customer.setEmail(email);
    customer.setAddress(address);
    return customer;
  }

  private Customer() {
  }

  private Integer getId() {
    return id;
  }

  private void setId(Integer id) {
    this.id = id;
  }

  private String getReferenceId() {
    return referenceId;
  }

  private void setReferenceId(String referenceId) {
    this.referenceId = referenceId;
  }

  private String getFullName() {
    return fullName;
  }

  private void setFullName(String fullName) {
    this.fullName = fullName;
  }

  private String getEmail() {
    return email;
  }

  private void setEmail(String email) {
    this.email = email;
  }

  private Address getAddress() {
    return address;
  }

  private void setAddress(Address address) {
    this.address = address;
  }
}
