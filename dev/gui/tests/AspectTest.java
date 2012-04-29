/*
 * Name: AspectTest
 *
 * What: This file contains the menu for running signal tests that test
 *   ability of signals to project a particular color or position.
 */
package cats.gui.tests;

import cats.gui.jCustom.JListDialog;
import cats.layout.items.PhysicalSignal;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Point;
import javax.swing.*;

/**
 * This file contains the menu for running signal tests that test
 * ability of signals to project a particular color or position.
 *
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */

public class AspectTest
    extends JMenuItem {

  /**
   * the List of indications
   */
  private static final String[] AspectList = {
      "off",
      "green",
      "yellow",
      "red",
      "flashing green",
      "flashing yellow",
      "flashing red",
      "lunar",
      "flashing lunar",
      "horizontal",
      "diagonal",
      "vertical"
  };

  /**
   * the constructor
   *
   * @param title is the label in the menu.
   */
  public AspectTest(String title) {
    super(title);

    addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        int selection;
        if ( (selection = JListDialog.select(AspectList,
                                             "Select Test Aspect",
                                             new Point(50, 50))) >= 0) {
          PhysicalSignal.signalTest(AspectList[selection]);
        }
      }
    });
  }
}
