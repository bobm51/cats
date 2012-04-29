// MeterLnTrafficController.java

package cats.jmri;

import cats.gui.Adjuster;
import cats.gui.CounterFactory;
import cats.gui.Sequence;
import cats.layout.Governor;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import jmri.jmrix.loconet.LnTrafficController;
import jmri.jmrix.loconet.LocoNetListener;
import jmri.jmrix.loconet.LocoNetMessage;

/**
 * MeterLnTrafficController.java
 *
 * Implementation of an LnTrafficController.  An instantiation contains
 * a queue for delaying sending packets out.  This delay is to prevent
 * overwhelming the Loconet and its interface device.
 * <P>
 *  Based in part on LnTrafficRouter.
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2004, 2006, 2009</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */
public class MeterLnTrafficController
    extends LnTrafficController
    implements LocoNetListener, PropertyChangeListener {

  /**
   * is the queue for metering out the messages.
   */
  private Governor MyGovernor;

  /**
   * is the number of milliseconds to wait after sending a command
   * before sending the next.  This meters the output so as to not
   * overrun the network.
   */
  private static Sequence FlowRate;
  
  /**
   * is the MeterLnTrafficController.
   */
  static protected MeterLnTrafficController Meter;

  /**
   * Create a LnTrafficController which meters its output.
   */
  public MeterLnTrafficController() {
    super();
    Meter = this;
    MyGovernor = new Governor();
    Governor.OutputThread = new Thread(MyGovernor);
    Governor.OutputThread.setName("LnMeter");
    Governor.OutputThread.start();
    Governor.OutputThread.setPriority(Thread.MAX_PRIORITY);
    if (FlowRate == null) {
      FlowRate = CounterFactory.CountKeeper.findSequence(
          CounterFactory.LNTAG);
      CounterFactory.CountKeeper.exposeSequence(CounterFactory.LNTAG);
      FlowRate.registerAdjustmentListener(this);
    }
  }

  /**
   * is a static function for returning the MeterLnTrafficController
   * to use.
   */
  static public LnTrafficController instance() {
    if (Meter == null) {
      new MeterLnTrafficController();
    }
    return Meter;
  }

  /**
   * queues the message for transmission.
   *
   * @param msg is the message to be sent.
   */
  public void sendLocoNetMessage(LocoNetMessage msg) {
    Governor.LocoQue.append(msg);
  }

  /**
   * returns the status of the connection.
   *
   * @return true if there is a LnTrafficController, somewhere.
   */
  @SuppressWarnings("deprecation")
public boolean status() {
    return LnTrafficController.instance() != null;
  }
  
  /**
   * Is there a backlog of information for the outbound link?
   * This includes both in the program (e.g. the outbound queue)
   * and in the command station interface (e.g. flow control from the port)
   * @return true if busy, false if nothing waiting to send.
   * Since everything is going through a queue, it is always false.
   */
  public boolean isXmtBusy() {
    return false;
  }

  /**
   * receive a message and forward it to all listeners.
   *
   * @param m is the message.
   */
  public void message(LocoNetMessage m) {
    notify(m);
  }

  /**
   * receives the notification that the delay between messages has changed.
   * @param ce is the PropertyChangeEvent
   */
  public void propertyChange(PropertyChangeEvent ce) {
    if (ce.getPropertyName().equals(Adjuster.CHANGE_TAG)) {
      MyGovernor.setMsgDelay(((Integer)ce.getNewValue()).intValue());
    }
  }

  static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(
      MeterLnTrafficController.class.getName());
}
/* @(#)MeterLnTrafficController.java */