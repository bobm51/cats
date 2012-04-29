/* Name: TrainPicker.java
 *
 * What:
 *  TrainPicker.java defines a GUI for showing all trains that meet
 *  a selection criteria, then allows the user to select one.
 */
package cats.gui.trains;

import cats.trains.Train;
import cats.trains.TrainStore;
import cats.gui.jCustom.AcceptDialog;
import javax.swing.ListSelectionModel;
import java.util.Enumeration;
import java.util.Vector;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * is a JPanel for showing all trains that meet a selection criteria,
 * then allows the user to select one.
 *
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2004, 2009</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */

public class TrainPicker
    extends JPanel {

  /**
   * is the list of Train identities.  The identities are fields extracted
   * from the GenericRecords that match the criteria.
   */
  private JList Trains;

  /**
   * constructs the body of the selection list.
   *
   * @param list is a Vector of Trains that meet the selection criteria.
   */
  public TrainPicker(Vector<Train> list) {
    Vector<String> ids = new Vector<String>(list.size());
    JScrollPane jsp;
    Train t;
    for (Enumeration<Train> e = list.elements(); e.hasMoreElements(); ) {
      t = e.nextElement();
      ids.add(new String(t.getSymbol() + " " + t.getName()));
    }
    Trains = new JList(ids);
    Trains.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    jsp = new JScrollPane(Trains);
    add(jsp);
  }

  /**
   * creates a Dialog for showing all the Trains in a Vector of Trains,
   * allows the user to select one, and if the Accept button is pushed,
   * returns that Train.
   *
   * @param list is the Vector of Trains.
   * @param title is the title to put on the Dialog.
   *
   * @return a Train from the Vector or null.
   */
  private static Train pickOne(Vector<Train> list, String title) {
    TrainPicker panel = new TrainPicker(list);
    int selection;
    if (AcceptDialog.select(panel, title) &&
        ( (selection = panel.Trains.getSelectedIndex()) >= 0)) {
      return list.elementAt(selection);
    }
    return null;
  }

  /**
   * creates a pickOne Dialog of Trains that have not been removed, then
   * if the user selects one, removes it.
   */
//  public static void removeTrain() {
//    Train t = pickOne(TrainStore.TrainKeeper.getNotRemoved(), "Remove a Train:");
//    if (t != null) {
//      t.remove();
//    }
//  }

  /**
   * creates a pickOne Dialog of Trains that have been removed, then
   * if the user selects one, moves it to the Created state, so that it
   * can be run again.
   */
  public static void rerunTrain() {
    Train t = pickOne(TrainStore.TrainKeeper.getRun(), "Rerun a Train:");
    if (t != null) {
      t.rerun();
    }
  }
}
/* @(#)TrainPicker.java */