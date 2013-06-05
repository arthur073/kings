
/**
 * A simple hashtable implementation of a Map.
 *
 * @author Jason Ginchereau
 * @version 11/12/99
 */
public class SimpleMap extends Map {

  public SimpleMap() {
    featureMap = new java.util.HashMap();
  }

  /**
   * Returns a feature given a unique identifier,
   * or null if there is no feature on the map with the
   * given featureID.
   */
  public Feature getFeature(UID featureID) {
    synchronized(featureMap) {
      return (Feature) featureMap.get(featureID);
    }
  }

  /**
   * Gets an array of all the Features that are located
   * within a certain area. (Currently will ignore the
   * area param and return the same as getFeatures())
   */
  public Feature[] getFeatures(java.awt.Shape area) {
      /*
    synchronized(featureMap) {
      return (Feature[]) featureMap.values().toArray(new Feature[0]);
    }
      */
      return getFeatures();
  }

  /**
   * Gets an array of all the Features in the map
   */
  public Feature[] getFeatures() {
    synchronized(featureMap) {
      return (Feature[]) featureMap.values().toArray(new Feature[0]);
    }
  }

  /**
   * Updates information about a map feature.  If the feature is not on the
   * map, it will be added.  If it already exists, information about
   * it will be updated according to the timestamps.
   * If information is actually updated (or the feature is added),
   * this method fires a MapEvent and returns true.
   */
  public boolean updateFeature(Feature feature) {
    boolean changed;
    synchronized(featureMap) {
      UID id = feature.getID();
      Feature oldFeature = (Feature) featureMap.get(id);
      if(oldFeature == null) { // feature not already on my map
        // put the feature into the map
        featureMap.put(id, feature);
        changed = true;
      }  // feature on my map already, so just update it
      else {
        changed = oldFeature.update(feature);
      }
    }
    return changed;
  }

  private java.util.Map featureMap;
}

