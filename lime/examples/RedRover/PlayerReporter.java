import java.awt.*;
import java.awt.event.*;

/**
 * This is used to display a picture and information about a given player.
 *
 * @author Brian Mesh
 * @version 2/24/2000
 */

public class PlayerReporter extends Frame{

    private Button okButton;
    private Feature thePlayer;
    private Image playerPic;

    public static void main (String args[]){
	new PlayerReporter(null, null);
    }

    public PlayerReporter(Image playerPic){
      //setSize(300,280);
	setTitle("Me");
	this.playerPic = playerPic;
	setVisible(true);
	addWindowListener(new WindowAdapter(){
		public void windowClosing(WindowEvent e){
		    setVisible(false);
		}
	    });
    }

    public PlayerReporter(Feature thePlayer, Image playerPic){
	this.thePlayer = thePlayer;

	if (thePlayer == null || playerPic == null){
	    dialogDone();
	}
	setResizable(false);
	setTitle((String)thePlayer.getAttributeValue(Feature.TEAM)+": "+
		 (String)thePlayer.getAttributeValue(Feature.NAME));
	this.playerPic = playerPic;
	setSize(300,300);
	setLayout(null);
	okButton = new Button("OK");
	okButton.addActionListener(new ActionListener(){
		public void actionPerformed(ActionEvent e){
		    dialogDone();
		}
	    });
	okButton.setBounds(10,280,280,15);
	add(okButton);
	addWindowListener(new WindowAdapter(){
		public void windowClosing(WindowEvent e){
		    dialogDone();
		}
		public void windowActivated(WindowEvent e){
		    repaint();
		}
		public void windowOpened(WindowEvent e){
		    repaint();
		}
		public void windowDeiconified(WindowEvent e){
		    repaint();
		}
	});
	addComponentListener(new ComponentAdapter(){
	    public void componentMoved(ComponentEvent e){
		repaint();
	    }
	    });
	setVisible(true);
	requestFocus();
    }

    public void paint (Graphics g){
	if (playerPic != null){
	    g.drawImage(playerPic,10,25,280,250,this);	
	}    
    }


    private void dialogDone(){
	setVisible(false);
	try{
	    finalize();
	}
	catch(Throwable e){
	    System.err.println("Error finalizing object dialog");
	    System.exit(1);
	}
    }

}
