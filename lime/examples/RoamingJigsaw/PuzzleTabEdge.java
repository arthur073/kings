import java.awt.geom.*;

public class PuzzleTabEdge extends Edge {
  private boolean tab;

  public PuzzleTabEdge(boolean tab) {
    this.tab = tab;
  }

  private static final int[]    segTypes = new int[] {
    PathIterator.SEG_MOVETO,
    PathIterator.SEG_LINETO,
    PathIterator.SEG_LINETO,
    PathIterator.SEG_LINETO,
    PathIterator.SEG_LINETO,
    PathIterator.SEG_LINETO,
    PathIterator.SEG_LINETO,
    PathIterator.SEG_LINETO,
    PathIterator.SEG_LINETO,
    PathIterator.SEG_LINETO,
    PathIterator.SEG_LINETO,
    PathIterator.SEG_LINETO
  };

  private static final double[] segCoords = new double[] {
    0.00, 0.00,
    0.40, 0.00,
    0.40, 0.20,
    0.37, 0.52,
    0.37, 0.68,
    0.46, 1.00,
    0.54, 1.00,
    0.63, 0.68,
    0.63, 0.52,
    0.60, 0.20,
    0.60, 0.00,
    1.00, 0.00
  };

  public PathIterator getPathIterator(AffineTransform t) {
    if(!tab) t.scale(1.0, -1.0);
    return new GeneralSegmentIterator(segTypes, segCoords, t);
  }
}
