package life.qbic.portal.offermanager.events

/**
 * A class that can can emit events of a given type T
 * to registered subscriptions.
 *
 * @since 1.0.0
 */
class EventEmitter<T> {

    private final List<Subscription<T>> subscriptions

    EventEmitter() {
        subscriptions = new LinkedList<>()
    }

    /**
     * Registers a new subscription, that gets notified when
     * an event is emitted.
     * @param s The subscription to register
     */
    void register(Subscription s) {
        this.subscriptions.add(s)
    }

    /**
     * Cancels an exising subscription from receiving future events.
     * @param s The subscription to cancel
     */
    void unregister(Subscription s) {
        this.subscriptions.remove(s)
    }

    /**
     * Emits an event of type T to all registered subscriptions.
     * @param t The event of type T that is emitted by the emitter to the registered
     * subscriptions.
     */
    void emit(T t) {
        for (Subscription s : subscriptions) {
            s.receive(t)
        }
    }
}
