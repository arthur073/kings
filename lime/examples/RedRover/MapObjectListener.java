
/**
 * Interface for listening for MapObjectEvents.
 *
 * @author Brian Mesh
 * @version 2/25/2000
 */
public interface MapObjectListener extends java.util.EventListener {
  void mapObjectNotification(MapObjectEvent evt);
}

