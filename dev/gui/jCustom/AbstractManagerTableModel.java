/*
 * Name: AbstractManagerTableModel.java
 *
 * What:
 *  This class provides an alternative TableModel to DesignerTableModel.  Is intended to
 *  be simpler, trading off flexibility for ease of sub-classing.  It is used where the
 *  format of the data held by the table is fixed and not customized by the user.
 */
package cats.gui.jCustom;

import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import cats.layout.AbstractListElement;

/**
 *  This class provides an alternative TableModel to DesignerTableModel.  Is intended to
 *  be simpler, trading off flexibility for ease of sub-classing.  It is used where the
 *  format of the data held by the table is fixed and not customized by the user.
 *
 * <p>Title: designer</p>
 * <p>Description: A program for designing dispatcher panels</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */

public abstract class AbstractManagerTableModel extends AbstractTableModel {

    /**
     * is the contents of the table.  The implementation should make
     * a copy prior to launching the JTable.
     */
    protected Vector<AbstractListElement> Contents;
    
    /**
     * is the column information.
     */
    protected ColumnData ColumnInfo[];
    
    /**
     * is the ctor
     * @param contents are the initial contents of the JTable
     */
    public AbstractManagerTableModel(Vector<AbstractListElement> contents) {
        Contents = contents;
    }
    
    /**
     * tells the JTable how many rows are needed.
     * 
     * @return the number of Chains.
     */
    public int getRowCount() {
        return Contents.size();
    }
    
    /**
     * tells the JTable how many columns are needed.
     * 
     * @return the number of fields that can be edited.
     */
    public int getColumnCount() {
        return ColumnInfo.length;
    }
    
    /**
     * tells the JTable what heading should be used in a column.
     * 
     * @param i is the column
     * 
     * @return the column heading for column i.
     */
    public String getColumnName(int i) {
        return ColumnInfo[i].cTitle;
    }

    /**
     * tells the JTable what class of Object is in a column.
     * 
     * @param col is the column number
     * 
     * @return the Class of objects in the column
     */
    public Class<?> getColumnClass(int col) {
        return ColumnInfo[col].cClass;
    }

    /**
     * tells the JTable how wide a column is.
     * 
     * @param col is the column number
     * 
     * @return the preferred width of the column.
     */
    public int getColWidth(int col) {
        return ColumnInfo[col].cWidth;
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
        return ColumnInfo[column].cAlignment;
    }
    
    /**
     * tells the JTable what value to show in a column.
     * 
     * @param row is the row (list element)
     * @param col is the field in the list element
     * 
     * @return a value from a list element
     */
    abstract public Object getValueAt(int row, int col);

    /**
     * changes a field in a list element
     * 
     * @param val is the new value
     * @param row is the row from the JTable (list element)
     * @param col is the JTable column (field)
     */
    abstract public void setValueAt(Object val, int row, int col);
    
    /**
     * tells the JTable which cells can be edited.  Most cells can be edited,
     * but a few models will want to override this method.
     * 
     * @param row is the row number of a cell (list element)
     * @param col is a column number (field)
     * 
     * @return true - the table cell can be edited
     */
    public boolean isCellEditable(int row, int col) {
        return true;
    }

    /**
     * is a query for if the row in the table can be deleted
     * or not.  The default is that it can be deleted; however,
     * derived classes may want to override this method.
     * @param row is the candidate row for deletion
     * @return true if it can be deleted and false if it cannot.
     */
    public boolean isUnprotected(int row) {
        return true;
    }
    
    /**
     * creates and inserts a new element
     * 
     * @param row is the place in the vector
     * where the element is inserted.
     */
    public void insertElement(int row) {
        Contents.insertElementAt(createElement(), row);
        fireTableRowsInserted(row, row + 1);
    }

    /**
     * creates a new list element.
     * @return the new list element
     */
    abstract public AbstractListElement createElement();
    
    /**
     * deletes a list element.
     * 
     * @param low is the lowest numbered color to delete
     * @param high is the highest numbered color to delete (it
     * can be the same as low, if only one color is being deleted).
     */
    public void delElement(int low, int high) {
        for (int count = high - low + 1; count > 0; --count) {
            if (isUnprotected(low)) {
                Contents.remove(low);
            }
        }
        fireTableRowsDeleted(low, high);
    }
 
    /**
     * steps through all the list elements and verifies that they are acceptable.
     * @return an error String if any element is not acceptable
     */
    abstract public String verifyResults();
}
/* @(#)AbstractManagerTableModel.java */