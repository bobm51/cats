/*
 * Name: ColumnData.java
 *
 * What:
 *  This class provides a data structure for describing a column in a JTable.  It is
 *  used by AbstractManagerTableModel.
 */
package cats.gui.jCustom;

/**
 *  This class provides a data structure for describing a column in a JTable.  It is
 *  used by AbstractManagerTableModel.
 *
 * <p>Title: designer</p>
 * <p>Description: A program for designing dispatcher panels</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */
public class ColumnData {
	
	/**
	 * is the column heading
	 */
    public String cTitle;
    
    /**
     * is the column width
     */
    public int cWidth;
    
    /**
     * is the placement of text in the cell
     */
    public int cAlignment;
    
    /**
     * is the underlying class of cell contents
     */
    public Class<?> cClass;
    
    /**
     * the ctor.
     * 
     * @param title is the column heading
     * @param width is the width of the column
     * @param alignment is where to place the value in the column
     * @param c is the class of objects in the column
     */
    public ColumnData(String title, int width, int alignment, Class<?> c) {
        cTitle = title;
        cWidth = width;
        cAlignment = alignment;
        cClass = c;
    }
}
/* @(#)ColoumnData.java */