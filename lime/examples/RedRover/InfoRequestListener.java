
/**
 * Interface for listening for InfoRequestEvents.
 *
 * @author Brian Mesh
 * @version 2/25/2000
 */
public interface InfoRequestListener extends java.util.EventListener {
  void infoRequested (InfoRequestEvent evt);
}

