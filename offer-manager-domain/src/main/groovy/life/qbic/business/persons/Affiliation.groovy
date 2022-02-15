package life.qbic.business.persons

import groovy.transform.ToString

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

/**
 * An affiliation that holds information about an institution.
 *
 * @since 1.3.0
 */
@ToString
@Entity
@Table(name = "affiliation")
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
    String category

    Affiliation(String organization, String addressAddition, String street, String postalCode, String city, String country, String category) {
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

    String getCategory() {
        return category
    }

    void setCategory(String category) {
        this.category = category
    }
}
