/* Name: TrainEditPane.java
 *
 * What:
 *  This file contains the class definition for a TrainEditPane object.  A
 *  TrainEditPane constructs a JTable within a ScrollPane for adding and
 *  deleting trains and for editing Fields in a train.
 *
 */
package cats.gui.store;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
//import java.util.Vector;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import cats.gui.jCustom.AcceptDialog;
//import cats.layout.store.TaggedVector;
import cats.trains.Train;

/**
 *  This file contains the class definition for a TrainEditPane object.  A
 *  TrainEditPane constructs a JTable within a ScrollPane for adding and
 *  deleting Trains and for editing Fields in a Train.
 * <p>
 * This class uses the strategy design pattern for controlling the editing.
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A model railroad dispatching program</p>
 * <p>Copyright: Copyright (c) 2003, 2009, 2010</p>
 * @author Rodney Black
 * @version $Revision$
 */
public class TrainEditPane
    extends StoreEditPane {

  /**
   * is the default label on the "Tie Down" Button.
   */
  private static final String TIE_DOWN = "Tie Down Train";

  /**
   * is the default label on the "Terminate" Button.
   */
  private static final String TERMINATE = "Terminate Train";

  /**
   * is the Button that ties down trains.
   */
  protected JButton TDButton = new JButton(TIE_DOWN);

  /**
   * is the JButton that removes trains.
   */
  protected JButton TermButton = new JButton(TERMINATE);
  
  /**
   * is the CatsTableModel controlling the editing
   */
  private CatsTableModel MyModel;

  /**
   * is a list of trains that have been tied down.
   */
//  protected static Vector<TaggedVector> TiedDown;

  /**
   * is a list of trains removed from the panel.
   */
//  protected static Vector<TaggedVector> Terminated;

  /**
   * constructs the AbstractEditPane.
   * @param model is the TableModel controlling the editing.
   * @param expand is true if the scrolling pane should be constructed
   * with more height to allow entries to be added or false if it should
   * be only as high as the data.
   */
  public TrainEditPane(CatsTableModel model, boolean expand) {
    super(model, expand);
    MyModel = model;
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    addButtons();
//    TiedDown = new Vector<TaggedVector>();
//    Terminated = new Vector<TaggedVector>();
  }

  /**
   * This method is designed to be overriden by a sub-class.  The sub-class
   * could add more or fewer buttons.
   */
  protected void addButtons() {
    JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
    buttonPanel.add(addUpButton(null));
    buttonPanel.add(addDownButton(null));
    buttonPanel.add(addAddButton(null));
    buttonPanel.add(addTieDownButton(null));
    buttonPanel.add(addTerminateButton(null));
    add(buttonPanel);
  }

  /**
   * sets up the "tie down" button and adds it to the ButtonPanel.
   *
   * @param legend is the label on the Button.
   * @return the delete button.
   */
  protected JButton addTieDownButton(String legend) {
    if (legend != null) {
      TDButton.setText(legend);
    }
    TDButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
//        Vector<Vector<Object>> contents;
//        if ( (TopRow >= 0) && (TopRow <= BottomRow) &&
//            (BottomRow < EditModel.getRowCount())) {
//          Table.editingCanceled(null);
//          contents = EditModel.getContents();
//          for (int train = TopRow; train <= BottomRow; ++train) {
//            TiedDown.add((TaggedVector)contents.elementAt(train));
//          }
//          EditModel.delRecord(TopRow, BottomRow);
//          UpButton.setEnabled(false);
//          DownButton.setEnabled(false);
//          validate();
//          repaint();
//        }
        if ( (TopRow >= 0) && (TopRow <= BottomRow) &&
            (BottomRow < EditModel.getRowCount())) {
          Table.editingCanceled(null);
          EditModel.delRecord(TopRow, BottomRow, Train.TRAIN_TIED_DOWN);
          UpButton.setEnabled(false);
          DownButton.setEnabled(false);
          enableDelButton(false);
          validate();
          repaint();
        }
      }
    });
    TDButton.setEnabled(false);
    return TDButton;
  }

  /**
   * sets up the "terminated" button and adds it to the ButtonPanel.
   *
   * @param legend is the label on the Button.
   * @return the delete button.
   */
  protected JButton addTerminateButton(String legend) {
    if (legend != null) {
      TermButton.setText(legend);
    }
    TermButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
//        Vector<Vector<Object>> contents;
//        if ( (TopRow >= 0) && (TopRow <= BottomRow) &&
//            (BottomRow < EditModel.getRowCount())) {
//          Table.editingCanceled(null);
//          contents = EditModel.getContents();
//          for (int train = TopRow; train <= BottomRow; ++train) {
//            Terminated.add((TaggedVector) contents.elementAt(train));
//          }
//          EditModel.delRecord(TopRow, BottomRow);
//          UpButton.setEnabled(false);
//          DownButton.setEnabled(false);
//          validate();
//          repaint();
//        }
        if ( (TopRow >= 0) && (TopRow <= BottomRow) &&
            (BottomRow < EditModel.getRowCount())) {
          Table.editingCanceled(null);
          EditModel.delRecord(TopRow, BottomRow, Train.TRAIN_TERMINATED);
          UpButton.setEnabled(false);
          DownButton.setEnabled(false);
          enableDelButton(false);
          validate();
          repaint();
        }
      }
    });
    TermButton.setEnabled(false);
    return TermButton;
  }

  /** is invoked to enable or disable the delete button.  For the default
   * StoreEditPane, this just operates on the Delete button.  For other
   * panes (e.g. TrainEditPane) it may enable or disable other buttons.
   * @param enable is true to enable the button(s) or false to disable it (them).
   */
  protected void enableDelButton(boolean enable) {
    TDButton.setEnabled(enable);
    TermButton.setEnabled(enable);
  }

  /**
   * saves changes to the edit table columns.
   */
  protected void saveColumns() {
    MyModel.saveWidths(getColWidths());
  }
  
  /**
   * is the factory to construct an edit table.
   *
   * @param pane is the panel used to direct the editing.
   * @param title is the title on the edit JFrame
   * @return true, if the edits were requested and consistent; false,
   * if the changes were canceled or there were errors
   */
  public static boolean editRecords(TrainEditPane pane, String title) {
    boolean notDone = true;
    String errorMsg = null;
    while (notDone) {
      if (AcceptDialog.select(pane, title)) {
        if ( ( (errorMsg = pane.EditModel.verifyResults()) == null)) {
          pane.saveColumns();
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
/* @(#)TrainEditPane.java */