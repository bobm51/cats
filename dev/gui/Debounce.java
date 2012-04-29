/* Name: Debounce.java
 *
 * What: This file contains the Debounce class.  It provides a Menu
 *  for selecting the number of seconds the program will wait after
 *  receiving an occupy message before passing that message on.
 *
 * Special Considerations:
 */
package cats.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

/**
 *  This file contains the Debounce class.  It provides a Menu
 *  for selecting the number of seconds the program will wait after
 *  receiving an occupy message before processing that message.
 *  <p>
 *  If an unoccupied message is received before timer expires, then
 *  the program considers the first message to be spurious.
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */
public class Debounce
    extends JMenu {

  /**
   * is the number of delays that can be selected.
   */
  private static final int MAX_SELECTIONS = 11;

  /**
   * is how other classes locate the Delay.
   */
  public static Debounce Timer;

  /**
   * is the number of seconds of delay.
   */
  private int Delay;

  /**
   * is the menu item selections.
   */
  private DelayClass[] Selections = new DelayClass[MAX_SELECTIONS];

  /**
   * constructs the menu items and initializes the debounce value.
   */
  public Debounce() {
    Delay = 0;
    for (int i = 0; i < MAX_SELECTIONS; ++i) {
      Selections[i] = new DelayClass(i);
      Selections[i].setText(String.valueOf(i));
      add(Selections[i]);
    }
    Selections[Delay].setEnabled(false);
    Timer = this;
  }

  /**
   * returns the current delay for debouncing, in seconds.
   *
   * @return the delay.
   */
  public int getDelay() {
    return Delay;
  }

  /**
   * is an inner class for holding each of the possible time delays.
   */
  private class DelayClass
      extends JMenuItem {

    private int MyDelay;

    /**
     * is the constructor
     * 
     * @param value is number of seconds to delay between
     * sending commands.
     */
    public DelayClass(int value) {
      MyDelay = value;
      addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          // Enable the current selection.
          Selections[Delay].setEnabled(true);

          // Disable the new selection, to indicate that it is active.
          setEnabled(false);
          Delay = MyDelay;
        }
      }
      );
    }
  }
}