
import lime.*;
import java.awt.*;

public class Owner extends Color {

  private AgentID id;

  public Owner(Color color, AgentID id) {
    super(color.getRGB());
    this.id = id;
  }

  public AgentID getID() {
    return id;
  }

  public AgentLocation getLocation() {
    return new AgentLocation(getID());
  }

  public boolean equals(Object o) {
    return super.equals(o) && o instanceof Owner && id.equals(((Owner) o).id);
  }
}
