
/**
 * An orientation between 0 and 360 degrees.
 * This is represented as an object to allow it to be conveniently
 * stored in other data structrues.
 *
 * @author Jason Ginchereau
 * @version 10/21/99
 */

public class Orientation implements java.io.Serializable, Cloneable {

  public static final Orientation NORTH = new Orientation(0.0);
  public static final Orientation EAST  = new Orientation(Math.PI / 2);
  public static final Orientation SOUTH = new Orientation(Math.PI);
  public static final Orientation WEST  = new Orientation(Math.PI * 3 / 2);

  public Orientation(double radians) {
    setRadians(radians);
  }

  public Orientation() {
    this(0.0);
  }

  public Orientation(Orientation copy) {
    this(copy.radians);
  }

  public double getRadians() {
    return radians;
  }

  public void setRadians(double radians) {
    while(radians < 0) radians += 2*Math.PI;
    while(radians >= 2*Math.PI) radians -= 2*Math.PI;
    this.radians = radians;
  }

  public double getDegrees() {
    return getRadians() * 180.0 / Math.PI;
  }

  public void setDegrees(double degrees) {
    setRadians(degrees * Math.PI / 180.0);
  }

  public String toString() {
    return String.valueOf(Math.round(getDegrees()));
  }

  private double radians;
}
