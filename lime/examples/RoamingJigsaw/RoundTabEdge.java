import java.awt.geom.*;

public class RoundTabEdge extends Edge {
  private boolean tab;

  public RoundTabEdge(boolean tab) {
    this.tab = tab;
  }

  private static final int[]    segTypes = new int[] {
    PathIterator.SEG_MOVETO,
    PathIterator.SEG_LINETO,
    PathIterator.SEG_QUADTO,
    PathIterator.SEG_LINETO
  };

  private static final double[] segCoords = new double[] {
    0.00, 0.00,
    0.35, 0.00,
    0.50, 2.00,  0.65, 0.00,
    1.00, 0.00
  };

  public PathIterator getPathIterator(AffineTransform t) {
    if(!tab) t.scale(1.0, -1.0);
    return new GeneralSegmentIterator(segTypes, segCoords, t);
  }
}
