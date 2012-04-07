/*
 * Name: AbstractListManager
 *
 * What:
 *  This class provides an abstract facility for holding lists of simple data structures.
 *  The list does not appear explicitly in the XML file, but its elements do.  Thus, the
 *  doneXML() method of each element must add themselves to the concrete list.
 */
package cats.layout;

import java.util.Enumeration;
import java.util.Vector;

import cats.gui.jCustom.AbstractManagerTableModel;

/**
 *  This class provides an abstract facility for holding lists of simple data structures.
 *  The list does not appear explicitly in the XML file, but its elements do.  Thus, the
 *  doneXML() method of each element must add themselves to the concrete list.
 *
 * <p>Title: designer</p>
 * <p>Description: A program for designing dispatcher panels</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */
abstract public class AbstractListManager implements ListManager {
    
    /**
     * The list being wrapped
     */
    protected Vector<AbstractListElement> TheList;

    /**
     * A copy of the list to allow editing to be
     * cancelled.
     */
    protected Vector<AbstractListElement> TheListCopy;
        
   /**
     * the constructor.
     */
    public AbstractListManager() {
        TheList = new Vector<AbstractListElement>();
        init();
    }
    
    /**
     * adds a new list element.
     *
     * @param temp is the element to be added.
     * @return true if the element was added
     */
    public boolean add(AbstractListElement temp) {
        TheList.add(temp);
        return true;
    }

    /**
     * returns the number of elements that have been defined.
     * @return the number of elements.
     */
    public int size() {
        return TheList.size();
    }
 
    /**
     * creates an Enumeration over the elements.
     * @return the Enumeration.
     */
//    private Enumeration elements() {
//        return TheList.elements();
//    }
    
    /**
     * locates the element at a specific index.
     *
     * @param index is the index of the element being requested.
     *
     * @return the element at that index or null, if the index
     * is invalid.
     */
//    public AbstractListElement elementAt(int index) {
//        if ( (index >= 0) && (index < TheList.size())) {
//            return (AbstractListElement) TheList.elementAt(index);
//        }
//        return null;
//    }

    /**
     * locates an element by the field by which it is known
     * @param key is the value used as the search key
     * @return the first element found with a matching key
     * or null.
     */
    public AbstractListElement findElement(String key) {
        AbstractListElement temp;
        if (key != null) {
            for (Enumeration<AbstractListElement> iter = TheList.elements(); iter.hasMoreElements(); ) {
                temp = iter.nextElement();
                if (key.equals(temp.getElementName())) {
                    return temp;
                }
            }
        }
        return null;
    }
    
    /**
     * creates a Vector for listing the elements in a JComboBox
     * @return the Vector of element names.
     */
    public Vector<String> getListList() {
        Vector<String> tags = new Vector<String>();
        AbstractListElement temp;
        
        for (Enumeration<AbstractListElement> iter = TheList.elements(); iter.hasMoreElements(); ) {
            temp = iter.nextElement();
            tags.add(temp.getElementName());
        }
        return tags;
    }

    /**
     * generates a copy of the list, then uses it to
     * create an AbstractManagerTableModel for editing.
     * @return the AbstractManagerTableModel
     */
    abstract public AbstractManagerTableModel createModel(); 

    /**
     * makes a copy of the list
     * @return a copy of the list
     */
    public Vector<AbstractListElement> makeCopy() {
        TheListCopy = new Vector<AbstractListElement>();
        for (Enumeration<AbstractListElement> iter = TheList.elements(); iter.hasMoreElements(); ) {
            TheListCopy.add(iter.nextElement().copy());
        }
        return TheListCopy;
    }
    
    /**
     * commits the changes in the JTable.
     */
    public void commit() {
        TheList = TheListCopy;
        TheListCopy = null;
    }
       
    /**
     * tells the XMLEleObject that no more setValue or setObject calls will
     * be made; thus, it can do any error checking that it needs.
     *
     * @return null, if it has received everything it needs or an error
     * string if something isn't correct.
     */
    public String doneXML() {
        return null;
    }
    
    /**
     * clears out the manager's state.
     */
    public void init() {
        TheList.clear();
    }
}
/* @(#)AbstractListManager.java */