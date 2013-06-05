/*
 * Project : lights.extensions.fuzzy.types
 * Created on Nov 18, 2003
 */
package lights.extensions.fuzzy.membershipfunctions;

import junit.framework.*;
import lights.extensions.fuzzy.membershipfunctions.TrapezFunction;

/**
 * @author balzarot
 */
public class TrapezFunctionTest extends TestCase {

	public TrapezFunctionTest(String name) {
		super(name);
	}

	public void testConstructors() {
		TrapezFunction tf = new TrapezFunction(10,12,2,3);
		assertEquals("x1",    10f, tf.getX1(),    0.01f);
		assertEquals("x2",    12f, tf.getX2(),    0.01f);
		assertEquals("alpha",  2f, tf.getAlpha(), 0.01f);
		assertEquals("beta",   3f, tf.getBeta(),  0.01f);
		assertEquals("Ymax",   1f, tf.getYmax(),  0.01f);
		assertEquals("Ymin",   0f, tf.getYmin(),  0.01f);

		tf = new TrapezFunction(5,15,6,33,0.95f,0.1f);
		assertEquals("x1",     5.00f, tf.getX1(),    0.01f);
		assertEquals("x2",    15.00f, tf.getX2(),    0.01f);
		assertEquals("alpha",  6.00f, tf.getAlpha(), 0.01f);
		assertEquals("beta",  33.00f, tf.getBeta(),  0.01f);
		assertEquals("Ymax",   0.95f, tf.getYmax(),  0.01f);
		assertEquals("Ymin",    0.1f, tf.getYmin(),  0.01f);
	}

	public void testConstructorsExceptions() {

		TrapezFunction tf;

		try{
			tf = new TrapezFunction(1,10,-1,2);
			fail(" alpha=-1 without exception ");
		}catch(Exception e){}

		try{
			tf = new TrapezFunction(0,5,1,-3);
			fail(" beta=-3 without exception ");
		}catch(Exception e){}

		try{
			tf = new TrapezFunction(10,1,5,5);
			fail(" x1 > x2 without exception ");
		}catch(Exception e){}

		try{
			tf = new TrapezFunction(1,2,1,1,2,0);
			fail(" ymax > 1 without exception ");
		}catch(Exception e){}

		try{
			tf = new TrapezFunction(1,2,1,1,1,-1);
			fail(" ymin < 0 without exception ");
		}catch(Exception e){}

		try{
			tf = new TrapezFunction(1.0f,2.0f,1.0f,1.1f,0.2f,0.4f);
			fail(" ymin > ymax without exception ");
		}catch(Exception e){}

	}

	public void testSimpleTrapez()
	{
		TrapezFunction tf = new TrapezFunction(10.0f, 13.0f, 5.0f, 3.0f, 0.8f, 0.2f);
		assertEquals("p1", 0.0f, tf.getMembershipValue( 0.0f), 0.01f);
		assertEquals("p2", 0.2f, tf.getMembershipValue( 5.0f), 0.01f);
		assertEquals("p3", 0.5f, tf.getMembershipValue( 7.5f), 0.01f);
		assertEquals("p4", 0.8f, tf.getMembershipValue(10.0f), 0.01f);
		assertEquals("p5", 0.8f, tf.getMembershipValue(12.5f), 0.01f);
		assertEquals("p6", 0.8f, tf.getMembershipValue(13.0f), 0.01f);
		assertEquals("p7", 0.6f, tf.getMembershipValue(14.0f), 0.01f);
		assertEquals("p8", 0.2f, tf.getMembershipValue(16.0f), 0.01f);
		assertEquals("p9", 0.0f, tf.getMembershipValue(18.0f), 0.01f);
	}

	public void testStrangeTrapez()
	{
		TrapezFunction tf;

		tf = new TrapezFunction(10.0f, 13.0f, 0.0f, 0.0f, 0.85f, 0.2f);
		assertEquals("rectangle", 0.85f, tf.getMembershipValue(10f), 0.01f);
		assertEquals("rectangle", 0.85f, tf.getMembershipValue(13f), 0.01f);

		tf = new TrapezFunction(10.0f, 10.0f, 3.0f, 3.0f, 0.85f, 0.2f);
		assertEquals("triangle1", 0.85f, tf.getMembershipValue(10f), 0.01f);

		tf = new TrapezFunction(10.0f, 10.0f, 0.0f, 2.0f, 0.9f, 0.2f);
		assertEquals("triangle2", 0.9f, tf.getMembershipValue(10f), 0.01f);

		tf = new TrapezFunction(10.0f, 10.0f, 2.0f, 0.0f, 0.9f, 0.2f);
		assertEquals("triangle3", 0.9f, tf.getMembershipValue(10f), 0.01f);

		tf = new TrapezFunction(5.0f, 5.0f, 0.0f, 0.0f, 0.5f, 0.1f);
		assertEquals("line1", 0.5f, tf.getMembershipValue(5f), 0.01f);

		tf = new TrapezFunction(10.0f, 15.0f, 5.0f, 5.0f, 0.5f, 0.5f);
		assertEquals("line2", 0.5f, tf.getMembershipValue(8f),  0.01f);
		assertEquals("line2", 0.5f, tf.getMembershipValue(12f), 0.01f);
		assertEquals("line2", 0.5f, tf.getMembershipValue(19f), 0.01f);
	}

	
}
