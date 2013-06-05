import java.awt.*;
import java.awt.event.*;

/**
 * This is an object dialog.  It is used by MapViewer to allow the user to record
 * objects they have found and search for others.
 *
 * @author Brian Mesh
 * @version 2/24/2000
 */

public class ObjectDialog extends Frame{

    public static final int FOUND = 1;
    public static final int LOOKFOR = 2;

    private MapViewer mapV;
    private int dType;
    private Panel lPanel, rPanel;
    private Button okButton;
    private CheckboxGroup objType, searchType;
    private Checkbox getOne;

    public static void main (String args[]){
	new ObjectDialog(LOOKFOR,null);
    }

    public ObjectDialog(int dType, MapViewer parentViewer){
	setSize(200,100);
	setResizable(false);
	setLayout(null);
	lPanel = new Panel();
	lPanel.setBounds(10,25,85,70);
	lPanel.setLayout(new GridLayout(3,1));
	objType = new CheckboxGroup();
	searchType = new CheckboxGroup();
	lPanel.add(new Checkbox("Green Flag",objType,true));
	lPanel.add(new Checkbox("Red Flag",objType,false));
	lPanel.add(new Checkbox("Clue",objType,false));
	add(lPanel);
	rPanel = new Panel();  
	rPanel.setBounds(105,25,85,45);
	rPanel.setLayout(new GridLayout(2,1));
	getOne = new Checkbox("Get one",searchType,true);
	rPanel.add(getOne);
	rPanel.add(new Checkbox("Get all",searchType,false));
	okButton = new Button("Submit");
	okButton.setBounds(105,70,85,20);
	okButton.addActionListener(new ActionListener(){
		public void actionPerformed(ActionEvent e){
		    submitInfo();
		}
	    });
	add(okButton);
	switch(dType){
	case FOUND:
	    setTitle("Found an Object");
	    break;
	case LOOKFOR:
	    setTitle("Look For Object");
	    add(rPanel);
	    break;
	default:
          //System.err.println("ObjectDialong: Invalide type parameter");
	    dialogDone();
	    return;
	}
      	mapV = parentViewer;
	this.dType = dType;
	addWindowListener(new WindowAdapter(){
		public void windowClosing(WindowEvent e){
		    dialogDone();
		}
	    });
	setVisible(true);
	requestFocus();
    }

    private void submitInfo(){
	if (mapV != null)
	    mapV.submitObject(dType,objType.getSelectedCheckbox().getLabel(),getOne.getState());
	dialogDone();
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
	if (mapV == null)
	    System.exit(0);
	else
	    mapV.objectDialogComplete(dType);
    }
}
