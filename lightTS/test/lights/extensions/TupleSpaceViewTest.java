/*
 * Copyright (C) 2005 costa

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
package lights.extensions;

import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import junit.framework.TestCase;
import lights.Field;
import lights.Tuple;
import lights.TupleSpace;
import lights.extensions.aggregation.Aggregator;
import lights.extensions.aggregation.TupleSpaceView;
import lights.interfaces.ITuple;
import lights.interfaces.ITupleSpace;
import lights.interfaces.IValuedField;
import lights.interfaces.TupleSpaceException;

public class TupleSpaceViewTest extends TestCase {

	private ITupleSpace tupleSpace;

	public static void main(String[] args) {
		junit.textui.TestRunner.run(TupleSpaceViewTest.class);
	}

	protected void setUp() throws Exception {
		super.setUp();
		tupleSpace = new TupleSpace();
		ITuple tuple;

		// Create and insert ten tuple whose format is <"Bella raga", integer
		// from 0 to 9>
		for (int i = 0; i < 10; i++) {
			tuple = new Tuple();
			tuple.add(new Field().setValue("Bella raga")).add(
					new Field().setValue(new Integer(i)));
			tupleSpace.out(tuple);
		}

		// Create and insert a tuple <"tutto rego ?", 1000>
		tuple = new Tuple();
		tuple.add(((IValuedField) new Field()).setValue("tutto rego ?")).add(
				((IValuedField) new Field()).setValue(new Integer(10000)));
		tupleSpace.out(tuple);
	}

	public void testRdp() {

		ITuple template = new Tuple().add(new Field().setValue("Bella raga"))
				.add(new Field().setType(Integer.class));

		// Create an Aggregator which estimates the average value among the
		// tuples matching templates
		Aggregator[] aggregators = { new Aggregator(template) {

			public ITuple[] aggregate(ITuple[] tuples) {
				ITuple returnTuples[] = new ITuple[1];

				float sum = 0;

				for (int i = 0; i < tuples.length; i++) {
					sum += ((Integer) ((IValuedField) tuples[i].get(1))
							.getValue()).intValue();
				}

				returnTuples[0] = new Tuple().add(new Field()
						.setValue(new Float(sum / tuples.length)));
				return returnTuples;
			}

		} };

		TupleSpaceView view = new TupleSpaceView(tupleSpace, aggregators);

		ITuple aggregateTemplate = new Tuple().add(new Field()
				.setType(Float.class));

		try {
			ITuple result = view.rdp(aggregateTemplate);
			assertEquals("<4.5>", result.toString());
		} catch (TupleSpaceException e) {
			fail();
		}

	}

	public void testRdg() {

		ITuple template = new Tuple().add(new Field().setValue("Bella raga"))
				.add(new Field().setType(Integer.class));

		// Create an aggregators which extract the lowest and the highest value
		// from a set of tuples matching templates
		Aggregator[] aggregators = { new Aggregator(template) {

			public ITuple[] aggregate(ITuple[] tuples) {
				ITuple returnTuples[] = new ITuple[2];

				int min = Integer.MAX_VALUE;
				int max = Integer.MIN_VALUE;
				int value;

				for (int i = 0; i < tuples.length; i++) {
					value = ((Integer) ((IValuedField) tuples[i].get(1))
							.getValue()).intValue();
					if (value > max) {
						max = value;
					}
					if (value < min) {
						min = value;
					}
				}

				returnTuples[0] = new Tuple().add(new Field()
						.setValue(new Integer(min)));

				returnTuples[1] = new Tuple().add(new Field()
						.setValue(new Integer(max)));

				return returnTuples;
			}

		} };

		TupleSpaceView view = new TupleSpaceView(tupleSpace, aggregators);

		ITuple aggregateTemplate = new Tuple().add(new Field()
				.setType(Integer.class));

		try {
			ITuple[] result = view.rdg(aggregateTemplate);
			assertEquals("<0>", result[0].toString());
			assertEquals("<9>", result[1].toString());
		} catch (TupleSpaceException e) {
			fail();
		}
	}

	/* This test comes directly from the example we put on the review paper */
	public void testOldExample() {
		// Generate the first class
		class AverageAggregator extends Aggregator {

			/**
			 * 
			 */
			public AverageAggregator() {
				super();
				// TODO Auto-generated constructor stub
			}

			/**
			 * @param template
			 */
			public AverageAggregator(ITuple template) {
				super(template);
				// TODO Auto-generated constructor stub
			}

			public ITuple[] aggregate(ITuple[] tuples) {
				int sum = 0;
				float average = 0;

				for (int i = 0; i < tuples.length; i++) {
					sum += ((Integer) ((Field) tuples[i].get(1)).getValue())
							.intValue();
				}

				average = (float) sum / tuples.length;

				String type = ((String) ((Field) tuples[0].get(2)).getValue());

				ITuple tuple = new Tuple().add(new Field().setValue("Room1"))
						.add(new Field().setValue(type)).add(
								new Field().setValue(new Float(average)));
				ITuple finals[] = { tuple };
				return finals;
			}

		}
		;

		class JoinAggregator extends Aggregator {

			/**
			 * 
			 */
			public JoinAggregator() {
				super();
				// TODO Auto-generated constructor stub
			}

			/**
			 * @param template
			 */
			public JoinAggregator(ITuple template) {
				super(template);
				// TODO Auto-generated constructor stub
			}

			public ITuple[] aggregate(ITuple[] tuples) {
				List temperatureTuples = new LinkedList();
				List lightTuples = new LinkedList();
				Vector viewTuples = new Vector(10);

				String type;
				for (int i = 0; i < tuples.length; i++) {
					type = (String) ((Field) tuples[i].get(2)).getValue();
					if (type.equals("Temperature")) {
						temperatureTuples.add(tuples[i]);
					} else {
						lightTuples.add(tuples[i]);
					}
				}

				ITuple tuple;
				for (int i = 0; i < temperatureTuples.size(); i++) {
					ITuple temperature = (ITuple) temperatureTuples.get(i);
					ITuple light = (ITuple) lightTuples.get(i);
					tuple = new Tuple().add(temperature.get(0)).add(
							temperature.get(1)).add(light.get(1)).add(
							temperature.get(3));
					viewTuples.add(tuple);
				}

				if (viewTuples.size() == 0) {
					return null;
				} else {
					ITuple[] returnTuples = new ITuple[viewTuples.size()];
					viewTuples.copyInto(returnTuples);
					return returnTuples;
				}
			}

		}
		;

		// Creating a new tuple space
		ITupleSpace tupleSpace = new TupleSpace();

		// Generating TEMPERATURE values and insert them into the tuplespace
		ITuple tuple;
		for (int i = 0; i < 10; i++) {
			tuple = new Tuple().add(new Field().setValue(new String("Room1")))
					.add(new Field().setValue(new Integer(i))).add(
							new Field().setValue(new String("Temperature")))
					.add(new Field().setValue(new Float(i)));
			try {
				tupleSpace.out(tuple);
			} catch (TupleSpaceException e) {
				fail("TupleSpaceException thrown");
			}
		}

		// Generating LIGHT values and insert them into the tuplespace
		for (int i = 0; i > -10; i--) {
			tuple = new Tuple().add(new Field().setValue(new String("Room1")))
					.add(new Field().setValue(new Integer(i))).add(
							new Field().setValue(new String("Light"))).add(
							new Field().setValue(new Float(i)));
			try {
				tupleSpace.out(tuple);
			} catch (TupleSpaceException e) {
				fail("TupleSpaceException thrown");
			}
		}

		ITuple temperatureTemplate = new Tuple().add(
				new Field().setValue(new String("Room1"))).add(
				new Field().setType(Integer.class)).add(
				new Field().setValue(new String("Temperature"))).add(
				new Field().setType(Float.class));

		ITuple lightTemplate = new Tuple().add(
				new Field().setValue(new String("Room1"))).add(
				new Field().setType(Integer.class)).add(
				new Field().setValue(new String("Light"))).add(
				new Field().setType(Float.class));

		ITuple template = new Tuple().add(
				new Field().setValue(new String("Room1"))).add(
				new Field().setType(Integer.class)).add(
				new Field().setType(String.class)).add(
				new Field().setType(Float.class));

		// Creating instances of Aggregator
		AverageAggregator temperatureAverageAggregator = new AverageAggregator(
				temperatureTemplate);

		AverageAggregator lightAverageAggregator = new AverageAggregator(
				lightTemplate);

		JoinAggregator joinAggregator = new JoinAggregator(template);

		Aggregator[] aggregators = { temperatureAverageAggregator,
				lightAverageAggregator, joinAggregator };

		TupleSpaceView tupleSpaceView = new TupleSpaceView(tupleSpace,
				aggregators);

		// Retrieving TEMPERATURE average value
		ITuple averageTemplate = new Tuple().add(new Field().setValue("Room1"))
				.add(new Field().setValue(new String("Temperature"))).add(
						new Field().setType(Float.class));

		try {
			tuple = tupleSpaceView.rdp(averageTemplate);
			assertEquals("<Room1, Temperature, 4.5>", tuple.toString());
		} catch (TupleSpaceException e) {
			fail();
		}

		// Retrieving LIGHT average value
		averageTemplate = new Tuple().add(new Field().setValue("Room1")).add(
				new Field().setValue(new String("Light"))).add(
				new Field().setType(Float.class));

		try {
			tuple = tupleSpaceView.rdp(averageTemplate);
			assertEquals("<Room1, Light, -4.5>", tuple.toString());
		} catch (TupleSpaceException e) {
			fail();
		}

		// Retrieving ALL average values
		averageTemplate = new Tuple().add(new Field().setValue("Room1")).add(
				new Field().setType(String.class)).add(
				new Field().setType(Float.class));

		try {
			ITuple[] tuples = tupleSpaceView.rdg(averageTemplate);
			assertEquals("<Room1, Temperature, 4.5>", tuples[0].toString());
			assertEquals("<Room1, Light, -4.5>", tuples[1].toString());
		} catch (TupleSpaceException e) {
			fail();
		}

		// Retrieving first JOIN tuple
		ITuple joinTemplate = new Tuple().add(new Field().setValue("Room1"))
				.add(new Field().setType(Integer.class)).add(
						new Field().setType(Integer.class)).add(
						new Field().setType(Float.class));

		try {
			tuple = tupleSpaceView.rdp(joinTemplate);
			assertEquals("<Room1, 0, 0, 0.0>", tuple.toString());
		} catch (TupleSpaceException e) {
			fail();
		}

	}

}
