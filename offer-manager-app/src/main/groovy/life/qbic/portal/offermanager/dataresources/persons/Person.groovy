package life.qbic.portal.offermanager.dataresources.persons

import groovy.transform.ToString

import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.JoinTable
import javax.persistence.ManyToMany
import javax.persistence.Table

/**
 * <b><class short description - 1 Line!></b>
 *
 * <p><More detailed description - When to use, what it solves, etc.></p>
 *
 * @since <version tag>
 */
@ToString
@Entity
@Table(name = "person")
class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Integer id

    @Column(name = "user_id")
    String userId

    @Column(name = "first_name")
    String firstName

    @Column(name = "last_name")
    String lastName

    @Column(name = "title")
    String title

    @Column(name = "email")
    String email

    @Column(name = "active")
    Integer active

    @ManyToMany(cascade = [CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH],
        fetch = FetchType.LAZY)
    @JoinTable(name = "person_affiliation", joinColumns = [ @JoinColumn(name = "person_id") ],
            inverseJoinColumns = [ @JoinColumn(name = "affiliation_id")])
    Set<Affiliation> affiliations

    Person() {}

    Set<Affiliation> getAffiliations() {
        return affiliations
    }

    void setAffiliations(Set<Affiliation> affiliations) {
        this.affiliations = affiliations
    }

    Person(String userId, String firstName, String lastName, String title, String email, Integer active, Set<Affiliation> affiliations) {
        this.userId = userId
        this.firstName = firstName
        this.lastName = lastName
        this.title = title
        this.email = email
        this.active = active
        this.affiliations = affiliations
    }

    Integer getId() {
        return id
    }

    void setId(Integer id) {
        this.id = id
    }

    String getUserId() {
        return userId
    }

    void setUserId(String userId) {
        this.userId = userId
    }

    String getFirstName() {
        return firstName
    }

    void setFirstName(String firstName) {
        this.firstName = firstName
    }

    String getLastName() {
        return lastName
    }

    void setLastName(String lastName) {
        this.lastName = lastName
    }

    String getTitle() {
        return title
    }

    void setTitle(String title) {
        this.title = title
    }

    String getEmail() {
        return email
    }

    void setEmail(String email) {
        this.email = email
    }

    Integer getActive() {
        return active
    }

    void setActive(Integer active) {
        this.active = active
    }

    void addAffiliation(Affiliation affiliation) {
        this.affiliations.add(affiliation)
    }

    void removeAffiliation(Affiliation affiliation) {
        if (affiliation in affiliations) {
            affiliations.remove(affiliation)
        }
    }

}
