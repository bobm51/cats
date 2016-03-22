// MeterLnLight.java

package cats.jmri;

import cats.layout.MsgFilter;
import cats.layout.RREventObserver;
import java.beans.PropertyChangeListener;
import jmri.implementation.AbstractLight;
import jmri.NamedBean;
import jmri.jmrix.loconet.LnConstants;
import jmri.jmrix.loconet.LocoNetMessage;

/**
 * MeterLnLight.java
 *
 * Implementation of the Metered Light Object for Loconet.  These
 * objects do not send messages directly to the Loconet, but through
 * a queue.  The queue meters out the messages to avoid overwhelming
 * the Loconet interface.
 * <P>
 *  Based on the JMRI LnLight.
 *
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2004, 2009, 2016</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */
public class MeterLnLight
    extends AbstractLight
    implements RREventObserver, NamedBean {

  /**
   * Create a MeterLnLight object, with only system name.
   * <P>
   * 'systemName' was previously validated in MeterLnLightManager
   * @param systemName is the JMRI system name
   */
  public MeterLnLight(String systemName) {
      super(systemName);
      // Initialize the Light
      initializeLight(systemName);
  }
  
  /**
   * Create a MeterLnLight object, with both system and user names.
   * <P>
   * 'systemName' was previously validated in MeterLnLightManager
   * @param systemName is the JMRI system name
   * @param userName is the name, as defined by the user
   */
  public MeterLnLight(String systemName, String userName) {
      super(systemName, userName);
      initializeLight(systemName);
  }
      
  /**
   * Sets up system dependent instance variables and sets system
   *    independent instance variables to default values
   * Note: most instance variables are in AbstractLight.java
   */
  private void initializeLight(String systemName) {
      // Extract the Bit from the name
      mBit = MeterLnLightManager.instance().getBitFromSystemName(systemName) - 1;
      // compute address fields
      HiAddr = mBit/128;
      LoAddr = mBit-(HiAddr*128);
      
      // Save system name
      mSystemName = systemName;
      // Set initial state
      setOwnState( OFF );
      // Set defaults for all other instance variables
//      setControlType( NO_CONTROL );
//      setControlSensor( null );
//      setControlSensorSense(Sensor.ACTIVE);
//      setFastClockControlSchedule( 0,0,0,0 );
//      setControlTurnout( null );
//      setControlTurnoutState( Turnout.CLOSED );
  }

  /**
   *  System dependent instance variables
   */
  String mSystemName = "";     // system name 
  protected int mState = OFF;  // current state of this light
  private int oldState = OFF;
  int mBit = 0;
  int HiAddr = 0;              // high address byte
  int LoAddr = 0;              // low address byte

  /**
   *  Return the current state of this Light
   */
  public int getState() { return mState; }

  /**
   *  Set the current state of this Light
   *     This routine requests the hardware to change.
   */
  public void setState(int newState) {
  LocoNetMessage l = new LocoNetMessage(4);
  l.setOpCode(LnConstants.OPC_SW_REQ);
  // compute address fields
  int hiadr = HiAddr;
  // set bits for ON/OFF
  if (newState==ON) {
    hiadr |= (jmri.jmrix.loconet.LnConstants.OPC_SW_REQ_OUT |
        jmri.jmrix.loconet.LnConstants.OPC_SW_REQ_DIR);
  }
  else if (newState==OFF) {
    hiadr |= jmri.jmrix.loconet.LnConstants.OPC_SW_REQ_OUT;
  }
  else {
    log.warn("illegal state requested for Light: "+getSystemName());
    hiadr |= jmri.jmrix.loconet.LnConstants.OPC_SW_REQ_OUT;
      }
  // store and send
  l.setElement(1,LoAddr);
  l.setElement(2,hiadr);
  MeterLnTrafficController.instance().sendLocoNetMessage(l);
  setOwnState(newState);
  }
  
  /**
   * Set out internal state information, and notify bean listeners.
   * @param s is the new state of the MeterLnLight
   */
  public void setOwnState(int s) {
      if (mState != s) {
          oldState = mState;
          mState = s;
          firePropertyChange("KnownState", new Integer(oldState), new Integer(mState));
      }
  }

  /**
   * Registers an Object that wants to know when the state of the
   * light changes.  This is a wrapper around the NetBeans
   * addPropertyChangeListener so that the MeterLnLight Object
   * can be registered with the MsgFilter.
   * 
   * @param l is the Object wanting to know when the Light changes.
   */
  public void addPropertyChangeListener(PropertyChangeListener l) {
    if (!(l instanceof MeterLnLightManager)) {
      MsgFilter.registerSWREQObserver(mBit, this);
    }
    super.addPropertyChangeListener(l);
  }
  
  /**
   * is the interface through which the RREvent is delivered.
   *
   * @param msg is the LocoNetMessage received.
   */
  public void acceptMessage(LocoNetMessage msg) {
    // save the state
    int state = msg.getElement(2) & jmri.jmrix.loconet.LnConstants.OPC_SW_REQ_DIR;
    if (log.isDebugEnabled()) {
      log.debug("OPC_SW_REQ received with valid address, old state "
          + getState() + " new packet " + state);
    }
    if (state != 0) {
      if (log.isDebugEnabled()) {
        log.debug("Set ON");
      }
      setOwnState(ON);
    }
    else {
      if (log.isDebugEnabled()) {
        log.debug("Set OFF");
      }
      setOwnState(OFF);
    }
  }

  static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(
      MeterLnLight.class.getName());
}
/* @(#)MeterLnLight.java */
