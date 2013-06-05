package polyester;

/**
 * Receives responses from search dispatched to the peer network.
 * 
 * @author Neal Arthorne
 * @version 1.0
 */
public interface TupleListener {

    /**
     * Receives zero or more search responses.
     * 
     * @param responses responses to a search query
     */
    public void receiveTuple(TupleEvent e);
    
    //public void receiveSearchResponse(SearchResponse SingleResponse);
}