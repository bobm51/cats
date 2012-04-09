/*
 * Name: HWSignalHead
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
 *   <p>
 *   This class is different from SWSignalHead in that the hardware
 *   that it talks to performs the timeing for flashing.
 */
package cats.jmri;

import jmri.SignalHead;
import cats.layout.items.PhysicalSignal;

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
 *   This class is different from SWSignalHead in that the hardware
 *   that it talks to performs the timeing for flashing.
 *
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2008, 2010</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */

public class HWSignalHead extends SWSignalHead {

  /**
   * constructs a HWSignalHead
   * @param systemName is the JMRI system name
   * @param userName is the external name known by the user
   */
  public HWSignalHead(String systemName, String userName) {
    super(systemName, userName);
  }

  /**
   * constructs a HWSignalHead
   * @param systemName is the JMRI system name
   */
  public HWSignalHead(String systemName) {
    super(systemName);
  }
  
  /**
   * constructs a SWSignalHead using an application
   * system name, which is not visible to users.
   */
//  public HWSignalHead()
//  {
//    super();
//  }

  /**
   * This overrides the DefaultSignalHead class to ignore
   * the flashing appearances because the hardware can do it.
   * 
   * @param newAppearance identifies which appearance is
   * being presented.  @see jmri.SignalHead
   */
  public void setAppearance(int newAppearance) {
    int oldAppearance = mAppearance;
    mAppearance = newAppearance;
            
  if (oldAppearance != newAppearance) {
    updateOutput();

        // notify listeners, if any
        firePropertyChange("Appearance", new Integer(oldAppearance), new Integer(newAppearance));
    }
}


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
        appearance = mAppearance;
    }
    else {
      appearance = SignalHead.DARK;
    }
    if (CurrentDecoder != null) {
      CurrentDecoder.sendUndoCommand();
    }
    CurrentDecoder = findSpec(appearance).Spec;
    if (CurrentDecoder == null) {
      if (!sentWarning) {
        String msg = "A SignalHead is missing the decoder definitions for "
          + PhysicalSignal.toCATS(appearance);
        log.warn(msg);
      }
    }
    else {
      CurrentDecoder.sendCommand();
    }
  }
  
  static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(HWSignalHead.class.getName());

}
/* @(#)HWSignalHead.java */