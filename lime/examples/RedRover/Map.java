
/**
 * This class maintains records of all information that is known by
 * a player about him/herself and all other players/features in the
 * game.  This information will be used to actually draw the MapViewer.
 *
 * @author Jason Ginchereau
 * @version 11/01/99
 */
public abstract class Map {

  /**
   * Gets a Feature that is on the map given its ID.
   */
  public abstract Feature getFeature(UID featureID);

  /**
   * Gets an array of all the Features that are located
   * within a certain area.
   */
  public abstract Feature[] getFeatures(java.awt.Shape area);
  // Note that since a Location is a Point2D, an simple implementation
  // would be to test if area.contains(feature.getAttributeValue("location")).

  public abstract Feature[] getFeatures();

  /**
   * Gets the Feature with the same ID as newFeatureInfo and
   * calls its update() method to update any changed Attributes.
   * If the Feature is not already on the map, it will be added.
   * This method returns true if it results in any change to the Map.
   */
  public abstract boolean updateFeature(Feature newFeatureInfo);
}

