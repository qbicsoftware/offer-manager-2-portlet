package life.qbic.portal.offermanager.components;

/**
 * Classes implementing this interface can be updated with a given object type
 * @param <T> the type of object that can be consumed for an update
 */
public interface Updatable<T> {
  void update(T value);
}
