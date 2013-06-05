import java.awt.*;
import java.awt.event.*;
import java.net.*;
import javax.swing.*;

/**
 * A single-user puzzle application/applet.
 */
public class Puzzle extends JApplet {
  public static void main(String[] args) {
    if(args.length != 2 || args[1].indexOf('x') < 0) {
      System.err.println("Usage: java Puzzle <imageurl> <width>x<height>");
      return;
    }

    final JFrame f = new JFrame("Puzzle");
    Puzzle p = new Puzzle();
    p.frame = f;
    f.setContentPane(p.getContentPane());

    f.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent evt) {
        f.setVisible(false);
        f.dispose();
        System.exit(0);
      }
    });

    int i = args[1].indexOf('x');
    p.loadPuzzle(args[0], args[1].substring(0, i), args[1].substring(i+1));

    f.pack();
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    f.setLocation(screenSize.width/2 - f.getWidth()/2,
                  screenSize.height/2 - f.getHeight()/2);
    f.setVisible(true);

    PuzzlePieceSet pieces = p.canvas.getPieces();
    if(pieces != null)
      pieces.shuffle(new Rectangle(new Point(0, 0), p.canvas.getSize()));
  }

  protected JFrame       frame;
  protected PuzzleCanvas canvas;
  protected Color        color;

  public Puzzle() {
    canvas = new PuzzleCanvas(null);
    getContentPane().add(canvas);

    frame = null;
    color = Color.white;
  }
  
  public void init() {
    checkParam("image");
    checkParam("size");
    String size = getParameter("size").toLowerCase();
    if(size.length() < 3 || size.indexOf('x') < 1)
      throw new IllegalArgumentException("size must be WxH");
    loadPuzzle(getParameter("image"),
               size.substring(0, size.indexOf('x')),
               size.substring(size.indexOf('x') + 1));
    PuzzlePieceSet pieces = canvas.getPieces();
    if(pieces != null)
      pieces.shuffle(new Rectangle(new Point(0, 0), canvas.getSize()));
  }

  private void checkParam(String p) {
    if(getParameter(p) == null)
      throw new IllegalArgumentException("Parameter is null: " + p);
  }

  protected void loadPuzzle(String imgStr, String wStr, String hStr) {
    if(imgStr == null || imgStr.equals("")) {
      throw new IllegalArgumentException("Image URL is null");
    }

    URL imgUrl;
    try {
      try {
        imgUrl = new URL(getDocumentBase(), imgStr);
      } catch(NullPointerException npex) {
        imgUrl = new URL(new java.io.File(".").toURL(), imgStr);
      }
    } catch(MalformedURLException mfuex) {
      throw new IllegalArgumentException(mfuex.toString());
    }

    int w, h;
    try {
      w = Integer.parseInt(wStr);
      h = Integer.parseInt(hStr);
    } catch(NumberFormatException nfex) {
      w = h = 0;
    }

    if(w < 2 || h < 2) {
      throw new IllegalArgumentException("Width and height must be integers >= 2");
    }

    Image img = loadImage(imgUrl);
    if(img == null) {
      throw new IllegalArgumentException("Failed to load image: " + imgStr);
    }

    if(img.getWidth(null) / w < 20)
      throw new IllegalArgumentException("Pieces are too small! " +
                                         "Use a larger number for width.");
    if(img.getHeight(null) / h < 20)
      throw new IllegalArgumentException("Pieces are too small! " +
                                         "Use a larger number for height.");

    loadPuzzle(img, w, h);
  }

  protected void loadPuzzle(Image img, int w, int h) {
    PuzzlePieceSet pieces = new PuzzlePieceSet("puzzle", color);
    pieces.init(new Dimension(w, h), img);
    pieces.generatePieces(edgeSet, new java.util.Random(), true, null, false);
    canvas.setPieces(pieces);
  }

  protected final Image loadImage(URL url) {
    Image img;
    try {
      img = getImage(url);
    } catch(NullPointerException npex) {
      img = Toolkit.getDefaultToolkit().getImage(url);
    }

    MediaTracker mt = new MediaTracker(this);
    mt.addImage(img, 0);
    while(true) {
      try {
        mt.waitForID(0);
        break;
      } catch(InterruptedException iex) { }
    }
    return mt.isErrorID(0)? null : img;
  }

  public static Edge[][] edgeSet = new Edge[][] {
    //{ new SquareTabEdge(false), new SquareTabEdge(true)  },
    //{ new SquareTabEdge(true) , new SquareTabEdge(false) },
    //{ new RoundTabEdge(false) , new RoundTabEdge(true)   },
    //{ new RoundTabEdge(true)  , new RoundTabEdge(false)  },
    { new PuzzleTabEdge(false), new PuzzleTabEdge(true)  },
    { new PuzzleTabEdge(false), new PuzzleTabEdge(true)  },
    { new PuzzleTabEdge(false), new PuzzleTabEdge(true)  },
    { new PuzzleTabEdge(true) , new PuzzleTabEdge(false) },
    { new PuzzleTabEdge(true) , new PuzzleTabEdge(false) },
    { new PuzzleTabEdge(true) , new PuzzleTabEdge(false) },
    { new WaveEdge()          , new WaveEdge()           }
  };
}
