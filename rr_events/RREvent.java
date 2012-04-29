/* Name: RREvent.java
 *
 * What:
 *   This file contains an abstract class for objects that can be queued between
 *   the Java event handler (or other tasks) and the rest of the layout.
 */
package cats.rr_events;

/**
 *   This file contains an abstract class for objects that can be queued between
 *   the Java event handler (or other tasks) and the rest of the layout.
 *   The intent for this architecture is two-fold:
 *   <ol>
 *   <li> separate the processing of Swing event from the Java event handler
 *        thread.  Thus, the event handler will not be tied up in making
 *        state changes and might miss other Swing events.
 *   <li> provide a synchronization mechanism so that Java events do not
 *        conflict with events arriving from the railroad.
 *   </ol>
 *   This architecture is similar to the command design pattern, so that
 *   actions are represented as objects and those objects can be queued.
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */
public abstract class RREvent {

  /**
   * Performs the command encapsulated by this object.
   */
  public abstract void doIt();

  /**
   * adds the RREvent object to the queue.
   */
  public void queUp() {
    RREventManager.EventQue.append(this);
  }
}
/* @(#)RREvent.java */