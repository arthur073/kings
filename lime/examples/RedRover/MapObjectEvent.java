
/**
 * An event that indicates the user has either requested or found
 * a map object
 *
 * @author Brian Mesh
 * @version 2/25/2000
 */
public class MapObjectEvent extends java.util.EventObject {

  public static final int OBJECT_FOUND = 1;
  public static final int OBJECT_LOOKFORONE = 2;
  public static final int OBJECT_LOOKFORALL = 3;

  /**
   * Creates a new MapObjectEvent.
   *
   * @param source the object that generates this event
   * @param actionType the type of MapObject change, one of the constants
   *           defined in this class
   * @param objMesg the object to act upon
   */
  public MapObjectEvent(Object source, int actionType, String objMesg) {
    super(source);
    this.actionType = actionType;
    this.objMesg = objMesg;
  }

  /**
   * Gets the type of MapObject change.
   */
  public int getActionType() {
    return actionType;
  }

  public String getMessage() {
    return objMesg;
  }

  private int actionType;
  private String objMesg;
}

