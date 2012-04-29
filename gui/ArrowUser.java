/*
 * Name: ArrowUser.java
 *
 * What:
 *   An interface for registering a consumer with teh KeyHandler.
 */
package cats.gui;

/**
 * This file contains the interface for accepting Arrow keys.
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */

public interface ArrowUser {

  /**
   *   This method is called to tell the object which of the Arrow
   *   keys has been seen.
   *
   * @param arrow is a key code.
   *
   * @see java.awt.event.KeyEvent
   */
  public void takeArrow(int arrow);

}