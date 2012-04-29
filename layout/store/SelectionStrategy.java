/*
 * Name: SelectionStrategy.java
 *
 * What:
 *  This interface is for defining classes that are contained within a GenericRecord
 *  for querying if the GenericRecord should be visible or not on an edit screen.
 *  It is the interface for the Strategy design pattern.
 */
package cats.layout.store;

/**
 *  This interface is for defining classes that are contained within a GenericRecord
 *  for querying if the GenericRecord should be visible or not on an edit screen.
 *  It is the interface for the Strategy design pattern.
 *
 * <p>Title: CATS - Computer Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */
public interface SelectionStrategy {

    /**
     * is called to query if the containing GenericRecord should appear in the
     * JTable or not.
     * @param rec is the GenericRecord being queried
     * @return true if it should appear and false if not
     */
    public boolean isSelected(GenericRecord rec);

    /**
     * is called to hide the GenericRecord on the JTable
     * @param rec is the GenericRecord being unselected
     * 
     * @param reason is why the GenericRecord should be hidden.
     */
    public void unselect(GenericRecord rec, String reason);
    
    /**
     * is called to determine if the GenericRecord can be deleted from the Vector
     * or not.
     * @param rec is the GenericRecord being queried
     * @return true if it can be.
     */
    public boolean isDeletable(GenericRecord rec);

    /**
     * provides a finely grained way of preventing editing on a specific field
     * in an individual record.
     * @param rec is the record the edit request is for
     * @param field is the name of the field
     * @return true if the field can be changed by the editor
     */
    public boolean isEditable(GenericRecord rec, String field);
}
/* @(#)SelectionStrategy.java */