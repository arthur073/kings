import java.awt.geom.*;

public class WaveEdge extends Edge {

  public WaveEdge() {
  }

  private static final int[]    segTypes = new int[] {
    PathIterator.SEG_MOVETO,
    PathIterator.SEG_CUBICTO
  };

  private static final double[] segCoords = new double[] {
    0.0, 0.0,
    0.35, 0.8,  0.65, -0.8,  1.0, 0.0
  };

  public PathIterator getPathIterator(AffineTransform t) {
    return new GeneralSegmentIterator(segTypes, segCoords, t);
  }
}
