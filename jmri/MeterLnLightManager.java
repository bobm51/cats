// MeterLnLightManager.java

package cats.jmri;

import jmri.managers.AbstractLightManager;
import jmri.jmrix.loconet.LnConstants;
import jmri.jmrix.loconet.LnSensorAddress;
import jmri.jmrix.loconet.LocoNetMessage;
import jmri.jmrix.loconet.LocoNetListener;
import jmri.Light;

/**
 * Implement a metered light manager for Loconet systems.  The key
 * feature is that all Loconet messages go through a singleton which
 * meters out the messages to the Loconet so that they do not
 * overwhelm the Loconet.
 * <P>
 * System names are "MLnnn", where nnn is the Loconet address.
 * <P>
 * Based in part on SerialLightManager.java
 *
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2004, 2009, 2016</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */
public class MeterLnLightManager
extends AbstractLightManager
implements LocoNetListener {

	  /**
	   * is the interface specifier.
	   */
	  private static final String MY_SPECIFIER = "M";

	  /**
	   * is the kind of device specifier.
	   */
	  private static final String MY_DEVICE = "L";

	  /**
	   * is the system identifier
	   */
	  private static final String MY_IDENTIFIER = MY_SPECIFIER + MY_DEVICE;
	  
  /**
   * constructs the MeterLightManager.
   */
  public MeterLnLightManager() {
    _instance = this;
    if (MeterLnTrafficController.instance() == null) {
      new MeterLnTrafficController();
    }
  }

  /**
   *  Returns the system letter for Loconet
   *
   * @return the leading character in the system name.
   */
//  public char systemLetter() {
//    return MY_SPECIFIER;
//  }
  
  public String getSystemPrefix() {
    return MY_SPECIFIER;
  }

  /*
   * return the system prefix
   */
//  @SuppressWarnings("deprecation")
//public String getSystemPrefix(){
//    return ""+systemLetter(); // for now.  if more than one, this
//                              // needs to be different.
//  }

  /**
   * Method to create a new Light based on the system name
   * Returns null if the system name is not in a valid format or
   *    if the system name does not correspond to a configured
   *    Loconet address.
   * Assumes calling method has checked that a Light with this
   *    system name does not already exist
   *
   * @param systemName should be "ML" something
   * @param userName is what the user labels the new Light.
   *
   * @return a new Light for Loconet, with all messages going
   * through a Governor.
   */
  public Light createNewLight(String systemName, String userName) {
    return new MeterLnLight(systemName, userName);
  }
  
  /**
   *  Get the bit address from the system name 
   *  @param systemName is the JMRI system name
   *  @return the integer portion of the system name - its Loconet address
   */
  public int getBitFromSystemName (String systemName) {
    // validate the system Name leader characters
	    if ( !systemName.startsWith(MY_IDENTIFIER) ) {
	        // here if an illegal loconet light system name 
	        log.error("illegal character in header field of loconet light system name: "+systemName);
	        return (0);
	      }
    // name must be in the LLnnnnn format
    int num = 0;
    try {
      num = Integer.valueOf(systemName.substring(2)).intValue();
    }
    catch (Exception e) {
      log.error("illegal character in number field of system name: "+systemName);
      return (0);
    }
    if (num<=0) {
      log.error("invalid loconet light system name: "+systemName);
      return (0);
    }
    else if (num>4096) {
      log.error("bit number out of range in loconet light system name: "+systemName);
      return (0);
    }
    return (num);
  } 
  /**
   * Public method to validate system name for configuration
   * @param systemName is the string being tested.
   *
   * @return 'true' if system name has a valid meaning in current configuration,
   *      else returns 'false'
   */
  public boolean validSystemNameConfig(String systemName) {
    return validSystemNameFormat(systemName);
  }
  
  /**
   * Public method to validate system name format
   *   returns 'true' if system name has a valid format, else returns 'false'
   */
  public boolean validSystemNameFormat(String systemName) {
    return (getBitFromSystemName(systemName)!=0);
  }

  /**
   * Allow access to MeterLnLightManager
   *
   * @return the singleton MeterLnLightManger.  If one does not exist, one
   * is created.
   */
  static public MeterLnLightManager instance() {
    if (_instance == null) {
      _instance = new MeterLnLightManager();
    }
    return _instance;
  }

  /**
   * reports the existence of an MeterLnLightManager.  This would not be needed
   * if MeterLnLightManager was integrated into JMRI.
   *
   * @return true if it exists and false if it doesn't.
   */
  static public boolean exists() {
    return _instance != null;
  }

  /**
   * Member function that will be invoked by a LocoNetInterface implementation
   * to forward a LocoNet message from the layout.
   *
   * @param msg  The received LocoNet message.  Note that this same object
   *             may be presented to multiple users. It should not be
   *             modified here.
   */
  public void message(LocoNetMessage msg) {

    // parse message type
    LnSensorAddress a;
    switch (msg.getOpCode()) {
      case LnConstants.OPC_SW_REQ: { /* page 9 of Loconet PE */
        int sw1 = msg.getElement(1);
        int sw2 = msg.getElement(2);
        a = new LnSensorAddress(sw1, sw2, MY_IDENTIFIER);
//        a = new LnSensorAddress(sw1, sw2);
        if (log.isDebugEnabled()) {
          log.debug("SWITCH_REQ received with address " + a);
        }
        break;
      }
      default: // here we didn't find an interesting command
        return;
    }
    // reach here for loconet sensor input command; make sure we know about this one
    String s = a.getNumericAddress();
    if (null == getBySystemName(s)) {
      // need to store a new one
      if (log.isDebugEnabled()) {
        log.debug("Create new LnLight as " + s);
      }
      createNewLight(s, null);
    }
  }

  static MeterLnLightManager _instance = null;

  static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(
      MeterLnLightManager.class.getName());

}
/* @(#)MeterLnLightManager.java */
