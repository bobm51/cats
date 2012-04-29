/* What: ListSpec.java
 *
 * What:
 *  This is an abstract class for formatting a JList as a JTableCell.  The
 *  concrete classes hold the objects that form the JList.  It is derived
 *  from the CATS SpecialType so that the StoreEditPane can locate the
 *  methods for acquiring a CellEditor and CellRenderer.
 */
package cats.gui.store;

import java.awt.Component;
import javax.swing.JComboBox;
import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import java.util.Enumeration;
import java.util.Vector;

import cats.layout.store.FieldPair;
import cats.layout.store.GenericRecord;

/**
 *  This is an abstract class for formatting a JList as a JTableCell.  The
 *  concrete classes hold the objects that form the JList.  It is derived
 *  from the CATS SpecialType so that the StoreEditPane can locate the
 *  methods for acquiring a CellEditor and CellRenderer.
 * <p>Title: designer</p>
 * <p>Description: A program for designing dispatcher panels</p>
 * <p>Copyright: Copyright (c) 2003, 2009</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */

/**
 * is a filter for selecting certain file suffixes.
 */
public abstract class ListSpec
    extends SpecialType {

  /**
   * is the String identifying the item that has been selected from the list.
   */
  protected Object CurrentSelection;

  /**
   * is the no argument constructor.
   */
  public ListSpec() {
  }

  /**
   * is the single argument constructor.
   *
   * @param sel is initial selection.
   */
  public ListSpec(Object sel) {
    CurrentSelection = sel;
  }

  /**
   * is the sub-class specific method for building the Vector of labels
   * in the Selection dialog.
   *
   * @return the Vector of Strings that forms the contents of the Selection
   * Dialog.
   */
  protected abstract Vector<String> createSpecificList();

  /**
   * steps through all elements in the list of GenericRecords and extracts
   * the identifier field from each.  The identifier fields are collected into
   * a Vector that is turned into a JList for the combo box.
   *
   * @param vec is the List that forms the body of the Selection List.
   * @param tag is the Tag of the field in the list.
   *
   * @return the Vector of labels that form the contents of the Selection
   * Dialog.
   */
  @SuppressWarnings("unchecked")
protected Vector<String> createList(Vector<?> vec, String tag) {
    Vector<String> list = new Vector<String>(vec.size());
    GenericRecord record;
    FieldPair pair;
    for (Enumeration<GenericRecord> e = (Enumeration<GenericRecord>) vec.elements(); e.hasMoreElements(); ) {
      record = e.nextElement();
      if ( (pair = record.findPair(tag)) != null) {
        list.add((String) pair.FieldValue);
      }
    }
    return list;
  }

  /*
   * is used for printing it's value in a JTextTextField, as well as the
   * common usage.
   *
   * @return the value of the JComboBox selection, as a String.
   */
  public String toString() {
    if (CurrentSelection != null) {
      return CurrentSelection.toString();
    }
    return null;
  }

  /**
   * returns a JTableCellEditor for editing the JCombo list.
   *
   * @return the editor.
   */
  public DefaultCellEditor getCellEditor() {
    JComboBox combo = new JComboBox(createSpecificList());
    return new DefaultCellEditor(combo);
  }

  /**
   * returns a JTableCellRenderer for rendering the JCombo list.
   *
   * @return the renderer.
   */
  public TableCellRenderer getCellRenderer() {
    return new ComboRenderer(createSpecificList());
  }
  
  /**
   * converts an array of Strings into a Vector.  I think there is a
   * standard language method for this in the newer versions
   * of Java, but I do not remember what it is.
   * 
   * @param a is the array
   * 
   * @return the array, as a Vector.
   */
  static public Vector<String> arrayToVector(String[] a) {
      Vector<String> myList = new Vector<String>(a.length);
      for (int l = 0; l < a.length; ++l) {
          myList.add(a[l]);
      }
      return myList;       
  }
}

/**
 * is an inner class for handling the Crew Selection via a JComboBox.
 */
class ComboRenderer
    extends JComboBox
    implements TableCellRenderer {

  /**
   * is the list composing the JComboBox.
   */
  private Vector<?> MyList;

  /**
   * is the constructor.
   *
   * @param list is a Vector of crew names.
   */
  public ComboRenderer(Vector<?> list) {
    super(list);
    MyList = list;
  }

  /*
   * is the method required by the TableCellRenderer interface.
   * <p>
   * The class assumes that a blank String has been added to the
   * selection list, as the last item.  So, if the value is null
   * or is the blank string, the index will be set to that item.
   */
  public Component getTableCellRendererComponent(JTable table,
                                                 Object value,
                                                 boolean isSelected,
                                                 boolean hasFocus, int row,
                                                 int column) {
    String svalue = (String) value;
    int index = -1;
    if (isSelected) {
      setForeground(table.getSelectionForeground());
      super.setBackground(table.getSelectionBackground());
    }
    else {
      setForeground(table.getForeground());
      setBackground(table.getBackground());
    }
    if ( (svalue == null) || (svalue.length() == 0)) {
//      index = getItemCount() - 1;
    }
    else {
      index = MyList.indexOf(svalue);
    }
    setSelectedIndex(index);
    return this;
  }
}
/* @(#)ListSpec.java */