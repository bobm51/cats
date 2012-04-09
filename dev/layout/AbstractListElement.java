/*
 * Name: AbstractListElement.java
 *
 * What:
 *  This class provides the methods for loading accessing and saving a simple data structure.
 *  The meat is in its derived classes.
 */
package cats.layout;

import cats.layout.xml.XMLEleObject;

/**
 *  This class provides the methods for loading accessing and saving a simple data structure.
 *  The meat is in its derived classes.
 *
 * <p>Title: designer</p>
 * <p>Description: A program for designing dispatcher panels</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */

public abstract class AbstractListElement implements XMLEleObject {

    /**
     *  a true flag means that it has been saved,
     */
    protected boolean Saved = true;

    /**
     * is a derived class specific method for retrieving the "name" field
     * from the data structure.
     * @return a String which identifies the data structure to the rest of the world
     */
    abstract public String getElementName();

    /**
     * creates a copy of itself
     * @return the copy
     */
    abstract public AbstractListElement copy();
    
    /**
     * asks if the state of the Object has been saved to a file
     *
     * @return true if it has been saved; otherwise return false if it should
     * be written.
     */
    public boolean isSaved() {
        return Saved;
    }
}
/* @(#)AbstractListElement.java */