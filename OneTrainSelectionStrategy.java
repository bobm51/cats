/*
 * Name: DefaultSelectionStrategy.java
 *
 * What:
 *  This class is the SelectionStrategy for changing the status of one train.  It
 *  <ul>
 *  <li>hides all trains except for the one passed in in the constructor.
 *  <li>treats all GenericRecords which do not have the Mandatory attribute set as deleteable
 *  <li>handles unselect by changing the status of the Train to the string passed in
 *  </ul>
 */
package cats.trains;

import cats.layout.store.GenericRecord;
import cats.layout.store.SelectionStrategy;

/**
 *  This class is the SelectionStrategy for changing the status of one train.  It
 *  <ul>
 *  <li>hides all trains except for the one passed in in the constructor.
 *  <li>treats all GenericRecords which do not have the Mandatory attribute set as deleteable
 *  <li>handles unselect by changing the status of the Train to the string passed in
 *  </ul>
 *
 * <p>Title: CATS - Computer Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */
public class OneTrainSelectionStrategy implements SelectionStrategy {

  /**
   * the ctor
   */
  public OneTrainSelectionStrategy() {
  }
  
  /**
   * is called to determine if the GenericRecord can be deleted from the Vector
   * or not.
   * @param rec is the GenericRecord being queried
   * @return true if it can be.
   */
  public boolean isDeletable(GenericRecord rec) {
      return true;
  }

  /**
   * is called to query if the containing GenericRecord should appear in the
   * JTable or not.
   * @param rec is the GenericRecord being queried
   * @return true if it should appear and false if not
   */
  public boolean isSelected(GenericRecord rec) {
    return true;
  }

  /**
   * is called to hide the GenericRecord on the JTable.  Because there may be
   * linkages between the data in the StoredObject represented by the GenericRecord
   * and other StoredObjects, the GenericRecord/StoredObject cannot be simply deleted.
   * Thus, the GenericRecord is marked at DELETED and the updateRecords() method
   * of the Store will make all adjustments and possibly remove the GenericRecord
   * and StoredObject.
   * @param rec is the GenericRecord being unselected
   * 
   * @param reason is why the GenericRecord should be hidden.
   */
  public void unselect(GenericRecord rec, String reason) {
      rec.setStatus(reason);
  }
  /**
   * provides a finely grained way of preventing editing on a specific field
   * in an individual record.
   * @param rec is the record the edit request is for
   * @param field is the name of the field
   * @return true if the field can be changed by the editor
   */
  public boolean isEditable(GenericRecord rec, String field) {
    return true;
  }
}
/* @(#)OneTrainSelectionStrategy.java */