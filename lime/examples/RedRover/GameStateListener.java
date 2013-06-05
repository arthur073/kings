
/**
 * Interface for listening for GameStateEvents.
 *
 * @author Jason Ginchereau
 * @version 11/12/99
 */
public interface GameStateListener extends java.util.EventListener {
  void gameStateChanged(GameStateEvent evt);
}

