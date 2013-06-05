/*
 * Project : lights.extensions.fuzzy.types Created on Dec 19, 2003
 */
package lights.extensions.fuzzy;

import java.util.Iterator;

import junit.framework.TestCase;
import lights.extensions.fuzzy.FloatFuzzyType;
import lights.extensions.fuzzy.FuzzyTerm;
import lights.extensions.fuzzy.membershipfunctions.ConstantFunction;
import lights.extensions.fuzzy.membershipfunctions.PiFunction;
import lights.extensions.fuzzy.membershipfunctions.SFunction;
import lights.extensions.fuzzy.membershipfunctions.TriangleFunction;
import lights.extensions.fuzzy.membershipfunctions.ZFunction;

/**
 * @author balzarot
 */
public class FloatFuzzyVariableTest extends TestCase {

	public FloatFuzzyVariableTest(String name) {
		super(name);
	}

	public void testConstructors() {

		FloatFuzzyType floatFuzzyType = new FloatFuzzyType("Temp", -100, 100);
		assertEquals("name", "Temp", floatFuzzyType.getName());
		assertEquals("max", 100, floatFuzzyType.getMax(), 0.001f);
		assertEquals("min", -100, floatFuzzyType.getMin(), 0.001f);

		assertEquals("terms number", 0, floatFuzzyType.getTermsNumber());

		Iterator iterator = floatFuzzyType.getTerms();
		assertFalse("empty set", iterator.hasNext());

		floatFuzzyType = new FloatFuzzyType("", 0, 10, "Kelvin");
		assertEquals("units", "Kelvin", floatFuzzyType.getUnits());

		ConstantFunction cmf1 = new ConstantFunction(0.2f);
		ConstantFunction cmf2 = new ConstantFunction(0.2f);

		floatFuzzyType.addTerm(new FuzzyTerm("Term1", cmf1));
		floatFuzzyType.addTerm(new FuzzyTerm("Term2", cmf2));

		assertEquals("getMembershipFuntion", cmf1, floatFuzzyType.getTerm(
				"Term1").getMembershipFunction());
		iterator = floatFuzzyType.getTerms();
		assertEquals("term1", "Term1", ((FuzzyTerm) iterator.next()).getTerm());
		assertEquals("term2", "Term2", ((FuzzyTerm) iterator.next()).getTerm());
		assertFalse("terms number", iterator.hasNext());
	}

	public void testAddTerm() {
		FloatFuzzyType floatFuzzyType = new FloatFuzzyType("Temp", 0, 1000,
				"Kelvin");

		ConstantFunction constant = new ConstantFunction(0.2f);
		TriangleFunction triangle = new TriangleFunction(50.0f, 40.0f);

		floatFuzzyType.addTerm(new FuzzyTerm("Term1", constant));
		floatFuzzyType.addTerm(new FuzzyTerm("Term2", triangle));

		assertEquals("terms number", 2, floatFuzzyType.getTermsNumber());

		assertEquals("p1", 0.2f, floatFuzzyType.getMembershipValue("Term1",
				100.0f), 0.001f);
		assertEquals("p2", 1.0f, floatFuzzyType.getMembershipValue("Term2",
				50.0f), 0.001f);
		assertEquals("p3", 0.0f, floatFuzzyType.getMembershipValue("Term2",
				100.0f), 0.001f);

		assertEquals("p3", "Term1", floatFuzzyType.getTerm(100.0f).getTerm());
		assertEquals("p3", "Term2", floatFuzzyType.getTerm(60.0f).getTerm());
	}

	public void testTrianglePartition() {
		FloatFuzzyType floatFuzzyType = new FloatFuzzyType("Temp", 0, 1000, "K");
		String[] terms = new String[5];
		terms[0] = "Very Low";
		terms[1] = "Low";
		terms[2] = "Medium";
		terms[3] = "High";
		terms[4] = "Very High";
		floatFuzzyType.generateTrianglePartition(terms);

		assertEquals("terms number", 5, floatFuzzyType.getTermsNumber());

		TriangleFunction tf;
		tf = (TriangleFunction) floatFuzzyType.getTerm(terms[0])
				.getMembershipFunction();
		assertEquals("T1 - Xtop", 0f, tf.getXtop(), 0.01f);
		assertEquals("T1 - Xmax", 250f, tf.getXmax(), 0.01f);
		assertEquals("T1 - Xmin", 0f, tf.getXmin(), 0.01f);
		assertEquals("T1 - Ymax", 1f, tf.getYmax(), 0.01f);
		assertEquals("T1 - Ymin", 0f, tf.getYmin(), 0.01f);

		tf = (TriangleFunction) floatFuzzyType.getTerm(terms[1])
				.getMembershipFunction();
		assertEquals("T2 - Xtop", 250f, tf.getXtop(), 0.01f);
		assertEquals("T2 - Xmax", 500f, tf.getXmax(), 0.01f);
		assertEquals("T2 - Xmin", 0f, tf.getXmin(), 0.01f);

		tf = (TriangleFunction) floatFuzzyType.getTerm(terms[2])
				.getMembershipFunction();
		assertEquals("T3 - Xtop", 500f, tf.getXtop(), 0.01f);
		assertEquals("T3 - Xmax", 750f, tf.getXmax(), 0.01f);
		assertEquals("T3 - Xmin", 250f, tf.getXmin(), 0.01f);

		tf = (TriangleFunction) floatFuzzyType.getTerm(terms[3])
				.getMembershipFunction();
		assertEquals("T4 - Xtop", 750f, tf.getXtop(), 0.01f);
		assertEquals("T4 - Xmax", 1000f, tf.getXmax(), 0.01f);
		assertEquals("T4 - Xmin", 500f, tf.getXmin(), 0.01f);

		tf = (TriangleFunction) floatFuzzyType.getTerm(terms[4])
				.getMembershipFunction();
		assertEquals("T5 - Xtop", 1000f, tf.getXtop(), 0.01f);
		assertEquals("T5 - Xmax", 1000f, tf.getXmax(), 0.01f);
		assertEquals("T5 - Xmin", 750f, tf.getXmin(), 0.01f);

	}

	public void testPiPartition() {
		FloatFuzzyType floatFuzzyType = new FloatFuzzyType("Temp", 0, 1000, "K");
		String[] terms = new String[5];
		terms[0] = "Very Low";
		terms[1] = "Low";
		terms[2] = "Medium";
		terms[3] = "High";
		terms[4] = "Very High";
		floatFuzzyType.generatePiPartition(terms);

		assertEquals("terms number", 5, floatFuzzyType.getTermsNumber());

		SFunction sf = (SFunction) floatFuzzyType.getTerm(terms[0])
				.getMembershipFunction();
		assertEquals("S - center", 125f, sf.getCenter(), 0.01f);
		assertEquals("S - delta", 125f, sf.getDelta(), 0.01f);

		PiFunction pf = (PiFunction) floatFuzzyType.getTerm(terms[1])
				.getMembershipFunction();
		assertEquals("PI.1 - center", 250f, pf.getCenter(), 0.01f);
		assertEquals("PI.1 - delta", 250f, pf.getDelta(), 0.01f);

		pf = (PiFunction) floatFuzzyType.getTerm(terms[2])
				.getMembershipFunction();
		assertEquals("PI.2 - center", 500f, pf.getCenter(), 0.01f);
		assertEquals("PI.2 - delta", 250f, pf.getDelta(), 0.01f);

		pf = (PiFunction) floatFuzzyType.getTerm(terms[3])
				.getMembershipFunction();
		assertEquals("PI.3 - center", 750f, pf.getCenter(), 0.01f);
		assertEquals("PI.3 - delta", 250f, pf.getDelta(), 0.01f);

		ZFunction zf = (ZFunction) floatFuzzyType.getTerm(terms[4])
				.getMembershipFunction();
		assertEquals("Z - center", 875f, zf.getCenter(), 0.01f);
		assertEquals("Z - delta", 125f, zf.getDelta(), 0.01f);
	}

	public void testGreater1() {
		FloatFuzzyType floatFuzzyType = new FloatFuzzyType("Temp", 0, 1000, "K");

		assertEquals(" 90 >> 100", 0.0f, floatFuzzyType.isGreater(90f, 100f),
				0.01f);
		assertEquals("100 >> 100", 0.0f, floatFuzzyType.isGreater(100f, 100f),
				0.01f);
		assertEquals("110 >> 100", 0.1f, floatFuzzyType.isGreater(110f, 100f),
				0.01f);
		assertEquals("150 >> 100", 0.5f, floatFuzzyType.isGreater(150f, 100f),
				0.01f);
		assertEquals("200 >> 100", 1.0f, floatFuzzyType.isGreater(200f, 100f),
				0.01f);
		assertEquals("350 >> 300", 0.5f, floatFuzzyType.isGreater(350f, 300f),
				0.01f);
	}

	public void testGreater2() {
		FloatFuzzyType floatFuzzyType = new FloatFuzzyType("Temp", 0, 1000, "K");
		floatFuzzyType.setGreaterFunction(new SFunction(50f, 50f));
		assertEquals("200 >> 200", 0.0f, floatFuzzyType.isGreater(200f, 200f),
				0.01f);
		assertEquals("350 >> 300", 0.5f, floatFuzzyType.isGreater(350f, 300f),
				0.01f);
		assertEquals("360 >> 300", 0.68f, floatFuzzyType.isGreater(360f, 300f),
				0.01f);
	}

	public void testSmaller1() {
		FloatFuzzyType floatFuzzyType = new FloatFuzzyType("Temp", 0, 1000, "K");

		assertEquals("110 << 500", 1.0f, floatFuzzyType.isSmaller(110f, 500f),
				0.01f);
		assertEquals("400 << 500", 1.0f, floatFuzzyType.isSmaller(400f, 500f),
				0.01f);
		assertEquals("450 << 500", 0.5f, floatFuzzyType.isSmaller(450f, 500f),
				0.01f);
		assertEquals("500 << 500", 0.0f, floatFuzzyType.isSmaller(500f, 500f),
				0.01f);
		assertEquals("520 << 500", 0.0f, floatFuzzyType.isSmaller(520f, 500f),
				0.01f);
		assertEquals(" 70 << 100", 0.3f, floatFuzzyType.isSmaller(70f, 100f),
				0.01f);
	}

	public void testSmaller2() {
		FloatFuzzyType floatFuzzyType = new FloatFuzzyType("Temp", 0, 1000, "K");
		floatFuzzyType.setSmallerFunction(new ZFunction(-50f, 50f));
		assertEquals(" 50 << 100", 0.5f, floatFuzzyType.isSmaller(50f, 100f),
				0.01f);
		assertEquals(" 90 << 100", 0.02f, floatFuzzyType.isSmaller(90f, 100f),
				0.01f);
		assertEquals(" 10 << 100", 0.98f, floatFuzzyType.isSmaller(10f, 100f),
				0.01f);
	}

	public void testNearly1() {
		FloatFuzzyType floatFuzzyType = new FloatFuzzyType("Temp", 0, 1000, "K");

		assertEquals(" 90 ~ 100", 0.98f, floatFuzzyType.isNearly(90f, 100f),
				0.01f);
		assertEquals("500 ~ 500", 1.0f, floatFuzzyType.isNearly(500f, 500f),
				0.01f);
		assertEquals("510 ~ 500", 0.98f, floatFuzzyType.isNearly(510f, 500f),
				0.01f);
		assertEquals("450 ~ 500", 0.5f, floatFuzzyType.isNearly(450f, 500f),
				0.01f);
		assertEquals("600 ~ 100", 0.0f, floatFuzzyType.isNearly(600f, 100f),
				0.01f);
		assertEquals(" 35 ~  40", 0.99f, floatFuzzyType.isNearly(35f, 40f),
				0.01f);
	}

	public void testNearly2() {
		FloatFuzzyType floatFuzzyType = new FloatFuzzyType("Temp", 0, 1000, "K");
		floatFuzzyType.setNearlyFunction(new PiFunction(0f, 200f));
		assertEquals("500 ~ 400", 0.5f, floatFuzzyType.isNearly(500f, 400f),
				0.01f);
		assertEquals("300 ~ 400", 0.5f, floatFuzzyType.isNearly(300f, 400f),
				0.01f);
		assertEquals("100 ~ 150", 0.875f, floatFuzzyType.isNearly(100f, 150f),
				0.01f);
	}
}

