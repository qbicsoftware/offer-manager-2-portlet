qOffer 2.0
-----------------------------------

.. image:: https://github.com/qbicsoftware/qOffer_2.0/workflows/Build%20Maven%20Package/badge.svg
    :target: https://github.com/qbicsoftware/qOffer_2.0/workflows/Build%20Maven%20Package/badge.svg
    :alt: Github Workflow Build Maven Package Status

.. image:: https://github.com/qbicsoftware/qOffer_2.0/workflows/Run%20Maven%20Tests/badge.svg
    :target: https://github.com/qbicsoftware/qOffer_2.0/workflows/Run%20Maven%20Tests/badge.svg
    :alt: Github Workflow Tests Status

.. image:: https://github.com/qbicsoftware/qOffer_2.0/workflows/QUBE%20lint/badge.svg
    :target: https://github.com/qbicsoftware/qOffer_2.0/workflows/QUBE%20lint/badge.svg
    :alt: qube Lint Status

.. image:: https://readthedocs.org/projects/qOffer-2.0/badge/?version=latest
    :target: https://qOffer-2.0.readthedocs.io/en/latest/?badge=latest
    :alt: Documentation Status

.. image:: https://flat.badgen.net/dependabot/thepracticaldev/dev.to?icon=dependabot
    :target: https://flat.badgen.net/dependabot/thepracticaldev/dev.to?icon=dependabot
    :alt: Dependabot Enabled


qOffer assists in managing and creating offers

* Free software: MIT
* Documentation: https://qOffer-2.0.readthedocs.io.

Features
--------

* Create new offers
* Update stored offers
* Add new customers to the database
* Add new affiliations to the database
* Search for offers in the database

Usage information
------------------

Build the project with

.. code-block: bash
mvn clean package

Deploy the created portlet in a Liferay instance.
Make sure that the chromium is installed on the server, it is required for the download of the offer.

Local testing
--------------

Make sure to have chromium installed on your laptop.
You can do so via homebrew

.. code-block: bash
brew install --cask chromium

Create the project with

.. code-block: bash
mvn clean jetty:run

open the application through localhost:8080


Credits
-------

This project was created with qube_.

.. _qube: https://github.com/qbicsoftware/qube
