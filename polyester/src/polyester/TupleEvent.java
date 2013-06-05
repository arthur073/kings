package polyester;

import java.util.List;

import lights.interfaces.ITuple;

public class TupleEvent {
	
	private List<ITuple> eventContents;
	
	public TupleEvent(List<ITuple> l){
		eventContents = l;
	}

	public List<ITuple> getContents(){
		return eventContents;
	}
}
