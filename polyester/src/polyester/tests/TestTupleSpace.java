package polyester.tests;

import java.util.*;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.xpath.*;

import lights.*;
import lights.extensions.*;
import lights.interfaces.*;

import static org.junit.Assert.*;

import org.junit.*;

import polyester.*;
import polyester.example.DatabaseWorker;
import polyester.example.WebWorker;
import polyester.util.SearchString;
import static polyester.Worker.*;

public class TestTupleSpace {
	
	
	TupleSpace ts;

	@Before
	public void setUp() throws Exception {
		ts = new TupleSpace();
	}
	
	@Test
	public final void testOneTupleWildCardField() throws TupleSpaceException {
		//create and populate first tuple
		ITuple t = new Tuple();
		IValuedField f = new Field().setValue("France");
		t.add(f);
		
		//put it in the tuple space
		ts.out(t);
		
		//create and populate the second tuple 
		ITuple t2 = new Tuple();
		IField f2 = new Field().setType(String.class); //it's a "wild card"
		t2.add(f2);
		
		// see if they match
		ITuple[] results = null;
		try {
			results = ts.rdg(t2);
		} catch (TupleSpaceException e) {}
		
		assertEquals(results.length, 1);
		assertEquals(results[0].length(), 1);
		assertEquals( ((Field) results[0].get(0)).getValue(), "France");
		
	}
	
	@Test
	public final void testRegexp() throws TupleSpaceException {
		//NOte: this test now somewhat obsolete: change in WebWorker implementation 
		//means the test results cannot be accessed directly. Therefore no Assert could be written for this test.
		
		//populate tuple space
		IField reg = new RegexField("aaaaaab");
		IField reg2 = new RegexField("a*b");
		
		Tuple t = new Tuple();
		t.add(reg);
		ts.out(t);
		
		Tuple t2 = new Tuple();
		t2.add(reg2);
		
		System.out.println(reg.getType());
		System.out.println(reg2.getType());
		
		new WebWorker(ts).start();
		
	}
	
	@Test
	public final void testRegexp2() throws TupleSpaceException {
		
		//populate tuple space
		IField reg = new RegexField("a*b");
		IField reg2 = new RegexField("aaaaaab");
		
		Tuple t = new Tuple();
		t.add(reg);
		ts.out(t);
		
		Tuple t2 = new Tuple();
		t2.add(reg2);
		
		System.out.println(reg.getType());
		System.out.println(reg2.getType());
		
		//List<String> l = new WebWorker(ts).searchResultsAsStringList(t2);
		//System.out.println(l);
		
		//assertNotNull(l);
		// note = obsolete.
	}

	
	@Test
	public final void testTwoFields() throws Exception {
		//create and populate first tuple
		ITuple t = new Tuple();
		IValuedField f = new Field().setValue("Search");
		t.add(f);

		//DOM factory boilerplate
		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
		domFactory.setNamespaceAware(true); // never forget this!
		DocumentBuilder builder = domFactory.newDocumentBuilder();
		
		Document doc = builder.parse("books.xml");
		IField reg = new XMLField(doc);
		
		t.add(reg);
		
		//put it in the tuple space
		ts.out(t);
		
		//XPATH boilerplate
		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();
		
		//create and populate the second tuple 
		ITuple t2 = new Tuple();
		IField f2 = new Field().setValue("Search"); //must be exact match
		t2.add(f2);
		
		//create matching tuple
		XPathExpression expr = xpath
				.compile("//book[author='Neal Stephenson']/title/text()");		
		IField reg2 = new XMLField(expr);
		t2.add(reg2);
		
		// see if they match
		ITuple[] results = null;
		try {
			results = ts.rdg(t2);
		} catch (TupleSpaceException e) {}
		
		assertEquals(results.length, 1);
		assertEquals(results[0].length(), 2);
		assertEquals( ((Field) results[0].get(0)).getValue(), "Search");
		
		Document doc2 = (Document) (((XMLField) results[0].get(1))).getValue();
		
		NodeList nodes = (NodeList) expr.evaluate(doc2, XPathConstants.NODESET);
		
		assertEquals( nodes.item(0).getNodeValue(), "Snow Crash");
		assertEquals( nodes.item(1).getNodeValue(), "Zodiac");
		
	}
	
	/* fopllowing tests obsolete
	@Test
	public final void testXPathField() throws Exception {
		
		//using WebWorker and DatabaseWorker - see inside each to see how it's done
		
		assertEquals(ts.size(), 0);
		
		WebWorker ww = new WebWorker(ts);
		ww.search("//book[author='Neal Stephenson']/title/text()");
		
		assertEquals(ts.size(), 1);
		
		DatabaseWorker dw = new DatabaseWorker(ts);		
		dw.add("books.xml");
		
		ITuple[] results = dw.handleSearch();
		
		assertEquals(results.length, 1);
		assertEquals(results[0].length(), 2);
		assertEquals( ((Field) results[0].get(VERB)).getValue(), "Search");
		
		SearchString ex3 = (SearchString) (( (Field) results[0].get(PAYLOAD))).getValue();
		
		System.out.println(ex3);
		assertEquals(ex3.getValue(), "//book[author='Neal Stephenson']/title/text()");
		
		List l = dw.retrieveMatches(results[0]);
		assertEquals(l.size(), 1);
		System.out.println("List: " + l);
		
		Set s = dw.retrieveAllMatches(results);
		assertEquals(s.size(), 1);
		System.out.println("Set: " + s);
		
		assertEquals(ts.size(), 1);
		
		dw.postMatches(s);
		
		assertEquals(ts.size(), 2);
		
		ITuple[] results2 = ww.retrieve();
		
		assertEquals(ts.size(), 2);
		
		assertEquals(results2.length, 1);
		assertEquals(results2[0].length(), 2);
		assertEquals( ((Field) results2[0].get(VERB)).getValue(), "Result");
		
		Object o = ((Field) results2[0].get(PAYLOAD)).getValue();
		
		Document doc = (Document) o;
		System.out.println("Fetched: " + doc.getBaseURI());
		
	}
	
	@Test
	public final void testXPathAndUrlField() throws Exception {
		
		//using WebProxy and DatabaseProxy - see inside each to see how it's done
		
		WebWorker wp = new WebWorker(ts);
		wp.search("//book[author='Neal Stephenson']/title/text()");
		
		DatabaseWorker dp = new DatabaseWorker(ts);
		ITuple[] results = dp.handleSearch();
		
		assertEquals(results.length, 1);
		assertEquals(results[0].length(), 2);
		assertEquals( ((Field) results[0].get(VERB)).getValue(), "Search");
		
		SearchString ex3 = (SearchString) (( (Field) results[0].get(PAYLOAD))).getValue();
		
		System.out.println(ex3);
		assertEquals(ex3.getValue(), "//book[author='Neal Stephenson']/title/text()");
		
	}
	*/
	//@Test
	public final void testTwoFieldsInReverse() throws Exception {
		
		//same thing as the previous test, but matching happens the other way!
		
		//XPATH boilerplate
		XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = factory.newXPath();
		
		//create and populate the first tuple 
		ITuple t2 = new Tuple();
		IField f2 = new Field().setValue("Search"); //must be exact match
		t2.add(f2);
		
		XPathExpression expr = xpath
				.compile("//book[author='Neal Stephenson']/title/text()");		
		IField reg2 = new XMLField(expr);
		t2.add(reg2);
		
		//put it in the tuple space
		ts.out(t2);
		
		//create and populate second tuple
		ITuple t = new Tuple();
		IValuedField f = new Field().setValue("Search");
		t.add(f);

		//DOM factory boilerplate
		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
		domFactory.setNamespaceAware(true); // never forget this!
		DocumentBuilder builder = domFactory.newDocumentBuilder();
		
		Document doc = builder.parse("books.xml");
		IField reg = new XMLField(doc);
		
		t.add(reg);
		
		// see if they match
		ITuple[] results = null;
		try {
			results = ts.rdg(t);
		} catch (TupleSpaceException e) {}
		
		assertEquals(results.length, 1);
		assertEquals(results[0].length(), 2);
		assertEquals( ((Field) results[0].get(0)).getValue(), "Search");
		
		System.out.println(((Field) results[0].get(1)).getValue());
		//assertEquals( ((Field) results[0].get(1)).getValue(), expr);		

		
	}


}
