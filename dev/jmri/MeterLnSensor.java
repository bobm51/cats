// MeterLnSensor.java

package cats.jmri;

import java.beans.PropertyChangeListener;

import cats.layout.MsgFilter;
import cats.layout.RREventObserver;
import jmri.implementation.AbstractSensor;
import jmri.JmriException;
import jmri.jmrix.loconet.LnConstants;
import jmri.jmrix.loconet.LnSensorAddress;
import jmri.jmrix.loconet.LocoNetMessage;
import jmri.Sensor;

/**
 * Extend jmri.AbstractSensor for LocoNet layouts.
 * <P>
 * Some of the message formats used in this class are Copyright Digitrax, Inc.
 * and used with permission as part of the JMRI project.  That permission
 * does not extend to uses in other software products.  If you wish to
 * use this code, algorithm or these message formats outside of JMRI, please
 * contact Digitrax Inc for separate permission.
 * <p>
 * The difference between this Loconet Sensor and LnSensor is that
 * these sensors attach to a Loconet message distributor; thus, the
 * message() method is invoked only for messages that have the proper
 * Loconet address - not for all messages.
 * <p>
 * Much of this code is derived from LnSensor.java.
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2004, 2009</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */
public class MeterLnSensor
    extends AbstractSensor
    implements RREventObserver {

  /**
   * is the Loconet address, in LnAddress format.
   */
  private LnSensorAddress a;

  /**
   * is the Loconet address as a number.
   */
  private int LocoAddress;

  /**
   * a constructor for when both names are provided.
   *
   * @param systemName is the Sensor name as known by JMRI.
   * @param userName is how the user refers to the sensor.
   */
  public MeterLnSensor(String systemName, String userName) {
    super(systemName, userName);
    init(systemName);
  }

  /**
   * a constructor for when only the system name is provided.
   *
   * @param systemName is the Sensor name as known by JMRI.
   */
  public MeterLnSensor(String systemName) {
    super(systemName);
    init(systemName);
  }

  /**
   * Common initialization for both constructors
   * @param id is the name of the Metered Sensor.
   */
  private void init(String id) {
    // store address forms
    a = new LnSensorAddress("LS" + id.substring(2), "L");
//	    a = new LnSensorAddress("LS" + id.substring(2));
    LocoAddress = Integer.parseInt(id.substring(2, id.length())) - 1;
    if (log.isDebugEnabled()) {
      log.debug("create address " + a);
    }
  }

  /**
   * request an update on status by sending a loconet message
   */
  public void requestUpdateFromLayout() {
    // the only known way to do this from LocoNet is to request the
    // status of _all_ devices, which is here considered too
    // heavyweight.  Perhaps this is telling us we need
    // a "update all" in the SensorManager (and/or TurnoutManager)
    // interface?
  }

  /**
   * User request to set the state, which means that we broadcast that to
   * all listeners by putting it out on LocoNet.
   * In turn, the code in this class should use setOwnState to handle
   * internal sets and bean notifies.
   * @param s is the state.
   * @throws JmriException
   */
  public void setKnownState(int s) throws jmri.JmriException {
    // send OPC_INPUT_REP with new state to this address
    LocoNetMessage l = new LocoNetMessage(4);
    l.setOpCode(LnConstants.OPC_INPUT_REP);
    a.insertAddress(l);
    // set state
    if (s == Sensor.ACTIVE) {
      l.setElement(2, l.getElement(2) | 0x10);
    } // otherwise is already OK
    l.setElement(2, l.getElement(2) | 0x40);
    // send
    MeterLnTrafficController.instance().sendLocoNetMessage(l);
    super.setKnownState(s);
  }

  /**
   * Registers an Object that wants to know when the state of the
   * Sensor changes.  This is a wrapper around the NetBeans
   * addPropertyChangeListener so that the MeterLnSensor Object
   * can be registered with the MsgFilter.
   * 
   * @param l is the Object wanting to know when the Light changes.
   */
  public void addPropertyChangeListener(PropertyChangeListener l) {
    if (!(l instanceof MeterLnSensorManager)) {
      MsgFilter.registerObserver(LocoAddress, this);
    }
    super.addPropertyChangeListener(l);
  }

  /**
   * implementing classes will typically have a function/listener to get
   * updates from the layout, which will then call
   *      public void firePropertyChange(String propertyName,
   *      					Object oldValue,
   *                                          Object newValue)
   * _once_ if anything has changed state (or set the commanded state directly)
   * @param l
   */
  public void acceptMessage(LocoNetMessage l) {
    int sw2 = l.getElement(2);
    // save the state
    int state = sw2 & 0x10;
    if (log.isDebugEnabled()) {
      log.debug("INPUT_REP received with valid address, old state "
          + getKnownState() + " new packet " + state);
    }
    if (state != 0) {
      if (log.isDebugEnabled()) {
        log.debug("Set ACTIVE");
      }
      setOwnState(Sensor.ACTIVE);
    }
    else {
      if (log.isDebugEnabled()) {
        log.debug("Set INACTIVE");
      }
      setOwnState(Sensor.INACTIVE);
    }
  }

  public void dispose() {
  }

  static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(
      MeterLnSensor.class.getName());

}
/* @(#)MeterLnSensor.java */