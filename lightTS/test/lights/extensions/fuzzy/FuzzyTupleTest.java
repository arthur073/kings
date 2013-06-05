/*
 * Created on Jan 20, 2004
 * 
 * To change the templates for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package lights.extensions.fuzzy;

import junit.framework.TestCase;
import lights.Field;
import lights.Tuple;
import lights.TupleSpace;
import lights.extensions.aggregation.VirtualTuple;
import lights.interfaces.ITuple;
import lights.interfaces.ITupleSpace;
import lights.interfaces.TupleSpaceException;

/**
 * @author Paolo Costa <paolo.costa@polimi.it>
 * 
 * To change the templates for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class FuzzyTupleTest extends TestCase {

	private FloatFuzzyType temperature;

	private FloatFuzzyType money;

	private FloatFuzzyType distance;

	private ITupleSpace ts;

	private ITuple t1;

	/**
	 * Constructor for FuzzyTupleMatcherTest.
	 * 
	 * @param arg0
	 */
	public FuzzyTupleTest(String arg0) {
		super(arg0);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(FuzzyTupleTest.class);
	}

	protected void setUp() throws Exception {
		super.setUp();
		temperature = new FloatFuzzyType("Temperatura", -100, 100);
		String terms[] = { "Basso", "Medio", "Alto" };
		temperature.generateTrianglePartition(terms);

		money = new FloatFuzzyType("Soldi", 0, 200);
		terms[2] = "Caro";
		terms[1] = "Medio";
		terms[0] = "Buon prezzo";
		money.generatePiPartition(terms);

		distance = new FloatFuzzyType("Distanza", 0, 1000);
		terms[2] = "Lontano";
		terms[1] = "Medio";
		terms[0] = "Vicino";
		distance.generateTrianglePartition(terms);

		// Creating tuple space...
		ts = new TupleSpace();

		t1 = new Tuple();
		t1.add(new Field().setValue(new Float(50))).add(
				new Field().setValue(new Float(150))).add(
				new Field().setValue(new Float(100)));

		ts.out(t1);
	}

	final public void testMatches() {

		try {

			ITuple template = new FuzzyTuple().add(
					new FuzzyField("Temperatura").setFuzzyType(temperature)).add(
					new FuzzyField("Soldi").setFuzzyType(money)).add(
					new FuzzyField("Distanza").setFuzzyType(distance));

			((FuzzyTuple) template)
					.setMatchingExpression("((Temperatura IS Alto) && (Soldi IS Caro)) -Maximum- ((Distanza == 100) && (Temperatura > 20))");
			((FuzzyTuple) template).setThreshold(0.2f);

			ITuple tuple = ts.rdp(template);
			if (tuple != null) {
				assertEquals(tuple.toString(), t1.toString());
			} else {
				fail("Tuple not found");
			}
		} catch (TupleSpaceException e) {
			fail();
		}
	}

	final public void testVirtualize() throws TupleSpaceException { // //
		Tuple tuple = new Tuple();
		tuple.add(new Field().setValue(new Float(25))).add(
				new Field().setValue(new Float(25)));
		ts.out(tuple);

		ITuple template = new FuzzyTuple().add(
				new FuzzyField("Temperatura").setFuzzyType(temperature));
		((FuzzyTuple) template).setMatchingExpression("Temperatura IS Alto");
		((FuzzyTuple) template).setThreshold(0.8f);
		ITuple virtualTemplate = new VirtualTuple(template) {

			public ITuple virtualize(ITuple tuple) {
				ITuple t = new Tuple();
				t.add(new Field().setValue(new Float(80)));
				return t;
			}
		};

		virtualTemplate.add(new Field().setType(Float.class)).add(
				new Field().setType(Float.class));

		ITuple tupleFound = ts.rdp(virtualTemplate);
		try {
			assertEquals(tupleFound.toString(), tuple.toString());
		} catch (NullPointerException e) {
			fail("Tuple has not been found");
		}
	}
}