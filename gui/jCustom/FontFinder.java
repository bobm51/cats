/*
 * Name: FontFinder.java
 *
 * What:
 *   This file defines a class for selecting a Font.  It is a proxy in the sense
 *   that it holds both the current font and the default font.  It is invoked to retrieve
 *   properties of a FontDefinition.  If the current FontDefinition can be found, it is used.
 *   If the current FontDefinition cannot be found, the default is used.
 */
package cats.gui.jCustom;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JComboBox;

import cats.layout.FontList;

/**
 *   This file defines a class for selecting a Color.  It is a proxy in the sense
 *   that it holds both the current color and the default color.  It is invoked to retrieve
 *   properties of a ColorDefinition.  If the current ColorDefinition can be found, it is used.
 *   If the current ColorDefinition cannot be found, the default is used.
 *
 * <p>Title: designer</p>
 * <p>Description: A program for designing dispatcher panels</p>
 * <p>Copyright: Copyright (c) 2009, 2019</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */
public class FontFinder {
    /**
     * is the FontDefinition key for the current font
     */
    private String Current;
    
    /**
     * is the FontDefinition key for the default font
     */
    private final String Default;
    
    /**
     * is the ctor
     *
     * @param defFont is the default key.  It cannot be null.
     */
    public FontFinder(String defFont) {
        this(defFont, defFont);
    }
    
    /**
     * is a complete ctor
     *
     * @param current is the key of the current font
     * @param defFont is the key of the default font
     */
    public FontFinder(String current, String defFont) {
        /** good place for an assert? **/
        if (FontList.instance().findElementbyKey(current) == null) {
            Current = new String(defFont);
        }
        else {
            Current = new String(current);
        }
        Default = new String(defFont);
    }
    
    /**
     * gets the Color of the active Font
     *
     * @return the active Color.  It will not be null.
     */
    public Color getColor() {
        return FontList.safeGetColor(Default, Current);
    }
 
    /**
     * gets the active Font
     * @return the active Font
     */
    public Font getFont() {
        return FontList.safeGetFont(Default, Current);
    }
    
    /**
     * gets the active Font
     * 
     * @return the active Font.  It will not be null.
     */
    /**
     * gets the active Font name
     *
     * @return the name of the active Font.  It will not be null.
     */
    private String getFontName() {
        return FontList.safeGetName(Default, Current);
    }
    
    /**
     * creates a JComboBox for selecting a FontDefinition
     *
     * @return a JComboBox over FontList, with the active font
     * preselected.
     */
    @SuppressWarnings("rawtypes")
	public JComboBox createFinder() {
        @SuppressWarnings("unchecked")
		JComboBox box = new JComboBox(FontList.instance().getListList());
        box.setSelectedItem(getFontName());
        return box;
    }
    
    /**
     * changes the key of the current FontDefinition.
     *
     * @param name is the new key
     */
    public void setCurrent(String name) {
        Current = name;
    }
    
    /**
     * makes a copy of the FontFinder
     *
     * @return the copy
     */
    public FontFinder copy() {
        return new FontFinder(Current, Default);
    }
    
    /**
     * retrieves the tag of the current FontDefinition.  It could
     * be null, so this code must protect against that possibility.
     * @return the tag from FontList of the FontDefinition being used.
     */
    public String getFontTag() {
        if (Current != null) {
            return new String(Current);
        }
        return new String(Default);
    }
}
/* @(#)FontFinder.java */