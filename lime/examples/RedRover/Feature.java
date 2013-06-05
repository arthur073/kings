
import java.util.HashMap;
import java.util.Iterator;
import lights.adapters.*;
import lights.interfaces.*;
//import com.ibm.tspaces.*;

/**
 * A Feature is anything that can be on a map.  It has a 
 * unique identifier and a set of Attributes that can be
 * set or retrieved via their name.  All Attributes have
 * a timestamp which is the time value when they were last
 * updated.
 * <p>
 * Implementations of this class might include Players
 * and Landmarks.
 *
 * @author Jason Ginchereau
 * @version 11/16/99
 */
public /*abstract*/ class Feature implements java.io.Serializable {

  /**
   * Some standard Attribute names that might be used in any kind of Feature.
   */
  public static final String NAME        = "name";
  public static final String TEAM        = "team";
  public static final String LOCATION    = "location";
  public static final String ORIENTATION = "orientation";
  public static final String GHOST       = "ghost";
  public static final String FROZEN      = "frozen";
  public static final String IT          = "it";
  public static final String PICTURE     = "picture";
  // more..?

  /**
   * Creates a new Feature with an empty Attribute set.
   */
  public Feature(UID id) {
    this(id, new HashMap());
  }

  public Feature(UID id, java.util.Map attributes) {
    this.id = id;
    this.attributes = attributes;
  }

  /**
   * All features provide a unique identifier which can
   * never be altered.
   */
  public UID getID() {
    return id;
  }

  /**
   * Returns an Iterator over all the Attributes of the feature.
   * Note that the iteration is NOT thread-safe and will fail
   * immediately if any changes are made during iteration.
   * However, synchronizing on the Feature instance will allow
   * safe iteration.
   */
  public Iterator attributes() {
    return attributes.values().iterator();
  }

  /**
   * Gets a named Attribute.
   * Returns null if the Attribute is not set.
   */
  public synchronized Attribute getAttribute(String name) {
    return (Attribute) attributes.get(name);
  }

  /**
   * Sets an Attribute.
   * Returns the previously set Attribute with the same name, if any.
   */
  public synchronized Attribute setAttribute(Attribute attr) {
    return (Attribute) attributes.put(attr.getName(), attr);
  }

  /**
   * Gets the value of a named Attribute.  The value could be null.
   * @throw NoSuchElementException if the Attribute does not exist.
   */
  public Object getAttributeValue(String name) {
    Object retObj = null;
    Attribute attr = getAttribute(name);
    if(attr == null) 
      retObj = null;
    else 
      retObj = attr.getValue();
    
    return retObj;
  }

  /**
   * Sets a named Attribute's value, causing its last update time to
   * be the current system time.
   * Returns the previously set value, if any.
   * @throw NoSuchElementException if the Attribute does not exist.
   */
  public Object setAttributeValue(String name, Object value) {
    Attribute attr = getAttribute(name);
    if(attr == null) throw new java.util.NoSuchElementException(name);
    return attr.setValue(value);
  }

  /**
   * Updates this Feature by conditionally copying Attributes from
   * newFeatureInfo to this feature.  Attributes will only be copied
   * if either this feature does not have the Attribute set, or if
   * the Attribute in newFeatureInfo is newer than the currently set
   * Attribute.  Returns true if any Attributes were copied.
   */
  public synchronized boolean update(Feature newFeatureInfo) {
    boolean changed = false;
    for(Iterator i = newFeatureInfo.attributes(); i.hasNext(); ) {
      Attribute newAttr = (Attribute)i.next();
      Attribute thisAttr = (Attribute) attributes.get(newAttr.getName());



      if(thisAttr == null) {
        setAttribute(newAttr.copy());
        changed = true;
      }
      else {
        if(thisAttr.update(newAttr)) {
          changed = true;
        }
      }
    }
    return changed;
  }

  /**
   * Returns a complete copy of this Feature.
   */
  public synchronized Feature copy() {
    return new Feature(id, new HashMap(attributes));
  }

  /**
   * Returns a copy of this Feature that contains only
   * Attributes with the specified category.
   */
  public synchronized Feature copy(int category) {
    HashMap copyMap = new HashMap();
    for(Iterator i = attributes(); i.hasNext(); ) {
      Attribute attr = (Attribute) i.next();
      if(attr.getCategory() == category) {
        attr = attr.copy();
        copyMap.put(attr.getName(), attr);
      }
    }
    return new Feature(id, copyMap);
  }

  /**
   * Returns a tuple which can be written to the Lime
   * tuple which contains the following format:<br>
   * <pre>{feature_id, info_string, feature}</pre>
   *
   * @param str The info_string
   */
  public synchronized ITuple toTuple (String str){
    ITuple t = null;
    t = new Tuple();
    t.add(new Field().setToActual(this.id));
    t.add(new Field().setToActual(str));
    t.add(new Field().setToActual(this));

    return t;
  }

  public String toString() {
    String s = "Feature ID=" + id + "\n";
    for(Iterator i = attributes(); i.hasNext(); ) {
      s += "    " + ((Attribute) i.next()) + "\n";
    }
    return s;
  }

  private UID           id;
  private java.util.Map attributes;
}
