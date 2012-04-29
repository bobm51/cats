/* Name: KeyHandler.java
 *
 * What: This file contains the KeyHandler class - a singleton which
 *   watches the keyboard and distributes keystrokes to the objects
 *   with the current focus.
 *
 * Special Considerations:
 *   This uses the observer pattern as keys will be distributed only
 *   one object at a time. The object must register with the handler
 *   to receive the keys.
 *
 *   Initially, only the TrainsPullDown and Trains are interested in
 *   Keys.  The TrainsPullDown wants the up and down arrow keys for
 *   selecting Trains and the selected Train wants the left and
 *   right arrow keys for moving.
 */
package cats.gui;

import cats.rr_events.ArrowEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;

/**
 *   This class is a singleton which
 *   watches the keyboard and distributes keystrokes to the objects
 *   with the current focus.
 *  <p>
 *   This uses the observer pattern as keys will be distributed only
 *   one object at a time. The object must register with the handler
 *   to receive the keys.
 *
 *   Initially, only the Roster is interested in
 *   Keys.
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */
public class KeyHandler
    extends KeyAdapter {

  /**
   * is the singleton that receives all keystrokes.
   */
	public static final KeyHandler ArrowHandler = new KeyHandler();
  private static ArrowUser Observer;

  /** Name: KeyHandler()
   *
   * What:
   *	This is a class constructor
   *
   * Inputs:
   *  none
   *  
   * Returns:
   *	none
   *
   * Special Considerations:
   */
public KeyHandler() {
  }

/** Name: void wantsArrow(ArrowUser user)
   *
   * What:
   *	This is method is called to tell the KeyHandler that it
   *  wants to receive Arrow keys.
   *
   * Inputs:
   *   user wants to be told when an Arrow key is pushed.
   *
   * Returns:
   *	none
   *
   * @param user is the object that wants to be notified of
   * keystrokes.
   */
  public static void wantsArrow(ArrowUser user) {
    Observer = user;
  }

  /* Name: void keyPressed(KeyEvent key)
   *
   * What:
   *	This method is told when a key is pressed and which it is.
   *
   * Inputs:
   *  the event that a key was pressed.
   *
   * Returns:
   *	none
   *
   * Special Considerations:
   */
  public void keyPressed(KeyEvent key) {
    ArrowEvent ae;
    if (Observer != null) {
      switch (key.getKeyCode()) {
        case KeyEvent.VK_UP:
        case KeyEvent.VK_DOWN:
        case KeyEvent.VK_LEFT:
        case KeyEvent.VK_RIGHT:
        case KeyEvent.VK_PAGE_UP:
        case KeyEvent.VK_PAGE_DOWN:
        case KeyEvent.VK_TAB:
          ae = new ArrowEvent(key.getKeyCode(), Observer);
          ae.queUp();
          break;

        default:
      }
    }
  }
}
/* @(#)KeyHandler.java */