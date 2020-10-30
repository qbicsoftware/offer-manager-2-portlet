package life.qbic.portal.qoffer2.web.views

/**
 * Listens for changes in the affiliation selection
 *
 *
 * @since: 1.0.0
 */
interface AffiliationSelectionListener extends EventListener{

    /**
     * An affiliation was selected in an AffiliationSelectionEvent
     * @param event the event holding the new selection and the event source
     */
    void affiliationSelected(AffiliationSelectionEvent event)
}
