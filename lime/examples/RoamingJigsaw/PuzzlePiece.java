import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;

/**
 * Holds information about one piece including position, original
 * location in the puzzle, rotation, and edges.  Connected pieces are
 * chained onto the next pointer, so that operations can be recursively
 * performed on the group of connected pieces.
 */
public final class PuzzlePiece implements java.io.Serializable {

  /**
   * Encapsulates the row/col "identification" of a piece, which is
   * immutable and can be passed around and compared with minimal overhead.
   */
  public static final class ID implements java.io.Serializable {
    public final int row;
    public final int col;
    public ID(int row, int col) {
      this.row = row; this.col = col;
    }
    public ID(int[] rc) {
      this.row = rc[0]; this.col = rc[1];
    }
    public boolean equals(Object o) {
      if(o instanceof PuzzlePiece) o = ((PuzzlePiece)o).id;
      return this.row == ((ID)o).row && this.col == ((ID)o).col;
    }
    public int[] toArray() {
      return new int[] { row, col };
    }
    public String toString() {
      return "[" + row + "," + col + "]";
    }
  }

  public final PuzzlePiece.ID  id;  // original row & col in the picture


  public static final int NONE   = -1;
  public static final int TOP    =  0;
  public static final int LEFT   =  1;
  public static final int BOTTOM =  2;
  public static final int RIGHT  =  3;

  private Edge[]      edges;
  private Color       selectionColor;
  private Point       location;  // canvas location of the center of the piece
  private int         rotation;  // degrees
  private PuzzlePiece next;      // next piece in connected group


  // transient members are not saved in serialization
  private transient PuzzlePieceSet pieceSet = null;
  private transient Pixels         pixels   = null;
  private transient BufferedImage  image    = null;
  private transient Shape          shape    = null;

  public PuzzlePiece(PuzzlePieceSet pieceSet, PuzzlePiece.ID id) {
    this.pieceSet = pieceSet;
    this.id = id;

    this.rotation = 0;
    this.edges = new Edge[4];
    this.location = new Point();
    this.next = null;
  }

  public PuzzlePiece next() {
    return next;
  }

  public int getRotation() {
    return this.rotation;
  }
  public void setRotation(int rotation) {
    while(rotation >= 360) rotation -= 360;
    while(rotation < 0) rotation += 360;

    if(this.rotation != rotation) {
      // rotate about the center of the connected piece group
      Point center = getCenter();
      AffineTransform rotator = AffineTransform.getRotateInstance(
          (this.rotation - rotation) * Math.PI / 180, center.x, center.y);
      Point newLocation = (Point) rotator.transform(getLocation(), new Point());
      this.location.x = newLocation.x;
      this.location.y = newLocation.y;
      this.rotation = rotation;

      PuzzlePiece p = next;
      this.next = null;

      // create a cirular linked list of the other pieces
      if(p != null) for(PuzzlePiece pi = p; ; pi = pi.next) {
        pi.rotation = rotation;
        if(pi.next == null) {
          pi.next = p;
          break;
        }
      }

      Point distance = new Point();
      // Now, snap each of the other pieces back onto the
      // newly rotated piece:

      while(p != null) {  // while there are still pieces in this list
        for(PuzzlePiece ti = this; ti != null; ti = ti.next)
          if(ti.trySnap(p.next, distance) != NONE) {
            p.next.location.x += distance.x;
            p.next.location.y += distance.y;
            PuzzlePiece q = p.next;
            if(p.next == p) p = null;
            else p.next = p.next.next; // remove it from the list
            q.next = null;
            this.connect(q);
            break;
          }

        if(p != null) p = p.next;
      }
    }
  }

  public Edge getEdge(int whichEdge) {
    return this.edges[whichEdge];
  }
  public void setEdge(int whichEdge, Edge edge) {
    this.edges[whichEdge] = edge;
    image = null;
  }

  public Color getSelectionColor() {
    return selectionColor;
  }
  public void setSelectionColor(Color c) {
    this.selectionColor = c;
    image = null;
    if(next != null) next.setSelectionColor(c);
  }

  public Point getLocation() {
    return location;
  }
  public void setLocation(Point p) {
    translate(p.x - this.location.x, p.y - this.location.y);
  }
  public void setLocation(int x, int y) {
    translate(x - this.location.x, y - this.location.y);
  }

  /**
   * Get the center of the entire set of connected pieces.
   */
  public Point getCenter() {
    int x = this.location.x;
    int y = this.location.y;
    int n = 1;
    for(PuzzlePiece p = next; p != null; p = p.next) {
      x += p.location.x;
      y += p.location.y;
      n++;
    }
    return new Point(x / n, y / n);
  }
  public void setCenter(Point p) {
    Point c = getCenter();
    translate(p.x - c.x, p.y - c.y);
  }
  public void setCenter(int x, int y) {
    Point c = getCenter();
    translate(x - c.x, y - c.y);
  }

  public void translate(Point d) {
    translate(d.x, d.y);
  }
  public void translate(int dx, int dy) {
    for(PuzzlePiece p = this; p != null; p = p.next) {
      p.location.x += dx;
      p.location.y += dy;
    }
  }

  /**
   * Get the box that contains all pieces in this connected group.
   */
  public Rectangle getBounds() {
    Rectangle bounds = getLocalToCanvasTransform()
        .createTransformedShape(getShape()).getBounds();

    // Add an extra 2 pixels all around just to be safe.
    bounds.x -= 2;
    bounds.y -= 2;
    bounds.width += 4;
    bounds.height += 4;

    if(next != null) bounds.add(next.getBounds());
    return bounds;
  }

  /**
   * Paints this piece on the canvas.
   */
  public void paint(Graphics2D g) {
    if(this.image == null) createImage();

    AffineTransform originalT = g.getTransform();
    AffineTransform t = getLocalToCanvasTransform();
    g.transform(t);
    g.translate(-pieceSet.pw/2.0 - pieceSet.ew, -pieceSet.ph/2.0 - pieceSet.eh);
    g.drawImage(this.image, 0, 0, null);
    g.setTransform(originalT);

    if(next != null) next.paint(g);
  }

  /**
   * Creates the piece image from the pixel data and edge styles.
   */
  synchronized void createImage() {
    this.shape = getEdgePath(true);

    if(pixels == null) pixels = new Pixels(id, pieceSet);

    MemoryImageSource source
        = new MemoryImageSource(pieceSet.pw + 2*pieceSet.ew,
                                pieceSet.ph + 2*pieceSet.eh,
                                pixels.pixels, 0, pieceSet.pw + 2*pieceSet.ew);
    this.image = new BufferedImage(pieceSet.pw + 2*pieceSet.ew,
                                   pieceSet.ph + 2*pieceSet.eh,
                                   BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = (Graphics2D) image.getGraphics();
    g.drawImage(Toolkit.getDefaultToolkit().createImage(source), 0, 0, null);

    // move to local coordinates
    g.translate(pieceSet.ew + pieceSet.pw/2.0, pieceSet.eh + pieceSet.ph/2.0);

    g.setComposite(AlphaComposite.Src);
    g.setPaint(new Color(0, 0, 0, 0)); // transparent
    Area a = new Area(new Rectangle2D.Double(-pieceSet.pw/2.0 - pieceSet.ew,
                                             -pieceSet.ph/2.0 - pieceSet.eh,
                                             pieceSet.pw + 2*pieceSet.ew,
                                             pieceSet.ph + 2*pieceSet.eh));
    a.subtract(new Area(shape));
    g.fill(a); // fill the area not covered by the piece with transparency

    if(selectionColor != null) {
      g.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_SQUARE,
                                  BasicStroke.JOIN_BEVEL, 2.0f,
                                  new float[] { 4.0f, 4.0f }, 0));
      g.setPaint(selectionColor);
      g.draw(getEdgePath(false));
    }
  }

  public synchronized void setPixels(Pixels pixels) {
    this.pixels = pixels;
  }
  public synchronized void setPieceSet(PuzzlePieceSet pieceSet) {
    this.pieceSet = pieceSet;
  }

  public Pixels getPixels() {
    if(pixels == null) {
      synchronized(this) {
        if(pixels == null) pixels = new Pixels(id, pieceSet);
      }
    }
    return pixels;
  }

  public static class Pixels implements java.io.Serializable {
    private PuzzlePiece.ID id;  // necessary to properly implement equals()
    private int[] pixels;

    public Pixels(PuzzlePiece.ID id, PuzzlePieceSet pieceSet) {
      this.id = id;

      int x = (id.col == 0? 0 : pieceSet.pixelSize.width  * id.col
                                / pieceSet.puzzleSize.width - pieceSet.ew);
      int y = (id.row == 0? 0 : pieceSet.pixelSize.height * id.row
                                / pieceSet.puzzleSize.height - pieceSet.eh);
      int w = pieceSet.pw + (id.col == 0? 0 : pieceSet.ew)
            + (id.col == pieceSet.puzzleSize.width-1? 0 : pieceSet.ew);
      int h = pieceSet.ph + (id.row == 0? 0 : pieceSet.eh)
            + (id.row == pieceSet.puzzleSize.height-1? 0 : pieceSet.eh);
      int scansize = pieceSet.pw + 2*pieceSet.ew;
      int offset = (id.col == 0? pieceSet.ew : 0)
                 + (id.row == 0? pieceSet.eh : 0) * scansize;

      this.pixels = new int[(pieceSet.pw + 2*pieceSet.ew) *
                            (pieceSet.ph + 2*pieceSet.eh)];

      for(int i = 0; i < w; i++) for(int j = 0; j < h; j++)
        pixels[j*scansize+offset+i]
            = pieceSet.pixels[(y + j) * pieceSet.pixelSize.width + (x + i)];
    }

    public int[] getPixels() {
      return pixels;
    }

    public boolean equals(Object o) {
      return id.equals(((Pixels)o).id);
    }
  }

  public boolean coversPoint(int x, int y) {
    return coversPoint(new Point(x, y));
  }
  public boolean coversPoint(Point2D p) {
    Point2D q = getCanvasToLocalTransform().transform(p, new Point2D.Double());
    if(getShape().getBounds().contains(q) && getShape().contains(q))
      return true;
    else
      return next != null? next.coversPoint(p) : false;
  }

  private AffineTransform getCanvasToLocalTransform() {
    AffineTransform t = new AffineTransform();
    if(rotation != 0) t.rotate(rotation * Math.PI / 180.0);
    t.translate((double) -location.x, (double) -location.y);
    return t;
  }
  // returns the inverse of the above, but slightly more efficiently
  private AffineTransform getLocalToCanvasTransform() {
    AffineTransform t = new AffineTransform();
    t.translate((double) location.x, (double) location.y);
    if(rotation != 0) t.rotate(-rotation * Math.PI / 180.0);
    return t;
  }

  private AffineTransform getEdgeToLocalTransform(int whichEdge) {
    double sinTheta, cosTheta;
    switch(whichEdge) { // faster and more efficient than using PI
      case LEFT  : sinTheta = -1;  cosTheta =  0;  break;
      case BOTTOM: sinTheta =  0;  cosTheta = -1;  break;
      case RIGHT : sinTheta =  1;  cosTheta =  0;  break;
      default    : sinTheta =  0;  cosTheta =  1;  break;
    }
    AffineTransform t = new AffineTransform(cosTheta, sinTheta,
                                            -sinTheta, cosTheta, 0.0, 0.0);
    if(whichEdge == LEFT || whichEdge == RIGHT) {
      t.translate(-pieceSet.ph/2.0, -pieceSet.pw/2.0);
      t.scale(pieceSet.ph, -pieceSet.ew);
    }
    else {
      t.translate(-pieceSet.pw/2.0, -pieceSet.ph/2.0);
      t.scale(pieceSet.pw, -pieceSet.eh);
    }
    return t;
  }

  /**
   * Gets the current shape of this individual puzzle piece.
   */
  public Shape getShape() {
    if(shape == null) shape = getEdgePath(true);
    return shape;
  }

  /**
   * Returns the path around the edges of this puzzle piece, in the
   * local coordinate system.  If close is false, only non-null edges
   * will be included.
   */
  private GeneralPath getEdgePath(boolean close) {
    GeneralPath gp = new GeneralPath(GeneralPath.WIND_NON_ZERO, 4);
    for(int i = 3; i >= 0; i--) {  // must go clockwise
      AffineTransform t = getEdgeToLocalTransform(i);
      if(edges[i] != null) gp.append(edges[i].getPathIterator(t), close);
      else if(close) gp.append(new Edge().getPathIterator(t), close);
    }
    if(close) gp.closePath();
    return gp;
  }

  /**
   * Returns the edge onto which p can be snapped, or NONE.  The distance
   * p needs to be moved first is saved in the distance parameter.
   */
  public int trySnap(PuzzlePiece p, Point distance) {
    int whichEdge = NONE;
    if(p.rotation == this.rotation) {
      Point snapPoint = new Point();
      if(id.row == p.id.row) {
        if(id.col - 1 == p.id.col) {
          snapPoint.x = -pieceSet.pw;
          whichEdge = LEFT;
        }
        else if(id.col + 1 == p.id.col) {
          snapPoint.x = pieceSet.pw;
          whichEdge = RIGHT;
        }
      }
      else if(id.col == p.id.col) {
        if(id.row - 1 == p.id.row) {
          snapPoint.y = -pieceSet.ph;
          whichEdge = TOP;
        }
        else if(id.row + 1 == p.id.row) {
          snapPoint.y = pieceSet.ph;
          whichEdge = BOTTOM;
        }
      }
      if(whichEdge != NONE) {
        getLocalToCanvasTransform().transform(snapPoint, distance);
        distance.x -= p.location.x;
        distance.y -= p.location.y;
      }
    }
    return whichEdge;
  }

  public boolean connect(PuzzlePiece p) {
    boolean success = true;

    if(p == this) success = false;  // already connected

    // check for recursive connection
    for(PuzzlePiece q = p; q != null && success; q = q.next) 
      if(q.id.equals(id)) success = false;

    if (success) {
      if(next != null) next.connect(p);
      else next = p;
    }
    return success;
  }

  public boolean equals(Object o) {
    return id.equals(o);
  }

  public String toString() {
    return id.toString();
  }
}
