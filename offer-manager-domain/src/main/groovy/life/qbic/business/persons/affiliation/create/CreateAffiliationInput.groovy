package life.qbic.business.persons.affiliation.create

import life.qbic.business.persons.affiliation.Affiliation

/**
 * The input interface for the Create Affiliation use case.
 *
 * To start the use case, only one method needs to be called.
 *
 * @since 1.0.0
 */
interface CreateAffiliationInput {

    /**
     * Creates a new {@link Affiliation} in the customer database
     * @param affiliation The affiliation to be created
     */
    void createAffiliation(Affiliation affiliation)

}
