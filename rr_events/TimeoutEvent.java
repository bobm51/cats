/* Name: TimeoutEvent.java
 *
 * What:
 *   This class queues a timeout.
 */
package cats.rr_events;

import cats.layout.TimeoutObserver;

/**
 *   This class queues a timeout event.
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */
public class TimeoutEvent
    extends RREvent {

  /**
   * is the object receiving the timeout.
   */
  private TimeoutObserver Who;

  /**
   * is the constructor.  There is an assumption in all of this that
   * because messages may be sent to multiple recipients and the messages
   * are not being copied, that no recipient is going to change a message's
   * contents.
   *
   * @param who is the Object where the message will be delivered.
   */
  public TimeoutEvent(TimeoutObserver who) {
    Who = who;
  }

  /*
   * Performs the command encapsulated by this object.
   */
  public void doIt() {
    Who.acceptTimeout();
  }
}
/* @(#)TimeoutEvent.java */