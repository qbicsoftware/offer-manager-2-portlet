package life.qbic.business.persons.affiliation

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

import javax.persistence.*

/**
 * An affiliation that holds information about an institution.
 *
 * @since 1.3.0
 */
@ToString
@Entity
@Table(name = "affiliation")
@EqualsAndHashCode(excludes = ["id"])
class Affiliation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    int id

    @Column(name = "organization")
    String organization

    @Column(name = "address_addition")
    String addressAddition

    @Column(name = "street")
    String street

    @Column(name = "postal_code")
    String postalCode

    @Column(name = "city")
    String city

    @Column(name = "country")
    String country

    @Column(name = "category")
    @Convert(converter = AffiliationCategoryConverter.class)
    AffiliationCategory category

    @Column(name = "active", columnDefinition = "tinyint", nullable = false)
    boolean isActive = true

    Affiliation(String organization, String addressAddition, String street, String postalCode, String city, String country, AffiliationCategory category) {
        this.organization = organization
        this.addressAddition = addressAddition
        this.street = street
        this.postalCode = postalCode
        this.city = city
        this.country = country
        this.category = category
    }

    Affiliation() {

    }

    int getId() {
        return id
    }

    void setId(int id) {
        this.id = id
    }

    String getOrganization() {
        return organization
    }

    void setOrganization(String organization) {
        this.organization = organization
    }

    String getAddressAddition() {
        return addressAddition
    }

    void setAddressAddition(String addressAddition) {
        this.addressAddition = addressAddition
    }

    String getStreet() {
        return street
    }

    void setStreet(String street) {
        this.street = street
    }

    String getPostalCode() {
        return postalCode
    }

    void setPostalCode(String postalCode) {
        this.postalCode = postalCode
    }

    String getCity() {
        return city
    }

    void setCity(String city) {
        this.city = city
    }

    String getCountry() {
        return country
    }

    void setCountry(String country) {
        this.country = country
    }

    AffiliationCategory getCategory() {
        return category
    }

    void setCategory(AffiliationCategory category) {
        this.category = category
    }

    protected boolean getIsActive() {
        return isActive
    }

    boolean isActive() {
        return getIsActive()
    }

    void inactivate() {
        setIsActive(false)
    }

    protected void setIsActive(boolean isActive) {
        this.isActive = isActive
    }
}
