package life.qbic.portal.offermanager.components.person.create

import life.qbic.datamodel.dtos.business.Affiliation

/**
 * <h1>Organisation of an affiliation</h1>
 * <br>
 * <p>An address organisation can have multiple affiliations. In order to correctly map between an organisation and
 * all of its affiliations</p>
 *
 * @since 1.0.0
 */
class Organisation {

    /**
     * The name of the organisation
     * @since 1.0.0
     */
    final String name
    /**
     * The affiliation associated to that organisation.
     * It needs to be immutable so that new affiliations can be added
     *
     * @since 1.0.0
     */
    List<Affiliation> affiliations

    Organisation(Affiliation affiliation) {
        this.name = affiliation.organisation
        this.affiliations = new ArrayList<Affiliation>([affiliation])
    }

    /**
     * Creates an organisation with an organisation name and all of its affiliations
     * @param name
     * @param affiliations
     */
    Organisation(String name, List<Affiliation> affiliations) {
        this.name = name
        this.affiliations = new ArrayList<Affiliation>(affiliations)
    }


}
