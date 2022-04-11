package life.qbic.portal.offermanager.components;

import java.util.EventListener;

/**
 * <p>Notifies listening collaborators that an action was aborted.</p>
 */
public interface AbortNotifier {
  @FunctionalInterface
  interface AbortListener extends EventListener {
    void onAbort();
  }
  void addAbortListener(AbortListener abortListener);
}
