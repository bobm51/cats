/* Name: CrewPicker.java
 *
 * What:
 *  CrewPicker.java defines a GUI for showing all crew and what their
 current Train assignment is.
 */
package cats.gui.crew;

import cats.crew.*;
import cats.gui.jCustom.JListDialog;
import java.awt.Point;
import java.util.Vector;

/**
 * is a JPanel for showing all Crew and what their current Train
 * assignment is.  If a member is selcted, then it is returned.
 *
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2004, 2009</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */

public class CrewPicker {

  /**
   * creates a Dialog for showing all the Crews in a Vector of Crews,
   * allows the user to select one, and if the Accept button is pushed,
   * returns that Crew.
   *
   * @param list is the Vector of Crew.
   * @param title is the title to put on the Dialog.
   * @param pt is a point on the screen where the dialog should be placed
   *
   * @return a Crew from the Vector or null.
   */
  public static Crew pickOne(Vector<Crew> list, String title, Point pt) {
    String[] crews = new String[list.size()];
    Crew c;
    int selection;
    for (int i = 0; i < crews.length; ++i) {
      c = list.elementAt(i);
      if (c.getTrain() != null) {
        crews[i] = new String(c.getCrewName() + " " + c.getTrain().getSymbol());
      }
      else {
        crews[i] = new String(c.getCrewName());
      }
    }
    if ((selection = JListDialog.select(crews, title, pt)) >= 0) {
      return list.elementAt(selection);
    }
    return null;
  }
}
/* @(#)CrewPicker.java */