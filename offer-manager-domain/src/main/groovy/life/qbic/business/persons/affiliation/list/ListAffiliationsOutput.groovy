package life.qbic.business.persons.affiliation.list

import life.qbic.business.persons.Affiliation

/**
 * This interface serves as output for the List Affiliation use case
 *
 * Implement this interface, when you want to create an object that receives
 * a list of all available affiliations.
 *
 * @author Sven Fillinger
 * @since 1.0.0
 */
interface ListAffiliationsOutput {

  void reportAvailableAffiliations(List<Affiliation> affiliations)

}
