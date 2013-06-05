/**
 *  Used to store and pass player info across a stream.
 *
 *  @author Brian D. Mesh
 *  @version 2/26/2000
 *
 **/

import java.awt.*;
import java.awt.image.*;

public class PlayerInfo implements java.io.Serializable{

    int w, h;
    int [] pixels;
    String pictName = "";

    public PlayerInfo(String pict){
	pictName = pict;
    }

    public PlayerInfo(Image image){
	//	BufferedImage bi = new BufferedImage(400,400,BufferedImage.TYPE_INT_ARGB);
	//Graphics2D g = (Graphics2D) image.getGraphics();
	//g.drawImage(image,0,0,null);
	w = image.getWidth(null);
	h = image.getHeight(null);
	//System.err.println("Size of my image is " + w + " x "+h);
	pixels = new int[w*h];
	PixelGrabber pg = new PixelGrabber(image, 0, 0, w, h, pixels, 0, w);
	try {
	    pg.grabPixels();
	} catch (InterruptedException e) {
	    System.err.println("interrupted waiting for pixels!");
	    return;
	}
	if ((pg.getStatus() & ImageObserver.ABORT) != 0) {
	    System.err.println("image fetch aborted or errored");
	    return;
	}
	 




	/*pg.startGrabbing();
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
	    }*/
    }

  public Image getPicture(){
    if (pictName.length()==0){
      //System.err.println("Recomposing picture");
      MemoryImageSource source = new MemoryImageSource(w,h,pixels,0,w);
      return Toolkit.getDefaultToolkit().createImage(source);
    }
    else{
      //System.err.println("Opening image");
      return Toolkit.getDefaultToolkit().createImage(pictName);
    }
  }
}
