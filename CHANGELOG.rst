==========
Changelog
==========

This project adheres to `Semantic Versioning <https://semver.org/>`_.

1.0.0-alpha.7-SNAPSHOT (2021-04-14)
-----------------------------------

**Added**

* Product selection now notifies a user if the provided input is incorrect and disables the button until the given information is valid (`#407 <https://github.com/qbicsoftware/offer-manager-2-portlet/issues/407>`_)

* Experimental designs can be defined for an offer (`#263 <https://github.com/qbicsoftware/offer-manager-2-portlet/issues/263>` _)

**Fixed**

* Add timeout of 10 second to PDF rendering (`#494 <https://github.com/qbicsoftware/offer-manager-2-portlet/pull/494>`_)


**Dependencies**

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
----------------------------------------------

**Added**

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
