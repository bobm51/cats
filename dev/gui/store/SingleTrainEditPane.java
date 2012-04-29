/* Name: SingleTrainEditPane.java
 *
 * What:
 *  This file contains the class definition for a SingleTrainEditPane object.  A
 *  SingleTrainEditPane constructs a JTable with the information on one train.
 *  A JTable is a little overkill, but by sub-classing TrainEditPane, the
 *  sub-class maintains a common feel with the super-class.  Plus, some of the
 *  code can be reused.
 */
package cats.gui.store;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

//import cats.layout.store.TaggedVector;
import cats.trains.Train;

/**
 *  This file contains the class definition for a SingleTrainEditPane object.  A
 *  SingleTrainEditPane constructs a JTable with the information on one train.
 *  A JTable is a little overkill, but by sub-classing TrainEditPane, the
 *  sub-class maintains a common feel with the super-class.  Plus, some of the
 *  code can be reused.
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A model railroad dispatching program</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * @author Rodney Black
 * @version $Revision$
 */
public class SingleTrainEditPane extends TrainEditPane {

  /**
   * is the default label on the "Rerun Train" Button.
   */
  private static final String RERUN = "Rerun Train";
  
  /**
   * is the JButton that reruns a train.
   */
  protected JButton RerunButton;

  /**
   * is true when the Rerun Button is pushed.
   */
//  static private boolean RerunTrain;
  
  /**
   * is the Train being edited.
   */
  static private Train MyTrain;

  /**
   * is the Train Record being edited.
   */
//  private TaggedVector TrainInfo;
  
  /**
   * constructs the AbstractEditPane.
   * @param model is the TableModel controlling the editing.
   * @param train is the Train being edited
   */
  public SingleTrainEditPane(CatsTableModel model, Train train) {
    super(model, false);
//    TrainInfo = model.RecordContents.elementAt(0);
//    MyTrain = (Train)TrainInfo.getTag().getActiveReference();
    MyTrain = train;
    TDButton.setEnabled(!MyTrain.hasRun());
    TermButton.setEnabled(MyTrain.isNotRemoved());
    RerunButton.setEnabled(MyTrain.hasRun());
    TopRow = BottomRow = 0;
//    RerunTrain = false;
  }
  
  /**
   * This method is designed to be overridden by a sub-class.  The sub-class
   * could add more or fewer buttons.  Since only a single train is being
   * edited, many of the buttons can be ignored: add, delete, move up,
   * move down.
   */
  protected void addButtons() {
    JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
    buttonPanel.add(addTieDownButton(null));
    buttonPanel.add(addTerminateButton(null));
    buttonPanel.add(addRerunButton(null));
    add(buttonPanel);
  }
  
  /**
   * sets up the "Rerun" button and adds it to the ButtonPanel.
   *
   * @param legend is the label on the Button.
   * @return the delete button.
   */
  protected JButton addRerunButton(String legend) {
    if (RerunButton == null) {
      RerunButton = new JButton(RERUN);
    }
    if (legend != null) {
      RerunButton.setText(legend);
    }
    RerunButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        if ( (TopRow == 0) && (BottomRow == 0)) {
          Table.editingCanceled(null);
//          EditModel.delRecord(TopRow, BottomRow);
          EditModel.delRecord(TopRow, BottomRow, Train.TRAIN_RERUN);
//          RerunTrain = true;
          validate();
          repaint();
        }
      }
    });
    return RerunButton;
  }
  
  /**
   * saves changes to the edit table columns.  Changes made while
   * editing a train are ignored.
   */
  protected void saveColumns() {
  }
}
/* @(#)SingleTrainEditPane.java */