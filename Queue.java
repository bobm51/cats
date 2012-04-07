/* Name: Queue.java
 *
 * What:
 *   This class defines a Queue of Strings, which can be shared by multiple
 *   threads.
 *
 * Special Considerations:
 *   This class is application independent.
 */
package cats.layout;

/**
 *   This class defines a Queue of Strings, which can be shared by multiple
 *   threads.
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2004, 2005, 2006, 2007, 2008, 2009, 2010</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */
public class Queue {
  volatile Element Head; /* where Strings are removed */
  volatile Element Tail; /* where Strings are added */
  boolean Flushed = false;

  /**
   *   An inner class object for providing the linkage between
   *   entries in the Queue.
   */
  class Element {
    Element Next;
    Object Content;

    /**
     *  The constructor.  It creates a wrapper around the
     *   String, containing the linkages that form the Queue
     *
     * @param   s is the thing being put in the Queue
     */
    public Element(Object s) {
      Content = s;
    }
  }

  /**
   *  The Queue constructor
   *
   */
  public Queue() {
  }

  /**
   *  This method adds a String to the Queue
   *
   * @param s is the Object to be added to the Queue
   */
  public synchronized void append(Object s) {
    Element e = new Element(s);
    if (Tail == null) {
      Head = e;
    }
    else {
      Tail.Next = e;
    }
    e.Next = null;
    Tail = e;
    notify(); /* let the consumer know the Queue is not empty */
  }

  /**
   * This method pulls the oldest String off the Queue
   *
   * @return The oldest Object on the Queue
   */
  public synchronized Object get() {
    try {
      while ( (Head == null) && !Flushed) {
        wait();
      }
    }
    catch (InterruptedException e) {
      return null;
    }
    Element ele = Head;
    if (Head != null) {
      Head = Head.Next;
      if (Head == null) {
        Tail = null;
      }
      ele.Next = null; /* help the garbage collector out */
      return ele.Content;
    }
    return null;
  }

  /**
   *  This method notifies any Queue consumers that the Queue
   *   should be flushed
   *
   */
  public synchronized void flush() {
    Flushed = true;
    notify();
  }
}
/* @(#)Queue.java */