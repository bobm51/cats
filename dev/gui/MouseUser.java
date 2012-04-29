/* Name: MouseUser.java
 *
 * What: This file contains the interface for accepting Mouse button
 *   releases.
 *
 * Special Considerations:
 */
package cats.gui;

import java.awt.event.MouseEvent;

/**
 * is an interface for accepting mouse button releases.
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */
public interface MouseUser {

  /**
   *   This method tells the Object that accepted the Mouse Press
   *   that the user released the mouse button
   *
   * @param event provides information on the Mouse button release
   *
   */
  public void finishMouse(MouseEvent event);

}