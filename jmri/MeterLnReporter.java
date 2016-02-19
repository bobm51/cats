// MeterLnReporter.java

package cats.jmri;

import java.beans.PropertyChangeListener;

import cats.layout.MsgFilter;
import cats.layout.RREventObserver;
import cats.layout.items.IOSpec;
import jmri.implementation.AbstractReporter;
import jmri.jmrix.loconet.LnConstants;
import jmri.jmrix.loconet.LocoNetMessage;

/**
 * Extend jmri.AbstractReporter for LocoNet layouts.  The class name
 * is misleading.  It is used more like an JMRI Sensor.  Since
 * CATS already uses MeterLnSensor name, some
 * other name was needed.  Because of the ambiguity in the OPC_SW_REP
 * message (there are 2 bits -> 4 possible values), perhaps this
 * should allow more than 2 states.
 * <P>
 * Some of the message formats used in this class are Copyright Digitrax, Inc.
 * and used with permission as part of the JMRI project.  That permission
 * does not extend to uses in other software products.  If you wish to
 * use this code, algorithm or these message formats outside of JMRI, please
 * contact Digitrax Inc for separate permission.
 * <p>
 * The difference between this Loconet Reporter and LnSensor is that
 * these sensors attach to a Loconet message distributor; thus, the
 * message() method is invoked only for messages that have the proper
 * Loconet address - not for all messages.
 * <p>
 * The difference between this Loconet Reporter and MeterLnSensor is
 * the Loconet messages.  MeterLnSensor uses the OPC_INPUT_REP messages.
 * This uses OPC_SW_REP.
 * <p>
 * Much of this code is derived from LnReporter.java.
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2006, 2009, 2016</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */
public class MeterLnReporter
    extends AbstractReporter
    implements RREventObserver {

  
  /**
   * is the Loconet address as a number.
   */
  private int LocoAddress;
  private int HiAddr = 0;              // high address byte
  private int LoAddr = 0;              // low address byte
  private int MyState;

  /**
   * a constructor for when both names are provided.
   *
   * @param systemName is the Reporter name as known by JMRI.
   * @param userName is how the user refers to the Reporter.
   */
  public MeterLnReporter(String systemName, String userName) {
    super(systemName, userName);
    init(systemName);
  }

  /**
   * a constructor for when only the system name is provided.
   *
   * @param systemName is the Reporter name as known by JMRI.
   */
  public MeterLnReporter(String systemName) {
    super(systemName);
    init(systemName);
  }

  /**
   * Common initialization for both constructors
   * @param id is the name of the Metered Sensor.
   */
  private void init(String id) {
    // Extract the Bit from the name
    LocoAddress = MeterLnReporterManager.instance().getBitFromSystemName(id) - 1;
    // compute address fields
    HiAddr = LocoAddress/128;
    LoAddr = LocoAddress-(HiAddr*128);
    
    // Set initial state
    MyState = 2;
  }

  /**
   * User request to set the state, which means that we broadcast that to
   * all listeners by putting it out on LocoNet.
   * In turn, the code in this class should use setOwnState to handle
   * internal sets and bean notifies.
   * @param s is the state.
   * @throws JmriException
   */
  public void setState(int s) {
    LocoNetMessage l = new LocoNetMessage(4);
    l.setOpCode(LnConstants.OPC_SW_REP);
    // compute address fields
    int hiadr = HiAddr;
    // set bits for ON/OFF
    if (s==1) {
      hiadr |= (jmri.jmrix.loconet.LnConstants.OPC_SW_REP_SW |
          jmri.jmrix.loconet.LnConstants.OPC_SW_REP_HI);
    }
    else if (s==0) {
      hiadr |= jmri.jmrix.loconet.LnConstants.OPC_SW_REP_SW;
    }
    else {
      log.warn("illegal state requested for Sensor: "+getSystemName());
      hiadr |= jmri.jmrix.loconet.LnConstants.OPC_SW_REP_SW;
        }
    // store and send
    l.setElement(1,LoAddr);
    l.setElement(2,hiadr);
    MeterLnTrafficController.instance().sendLocoNetMessage(l);
    MyState = s;
  }
  
  /**
   * Set out internal state information, and notify bean listeners.
   * @param s is the new vale of the state
   */
  public void setOwnState(int s) {
      if (MyState != s) {
        MyState = s;
        setReport((s == 0) ? IOSpec.CLOSE_TEXT : IOSpec.THROW_TEXT);
      }
  }

  /**
   * returns the current state.
   * 
   * @return the current integer state.
   */
  public int getState() {
    return MyState;
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
    if (!(l instanceof MeterLnReporterManager)) {
      MsgFilter.registerSWREPObserver(LocoAddress, this);
    }
    super.addPropertyChangeListener(l);
  }

  /**
   * implementing classes will typically have a function/listener to get
   * updates from the layout, which will then call
   *      public void firePropertyChange(String propertyName,
   *                Object oldValue,
   *                                          Object newValue)
   * _once_ if anything has changed state (or set the commanded state directly)
   * @param l
   */
  public void acceptMessage(LocoNetMessage l) {
    // save the state
    int state = l.getElement(2) & jmri.jmrix.loconet.LnConstants.OPC_SW_REP_HI;
    if (log.isDebugEnabled()) {
      log.debug("OPC_SW_REP received with valid address, old state "
          + getState() + " new packet " + state);
    }
    if (state != 0) {
      state = 1;
    }
    if (state != MyState) {
      setOwnState(state);
    }
  }

  public void dispose() {
  }

  static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(
      MeterLnSensor.class.getName());

}
/* @(#)MeterLnReporter.java */