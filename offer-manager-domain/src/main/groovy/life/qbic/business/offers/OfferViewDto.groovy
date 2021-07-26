package life.qbic.business.offers

import groovy.transform.EqualsAndHashCode
import life.qbic.business.productitem.ProductItemViewDto
import life.qbic.datamodel.dtos.business.Affiliation
import life.qbic.datamodel.dtos.business.Customer
import life.qbic.datamodel.dtos.business.OfferId
import life.qbic.datamodel.dtos.business.ProjectManager
import life.qbic.datamodel.dtos.projectmanagement.ProjectIdentifier

import java.time.Instant

/**
 * <b>Information about an offer that should be visible to the application</b>
 *
 * <p>This data-transfer object is intended to be used to transfer information about an offer to the view.</p>
 *
 * @since 1.1.0
 */
@EqualsAndHashCode
class OfferViewDto {

  /**
   * The customer for which this offer was created
   */
  final Customer customer

  /**
   * Timestamp when the offer expires
   */
  final Instant expirationDate

  /**
   * The identifier for the offer which makes it distinguishable from other offers
   */
  final OfferId identifier

  /**
   * A list of items for which the customer will be charged
   */
  final List<ProductItemViewDto> items

  /**
   * A list of items for which an overhead cost is applicable
   */
  final List<ProductItemViewDto> itemsWithOverhead

  /**
   * A list of Items for which an overhead cost is not applicable
   */
  final List<ProductItemViewDto> itemsWithoutOverhead

  /**
   * The net price of all items for which an overhead cost is applicable, without overhead and taxes
   */
  final double itemsWithOverheadNetPrice

  /**
   * The net price of all items for which an overhead cost is not applicable, without overhead and taxes
   */
  final double itemsWithoutOverheadNetPrice

  /**
   * Timestamp on which the offer was lastly modified
   */
  final Instant modificationDate

  /**
   * The net price of all items without taxes and overhead
   */
  final double netPrice

  /**
   * The amount of overheads part, of the total price
   */
  final double overheads

  /**
   * The overhead ratio applied to the pricing dependent on the customer affiliation
   */
  final double overheadRatio

  /**
   * The QBiC project manager who was assigned to the project
   */
  final ProjectManager projectManager

  /**
   * A short objective of the project
   */
  final String projectObjective

  /**
   * The title of the project
   */
  final String projectTitle

  /**
   * The affiliation of the customer selected for this offer
   */
  final Affiliation selectedCustomerAffiliation

  /**
   * The amount of taxes, part of the total total price
   */
  final double taxes

  /**
   * The sum of discount provided for this offer
   */
  final double totalDiscount

  /**
   * The total price of the offer (the price of all items) including taxes, overheads and discounts
   */
  final double totalPrice

  /**
   * The associated project identifier, if a project based on the offer exists.
   */
  final Optional<ProjectIdentifier> associatedProject

  /**
   * The experimental design of a project
   */
  final Optional<String> experimentalDesign

  private OfferViewDto(Builder builder) {
    this.customer = builder.customer
    this.expirationDate = builder.expirationDate
    this.identifier = builder.identifier
    this.items = builder.items
    this.itemsWithOverhead = builder.itemsWithOverhead
    this.itemsWithoutOverhead = builder.itemsWithoutOverhead
    this.itemsWithOverheadNetPrice = builder.itemsWithOverheadNetPrice
    this.itemsWithoutOverheadNetPrice = builder.itemsWithoutOverheadNetPrice
    this.modificationDate = builder.modificationDate
    this.netPrice = builder.netPrice
    this.overheads = builder.overheads
    this.overheadRatio = builder.overheadRatio
    this.projectManager = builder.projectManager
    this.projectObjective = builder.projectObjective
    this.projectTitle = builder.projectTitle
    this.selectedCustomerAffiliation = builder.selectedCustomerAffiliation
    this.taxes = builder.taxes
    this.totalDiscount = builder.totalDiscount
    this.totalPrice = builder.totalPrice
    this.associatedProject = builder.associatedProject
    this.experimentalDesign = builder.experimentalDesign
  }

  static class Builder {

    /**
     * The customer for which this offer was created
     */
    final Customer customer

    /**
     * Timestamp when the offer expires
     */
    final Instant expirationDate

    /**
     * The identifier for the offer which makes it distinguishable from other offers
     */
    final OfferId identifier

    /**
     * A list of items for which the customer will be charged
     */
    final List<ProductItemViewDto> items

    /**
     * A list of items for which an overhead cost is applicable
     */
    final List<ProductItemViewDto> itemsWithOverhead

    /**
     * A list of Items for which an overhead cost is not applicable
     */
    final List<ProductItemViewDto> itemsWithoutOverhead

    /**
     * The net price of all items for which an overhead cost is applicable, without overhead and taxes
     */
    final double itemsWithOverheadNetPrice

    /**
     * The net price of all items for which an overhead cost is not applicable, without overhead and taxes
     */
    final double itemsWithoutOverheadNetPrice

    /**
     * Timestamp on which the offer was lastly modified
     */
    final Instant modificationDate

    /**
     * The net price of all items without taxes and overhead
     */
    final double netPrice

    /**
     * The amount of overheads part, of the total price
     */
    final double overheads

    /**
     * The overhead ratio applied to the pricing dependent on the customer affiliation
     */
    final double overheadRatio

    /**
     * The QBiC project manager who was assigned to the project
     */
    final ProjectManager projectManager

    /**
     * A short objective of the project
     */
    final String projectObjective

    /**
     * The title of the project
     */
    final String projectTitle

    /**
     * The affiliation of the customer selected for this offer
     */
    final Affiliation selectedCustomerAffiliation

    /**
     * The amount of taxes, part of the total total price
     */
    final double taxes

    /**
     * The sum of discount provided for this offer
     */
    final double totalDiscount

    /**
     * The total price of the offer (the price of all items) including taxes, overheads and discounts
     */
    final double totalPrice

    /**
     * The associated project identifier, if a project based on the offer exists.
     */
    Optional<ProjectIdentifier> associatedProject

    /**
     * The experimental design of a project
     */
    Optional<String> experimentalDesign

    Builder(Customer customer, Instant expirationDate, OfferId identifier, List<ProductItemViewDto> items, List<ProductItemViewDto> itemsWithOverhead, List<ProductItemViewDto> itemsWithoutOverhead, double itemsWithOverheadNetPrice, double itemsWithoutOverheadNetPrice, Instant modificationDate, double netPrice, double overheads, double overheadRatio, ProjectManager projectManager, String projectObjective, String projectTitle, Affiliation selectedCustomerAffiliation, double taxes, double totalDiscount, double totalPrice) {
      this.customer = Objects.requireNonNull(customer)
      this.expirationDate = Objects.requireNonNull(expirationDate)
      this.identifier = Objects.requireNonNull(identifier)
      this.items = Objects.requireNonNull(items)
      this.itemsWithOverhead = Objects.requireNonNull(itemsWithOverhead)
      this.itemsWithOverheadNetPrice = Objects.requireNonNull(itemsWithOverheadNetPrice)
      this.itemsWithoutOverhead = Objects.requireNonNull(itemsWithoutOverhead)
      this.itemsWithoutOverheadNetPrice = Objects.requireNonNull(itemsWithoutOverheadNetPrice)
      this.modificationDate = Objects.requireNonNull(modificationDate)
      this.netPrice = Objects.requireNonNull(netPrice)
      this.overheadRatio = Objects.requireNonNull(overheadRatio)
      this.overheads = Objects.requireNonNull(overheads)
      this.projectManager = Objects.requireNonNull(projectManager)
      this.projectObjective = Objects.requireNonNull(projectObjective)
      this.projectTitle = Objects.requireNonNull(projectTitle)
      this.selectedCustomerAffiliation = Objects.requireNonNull(selectedCustomerAffiliation)
      this.taxes = Objects.requireNonNull(taxes)
      this.totalDiscount = Objects.requireNonNull(totalDiscount)
      this.totalPrice = Objects.requireNonNull(totalPrice)

      this.experimentalDesign = Optional.empty()
      this.associatedProject = Optional.empty()
    }

    Builder experimentalDesign(String experimentalDesign) {
      this.experimentalDesign = Optional.of(experimentalDesign)
      return this
    }

    Builder associatedProject(ProjectIdentifier projectIdentifier) {
      this.associatedProject = Optional.of(projectIdentifier)
      return this
    }

    OfferViewDto build() {
      return new OfferViewDto(this)
    }



  }
}
