/* Name: Governor.java
 *
 * What:
 *   This class defines a Singleton facility for batching outgoing Loconet
 *   commands.  It serves two purpose:
 *   * it prevents the dispatcher panel from overruning the Loconet interface
 *     on a fast computer
 *   * it provides a processing pause between messages to the same SE8C.
 *
 * Special Considerations:
 *   This is a delicate piece of code.
 */
package cats.layout;

import jmri.jmrix.loconet.LocoNetMessage;
import cats.jmri.MeterLnTrafficController;
import jmri.jmrix.loconet.LnTrafficController;

/**
 *   This class defines a Singleton facility for batching outgoing Loconet
 *   commands.  It serves two purposes:
 * <ul>
 * <li> it prevents the dispatcher panel from overruning the Loconet interface
 *      on a fast computer
 * <li> it provides a processing pause between messages to the same SE8C.
 * </ul>
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */
public class Governor
    implements Runnable {
  /**
   * Is the number of milliseconds to delay between successive messages
   * to the SE8C.
   */
  private static int MsgDelay;

  /**
   * a FIFO for holding Loconet messages ready to be sent to the layout.
   */
  public static Queue LocoQue = new Queue();

  /**
   * The thread that periodically writes to the Loconet.
   */
  public static Thread OutputThread;

  /**
   * The class constructor.
   */
  public Governor() {
    MsgDelay = 10;
  }

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
    LocoNetMessage msg;
    LnTrafficController ltc;

    while (true) {
      msg = (LocoNetMessage) LocoQue.get();
      ltc = MeterLnTrafficController.getTrafficController();
      if (ltc.isXmtBusy()) {
        System.out.println("Loconet is busy");
      }
      ltc.sendLocoNetMessage(msg);
      try {
        Thread.sleep(MsgDelay);
      } catch(InterruptedException ignore) {}
    }
  }

  /**
   * sets the delay between sending messages.
   *
   * @param msecDelay is the delay in milliseconds
   */
  public void setMsgDelay(int msecDelay) {
    MsgDelay = msecDelay;
  }
}
/* @(#)Governor.java */