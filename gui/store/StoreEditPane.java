/* Name: StoreEditPane.java
 *
 * What:
 *  This file contains the class definition for a StoreEditPane object.  A
 *  StoreEditPane constructs a JTable within a ScrollPane for adding and
 *  deleting records and for editing Fields in a record.
 *
 */
package cats.gui.store;

//import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;

import javax.swing.CellEditor;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
//import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

//import cats.gui.jCustom.AcceptDialog;

/**
 *  This file contains the class definition for a Store EditPane object.  A
 *  StoreEditPane constructs a JTable within a ScrollPane for adding and
 *  deleting records and for editing Fields in a record.
 * <p>
 * This class uses the strategy design pattern for controlling the editing.
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A model railroad dispatching program</p>
 * <p>Copyright: Copyright (c) 2003, 2009</p>
 * @author Rodney Black
 * @version $Revision$
 */
public class StoreEditPane
    extends JPanel {
  /**
   * is the Button for interchanging the selected row with the one above it.
   */
  protected JButton UpButton = new JButton("Move Record Up");

  /**
   * is the Button for interchanging the selected row with the one below it.
   */
  protected JButton DownButton = new JButton("Move Record Down");

  /**
   * is the Button for adding a new row below Bottom row.
   */
  protected JButton AddButton = new JButton("Add Record");

  /**
   * is the Button for removing a Record at the selection point.
   */
  protected JButton DelButton = new JButton("Delete Record(s)");

  /**
   * is the JTable that holds the fields of the Records.
   */
  protected JTable Table;

  /**
   * is the JPanel containing the buttons.
   */
//  protected JPanel ButtonPanel;

  /**
   * is the TableModel that controls the editing.
   */
  protected CatsTableModel EditModel;

  /**
   * is the row number of the top row of the selected range.
   */
  protected int TopRow;

  /**
   * is the row number of the bottom row of the selected range.
   */
  protected int BottomRow = -1;

  /**
   * constructs the AbstractEditPane.
   * @param model is the TableModel controlling the editing.
   * @param expand is true if the scrolling pane should be constructed
   * with more height to allow entries to be added or false if it should
   * be only as high as the data.
   */
  public StoreEditPane(CatsTableModel model, boolean expand) {
    TableColumnModel tcm;
    Class<?> colClass;
    SpecialType special;
    EditModel = model;
    Table = new JTable(EditModel);
    int tableHeight;
    int tableWidth = 0;
    DefaultTableCellRenderer renderer;
    DefaultCellEditor editor;
    TableCellRenderer tcr;
    
    tcm = Table.getColumnModel();
    for (int c = 0; c < tcm.getColumnCount(); ++c) {
//      tableWidth += model.getColumnWidth(c);
//      tcm.getColumn(c).setPreferredWidth(model.getColumnWidth(c));
//      colClass = EditModel.getColumnClass(c);
//      if (SpecialType.class.isAssignableFrom(colClass)) {
//        try {
//          special = (SpecialType) colClass.newInstance();
//          editor = special.getCellEditor();
//          editor.setClickCountToStart(1);
//          tcm.getColumn(c).setCellEditor(editor);
//          tcr = special.getCellRenderer();
//          if (DefaultTableCellRenderer.class.isInstance(tcr) ) {
//              ((DefaultTableCellRenderer)tcr).setHorizontalAlignment(model.getColumnAlignment(c));
//          }
//          tcm.getColumn(c).setCellRenderer(tcr);
//        }
//        catch (IllegalAccessException iae) {
//          System.out.println("Illegal Access Exception for " +
//                             colClass.getName());
//        }
//        catch (InstantiationException ie) {
//          System.out.println("Instantiation Exception for " +
//                             colClass.getName());
//        }
//      }
//      else if (!Boolean.class.isAssignableFrom(colClass)){
//        renderer = new DefaultTableCellRenderer();
//        renderer.setHorizontalAlignment(model.getColumnAlignment(c));
//        tcm.getColumn(c).setCellRenderer(renderer);
//        if (Integer.class.isAssignableFrom(colClass)) {
//            editor = new DefaultCellEditor(new JFormattedTextField(NumberFormat.getIntegerInstance()));
//        }
//        else {
//            editor = new DefaultCellEditor(new JTextField());
//        }
//        editor.setClickCountToStart(1);
//        tcm.getColumn(c).setCellEditor(editor);
//      }
      tableWidth += model.getColWidth(c);
      tcm.getColumn(c).setIdentifier(model.getColumnIdentifier(c));
      tcm.getColumn(c).setPreferredWidth(model.getColWidth(c));
      colClass = EditModel.getColumnClass(c);
      if (SpecialType.class.isAssignableFrom(colClass)) {
          try {
              special = (SpecialType) colClass.newInstance();
              editor = special.getCellEditor();
              editor.setClickCountToStart(1);
              tcm.getColumn(c).setCellEditor(editor);
              tcr = special.getCellRenderer();
              if (DefaultTableCellRenderer.class.isInstance(tcr) ) {
                  ((DefaultTableCellRenderer)tcr).setHorizontalAlignment(model.getColumnAlignment(c));
              }
              tcm.getColumn(c).setCellRenderer(tcr);
          }
          catch (IllegalAccessException iae) {
              System.out.println("Illegal Access Exception for " +
                      colClass.getName());
          }
          catch (InstantiationException ie) {
              System.out.println("Instantiation Exception for " +
                      colClass.getName());
          }
      }
      else if (!Boolean.class.isAssignableFrom(colClass)){
          if (Integer.class.isAssignableFrom(colClass)) {
              editor = new FormattedCellEditor(new JFormattedTextField(NumberFormat.getIntegerInstance()));
              renderer = new FormattedCellRenderer(NumberFormat.getIntegerInstance());
          }
          else {
              renderer = new DefaultTableCellRenderer();
              editor = new DefaultCellEditor(new JTextField());
          }
          editor.setClickCountToStart(1);
          tcm.getColumn(c).setCellEditor(editor);
          renderer.setHorizontalAlignment(model.getColumnAlignment(c));
          tcm.getColumn(c).setCellRenderer(renderer);
      }
    }
    doLayout();
    Table.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);

    Table.getSelectionModel().addListSelectionListener(new
        ListSelectionListener() {
      public void valueChanged(ListSelectionEvent ev) {
        int selRows[] = Table.getSelectedRows();
        int rowCount = EditModel.getRowCount();
        Table.editingCanceled(null);
        TopRow = rowCount - 1;
        BottomRow = -1;
        if (!ev.getValueIsAdjusting()) {
          for (int r = 0; r < selRows.length; ++r) {
            TopRow = Math.min(TopRow, selRows[r]);
            BottomRow = Math.max(BottomRow, selRows[r]);
          }
          UpButton.setEnabled(TopRow > 0);
          DownButton.setEnabled((BottomRow >= 0) && (BottomRow < (rowCount - 1)));
          if (selRows.length > 0) {
            enableDelButton(true);
          }
        }
      }
    });
    tableHeight = Math.min(20, EditModel.getRowCount());
    if (expand) {
      tableHeight = Math.max(tableHeight, 5);
    }
    else {
      tableHeight = Math.max(tableHeight, 2);
    }
    JScrollPane jsp = new JScrollPane(Table);
    jsp.setPreferredSize(new Dimension(tableWidth, tableHeight * 20));

    add(jsp);
  }

  /**
   * sets up the "up" button and adds it to the ButtonPanel.
   *
   * @param legend is the label on the Button.
   *
   * @return the UpButton
   */
  protected JButton addUpButton(String legend) {
    if (legend != null) {
      UpButton.setText(legend);
    }
    UpButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        int top = TopRow - 1;
        int bottom = BottomRow - 1;
        if (top <= bottom) {
          Table.editingCanceled(null);
          EditModel.moveUp(TopRow, BottomRow);
          Table.removeRowSelectionInterval(TopRow, BottomRow);
          Table.setRowSelectionInterval(top, bottom);
          UpButton.setEnabled(TopRow > 0);
        }
      }
    });
    UpButton.setEnabled(false);
    return UpButton;
  }

  /**
   * sets up the "down" button and adds it to the ButtonPanel.
   *
   * @param legend is the label on the Button.
   *
   * @return the DownButton
   */
  protected JButton addDownButton(String legend) {
    if (legend != null) {
      DownButton.setText(legend);
    }

    DownButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        int top = TopRow + 1;
        int bottom = BottomRow + 1;
        if (top <= bottom) {
          Table.editingCanceled(null);
          EditModel.moveDown(TopRow, BottomRow);
          Table.removeRowSelectionInterval(TopRow, BottomRow);
          Table.setRowSelectionInterval(top, bottom);
          DownButton.setEnabled(BottomRow < (EditModel.getRowCount() - 1));
        }
      }
    });
    DownButton.setEnabled(false);
    return DownButton;
  }

  /**
   * sets up the "add" button and adds it to the ButtonPanel.
   *
   * @param legend is the label on the Button.
   *
   * @return the AddButton
   */
  protected JButton addAddButton(String legend) {
    if (legend != null) {
      AddButton.setText(legend);
    }
    AddButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        EditModel.insertRecord(BottomRow + 1);
        validate();
        repaint();
      }
    });
    AddButton.setEnabled(true);
    return AddButton;
  }

  /**
   * sets up the "delete" button and adds it to the ButtonPanel.
   *
   * @param legend is the label on the Button.
   *
   * @return the DelButton
   */
  protected JButton addDelButton(String legend) {
    if (legend != null) {
      DelButton.setText(legend);
    }
    DelButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
//        Table.editingCanceled(null);
//        EditModel.delRecord(TopRow, BottomRow);
//        DelButton.setEnabled(false);
//        UpButton.setEnabled(false);
//        DownButton.setEnabled(false);
//        validate();
//        repaint();
        Table.editingCanceled(null);
        EditModel.delRecord(TopRow, BottomRow, null);
        DelButton.setEnabled(false);
        UpButton.setEnabled(false);
        DownButton.setEnabled(false);
        validate();
        repaint();
      }
    });
    DelButton.setEnabled(false);
    return DelButton;
  }

  /**
   * returns the widths of each column in a Vector.
   *
   * @return an array of int's, where each element is the current width of
   * a column.  The array is arranged in the order of columns in the JTable.
   */
  protected int[] getColWidths() {
    int[] widths = new int[Table.getColumnCount()];
    TableColumnModel tcm = Table.getColumnModel();
    for (int col = 0; col < widths.length; ++col) {
      widths[col] = tcm.getColumn(col).getWidth();
    }
    return widths;
  }

  /** is invoked to enable or disable the delete button.  For the default
   * StoreEditPane, this just operates on the Delete button.  For other
   * panes (e.g. TrainEditPane) it may enable or disable other buttons.
   * @param enable is true to enable the button(s) or false to disable it (them).
   */
  protected void enableDelButton(boolean enable) {
    DelButton.setEnabled(enable);
  }
  
  /**
   * tells the JTable that editing is complete because the Accept
   * button was pushed.
   */
  public void stopEditing() {
    CellEditor ce;
    if (Table.isEditing() && ((ce = Table.getCellEditor()) != null)) {
      ce.stopCellEditing();
    }
  }
  
  /**
   * is the factory to construct an edit table.
   *
   * @param model is the TableModel used to direct the editing.
   * @param title is the title String to put on the window frame
   * @return always false.  The concrete classes can return
   * true.
   */
//  public static boolean editRecords(CatsTableModel model, String title) {
//    boolean notDone = true;
//    String errorMsg = null;
//    StoreEditPane pane = new StoreEditPane(model, true);
//    while (notDone) {
//      if (AcceptDialog.select(pane, title)) {
//        if ((errorMsg = model.verifyResults()) == null) {
//          model.saveWidths(pane.getColWidths());
//          return true;
//        }
//        JOptionPane.showMessageDialog( (Component)null, errorMsg,
//            "Data Error",
//            JOptionPane.ERROR_MESSAGE);
//      }
//      else {
//        return false;
//      }
//    }
//    return false;
//  }
}
/* @(#)StoreEditPane.java */