
import lime.*;
import lights.adapters.*;
import lights.interfaces.*;

import java.awt.*;
import java.util.*;

public class LimePuzzlePieceSet extends PuzzlePieceSet
                                implements ReactionListener {

  private LimePuzzleAgent agent;
  private LimeTupleSpace  lts;

  private RegisteredReaction registeredPuzzleReaction;
  private RegisteredReaction registeredPieceReaction;

  public LimePuzzlePieceSet(String name, Color color,
                            LimePuzzleAgent agent, LimeTupleSpace lts) {
    super(name, color);
    this.lts = lts;
    this.agent = agent;

    installPuzzleUpon();
  }

  protected Frame getDialogOwner() {
    return agent.getFrame();
  }

  /**
   * Writes puzzle descriptor, pieces, and pixels to the tuple space.
   * Assumes we are already inside the agent thread.
   */
  void shareAll() {
    ProgressDialog pd = new ProgressDialog(getDialogOwner(),
        "Sharing Puzzle Pieces", false, pieces.size() + 1);
    pd.setVisible(true);
    int count = 0;
    AgentLocation myLoc = new AgentLocation(agent.getMgr().getID());

    try {
      // write a description of the puzzle to the tuple space
	lts.out(myLoc, new Tuple().addActual(getName())
                              .addActual(puzzleSize)
                              .addActual(pixelSize)
                              .addActual(new AgentLocation(agent.getMgr().getID())));
      pd.setValue(++count);

      // write all pieces and pixels to the tuple space
      for(ListIterator i = pieces.listIterator(); i.hasNext(); ) {
        PuzzlePiece p = (PuzzlePiece) i.next();
        lts.out(myLoc, new Tuple().addActual(getName())
                                .addActual(p.id)
                                .addActual(p.getPixels()));
        lts.out(myLoc, new Tuple().addActual(getName())
                                .addActual(p.id)
                                .addActual(p));
        pd.setValue(++count);
      }
    }
    catch(TupleSpaceEngineException ex) {
      ex.printStackTrace(System.err);
    }

    pd.setVisible(false);
    pd.dispose();
  }

  private RegisteredReaction installUpon(ITuple template, short mode) {
    RegisteredReaction retValue = null;
    try {
      //Thread.sleep(200);
      retValue = lts.addWeakReaction(new Reaction[] {
        new UbiquitousReaction(template, this, mode) })[0];
    } catch(TupleSpaceEngineException ex) {
      ex.printStackTrace(System.err); 
    }
    return retValue;
  }

  private void installPuzzleUpon() {
    agent.enqueueOp(new Runnable() { public void run() {
      registeredPuzzleReaction = installUpon(new Tuple() 
          .addActual(getName())
          .addFormal(Dimension.class)
          .addFormal(Dimension.class)
          .addFormal(AgentLocation.class), Reaction.ONCE);
    }});
  }

  private void installPieceUpon() {
    agent.enqueueOp(new Runnable() { public void run() {
      registeredPieceReaction = installUpon(new Tuple()
          .addActual(getName())
          .addFormal(PuzzlePiece.ID.class)
          .addFormal(PuzzlePiece.class), Reaction.ONCEPERTUPLE);
    }});
  }

  public void reactsTo(final ReactionEvent evt) {
    if(registeredPuzzleReaction != null) {

      if(puzzleSize == null) {
        ITuple t = evt.getEventTuple();
        Dimension puzzleSize = (Dimension) t.get(1).getValue();
        Dimension pixelSize = (Dimension) t.get(2).getValue();
        init(puzzleSize, pixelSize, null);
      }

      registeredPuzzleReaction = null;

      if(registeredPieceReaction == null) {
        installPieceUpon();
      }
    }
    else {
      if(evt.getSourceAgent().equals(agent.getMgr().getID())) {
        if(agent.DEBUG)
          System.err.println("LimePuzzle: ignoring my own puzzle piece tuple.");
        return;
      }

      ITuple t = evt.getEventTuple();
      PuzzlePiece p = (PuzzlePiece) t.get(2).getValue();
      if(agent.DEBUG) 
        System.err.println("LimePuzzle: piece reaction: " + p.id);

      PuzzlePiece myPiece = find(p.id);
      if(myPiece != null) {
        Rectangle bounds = new Rectangle(myPiece.getBounds());
        // commented out by alm to prevent pieces from moving when just
        // selected or rotated
        //myPiece.setLocation(p.getLocation());
        //myPiece.setRotation(p.getRotation());
        myPiece.setSelectionColor(p.getSelectionColor());
        bounds.add(myPiece.getBounds());
        firePuzzlePieceSetChanged(bounds);


        synchronized(this) {
          PuzzlePiece q = myPiece;

          // a piece was reacted to, now we need to update the local piece

          // the piece reaced to is p
          // my piece which corresponds to p is myPiece
//            System.err.println("in reaction: p=");
//            for (PuzzlePiece x = p; x!=null; x=x.next()) {
//              System.err.println("::::::: "+x.id);
//            }
//            System.err.println("end of p");
//            System.err.println("my piece=");
//            for (PuzzlePiece x = myPiece; x!=null; x=x.next()) {
//              System.err.println("::::::: "+x.id);
//            }
//            System.err.println("end of my piece=");

          ///^^^^^^^^^^^^^^debugging^^^^^^^^^^^^^^^^^

          PuzzlePiece prev = p;
          p = p.next();
          PuzzlePiece piecesToFit = p;

          while (piecesToFit != null) {
            PuzzlePiece s = find(p.id);

            Rectangle sBounds = new Rectangle(s.getBounds());
            if(s != myPiece) {

              //System.err.println("Starting doJoin: " + myPiece + " & " + s);
              if(doJoin(myPiece, s, false)) {
                // fit the pieces together
                firePuzzlePieceSetChanged(sBounds);
                if (piecesToFit == p) {
                  piecesToFit = piecesToFit.next(); 
                } 
              } 
                
            } else // the piece being looked at (p) is already linked in
              piecesToFit = piecesToFit.next();

            p = p.next();
            if (p == null) p = piecesToFit;
          }
          

        }
      }
      else {
        final PuzzlePiece newPiece = p;

        agent.enqueueOp(new Runnable() { public void run() {
          AgentLocation loc = new AgentLocation(evt.getSourceAgent());
          boolean foundAllPixels = true;
          for(PuzzlePiece q = newPiece; q != null; q = q.next()) {
            ITuple s = new Tuple().addActual(getName())
                                  .addActual(q.id)
                                  .addFormal(PuzzlePiece.Pixels.class);
            try {
              s = lts.rdp(loc, loc, s);
            }
            catch(TupleSpaceEngineException ex) {
              ex.printStackTrace(System.err);
              s = null;
            }

            if(s != null) {
              q.setPixels((PuzzlePiece.Pixels) s.get(2).getValue());
              q.setPieceSet(LimePuzzlePieceSet.this);
            }
            else {
              foundAllPixels = false;
              break;
            }
          }
          if(foundAllPixels) {
            pieces.add(newPiece);
            firePuzzlePieceSetChanged(newPiece.getBounds());
          }
        }});
      }
    }
  }

  /**
   * These methods extend the normal PuzzlePieceSet operations to
   * update the changed piece in the LimeTupleSpace.
   */
  public void rotatePiece(PuzzlePiece p, int rotation) {
    super.rotatePiece(p, rotation);
    // Update is not really necessary until the drop.
    /*
    updatePiece(p, null);

    */
  }

  public void selectPiece(final PuzzlePiece p, boolean select) {
    if(select) {
      //System.err.println("LimePuzzle (" + selectionColor + "): select " + p.id);
      agent.enqueueOp(new Runnable() { public void run() {
        // Remove the piece from its old location, and put it here.
        AgentLocation loc = ((Owner) p.getSelectionColor()).getLocation();
        AgentLocation myLoc = new AgentLocation(agent.getMgr().getID());
        ITuple t;
        try {
          t = lts.inp(loc, loc, new Tuple()
              .addActual(getName())
              .addActual(p.id)
              .addFormal(PuzzlePiece.class));
        }
        catch(TupleSpaceEngineException ex) {
          ex.printStackTrace(System.err);
          t = null;
        }
        if(t != null) {
          superSelectPiece(p);
          try {
            lts.out(myLoc, new Tuple()
              .addActual(getName())
              .addActual(p.id)
              .addActual(p));
          }
          catch(TupleSpaceEngineException ex) {
            ex.printStackTrace(System.err);
          }

          // Remove all pixels from their old location and put them here.
          for(PuzzlePiece q = p; q != null; q = q.next()) {
            try {
              t = lts.inp(loc, loc, new Tuple()
                .addActual(getName())
                .addActual(q.id)
                .addFormal(PuzzlePiece.Pixels.class));
            }
            catch(TupleSpaceEngineException ex) {
              ex.printStackTrace(System.err);
              t = null;
            }
            if(t != null) try {
              lts.out(myLoc, t);
            }
            catch(TupleSpaceEngineException ex) {
              ex.printStackTrace(System.err);
            }
          }
        }
        else {
          Toolkit.getDefaultToolkit().beep();
        }
      }});
    }
  }

  /**
   * Required for access from inner class above.
   */
  private void superSelectPiece(PuzzlePiece p) {
    super.selectPiece(p, true);
  }

  public void dropPiece(PuzzlePiece p) {
    super.dropPiece(p);

    ///alm: note probably want to remove this to keep movements from being
    //propaged to the tuple space.
    //updatePiece(p, null);
  }

  protected synchronized boolean join(final PuzzlePiece p, final PuzzlePiece q,
                                   boolean notify) {
    boolean success = super.join(p, q, notify);
    if(notify) updatePiece(p, q.id);
    return success;
  }

  /**
   * Writes the piece to the LimeTupleSpace after removing the old one.
   */
  private void updatePiece(final PuzzlePiece p,
                           final PuzzlePiece.ID removePieceID) {
    agent.enqueueOp(new Runnable() { public void run() {
      AgentLocation myLoc = new AgentLocation(agent.getMgr().getID());
      ITuple t;
      if(removePieceID != null) {
        try {
	    t = lts.inp(myLoc, myLoc, new Tuple().addActual(getName())
                                           .addActual(removePieceID)
                                           .addFormal(PuzzlePiece.class));
        }
        catch(TupleSpaceEngineException ex) {
          ex.printStackTrace(System.err);
          t = null;
        }
	if(t == null) {
          System.err.println("LimePuzzle (" + selectionColor + "): " +
                             "failed to remove joined piece: " + removePieceID);
        }
      }

      try {
        t = lts.inp(myLoc, myLoc, new Tuple().addActual(getName())
                                             .addActual(p.id)
                                             .addFormal(PuzzlePiece.class));
      }
      catch(TupleSpaceEngineException ex) {
        ex.printStackTrace(System.err);
        t = null;
      }
      if(t == null) {
        System.err.println("LimePuzzle (" + selectionColor + "): " +
                           "failed to remove updated piece: " + p.id);
      }

      try {
        lts.out(myLoc, new Tuple().addActual(getName())
                                .addActual(p.id)
                                .addActual(p));
      }
      catch(TupleSpaceEngineException ex) {
        ex.printStackTrace(System.err);
      }
    }});
  }
 
}
