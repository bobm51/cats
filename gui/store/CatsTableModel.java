/* Name: CatsTableModel.java
 *
 * What:
     *  This file contains the class definition for an AbstractTableModel object.  An
 *  AbstractTableModel controlls the editing within a StoreEditPane.
 *
 */
package cats.gui.store;

import java.util.Iterator;
import javax.swing.table.AbstractTableModel;
import cats.layout.store.AbstractStore;
import cats.layout.store.FieldInfo;
import cats.layout.store.FieldVector;
import cats.layout.store.GenericRecord;
import cats.layout.store.RecordVector;

/**
 *  This file contains the class definition for an AbstractTableModel object.  An
 *  AbstractTableModel directs the editing within a StoreEditPane.
 *  @see cats.gui.store.StoreEditPane
 *  @see cats.layout.store.AbstractStore
 * <p>
 * Its sub-classes for the strategies used for controlling the editing.
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A model railroad dispatching program</p>
 * <p>Copyright: Copyright (c) 2003, 2009, 2010</p>
 * @author Rodney Black
 * @version $Revision$
 */
public class CatsTableModel
    extends AbstractTableModel {

  /**
   * is the Vector of Records.  There is one record for each element in the
   * Vector.  Each Record is also a Vector of fields.
   */
  protected RecordVector<GenericRecord> Contents;

  /**
   * is the Vector of FieldInfo that describes each Field (column).
   */
  protected FieldVector Formatting;

  /**
   * is the RecordStore being edited.
   */
  protected AbstractStore Store;

  /**
   * is the number of visible columns
   */
  private final int NumCols;
  
  /**
   * is the constructor.
   *
   * @param contents are the data records being edited.  Because this object
   * manipulates the Vector, the caller should be certain to make a copy
   * before instantiating this object.
   *
   * @param format is the formatting information for the contents
   * @param store is the AbstractStore being edited
   */
  public CatsTableModel(RecordVector<GenericRecord> contents, FieldVector format, AbstractStore store) {
    Contents = contents;
    Formatting = format;
    Store = store;
    int cols = 0;
    
    /* Count the number of visible columns.  This cannot change. */
    for (Iterator<FieldInfo> iter = Formatting.iterator(); iter.hasNext(); ) {
        if (iter.next().getVisible()) {
            ++cols;
        }
    }
    NumCols = cols;
  }

  /**
   * is called to get a copy of the GenericRecords being edited.  This should
   * only be called as part of verification.
   * 
   * @return the GenericRecords
   */
  public RecordVector<GenericRecord> getContents() {
      return Contents;
  }
  
  /**
   * counts the number of columns.  The number is immutable throughout
   * the life of the TableModel; thus, it is computed when the TableModel
   * is created.
   * 
   * @return the immutable number of columns
   */
  public int getColumnCount() {
      return NumCols;
  }

  /**
   * counts the number of visible rows.  It may change, as rows are added and
   * deleted.
   * 
   * @return the number of visible rows
   */
  public int getRowCount() {
      int rows = 0;
      for (Iterator<GenericRecord> iter = Contents.iterator(); iter.hasNext(); ) {
          if (Contents.isVisible(iter.next())) {
              ++rows;
          }
      }
      return rows;
  }

  /**
   * return the value from the requested visible record at the requested
   * visible column.  The element at that place is a tag:value pair.  The value
   * half is what is of interest.
   * 
   * @param r is the row index in the JTable
   * @param c is the column index in the JTable
   * @return the value at that place.  It may have to be cast to the right
   * type, per the equivalent format field.
   */
  public Object getValueAt(int r, int c) {
      int column = toColumn(c);
      int row = toRow(r);
      FieldInfo desc = Formatting.get(column);
      Object o = Contents.elementAt(row).findValue(desc.getKeyField());
      Class<?> colClass = desc.getFieldClass();
      if (ClassSpec.class.isAssignableFrom(colClass)) {
          o = ClassSpec.normalizeClassName(o.toString());
      }
      return o;
  }

  /**
   * sets the value at the requested visible record at the requested visible
   * column.  The element at that place is a tag:value pair.  It may be
   * necessary to cast the value type.
   * 
   * @param aValue is the new value
   * @param r is the index of the requested visible row
   * @param c is the index of the requested column
   */
  public void setValueAt(Object aValue, int r, int c) {
    int column = toColumn(c);
    int row = toRow(r);
    GenericRecord rec = Contents.elementAt(row);
    FieldInfo desc = Formatting.get(column);
    Class<?> colClass = desc.getFieldClass();
    if (ClassSpec.class.isAssignableFrom(colClass)) {
      aValue = ClassSpec.toClass((String) aValue);
    }
    rec.findPair(desc.getKeyField()).FieldValue = aValue;
    if (!GenericRecord.CREATED.equals(rec.getStatus())) {
      Contents.elementAt(row).setStatus(GenericRecord.CHANGED);
    }
  }
  
  /**
   * converts a JTable column number to a Contents column number. If there
   * are no visible columns, this will return the index of the last column. 
   * @param c is the JTable column number
   * @return the number of the "cth" visible column
   */
  protected int toColumn(int c) {
      int cols = 0;
      int stepper = 0;
      for (Iterator<FieldInfo> iter = Formatting.iterator(); iter.hasNext(); ) {
          if (iter.next().getVisible()) {
              if (cols == c) {
                  break;
              }
              ++cols;
          }
          ++stepper;
      }
      return stepper;
  }
  
  /**
   * converts a JTable row number to a Contents row number.  The mapping is
   * controlled by the abstract method isVisible().  Any record which is not visible is
   * skipped in the count.
   * @param r is the index of the row in the JTable
   * @return the index of the equivalent record in the RecordVector
   */
  protected int toRow(int r) {
      int rows = 0;
      int stepper = 0;
      for (Iterator<GenericRecord> iter = Contents.iterator(); iter.hasNext(); ) {
          if (Contents.isVisible(iter.next())) {
              if (rows == r) {
                  break;
              }
              ++rows;
          }
          ++stepper;
      }
      return stepper;            
  }
  
  /**
   * tells the JTable what heading should be used in a column.
   * 
   * @param i is the column
   * 
   * @return the column heading for column i.
   */
  public String getColumnName(int i) {
      int col = toColumn(i);
      return Formatting.elementAt(col).getLabel();
  }
  
  /**
   * tells the JTable what class of Object is in a column.
   * 
   * @param col is the column number
   * 
   * @return the Class of objects in the column
   */
  public Class<?> getColumnClass(int col) {
      int index = toColumn(col);
      Class<?> c = Formatting.elementAt(index).getFieldClass();
      return c;
  }
  
  /**
   * tells the JTable how wide a column is.
   * 
   * @param col is the column number
   * 
   * @return the preferred width of the column.
   */
  public int getColWidth(int col) {
      int index = toColumn(col);
      return Formatting.elementAt(index).getWidth();
  }
  
  /**
   * is called to retrieve the alignment of the text fields within
   * a column.
   * 
   * @param column is the index of the column being queried.
   * 
   * @return the SwingConstant for the alignment
   */
  public int getColumnAlignment(int column) {
      int index = toColumn(column);
      String align = Formatting.elementAt(index).getAlignment();
      if (align.length() == 0) {
          align = AlignmentList.DEFAULT;
      }
      return AlignmentList.findAlignment(align);
  }

  /**
   * is called to retrieve the tag on a field at a specific
   * column.  The field order does not change while the table is
   * active.  Thus, the requested column is the index of the field
   * in the model.
   * @param index is the index into the vector of fields that describe
   * each column.  Visibility must be considered.
   * @return the tag on the field
   */
  public String getColumnIdentifier(int index) {
      return Formatting.elementAt(toColumn(index)).getKeyField();
  }
  
  /**
   * updates the column widths in the field descriptions.
   *
   * @param widths is an array of int's, one element for each column.
   */
  public void saveWidths(int[] widths) {
      int tCol = 0;
      for (int col = 0; col < Formatting.size(); ++col) {
          if (Formatting.elementAt(col).getVisible()) {
              (Formatting.elementAt(col)).setWidth(widths[tCol++]);
          }
      }
  }
  
  /**
   * tells the JTable which cells can be edited.  Most cells can be edited,
   * but a few models will want to override this method.
   * 
   * @param row is the row number of a cell (list element) in the JTable
   * @param col is a column number (field)
   * 
   * @return true - the table cell can be edited
   */
  public boolean isCellEditable(int row, int col) {
      int index = toColumn(col);
      boolean b = Formatting.elementAt(index).getEdit();
      if (b) {
        b = Contents.isFieldEditable(Contents.get(toRow(row)), getColumnIdentifier(col));
      }
      else {
        b = GenericRecord.CREATED.equals(Contents.get(toRow(row)).getStatus());
      }
      return b;
  }

  /**
   * is a query for if the row in the table can be deleted
   * or not.  The default is that it can be deleted; however,
   * derived classes may want to override this method.
   * @param row is the index of the GenericRecord in Contents, not on the
   * JTable.
   * @return true if it can be deleted and false if it cannot.
   */
  public boolean isProtected(int row) {
      return !Contents.isUnProtected(Contents.elementAt(row));
  }
  
  /**
   * creates and inserts a new element
   * 
   * @param row is the place in the vector
   * where the element is inserted.
   */
  public void insertRecord(int row) {
      GenericRecord newRec = Formatting.createDefaultRecord(FieldInfo.DATARECORD, null);
      newRec.setStatus(GenericRecord.CREATED);
      Contents.insertElementAt(newRec, toRow(row));
      fireTableRowsInserted(row, row + 1);
  }

  /**
   * deletes a list element.  The deletion is from highest index to lowest
   * because after an element is deleted, all the higher ones move down.
   * 
   * @param low is the lowest numbered row to delete
   * @param high is the highest numbered row to delete (it
   * can be the same as low, if only one row is being deleted).
   * @param status is a String that explains why the record should be deleted
   */
  public void delRecord(int low, int high, String status) {
      int first = toRow(low);
      int last = toRow(high);
      GenericRecord rec;
      for (int i = last; i >= first; --i) {
          rec = Contents.elementAt(i);
          if (!isProtected(i) && Contents.isVisible(rec)) {
              Contents.hide(rec, status);
          }
      }
      fireTableRowsDeleted(low, high);
  }

  /**
   * moves a block of Generic Records down in the Vector.  The table model displays
   * a Vector of GenericRecords.  Some of these GenericRecords may not be visible.  However,
   * it is desirable to retain the ordering between visible and non-visible GenericRecords.
   * When a visible GenericRecord is moved, all the invisible GenricRecords that follow it
   * (up to the next visible GenericRecord) should move with it.  Thus, the visible GenericRecord
   * acts as an anchor for itself and possibly more GenericRecords.
   * <p>
   * This algorithm moves the anchor (and attached blocks) down (to lower indexes) "n" visible
   * GenericRecords.  Movement is always down.  There are several reasons for this:
   * <ul>
   * <li>After removing an element from a Java Vector, the indexes of all elements after it
   * shift to the next smaller value.  By removing from after the insertion point, the index
   * of the insertion point does not change.
   * <li>As long as the insertion point is not a negative number (and the blocks to move exist),
   * the insertion point is guaranteed to exist.
   * <li>Moving a block up one visible GenericRecord is accomplished by moving the anchor
   * (and associated invisible GenericRecords) to in front of the block being moved.
   * </ul>
   * @param bottom is the index of bottom (lowest numbered) GenericRecord to move, in the index space
   * of visible GenericRecords (i.e. as known by the JTable)
   * @param size is the number of visible GenericRecords (i.e. anchors) to move
   * @param dest is the index of the visible GenericRecord, in the index space of visible
   * GenericRecords, of the insertion point.  All GenericRecords in the moved block will be
   * inserted immediately before the dest visible GenericRecord
   */
  private void moveRows(int bottom, int size, int dest) {
      GenericRecord rec;
      int insert = toRow(dest);   // the index into Contents of the insertion point
      int count = 0;              // a counter of visible records
      int pull = toRow(bottom) + 1;   // the index into Contents of where records are being moved from
      if ((dest >= 0) && (bottom > dest) && (size > 0)) {
          // this locates the beginning (highest index) of the block being moved
          while (pull < Contents.size()) {
              rec = Contents.elementAt(pull);
              if (Contents.isVisible(rec)) {
                  if ((++count) == size) {
                      break;
                  }
              }
              ++pull;
          }

          // At this point pull is the index of the next visible record or off the Vector,
          // so the last GenericRecord in the block is one less
          --pull;

          // The indexes do not have to be adjusted.  Pull from pull and add at insert until
          // the record corresponding to bottom has been moved
          for (count = 0; count < size;) {
              rec = Contents.remove(pull);
              Contents.add(insert, rec);
              if (Contents.isVisible(rec)) {
                  ++count;
              }
          }
      }
  }
  
  /**
   * moves a contiguous group of rows up one row in the JTable (i.e.
   * to lower indices).
   *
   * @param top is the lowest numbered row to be moved.
   * @param bottom is the highest numbered row to be moved.
   */
  public void moveUp(int top, int bottom) {
    moveRows(top, bottom - top + 1, top - 1);
  }

  /**
   * moves a contiguous group of rows down one row.
   *
   * @param top is the lowest numbered row to be moved.
   * @param bottom is the highest numbered row to be moved.
   */
  public void moveDown(int top, int bottom) {
    moveRows(bottom + 1, 1, top);
  }

  /**
   * Moves a block of columns to lower indices.  It is the same algorithm
   * as moveRows except it operates on formatting, the indexes are in
   * Formatting, and the visibility test is different.
   * @param left is the lowest index FieldInfos in the block
   * @param size is the number of visible FieldInfos to move
   * @param dest is the where left should be moved to
   */
  private void moveColumns(int left, int size, int dest) {
      FieldInfo field;
      int insert = dest;   // the index into Formatting of the insertion point
      int count = 0;              // a counter of visible FieldInfos
      int pull = left + 1;   // the index into Formatting of where records are being moved from
      if ((dest >= 0) && (left > dest) && (size > 0)) {
          // this locates the beginning (highest index) of the block being moved
          while (pull < Formatting.size()) {
              field = Formatting.elementAt(pull);
              if (field.getVisible()) {
                  if ((++count) == size) {
                      break;
                  }
              }
              ++pull;
          }

          // At this point pull is the index of the next visible FieldInfo or off the Vector,
          // so the last FieldInfo in the block is one less
          --pull;

          // The indexes do not have to be adjusted.  Pull from pull and add at insert until
          // the FieldInfo corresponding to left has been moved
          for (count = 0; count < size;) {
              field = Formatting.remove(pull);
              Formatting.add(insert, field);
              if (field.getVisible()) {
                  ++count;
              }
          }
      }        
  }

  /**
   * steps through the Vector of FieldInfos and arranges them in the order
   * specified by ids.
   * @param ids is the order of the columns (FieldInfos) when closing the JTable.
   * Each column has the tag of the FieldInfo that describes it.
   */
  public void reorderFormat(String[] ids) {
      FieldInfo field;
      int index = 0;
      int mark;
      String match;
      for (int col = 0; col < ids.length; ++col) {
          // find the next visible FieldInfo
          field = Formatting.elementAt(index);
          while (!field.getVisible()) {
              field = Formatting.elementAt(++index);
          }
          
          // if not done, see if it has been moved
          match = ids[col];
          if (!field.getKeyField().equals(match)) {
              // locate where the named FieldInfo was
              for (mark = index + 1; !Formatting.elementAt(mark).getKeyField().equals(match);
              ++mark) {
                  // do nothing
              }
              moveColumns(mark, 1, index);
          }
          ++index;
      }
  }
  
  /**
   * steps through all the GenericRecords and verifies that they are acceptable.
   * @return an error String if any GenericRecord is not acceptable
   */
  public String verifyResults() {
      return Store.checkConsistency(this);
  }
  
  /**
   * steps through all the GenericRecords in a RecordVector and verifies that
   * they are acceptable.  This is the default verification scheme.  It assumes
   * that the value of the keys is a String.  It checks that each value is
   * not the empty string ("") and is unique.  It terminates on the first
   * error.
   * @return an error String if any GenericRecord is not acceptable
   */
  public String defaultVerifyResults() {
    int recordCount = Contents.size();
    String keyTag = Formatting.getKeyField();
    String label = Formatting.getFieldInfo(keyTag).getLabel();
    String key1;
    String key2;
    GenericRecord gr;

    // The first check is to be sure all key fields are not blank.
    for (int rec = 0; rec < recordCount; ++rec) {
      gr = Contents.get(rec);
      if (Contents.isVisible(gr)) {
        key1 = (String) gr.findValue(keyTag);
        if ((key1 == null) || key1.trim().equals("")) {
          return new String("Row " + (rec + 1) + " needs a " + label);
        }
      }
    }

    // The next check is to be sure there are no duplicates.
    for (int lower = 0; lower < recordCount; ++lower) {
      gr = Contents.get(lower);
      if (Contents.isVisible(gr)) {
        key1 = ((String) gr.findValue(keyTag)).trim();
        for (int test = lower + 1; test < recordCount; ++test) {
          gr = Contents.get(test);
          if (Contents.isVisible(gr)) {
            key2 = ((String) gr.findValue(keyTag)).trim();
            if (key1.equals(key2)) {
              return new String("Multiple " + label + " have the value " + key1);
            }
          }
        }
      }
    }
    return null;        
  }
  
  /**
   * is called to retrieve the preferred width of a column.  The width is
   * taken from the Vector of FieldInfos.
   *
   * @param nCol is the column being queried.
   *
   * @return the preferred width.
   */
//  public int getColumnWidth(int nCol) {
//    int width = FieldInfo.MEDIUM_WIDTH;
//    if ( (nCol >= 0) && (nCol < FieldDescriptions.size())) {
//      width = FieldDescriptions.elementAt(nCol).getWidth();
//    }
//    return width;
//  }

  /**
   * tests a row to see if it can be deleted.
   * @param nRow is the row being examined.
   *
   * @return true if the Field Name begins with a numeral.  Field Names
   * that begain with numerals are user added fields and can be deleted.
   */
//  public abstract boolean isUnprotected(int nRow);

  /**
   * deletes a contiguous group of Records.
   *
   * @param top is the lowest numbered row in the group.
   * @param bottom is the highest numbered row in the group.
   */
//  public abstract void delRecord(int top, int bottom);

  /**
   * is called to determine if a column should be shown or not.
   *
   * @param nCol is the View column index.
   *
   * @return true if the column is visible.
   */
//  public boolean isColumnVisible(int nCol) {
//    if ((nCol >= 0) && (nCol < FieldDescriptions.size())) {
//      return (FieldDescriptions.elementAt(nCol).getVisible());
//    }
//    return false;
//  }

  /**
   * is called to retrieve the column index (Field index) for a particular
   * FieldInfo.
   *
   * @param fieldName is the Tag on the Field being requested.
   *
   * @return the index of the FieldInfo or -1 if it is not found.
   */
//  public int getFieldColumn(String fieldName) {
//    for (int result = 0; result < getColumnCount(); ++result) {
//      if (fieldName.equals( FieldDescriptions.elementAt(result).
//          findValue(FieldInfo.KEY_TAG))) {
//        return result;
//      }
//    }
//    return -1;
//  }
}
/* @(#)CatsTableModel.java */