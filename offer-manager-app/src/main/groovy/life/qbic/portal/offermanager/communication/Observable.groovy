package life.qbic.portal.offermanager.communication

import java.util.concurrent.CopyOnWriteArrayList

/**
 * A minimal observable that notifies registered {@link Observer observers} when it changes.
 *
 * <p>Replaces the deprecated {@link java.util.Observable} class. The
 * {@link java.util.Observable}/{@link java.util.Observer} pair has been deprecated since Java 9
 * and Groovy struggles to dispatch the protected {@code setChanged()} method of the JDK class,
 * which leads to {@link groovy.lang.MissingMethodException} at runtime. This implementation
 * exposes {@link #setChanged()} as a public method, so it can be dispatched by any Groovy
 * version.</p>
 *
 * @since 1.0.0
 */
class Observable {

    private final List<Observer> observers = new CopyOnWriteArrayList<>()

    private boolean changed = false

    /**
     * Registers an {@link Observer} that will be notified of future changes.
     *
     * @param observer the observer to register
     */
    void addObserver(Observer observer) {
        observers.add(observer)
    }

    /**
     * Deregisters an {@link Observer} so it no longer receives notifications.
     *
     * @param observer the observer to deregister
     */
    void deleteObserver(Observer observer) {
        observers.remove(observer)
    }

    /**
     * Marks this observable as changed. Observers are only notified by
     * {@link #notifyObservers()} if the observable has been marked as changed.
     */
    void setChanged() {
        changed = true
    }

    /**
     * Notifies all registered observers if this observable has been marked as changed.
     *
     * <p>After notifying, the changed flag is reset, so a subsequent call without an
     * intervening {@link #setChanged()} does not notify again.</p>
     */
    void notifyObservers() {
        if (changed) {
            changed = false
            observers.each { it.update(this) }
        }
    }
}