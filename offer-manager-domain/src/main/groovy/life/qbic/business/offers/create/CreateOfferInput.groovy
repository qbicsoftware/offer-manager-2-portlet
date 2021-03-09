package life.qbic.business.offers.create

import life.qbic.datamodel.dtos.projectmanagement.ProjectApplication


/**
 * Input interface for the {@link CreateOffer} use case
 *
 * @since: 1.0.0
 * @author: Tobias Koch
 */
interface CreateOfferInput {

  /**
   * Saves an offer in a (persistent) datasource.
   *
   * Developers shall call this method to pass offer content
   * provided from the user in order to trigger the completion
   * of the business use case `Create Offer`,
   * which will apply business policies for offer creation and storage
   * in a pre-configured, optimally persistent data-source.
   *
   * There is no need to set the offer identifier in the passed content,
   * this will be determined and set by the implementation of the use case.
   *
   * If the identifier is passed with the content, it will be ignored.
   *
   * @param projectApplication {@link life.qbic.datamodel.dtos.projectmanagement.ProjectApplication}
   * @since 1.0.0
   */
  void createOffer(ProjectApplication projectApplication)

}