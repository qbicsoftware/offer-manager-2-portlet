package life.qbic.portal.offermanager.components;

public interface UserInput<T> {
  public boolean isValid();
  public T get();
}
