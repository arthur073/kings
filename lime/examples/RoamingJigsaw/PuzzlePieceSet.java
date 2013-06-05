
import ProgressDialog;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.EventListenerList;

/**
 * Holds a list of PuzzlePieces and handles all the basic piece
 * operations: moves, rotations, selections, and joins.  Notifies
 * listeners when any of these operations have taken place.
 */
public class PuzzlePieceSet {

  private   String            name;
  protected Dimension         puzzleSize; // WxH of puzzle
  protected Dimension         pixelSize;  // dimensions of pixel data
  protected int[]             pixels;     // pixel data
  protected int               pw, ph;     // size of piece in pixels
  protected int               ew, eh;     // size of edges in pixels
  protected java.util.List    pieces;
  protected Color             selectionColor;

  private   transient EventListenerList listeners = null;

  public PuzzlePieceSet(String name, Color color) {
    this.name = name;
    this.selectionColor = color;
    pieces = new LinkedList();
    listeners = new EventListenerList();
  }

  public void init(Dimension puzzleSize, Image image) {
    init(puzzleSize, 
         new Dimension(image.getWidth(null), image.getHeight(null)),
         grabPixels(image));
  }

  public void init(Dimension puzzleSize, Dimension pixelSize, int[] pixels) {
    this.puzzleSize = puzzleSize;
    this.pixelSize = pixelSize;
    this.pixels = pixels;

    // percentage of pixel width forming one edge
    final float EDGE_EXTENT = 0.24f;
    
    pw = Math.round((float) pixelSize.width  / (float) puzzleSize.width);
    ph = Math.round((float) pixelSize.height / (float) puzzleSize.height);
    ew = Math.round(EDGE_EXTENT *
                    (float) pixelSize.width  / (float) puzzleSize.width);
    eh = Math.round(EDGE_EXTENT *
                    (float) pixelSize.height / (float) puzzleSize.height);

    firePuzzlePieceSetChanged(null);
  }

  public void generatePieces(Edge[][] edgeSet, Random random, boolean buffer,
                             Rectangle shuffleBox, boolean fireEvents) {
    ProgressDialog pd = new ProgressDialog(getDialogOwner(),
        "Generating Puzzle Pieces",
        false, puzzleSize.width * puzzleSize.height + 1);
    pd.setVisible(true);

    pieces.clear();
    Edge[][][] edges = generateEdges(puzzleSize, edgeSet, random);
    pd.setValue(1);

    for(int row = 0; row < puzzleSize.height; row++)
      for(int col = 0; col < puzzleSize.width; col++) {
        PuzzlePiece p = new PuzzlePiece(this, new PuzzlePiece.ID(row, col));
        for(int i = 0; i < 4; i++) p.setEdge(i, edges[row][col][i]);
        p.setSelectionColor(selectionColor);
        if(buffer) p.createImage();

        if(shuffleBox != null) {
          synchronized(this) {
            pieces.add(random.nextInt(pieces.size() + 1), p);
            shuffle(p, shuffleBox, random);
          }
        }
        else {
          synchronized(this) { pieces.add(p); }
        }

        if(fireEvents) firePuzzlePieceSetChanged(p.getBounds());

        pd.setValue(row * puzzleSize.width + col+1 + 1);
      }

    pd.setVisible(false);
  }

  protected Frame getDialogOwner() {
    return null;
  }

  protected static Edge[][][] generateEdges(Dimension puzzleSize,
                                            Edge[][] edgeSet, Random random) {
    Edge[][][] edgeArray = new Edge[puzzleSize.height][puzzleSize.width][4];

    for(int j = 0; j < puzzleSize.height; j++)
      for(int i = 0; i < puzzleSize.width; i++) {
        if(i > 0) {
          int e = random.nextInt(edgeSet.length);
          edgeArray[j][i-1][PuzzlePiece.RIGHT ] = edgeSet[e][0];
          edgeArray[j][i  ][PuzzlePiece.LEFT  ] = edgeSet[e][1];
        }
        if(j > 0) {
          int e = random.nextInt(edgeSet.length);
          edgeArray[j-1][i][PuzzlePiece.BOTTOM] = edgeSet[e][0];
          edgeArray[j  ][i][PuzzlePiece.TOP   ] = edgeSet[e][1];
        }
      }
    return edgeArray;
  }

  public void addPuzzlePieceSetListener(PuzzlePieceSetListener l) {
    listeners.add(PuzzlePieceSetListener.class, l);
  }

  public void removePuzzlePieceSetListener(PuzzlePieceSetListener l) {
    listeners.remove(PuzzlePieceSetListener.class, l);
  }

  protected final void firePuzzlePieceSetChanged(Rectangle r) {
    if(r != null) {
      r.x -= 2; r.y -= 2;
      r.width += 4; r.height += 4;
    }
    Object[] listenerList = listeners.getListenerList();
    for(int i = listenerList.length-2; i >= 0; i -= 2) {
      //if(listenerList[i] == PuzzlePieceSetListener.class) {
        ((PuzzlePieceSetListener)listenerList[i+1]).puzzlePieceSetChanged(r);
      //}
    }
  }

  public Dimension getPuzzleSize() {
    return puzzleSize;
  }

  public Dimension getPixelSize() {
    return pixelSize;
  }

  public Color getSelectionColor() {
    return selectionColor;
  }

  public void setSelectionColor(Color c) {
    selectionColor = c;
  }

  /*
   * All methods which modify or iterate over the list must be synchronized.
   */

  /**
   * Shuffles piece location, order, rotation, and flipping.
   * Connected pieces will stay connected.
   */
  public void shuffle(Rectangle boundingBox) {
    shuffle(boundingBox, new Random());
  }

  public synchronized void shuffle(Rectangle boundingBox, Random random) {
    int count = pieces.size();
    for(int i = 0; i < count; i++) {
      pieces.add(pieces.remove(random.nextInt(count)));
    }

    for(ListIterator i = pieces.listIterator(); i.hasNext(); ) {
      shuffle((PuzzlePiece) i.next(), boundingBox, random);
    }

    firePuzzlePieceSetChanged(null);
  }

  private void shuffle(PuzzlePiece p, Rectangle boundingBox, Random random) {
    p.setRotation(90 * random.nextInt(4));

    Rectangle bounds = p.getBounds();
    int minDx = boundingBox.x - bounds.x;
    int minDy = boundingBox.y - bounds.y;
    int maxDx = boundingBox.x + boundingBox.width
              - bounds.x - bounds.width;
    int maxDy = boundingBox.y + boundingBox.height
              - bounds.y - bounds.height;
    p.translate(minDx + random.nextInt(maxDx - minDx + 1),
                minDy + random.nextInt(maxDy - minDy + 1));
  }

  /**
   * Paints the set of PuzzlePieces.
   */
  public synchronized void paint(Graphics2D g) {
    for(ListIterator i = pieces.listIterator(); i.hasNext(); )
      ((PuzzlePiece) i.next()).paint(g);
  }

  /**
   * Moves the piece to the top (end of the list).
   */
  public synchronized void raisePiece(PuzzlePiece p) {
    if(pieces.remove(p)) pieces.add(p);
    firePuzzlePieceSetChanged(p.getBounds());
  }

  public synchronized void rotatePiece(PuzzlePiece p, int rotation) {
    Rectangle bounds = new Rectangle(p.getBounds());
    p.setRotation(p.getRotation() + rotation);
    bounds.add(p.getBounds());
    firePuzzlePieceSetChanged(bounds);
  }

  public boolean pieceIsSelected(PuzzlePiece p) {
    return p.getSelectionColor().equals(selectionColor);
  }
  public synchronized void selectPiece(PuzzlePiece p, boolean select) {
    p.setSelectionColor(select? selectionColor : Color.white);
    firePuzzlePieceSetChanged(p.getBounds());
  }

  public synchronized void movePiece(PuzzlePiece p, int dx, int dy) {
    Rectangle bounds = new Rectangle(p.getBounds());
    p.translate(dx, dy);
    bounds.add(p.getBounds());
    firePuzzlePieceSetChanged(bounds);
  }

  public void dropPiece(PuzzlePiece p) {
    // nothing to do here
  }

  /**
   * Finds the piece (or connected group) at the given location.
   */
  public synchronized PuzzlePiece find(Point loc) {
    PuzzlePiece target = null;
    PuzzlePiece p;
    for(ListIterator i = pieces.listIterator(); i.hasNext(); ) {
      p = (PuzzlePiece) i.next();
      if(p.coversPoint(loc)) target = p;
    }
    return target; // the last (highest) one in the list that covers the point
  }

  /**
   * Finds the piece (or connected group) with the given id.
   */
  public synchronized PuzzlePiece find(PuzzlePiece.ID id) {
    PuzzlePiece p, pi;
    for(ListIterator i = pieces.listIterator(); i.hasNext(); ) {
      p = (PuzzlePiece) i.next();
      for(pi = p; pi != null; pi = pi.next())
        if(id.equals(pi)) return p;
    }
    return null;
  }

  /**
   * The square of the maximum distance
   * between two pieces that can snap together.
   */
  private int joinTolerance = 50;
  public int getJoinTolerance() {
    return joinTolerance;
  }
  public void setJoinTolerance(int t) {
    if(t < 0 || t > 1000) throw new IllegalArgumentException();
    joinTolerance = t;
  }

  /**
   * Searches for a matching piece group to join with p, but does
   * not move or connect any pieces.  Returns null if none is found.
   */
  public synchronized PuzzlePiece findJoin(PuzzlePiece p) {
    if(!p.getSelectionColor().equals(selectionColor)) return null;
    PuzzlePiece q, pi, qi;
    Point distance = new Point();
    for(ListIterator i = pieces.listIterator(); i.hasNext(); ) {
      q = (PuzzlePiece) i.next();
      if(p == q || !q.getSelectionColor().equals(selectionColor)) continue;
      for(pi = p; pi != null; pi = pi.next())
        for(qi = q; qi != null; qi = qi.next())
          if(pi.trySnap(qi, distance) != PuzzlePiece.NONE) {
            if(distance.x * distance.x + distance.y * distance.y
               < joinTolerance) return q;
          }
    }
    return null;
  }

  /**
   * Joins the two piece groups p and q.  The q group will be translated
   * and rotated to line up with the p group.  Returns true if successful.
   */
  public synchronized boolean doJoin(PuzzlePiece p, PuzzlePiece q) {
    return doJoin(p, q, true);
  }
  /**
   * The local parameter signals whether the join was initiated here,
   * or somewhere else.
   */
  protected synchronized boolean doJoin(PuzzlePiece p, PuzzlePiece q,
                                        boolean local) {
    q.setRotation(p.getRotation());
    boolean joined = false;
    Point distance = new Point();

    PuzzlePiece pi, qi;
    for(pi = p; pi != null; pi = pi.next())
      for(qi = q; qi != null; qi = qi.next()) {
        //        System.err.println("Trying to join: " + pi + " & " + qi);
        int whichEdge = pi.trySnap(qi, distance);
        if(whichEdge != PuzzlePiece.NONE) {
          if(!joined) {
            q.translate(distance);
            joined = true;
          }
          pi.setEdge(whichEdge, null);
          whichEdge = (whichEdge + 2) % 4; // other piece's opposite edge
          qi.setEdge(whichEdge, null);
        }
      }
    //    System.err.println("Joined? " + joined);
    if(joined) joined = join(p, q, local);
    return joined;
  }

  protected synchronized boolean join(PuzzlePiece p, PuzzlePiece q,
                                   boolean local) {
    //System.out.println("Starting join... " + p + " & " + q);
    Rectangle qBounds = new Rectangle(q.getBounds());
    pieces.remove(q);
    //System.out.println("Calling connect on: " + p);
    boolean success = p.connect(q);
    p.setSelectionColor(p.getSelectionColor());
    qBounds.add(p.getBounds());
    //System.out.print("The entire piece... ");
//      for(PuzzlePiece n = p; n != null; n = n.next()){
//        System.out.println(n + " ");
//      }
    firePuzzlePieceSetChanged(qBounds);
    return success;
  }

  /**
   * Forces all pieces to fit inside the rectangle.
   */
  public synchronized void setBounds(Rectangle bounds) {
    int dx, dy;
    for(ListIterator i = pieces.listIterator(); i.hasNext(); ) {
      PuzzlePiece p = (PuzzlePiece) i.next();
      Rectangle b = p.getBounds();
      dx = 0; dy = 0;
      if(b.x < bounds.x)
        dx = bounds.x - b.x;
      else if(b.x + b.width > bounds.x + bounds.width)
        dx = bounds.x + bounds.width - b.x - b.width;
      if(b.y < bounds.y)
        dy = bounds.y - b.y;
      else if(b.y + b.height > bounds.y + bounds.height)
        dy = bounds.y + bounds.height - b.y - b.height;
      if(dx != 0 || dy != 0) {
        movePiece(p, dx, dy);
        dropPiece(p);
      }
    }
  }

  private static int[] grabPixels(Image image) {
    int w = image.getWidth(null);
    int h = image.getHeight(null);
    int[] pixels = new int[w * h];
    PixelGrabber pg = new PixelGrabber(image, 0, 0, w, h, pixels, 0, w);
    pg.startGrabbing();
    while(true) {
      int status = pg.getStatus();
      if((status & ImageObserver.ERROR) != 0 ||
         (status & ImageObserver.ABORT) != 0)
        throw new RuntimeException("Error grabbing pixels from image");
      else if((status & ImageObserver.ALLBITS) != 0)
        break;
      try {
        Thread.sleep(80);
      } catch(InterruptedException iex) { }
    }
    return pixels;
  }

  public String getName() {
    return name;
  }

  public int getPieceCount() {
    return pieces.size();
  }

  public synchronized int getSelectedPieceCount() {
    int count = 0;
    for(ListIterator i = pieces.listIterator(); i.hasNext(); ) {
      if(((PuzzlePiece) i.next()).getSelectionColor().equals(selectionColor))
        count++;
    }
    return count;
  }

  public String toString() {
    return getName() + "(" + (puzzleSize.width * puzzleSize.height) + ")";
  }
}
