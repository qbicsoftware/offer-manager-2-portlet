package life.qbic.portal.portlet.customers.affiliation

import life.qbic.datamodel.dtos.business.Affiliation

/**
 * <class short description - 1 Line!>
 *
 * <More detailed description - When to use, what it solves, etc.>
 *
 * @author Sven Fillinger
 * @since 1.0.0
 */
interface ListAffiliationsOutput {

  void reportAvailableAffiliations(List<Affiliation> affiliations)

}