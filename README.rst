TOMATO - The Offer MAnager Tool
-----------------------------------

|maven-build| |maven-test| |release| |java| |groovy|

Tomato assists in managing and creating offers

* Free software: MIT

Features
--------

* Create new offers
* Create additional offer versions of existing offers
* Manage person entries and affiliations
* Search for offers in the database

Usage information
------------------

Build the project with

.. code-block:: bash

  mvn clean package

Deploy the created portlet in a Liferay instance.
Make sure that the chromium is installed on the server, it is required for the download of the offer.

Local testing
--------------

Make sure to have chromium installed on your laptop.
In case you use a Mac, can do so via homebrew

.. code-block:: bash

  brew install --cask chromium

If you want to build the chromium browser from source please see the instructions on `the chromium website <https://www.chromium.org/developers/how-tos/get-the-code>`_
For some Linux system the application is also provided by the name ``chromium-browser``

.. code-block:: bash

  sudo apt-get install chromium-browser

After successful installation please provide the offer manager with your chromium installation by setting

.. code-block:: bash

  export CHROMIUM_EXECUTABLE=<your/path/to/chromium>

Run the project with

.. code-block:: bash

  mvn clean jetty:run -Denvironment=testing

And open the application through ``localhost:8080``. The system property ``-Denvironment=testing`` will
enable to application to run in test mode and does not require a successful user role
determination to access all the features.

Authorization and roles
-----------------------

The offer manager app currently distinguishes between two roles: ``Role.PROJECT_MANAGER`` and
``Role.OFFER_ADMIN``. The admin role provides access to features such as the service
product maintenance interface, and only dedicated users with the admin role will be able to
access it.

The current production implementation of the ``RoleService`` interface is used for deployment in an
``Liferay 6.2 GA6`` environment and maps the Liferay *site-roles* `"Project Manager"` and `"Offer
Administration"` to the internal app role representation.

If an authenticated user has none of these roles, she will not be able to execute the application.


System setup
------------

In order to enable the offer manager app to convert an offer as PDF, you need to define a
environment variable in the system's environment accessible by the application.

The app will look for an environment variable ``CHROMIUM_EXECUTABLE``, so make sure to have set it.

In the example of the local test environment, a simple
::

  export CHROMIUM_EXECUTABLE=chromium

is sufficient.


Credits
-------

This project was created with qube_.

.. _qube: https://github.com/qbicsoftware/qube

.. |maven-build| image:: https://github.com/qbicsoftware/offer-manager-2-portlet/workflows/Build%20Maven%20Package/badge.svg
    :target: https://github.com/qbicsoftware/offer-manager-2-portlet/workflows/Build%20Maven%20Package/badge.svg
    :alt: Github Workflow Build Maven Package Status

.. |maven-test| image:: https://github.com/qbicsoftware/offer-manager-2-portlet/workflows/Run%20Maven%20Tests/badge.svg
    :target: https://github.com/qbicsoftware/offer-manager-2-portlet/workflows/Run%20Maven%20Tests/badge.svg
    :alt: Github Workflow Tests Status  

.. |release| image:: https://img.shields.io/github/v/release/qbicsoftware/offer-manager-2-portlet.svg
    :target: https://github.com/qbicsoftware/offer-manager-2-portlet/release
    :alt: Release status 

.. |java| image:: https://img.shields.io/badge/language-java-blue.svg
    :alt: Written in Java

.. |groovy| image:: https://img.shields.io/badge/language-groovy-blue.svg
    :alt: Written in Groovy
    
