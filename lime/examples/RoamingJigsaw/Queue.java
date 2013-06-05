
/**
 * A basic queue implemented with a doubly-linked list.
 * Includes proper synchronization so multiple threads can
 * easily use the queue.
 *
 * @author Jason Ginchereau
 */
class Queue {

  private Node    head;
  private Node    tail;
  private int     size   = 0;
  private boolean closed = false;

  /**
   * Adds an object to the queue.
   */
  public final synchronized void add(Object obj) {
    if(closed) throw new IllegalStateException();
    Node n = new Node();
    n.obj = obj;
    if(tail != null) tail.next = n;
    else head = n;
    n.prev = tail;
    n.next = null;
    tail = n;
    size++;
    notify();
  }
  
  /**
   * Blocks until an object is available.
   * Returns null if the queue is closed.
   */
  public final synchronized Object getNext() {
    while(head == null && !closed) {
      try { wait(); }
      catch(InterruptedException iex) { }
    }
    if(closed) return null;
    Object obj = head.obj;
    if(head == tail) tail = null;
    head = head.next;
    if(head != null) head.prev = null;
    size--;
    return obj;
  }

  /**
   * Removes an object from the queue.
   * Returns true of the object was found and removed.
   */
  public final synchronized boolean remove(Object obj) {
    for(Node n = head; n != null; n = n.next) {
      if(n.obj == obj) {
        if(n == head) head = head.next;
        else n.prev.next = n.next;
        if(n == tail) tail = tail.prev;
        else n.next.prev = n.prev;
        size--;
        return true;
      }
    }
    return false;
  }

  public final int getSize() {
    return size;
  }

  public final boolean isClosed() {
    return closed;
  }

  public final void close() {
    closed = true;
    notifyAll();
  }

  protected void finalize() throws Throwable {
    close();
    super.finalize();
  }

  private static final class Node {
    Node   prev;
    Node   next;
    Object obj;
  }

  /*
  // test for proper synchronization:
  public static void main(String[] args) {
    final Queue q = new Queue();

    new Thread() { public void run() {
      for(int i = 0; ; i++) {
        try { Thread.sleep((int) (Math.random() * 500)); }
        catch(InterruptedException iex) { }
        System.err.println("adding " + i);
        q.add(new Integer(i));
      }
    }}.start();
    new Thread() { public void run() {
      while(true) {
        try { Thread.sleep((int) (Math.random() * 500)); }
        catch(InterruptedException iex) { }
        System.err.println("got " + q.getNext());
      }
    }}.start();
  }
  */
}
