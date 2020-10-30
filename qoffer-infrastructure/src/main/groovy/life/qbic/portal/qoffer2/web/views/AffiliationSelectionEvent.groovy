package life.qbic.portal.qoffer2.web.views

import life.qbic.datamodel.dtos.business.Affiliation

/**
 * An event to be fired when an Affiliation is selected
 *
 * @since: 1.0.0
 */
class AffiliationSelectionEvent extends EventObject{

    final Affiliation value
    /**
     * Constructs a selection Event.
     *
     * @param source The object on which the Event initially occurred.
     * @param value The selected Affiliation
     * @exception IllegalArgumentException  if source is null.
     */
    AffiliationSelectionEvent(Object source, Affiliation affiliation) {
        super(source)
        this.value = affiliation
    }


}
