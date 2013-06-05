import java.awt.geom.*;

public class Edge implements java.io.Serializable, Cloneable {

  /**
   * Returns a PathIterator for this edge, automatically applying an
   * AffineTransform.  The iterator may omit the last component of
   * the path because it will be connected to the start of the next
   * edge.  All path coordinates should be in the range [0, 1]
   * (before transformation).  The first point must be (0, 0) and
   * the last must be (1, 0).  The function y = f(x) is the curve
   * which describes a top edge of a puzzle piece.  The default
   * implementation just returns a straight path from (0, 0) to (1, 0)
   * -- a flat edge.
   */
  public PathIterator getPathIterator(AffineTransform t) {
    return new LineSegmentIterator(new double[] { 0.0, 0.0,  1.0, 0.0 }, t);
  }


  /**
   * A utility class so subclasses of Edge can just supply
   * a set of coordinates.
   */
  protected static class LineSegmentIterator implements PathIterator {
    private int             index;
    private double[]        segments;
    private AffineTransform transform;

    public LineSegmentIterator(double[] segments, AffineTransform t) {
      this.segments  = segments;
      this.transform = t;
      index = 0;
    }

    public int getWindingRule() {
      return WIND_NON_ZERO;
    }

    public boolean isDone() {
      return index >= segments.length / 2;
    }

    public void next() {
      index++;
    }

    public int currentSegment(double[] coords) {
      transform.transform(segments, 2 * index, coords, 0, 1);
      return index == 0? SEG_MOVETO : SEG_LINETO;
    }

    public int currentSegment(float[] coords) {
      transform.transform(segments, 2 * index, coords, 0, 1);
      return index == 0? SEG_MOVETO : SEG_LINETO;
    }
  }

  /**
   * A utility class so subclasses of Edge can just supply
   * a set of coordinates.
   */
  protected static class GeneralSegmentIterator implements PathIterator {
    private int             tIndex;
    private int             sIndex;
    private int[]           types;
    private double[]        segments;
    private AffineTransform transform;

    public GeneralSegmentIterator(int[] types, double[] segments,
                                  AffineTransform t) {
      this.types     = types;
      this.segments  = segments;
      this.transform = t;
      tIndex = 0;
      sIndex = 0;
    }

    public int getWindingRule() {
      return WIND_NON_ZERO;
    }

    public boolean isDone() {
      return tIndex >= types.length;
    }

    public void next() {
      sIndex += 2 * getCoordCount(types[tIndex]);
      tIndex++;
    }

    public int currentSegment(double[] coords) {
      transform.transform(segments, sIndex, coords, 0,
                          getCoordCount(types[tIndex]));
      return types[tIndex];
    }

    public int currentSegment(float[] coords) {
      transform.transform(segments, sIndex, coords, 0,
                          getCoordCount(types[tIndex]));
      return types[tIndex];
    }

    private static int getCoordCount(int type) {
      switch(type) {
        case SEG_MOVETO:
        case SEG_LINETO:  return 1;
        case SEG_QUADTO:  return 2;
        case SEG_CUBICTO: return 3;
        default: throw new IllegalArgumentException("Illegal segment type.");
      }
    }
  }
}
