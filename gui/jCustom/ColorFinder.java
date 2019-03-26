/*
 * Name: ColorFinder.java
 *
 * What:
 *   This file defines a class for selecting a Color.  It is a proxy in the sense
 *   that it holds both the current color and the default color.  It is invoked to retrieve
 *   properties of a ColorDefinition.  If the current ColorDefinition can be found, it is used.
 *   If the current ColorDefinition cannot be found, the default is used.
 */
package cats.gui.jCustom;

import java.awt.Color;

import javax.swing.JComboBox;

import cats.layout.ColorList;

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
public class ColorFinder {
    /**
     * is the ColorDefinition key for the current color
     */
    private String Current;
    
    /**
     * is the ColorDefinition key for the default color
     */
    private final String Default;
    
    /**
     * is the ctor
     *
     * @param defColor is the default key.  It cannot be null.
     */
    public ColorFinder(String defColor) {
        this(defColor, defColor);
    }
    
    /**
     * is a complete ctor
     *
     * @param current is the key of the current color
     * @param defColor is the key of the default color
     */
    public ColorFinder(String current, String defColor) {
        /** good place for an assert? **/
        if (ColorList.instance().findElementbyKey(current) == null) {
            Current = new String(defColor);
        }
        else {
            Current = new String(current);
        }
        Default = new String(defColor);
    }
    
    /**
     * gets the active Color
     *
     * @return the active Color.  It will not be null.
     */
    public Color getColor() {
        return ColorList.safeGetColor(Default, Current);
    }
    
    /**
     * gets the active Color name
     *
     * @return the name of the active Color.  It will not be null.
     */
    private String getColorName() {
        return ColorList.safeGetName(Default, Current);
    }
    
    /**
     * creates a JComboBox for selecting a ColorDefinition
     *
     * @return a JComboBox over ColorList, with the active color
     * preselected.
     */
    @SuppressWarnings("rawtypes")
	public JComboBox createFinder() {
        @SuppressWarnings("unchecked")
		JComboBox box = new JComboBox(ColorList.instance().getListList());
        box.setSelectedItem(getColorName());
        return box;
    }
    
    /**
     * changes the key of the current ColorDefinition.
     *
     * @param name is the new key
     */
    public void setColor(String name) {
        Current = name;
    }
    
    /**
     * makes a copy of the ColorFinder
     *
     * @return the copy
     */
    public ColorFinder copy() {
        return new ColorFinder(Current, Default);
    }
    
    /**
     * retrieves the tag of the current ColorDefinition.  It could
     * be null, so this code must protect against that possibility.
     * @return the tag from ColorList of the ColorDefinition being used.
     */
    public String getColorTag() {
        if (Current != null) {
            return new String(Current);
        }
        return new String(Default);
    }
}
/* @(#)ColorFinder.java */