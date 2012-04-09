/*
 * Name: ListManager
 *
 * What:
 *  This interface defines the methods needed for implementing a list of AbstractListElement.
 *  Implementing classes are edited via ManagerEditPane, using an AbstractManagerTableModel
 *  for controlling the editing.
 */
package cats.layout;

import java.util.Vector;

import cats.gui.jCustom.AbstractManagerTableModel;

/**
 *  This interface defines the methods needed for implementing a list of AbstractListElement.
 *  Implementing classes are edited via ManagerEditPane, using an AbstractManagerTableModel
 *  for controlling the editing.
 *
 * <p>Title: designer</p>
 * <p>Description: A program for designing dispatcher panels</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */
public interface ListManager {
    
    /**
     * returns the number of elements that have been defined.
     * @return the number of elements.
     */
    public int size();
    
    /**
     * adds a new list element.
     *
     * @param temp is the element to be added.
     * @return true if the element was added
     */
    public boolean add(AbstractListElement temp);

    /**
     * locates the element at a specific index.
     *
     * @param index is the index of the element being requested.
     *
     * @return the element at that index or null, if the index
     * is invalid.
     */
//    public AbstractListElement elementAt(int index);

    /**
     * locates an element by the field by which it is known
     * @param key is the value used as the search key
     * @return the first element found with a matching key
     * or null.
     */
    public AbstractListElement findElement(String key);
    
    /**
     * creates a Vector for listing the elements in a JComboBox
     * @return the Vector of element names.
     */
    public Vector<String> getListList();
    
    /**
     * generates a copy of the list, then uses it to
     * create an AbstractManagerTableModel for editing.
     * @return the AbstractManagerTableModel
     */
    public AbstractManagerTableModel createModel(); 

    /**
     * commits the changes in the JTable.
     */
    public void commit();

    /**
     * clears out the manager's state.
     */
    public void init();
}
/* @(#)ListManager.java */