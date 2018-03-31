/**
 * Name: JmriPrefixManager.java
 * 
 * What:
 *    JmriPrefixManager is a singleton that holds all the JMRI
 *    prefixes being used.  JMRI prefixes identify how CATS talks
 *    to the layout and receives events from the layout.  When
 *    a prefix is registered with this singleton, it handles
 *    instantiating the various managers.  Thus, it performs
 *    a task done under JMRI in the XML files.  One additional
 *    design goal is to accumulate the manager knowledge code
 *    here so that as new DCC systems are added to JMRI, only
 *    this class needs to be changed.
 */
package cats.jmri;

import java.util.ArrayList;
import java.util.Iterator;

import cats.layout.items.IOSpecChain;

import jmri.managers.AbstractSensorManager;
import jmri.InstanceManager;
import jmri.Light;
import jmri.LightManager;
import jmri.Memory;
import jmri.MemoryManager;
import jmri.Reporter;
import jmri.ReporterManager;
import jmri.Route;
import jmri.RouteManager;
import jmri.Sensor;
import jmri.SensorManager;
import jmri.SignalHead;
import jmri.SignalHeadManager;
import jmri.Turnout;
import jmri.TurnoutManager;
import jmri.managers.ProxyLightManager;
import jmri.managers.ProxyReporterManager;
import jmri.managers.ProxySensorManager;
import jmri.managers.ProxyTurnoutManager;

/**
 *    JmriPrefixManager is a singleton that holds all the JMRI
 *    prefixes being used.  JMRI prefixes identify how CATS talks
 *    to the layout and receives events from the layout.  When
 *    a prefix is registered with this singleton, it handles
 *    instantiating the various managers.  Thus, it performs
 *    a task done under JMRI in the XML files.  One additional
 *    design goal is to accumulate the manager knowledge code
 *    here so that as new DCC systems are added to JMRI, only
 *    this class needs to be changed.
 *    <p>
 *    See the JMRI help page on "Names and Naming".
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2006, 2009, 2011</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */
public class JmriPrefixManager {
  
  /**
   * is the singleton.
   */
  public static JmriPrefixManager PrefixManager;
  
  /**
   * is the list of prefixes being used.
   */
  private ArrayList<String> Prefixes;
  
  /**
   * is the list of Managers loaded.  This is a workaround
   * because I cannot find a way to query anything about
   * what Managers are loaded.  Furthermore, the ProxyManagers
   * attempt to create an Object from the default Manager, which
   * has a bug that appends the prefix onto the System name.
   * The creation routine then triggers an exception in the 
   * "provide" routine.
   */
  private ArrayList<String> Managers;
  
  /**
   * is the list of Sensor managers.  This is needed for reading
   * back the state of the layout.
   */
  private static ArrayList<SensorManager> SMgrs = new ArrayList<SensorManager>();
  
  /**
   * is a flag indicating that the LnSensorManager has been
   * seen.  The reason for knowing this is so it is not
   * stimulated twice when reading back the state of the layout.
   */
  private static boolean LnSensorSeen = false;
  
  /**
   * the no argument ctor.  It creates the singleton.
   */
  public JmriPrefixManager() {
    Prefixes = new ArrayList<String>();
    Managers = new ArrayList<String>();
    if (PrefixManager == null) {
      PrefixManager = this;
    }
  }
  
  /**
   * searches the list of JMRI prefixes for a particular one.
   * 
   * @param prefix is the prefix being looked for
   * @return true if something is accesing such a device or
   * false if not.
   */
  public boolean isDeviceUsed(String prefix) {
    for (Iterator<String> iter = Prefixes.iterator(); iter.hasNext();) {
      if (iter.next().equals(prefix)) {
        return true;
      }
    }
    return false;
  }
  
  /**
   * adds a prefix to the list of prefixes.
   * 
   * @param prefix is the new prefix.
   */
  private void addPrefix(String prefix) {
    for (Iterator<String> iter = Prefixes.iterator(); iter.hasNext();) {
      if (iter.next().equals(prefix)) {
        return;
      }
    }
    Prefixes.add(prefix);
  }
  
  /**
   * searches the list of Managers that CATS has loaded.
   * 
   * @param manager is the Class name for a Manager.
   * 
   * @return true if CATS has loaded it; false if CATS has
   * not loaded it.  JMRi may have already loaded it,
   * so there could be two of these.
   */
  private boolean isManagerLoaded(String manager) {
    return (Managers.contains(manager));
  }
  
  /**
   * adds the Class name for a manager to the list of loaded
   * managers.  There is no checking to see that it has already
   * been laoded.
   * 
   * @param manager is the Class name (from JmriName).
   */
  private void loadManager(String manager) {
    Managers.add(manager);
  }
  
  
  /**
   * handles error reporting.
   * 
   * @param msg is the error string.
   */
  private static void errorOut(String msg) {
    System.out.println(msg);
    log.warn(msg);    
  }
  
  /**
   * looks for a chain manager.  This is hardware system
   * independent, so the first character does not matter.
   * 
   * @param prefix is the JMRI prefix
   * @param addr is the decoder address
   * 
   * @return the IOSpecChain for the address
   */
  public static IOSpecChain findChain(String prefix, String addr) {
    PrefixManager.addPrefix(prefix);
    return IOSpecChainManager.instance().provideChain(prefix + addr);
  }
  
  /**
   * looks for a signal head manager for the hardware system.
   * SignalHeads are named differently from other devices.  They
   * tend to not have the two letter prefix.  They can be anything
   * (including the two letter prefix).
   * 
   * @param name is either the system or user name of a head.
   * 
   * @return the SignalHead for the address.
   */
  public static SignalHead findHead(String name) {
    SignalHeadManager shm = InstanceManager.getDefault(jmri.SignalHeadManager.class);
    if (shm != null) {
      return shm.getSignalHead(name);
    }
    return null;
  }
  
  /**
   * looks for a light manager for the hardware system.
   * 
   * @param prefix is the JMRI prefix
   * @param addr is the decoder address
   * @param managerName is the name of the manager class
   * 
   * @return the Light for the address.
   */
  public static Light findLight(String prefix, String addr,
      String managerName) {
    LightManager lm = InstanceManager.lightManagerInstance();
    Light light;
    if (lm == null) {
      InstanceManager.setLightManager(lm = new ProxyLightManager());
    }
    if (managerName != null) {
      if (!PrefixManager.isManagerLoaded(managerName)) {
        try {
          ((ProxyLightManager) lm).addManager((LightManager)Class.forName(managerName).newInstance());
        } catch (InstantiationException e) {
          errorOut("Instantiation exception on " + managerName);
//        e.printStackTrace();
        } catch (IllegalAccessException e) {
          errorOut("IllegalAccess exception on " + managerName);
//        e.printStackTrace();
        } catch (ClassNotFoundException e) {
          errorOut("ClassNotFound exception on " + managerName);
//        e.printStackTrace();
        }
        PrefixManager.loadManager(managerName);
      }
    }
    if ((light = lm.provideLight(prefix + addr)) == null) {
      errorOut("JMRI light " + prefix +
      " is not implemented.");
    }
    PrefixManager.addPrefix(prefix);
    return light;
  }
  
  /**
   * looks for a memory object.  This is hardware system
   * independent, so the first character does not matter.
   * 
   * @param prefix is the JMRI prefix
   * @param addr is the decoder address
   * @return a Memory object for the address
   */
  public static Memory findMemory(String prefix, String addr) {
    MemoryManager mm = InstanceManager.memoryManagerInstance();
    Memory m= null;
    if (mm == null) {
      errorOut("JMRI memory manager was not created.");
    }
    else {
      m = mm.provideMemory(prefix + addr);
    }
    PrefixManager.addPrefix(prefix);
    return m;
  }
  
  /**
   * looks for a sensor for the hardware system.
   * 
   * @param prefix is the JMRI prefix
   * @param addr is the decoder address
   * @param managerName is the name of the manager class
   * 
   * @return a sensor for the address.
   */
  public static Sensor findSensor(String prefix, String addr,
      String managerName) {
    SensorManager sm = InstanceManager.sensorManagerInstance();
    SensorManager newMgr = null;
    Sensor s;
    if (sm == null) {
      InstanceManager.setSensorManager(sm = new ProxySensorManager());
    }
    if (managerName != null) {
      if (!PrefixManager.isManagerLoaded(managerName)) {
        try {
          ((ProxySensorManager) sm).addManager(newMgr = (SensorManager)Class.forName(managerName).newInstance());
        } catch (InstantiationException e) {
          errorOut("Instantiation exception on " + managerName);
        } catch (IllegalAccessException e) {
          errorOut("IllegalAccess exception on " + managerName);
        } catch (ClassNotFoundException e) {
          errorOut("ClassNotFound exception on " + managerName);
        } 
        PrefixManager.loadManager(managerName);
        
        // this remembers the SensorManagers so that they can
        // be used to read back the state of the layout
        if ((newMgr != null) && (AbstractSensorManager.class.isInstance(newMgr))) {
          if ("LS".equals(prefix) || "MS".equals(prefix)) {
            if (!LnSensorSeen) {
              LnSensorSeen = true;
//              SMgrs.add(LnSensorManager.instance());
              SMgrs.add(newMgr);
            }
          }
          else {
            SMgrs.add(newMgr);
          }
        }
      }
    }
    if ((s = sm.provideSensor(prefix + addr)) == null) {
      errorOut("JMRI sensor " + prefix + " is not implemented.");
    }
    PrefixManager.addPrefix(prefix);
    return s;
  }
  
  /**
   * looks for a turnout for the hardware system.
   * 
   * @param prefix is the JMRI prefix
   * @param addr is the decoder address
   * @param managerName is the name of the manager class
   * @return a turnout for the address.
   */
  public static Turnout findTurnout(String prefix, String addr,
      String managerName) {
    TurnoutManager tm = InstanceManager.turnoutManagerInstance();
    Turnout t;
    if (tm == null) {
    	tm = new ProxyTurnoutManager();
      InstanceManager.setTurnoutManager(tm);
    }
    if (managerName != null) {
      if (!PrefixManager.isManagerLoaded(managerName)) {
        try {
          ((ProxyTurnoutManager) tm).addManager((TurnoutManager)Class.forName(managerName).newInstance());
        } catch (InstantiationException e) {
          errorOut("Instantiation exception on " + managerName);
//        e.printStackTrace();
        } catch (IllegalAccessException e) {
          errorOut("IllegalAccess exception on " + managerName);
//        e.printStackTrace();
        } catch (ClassNotFoundException e) {
          errorOut("ClassNotFound exception on " + managerName);
//        e.printStackTrace();
        } 
        PrefixManager.loadManager(managerName);
      }
    }
    if ((t = tm.provideTurnout(prefix + addr)) == null) {
      errorOut("JMRI turnout " + prefix + " is not implemented.");
    }
    PrefixManager.addPrefix(prefix);
    return t;
  }
  
  /**
   * looks for a reporter object.  This is hardware system
   * independent, so the first character does not matter.
   * 
   * @param prefix is the JMRI prefix
   * @param addr is the decoder address
   * @param managerName is the name of the manager class
   * @return a Reporter object for the address
   */
  public static Reporter findReporter(String prefix, String addr,
      String managerName) {
    ReporterManager rm = InstanceManager.reporterManagerInstance();
    Reporter r = null;
    if (rm == null) {
      errorOut("JMRI reporter manager was not created.");
      rm = new ProxyReporterManager();
    }
    if (managerName != null) {
      if (!PrefixManager.isManagerLoaded(managerName)) {
        try {
          ((ProxyReporterManager) rm).addManager((ReporterManager)Class.forName(managerName).newInstance());
        } catch (InstantiationException e) {
          errorOut("Instantiation exception on " + managerName);
        } catch (IllegalAccessException e) {
          errorOut("IllegalAccess exception on " + managerName);
        } catch (ClassNotFoundException e) {
          errorOut("ClassNotFound exception on " + managerName);
        } 
        PrefixManager.loadManager(managerName);
      }
    }
    if ((r = rm.provideReporter(prefix + addr)) == null) {
      errorOut("JMRI sensor " + prefix + " is not implemented.");
    }
    PrefixManager.addPrefix(prefix);
    return r;
  }
  
  /**
   * looks for a route object.  This is hardware system
   * independent, so the first character does not matter.
   * 
   * @param prefix is the JMRI prefix
   * @param addr is the decoder address
   * @return a Route object for the address
   */
  public static Route findRoute(String prefix, String addr) {
    RouteManager rm = InstanceManager.routeManagerInstance();
    Route r = null;
    if (rm == null) {
      errorOut("JMRI route manager was not created.");
    }
    else {
      r = rm.getRoute(prefix + addr);
    }
    PrefixManager.addPrefix(prefix);
    return r;
  }
  
  /**
   * reads back the layout, if possible.  Currently, only the
   * LnSensor manager supports reading back.
   *
   */
  public static void readBack() {
    
    try {
      if (AbstractSensorManager.class.getMethod("updateAll", (Class <?> [] )(null)) != null) {
        for (Iterator<SensorManager> iter = SMgrs.iterator(); iter.hasNext(); ) {
          ((AbstractSensorManager) iter.next()).updateAll();
        }
      }
    } catch (SecurityException e) {
      errorOut("Security exception reading layout.");
    } catch (NoSuchMethodException e) {
      errorOut("Could not read layout - JMRI should be updated.");
    }
  }
  
  static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(
      JmriPrefixManager.class.getName());
}
