// MeterLnSensorManager.java

package cats.jmri;

import jmri.Sensor;
import jmri.jmrix.loconet.LnConstants;
import jmri.jmrix.loconet.LnTrafficController;
import jmri.jmrix.loconet.LocoNetMessage;

/**
 * Manage the LocoNet-specific Sensor implementation.
 *
 * System names are "MSnnn", where nnn is the sensor number without padding.
 * This code is derived from ther JMRI LnSensorManager.
 *
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2004, 2009</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */
public class MeterLnSensorManager extends jmri.managers.AbstractSensorManager {

  /**
   * is the system interface identifier.
   */
    public String getSystemPrefix() {
        return "M";
      }
 	
   /*
    * return the system prefix
    */
// 	@SuppressWarnings("deprecation")
//	public String getSystemPrefix(){
// 		return ""+systemLetter(); // for now.  if more than one, this needs
// 		// to be different
// 	}
    
    private boolean busy = false;

    /**
     * check for the existance of a Manager.
     *
     * @return the manager.  If one does not exist, create it.
     */
    static public MeterLnSensorManager instance() {
//        if (mInstance == null) new MeterLnSensorManager();
        return mInstance;
    }

    /**
     * is the singleton manager.
     */
    static private MeterLnSensorManager mInstance = null;

    // to free resources when no longer used
    public void dispose() {
    }

    // LocoNet-specific methods

    public Sensor createNewSensor(String systemName, String userName) {
        return new MeterLnSensor(systemName, userName);
    }

    // ctor has to register for LocoNet events
    /**
     * constructor.
     */
    public MeterLnSensorManager() {
        mInstance = this;
    }
    
    /**
     * Requests status updates from all layout sensors.
   */
  public void updateAll() {
    if (!busy) {
      setUpdateBusy();
      SensorUpdateThread thread = new SensorUpdateThread(this);
      thread.setName("LnSensors");
      thread.start();
    }
  }

    /**
     * Method to set Route busy when commands are being issued to 
     *   Route turnouts
   */
    public void setUpdateBusy() {
    busy = true;
  }

    /**
     * Method to set Route not busy when all commands have been
     *   issued to Route turnouts
   */
    public void setUpdateNotBusy() {
    busy = false;
  }
  
    static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(MeterLnSensorManager.class.getName());

}

/**
 * Class providing a thread to update sensor states
 */
class SensorUpdateThread extends Thread
{
  /**
   * Constructs the thread
   * @param sensorManager is the event distributor
   */
  public SensorUpdateThread (MeterLnSensorManager sensorManager) {
    sm = sensorManager;
    tc = MeterLnTrafficController.instance();
  }
  
  /** 
   * Runs the thread - sends 8 commands to query status of all stationary sensors
   *     per LocoNet PE Specs, page 12-13
   * Thread waits 500 msec between commands after a 2 sec initial wait.
   */
  public void run () {
    byte sw1[] = {0x78,0x79,0x7a,0x7b,0x78,0x79,0x7a,0x7b};
    byte sw2[] = {0x27,0x27,0x27,0x27,0x07,0x07,0x07,0x07};
    // create and initialize loconet message
        LocoNetMessage m = new LocoNetMessage(4);
        m.setOpCode(LnConstants.OPC_SW_REQ);
    for (int k = 0; k < 8; k++) {
      try {
        Thread.sleep(1000);
      }
      catch (InterruptedException e) {
        break;
      }
      m.setElement(1,sw1[k]);
      m.setElement(2,sw2[k]);
      tc.sendLocoNetMessage(m);
    }
    sm.setUpdateNotBusy();
  }
  
  private MeterLnSensorManager sm = null;
  private LnTrafficController tc = null;

}

/* @(#)MeterLnSensorManager.java */
