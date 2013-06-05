import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

/**
 * The surface upon which the PuzzlePieceSet is painted.  Handles
 * interaction via keyboard and mouse, and calls the appropriate
 * methods on the PuzzlePieceSet in response to that input.  Listens
 * to the PuzzlePieceSet via the PuzzlePieceSetListener interface so
 * it can repaint changes in the PuzzlePieceSet.
 */
public class PuzzleCanvas extends JComponent
                          implements PuzzlePieceSetListener,
                                     ComponentListener, KeyListener,
                                     MouseListener, MouseMotionListener {
  private PuzzlePieceSet pieceSet;
  private PuzzlePiece    currentPiece;
  private Point          dragPoint;

  // true iff the current piece has been moved/rotated since being picked up
  private boolean        dragged;

  public PuzzleCanvas(PuzzlePieceSet pieceSet) {
    this.pieceSet = pieceSet;
    this.currentPiece = null;
    this.dragPoint = null;

    setBackground(Color.darkGray);
    setDoubleBuffered(true);

    if(pieceSet != null) pieceSet.addPuzzlePieceSetListener(this);
    addKeyListener(this);
    addMouseListener(this);
    addMouseMotionListener(this);
    addComponentListener(this);
  }

  public PuzzlePieceSet getPieces() {
    return pieceSet;
  }
  public synchronized void setPieces(PuzzlePieceSet pieceSet) {
    if(this.pieceSet != null)
      this.pieceSet.removePuzzlePieceSetListener(this);
    this.pieceSet = pieceSet;
    if(this.pieceSet != null) {
      this.pieceSet.addPuzzlePieceSetListener(this);
    }
    currentPiece = null;
    repaint();
  }

  public Dimension getPreferredSize() {
    Dimension d = new Dimension(200, 200);
    if(pieceSet != null && pieceSet.getPixelSize() != null) {
      d.width  = Math.max(d.width , pieceSet.getPixelSize().width * 5/3);
      d.height = Math.max(d.height, pieceSet.getPixelSize().height * 5/3);
    }
    return d;
  }
  public Dimension getMinimumSize() {
    return getPreferredSize();
  }

  // PuzzlePieceSetListener interface:
  public void puzzlePieceSetChanged(Rectangle r) {
    if(r != null) repaint(r);
    else repaint();
  }

  public void paintComponent(Graphics g) {
    g.setColor(getBackground());
    g.fillRect(0, 0, getWidth(), getHeight());
    if(pieceSet != null) pieceSet.paint((Graphics2D)g);
  }

  // KeyListener interface methods:
  public void keyPressed(KeyEvent evt) {
    if(currentPiece != null) {
      int rotation;
      switch(evt.getKeyCode()) {
        case KeyEvent.VK_SPACE:
        case KeyEvent.VK_RIGHT:
          rotation = -90;
          break;
        case KeyEvent.VK_LEFT:
          rotation = 90;
          break;
        default:
          return;
      }
      dragged = true;
      pieceSet.rotatePiece(currentPiece, rotation);
    }
  }
  public void keyReleased(KeyEvent evt) { }
  public void keyTyped(KeyEvent evt) { }

  // MouseListener interface methods:
  public void mousePressed(MouseEvent evt) {
    // Only left-clicks here.
    if((evt.getModifiers() & MouseEvent.BUTTON1_MASK) == 0) return;

    if(!hasFocus()) requestFocus();
    if(pieceSet != null && currentPiece == null) {
      dragPoint = evt.getPoint();
      PuzzlePiece p = pieceSet.find(dragPoint);
      if(p != null){
        //alm commented out
        //                &&
        //                 p.getSelectionColor().equals(pieceSet.getSelectionColor())) {
        currentPiece = p;
        dragged = false;
        pieceSet.raisePiece(p);
      }
    }
  }
  public void mouseReleased(MouseEvent evt) {
    // Only left-clicks here.
    if((evt.getModifiers() & MouseEvent.BUTTON1_MASK) == 0) return;

    if(currentPiece != null)
      currentPiece = pieceSet.find(currentPiece.id);
    if(currentPiece != null) {
      if(dragged) {
        pieceSet.dropPiece(currentPiece);
      }

      PuzzlePiece p;
      if(pieceSet != null && (p = pieceSet.findJoin(currentPiece)) != null) {
        pieceSet.doJoin(p, currentPiece);
        // pieceSet.raisePiece(p);
      }

      currentPiece = null;
    }
  }
  public void mouseClicked(MouseEvent evt) {
    // Only right-clicks here.
    if((evt.getModifiers() &
       (MouseEvent.BUTTON2_MASK | MouseEvent.BUTTON3_MASK)) != 0) {
      if(!hasFocus()) requestFocus();
      if(pieceSet != null) {
        if(currentPiece == null) {
          PuzzlePiece p = pieceSet.find(evt.getPoint());
          if(p != null) {
            pieceSet.selectPiece(p, !pieceSet.pieceIsSelected(p));
          }
        }
      }
    }
    else if(!dragged && (evt.getClickCount() % 2 == 0) &&
            (evt.getModifiers() & MouseEvent.BUTTON1_MASK) != 0) {
      PuzzlePiece p = pieceSet.find(evt.getPoint());
      if(p != null){
        //alm commented out
      //      &&
      //         p.getSelectionColor().equals(pieceSet.getSelectionColor())) {
        pieceSet.rotatePiece(p, -90);
        pieceSet.dropPiece(p);
      }
    }
  }

  public void mouseEntered(MouseEvent evt) { }
  public void mouseExited(MouseEvent evt) { }

  // MouseMotionListener interface methods:
  public void mouseDragged(MouseEvent evt) {
    if(currentPiece != null) {
      Point p = evt.getPoint();
      p.x = Math.max(0, Math.min(p.x, getWidth()));
      p.y = Math.max(0, Math.min(p.y, getHeight()));
      Rectangle b = currentPiece.getBounds();
      int dx = Math.max(-b.x, Math.min(p.x - dragPoint.x,
                                       getWidth() - b.x - b.width));
      int dy = Math.max(-b.y, Math.min(p.y - dragPoint.y,
                                       getHeight() - b.y - b.height));
      dragPoint = p;
      dragged = true;
      pieceSet.movePiece(currentPiece, dx, dy);
    }
  }
  public void mouseMoved(MouseEvent evt) { }

  // ComponentListener interface methods:
  public void componentResized(ComponentEvent evt) {
    if(pieceSet != null) {
      pieceSet.setBounds(new Rectangle(0, 0, getWidth(), getHeight()));
    }
  }
  public void componentShown(ComponentEvent evt) { }
  public void componentHidden(ComponentEvent evt) { }
  public void componentMoved(ComponentEvent evt) { }
}
