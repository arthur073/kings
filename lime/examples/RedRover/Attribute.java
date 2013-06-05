
/**
 * A Feature Attribute.
 *
 * @author Jason Ginchereau
 * @version 11/01/99
 */
public class Attribute implements java.io.Serializable {

  // Category definitions:
  public static final int PRIVATE = 0;
  public static final int TEAM    = 10;
  public static final int PUBLIC  = 20;

  public Attribute(String name, Object value, int category) {
    this(name, value, category, System.currentTimeMillis());
  }

  private Attribute(String name, Object value, int category,
                    long lastModifiedTime) {
    this.name = name;
    this.value = value;
    this.category = category;
    this.lastModifiedTime = lastModifiedTime;
  }

  /**
   * Gets the name of this Attribute.
   */
  public String getName() {
    return name;
  }

  /**
   * Gets the value of this Attribute.
   */
  public Object getValue() {
    return value;
  }

  /**
   * Sets this Attribute's value.  Null is a valid value.
   * Returns the previously set value, if any.
   */
  public Object setValue(Object value) {
    Object prev = this.value;
    this.value = value;
    this.lastModifiedTime = System.currentTimeMillis();
    return prev;
  }

 /**
   * Gets the category of this Attribute, as one of the
   * constants defined by this class.
   */
  public int getCategory() {
    return category;
  }

  /**
   * Gets the time this Attribute was last modified.
   */
  public long getLastModifiedTime() {
    return lastModifiedTime;
  }

  /**
   * Sets this attribute to have the same value and
   * last-modified time as attr, only if the attr is newer.
   * Returns true if attr was newer and the update actually took place.
   */
  public boolean update(Attribute attr) {

    // update if time of new value is more recent, except always update the
    // GHOST field
    if(lastModifiedTime < attr.lastModifiedTime 
       || attr.name.equals(Feature.GHOST)) {
      this.value = attr.value;
      this.lastModifiedTime = attr.lastModifiedTime;
      return true;
    }
    else {
      return false;
    }
  }

  /**
  /**
   * Returns an exact copy of this Attribute.
   */
  public Attribute copy() {
    return new Attribute(name, value, category, lastModifiedTime);
  }

  /**
   * Returns the name and value of the Attribute as a String.
   */
  public String toString() {
    return "" + name + "=" + value +
           "  (cat=" + category + ", t=" + lastModifiedTime + ")";
  }

  private String name;
  private Object value;
  private int    category;
  private long   lastModifiedTime;
}

