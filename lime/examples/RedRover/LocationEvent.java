
/**
 * An event that indicates a change in location.
 *
 * @author Jason Ginchereau
 * @version 11/12/99
 */
public class LocationEvent extends java.util.EventObject {

  /**
   * Creates a new LocationEvent with the new location.
   */
  public LocationEvent(Object source, Location newLocation) {
    this(source, null, newLocation);
  }

  /**
   * Creates a new LocationEvent with the old and new location.
   */
  public LocationEvent(Object source,
                       Location oldLocation, Location newLocation) {
    super(source);
    this.oldLocation = oldLocation;
    this.newLocation = newLocation;
  }

  /**
   * Gets the new location.
   */
  public Location getNewLocation() {
    return newLocation;
  }

  /**
   * Gets the old location, or null if unspecified.
   */
  public Location getOldLocation() {
    return oldLocation;
  }

  private Location oldLocation;
  private Location newLocation;
}

