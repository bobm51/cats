/*
 * Name: SWSignalHead
 * 
 * What:
 *   This file contains a SWSignalHead.  A SWSignalHead collects
 *   some JMRI Sensors or Turnouts into a group, so that they can be
 *   treated as a JMRI SignalHead.  This is an example of the Composition
 *   pattern.
 *   <p>
 *   The components need not be homogeneous.  For example, a Loconet
 *   Turnout could be mixed with a C/MRI Sensor.  The important thing
 *   is that the components reflect how the layout is wired.
 */
package cats.jmri;

import cats.layout.FlashRate;
import cats.layout.items.AspectCommand;
import cats.layout.items.IOInterface;
import cats.layout.items.PhysicalSignal;
import jmri.implementation.DefaultSignalHead;
import jmri.InstanceManager;
import jmri.SignalHead;
import jmri.SignalHeadManager;

/**
 *   This file contains a SWSignalHead.  A SWSignalHead collects
 *   some JMRI Sensors or Turnouts into a group, so that they can be
 *   treated as a JMRI SignalHead.  This is an example of the Composition
 *   pattern.
 *   <p>
 *   The components need not be homogeneous.  For example, a Loconet
 *   Turnout could be mixed with a C/MRI Sensor.  The important thing
 *   is that the components reflect how the layout is wired.
 *   <p>
 *   I would like to use the JMRI flashing routines, but I like having
 *   the ability to set the flash rate, so I duplicated them here.
 *
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */

public class SWSignalHead extends DefaultSignalHead {

  /**
   * is the JMRI device name for an application specific
   * SignalHead.
   */
  public static final String CATS_SIGNALHEAD = "CATS:SignalHead:";
  
  /**
   * the "address" (SWSignalHead) unique identifier for
   * the next SWSignalHead created.
   */
  protected static int NextID = 1;
  
  /**
   * is the list of IOInterfaces for each Appearance,
   */
  private Mapping[] IOSpecs = {
      new Mapping(SignalHead.DARK),
      new Mapping(SignalHead.GREEN),
      new Mapping(SignalHead.YELLOW),
      new Mapping(SignalHead.RED),
      new Mapping(SignalHead.FLASHGREEN),
      new Mapping(SignalHead.FLASHYELLOW),
      new Mapping(SignalHead.FLASHRED),
      new Mapping(SignalHead.LUNAR),
      new Mapping(SignalHead.FLASHLUNAR)
  };

  /**
   * the decoder(s) currently active
   */
  protected IOInterface CurrentDecoder;
  
  /**
   * is a timer that controls the on and off periods.
   */
  private javax.swing.Timer timer = null;
  
  /**
   * Default on or off time of flashing signal
   */
  int delay = 750;

  /**
   * is a flag so a "missing decoder message" is logged only once
   */
  protected boolean sentWarning = false;
  
  /**
   * constructs a SWSignalHead
   * @param systemName is the JMRI system name
   * @param userName is the external name known by the user
   */
  public SWSignalHead(String systemName, String userName) {
    super(systemName, userName);
  }

  /**
   * constructs a SWSignalHead
   * @param sysName is the JMRI system name
   */
  public SWSignalHead(String sysName) {
    super(sysName);
  }

  /**
   * constructs a SWSignalHead using an application
   * system name, which is not visible to users.
   */
//  public SWSignalHead()
//  {
//    this(createSignalHeadName(), null);
//  }

  /**
   * Type-specific routine to handle output to the layout hardware.
   * 
   * Does not notify listeners of changes; that's done elsewhere.
   * Should use the following variables to determine what to send:
   *<UL>
   *<LI>mAppearance
   *<LI>mLit
   *<LI>mFlashOn
   *</ul>
   */
  protected void updateOutput() {
    int appearance = (mLit) ? mAppearance : SignalHead.DARK;
    if (mLit) {
      if (!mFlashOn &&
          ((mAppearance == FLASHGREEN) ||
          (mAppearance == FLASHYELLOW) ||
          (mAppearance == FLASHRED) )) {
        appearance = SignalHead.DARK;        
      }
      else {
        appearance = mAppearance;
      }
    }
    else {
      appearance = SignalHead.DARK;
    }
    if (CurrentDecoder != null) {
      CurrentDecoder.sendUndoCommand();
    }
    CurrentDecoder = findSpec(appearance).Spec;
    if (CurrentDecoder == null) {
      String msg;
      if (!sentWarning) {
        if (getUserName() != null) {
          msg = "SignalHead " + getUserName();
          ;
        }
        else {
          msg = "A SignalHead ";
        }
        log.warn(msg + " is missing the decoder definition for "
            + PhysicalSignal.toCATS(appearance));
      }
      sentWarning = true;
    }
    else {
      CurrentDecoder.sendCommand();
    }
  }
  
  /*
   * Start the timer that controls flashing
   */
  protected void startFlash() {
    delay = FlashRate.TheFlashRate.getRate();
    // note that we don't force mFlashOn to be true at the start
    // of this; that way a flash in process isn't disturbed.
    if (timer==null) {
      timer = new javax.swing.Timer(delay, new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent e) {
          timeout();
        }
      });
      timer.setInitialDelay(delay);
      timer.setRepeats(true);
    }
    timer.start();
  }

  /**
   * invoked on a timeout.  This is what turns the light on or off.
   */
  private void timeout() {
    mFlashOn = !mFlashOn;
    updateOutput();
  }
  
  /*
   * Stop the timer that controls flashing.
   *
   * This is only a resource-saver; the actual use of 
   * flashing happens elsewere
   */
  protected void stopFlash() {
      if (timer!=null) timer.stop();
      mFlashOn = true;
  }

  /**
   * locates the IOInterface that implements an appearance
   * @param appearance is the JMRI constant for the appearance
   * @return the IOInterface that sets the signal to the requested
   * appearance.  If the appearance is invalid (which should not
   * happen) or there is no IOInterface, null will be returned.
   */
  protected Mapping findSpec(int appearance) {
    for (int index = 0; index < IOSpecs.length; ++index) {
      if (IOSpecs[index].JMRIValue == appearance) {
        return IOSpecs[index];
      }
    }
  return null;    
  }
  
  /**
   * locates the IOInterface that implements the appearance
   * @param appearance is the CATS name for the appearance
   * @return the IOInterface that sets the signal to the requested
   * appearance.  If the CATS name is invalid (which should
   * not happen) or there is no IOInterface, null will be returned.
   */
  private Mapping findSpec(String appearance) {
    return findSpec(PhysicalSignal.toJMRI(appearance));
  }

  /**
   * sets the IOInterface that is used to change the SignalHead to
   * an appearance
   * @param aspectCommand is the appearance name and decoder command
   * @return true if the appearance is found (and the IOInterface
   * is set).  Return false if the appearance cannot be found.
   * Any existing IOInterface is overwritten.
   */
  public boolean addIOSpec(AspectCommand aspectCommand) {
    String msg;
    String label = aspectCommand.getPresentationLabel();
    if (label != null) {
      Mapping found = findSpec(label);
      if (found != null) {
        found.Spec = aspectCommand.getCommands();
        return true;
      }
      msg = "illegal aspect name: " + label;
      log.warn(msg);
    }
    msg = "missing aspect name in AspectCommand";
    log.warn(msg);
    return false;
  }

  /**
   * this method creates a unique JMRI system name for a
   * SignalHead.  It starts with the CATS prefix, and
   * appends a number.  It checks the SignalHeadManger to
   * determine if a SignalHead with that name already
   * exists.  If so, it increments the counter and tries
   * again.
   * 
   * @return an unused CATS SignalHead JMRI system name,
   */
  static public String createSignalHeadName()
  {
    SignalHeadManager shm = InstanceManager.getDefault(jmri.SignalHeadManager.class);
    SignalHead s;
    String name = CATS_SIGNALHEAD + (NextID++);
    while (((s = shm.getSignalHead(name)) != null) && (s.getUserName() != null)) {
      name = CATS_SIGNALHEAD + (NextID++);
    }
    return name;
  }
  
  /**
   * is an internal class for associating a JRMI appearance value
   * (@see jmri.SignalHead.java) with an IOInterface.
   */
  protected class Mapping {
    
    /**
     * is the JMRI appearance value constant
     */
    public int JMRIValue;
    
    /**
     * are the instructions for changing the signal head to that appearance
     */
    public IOInterface Spec;

    /**
     * is the constructor - IOInterfaces are added later.
     * @param key
     */
    public Mapping(int key) {
      JMRIValue = key;
    }
  }
  static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(SWSignalHead.class.getName());

}
/* @(#)SWSignalHead.java */