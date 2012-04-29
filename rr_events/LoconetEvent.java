/* Name: LoconetEvent.java
 *
 * What:
 *   This class queues a Loconet message.
 */
package cats.rr_events;

import cats.layout.RREventObserver;
import jmri.jmrix.loconet.LocoNetMessage;

/**
 *   This class queues a Loconet message.
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */
public class LoconetEvent
    extends RREvent {

  /**
   * is the object receiving the Loconet message.
   */
  private RREventObserver Who;

  /**
   * is the LocoNetMessage received
   */
  private LocoNetMessage Msg;

  /**
   * is the constructor.  There is an assumption in all of this that
   * because messages may be sent to multiple recipients and the messages
   * are not being copied, that no recipient is going to change a message's
   * contents.
   *
   * @param who is the Object where the message will be delivered.
   * @param msg is the LocoNetMessage being delivered.
   */
  public LoconetEvent(RREventObserver who, LocoNetMessage msg) {
    Who = who;
    Msg = msg;
  }

  /*
   * Performs the command encapsulated by this object.
   */
  public void doIt() {
    Who.acceptMessage(Msg);
  }
}
/* @(#)LoconetEvent.java */