/**
 * Name: IOSpec.java
 *
 * What:
 *  This is a class for holding the information for triggering something
 *  on the railroad or identifying an event from the railroad.
 */
package cats.layout.items;

import cats.gui.DecoderInterlock;
import cats.jmri.JmriName;
import cats.jmri.JmriPrefixManager;
import cats.layout.DecoderObserver;
import cats.layout.xml.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import jmri.JmriException;
import jmri.Light;
import jmri.Memory;
import jmri.Reporter;
import jmri.Route;
import jmri.Sensor;
import jmri.SignalHead;
import jmri.Turnout;

/**
 * is a class for holding the information needed to trigger something
 * on the railroad or identifying an event from the railroad.
 * <p>
 * Todo: Verify that objects defined by PanelPro can be accessed.
 * <p>
 * This class implements the facade design pattern by hiding the type
 * of the various inputs from the railroad.
 *
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2004, 2006, 2009</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */

public class IOSpec
implements IOInterface, XMLEleObject {
  
  /**
   * is the tag for indentifying an IOSpec in the XMl file.
   */
  static final String XML_TAG = "IOSPEC";
  
  /**
   * is the attribute tag for identifying the decoder address.
   */
  static final String DECADDR = "DECADDR";
  
  /**
   * is the attribute tag for identifying the Loconet message type.
   */
  static final String JMRIPREFIX = "JMRIPREFIX";
  
  /**
   * is the XML tag for the UserName attribute.
   */
  public static final String USER_NAME = "USER_NAME";
  
  /**
   * is the text string for a Throw command.
   */
  public static final String THROW_TEXT = "throw";
  
  /**
   * is the text string for a Close command.
   */
  public static final String CLOSE_TEXT = "close";
  
  /**
   * is the XML tag for explicitly sending a command when exiting
   * a state.
   */
  public static final String EXIT_CMD = "EXIT_CMD";

  /**
   * is the XML tag for the delay between commands.
   */
  public static final String DELAY = "DELAY";

  /**
   * is the kind of trigger or event.  True is a "throw" and false is
   * a "close".
   */
  private boolean ThrowCmd;
  
  /**
   * is the address to send the trigger to.
   */
  private String DecAddr;

  /**
   * is the number of milliseconds to wait after starting the
   * chain before starting the next command in the parent chain.
   */
  private int Delay;
  
  /**
   * is the JMRI user name
   */
  private String UserName;
  
  /**
   * is the JMRI prefix name used for finding the specific decoder.
   */
  protected String JmriPrefix;
  
  /**
   * is the throw/close state of the IOSpec
   */
  private String State;
  
  /**
   * is true if a command should be sent when the state is exited.
   * Some devices (e.g. SE8C) have mutually exclusive states (e.g one
   * command turns all LEDS off except for the green) so the active
   * output is turned off when the state changes.  Other devices have
   * a state for each output, so one output must be deactivated before
   * the next is activated.  ExitCmd should be false for the former class
   * of devices and true for the latter.
   */
  private boolean ExitCmd = false;
  
  /**
   * is the object that is controlled by the IOSpec.
   */
  private IOSpecAdapter MyAdapter;
  
  /**
   * is the object that wants to be notified when the event happens.
   */
  private DecoderObserver Observer;
  
  /**
   * the constructor for the XML reader.
   *
   * @param address is the address the command should be sent to.
   * @param name is the JMRI user name.  It can be null.
   */
  public IOSpec(String address, String name) {
    DecAddr = address;
    UserName = name;
  }
  
  /**
   * saves the JMRI prefix name.
   *
   * @param prefix identifies the kind of decoder being defined.
   */
  public void setPrefix(String prefix) {
    JmriPrefix = prefix;
  }
  
  /**
   * retrieves the JMRI prefix.
   *
   * @return the kind of decoder.
   */
  public String getPrefix() {
    return JmriPrefix;
  }

  /**
   * replaces the Delay value.
   * 
   * @param d is the new delay in milliseconds.
   */
  public void setDelay(int d) {
      Delay = d;
  }
  
  /**
   * retrieves the Delay value.
   * 
   * @return the delay in milliseconds after the previous command
   *  in a chain.
   */
  public int getDelay() {
      return Delay;
  }
  
  /**
   * sends a command to the decoder to set it to the state specified.
   */
  public void sendCommand() {
    MyAdapter.doCommand(ThrowCmd);
  }
  
  /**
   * sends the deactivate command (if it exists).
   */
  public void sendUndoCommand() {
    if (ExitCmd) {
      MyAdapter.doCommand(!ThrowCmd);
    }
  }
  
  /**
   * forces the decoder into a particular state.
   * 
   * @param state is the state (Throw or Close) to send the decoder to.
   */
  public void forceState(boolean state) {
    MyAdapter.doCommand(state);
  }
  
  /**
   * sets the flag designating if a command should be sent when the
   * active state is exited or not.
   * @param flag is true if a command should be sent when the active
   * state is turned off and false when a command should not be sent.
   */
  public void setExitFlag(boolean flag) {
    ExitCmd = flag;
  }
  
  /**
   * retrieves the decoder's name, which is used to identify it to the
   * control structure.  If one has not been defined, MS will be used.
   *
   * @return the name of the decoder.
   */
  public String getName() {
    if (JmriPrefix == null) {
      JmriPrefix = new String("MS");
    }
    return new String(JmriPrefix + DecAddr);
  }
  
  /**
   * retrieves the JMRI User Name.
   * 
   * @return the JMRI User Name fo rthe Chain.
   */
  public String getUserName() {
    return new String(UserName);
  }
  
  /**
   * saves the JMRI User Name.
   * @param name is the User defined name.
   */
  public void setUserName(String name) {
    UserName = name;
  }

  /**
   * returns the polarity of the decoder
   * 
   * @return the "State" string
   */
  public String getPolarity() {
    return new String(State);
  }
  
  /**
   * registers a listener with the appropriate JMRI Sensor or Turnout.
   * This method uses the facade pattern to make disparate objects look
   * the same.
   *
   * @param listener is the object which wants to know when the state of
   * the Sensor or Turnout changes.
   */
  public void registerListener(DecoderObserver listener) {
    Observer = listener;
    if (JmriPrefix == null) {
      JmriPrefix = "MS";
    }
    JmriName jName = JmriName.getName(JmriPrefix);
    String jType;
    if (jName != null) {
      jType = jName.getJmriType();
      if (jType.equals(JmriName.HEAD)) {
        new HeadMonitor(JmriPrefix, DecAddr, listener, (State.equals(THROW_TEXT)));      
      }
      else if (jType.equals(JmriName.LIGHT)) {
        new LightMonitor(JmriPrefix, DecAddr,
            jName.getJmriClass(), listener, (State.equals(THROW_TEXT)));
      }
      else if (jType.equals(JmriName.MEMORY)) {
        new MemoryMonitor(JmriPrefix, DecAddr, listener, (State.equals(THROW_TEXT)));
      }
      else if (jType.equals(JmriName.REPORTER)) {
        new ReporterMonitor(JmriPrefix, DecAddr,
            jName.getJmriClass(), listener, (State.equals(THROW_TEXT)));
      }
      else if (jType.equals(JmriName.SENSOR)) {
        new SensorMonitor(JmriPrefix, DecAddr,
            jName.getJmriClass(), listener, (State.equals(THROW_TEXT)));
      }
      else if (jType.equals(JmriName.TURNOUT)) {
        new TurnOutMonitor(JmriPrefix, DecAddr,
            jName.getJmriClass(), listener, (State.equals(THROW_TEXT)));
      }  
    }
  }

  /**
   * Locks the decoder command while the route is locked through a turnout so that
   * no other decoder can move the points.
   */
  public void lockOutCommand() {
    if (DecoderInterlock.TheInterlockType.getFlagValue()) {
      LockedDecoders.BlackList.lockUpDecoder(getName(), ThrowCmd);
    }
  }
  
  /**
   * Unlocks the decoder command when a route through a turnout has cleared.  The points
   * can again be moved.
   */
  public void unlockOutCommand() {
    if (DecoderInterlock.TheInterlockType.getFlagValue()) {
      LockedDecoders.BlackList.releaseDecoder(getName(), ThrowCmd);
    }
  }
  
  /**
   * tests if the decoder command has been locked out.
   * @return true if the command should not be sent. False if it is safe to send it.
   */
  public boolean isLockedOut() {
    if (DecoderInterlock.TheInterlockType.getFlagValue()) {
      return LockedDecoders.BlackList.isLocked(getName(), ThrowCmd);
    }
    return false;
  }

  /**
   * registers the decoder with the LockedDecoder as a candidate for having the lock
   * treatment.
   */
  public void registerLock() {
    LockedDecoders.BlackList.registerDecoder(getName(), ThrowCmd);
  }

  /**
   * compares two IOSpecs for equality
   *
   * @param test is the IOSpec being tested for being equal.
   *
   * @return true if the commands and addresseses are the same.
   */
  public boolean equals(IOSpec test) {
    if (test != null) {
      return ( (test.DecAddr.equals(DecAddr)) && (test.ThrowCmd == ThrowCmd));
    }
    return false;
  }
  
  /*********************************************************
   *   the following inner classes are for outputs
   *********************************************************/  
  
  /**
   * is a private interface that provides the facade for the various
   * kinds of DCC objects on the layout.
   */
  private interface IOSpecAdapter {
    
    /**
     * is for output devices.
     *
     * @param position is the setting of the light/turnout/sensor
     */
    public void doCommand(boolean position);
    
  }
  
  /**
   * is an inner class for executing a Chain of commands.  Note that
   * there is no undo command.
   */
  private class ChainAdapter
  implements IOSpecAdapter {
    
    /**
     * is the Chain.
     */
    IOSpecChain MyChain;
    
    /**
     * is the constructor.
     *
     * @param hwName is the hardware name of the Chain being controlled.
     * @param addr is the decoder address of the Chain being controlled.
     */
    public ChainAdapter(String hwName, String addr) {
      MyChain = JmriPrefixManager.findChain(hwName, addr);
    }
    
    public void doCommand(boolean position) {
      if (MyChain != null) {
        if (position) {
          MyChain.sendUndoCommand();
        }
        else {
          MyChain.sendCommand();
        }
      }
    }
  }
  
  /**
   * is an inner class for setting lights.
   */
  private class LightAdapter
  implements IOSpecAdapter {
    
    /**
     * is the Light.
     */
    Light MyLight;
    
    /**
     * is the constructor.
     *
     * @param hwName is the hardware name of the light being controlled.
     * @param addr is the decoder address of the light being controlled.
     * @param mgrName is the JMRI class name for the Light manager.
     */
    public LightAdapter(String hwName, String addr, String mgrName) {
      MyLight = JmriPrefixManager.findLight(hwName, addr, mgrName);
      if ((MyLight != null) &&(UserName != null) &&
          (UserName.length() > 0)) {
        MyLight.setUserName(UserName);
      }
    }
    
    public void doCommand(boolean position) {
      if (MyLight != null) {
        MyLight.setState(position ? Light.OFF : Light.ON);
      }
    }
  }
  
  /**
   * is an inner class for setting memory events.
   */
  private class MemoryAdapter
  implements IOSpecAdapter {
    
    /**
     * is the Memory object.
     */
    Memory MyMemory;
    
    /**
     * is the constructor.
     *
     * @param hwName is the hardware name of the Memory being controlled.
     * @param addr is the decoder address of the Memory being controlled.
     */
    public MemoryAdapter(String hwName, String addr) {
      MyMemory = JmriPrefixManager.findMemory(hwName, addr);
      if ((MyMemory != null) && (UserName != null) &&
          (UserName.length() > 0)) {
        MyMemory.setUserName(UserName);
      }
    }
    
    public void doCommand(boolean position) {
      if (MyMemory != null) {
        try {
          MyMemory.setState(position ? 1 : 0);
        } catch (JmriException e) {
          System.out.println("JMRI exception in setting Memory "
              + getName());
        }
      }
    }
  }
  
  /**
   * is an inner class for setting Reporter events.  It has
   * not been implemented because Reporter and Route are both
   * identified with 'R'.
   */
  private class ReporterAdapter
  implements IOSpecAdapter {
    
    /**
     * is the Reporter.
     */
    Reporter MyReporter;
    
    /**
     * is the constructor.
     *
     * @param hwName is the hardware name of the Reporter being controlled.
     * @param addr is the decoder address of the Reporter being controlled.
     * @param mgrName is the JMRI class name for the Light manager.
     */
    public ReporterAdapter(String hwName, String addr, String mgrName) {
      MyReporter = JmriPrefixManager.findReporter(hwName, addr, mgrName);
      if ((MyReporter != null) && (UserName != null) &&
          (UserName.length() > 0)) {
        MyReporter.setUserName(UserName);
      }
    }
    
    public void doCommand(boolean position) {
      if (MyReporter != null) {
        try {
          MyReporter.setState(position ? 1 : 0);
        } catch (JmriException e) {
          System.out.println("JMRI exception in setting Reporter "
              + getName());
        }
      }
    }
  }
  
  /**
   * is an inner class for setting Routes.
   */
  private class RouteAdapter
  implements IOSpecAdapter {
    
    /**
     * is the Route.
     */
    Route MyRoute;
    
    /**
     * is the constructor.
     *
     * @param hwName is the hardware name of the Route being controlled.
     * @param addr is the decoder address of the Route being controlled.
     */
    public RouteAdapter(String hwName, String addr) {
      MyRoute = JmriPrefixManager.findRoute(hwName, addr);
      if ((MyRoute != null) && (UserName != null) &&
          (UserName.length() > 0)) {
        MyRoute.setUserName(UserName);
      }
    }
    
    public void doCommand(boolean position) {
      if (MyRoute != null) {
        MyRoute.setRoute();
      }
    }
  }
  
  /**
   * is an inner class for setting sensors.
   */
  private class SensorAdapter
  implements IOSpecAdapter {
    
    /**
     * is the Light.
     */
    Sensor MySensor;
    
    /**
     * is the constructor.
     *
     * @param sensorName is the hardware name of the sensor being controlled.
     * @param addr is the decoder address
     * @param mgrName is the JMRI class name for the sensor manager
     */
    public SensorAdapter(String sensorName, String addr,
        String mgrName) {
      MySensor = JmriPrefixManager.findSensor(sensorName, addr, mgrName);
      if ((MySensor != null) && (UserName != null) &&
          (UserName.length() > 0)) {
        MySensor.setUserName(UserName);
      }
    }
    
    public void doCommand(boolean position) {
      if (MySensor != null) {
        try {
          MySensor.setKnownState(position ? Sensor.INACTIVE : Sensor.ACTIVE);
        }
        catch (jmri.JmriException je) {
          String except = "Caught JmriException sending a command to " +
          MySensor.getSystemName();
          System.out.println(except);
        }
      }
    }
  }
  
  /**
   * is an inner class for setting Turnouts.
   */
  private class TurnOutAdapter
  implements IOSpecAdapter {
    
    /**
     * is the Light.
     */
    Turnout MyTurnout;
    
    /**
     * is the constructor.
     *
     * @param turnOutName is the hardware name of the turnout being controlled.
     * @param addr is the decoder address of the turnout being controlled.
     * @param mgrName is the JMRI class name for the turnout manager.
     */
    public TurnOutAdapter(String turnOutName, String addr,
        String mgrName) {
      MyTurnout = JmriPrefixManager.findTurnout(turnOutName, addr,
          mgrName);
      if ((MyTurnout != null) && (UserName != null) &&
          (UserName.length() > 0)) {
        MyTurnout.setUserName(UserName);
      }
    }
    
    public void doCommand(boolean position) {
      if (MyTurnout != null) {
        log.debug("Setting turnout " + MyTurnout.getSystemName() + " to " + 
            (position ? "throw" : "close"));
        MyTurnout.setCommandedState(position ? Turnout.THROWN : Turnout.CLOSED);
      }
    }
  }
  
/*********************************************************
 *   the following inner classes are for inputs
 *********************************************************/  
  /**
   * is an inner class for monitoring changes in a JMRI SignalHead.
   */
  private class HeadMonitor
  implements PropertyChangeListener {
    
    /**
     * is the SignalHead being monitored.
     */
    SignalHead MyHead;
    
    /**
     * is the state of the monitored Sensor that triggers an
     * event to the listener.
     */
    int Trigger;
    
    /**
     * is the constructor.
     *
     * @param headName is the system name of the SignalHead being
     * monitored.
     * @param addr is the decoder address
     * @param listener is the CATS object which wants to be told
     * when the sensor changes to a particular value.
     * @param trigger is the value that triggers further action.
     *
     */
    public HeadMonitor(String headName, String addr, 
        DecoderObserver listener,  boolean trigger) {
      
      MyHead = JmriPrefixManager.findHead(headName + addr);
      if (MyHead != null) {
        if ((UserName != null) && (UserName.length() > 0)) {
          MyHead.setUserName(UserName);
        }
        MyHead.addPropertyChangeListener(this);
        Trigger = (trigger) ? SignalHead.GREEN : SignalHead.RED;
      }
    }
    
    /**
     * receives the change from the railroad, sees if it is the change
     * that is being watched for, and sends it to the listener.
     *
     * @param evt is describes the JMRI object that changed.
     */
    public void propertyChange(PropertyChangeEvent evt) {
      if (evt.getPropertyName().equals("Aspects")) {
        Integer newValue = (Integer) evt.getNewValue();
        if (newValue.intValue() == Trigger) {
          Observer.acceptIOEvent();
        }
      }
    }
  }
  
  /**
   * is an inner class for monitoring changes in a JMRI light.
   */
  private class LightMonitor
  implements PropertyChangeListener {
    
    /**
     * is the sensor being monitored.
     */
    Light MyLight;
    
    /**
     * is the state of the monitored Light that triggers an
     * event to the listener.
     */
    int Trigger;
    
    /**
     * is the constructor.
     *
     * @param lightName is the system name of the Light being
     * monitored.
     * @param addr is the decoder address being monitored
     * @param mgrName is the JMRI class name for the light manager
     * @param listener is the CATS object which wants to be told
     * when the sensor changes to a particular value.
     * @param trigger is the value that triggers further action.
     *
     */
    public LightMonitor(String lightName, String addr,
        String mgrName, DecoderObserver listener, boolean trigger) {
      MyLight = JmriPrefixManager.findLight(lightName, addr, mgrName);
      if (MyLight != null) {
        if ((UserName != null) && (UserName.length() > 0)) {
          MyLight.setUserName(UserName);
        }
        MyLight.addPropertyChangeListener(this);
        Trigger = (trigger) ? Light.OFF : Light.ON;
      }
    }
    
    /**
     * receives the change from the railroad, sees if it is the change
     * that is being watched for, and sends it to the listener.
     *
     * @param evt is describes the JMRI object that changed.
     */
    public void propertyChange(PropertyChangeEvent evt) {
      if (evt.getPropertyName().equals("KnownState")) {
        Integer newValue = (Integer) evt.getNewValue();
        if (newValue.intValue() == Trigger) {
          Observer.acceptIOEvent();
        }
      }
    }
  }
  
  /**
   * is an inner class for monitoring changes in a JMRI Memory Object.
   */
  private class MemoryMonitor
  implements PropertyChangeListener {
    
    /**
     * is the Memory Object being monitored.
     */
    Memory MyMemory;
    
    /**
     * is the state of the monitored Memory that triggers an
     * event to the listener.
     */
    String Trigger;
    
    /**
     * is the constructor.
     *
     * @param memoryName is the system name of the Memory being
     * monitored.
     * @param addr is the decoder address being monitored
     * @param listener is the CATS object which wants to be told
     * when the sensor changes to a particular value.
     * @param trigger is the value that triggers further action.
     *
     */
    public MemoryMonitor(String memoryName, String addr,
        DecoderObserver listener, boolean trigger) {
      MyMemory = JmriPrefixManager.findMemory(memoryName, addr);
      if (MyMemory != null) {
        if ((UserName != null) && (UserName.length() > 0)) {
          MyMemory.setUserName(UserName);
        }
        MyMemory.addPropertyChangeListener(this);
        Trigger = (trigger ? "1" : "0");
      }
    }
    
    /**
     * receives the change from the railroad, sees if it is the change
     * that is being watched for, and sends it to the listener.
     *
     * @param evt is describes the JMRI object that changed.
     */
    public void propertyChange(PropertyChangeEvent evt) {
      if (evt.getPropertyName().equals("value")) {
        String newValue = (String) (evt.getNewValue());
        if ((newValue != null)&& (Trigger.equals(newValue))) {
          Observer.acceptIOEvent();
        }
      }
    }
  }
  
  /**
   * is an inner class for monitoring changes in a JMRI Reporter Object.
   */
  private class ReporterMonitor
  implements PropertyChangeListener {
    
    /**
     * is the Reporter Object being monitored.
     */
    Reporter MyReporter;
    
    /**
     * is the state of the monitored Reporter that triggers an
     * event to the listener.
     */
    int Trigger;
    
    /**
     * is the constructor.
     *
     * @param reporterName is the system name of the Reporter being
     * monitored.
     * @param addr is the decoder address being monitored
     * @param mgrName is the JMRI class name for the light manager
     * @param listener is the CATS object which wants to be told
     * when the sensor changes to a particular value.
     * @param trigger is the value that triggers further action.
     *
     */
    public ReporterMonitor(String reporterName, String addr,
        String mgrName, DecoderObserver listener, boolean trigger) {
      MyReporter = JmriPrefixManager.findReporter(reporterName, addr,
          mgrName);
      if (MyReporter != null) {
        if ((UserName != null) && (UserName.length() > 0)) {
          MyReporter.setUserName(UserName);
        }
        MyReporter.addPropertyChangeListener(this);
        Trigger = (trigger) ? 1 : 0;
      }
    }
    
    /**
     * receives the change from the railroad, sees if it is the change
     * that is being watched for, and sends it to the listener.
     *
     * @param evt is describes the JMRI object that changed.
     */
    public void propertyChange(PropertyChangeEvent evt) {
      if (evt.getPropertyName().equals("currentReport")) {
        if (MyReporter.getState() == Trigger) {
          Observer.acceptIOEvent();
        }
      }
    }
  }
    
  /**
   * is an inner class for monitoring changes in a JMRI sensor.
   */
  private class SensorMonitor
  implements PropertyChangeListener {
    
    /**
     * is the sensor being monitored.
     */
    Sensor MySensor;
    
    /**
     * is the state of the monitored Sensor that triggers an
     * event to the listener.
     */
    int Trigger;
    
    /**
     * is the constructor.
     *
     * @param sensorName is the system name of the Sensor being
     * monitored.
     * @param addr is the decoder address
     * @param mgrName is the JMRI class name for the sensor manager
     * @param listener is the CATS object which wants to be told
     * when the sensor changes to a particular value.
     * @param trigger is the value that triggers further action.
     *
     */
    public SensorMonitor(String sensorName, String addr, 
        String mgrName, DecoderObserver listener,  boolean trigger) {
      
      MySensor = JmriPrefixManager.findSensor(sensorName, addr, mgrName);
      if (MySensor != null) {
        if ((UserName != null) && (UserName.length() > 0)) {
          MySensor.setUserName(UserName);
        }
        MySensor.addPropertyChangeListener(this);
        Trigger = (trigger) ? Sensor.INACTIVE : Sensor.ACTIVE;
      }
    }
    
    /**
     * receives the change from the railroad, sees if it is the change
     * that is being watched for, and sends it to the listener.
     *
     * @param evt is describes the JMRI object that changed.
     */
    public void propertyChange(PropertyChangeEvent evt) {
      if (evt.getPropertyName().equals("KnownState")) {
        Integer newValue = (Integer) evt.getNewValue();
        if (newValue.intValue() == Trigger) {
          Observer.acceptIOEvent();
        }
      }
    }
  }
  
  /**
   * is an inner class for monitoring changes in a JMRI turnout.
   */
  private class TurnOutMonitor
  implements PropertyChangeListener {
    
    /**
     * is the turnout being monitored.
     */
    Turnout MyTurnOut;
    
    /**
     * is the state of the monitored turnout that triggers an
     * event to the listener.
     */
    int Trigger;
    
    /**
     * is the constructor.
     *
     * @param turnoutName is the system name of the TurnOut being
     * monitored.
     * @param addr is the decoder address being monitored
     * @param mgrName is the JMRI clas name for the turnout manager
     * @param listener is the CATS object which wants to be told
     * when the turnout changes to a particular value.
     * @param trigger is the value that triggers further action.
     *
     */
    public TurnOutMonitor(String turnoutName, String addr,
        String mgrName, DecoderObserver listener, boolean trigger) {
      MyTurnOut = JmriPrefixManager.findTurnout(turnoutName, addr,
          mgrName);
      if (MyTurnOut != null) {
        if ((UserName != null) && (UserName.length() > 0)) {
          MyTurnOut.setUserName(UserName);
        }
        MyTurnOut.addPropertyChangeListener(this);
        Trigger = (trigger) ? Turnout.THROWN : Turnout.CLOSED;
      }
    }
    
    /**
     * receives the change from the railroad, sees if it is the change
     * that is being watched for, and sends it to the listener.
     *
     * @param evt is describes the JMRI object that changed.
     */
    public void propertyChange(PropertyChangeEvent evt) {
      if (evt.getPropertyName().equals("KnownState")) {
        Integer newValue = (Integer) evt.getNewValue();
        if (newValue.intValue() == Trigger) {
          Observer.acceptIOEvent();
        }
      }
    }
  }
  /******************************************************
   * End of the private classes.
   ******************************************************/
  
  /*
   * is the method through which the object receives the text field.
   *
   * @param eleValue is the Text for the Element's value.
   *
   * @return if the value is acceptable, then null; otherwise, an error
   * string.
   */
  public String setValue(String eleValue) {
    State = new String(eleValue);
    return null;
  }
  
  /*
   * is the method through which the object receives embedded Objects.
   *
   * @param objName is the name of the embedded object
   * @param objValue is the value of the embedded object
   *
   * @return null if the Object is acceptible or an error String
   * if it is not.
   */
  public String setObject(String objName, Object objValue) {
    return new String("A " + XML_TAG + " cannot contain an Element ("
        + objName + ").");
  }
  
  /*
   * returns the XML Element tag for the XMLEleObject.
   *
   * @return the name by which XMLReader knows the XMLEleObject (the
   * Element tag).
   */
  public String getTag() {
    return new String(XML_TAG);
  }
  
  /*
   * tells the XMLEleObject that no more setValue or setObject calls will
   * be made; thus, it can do any error chacking that it needs.
   *
   * @return null, if it has received everything it needs or an error
   * string if something isn't correct.
   */
  public String doneXML() {
    String resultMsg = null;
    String sysName = getName();
    JmriName name = JmriName.getName(JmriPrefix);
    
    if (name != null) {
      if (name.getJmriType().equals(JmriName.CHAIN)) {
        MyAdapter = new ChainAdapter(JmriPrefix, DecAddr);      
      }
      else if (name.getJmriType().equals(JmriName.LIGHT)) {
        MyAdapter = new LightAdapter(JmriPrefix, DecAddr,
            name.getJmriClass());      
      } 
      else if (name.getJmriType().equals(JmriName.MEMORY)) {
        MyAdapter = new MemoryAdapter(JmriPrefix, DecAddr);      
      }
      else if (name.getJmriType().equals(JmriName.REPORTER)) {
        MyAdapter = new ReporterAdapter(JmriPrefix, DecAddr,
            name.getJmriClass());      
      }
      else if (name.getJmriType().equals(JmriName.ROUTE)) {
        MyAdapter = new RouteAdapter(JmriPrefix, DecAddr);      
      }
      else if (name.getJmriType().equals(JmriName.SENSOR)) {
        MyAdapter = new SensorAdapter(JmriPrefix, DecAddr,
            name.getJmriClass());      
      }
      else if (name.getJmriType().equals(JmriName.TURNOUT)) {
        MyAdapter = new TurnOutAdapter(JmriPrefix, DecAddr,
            name.getJmriClass());      
      }
      else {
        resultMsg = new String(sysName + " is not implemented as an output.");
      }
    }
    else {
      resultMsg = new String(sysName + " is not known.");
    }
    if (THROW_TEXT.equals(State)) {
      ThrowCmd = true;
    }
    else if (CLOSE_TEXT.equals(State)) {
      ThrowCmd = false;
    }
    else {
      resultMsg = new String("An " + XML_TAG + " cannot contain a text field ("
          + State + ").");
    }
    return resultMsg;
  }
  
  /**
   * registers an IOSpecFactory with the XMLReader.
   */
  static public void init() {
    XMLReader.registerFactory(XML_TAG, new IOSpecFactory());
    new LockedDecoders();
  }
  
  static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(
      IOSpec.class.getName());
}

/**
 * is a Class known only to the IOSpec class for creating IOSpecs from
 * an XML document.
 */
class IOSpecFactory
implements XMLEleFactory {
  
  /**
   * is the address to send the command to.
   */
  private String Recipient;
  
  /**
   * is the JMRI name prefix
   */
  private String Prefix;
  
  /**
   * is a flag for holding the exit command.
   */
  boolean ExitFlag;
  
  /**
   * is the JMRI user name
   */
  private String JMRIUserName;

  /**
   * is used when this IOSpec is in a chain to specify the number
   * of milliseconds to wait before executing the next link in
   * the chain.
   */
  int Delay;
  
  /*
   * tells the factory that an XMLEleObject is to be created.  Thus,
   * its contents can be set from the information in an XML Element
   * description.
   */
  public void newElement() {
    Recipient = "";
    ExitFlag = false;
    Prefix = null;
    JMRIUserName = null;
    Delay = 0;
  }
  
  /*
   * gives the factory an initialization value for the created XMLEleObject.
   *
   * @param tag is the name of the attribute.
   * @param value is it value.
   *
   * @return null if the tag:value are accepted; otherwise, an error
   * string.
   */
  public String addAttribute(String tag, String value) {
    String resultMsg = null;
    
    if (IOSpec.DECADDR.equals(tag)) {
      Recipient = value;
    }
    else if (IOSpec.JMRIPREFIX.equals(tag)) {
      Prefix = value;
    }
    else if (IOSpec.DELAY.equals(tag)) {
      try {
          int addr = Integer.parseInt(value);
          Delay = addr;
        }
        catch (NumberFormatException nfe) {
          System.out.println("Illegal delay for " + IOSpec.XML_TAG
                             + "XML Element:" + value);
        }        
  }
    else if (IOSpec.USER_NAME.equals(tag)) {
      JMRIUserName = new String(value);
    }
    else if (IOSpec.EXIT_CMD.equals(tag)) {
      if (Block.TRUE.equals(value)) {
        ExitFlag = true;
      }
    }
    else {
      resultMsg = new String("A " + IOSpec.XML_TAG +
          " XML Element cannot have a " + tag +
      " attribute.");
    }
    return resultMsg;
  }
  
  /*
   * tells the factory that the attributes have been seen; therefore,
   * return the XMLEleObject created.
   *
   * @return the newly created XMLEleObject or null (if there was a problem
   * in creating it).
   */
  public XMLEleObject getObject() {
    IOSpec spec = new IOSpec(Recipient, JMRIUserName);
    if (Prefix != null) {
      spec.setPrefix(Prefix);
    }
    spec.setExitFlag(ExitFlag);
    spec.setUserName(JMRIUserName);
    spec.setDelay(Delay);
    return spec;
  }
  
  static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(
      IOSpec.class.getName());
  
}
/* @(#)IOSpec.java */