
/**
 * Interface for listening for OrientationEvents.
 *
 * @author Jason Ginchereau
 * @version 11/12/99
 */
public interface OrientationListener extends java.util.EventListener {
  void playerTurned(OrientationEvent evt);
}

