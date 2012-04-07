/**
 * Name: IOInterface.java
 * 
 * What:
 *   An interface for the methods needed by a CATS SignalHead
 *   to control physical signal heads.
 */
package cats.layout.items;

/**
 * An interface for the methods needed by CATS to control
 * things (signal heads, turnouts).  An IOSpec needs to
 * implement this interface.  In addition, IOSpec composite
 * structures need to meet this interface so that a composite
 * can be used wherever an IOSpec is used.
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */
public interface IOInterface {

  /**
   * sends a command to the decoder to set it to its normal
   * state.
   */
  public void sendCommand();

  /**
   * sends the deactivate command (if it exists).  This sets the
   * decoder to its other state.
   */
  public void sendUndoCommand();

  /**
   * forces the decoder into a particular state.
   * 
   * @param state is the state (Throw or Close) to send the decoder to.
   */
  public void forceState(boolean state);

  
  /**
   * retrieves the Delay value.
   * 
   * @return the delay in milliseconds after the previous command
   *  in a chain.
   */
  public int getDelay() ;
  
  /**
   * retrieves the decoder's name, which is used to identify it to the
   * control structure.
   *
   * @return the name of the decoder.
   */
  public String getName();

  /**
   * Locks the decoder command while the route is locked through a turnout so that
   * no other decoder can move the points.
   */
  public void lockOutCommand();
  
  /**
   * Unlocks the decoder command when a route through a turnout has cleared.  The points
   * can again be moved.
   */
  public void unlockOutCommand();
  
  /**
   * tests if the decoder command has been locked out.
   * @return true if the command should not be sent. False if it is safe to send it.
   */
  public boolean isLockedOut();

  /**
   * registers the decoder with the LockedDecoder as a candidate for having the lock
   * treatment.
   */
  public void registerLock();
}
