/**
 * Interface for listening for InteractorEvents.
 *
 * @author Jason Ginchereau
 * @version 11/12/99
 */
public interface InteractorListener extends java.util.EventListener {
  void featureUpdated(InteractorEvent evt);
}

