/* Name: JobEditPane.java
 *
 * What:
 *  This file contains the class definition for a JobEditPane object.  A
 *  JobEditPane constructs a JTable within a ScrollPane for adding and
 *  deleting trains and for editing Fields in a Job.
 *
 */
package cats.gui.store;

import java.awt.Component;
import javax.swing.BoxLayout;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import cats.gui.jCustom.AcceptDialog;

/**
 *  This file contains the class definition for a JobEditPane object.  A
 *  JobEditPane constructs a JTable within a ScrollPane for adding and
 *  deleting Jobs and for editing Fields in a Job.
 * <p>
 * This class uses the strategy design pattern for controlling the editing.
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A model railroad dispatching program</p>
 * <p>Copyright: Copyright (c) 2003, 2009, 2010</p>
 * @author Rodney Black
 * @version $Revision$
 */
public class JobEditPane
    extends StoreEditPane {

  /**
   * constructs the JobEditPane.
   * @param model is the TableModel contolling the editing.
   */
  public JobEditPane(CatsTableModel model) {
    super(model, true);
    addButtons();
  }

  /**
   * This method is designed to be overriden by a sub-class.  The sub-class
   * could add more a fewer buttons.
   */
  protected void addButtons() {
    JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
    buttonPanel.add(addUpButton(null));
    buttonPanel.add(addDownButton(null));
    buttonPanel.add(addAddButton(null));
    buttonPanel.add(addDelButton(null));
    add(buttonPanel);
  }

  /**
   * is the factory to construct an edit table.
   *
   * @param model is the TableModel used to direct the editing.
   * @param title is the title on the Window
   * @return true if the Accept key was pushed and everything
   * checked out.
   */
  public static boolean editRecords(CatsTableModel model, String title) {
    boolean notDone = true;
    String errorMsg = null;
//    GenericRecord rec;
    JobEditPane pane = new JobEditPane(model);
//    TaggedVector tv;
//    StoredObject so;
    while (notDone) {
      if (AcceptDialog.select(pane, title)) {
        if ( ( (errorMsg = pane.EditModel.verifyResults()) == null)) {
//          for (Enumeration<Object> e = model.getDeletedRecords().elements();
//               e.hasMoreElements(); ) {
//            tv = (TaggedVector) e.nextElement();
//            if ( ( (rec = tv.getTag()) != null) &&
//                ( (so = rec.getActiveReference()) != null)) {
//              so.destructor();
//            }
//          }
          model.saveWidths(pane.getColWidths());
          return true;
        }
        JOptionPane.showMessageDialog( (Component)null, errorMsg,
                                      "Data Error",
                                      JOptionPane.ERROR_MESSAGE);
      }
      else {
        return false;
      }
    }
    return false;
  }
}
/* @(#)JobEditPane.java */