/* What: FontSpec.java
 *
 * What:
 *  This class is used to generate a JComboBox over the list of defined Fonts.
 */
package cats.gui.store;

import java.util.Vector;

import cats.layout.FontList;

/**
 *  This class is used to generate a JComboBox over the list of defined Fonts.
 * <p>Title: designer</p>
 * <p>Description: A program for designing dispatcher panels</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */
public class FontSpec extends ListSpec {

	/**
	 * the no argument constructor
	 */
    public FontSpec() {
        this("");
    }
 
    /**
     * is the single argument constructor
     * @param spec is the initial selection
     */
    public FontSpec(String spec) {
        super(spec);
    }
    
    protected Vector<String> createSpecificList() {
        return FontList.instance().getListList();
    }
}
/* @(#)FontSpec.java */