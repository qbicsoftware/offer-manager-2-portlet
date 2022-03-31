package life.qbic.business.persons

import groovy.transform.ToString
import life.qbic.business.persons.affiliation.Affiliation

import javax.persistence.*

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
    @Convert(converter = EmptyTitleRemover.class)
    String title

    @Column(name = "email")
    String email

    @Column(name = "active", columnDefinition = "tinyint", nullable = false)
    boolean isActive = true

    @ManyToMany(cascade = [CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH])
    @JoinTable(name = "person_affiliation", joinColumns = [ @JoinColumn(name = "person_id") ],
            inverseJoinColumns = [ @JoinColumn(name = "affiliation_id")])
    List<Affiliation> affiliations = []

    Person() {}

    @PostLoad
    protected void onPostLoad() {
        this.getAffiliations()
        if (affiliations.isEmpty()) {
            throw new IllegalStateException("Person $this was loaded without affiliations. Illegal State: A person must have at least one affiliation.")
        }
    }

    Person(String userId, String firstName, String lastName, String title, String email, List<Affiliation> affiliations) {
        this.userId = userId
        this.firstName = firstName
        this.lastName = lastName
        this.title = title
        this.email = email
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

    boolean getIsActive() {
        return isActive
    }

    void setIsActive(boolean isActive) {
        this.isActive = isActive
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

    List<Affiliation> getAffiliations() {
        return affiliations
    }

    void setAffiliations(List<Affiliation> affiliations) {
        if (affiliations.isEmpty()) {
            throw new IllegalArgumentException("A person must have at least one affiliation.")
        }
        this.affiliations.clear()
        this.affiliations.addAll(affiliations)
    }

    void addAffiliation(Affiliation affiliation) {
        if (!(affiliation in affiliations))
            this.affiliations.add(affiliation)
    }

    void removeAffiliation(Affiliation affiliation) {
        if (affiliation in affiliations) {
            if (affiliations.size() == 1) {
                throw new IllegalArgumentException("Cannot remove the last remaining affiliation: $affiliation")
            }
            affiliations.remove(affiliation)
        }
    }

    protected static class EmptyTitleRemover implements AttributeConverter<String, String> {
        @Override
        String convertToDatabaseColumn(String s) {
            if (s.isEmpty()) {
                return "None"
            }
            return s
        }

        @Override
        String convertToEntityAttribute(String s) {
            if (s.equals("None")) {
                return ""
            }
            return s
        }
    }
}
