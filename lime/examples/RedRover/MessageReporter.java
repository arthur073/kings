import java.awt.*;
import java.awt.event.*;

/**
 * This is simply a message display window.
 *
 * @author Brian Mesh
 * @version 2/24/2000
 */

public class MessageReporter extends Frame{

    private Button okButton;
    private static boolean parentCalled = true;

    public static void main (String args[]){
	parentCalled = false;
	new MessageReporter("This is a test message to see how well this really works.");
    }

    public MessageReporter(String mesg){
	setSize(400,80);
	setLayout(new BorderLayout());
	add(new TextField(mesg),BorderLayout.CENTER);
	okButton = new Button("OK");
	okButton.addActionListener(new ActionListener(){
		public void actionPerformed(ActionEvent e){
		    dialogDone();
		}
	    });
	add(okButton,BorderLayout.SOUTH);
	addWindowListener(new WindowAdapter(){
		public void windowClosing(WindowEvent e){
		    dialogDone();
		}
	    });
	setVisible(true);
	requestFocus();
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
	if (!parentCalled)
	    System.exit(0);
    }

}
