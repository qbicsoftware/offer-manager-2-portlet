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

System setup
------------

In order to enable the offer manager app to convert an offer as PDF, you need to define a
environment variable in the system's environment accessible by the application.

The app will look for an environment variable `CHROMIUM_ALIAS`, so make sure to have set it.

In the example of the local test environment, a simple `export CHROMIUM_ALIAS=chromium` is
sufficient.


Credits
-------

This project was created with qube_.

.. _qube: https://github.com/qbicsoftware/qube
