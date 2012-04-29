/*
 * Name: ColorManagerModel.java
 *
 * What:
 *  This class provides an AbstractTableModel for editing
 *  the list of Colors.
 */
package cats.gui.jCustom;

import java.awt.Color;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JLabel;

import cats.layout.AbstractListElement;
import cats.layout.ColorDefinition;

/**
 *  This class provides an AbstractTableModel for editing
 *  the list of Colors.
 *
 * <p>Title: designer</p>
 * <p>Description: A program for designing dispatcher panels</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */

public class ColorManagerModel extends AbstractManagerTableModel {
    
    /**
     * are the column descriptions
     */
    protected ColumnData ColInfo[] = {
        new ColumnData("Name", 100, JLabel.RIGHT, String.class),
        new ColumnData("Color", 20, JLabel.RIGHT, Color.class)
    };
    
    /**
     * is the prefix character on generating the names of new fields.  XML
     * will not accept an attribute name that begins with a digit.
     */
    private static final String LEAD_IN = "f";

    /**
     * the ctor
     * @param colorList is the initial table contents
     */
    public ColorManagerModel(Vector<AbstractListElement> colorList) {
        super(colorList);
        ColumnInfo = ColInfo;
    }

    public Object getValueAt(int row, int col) {
        Object ret = null;
        
        switch (col) {
        case 0: // Color name
            ret = ((ColorDefinition) Contents.elementAt(row)).getColorName();
            if (ret == null) {
                ret = new String("");
            }
            break;
            
        case 1:  // Color
            ret = ((ColorDefinition) Contents.elementAt(row)).getColorValue();
            if (ret == null) {
                ret = Color.BLACK;
            }
            break;
            
        default:
        }
        return ret;
    }

    public void setValueAt(Object val, int row, int col) {
        switch (col) {
        case 0:
            if (((String)val).length() == 0) {
                val = null;
            }
            else {
                ((ColorDefinition) Contents.elementAt(row)).setColorName(
                        (String) val);
            }
            break;
            
        case 1:
            ((ColorDefinition) Contents.elementAt(row)).setColorValue((Color) val);
            break;
            
        default:
        }
    }

    public boolean isUnprotected(int row) {
        String key = ((ColorDefinition) Contents.elementAt(row)).getElementKey();
        return key.startsWith(LEAD_IN);
    }

    /**
     * creates a new row (ColorDefinition) in the table.  Anything created in
     * the GUI is not a reserved ColorDefinition; thus, its key is synthesized
     * to be recognizable as user defined and not to conflict with reserved keys.
     * <p>
     * The synthesis algorithm is pretty simple.  It starts with the integer 0,
     * prefixes LEAD_IN to it and asks the ColorList if it exists.  If not, then key
     * is used.  If so, the integer is incremented and the process repeated.
     * 
     * @return a blank, user defined ColorDefinition
     */
    public AbstractListElement createElement() {
        ColorDefinition oldColor;
        String key = null;
        boolean unique = false;
        for (int i = 0; !unique; ++i) {
            key = new String(LEAD_IN + i);
            unique = true;
            for (Iterator<AbstractListElement> iter = Contents.iterator(); iter.hasNext(); ) {
                oldColor = (ColorDefinition) iter.next();
                if (oldColor.getElementKey().equals(key)) {
                    unique = false;
                    break;
                }
            }
        }
        return new ColorDefinition(key);
    }

    public String verifyResults() {
        // All ColorDefinitions must have names
        String name;
        for (Iterator<AbstractListElement> i = Contents.iterator(); i.hasNext() ; ) {
            name = ((ColorDefinition) i.next()).getColorName();
            if ((name == null) || (name.trim().length() == 0)) {
                return "All Color definitions must have names.";
            }
        }
        return null;
    }   
}
/* @(#)ColorManagerModel.java */