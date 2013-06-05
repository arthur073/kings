
/**
 * Interface for listening for LocationEvents.
 *
 * @author Jason Ginchereau
 * @version 11/12/99
 */
public interface LocationListener extends java.util.EventListener {
  void playerMoved(LocationEvent evt);
}

