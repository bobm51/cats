/* Name: ArrowEvent.java
 *
 * What:
 *   This class queues a Train selection/direction command.
 */
package cats.rr_events;

import cats.gui.ArrowUser;
/**
 *   This class queues a Train selection/direction key push.
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */
public class ArrowEvent
    extends RREvent {

  /**
   * is the Loconet message being queued.
   */
  private int ArrowKey;

  /**
   * is the object receiving the Key.
   */
  private ArrowUser Who;

  /**
   * is the constructor.
   *
   * @param key is the key code being queued.
   * @param who is the Object where the key code will be delivered.
   */
  public ArrowEvent(int key, ArrowUser who) {
    ArrowKey = key;
    Who = who;
  }

  /*
   * Performs the command encapsulated by this object.
   */
  public void doIt() {
    Who.takeArrow(ArrowKey);
  }
}
/* @(#)ArrowEvent.java */