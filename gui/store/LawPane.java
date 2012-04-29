/* Name: LawPane.java
 *
 * What:
 *  This Dialog is for setting the "Dead on the Law" Hours.
 */
package cats.gui.store;

import cats.gui.jCustom.AcceptDialog;
import cats.layout.Hours;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.text.SimpleDateFormat;

/**
 *  This Dialog is for setting the "Dead on the Law" Hours.
 *
 * <p>Title: designer</p>
 * <p>Description: A program for designing dispatcher panels</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */

public class LawPane
    extends JPanel {

  /**
   * is the field for editing the hours.
   */
  private JFormattedTextField HoursField;

  /**
   * constructs the JPanel for editing the Hours field.
   *
   * @param hours is the number of minutes in the legal time.
   */
  public LawPane(int hours) {
    String sHours;
    if (hours == TimeSpec.UNKNOWN_TIME) {
      sHours = new String("");
    }
    else {
      sHours = TimeSpec.convertMinutes(hours);
    }
    HoursField = new JFormattedTextField(new SimpleDateFormat("HH:mm"));
    HoursField.setColumns(5);
    HoursField.setText(sHours);
    add(new JLabel("Hours Allowed by Law to Work:"));
    add(HoursField);
  }

  /**
   * is the factory to construct a LawDialog.
   *
   * @param hours is the number of hours the crew may work when the method
   * is called.
   */
  public static void editHours(int hours) {
    LawPane pane = new LawPane(hours);
    if (AcceptDialog.select(pane, "Edit Work Hours")) {
      Hours.setHours(pane.HoursField.getText());
    }
  }
}
/* @(#)LawPane.java */