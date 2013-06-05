
/**
 * An event that indicates a change in orientation.
 *
 * @author Jason Ginchereau
 * @version 11/12/99
 */
public class OrientationEvent extends java.util.EventObject {

  /**
   * Creates a new OrientationEvent with the new orientation.
   */
  public OrientationEvent(Object source, Orientation newOrientation) {
    this(source, null, newOrientation);
  }

  /**
   * Creates a new OrientationEvent with the old and new orientation.
   */
  public OrientationEvent(Object source, Orientation oldOrientation,
                          Orientation newOrientation) {
    super(source);
    this.oldOrientation = oldOrientation;
    this.newOrientation = newOrientation;
  }

  /**
   * Gets the new orientation.
   */
  public Orientation getNewOrientation() {
    return newOrientation;
  }

  /**
   * Gets the old orientation, or null if unspecified.
   */
  public Orientation getOldOrientation() {
    return oldOrientation;
  }

  private Orientation oldOrientation;
  private Orientation newOrientation;
}

