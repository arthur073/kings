
/**
 * An event that indicates the user has initiated a game state
 * change, such as joining or leaving the game.
 *
 * @author Jason Ginchereau
 * @version 11/12/99
 */
public class GameStateEvent extends java.util.EventObject {

  public static final int PLAYER_JOINING_GAME = 1;
  public static final int PLAYER_LEAVING_GAME = 2;
  public static final int PLAYER_EXITING_GAME = 3;

  /**
   * Creates a new GameStateEvent.
   *
   * @param source the object that generates this event
   * @param id the type of game state change, one of the constants
   *           defined in this class
   */
  public GameStateEvent(Object source, int id) {
    super(source);
    this.id = id;
  }

  /**
   * Gets the type of game state change.
   */
  public int getID() {
    return id;
  }

  private int id;
}

