/*
 * Copyright (C) 2004 costa

 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation; either version 2
 of the License, or (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */
package lights.adapters;

import junit.framework.TestCase;
import lights.interfaces.ITuple;
import lights.interfaces.TupleSpaceException;

/**
 * @author costa
 * 
 * TODO Insert comment
 */
public class AdapterTest extends TestCase {

	TupleSpace ts;

	Tuple t1;
	
	public AdapterTest() {
		String name = "lights.adapters.tspaces.TupleSpaceFactory";
	    try {
	      TupleSpaceFactory.setFactory(name);
	    } catch(ClassNotFoundException e) {
	      System.out.println("Unable to load the class " + name);
	    }
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(AdapterTest.class);
	}

	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		//      Creating tuple space...
		ts = new TupleSpace();

		t1 = new Tuple();
		t1.add(new Field().setValue(new Float(50))).add(
				new Field().setValue(new Float(150))).add(
				new Field().setValue(new Float(100)));

		ts.out(t1);
	}

	
	// FIXME: capire perch? non funza...
	final public void testTupleRetrieval() {
		ITuple template = new Tuple();
		template.add(new Field().setType(Float.class)).add(
				new Field().setType(Float.class)).add(
				new Field().setType(Float.class));

		ITuple result = null;
		try {
			result = ts.rdp(template);
		} catch (TupleSpaceException e) {
			fail();
		}
		assertNotNull("Tupla non trovata", result);
	}

//	final public void testRangeField() {
//		ITuple templates = new Tuple().add(new Field().setType(Float.class))
//				.add(new RangeField().setUpperBound(new Float(200), true));
//		templates.add(new RangeField().setLowerBound(new Float(75), true));
//
//		ITuple result = null;
//		try {
//			result = ts.rdp(templates);
//		} catch (TupleSpaceException e) {
//			fail();
//		}
//		assertNotNull("Tupla non trovata", result);
//
//		// Try to find a non-existing tuple
//		templates = new Tuple().add(new Field().setType(Float.class)).add(
//				new RangeField().setUpperBound(new Float(100), true));
//		templates.add(new RangeField().setLowerBound(new Float(75), true));
//
//		result = null;
//		try {
//			result = ts.rdp(templates);
//		} catch (TupleSpaceException e) {
//			fail();
//		}
//		assertNull(result);
//
//		// RangeField with no bounds
//		templates = new Tuple().add(new Field().setType(Float.class)).add(
//				new Field().setType(Float.class));
//		templates.add(new RangeField().setType(Float.class));
//
//		result = null;
//		try {
//			result = ts.rdp(templates);
//			assertNotNull(result);
//		} catch (TupleSpaceException e) {
//			fail();
//		}
//	}
//
//	final public void testSubtypeField() {
//		ITuple templates = new Tuple().add(new Field().setType(Float.class))
//				.add(new Field().setType(Float.class));
//		templates.add(new SubtypeField().setType(Object.class));
//
//		ITuple result = null;
//		try {
//			result = ts.rdp(templates);
//		} catch (TupleSpaceException e) {
//			fail();
//		}
//		assertNotNull("Tupla non trovata", result);
//	}
//
//	final public void testRegexField() {
//		ITuple tuple = new Tuple().add(new Field().setValue("paolone"));
//
//		try {
//			ts.out(tuple);
//		} catch (TupleSpaceException e1) {
//			fail();
//		}
//
//		ITuple templates = new Tuple();
//		templates.add(new RegexField("paolo.*"));
//
//		ITuple result = null;
//		try {
//			result = ts.rdp(templates);
//		} catch (TupleSpaceException e) {
//			fail();
//		}
//		assertNotNull("Tupla non trovata", result);
//	}
//
//	final public void testPrefixMatcher() {
//		ITuple templates = new PrefixTuple().add(new Field()
//				.setType(Float.class));
//
//		ITuple result = null;
//		try {
//			result = ts.rdp(templates);
//		} catch (TupleSpaceException e) {
//			fail();
//		}
//		assertNotNull("Tupla non trovata", result);
//
//	}
//
//	final public void testVirtualize() throws TupleSpaceException { // //
//		Tuple tuple = new Tuple();
//		tuple.add(new Field().setValue(new Float(25))).add(
//				new Field().setValue(new Float(25)));
//		ts.out(tuple);
//
//		ITuple templates = new Tuple().add(new Field().setType(Float.class));
//		ITuple virtualTemplate = new VirtualTuple(templates) {
//
//			public ITuple virtualize(ITuple tuple) {
//				ITuple t = new Tuple();
//				t.add(new Field().setValue(new Float(50)));
//				return t;
//			}
//		};
//
//		virtualTemplate.add(new Field().setType(Float.class)).add(
//				new Field().setType(Float.class));
//
//		ITuple tupleFound = ts.rdp(virtualTemplate);
//		try {
//			assertEquals(tupleFound.toString(), tuple.toString());
//		} catch (NullPointerException e) {
//			fail("The tuple has not been found");
//		}
//	}

}