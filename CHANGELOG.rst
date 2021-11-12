==========
Changelog
==========

This project adheres to `Semantic Versioning <https://semver.org/>`_.

1.3.0-SNAPSHOT (2021-11-12)
---------------------------
**Added**

**Fixed**

**Dependencies**

**Deprecated**

1.2.2 (2021-11-09)
------------------

**Added**

* Address additions of customers are now shown on generated offer PDFs

**Fixed**

* Offer identifiers are now all in the format `O_project_rand_1`

* ZIP code and city of the offer recipient are in the same line now

**Dependencies**

**Deprecated**

1.2.1 (2021-10-27)
------------------

**Added**

**Fixed**

* Introduce proper exception handling when product cannot found in the database (DM-66)

* Address exception creation when CopyProduct use case was triggered (DM-33)

**Dependencies**

* com.vaadin 8.13.0 -> 8.14.0 (addresses CVE-2021-37714)

* Bump data model lib 2.13.0 -> 2.14.0 (includes extension of ProductCategory with ProductType abbreviation)

**Deprecated**

1.2.0 (2021-10-13)
------------------

**Added**

* Support for external service products (#700)

* Adds a facility column to the view (#835)

**Fixed**

* Update offer template footer to show correct names (#817)

* Generated offers are removed from the server after download (#776)

* Fix race condition issue in the update person use case (`#833 <https://github.com/qbicsoftware/offer-manager-2-portlet/issues/833>`_)

**Dependencies**

* org.jsoup 1.13.1 -> 1.14.2 (addresses CVE-2021-37714)

* org.apache.logging.log4j 1.2.17 -> 2.14.0 (addresses CVE-2019-17571)

* Bump data model lib 2.12.1 -> 2.13.0 (includes extended enum with the external service )

**Deprecated**

1.1.3 (2021-09-20)
------------------

**Added**

**Fixed**

* Add spacing to cost overview (`#810 <https://github.com/qbicsoftware/offer-manager-2-portlet/issues/810>`_)

**Dependencies**

**Deprecated**

1.1.2 (2021-09-17)
------------------

**Added**

**Fixed**

* Catch exception during discount generation for negative product prices (`#808 <https://github.com/qbicsoftware/offer-manager-2-portlet/pull/808>`_)

**Dependencies**

**Deprecated**

1.1.1 (2021-09-14)
------------------

**Added**

**Fixed**

* Avoid page breaks in footer content (`#798 <https://github.com/qbicsoftware/offer-manager-2-portlet/pull/798>`_)

* Disable buttons after deselecting offers (`#799 <https://github.com/qbicsoftware/offer-manager-2-portlet/issues/799>`_)

* Avoid division by zero during offer content creation (`#806 <https://github.com/qbicsoftware/offer-manager-2-portlet/pull/806>`_)

**Dependencies**

* Bump data model lib 2.12.0 -> 2.12.1 (includes important bug fixes)

**Deprecated**


1.1.0 (2021-08-17)
------------------

**Added**

* Function to apply the quantity discount price (`#707 <https://github.com/qbicsoftware/offer-manager-2-portlet/pull/707>`_)

* Add automatic data management discount generation (`#783 <https://github.com/qbicsoftware/offer-manager-2-portlet/pull/783>`_)

* Adds a new DTO OfferItem which represents an item on the offer (`#728 <https://github.com/qbicsoftware/offer-manager-2-portlet/pull/728>`_)

* Display total quantity discount included in the offer in the offer overview (`#686 <https://github.com/qbicsoftware/offer-manager-2-portlet/issues/686>`_)

* Adds a new DTO OfferContent which represents the content of an offer (`#724 <https://github.com/qbicsoftware/offer-manager-2-portlet/pull/724>`_)

* Uses new DTOs instead of performing unnecessary computations in PDF creation (`#725 <https://github.com/qbicsoftware/offer-manager-2-portlet/pull/725>`_)

* Adds the CreateOfferContent use case (`#735 <https://github.com/qbicsoftware/offer-manager-2-portlet/pull/735>`_)

* Add discount unit and percentage to discount items, specify the discount item description (`#755 <https://github.com/qbicsoftware/offer-manager-2-portlet/pull/755>`_)

* Adds the total price before VAT is applied to an offer (`#754 <https://github.com/qbicsoftware/offer-manager-2-portlet/pull/754>`_)

* Add new offer layout and css  (`#768 <https://github.com/qbicsoftware/offer-manager-2-portlet/pull/768>`_)

**Fixed**

* Invert quantity discount in order to be able to display size of discount (`#710 <https://github.com/qbicsoftware/offer-manager-2-portlet/pull/710>`_

* Include Offer ID Prefix in Offer PDF filename (`#685 <https://github.com/qbicsoftware/offer-manager-2-portlet/issues/685>`_)

* Differentiate between internal and external product unit prices (`#713 <https://github.com/qbicsoftware/offer-manager-2-portlet/pull/713>`_)

* Include internal and external prices as well as facilities in the products (`#712 <https://github.com/qbicsoftware/offer-manager-2-portlet/pull/712>`_)

* The internal and external product unit prices are shown in the user interface (`#723 <https://github.com/qbicsoftware/offer-manager-2-portlet/pull/723>`_)

* Reset the offer update view, when a new offer is selected in the offer overview (`#716 <https://github.com/qbicsoftware/offer-manager-2-portlet/issues/716>`_)

* Offers without an experimental design can now be updated (`#726 <https://github.com/qbicsoftware/offer-manager-2-portlet/issues/726>`_)

* Creating a product switches name and description (`#731 <https://github.com/qbicsoftware/offer-manager-2-portlet/pull/731>`_)

* Space names are now validated (`#736 <https://github.com/qbicsoftware/offer-manager-2-portlet/pull/736>`_)

* Fix rounding issue for discount unit price (`#784 <https://github.com/qbicsoftware/offer-manager-2-portlet/pull/784>`_)

* Fix app trying to unregister file downloaders that were not previously registered (`#796 <https://github.com/qbicsoftware/offer-manager-2-portlet/pull/796>`_)

* Restructured the cost summary to solve (`#784 <https://github.com/qbicsoftware/offer-manager-2-portlet/pull/784>`_ and `#689 <https://github.com/qbicsoftware/offer-manager-2-portlet/pull/689>`_)

**Dependencies**

* ``mysql:mysql-connector-java:8.0.24`` -> ``8.0.25``

* ``org.mariadb.jdbc:mariadb-java-client:2.7.2`` -> ``2.7.3``

* ``life.qbic:data-model-lib:2.8.2`` -> ``2.11.0``

**Deprecated**

1.0.5 (2021-06-15)
------------------

**Added**

* Increase Space allocated for product description in Offer PDF (`#675 <https://github.com/qbicsoftware/offer-manager-2-portlet/issues/675>`_)

* Allocate space for each productItem dependent on number of characters in each property (`#675 <https://github.com/qbicsoftware/offer-manager-2-portlet/issues/675>`_)

**Fixed**

* Limit width of product description columns (`#673 <https://github.com/qbicsoftware/offer-manager-2-portlet/issues/673>`_)

**Dependencies**

* Bump data model lib 2.8.1 -> 2.8.2 and therefore adds 'Batch' as unit for service products

**Deprecated**


1.0.5 (2021-06-15)
------------------

**Added**

* Increase space allocated for product description in Offer PDF based on the number of characters (`#675 <https://github.com/qbicsoftware/offer-manager-2-portlet/issues/675>`_)

**Fixed**

* Limit width of product description columns (`#673 <https://github.com/qbicsoftware/offer-manager-2-portlet/issues/673>`_)

**Dependencies**

* Bump data model lib 2.8.1 -> 2.8.2 and therefore adds 'Batch' as unit for service products


**Deprecated**


1.0.4 (2021-06-14)
------------------

**Added**

* DataStorage and ProjectManagement ProductItems are now included in the overhead calculation of an Offer Entity (`#665 <https://github.com/qbicsoftware/offer-manager-2-portlet/issues/665>`_)

* Offer PDF overhead listing now includes DataStorage and ProjectManagement ProductItems (`#665 <https://github.com/qbicsoftware/offer-manager-2-portlet/issues/665>`_)

**Fixed**

* Renamed ProductGroup.DATA_MANAGEMENT Enum Item to PROJECT_MANAGEMENT_AND_DATA_MANAGEMENT in OfferToPDFConverter to make it more explicit (`#666 <https://github.com/qbicsoftware/offer-manager-2-portlet/pull/666>`_)

**Dependencies**

* Bump data model lib 2.8.0 -> 2.8.1 and therefore adds 'Flow cell' as unit for service products

**Deprecated**

* Deprecate life.qbic.business.offers.Offer.getOverheadItems() since all ProductItems now have an associated overhead cost (`#665 <https://github.com/qbicsoftware/offer-manager-2-portlet/issues/665>`_)

* Deprecate life.qbic.business.offers.Offer.getNoOverheadItems() since all ProductItems now have an associated overhead cost (`#665 <https://github.com/qbicsoftware/offer-manager-2-portlet/issues/665>`_)

* Deprecate life.qbic.business.offers.Offer.getOverheadItemsNet() since all ProductItems now have an associated overhead cost (`#665 <https://github.com/qbicsoftware/offer-manager-2-portlet/issues/665>`_)

* Deprecate life.qbic.business.offers.Offer.getNoOverheadItemsNet() since all ProductItems now have an associated overhead cost (`#665 <https://github.com/qbicsoftware/offer-manager-2-portlet/issues/665>`_)

* Deprecate life.qbic.business.offers.Offer.itemsWithOverheadNetPrice since all ProductItems now have an associated overhead cost (`#665 <https://github.com/qbicsoftware/offer-manager-2-portlet/issues/665>`_)

* Deprecate life.qbic.business.offers.Offer.itemsWithoutOverheadNetPrice since all ProductItems now have an associated overhead cost (`#665 <https://github.com/qbicsoftware/offer-manager-2-portlet/issues/665>`_)

* Deprecate life.qbic.business.offers.Offer.itemsWithOverheads from Offer entity since all ProductItems now have an associated overhead cost (`#665 <https://github.com/qbicsoftware/offer-manager-2-portlet/issues/665>`_)

* Deprecate life.qbic.business.offers.Offer.itemsWithoutOverhead since all ProductItems now have an associated overhead cost (`#665 <https://github.com/qbicsoftware/offer-manager-2-portlet/issues/665>`_)

1.0.3 (2021-06-10)
------------------

**Added**

**Fixed**

* Assign proteomic and metabolomic products to product group data generation (`#660 <https://github.com/qbicsoftware/offer-manager-2-portlet/issues/660>`_)

**Dependencies**

**Deprecated**

1.0.2 (2021-06-07)
------------------

**Added**

**Fixed**

* Update of person entries is no longer blocked by ignored affiliation selection (`#654 <https://github.com/qbicsoftware/offer-manager-2-portlet/issues/654>`_)

* Disable the affiliation attachment during person update if no affiliation is selected (`#656 <https://github.com/qbicsoftware/offer-manager-2-portlet/issues/656>`_)

**Dependencies**

**Deprecated**

1.0.1 (2021-06-07)
------------------

**Added**

* More units for the service products

**Fixed**

**Dependencies**

* Bump data model lib 2.7.0 -> 2.8.0 and therefore adds more units for service products

**Deprecated**

1.0.0 (2021-06-04)
------------------

**Added**

* Only list overhead costs of productGroup items present in offer in Offer PDF  (`#643 <https://github.com/qbicsoftware/offer-manager-2-portlet/issues/643>`_)

* A person can now be updated if only the associated academic title is changed (`#567 <https://github.com/qbicsoftware/offer-manager-2-portlet/issues/567>`_)

* Small modifications for the offer layout  (`#620 <https://github.com/qbicsoftware/offer-manager-2-portlet/issues/620>`_)

* Filter for product id in `life.qbic.portal.offermanager.components.offer.create.SelectItemsView` (`#599 <https://github.com/qbicsoftware/offer-manager-2-portlet/issues/599>`_)

* Customers can now be updated in the selection step of creating a new offer (`#611 <https://github.com/qbicsoftware/offer-manager-2-portlet/pull/611>`_)

* Experimental design description is now added to the detailed project description during offer creation. (`#623 <https://github.com/qbicsoftware/offer-manager-2-portlet/pull/623>`_)

* Improve display of product descriptions during product modification (`#631 <https://github.com/qbicsoftware/offer-manager-2-portlet/issues/631>`_)

* Adapt dynamically generated offer PDF layout to match html template (`#613 <https://github.com/qbicsoftware/offer-manager-2-portlet/issues/613>`_)

* Limit grid size for offer item overviews while creating an offer (`#646 <https://github.com/qbicsoftware/offer-manager-2-portlet/issues/646>`_)

**Fixed**

* User ID of a person is set in database during person creation/update (`#616 <https://github.com/qbicsoftware/offer-manager-2-portlet/issues/616>`_)

* Layout of total price row in price summary of offer pdf stays inline (`#615 <https://github.com/qbicsoftware/offer-manager-2-portlet/issues/615>`_)

* Deprecated project identifier format assumption leads to failing parsing (`#617 <https://github.com/qbicsoftware/offer-manager-2-portlet/issues/617>`_)

* Restructured DependencyManager (`#624 <https://github.com/qbicsoftware/offer-manager-2-portlet/pull/624>`_) fixing (`#612 <https://github.com/qbicsoftware/offer-manager-2-portlet/issues/612>`_)

* Fix sorting by product id for the offer creation process (`#599 <https://github.com/qbicsoftware/offer-manager-2-portlet/issues/599>`_)

* Disable empty selection for address addition combobox (`#565 <https://github.com/qbicsoftware/offer-manager-2-portlet/issues/565>`_)

* Fix validation error being shown after an affiliation is added to a person (`#566 <https://github.com/qbicsoftware/offer-manager-2-portlet/issues/566>`_)

* Disable 'Archive Product' button after deselection (`#547 <https://github.com/qbicsoftware/offer-manager-2-portlet/issues/547>`_)

* Fix project information showing a validation error upon successful offer creation (`#633 <https://github.com/qbicsoftware/offer-manager-2-portlet/pull/633>`_)

* Fix `Copy Product` button being enabled even though the information was not changed (`#568 <https://github.com/qbicsoftware/offer-manager-2-portlet/issues/568>`_)

* Fix the misplaced product description panel (`#640 <https://github.com/qbicsoftware/offer-manager-2-portlet/issues/640>`_)

**Dependencies**

**Deprecated**

1.0.0-rc.1 (2021-04-25)
-----------------------

**Added**

* Introduce filterable Project Manager column to offer overview (`#576 <https://github.com/qbicsoftware/offer-manager-2-portlet/issues/576>`_)

* Adds ability to filter by project identifiers in the offer overview (`#591 <https://github.com/qbicsoftware/offer-manager-2-portlet/pull/591>`_)

* Displays a total price overview on the first offer page, including taxes, net cost and total cost (`#559 <https://github.com/qbicsoftware/offer-manager-2-portlet/issues/559>`_)

* Include overhead cost in total price overview on the first offer page (`#593 <https://github.com/qbicsoftware/offer-manager-2-portlet/pull/593>`_)

* Add a column filter option which takes a predicate as argument (`#589 <https://github.com/qbicsoftware/offer-manager-2-portlet/pull/589>`_)

* Enable Github workflow for changelog update checks (`#595 <https://github.com/qbicsoftware/offer-manager-2-portlet/pull/595>`_)

* Enable sorting products by productId (MaintainProductView) (`#574 <https://github.com/qbicsoftware/offer-manager-2-portlet/issues/574>`_)

* Adjust agreement text in offer template and move table header below section/product category title in offer html template (`#606 <https://github.com/qbicsoftware/offer-manager-2-portlet/pull/606>`_)

* Move table header below section/product category title in offer html template (`#604 <https://github.com/qbicsoftware/offer-manager-2-portlet/pull/604>`_)

* Provide entry point to affiliation creation in CreatePersonView (`#601 <https://github.com/qbicsoftware/offer-manager-2-portlet/pull/601>`_)

**Fixed**

* Update and fix broken offer template (`#597 <https://github.com/qbicsoftware/offer-manager-2-portlet/issues/597>`_)

* Enumeration of product items increases over all productGroups in Offer PDF (`#562 <https://github.com/qbicsoftware/offer-manager-2-portlet/issues/562>`_)

* Improve test description for external non-academic customers (`#605 <https://github.com/qbicsoftware/offer-manager-2-portlet/pull/605>`_)

* Tax cost for offers outside of germany is set to 0 (`#575 <https://github.com/qbicsoftware/offer-manager-2-portlet/issues/575>`_)

* App won't freeze after creation of multiple projects (`#558 <https://github.com/qbicsoftware/offer-manager-2-portlet/issues/558>`_)

**Dependencies**

* ``life.qbic.data-model-lib:2.5.0`` -> ``2.7.0`` (`#606 <https://github.com/qbicsoftware/offer-manager-2-portlet/pull/606>`_)

* ``com.vaadin.vaadin-bom:8.12.3`` -> ``8.13.0`` (`#572 <https://github.com/qbicsoftware/offer-manager-2-portlet/pull/572>`_)

* ``org.spockframework.spock-bom:2.0-M4-groovy-3.0`` -> ``2.0-groovy-3.0`` ( `#588 <https://github.com/qbicsoftware/offer-manager-2-portlet/pull/588>`_)

**Deprecated**

* Deprecate OfferOverview Constructor to allow for inclusion of ProjectManager (`#576 <https://github.com/qbicsoftware/offer-manager-2-portlet/issues/576>`_)


1.0.0-beta.2 (2021-04-30)
-------------------------

**Added**

**Fixed**

* Duplicate product identifiers are no longer generated (`#551 <https://github.com/qbicsoftware/offer-manager-2-portlet/pull/551>`_) fixes (`#546 <https://github.com/qbicsoftware/offer-manager-2-portlet/pull/546>`_)

* Rephrased error message for product creation failure (`#552 <https://github.com/qbicsoftware/offer-manager-2-portlet/issues/263>`_)

**Dependencies**

**Deprecated**


1.0.0-beta.1 (2021-04-27)
-----------------------------------

**Added**

* Product selection now notifies a user if the provided input is incorrect and disables the button until the given information is valid (`#407 <https://github.com/qbicsoftware/offer-manager-2-portlet/issues/407>`_)

* Experimental designs can be defined for an offer (`#263 <https://github.com/qbicsoftware/offer-manager-2-portlet/issues/263>`_)

* New dropdown menu bar (`#490 <https://github.com/qbicsoftware/offer-manager-2-portlet/issues/490>`_)

* Jump back to maintain view after product creation/update (`#481 <https://github.com/qbicsoftware/offer-manager-2-portlet/issues/481>`_)

* Search for affiliations is now possible (`#533 <https://github.com/qbicsoftware/offer-manager-2-portlet/pull/533>`_)

* Add confirmation request for product archiving (`#528 <https://github.com/qbicsoftware/offer-manager-2-portlet/issues/528>`_)

* Allow offer updated when experimental design has changed (`#515 <https://github.com/qbicsoftware/offer-manager-2-portlet/issues/515>`_)

**Fixed**

* Add timeout of 10 second to PDF rendering (`#494 <https://github.com/qbicsoftware/offer-manager-2-portlet/pull/494>`_)

* Allow resetting the date picker in the offer overview (`#486 <https://github.com/qbicsoftware/offer-manager-2-portlet/issues/486>`_)

* Naming of the downloaded offer pdf is consistent (`#498 <https://github.com/qbicsoftware/offer-manager-2-portlet/issues/498>`_)

* Reset the view after an offer has been created  (`#495 <https://github.com/qbicsoftware/offer-manager-2-portlet/issues/495>`_)

* Provide ISO 8601 date format renderer for offer overview table (`#299 <https://github.com/qbicsoftware/offer-manager-2-portlet/issues/299>`_)

* Provide functionality to remove items from an offer (`#516 <https://github.com/qbicsoftware/offer-manager-2-portlet/issues/516>`_)

* Adds amount to existing items on the offer (`#462 <https://github.com/qbicsoftware/offer-manager-2-portlet/issues/462>`_)

* Make filter for service product view work  (`#523 <https://github.com/qbicsoftware/offer-manager-2-portlet/issues/523>`_)

* Add validation for project information input for offer creation/update (`#488 <https://github.com/qbicsoftware/offer-manager-2-portlet/issues/488>`_)

**Dependencies**

* Bump Vaadin 8.12.0 -> 8.12.3

**Deprecated**


1.0.0-alpha.6 (2021-04-13)
-----------------------------------

**Added**

* Filter message in grids is now dependent on column ID (`#457 <https://github.com/qbicsoftware/offer-manager-2-portlet/pull/457>`_)

* Add link to item table in offer pdf (`#469 <https://github.com/qbicsoftware/offer-manager-2-portlet/pull/469>`_)

**Fixed**

* Allow natural sorting of prices by their double value as opposed to their String representation (`#458 <https://github.com/qbicsoftware/offer-manager-2-portlet/pull/458>`_)

* Update position of country string in affiliation summary during customer creation (`#453 <https://github.com/qbicsoftware/offer-manager-2-portlet/pull/453>`_)

* Input fields of the CreateProductView are cleared after successful product creation(`#454 <https://github.com/qbicsoftware/offer-manager-2-portlet/pull/454>`_)

* Shows the same affiliation organisation only once and maps it correctly to the address addition (`#448 <https://github.com/qbicsoftware/offer-manager-2-portlet/pull/448>`_)

* Fix fail based on double clicking a customer in the SelectCustomerView for in the offer creation process (`#452 <https://github.com/qbicsoftware/offer-manager-2-portlet/pull/452>`_)

* Make adding a new affiliation more intuitive (`#467 <https://github.com/qbicsoftware/offer-manager-2-portlet/pull/467>`_) (`#463 <https://github.com/qbicsoftware/offer-manager-2-portlet/pull/463>`_)

* Harmonized Title and label structure across all views (`#455 <https://github.com/qbicsoftware/offer-manager-2-portlet/pull/455>`_)

* Updating a person removes the old entry also from the customerResourceService and projectManagerResourceService (`#456 <https://github.com/qbicsoftware/offer-manager-2-portlet/pull/456>`_)

* Make empty address addition explicitly selectable during person creation and update (`#474 <https://github.com/qbicsoftware/offer-manager-2-portlet/pull/474>`_)

* Replace 'customer' with 'person' in menu bar for the headings 'create customer' and 'search person' (`#473 <https://github.com/qbicsoftware/offer-manager-2-portlet/pull/473>`_)

* Update position of country string in affiliation summary during customer creation (`#453 <https://github.com/qbicsoftware/offer-manager-2-portlet/pull/453>`_)

* Input fields of the CreateProductView are cleared after successful product creation(`#454 <https://github.com/qbicsoftware/offer-manager-2-portlet/pull/454>`_)

**Dependencies**

**Deprecated**

1.0.0-alpha.5 (2021-04-07)
-----------------------------------

**Added**

* Proteomic and Metabolomic Products can now be selected and included in an Offer (`#425 <https://github.com/qbicsoftware/offer-manager-2-portlet/pull/425>`_)

* Link offers to project now. The ``life.qbic.business.offers.Offer`` and ``life.qbic.portal.offermanager.dataresources.offers``
  have been extended with a new property to associate it with
  an existing project by its project identifier. (`#410 <https://github.com/qbicsoftware/offer-manager-2-portlet/pull/410>`_).

* Finalized the ``life.qbic.business.products.archive.ArchiveProduct`` and ``life/qbic/business/products/create/CreateProduct.groovy``
  use cases of the product maintenance and creation feature (`#411 <https://github.com/qbicsoftware/offer-manager-2-portlet/pull/411>`_).

* After a project has been created from an offer, the offer overview is updated accordingly
  (`#427 <https://github.com/qbicsoftware/offer-manager-2-portlet/pull/427>`_)

* Add the UpdatePersonView to separate the Update and Create Person use cases more consequently (`#436 <https://github.com/qbicsoftware/offer-manager-2-portlet/pull/436>`_)

* Proteomic and Metabolomic Products are now included in the Offer PDF (`#420 <https://github.com/qbicsoftware/offer-manager-2-portlet/pull/420>`_)

**Fixed**

* Popup based Notifications are now properly centered in a liferay-environment(`#428 <https://github.com/qbicsoftware/offer-manager-2-portlet/pull/428>`_)

* Properly refresh the SearchPersonView after Updating a Person (`#436 <https://github.com/qbicsoftware/offer-manager-2-portlet/pull/436>`_)

* Products that cannot be read from the database are skipped (`#444 <https://github.com/qbicsoftware/offer-manager-2-portlet/pull/444>`_)

**Dependencies**

**Deprecated**

1.0.0-alpha.4 (2021-03-16)
--------------------------

**Added**

* Introduce subtotals in Offer PDF ProductItem Table(`#349 <https://github.com/qbicsoftware/offer-manager-2-portlet/pull/349>`_)

* Add logging with throwable cause (`#371 <https://github.com/qbicsoftware/offer-manager-2-portlet/pull/371>`_)

* Introduce distinction of products in the offer PDF according to the associated service
  data generation, data analysis and project management (`#364 <https://github.com/qbicsoftware/offer-manager-2-portlet/pull/364>`_)

* Introduce overheadRatio property to life.qbic.business.offers.Offer
  used to show the applied overhead markup in the pricing footer of the Offer PDF(`#362 <https://github.com/qbicsoftware/offer-manager-2-portlet/pull/362>`_)

* Introduce first draft for OpenBis based project space and project creation (`#396 <https://github.com/qbicsoftware/offer-manager-2-portlet/pull/396>`_)

* Introduce first draft for product maintenance and creation (`#392 <https://github.com/qbicsoftware/offer-manager-2-portlet/pull/392>`_)

**Fixed**

* User cannot select other offers from the overview anymore, during the offer details are loaded
  after a selection. Selection is enabled again after the resource has been loaded. This solves a
  not yet reported issue that can be observed when dealing with a significant network delay. (`#374 <https://github.com/qbicsoftware/offer-manager-2-portlet/pull/374>`_)

**Dependencies**

**Deprecated**

1.0.0-alpha.3 (2021-03-02)
--------------------------

**Added**

* Authorization based on user roles. Two new roles have been introduced that represent
  the organisational roles project manager `Role.PROJECT_MANAGER` and offer admin `Role
  .OFFER_ADMIN`. The administrator will provide access to additional app features, such as the
  upcoming service product maintenance interface.

* Introduce Offer retrieval via Fetch Offer Use Case (`#344 <https://github.com/qbicsoftware/offer-manager-2-portlet/pull/344>`_)

**Fixed**

* Update the agreement section of the offer (`#329 <https://github.com/qbicsoftware/offer-manager-2-portlet/issues/329>`_)

* Make the offer controls more intuitive (`#341 <https://github.com/qbicsoftware/offer-manager-2-portlet/issues/341>`_)

* Update offers without changes is not possible anymore (`#222 <https://github.com/qbicsoftware/offer-manager-2-portlet/issues/222>`_)

* Rename CreateCustomer and UpdateCustomer classes and methods (`#315 <https://github.com/qbicsoftware/offer-manager-2-portlet/issues/315>`_)

**Dependencies**

**Deprecated**



1.0.0-SNAPSHOT (2020-11-09)

* Create project with QUBE

* Create project modules infrastructure and domain

* Possibility to list all affiliations stored in the database

* Possibility to list all customers and project managers stored in the database

* Possibility to list all offers stored in the database

* Create and add a new customer to the database

* Create and add a new affiliation to the database

* Create and add a new offer to the database

* Possibility to list all packages stored in the database

* Add the option to create a customer while creating an offer

* Show affiliation details when selecting an affiliation for a customer

* Possibility to filter for customers in table overview

* Show overview over all offers in database

* Possibility to download an offer

* Possibility to abort customer creation

* Dynamic cost overview upon offer creation

* Calculate prices of an offer (VAT, overheads, net price)

* Create an unique offer id

* Addressed `#124 <https://github.com/qbicsoftware/offer-manager-2-portlet/issues/124>`_

* Addressed `#234 <https://github.com/qbicsoftware/offer-manager-2-portlet/issues/234>`_

* Addressed `#246 <https://github.com/qbicsoftware/offer-manager-2-portlet/issues/246>`_

* Addressed `#260 <https://github.com/qbicsoftware/offer-manager-2-portlet/issues/260>`_

* Addressed `#269 <https://github.com/qbicsoftware/offer-manager-2-portlet/issues/269>`_

* Addressed `#270 <https://github.com/qbicsoftware/offer-manager-2-portlet/issues/270>`_

* Addressed `#271 <https://github.com/qbicsoftware/offer-manager-2-portlet/issues/270>`_

* Addressed `#275 <https://github.com/qbicsoftware/offer-manager-2-portlet/issues/275>`_

* Addressed `#282 <https://github.com/qbicsoftware/offer-manager-2-portlet/issues/282>`_

* Addressed `#295 <https://github.com/qbicsoftware/offer-manager-2-portlet/issues/295>`_

* Addressed `#309 <https://github.com/qbicsoftware/offer-manager-2-portlet/issues/309>`_

* Replace the project description with project objective (`#339 <https://github.com/qbicsoftware/offer-manager-2-portlet/pull/339>`_)

* Added support to configure the chromium browser executable. An environment variable
  `CHROMIUM_ALIAS` has been introduced that can be set to define the chromium executable in the
  deployment system of the application. Addresses `#336 <https://github.com/qbicsoftware/offer-manager-2-portlet/issues/336>`_


**Fixed**

* Fixed (`#324 <https://github.com/qbicsoftware/offer-manager-2-portlet/issues/324>`_) no affiliation preloaded into view upon customer update with (`#328 <https://github.com/qbicsoftware/offer-manager-2-portlet/pull/328>`_)

**Dependencies**

**Deprecated**
