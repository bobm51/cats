/* Name: RREventManager.java
 *
 * What:
 *   This file contains a class that becomes the thread that receives
 *   RREvents from a queue and executes them.
 */
package cats.rr_events;

import cats.layout.Queue;

/**
 *   This file contains a class that becomes the thread that receives
 *   RREvents from a queue and executes them.  All objects placed in the
 *   queue must be derived from RREvent, so that the doIt() method exists
 *   and can be executed.
 *   @see cats.rr_events.RREvent
 *   @see cats.layout.Queue
 *   @see java.lang.Runnable
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */
public class RREventManager implements Runnable {

  /**
   * is the event consumer constructor.
   */
  public RREventManager() {
  }

  /**
   * a FIFO for holding RREvent objects.
   */
  public static Queue EventQue = new Queue();

  /**
   * The asynchronous thread that periodically flushes the Queue
   * to disk.
   */
  public static Thread EventThread;

  /* Name: void run()
   *
   * What:
   *   This is the Thread
   *
   * Inputs:
   *   There are none
   *
   * Returns:
   *   None
   *
   * Special Considerations:
   */
  public void run() {
    RREvent event;
//    System.out.println("RREventManager::run");
    while (true) {
      try {
        event = ( (RREvent) EventQue.get());
        event.doIt();
      }
      catch (ClassCastException cce) {
        System.out.println("RREventManager received something that isn't an RREvent.");
      }
    }
  }
}
/* @(#)RREventManager.java */