
/**
 * A location in 2-dimensional space.
 *
 * @author Jason Ginchereau
 * @version 10/21/99
 */
public class Location extends java.awt.geom.Point2D
                      implements java.io.Serializable {

  public static final Location THE_VOID =
      new Location(java.lang.Double.NaN, java.lang.Double.NaN);

  public Location(double x, double y) {
    this.x = x;
    this.y = y;
  }

  public Location() {
    this(0.0, 0.0);
  }

  public Location(Location copy) {
    this(copy.x, copy.y);
  }

  public double getX() {
    return x;
  }

  public double getY() {
    return y;
  }

  public void setLocation(double x, double y) {
    this.x = x;
    this.y = y;
  }

  public String toString() {
    return "(" + (Math.round(x*100)/100) + ", " + (Math.round(y*100)/100) + ")";
  }

  private double x;
  private double y;
}
