// MeterLnTrafficController.java

package cats.jmri;

import cats.gui.Adjuster;
import cats.gui.CounterFactory;
import cats.gui.Sequence;
import cats.layout.Governor;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import jmri.InstanceManager;
import jmri.jmrix.SystemConnectionMemo;
import jmri.jmrix.loconet.LnTrafficController;
import jmri.jmrix.loconet.LocoNetListener;
import jmri.jmrix.loconet.LocoNetMessage;
import jmri.jmrix.loconet.LocoNetSystemConnectionMemo;

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
   * is the LnTrafficController being decorated
   */
  static private LnTrafficController DecoratedController;
  
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
      DecoratedController = findLNController();
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
public boolean status() {
    return DecoratedController != null;
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

	/**
	 * a utility for finding a Loconet Traffic Controller that can be
	 * used for signaling.  Three situations need to be considered:
	 * <ol>
	 * <li>no Loconet connection - in which case null is returned</li>
	 * <li>only one Loconet connection - in which case, it is returned</li>
	 * <li>multiple Loconet connections - in which case the one with a "L" system prefix is returned.
	 * If there is no "L" system prefix, the first is returned.
	 * </ol>
	 * @return an instance of an LnTrafficController or null
	 */
	static public LnTrafficController findLNController() {
		List<LocoNetSystemConnectionMemo> list = InstanceManager.getList(LocoNetSystemConnectionMemo.class);
		if (list.isEmpty()) {
			log.fatal("No Loconet connection was detected");
			return null;
		}
		if (list.size() == 1) {
			return list.get(0).getLnTrafficController();
		}
		for (Object memo : list) {
			if ("L".equals(((SystemConnectionMemo) memo).getSystemPrefix())) {
				return ((LocoNetSystemConnectionMemo) memo).getLnTrafficController();
			}
		}
		return list.get(0).getLnTrafficController();
	}
	
	/**
	 * retrieve the LnTrafficController
	 * @return LnTrafficController being used
	 */
	static public LnTrafficController getTrafficController() {
		return DecoratedController;
	}
  static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(
      MeterLnTrafficController.class.getName());
}
/* @(#)MeterLnTrafficController.java */