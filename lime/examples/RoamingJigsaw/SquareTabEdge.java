import java.awt.geom.*;

public class SquareTabEdge extends Edge {
  private boolean tab;

  public SquareTabEdge(boolean tab) {
    this.tab = tab;
  }

  private static final double[] segCoords = new double[]
  { 0.0, 0.0,  0.35, 0.0,  0.35, 1.0,  0.65, 1.0,  0.65, 0.0,  1.0, 0.0 };

  public PathIterator getPathIterator(AffineTransform t) {
    if(!tab) t.scale(1.0, -1.0);
    return new LineSegmentIterator(segCoords, t);
  }
}
