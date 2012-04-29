/* What: AlignmentList.java
 *
 * What:
 *  This class creates the list of text alignment values.  These
 *  are the names of constants in SwingConstants.  The resulting
 *  Selection List is used in the JTable editor for specifying
 *  how the text field of a column should be aligned in the JTable
 *  cell.
 */
package cats.gui.store;

import java.util.Vector;

import javax.swing.SwingConstants;

/**
 *  This class creates the list of text alignment values.  These
 *  are the names of constants in SwingConstants.  The resulting
 *  Selection List is used in the JTable editor for specifying
 *  how the text field of a column should be aligned in the JTable
 *  cell.
 * <p>Title: designer</p>
 * <p>Description: A program for designing dispatcher panels</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */

public class AlignmentList extends ListSpec {
    
    /**
     * is the default alignment
     */
    static public final String DEFAULT = "CENTER";

    /**
     * is the list of alignment names.
     */
    private final static String NAMES[] = {
            DEFAULT,
            "LEADING",
            "LEFT",
            "RIGHT",
            "TRAILING"            
    };

    /**
     * is the list of corresponding values.  These must be
     * in the same order as above.
     */
    private final static int VALUES[] = {
        SwingConstants.CENTER,
        SwingConstants.LEADING,
        SwingConstants.LEFT,
        SwingConstants.RIGHT,
        SwingConstants.TRAILING
    };
    
    /**
     * the names of the alignment constants
     */
    private final Vector<String> ALIGNMENT_NAMES = arrayToVector(NAMES);
 
    /**
     * is the sub-class specific method for building the Vector of labels
     * in the Selection dialog.
     *
     * @return the Vector of Strings that forms the contents of the Selection
     * Dialog.
     */
    protected Vector<String> createSpecificList() {
        return ALIGNMENT_NAMES;
    }
    
    /**
     * is a method for converting the SwingConstant name to its value.
     * 
     * @param name is the name of the constant (from NAMES)
     * 
     * @return the named value.  If it is not found, the default is returned.
     */
    public static int findAlignment(String name) {
        if ((name == null) || (name.length() == 0)) {
            name = DEFAULT;
        }
        for (int index = 0; index < NAMES.length; ++index) {
            if (NAMES[index].equals(name)) {
                return VALUES[index];
            }
        }
        return VALUES[0];
    }
}
/* @(#)AlignmentList.java */
