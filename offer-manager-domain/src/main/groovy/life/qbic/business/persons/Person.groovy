package life.qbic.business.persons

import groovy.transform.ToString
import life.qbic.business.persons.affiliation.Affiliation

import javax.persistence.*
import java.util.stream.Collectors

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

    @ManyToMany(cascade = [CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH], fetch = FetchType.EAGER)
    @JoinTable(name = "person_affiliation", joinColumns = [ @JoinColumn(name = "person_id") ],
            inverseJoinColumns = [ @JoinColumn(name = "affiliation_id")] )
    List<Affiliation> affiliations = []

    @Column(name = "reference_id")
    String referenceId

    Person() {}

    @PostLoad
    protected void onPostLoad() {
        loadAffiliations()
    }

    private List<Affiliation> loadAffiliations(){
        return this.affiliations
    }

    static create(String userId, String firstName, String lastName, String title, String email, List<Affiliation> affiliations) {
        return new Person(userId, firstName, lastName, title, email, affiliations, UUID.randomUUID().toString())
    }

    Person(String userId, String firstName, String lastName, String title, String email, List<Affiliation> affiliations, String referenceId) {
        this.userId = userId
        this.firstName = firstName
        this.lastName = lastName
        this.title = title
        this.email = email
        this.affiliations = affiliations
        UUID.fromString(referenceId)
        this.referenceId = referenceId
    }

    private setReferenceId(String id) {
        this.referenceId = id
    }

    String getReferenceId() {
        return referenceId
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

    /**
     * Returns active affiliations of a person
     * @return
     * @since 1.6.0
     */
    List<Affiliation> getAffiliations() {
        List<Affiliation> activeAffiliations = affiliations.stream()
                .filter(affiliation -> affiliation.isActive()).collect(Collectors.toList())
        return activeAffiliations
    }

    void setAffiliations(List<Affiliation> affiliations) {
        this.affiliations.clear()
        this.affiliations.addAll(affiliations)
    }

    void addAffiliation(Affiliation affiliation) {
        if (!(affiliation in affiliations))
            this.affiliations.add(affiliation)
    }

    void removeAffiliation(Affiliation affiliation) {
        if (affiliation in affiliations) {
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
