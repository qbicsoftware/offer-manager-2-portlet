TOMATO - The Offer MAnager Tool
-----------------------------------

Tomato assists in managing and creating offers

|maven-build| |maven-test| |code-ql| |release|
|license| |java| |groovy|

How to run
-----------

Build the project with Maven and Java 8

.. code-block:: bash

  mvn clean package

Deploy the created portlet in a Liferay instance.
Make sure that Chromium is installed on the server, it is required for the download of the offer. (see `How to use`_)

**Local testing**

Run the project with

.. code-block:: bash

  mvn clean jetty:run -Denvironment=testing

And open the application through ``localhost:8080``. The system property ``-Denvironment=testing`` will
enable to application to run in test mode and does not require a successful user role
determination to access all the features.


The WAR file will be created in the ``/target`` folder:

.. code-block:: bash
    |-target
    |---offer-manager-app-1.0.0.war
    |---...


How to use
-----------

**Authorization and roles**


The offer manager app currently distinguishes between two roles: ``Role.PROJECT_MANAGER`` and
``Role.OFFER_ADMIN``. The admin role provides access to features such as the service
product maintenance interface, and only dedicated users with the admin role will be able to
access it.

The current production implementation of the ``RoleService`` interface is used for deployment in an
``Liferay 6.2 GA6`` environment and maps the Liferay *site-roles* `"Project Manager"` and `"Offer
Administration"` to the internal app role representation.

If an authenticated user has none of these roles, she will not be able to execute the application.

**Configuration**

.. list-table::

    * - **Environment**
      - **Description**
      - **Default Value**
    * - datasource.url
      - Connection to datasource
      - https://openbis.domain.de
    * - datasource.api.ur
      - Connection to API datasource
      - https://openbis.domain.de/api/path
    * - datasource.user
      - The user name for the datasource
      - myuser
    * - datasource.password
      - The password for the datasource
      - mypassword
    * - mysql.host
      - Host address for MySQL database
      - 123.4.56.789
    * - mysql.pass
      - Password for MySQL database
      - mypassword
    * - mysql.user
      - MySQL user
      - mysqluser
    * - mysql.db
      - MySQL database
      - my_sql_database_name
    * - mysql.port
      - Port to MySQL database
      - 3306
    * - portal.user
      - Username for QBiC portal
      - qbcjb02


**System setup**

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


In order to enable the offer manager app to convert an offer as PDF, you need to define a
environment variable in the system's environment accessible by the application.

The app will look for an environment variable ``CHROMIUM_EXECUTABLE``, so make sure to have set it.


License
-------

This work is licensed under the `MIT license <https://mit-license.org/>`_.

**Note**: This work uses the `Vaadin Framework <https://github.com/vaadin>`_, which is licensed under `Apache 2.0 <https://www.apache.org/licenses/LICENSE-2.0>`_.


.. |maven-build| image:: https://github.com/qbicsoftware/offer-manager-2-portlet/workflows/Build%20Maven%20Package/badge.svg
    :target: https://github.com/qbicsoftware/offer-manager-2-portlet/actions/workflows/build_package.yml
    :alt: Github Workflow Build Maven Package Status

.. |maven-test| image:: https://github.com/qbicsoftware/offer-manager-2-portlet/workflows/Run%20Maven%20Tests/badge.svg
    :target: https://github.com/qbicsoftware/offer-manager-2-portlet/actions/workflows/run_tests.yml
    :alt: Github Workflow Tests Status  

.. |release| image:: https://img.shields.io/github/v/release/qbicsoftware/offer-manager-2-portlet.svg
    :target: https://github.com/qbicsoftware/offer-manager-2-portlet/releases
    :alt: Release status

.. |license| image:: https://img.shields.io/github/license/qbicsoftware/offer-manager-2-portlet
    :target: https://github.com/qbicsoftware/offer-manager-2-portlet/blob/master/LICENSE
    :alt: Project Licence

.. |java| image:: https://img.shields.io/badge/language-java-blue.svg
    :alt: Written in Java

.. |groovy| image:: https://img.shields.io/badge/language-groovy-blue.svg
    :alt: Written in Groovy
    
.. |code-ql| image:: https://github.com/qbicsoftware/offer-manager-2-portlet/actions/workflows/codeql-analysis.yml/badge.svg?branch=master
    :target: https://github.com/qbicsoftware/offer-manager-2-portlet/actions/workflows/codeql-analysis.yml
    :alt: CodeQL
