/*
 * Name: SignalTest
 *
 * What: This file contains the menu for running signal tests.
 */
package cats.gui.tests;

import cats.gui.jCustom.JListDialog;
import cats.layout.AspectMap;
import cats.layout.items.PhysicalSignal;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Point;
import javax.swing.*;

/**
 * This file contains the menu for running signal tests.
 *
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */

public class SignalTest
    extends JMenuItem {

  /**
   * the List of indications
   */
  private static String[] IndicationList = new String[AspectMap.IndicationNames.
      length];

  /**
   * the constructor.
   *
   * @param title is the label in the menu.
   */
  public SignalTest(String title) {
    super(title);

    for (int i = 0; i < IndicationList.length; ++i) {
      IndicationList[i] = AspectMap.IndicationNames[i][AspectMap.LABEL];
    }
    addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        int selection;
        if ( (selection = JListDialog.select(IndicationList,
                                             "Select Test Indication",
                                             new Point(50, 50))) >= 0) {
          PhysicalSignal.signalTest(selection);
        }
      }
    });
  }
}