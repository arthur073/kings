/**
 * Encapsulation of a chat message item
 *
 * @version November 29, 1999
 * @author Bryan D. Payne
 */

public class LChatMessage implements java.io.Serializable{
    
    /**
     * Create a chat message from the given info
     *
     * @param id The userid for this message
     * @param message The message
     */
    public LChatMessage (String id, String message, boolean boldOn,
			 boolean italicizeOn, boolean underlineOn,
			 java.awt.Color color){
	this.id = id;
	this.message = message;
	this.boldOn = boldOn;
	this.italicizeOn = italicizeOn;
	this.underlineOn = underlineOn;
	this.color = color;
    }

    /**
     * Accessor for the ID
     *
     * @return The ID
     */
    public String getID (){
	return id;
    }

    /**
     * Accessor for the message
     *
     * @return The message
     */
    public String getMessage (){
	return message;
    }

    /**
     * Specifies how this message should be displayed
     *
     * @return The message in a printable format
     */
    public String toString (){
	String result = "";

	result += "<b>"+id+":</b> ";
	if (boldOn) result += "<b>";
	if (italicizeOn) result += "<i>";
	if (underlineOn) result += "<u>";
	result += message;
	if (underlineOn) result += "</u>";
	if (italicizeOn) result += "</i>";
	if (boldOn) result += "</b>";
	result += "<br>";

	return result;
    }

    // Data carrying variables
    private String id;
    private String message;

    // Message properties
    private boolean boldOn;
    private boolean italicizeOn;
    private boolean underlineOn;
    private java.awt.Color color;
}
