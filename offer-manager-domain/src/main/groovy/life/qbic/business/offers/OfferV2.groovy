package life.qbic.business.offers

import life.qbic.business.offers.identifier.OfferId
import life.qbic.business.persons.Person
import life.qbic.business.persons.affiliation.Affiliation
import life.qbic.datamodel.dtos.business.ProductItem
import life.qbic.datamodel.dtos.projectmanagement.ProjectCode
import life.qbic.datamodel.dtos.projectmanagement.ProjectIdentifier
import life.qbic.datamodel.dtos.projectmanagement.ProjectSpace

import javax.persistence.*
import java.time.LocalDate

/**
 * <b>New Offer class</b>
 *
 * <p>An offer entity holds crucial information about a customers quote request, such
 * as the service items needed for the research question at hand, the project's objective
 * and more.</p>
 *
 * @since 1.3.0
 */
@Entity
@Table(name = "offer")
class OfferV2 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id

    /**
     * Date on which the offer was lastly modified
     */
    @Column(name = "creationDate")
    private LocalDate creationDate

    /**
     * The date on which the offer expires
     */
    @Column(name = "expirationDate")
    private LocalDate expirationDate

    /**
     * The customer for which this offer was created
     */
    @OneToOne(cascade = [CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH])
    @JoinColumn(name = "customerId")

    private Person customer
    /**
     * The QBiC project manager who was assigned to the project
     */
    @OneToOne(cascade = [CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH])
    @JoinColumn(name = "projectManagerId")
    private Person projectManager

    /**
     * The title of the project
     */
    @Column(name = "projectTitle")
    private String projectTitle

    /**
     * A short objective of the project
     */
    @Column(name = "projectObjective")
    private String projectObjective

    /**
     * A short description of the experimental design of the project
     */
    @Column(name = "experimentalDesign")
    @Convert(converter = OptionalStringConverter.class)
    private Optional<String> experimentalDesign

    /**
     * A list of items for which the customer will be charged
     */
    @OneToMany(cascade = [CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH])
    private List<ProductItem> items = []

    /**
     * The identifier for the offer which makes it distinguishable from other offers
     */
    @Column(name = "offerId")
    @Convert(converter = OfferIdConverter.class)
    private OfferId identifier

    /**
     * The affiliation of the customer selected for this offer
     */
    @OneToOne(cascade = [CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH])
    @JoinColumn(name = "customerAffiliationId")
    private Affiliation selectedCustomerAffiliation

    /**
     * Holds the determined overhead total derived from the
     * customer's affiliation.
     */
    @Column(name = "overheads")
    private double overhead

    /**
     * The overhead ratio that is applied to calculate the total offer price. The ratio is chosen
     * based on the customer's affiliation.
     *  e.g. 0.4 or a 40% markup for external customers
     */
    private double overheadRatio

    /**
     * A project that has been created from this offer (optional)
     */
    @Column(name = "associatedProject")
    @Convert(converter = OptionalProjectIdentifierConverter.class)
    private Optional<ProjectIdentifier> associatedProject

    LocalDate getCreationDate() {
        return creationDate
    }

    void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate
    }

    LocalDate getExpirationDate() {
        return expirationDate
    }

    void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate
    }

    Person getCustomer() {
        return customer
    }

    void setCustomer(Person customer) {
        this.customer = customer
    }

    Person getProjectManager() {
        return projectManager
    }

    void setProjectManager(Person projectManager) {
        this.projectManager = projectManager
    }

    String getProjectTitle() {
        return projectTitle
    }

    void setProjectTitle(String projectTitle) {
        this.projectTitle = projectTitle
    }

    String getProjectObjective() {
        return projectObjective
    }

    void setProjectObjective(String projectObjective) {
        this.projectObjective = projectObjective
    }

    Optional<String> getExperimentalDesign() {
        return experimentalDesign
    }

    void setExperimentalDesign(Optional<String> experimentalDesign) {
        this.experimentalDesign = experimentalDesign
    }

    List<ProductItem> getItems() {
        return items
    }

    void setItems(List<ProductItem> items) {
        this.items = items
    }

    OfferId getIdentifier() {
        return identifier
    }

    void setIdentifier(OfferId identifier) {
        this.identifier = identifier
    }

    Affiliation getSelectedCustomerAffiliation() {
        return selectedCustomerAffiliation
    }

    void setSelectedCustomerAffiliation(Affiliation selectedCustomerAffiliation) {
        this.selectedCustomerAffiliation = selectedCustomerAffiliation
    }

    double getOverhead() {
        return overhead
    }

    void setOverhead(double overhead) {
        this.overhead = overhead
    }

    double getOverheadRatio() {
        return overheadRatio
    }

    void setOverheadRatio(double overheadRatio) {
        this.overheadRatio = overheadRatio
    }

    Optional<ProjectIdentifier> getAssociatedProject() {
        return associatedProject
    }

    void setAssociatedProject(Optional<ProjectIdentifier> associatedProject) {
        this.associatedProject = associatedProject
    }

    private static class OptionalStringConverter implements AttributeConverter<Optional<String>, String> {

        @Override
        String convertToDatabaseColumn(Optional<String> s) {
            return s.orElse("")
        }

        @Override
        Optional<String> convertToEntityAttribute(String s) {
            // If the String is null or empty, we return an empty Optional
            return s ? Optional.of(s) : Optional.empty() as Optional<String>
        }
    }

    private static class OptionalProjectIdentifierConverter implements AttributeConverter<Optional<ProjectIdentifier>, String> {

        @Override
        String convertToDatabaseColumn(Optional<ProjectIdentifier> projectIdentifier) {
            if (projectIdentifier.isPresent()) {
                return projectIdentifier.toString()
            } else {
                return ""
            }
        }

        @Override
        Optional<ProjectIdentifier> convertToEntityAttribute(String s) {
            Optional<ProjectIdentifier> identifier = Optional.empty()
            if (!s) {
                return identifier
            }
            try {
                /*
                A full openBIS project ID has the format: '/<space>/<project>', where
                <space> and <project> are placeholders for real space and project names.
                 */
                def splittedIdentifier = s.split("/")
                if (splittedIdentifier.length != 3) {
                    throw new RuntimeException(
                            "Project identifier has an unexpected number of separators: ${projectIdentifier}. " +
                                    "The expected format must follow this schema: \'/<space>/<project>\'")
                }
                def space = new ProjectSpace(splittedIdentifier[1])
                def code = new ProjectCode(splittedIdentifier[2])
                identifier = Optional.of(new ProjectIdentifier(space, code))
            } catch (Exception ignored) {
                throw new IllegalArgumentException(String.format("Cannot parse %s to a project identifier.", identifier))
            }
            return identifier
        }
    }

    private static class OfferIdConverter implements AttributeConverter<OfferId, String> {

        @Override
        String convertToDatabaseColumn(OfferId offerId) {
            return offerId.toString()
        }

        @Override
        OfferId convertToEntityAttribute(String s) {
            return OfferId.from(s)
        }
    }
}
