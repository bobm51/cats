/* Name: RecordTableModel.java
 *
 * What:
 *  This file contains the class definition for a RecordTableModel object.  A
 *  RecordTableModel controlls the editing within a StoreEditPane, when the
 *  Records being edited are application specific.  That is, they have a
 *  varying number of fields, depending upon the data being held.
 *
 */
package cats.gui.store;

import cats.layout.store.AbstractStore;
import cats.layout.store.FieldVector;
import cats.layout.store.GenericRecord;
import cats.layout.store.RecordVector;

/**
 *  This file contains the class definition for a RecordTableModel object.  A
 *  RecordTableModel controls the editing within a StoreEditPane, when the
 *  Records being edited are application specific.  That is, they have a
 *  varying number of fields, depending upon the data being held.
 * <p>
 * Its sub-classes for the strategies used for controlling the editing.
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A model railroad dispatching program</p>
 * <p>Copyright: Copyright (c) 2003, 2010</p>
 * @author Rodney Black
 * @version $Revision$
 */
public class RecordTableModel
    extends CatsTableModel {

  /**
   * is a list of records that were deleted.
   */
//  private Vector<Object> Deleted;

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
  public RecordTableModel(RecordVector<GenericRecord> contents, FieldVector format, AbstractStore store) {
    super(contents, format, store);
//    Deleted = new Vector<Object>();
  }

  /**
   * tests a row to see if it can be deleted.
   *
   * @return true if the Field Name begins with a numeral.  Field Names
   * that begain with numerals are user added fields and can be deleted.
   */
//  public boolean isUnprotected(int nRow) {
//    return true;
//  }

  /**
   * deletes a contiguous group of Records.
   *
   * @param top is the lowest numbered row in the group.
   * @param bottom is the highest numbered row in the group.
   */
//  public void delRecord(int top, int bottom) {
//    Vector<Object> contents = getDataVector();
//    for (int count = bottom - top + 1; count > 0; --count) {
//      Deleted.add(contents.elementAt(top));
//      removeRow(top);
//    }
//  }

  /**
   * returns the Vector of deleted records.
   *
   * @return the tagged Vectors that were deleted.
   */
//  public Vector<Object> getDeletedRecords() {
//    return Deleted;
//  }
}
/* @(#)RecordTableModel.java */