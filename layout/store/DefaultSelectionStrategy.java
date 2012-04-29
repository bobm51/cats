/*
 * Name: DefaultSelectionStrategy.java
 *
 * What:
 *  This class is the default class for implementing the GenericRecord
 *  selection strategy.  It
 *  <ul>
 *  <li>treats all GenericRecords as visible
 *  <li>treats all GenericRecords which do not have the Mandatory attribute set as deleteable
 *  <li>handles unselect by deleting the GenericRecord
 *  </ul>
 */
package cats.layout.store;

/**
 *  This class is the default class for implementing the GenericRecord
 *  selection strategy.  It
 *  <ul>
 *  <li>treats all GenericRecords as visible
 *  <li>treats all GenericRecords which do not have the Mandatory attribute set as deleteable
 *  <li>handles unselect by deleting the GenericRecord
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
public class DefaultSelectionStrategy implements SelectionStrategy {

    /**
     * is the RecordVector that holds the GenericRecords being processed
     */
    protected final RecordVector<GenericRecord> Store;

    /**
     * is the ctor
     * @param parent is the Vector that the strategy applies to
     */
    public DefaultSelectionStrategy(RecordVector<GenericRecord> parent) {
        Store = parent;
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
        return rec.getStatus() != GenericRecord.DELETED;
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
        rec.setStatus(GenericRecord.DELETED);
    }
    
    /**
     * provides a finely grained way of preventing editing on a specific field
     * in an individual record.
     * @param rec is the record the edit request is for
     * @param field is the JTable column name of the field
     * @return true
     */
    public boolean isEditable(GenericRecord rec, String field) {
      return true;
    }
}
/* @(#)DefaultSelectionStrategy.java */