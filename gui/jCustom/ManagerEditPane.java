/* Name: ManagerEditPane.java
 *
 * What:
 *  This file contains the class definition for a ManagerEditPane object.
 *  A ManagerEditPane constructs a JTable within a ScrollPane for
 *  adding, deleting, and editing the elements in a list (maintained by the
 *  list Manager).
 *  <p>
 *  It is tightly coupled to a definition of AbstractManagerTableModel.
 *  <p>
 *  Note that when creating a new kind of data that requires rendering and
 *  editing, a TableCellEditor and a TableCellRenderer will need to be created
 *  and "registered"  in the body of the ManagerEditPane constructor.
 *
 */
package cats.gui.jCustom;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import cats.gui.store.ColorEditor;
import cats.gui.store.ColorRenderer;
import cats.gui.store.SizeType;
import cats.gui.store.StyleType;

/**
*  This file contains the class definition for a ManagerEditPane object.
*  A ManagerEditPane constructs a JTable within a ScrollPane for
*  adding, deleting, and editing the elements in a list (maintained by the
*  list Manager).
*  <p>
*  It is tightly coupled to a definition of AbstractManagerTableModel.
*  <p>
*  Note that when creating a new kind of data that requires rendering and
*  editing, a TableCellEditor and a TableCellRenderer will need to be created
*  and "registered"  in the body of the ManagerEditPane constructor.
* <p>Title: CATS - Crandic Automated Traffic System</p>
* <p>Description: A model railroad dispatching program</p>
* <p>Copyright: Copyright (c) 2009, 2010</p>
* @author Rodney Black
* @version $Revision$
*/
public class ManagerEditPane extends JPanel {
  
  /**
   * is the Button for adding a new row below Bottom row.
   */
  private final JButton AddButton = new JButton("Add");
  
  /**
   * is the Button for removing a Color at the selection point.
   */
  private final JButton DelButton = new JButton("Delete");

  /**
   * is the JTable that holds the fields of the Colors
   */
  private JTable Table = new JTable();
  
  /**
   * is the TableModel that controls the editing.
   */
  private AbstractManagerTableModel EditModel;
  
  /**
   * is the row number of the top row of the selected range.
   */
  private int TopRow;
  
  /**
   * is the row number of the bottom row of the selected range.
   */
  private int BottomRow = -1;
  
  /**
   * constructs the AbstractEditPane.
   * @param model is the TableModel controlling the editing.
   */
  public ManagerEditPane(AbstractManagerTableModel model) {
      JPanel movers = new JPanel();
      Class<?> colClass;
//      Table.setDefaultRenderer(PrefixSelector.class, PrefixSelector.getRenderer());
      EditModel = model;
      Table.setModel(model);
      TableColumnModel tcm = Table.getColumnModel();
      for (int col = 0; col < model.getColumnCount(); ++col) {
          TableColumn column = tcm.getColumn(col);
          column.setPreferredWidth(model.getColWidth(col));
          colClass = model.getColumnClass(col);
          if (colClass.equals(Color.class)) {
              Table.setDefaultEditor(Color.class, new ColorEditor());
              Table.setDefaultRenderer(Color.class, new ColorRenderer(true));               
          }
          else if (colClass.equals(SizeType.class)) {
              Table.setDefaultRenderer(SizeType.class, SizeType.getRenderer());
              Table.setDefaultEditor(SizeType.class, SizeType.getEditor());                
          }
          else if (colClass.equals(StyleType.class)) {
              Table.setDefaultRenderer(StyleType.class, StyleType.getRenderer());
              Table.setDefaultEditor(StyleType.class, StyleType.getEditor());                
          }
          
      }
      Table.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
      Table.getSelectionModel().addListSelectionListener(new
              ListSelectionListener() {
          public void valueChanged(ListSelectionEvent ev) {
              int selRows[] = Table.getSelectedRows();
              TopRow = EditModel.getRowCount();
              BottomRow = -1;
              if (!ev.getValueIsAdjusting()) {
                  for (int r = 0; r < selRows.length; ++r) {
                      TopRow = Math.min(TopRow, selRows[r]);
                      BottomRow = Math.max(BottomRow, selRows[r]);
                  }
                  DelButton.setEnabled(true);
              }
          }
      });
      AddButton.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent ae) {
              EditModel.insertElement(BottomRow + 1);
              validate();
              repaint();
          }
      });
      DelButton.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent ae) {
              Table.editingCanceled(null);
              EditModel.delElement(TopRow, BottomRow);
              DelButton.setEnabled(false);
              validate();
              repaint();
          }
      });
      movers.setLayout(new BoxLayout(movers, BoxLayout.Y_AXIS));
      DelButton.setEnabled(false);
      AddButton.setEnabled(true);
      movers.add(AddButton);
      movers.add(DelButton);
      JScrollPane jsp = new JScrollPane(Table);
      add(jsp);
      add(movers);
  }
  
  
  /**
   * is the factory to construct an edit table.
   *
   * @param model is the TableModel used to direct the editing.
   * @return true if the user asked that the Store be updated.
   */
  public static boolean editList(AbstractManagerTableModel model) {
      boolean notDone = true;
      String errorMsg = null;
      ManagerEditPane pane = new ManagerEditPane(model);
      while (notDone) {
          if (AcceptDialog.select(pane, "Edit List")) {
              if (((errorMsg = model.verifyResults()) == null)) {
                  return true;
              }
              JOptionPane.showMessageDialog( (Component)null, errorMsg, "Data Error",
                      JOptionPane.ERROR_MESSAGE);
          }
          else {
              return false;
          }
      }
      return false;
  }   
}
/* @(#)ManagerEditPane.java */