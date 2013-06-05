/**
 * @author   Jason Ginchereau
 * @version  November 16, 1999
 */

import lime.*;
public class UID implements java.io.Serializable {
  // identifying data
  private LimeServerID host; // logical host
  private long         n;

  // An increasing static identifier makes every UID unique.
  private static long ID = 0;

  /**
   * Creates a new unique identifier.
   */
  public UID() {
    this.host = LimeServer.getServerID();
    this.n = nextID();
  }

  // Added to support LSTS operations.  This should NOT
  // be used for normal operations because it doesn't
  // ensure a unique UID
  public UID (LimeServerID host, long n){
    this.host = host;
    this.n = n;
  }


  public int hashCode() {
    return host.hashCode() + (int)n;
  }

  private static synchronized long nextID() {
    return ID++;
  }

  // Added by bdp on 2-26-2000 to support LSTS ghost management
  public LimeServerID getAddress() {
    return host;
  }

  public long getN() {
    return n;
  }

  public String toString() {
    return "" + host + "[" + n + "]";
  }

  public boolean equals(Object o) {
    return o instanceof UID
      && n == ((UID)o).n
      && host.equals(((UID)o).host);
  }
}
