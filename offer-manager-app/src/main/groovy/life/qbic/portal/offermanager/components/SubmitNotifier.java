package life.qbic.portal.offermanager.components;

import java.util.EventListener;

/**
 * <p>Notifies listening collaborators that an action was submitted.</p>
 */
public interface SubmitNotifier {
  @FunctionalInterface
  interface SubmitListener extends EventListener {
    void onSubmit();
  }
  void addSubmitListener(SubmitListener submitListener);
}
