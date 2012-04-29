/* Name: PegCounter.java
 *
 * What:
 *  This file defines a class of singletons, each holding a counter of
 *  events.  The counter is kept in a JMRI Memory item; thus, it can be
 *  accessed through the JMRI memory table.
 *
 * Special Considerations:
 */
package cats.layout;

import java.beans.PropertyChangeListener;

import jmri.InstanceManager;
import jmri.JmriException;
import jmri.Memory;

/**
 *  This file defines a class of singletons, each holding a counter of
 *  events.  The counter is kept in a JMRI Memory item; thus, it can be
 *  accessed through the JMRI memory table.
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */
public class PegCounter {
  /**
   * is the JMRI Memory object that holds the count
   */
  private Memory Counter;
  
  /**
   * is the name of the JMRI Memory object
   */
  private final String ObjectName;
  
  /**
   * is the ctor
   * 
   * @param name is the UserName of the JMRI Memory object
   */
  public PegCounter(String name) {
    ObjectName = name;
    Counter = InstanceManager.memoryManagerInstance().provideMemory(name);
    if (Counter == null) {
      log.warn("Could not create a Peg Counter named " + name);
    }
    try {
      Counter.setState(0);
    } catch (JmriException e) {
      log.warn("JMRI exception on Peg Counter " + name);
      e.printStackTrace();
    }
  }

  /**
   * pegs an event by bumping the counter
   *
   */
  public void bumpCounter() {
    int count = Counter.getState();
    try {
      Counter.setState(count + 1);
    } catch (JmriException e) {
      log.warn("JMRI exception on Peg Counter " + ObjectName);
      e.printStackTrace();
    }
  }
  
  /**
   * zeros the counter
   */
  public void zeroCounter() {
    try {
      Counter.setState(0);
    } catch (JmriException e) {
      log.warn("JMRI exception on Peg Counter " + ObjectName);
      e.printStackTrace();
    }    
  }
  
  /**
   * registers a listener on the counter
   * @param listener is the listener
   */
  public void register(PropertyChangeListener listener) {
    Counter.addPropertyChangeListener(listener);
  }
  
  /**
   * removes a property change listener
   * @param listener is the listener
   */
  public void unregister(PropertyChangeListener listener) {
    Counter.removePropertyChangeListener(listener);
  }
  
  static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(PegCounter.class.getName());

}
/* @(#)PegCounter.java */