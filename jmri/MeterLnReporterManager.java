// MeterLnReporterManager.java

package cats.jmri;

import jmri.managers.AbstractReporterManager;
import jmri.jmrix.loconet.LnConstants;
import jmri.jmrix.loconet.LnSensorAddress;
import jmri.jmrix.loconet.LocoNetMessage;
import jmri.jmrix.loconet.LocoNetListener;
import jmri.Reporter;

/**
 * Implement a metered Reporter manager for Loconet systems.  The key
 * feature is that all Loconet messages go through a singleton which
 * meters out the messages to the Loconet so that they do not
 * overwhelm the Loconet.
 * <P>
 * System names are "MRnnn", where nnn is the Loconet address.
 * <P>
 * Based in part on SerialLightManager.java
 *
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2006, 2009</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */
public class MeterLnReporterManager
    extends AbstractReporterManager
    implements LocoNetListener {

	  /**
	   * is the interface specifier.
	   */
	  private static final String MY_SPECIFIER = "M";
 		
   /*
    * return the system prefix
    */
// 		@SuppressWarnings("deprecation")
//		public String getSystemPrefix(){
// 			return ""+systemLetter(); // for now.  if more than one, this needs
// 			// to be different
// 		}

	  /**
	   * is the kind of device specifier.
	   */
	  private static final String MY_DEVICE = "R";

	  /**
	   * if the JMRI system name
	   */
	  private static final String MY_IDENTIFIER = MY_SPECIFIER + MY_DEVICE;

  /**
   * constructs the MeterReporterManager.
   */
  public MeterLnReporterManager() {
    _instance = this;
    if (MeterLnTrafficController.instance() == null) {
      new MeterLnTrafficController();
    }
  }

  public String getSystemPrefix() {
	    return MY_IDENTIFIER;
	  }

  /**
   * Method to create a new Reporter based on the system name
   * Returns null if the system name is not in a valid format or
   *    if the system name does not correspond to a configured
   *    Loconet address.
   * Assumes calling method has checked that a Reporter with this
   *    system name does not already exist
   *
   * @param systemName should be "MR" something
   * @param userName is what the user labels the new Light.
   *
   * @return a new Reporter for Loconet, with all messages going
   * through a Governor.
   */
  public Reporter createNewReporter(String systemName, String userName) {
    return new MeterLnReporter(systemName, userName);
  }
  
  /**
   *  Get the bit address from the system name
   *  @param systemName is the JMRI system name
   *  @return the integer portion of the system name - the Loconet address 
   */
  public int getBitFromSystemName (String systemName) {
    // validate the system Name leader characters
	    if ( !systemName.startsWith(MY_IDENTIFIER) ) {
	        // here if an illegal loconet reporter system name 
	        log.error("illegal character in header field of loconet reporter system name: "+systemName);
	        return (0);
	      }
    // name must be in the MRnnnnn format
    int num = 0;
    try {
      num = Integer.valueOf(systemName.substring(2)).intValue();
    }
    catch (Exception e) {
      log.error("illegal character in number field of system name: "+systemName);
      return (0);
    }
    if (num<=0) {
      log.error("invalid loconet reporter system name: "+systemName);
      return (0);
    }
    else if (num>4096) {
      log.error("bit number out of range in loconet reporter system name: "+systemName);
      return (0);
    }
    return (num);
  }
  

  @Override
  public boolean allowMultipleAdditions(String systemName) {
      return true;
  }

  @Override
  public NameValidity validSystemNameFormat(String systemName) {
      return NameValidity.VALID;
  }

  /**
   * Allow access to MeterLnReporterManager
   *
   * @return the singleton MeterLnLightManger.  If one does not exist, one
   * is created.
   */
  static public MeterLnReporterManager instance() {
    if (_instance == null) {
      _instance = new MeterLnReporterManager();
    }
    return _instance;
  }

  /**
   * reports the existence of an MeterLnReporterManager.  This would not be needed
   * if MeterLnReporterManager was integrated into JMRI.
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
          log.debug("SWITCH_REP received with address " + a);
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
      createNewReporter(s, null);
    }
  }

  static MeterLnReporterManager _instance = null;

  static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(
      MeterLnReporterManager.class.getName());

}
/* @(#)MeterLnReporterManager.java */