package polyester.example;

//import java.util.Scanner;

import javax.swing.JOptionPane;

//import polyester.AbstractWorker;
import polyester.Worker;
import polyester.util.SearchString;

import lights.Field;
import lights.Tuple;
import lights.interfaces.ITuple;
import lights.interfaces.ITupleSpace;
import lights.interfaces.IValuedField;
import lights.interfaces.TupleSpaceException;

public class WebInputWorker extends Worker {

	public WebInputWorker(ITupleSpace space) {
		super(space);
	}
	
	@Override
	public void work() {
		search(inputSearch());
	}
	
	public void search(String searchString) {
		//create and populate first tuple
		ITuple t = new Tuple();
		IValuedField f = new Field().setValue("Search");
		t.add(f);
		
		SearchString s = null;

		try {
			s = new SearchString(searchString); 
		}
		catch (Exception e) {return;} //caution: no tuple goes out if invalid search
		
		t.add(new Field().setValue(s));
		
		//put it in the tuple space
		try {
			space.out(t);
		}
		catch (TupleSpaceException e) {}
	}

	
	/**
	 * Very rudimentary UI to get user's xpath search expression.
	 * In a real implementation, this would be done through a web 
	 * interface. 
	 */
	public String inputSearch() {
		return JOptionPane.showInputDialog("Enter your search");	
		//Scanner sc = new Scanner(System.in);
		//System.out.println("Enter your search");
		//return sc.nextLine();
	}

}
