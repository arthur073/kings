/*
 * Project : lights.extensions.fuzzy.types
 * Created on Nov 18, 2003
 */
package lights.extensions.fuzzy.membershipfunctions;

import junit.framework.*;
import lights.extensions.fuzzy.membershipfunctions.TriangleFunction;

/**
 * @author balzarot
 */
public class TriangleFunctionTest extends TestCase {

	public TriangleFunctionTest(String name) {
		super(name);
	}

	public void testConstructors() {
		TriangleFunction tf = new TriangleFunction(10,8);
		assertEquals("Xmax", 14f, tf.getXmax(), 0.01f);
		assertEquals("Xmin",  6f, tf.getXmin(), 0.01f);
		assertEquals("Xtop", 10f, tf.getXtop(), 0.01f);
		assertEquals("Ymax",  1f, tf.getYmax(), 0.01f);
		assertEquals("Ymin",  0f, tf.getYmin(), 0.01f);

		tf = new TriangleFunction(10,5,5);
		assertEquals("Xmax", 15, tf.getXmax(), 0.01);
		assertEquals("Xmin",  5, tf.getXmin(), 0.01);
		assertEquals("Xtop", 10, tf.getXtop(), 0.01);
		assertEquals("Ymax",  1, tf.getYmax(), 0.01);
		assertEquals("Ymin",  0, tf.getYmin(), 0.01);

		tf = new TriangleFunction(10.0f, 3.0f, 3.0f, 0.8f, 0.2f);
		assertEquals("Xmax", 13, tf.getXmax(), 0.01);
		assertEquals("Xmin",  7, tf.getXmin(), 0.01);
		assertEquals("Xtop", 10, tf.getXtop(), 0.01);
		assertEquals("Ymax",0.8, tf.getYmax(), 0.01);
		assertEquals("Ymin",0.2, tf.getYmin(), 0.01);
	}

	public void testConstructorsExceptions() {

		TriangleFunction tf;

		try{
			tf = new TriangleFunction(10,-1);
			fail(" (10,-1) without exception ");
		}catch(Exception e){}

		try{
			tf = new TriangleFunction(10,5,-1);
			fail(" (10,5,-1) without exception ");
		}catch(Exception e){}

		try{
			tf = new TriangleFunction(10,-1,5);
			fail(" (10,-1,5) without exception ");
		}catch(Exception e){}

		try{
			tf = new TriangleFunction(10,1,5,5,0);
			fail(" (10,1,5,5,0) without exception ");
		}catch(Exception e){}

		try{
			tf = new TriangleFunction(10,1,1,1,2);
			fail(" (10,1,1,1,2) without exception ");
		}catch(Exception e){}

		try{
			tf = new TriangleFunction(10,1,1,0.7f,0.9f);
			fail(" (10,1,1,0.7f,0.9f) without exception ");
		}catch(Exception e){}

	}

	public void testSimpleTriangle()
	{
		TriangleFunction tf = new TriangleFunction(10.0f, 3.0f, 5.0f, 0.8f, 0.2f);
		assertEquals("p1", 0.2f, tf.getMembershipValue(7f),    0.01f);
		assertEquals("p2", 0.8f, tf.getMembershipValue(10f),   0.01f);
		assertEquals("p3", 0.2f, tf.getMembershipValue(15f),   0.01f);
		assertEquals("p4", 0.5f, tf.getMembershipValue(8.5f),  0.01f);
		assertEquals("p5", 0.5f, tf.getMembershipValue(12.5f), 0.01f);
		assertEquals("p6", 0.0f, tf.getMembershipValue(-1f),   0.01f);
		assertEquals("p7", 0.0f, tf.getMembershipValue(24f),   0.01f);
	}

	public void testStrangeTriangle()
	{
		TriangleFunction tf;

		tf = new TriangleFunction(10.0f, 3.0f, 0f, 0.9f, 0.2f);
		assertEquals("triangle1", 0.9f, tf.getMembershipValue(10f),  0.01f);

		tf = new TriangleFunction(10.0f, 0f, 3.0f, 0.9f, 0.2f);
		assertEquals("triangle2", 0.9f, tf.getMembershipValue(10f),  0.01f);

		tf = new TriangleFunction(10.0f, 0f, 0f, 0.9f, 0.2f);
		assertEquals("line1", 0.9f, tf.getMembershipValue(10f),  0.01f);

		tf = new TriangleFunction(10.0f, 5f, 5f, 0.5f, 0.5f);
		assertEquals("line2", 0.5f, tf.getMembershipValue(12f),  0.01f);
	}

}
