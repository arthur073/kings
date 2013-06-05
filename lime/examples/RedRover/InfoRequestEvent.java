

/**
 * An event that request data from Game on a Feature
 *
 * @author Brian Mesh
 * @version 2/25/2000
 */
public class InfoRequestEvent extends java.util.EventObject {

    public static final int PICTURE = 1;

  /**
   * Creates a new InfoRequestEvent.
   *
   * @param source the object that generates this event
   * @param actionType what kind of information is needed
   * @param theFeature the feature whose information is needed
   */
  public InfoRequestEvent(Object source, int actionType, Feature theFeature) {
    super(source);
    this.actionType = actionType;
    this.theFeature = theFeature;
  }

  /**
   * Gets the request and feature to look for.
   */
  public int getActionType() {
    return actionType;
  }

  public Feature getFeature(){
    return theFeature;
  }

  private int actionType;
    private Feature theFeature;
}

