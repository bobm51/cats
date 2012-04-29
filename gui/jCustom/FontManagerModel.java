/*
 * Name: FontManagerModel.java
 *
 * What:
 *  This class provides an AbstractTableModel for editing
 *  the list of Fonts.
 */
package cats.gui.jCustom;

import java.awt.Color;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JLabel;

import cats.gui.store.SizeType;
import cats.gui.store.StyleType;
import cats.layout.AbstractListElement;
import cats.layout.FontDefinition;

/**
 *  This class provides an AbstractTableModel for editing
 *  the list of Fonts.
 *
 * <p>Title: designer</p>
 * <p>Description: A program for designing dispatcher panels</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */

public class FontManagerModel extends AbstractManagerTableModel {
    
    /**
     * are the column descriptions
     */
    protected ColumnData ColInfo[] = {
        new ColumnData("Name", 100, JLabel.RIGHT, String.class),
        new ColumnData("Color", 20, JLabel.RIGHT, Color.class),
        new ColumnData("Size", 50, JLabel.RIGHT, SizeType.class),
        new ColumnData("Emphasis", 50, JLabel.RIGHT, StyleType.class)
    };
    
    /**
     * is the prefix character on generating the names of new fields.  XML
     * will not accept an attribute name that begins with a digit.
     */
    private static final String LEAD_IN = "f";

    /**
     * the ctor
     * @param fontList is the initial table contents
     */
    public FontManagerModel(Vector<AbstractListElement> fontList) {
        super(fontList);
        ColumnInfo = ColInfo;
    }

    public Object getValueAt(int row, int col) {
        Object ret = null;
        
        switch (col) {
        case 0: // Font name
            ret = ((FontDefinition) Contents.elementAt(row)).getFontName();
            if (ret == null) {
                ret = new String("");
            }
            break;
            
        case 1:  // Color
            ret = ((FontDefinition) Contents.elementAt(row)).getFontColor();
            if (ret == null) {
                ret = Color.BLACK;
            }
            break;
            
        case 2:  // Size
            ret = ((FontDefinition) Contents.elementAt(row)).getFontSize();
            break;
            
        case 3:  // Style
            ret = ((FontDefinition) Contents.elementAt(row)).getFontStyle();
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
                ((FontDefinition) Contents.elementAt(row)).setFontName(
                        (String) val);
            }
            break;
            
        case 1:
            ((FontDefinition) Contents.elementAt(row)).setFontColor((Color) val);
            break;
            
        case 2:
            ((FontDefinition) Contents.elementAt(row)).setFontSize((String) val);
            break;
            
        case 3:
            ((FontDefinition) Contents.elementAt(row)).setFontStyle((String) val);
            break;
            
        default:
        }
    }

    public boolean isUnprotected(int row) {
        String key = ((FontDefinition) Contents.elementAt(row)).getElementKey();
        return key.startsWith(LEAD_IN);
    }

    /**
     * creates a new row (FontDefinition) in the table.  Anything created in
     * the GUI is not a reserved FontDefinition; thus, its key is synthesized
     * to be recognizable as user defined and not to conflict with reserved keys.
     * <p>
     * The synthesis algorithm is pretty simple.  It starts with the integer 0,
     * prefixes LEAD_IN to it and asks the FontList if it exists.  If not, then key
     * is used.  If so, the integer is incremented and the process repeated.
     * 
     * @return a blank, user defined FontDefinition
     */
    public AbstractListElement createElement() {
        FontDefinition oldFont;
        String key = null;
        boolean unique = false;
        for (int i = 0; !unique; ++i) {
            key = new String(LEAD_IN + i);
            unique = true;
            for (Iterator<AbstractListElement> iter = Contents.iterator(); iter.hasNext(); ) {
                oldFont = (FontDefinition) iter.next();
                if (oldFont.getElementKey().equals(key)) {
                    unique = false;
                    break;
                }
            }
        }
        return new FontDefinition(key);
    }

    public String verifyResults() {
        // All FontDefinitions must have names
        String name;
        for (Iterator<AbstractListElement> i = Contents.iterator(); i.hasNext() ; ) {
            name = ((FontDefinition) i.next()).getFontName();
            if ((name == null) || (name.trim().length() == 0)) {
                return "All Font definitions must have names.";
            }
        }
        return null;
    }   

}
/* @(#)FontManagerModel.java */