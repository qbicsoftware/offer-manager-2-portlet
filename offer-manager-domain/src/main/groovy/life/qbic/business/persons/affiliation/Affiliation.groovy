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
    boolean active = true

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

    /**
     * Requests the current status of the affiliation entity.
     * <p>
     * An affiliation that has been archived, is inactive. So this method will return
     * <code>false</code> in this case. Otherwise the return value is <code>true<code>.
     * <p>
     * We need to keep affiliations stored, even if they are not used anymore, to preserve
     * backwards compatibility for offer generation.
     * <p>
     * To make the intention clear, that they should not be used anymore (outdated, wrong, etc),
     * this flag has been introduced.
     * @return
     */
    boolean isActive() {
        return active
    }

    /**
     * Requests the current archived status. Is the direct inversion of {@link Affiliation#isActive}.
     * @return true, if archived else if still active
     */
    boolean isArchived() {
        return !isActive()
    }

    /**
     * Archives the current affiliation, and make the intention clear that the affiliation
     * shall not be used anymore in future offers.
     */
    void archive() {
        setActive(false)
    }

    protected void setActive(boolean active) {
        this.active = active
    }
}
