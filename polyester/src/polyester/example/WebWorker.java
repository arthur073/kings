package polyester.example;

import java.util.*;

import lights.*;
import lights.extensions.XMLField;
import lights.interfaces.*;
//import lights.extensions.*;
import polyester.AbstractWorker;
import polyester.TupleFactory;
//import polyester.Worker;
//import polyester.util.*;

import org.w3c.dom.*;

//import com.sun.org.apache.xerces.internal.dom.DeferredDocumentImpl;

public class WebWorker extends AbstractWorker {
	
	public WebWorker(ITupleSpace space) {
		super(space);
		//the template that this worker looks for is [literal:"Result"; formal:DeferredDocumentImpl]
		ITuple t = new Tuple();
		t.add(new Field().setValue("Result"));	
		/* With Document fields, matching works but it's difficult or impossible to keep track of repeated tuples because 
		 * of non-standard implementations, and the lack of hashcode() and equals() in these implementations
		 */
		t.add(TupleFactory.createXMLTemplateField());

		this.addQueryTemplate(t);
	}
	
	@Override
	protected List<ITuple> answerQuery(ITuple template, ITuple query) {
		
		System.out.println("New Result ! ");
		
		
		Document doc = (Document) ( (XMLField) query.get(PAYLOAD)).getValue();
		System.out.println(doc.getBaseURI());
		
		
		//return nothing
		return new ArrayList<ITuple>();
	}



	}
