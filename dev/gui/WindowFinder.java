/* Name: WindowFinder.java
 *
 * What:
 *  This file contains the WindowFinder class.  It provides a Singleton
 *  for locating the CTC Panel window.  It is just the location.  It is a
 *  separate class (object) to break a cycle of references during
 *  compilation.
 *
 * Special Considerations:
 */
package cats.gui;

import javax.swing.JPanel;

/**
 *  This file contains the WindowFinder class.  It provides a Singleton
 *  for locating the CTC Panel window.  It is just the location.  It is a
 *  separate class (object) to break a cycle of references during
 *  compilation.
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */
public class WindowFinder {
  /**
   * is the location of the CTC Panel.
   */
  private static JPanel CtcPanel;

  /**
   * sets a reference to the CTC Panel.
   *
   * @param ctc is the JPanel that holds the CTC main window.
   */
  public static void setLocation(JPanel ctc) {
    CtcPanel = ctc;
  }

  /**
   * retrieves the JPanel.
   *
   * @return the JPanel that contains the track diagram.
   */
  public static JPanel getLocation() {
    return CtcPanel;
  }
}
/* @(#)WindowFinder.java */