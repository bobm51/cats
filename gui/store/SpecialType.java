/* Name: SpecialType.java
 *
 * What:
 *  This Interface defines the methods supported by all Objects which
 *  are unique for CATS that appear in a JTable cell.  The class for each
 *  must define a get method for retrieving a CellEditor and a TableCellRenderer.
 */
package cats.gui.store;

import javax.swing.DefaultCellEditor;
import javax.swing.table.TableCellRenderer;

/**
 *  This Interface defines the methods supported by all Objects which
 *  are unique for CATS that appear in a JTable cell.  The class for each
 *  must define a get method for retrieving a CellEditor and a TableCellRenderer.
 * <p>Title: designer</p>
 * <p>Description: A program for designing dispatcher panels</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */

public abstract class SpecialType {

    /**
     * returns a JTableCellEditor for editing the Object in a JTable.
     *
     * @return the editor.
     */
    public abstract DefaultCellEditor getCellEditor();

    /**
     * returns a JTableCellRenderer for rendering the Object in a JTable.
     *
     * @return the renderer.
     */
    public abstract TableCellRenderer getCellRenderer();
}
/* @(#)SpecialType.java */