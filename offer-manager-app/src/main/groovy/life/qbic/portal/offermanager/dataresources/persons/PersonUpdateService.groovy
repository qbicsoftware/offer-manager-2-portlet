package life.qbic.portal.offermanager.dataresources.persons

import life.qbic.datamodel.dtos.general.Person
import life.qbic.portal.offermanager.communication.EventEmitter
import life.qbic.portal.offermanager.communication.Subscription
import life.qbic.portal.offermanager.dataresources.ResourcesService

/**
 * Service that represents an person to be updated.
 *
 * This service offers an EventEmitter property that can be
 * used for inter component communication, when a new person
 * source is available for update.
 *
 * @since: 1.0.0
 *
 */
class PersonUpdateService implements ResourcesService<Person> {

    private final EventEmitter<Person> customerForUpdateEvent

    PersonUpdateService(){
        this.customerForUpdateEvent = new EventEmitter<>()
    }

    @Override
    void reloadResources() {
        //nothing to reload
    }

    @Override
    void subscribe(Subscription<Person> subscription) {
        customerForUpdateEvent.register(subscription)
    }

    @Override
    void unsubscribe(Subscription<Person> subscription) {
        customerForUpdateEvent.unregister(subscription)
    }

    @Override
    void addToResource(Person resourceItem) {
        /*
        Since this service only is a proxy
        of an customer update event, this service holds
        no track of the resources.
        */
        customerForUpdateEvent.emit(resourceItem)
    }

    @Override
    void removeFromResource(Person resourceItem) {
        /*
        Since this service only is a proxy
        of an customer update event, this service holds
        no track of the resources.
        */
        customerForUpdateEvent.emit(resourceItem)
    }

    @Override
    Iterator<Person> iterator() {
        return [].iterator()
    }
}
