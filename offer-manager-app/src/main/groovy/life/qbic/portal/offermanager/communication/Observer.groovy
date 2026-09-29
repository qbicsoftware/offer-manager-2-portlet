package life.qbic.portal.offermanager.communication

/**
 * An observer that gets notified when an {@link Observable} it is registered to changes.
 *
 * <p>Replaces the deprecated {@link java.util.Observer} contract. The
 * {@link java.util.Observable}/{@link java.util.Observer} pair has been deprecated since Java 9
 * and Groovy struggles to dispatch its protected {@code setChanged()} method, which leads to
 * runtime failures. This interface together with {@link Observable} provides the same
 * notification behaviour without depending on the deprecated JDK classes.</p>
 *
 * @since 1.0.0
 */
interface Observer {

    /**
     * Called by an {@link Observable} when it notifies its observers about a change.
     *
     * @param observable the {@link Observable} that notified this observer
     */
    void update(Observable observable)
}