/* What: StyleType.java
 *
 * What:
 *  This is a class for selecting a Font style (emphasis).  The acceptable styles are contained
 *  by FontList.  This class generates a JComboBox and associated editor and renderer.
 */
package cats.gui.store;

import java.awt.Font;
import java.util.Vector;

import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

/**
 *  This is a class for selecting a Font style (emphasis).  The acceptable styles are contained
 *  by FontList.  This class generates a JComboBox and associated editor and renderer.
 * <p>Title: designer</p>
 * <p>Description: A program for designing dispatcher panels</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */
public class StyleType extends ListSpec {
    
    /**
     * are the recognized emphasis strings
     */
    static public final String[] FONT_EMPHASIS = {
        "BOLD",
        "ITALIC",
        "BOLD_ITALIC",
        "PLAIN"
        };

    /**
     * are the numeric values of the emphasis.
     */
    static public final int[] EMPHASIS_INDEX = {
        Font.BOLD, Font.ITALIC, Font.ITALIC + Font.BOLD,
        Font.PLAIN};
    
    /**
     * is the no argument constructor.
     */
    public StyleType() {
      this("");
    }

    /**
     * is the single argument constructor.
     *
     * @param spec is a dummy String.
     */
    public StyleType(String spec) {
      super(spec);
    }

    protected Vector<String> createSpecificList() {
        return ListSpec.arrayToVector(FONT_EMPHASIS);
    }
      
    /**
     * creates a TableCellRenderer for a list of Font styles
     * @return the TableCellRenderer
     */
    static public TableCellRenderer getRenderer() {
        return new StyleType().getCellRenderer();
    }

    /**
     * creates a TableCellEditor for a list of Font styles
     * 
     * @return the TableCellEditor
     */
    static public TableCellEditor getEditor() {
        return new StyleType().getCellEditor();
    }
}
/* @(#)StyleType.java */