/* What: SizeType.java
 *
 * What:
 *  This is a class for selecting a Font size.  The acceptable sizes are contained
 *  by FontList.  This class generates a JComboBox and associated editor and renderer.
 */
package cats.gui.store;

import java.util.Vector;

import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

/**
 *  This is a class for selecting a Font size.  The acceptable sizes are contained
 *  by FontList.  This class generates a JComboBox and associated editor and renderer.
 * <p>Title: designer</p>
 * <p>Description: A program for designing dispatcher panels</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */

public class SizeType extends ListSpec {

    /**
     * is the font size.  It must be convertible to an integer.
     */
    static public final String FONT_SIZE[] = {
        "8", "9", "10", "11", "12", "13", "14", "15", "16",
        "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30"
    };

    /**
     * is the no argument constructor.
     */
    public SizeType() {
      super();
    }

    /**
     * is the single argument constructor.
     *
     * @param spec is a dummy String.
     */
    public SizeType(String spec) {
      super(spec);
    }

    /**
     * creates a TableCellRenderer for a list of Font sizes
     * @return the TableCellRenderer
     */
    static public TableCellRenderer getRenderer() {
        return new SizeType().getCellRenderer();
    }

    /**
     * creates a TableCellEditor for a list of Font sizes
     * 
     * @return the TableCellEditor
     */
    static public TableCellEditor getEditor() {
        return new SizeType().getCellEditor();
    }

    protected Vector<String> createSpecificList() {
        return ListSpec.arrayToVector(FONT_SIZE);
    }
}
/* @(#)SizeType.java */