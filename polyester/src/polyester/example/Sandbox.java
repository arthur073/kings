package polyester.example;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import lights.*;
import lights.interfaces.*;
import polyester.*;
import lights.utils.TupleFactory;


public class Sandbox {

	public static void main (String[] args){
		/*DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
		domFactory.setNamespaceAware(true); // never forget this!
		DocumentBuilder builder;
		
		try {
			builder = domFactory.newDocumentBuilder();
		
		System.out.println("loading");
		String filename=("book1.xml");
		File thefile = new File(filename);
		System.out.println(thefile.getAbsolutePath());
	
		Document doc = builder.parse(thefile);
		System.out.println("contents:"+doc.getDocumentElement().getTextContent());
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		Integer i = new Integer(3);
		Object[] arr = new Object[3];
		arr[0] = i;
		arr[1] = "duhfield";
		arr[2] = new File("C:\\windows");
		ITuple t = TupleFactory.createTuple("YO", arr);
		//ITuple t2 = (ITuple)t.clone();
		List<ITuple> readqueries= new LinkedList<ITuple>();
		
		readqueries.add(t);
		
		Class<?> mytype = t.get(2).getType();
		
		try {
			Class<?> copyofclass = mytype.newInstance().getClass();
			
			System.out.println("original type: "+ mytype.getCanonicalName());
			System.out.println("copy "+ copyofclass.getCanonicalName());
			System.out.println("equality: "+ copyofclass.equals(mytype) + " == :"+ (copyofclass==mytype));
			
		} catch (InstantiationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		//THIS IS JUst about where the TUpleFactory is!!
		System.out.println("t is: "+t.toString());
		//System.out.println("t2 is: "+t2.toString());
		//System.out.println("They are equal:"+ t2.equals(t));
		//System.out.println("t is in my List, is t2?:"+ readqueries.contains(t2));
		
		for (IField f:t.getFields()){
			System.out.println("type:"+f.getType().getName());
		}
		
		TupleSpace ts= new TupleSpace();
		//Worker ww = new DatabaseWorker(ts);
		try {
			ts.out(t);
		//	ts.out(t2);
		} catch (TupleSpaceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println(ts.toString());
		
		String nayme="";  // Declare & initialize a String to hold input.
	    Scanner input=new Scanner(System.in); // Decl. & init. a Scanner.

	    System.out.print("Whut yur nayme? >");  // Troll asks for name.
	    nayme=input.next(); // Get what the user types.
	    System.out.println();  // Move down to a fresh line.
	    // Then say something trollish and use their name.
	    System.out.println("Hur, hur! Dat's a phunny nayme, " + nayme + "!");
		
		
	}
}
