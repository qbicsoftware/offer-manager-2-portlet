package life.qbic.portal.offermanager.communication

/**
 * The subscription interface serves as a functional interface
 * to receive communication of type T emitted by an EventEmitter of type T.
 *
 * @since 1.0.0
 */
interface Subscription<T> {
    /**
     * Event emitter will call this method and pass an event of type T
     * to registered subscriptions.
     * @param t An event of type T
     */
    void receive(T t)
}