/**
 * A class implements this interface to be notified about changes
 * in a PuzzlePieceSet.
 */
public interface PuzzlePieceSetListener extends java.util.EventListener {

  /**
   * Notifies the listener that the PuzzlePieceSet has changed
   * in some way (pieces were added/removed/moved/rotated, etc).
   * If the Rectangle is non-null, changes were confined to the
   * bounding box.
   */
  public void puzzlePieceSetChanged(java.awt.Rectangle r);
}
