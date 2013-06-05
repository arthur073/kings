package polyester.tests;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import polyester.Worker;

import lights.Field;
import lights.Tuple;
import lights.extensions.FastTupleSpace;
import lights.interfaces.ITuple;
import lights.interfaces.ITupleSpace;
import lights.interfaces.TupleSpaceException;

public class TestFastTS {
	
	private class TestWorker extends Worker{

		private String name;
		private int run;
		private Random rnd;
		private List<ITuple> tups;
		long startime;
		public TestWorker(ITupleSpace ts, String n){
			super(ts);
			name= n;
			run = 0;
			rnd = new Random(System.currentTimeMillis()+name.hashCode());
			tups = new ArrayList<ITuple>();
			startime = System.currentTimeMillis();
		}
		
		@Override
		public void work() {
			try {
				Tuple t = new Tuple();
				t.add(new Field().setValue(name + run));
				run++;
				tups.add(t);
				long stampin, stampout;
				if(rnd.nextBoolean()) {
						
					space.out(t);
					stampout = System.currentTimeMillis()-startime;
					
				}
				else if (rnd.nextBoolean()){
					
					stampin = System.currentTimeMillis()-startime;
					space.rdg(new Tuple());
					stampout = System.currentTimeMillis()-startime;
					System.out.println(name+" read in" +stampin);
					System.out.println(name+" read out" +stampout);
				}
				else {
					stampin = System.currentTimeMillis()-startime;
					space.outg(tups.toArray(new ITuple[tups.size()]));
					stampout = System.currentTimeMillis()-startime;
					System.out.println(name+" wr m in" +stampin);
					System.out.println(name+" wr m out" +stampout);
				}
				
					

			} catch (TupleSpaceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();

			}
		}


	}

	private class TestWorker2 extends Worker{

		private String name;
		private int run;
		
		public TestWorker2(ITupleSpace ts, String n){
			super(ts);
			name= n;
			run = 0;	
		}
		
		@Override
		public void work() {
			try {
				Tuple t = new Tuple();
				t.add(new Field().setValue(name));
				run++;
				
				space.rd(t);
				
			} catch (TupleSpaceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();

			}
		}


	}
	private FastTupleSpace fts;
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		TestFastTS tester = new TestFastTS();
		tester.test();
		//System.exit(0);
	}
	
	public TestFastTS(){
		fts = new FastTupleSpace();
	}
	
	public void test() throws InterruptedException{
		
		TestWorker w1 = new TestWorker(fts, "bobby");
		TestWorker2 w2 = new TestWorker2(fts, "bobby4");
		TestWorker2 w3 = new TestWorker2(fts, "bobby4");
		//TestWorker w4 = new TestWorker(fts, "the return of the space cowboy");
		
		w1.start();
		w2.start();
		w3.start();
		//w4.start();
		
		/*Thread.sleep(10000);
		w1.stop();
		w2.stop();
		w3.stop();
		w4.stop();*/
		
		//System.out.println(fts.toString());
		
	}
	
	
	

}
