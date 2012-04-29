/*
 * Name: FontList.java
 *
 * What:
 *  This class defines a list of FontDefinitions.  It holds all the Fonts used by the
 *  layout and the names by which they are known.
 *  <p>
 *  A complication with user editable lists is that what the user defines, the user can
 *  delete.  So, to protect CATS, each FontDefinition has a "key" field that is unique
 *  for the instantiation of the FontList.  The key field is encoded so that reserved
 *  (required) FontDefinitions cannot be deleted.  The key field is the mechanism that
 *  clients use for retrieving FontDefinitions.
 *  <p>
 *  There is a problem in the way the unique keys are generated.  Clients have the keys,
 *  yet the user may delete a FontDefinition, leading to the classic dangling pointer
 *  problem.  This class contains helper routines to return the default for the class
 *  of font from the reserved set, if a dangling pointer is detected.  However, if
 *  the key is reused (which can happen), the dangling poiner will reference an
 *  incorrect FontDefiniton.
 */
package cats.layout;

import java.awt.Color;
import java.awt.Font;
import java.util.Iterator;

import cats.gui.jCustom.AbstractManagerTableModel;
import cats.gui.jCustom.FontManagerModel;
import cats.layout.xml.XMLReader;

/**
 *  This class defines a list of FontDefinitions.  It holds all the Fonts used by the
 *  layout and the names by which they are known.
 *  <p>
 *  A complication with user editable lists is that what the user defines, the user can
 *  delete.  So, to protect CATS, each FontDefinition has a "key" field that is unique
 *  for the instantiation of the FontList.  The key field is encoded so that reserved
 *  (required) FontDefinitions cannot be deleted.  The key field is the mechanism that
 *  clients use for retrieving FontDefinitions.
 *  <p>
 *  There is a problem in the way the unique keys are generated.  Clients have the keys,
 *  yet the user may delete a FontDefinition, leading to the classic dangling pointer
 *  problem.  This class contains helper routines to return the default for the class
 *  of font from the reserved set, if a dangling pointer is detected.  However, if
 *  the key is reused (which can happen), the dangling poiner will reference an
 *  incorrect FontDefiniton.
 *
 * <p>Title: designer</p>
 * <p>Description: A program for designing dispatcher panels</p>
 * <p>Copyright: Copyright (c) 2009, 2010</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */

public class FontList extends AbstractListManager {
    
    /**
     * is the internal name of the Train label font.
     */
    public static final String FONT_TRAIN = "FONT_TRAIN";

    /**
     * is the internal name of the Label font.
     */
    public static final String FONT_LABEL = "FONT_LABEL";

    /**
     * the XML tag for the font of trains which are working.
     */
    public static final String ONCALL = "ONCALL";
    
    /**
     * the XML tag for the font of trains which are working.
     */
    public static final String ACTIVE = "ACTIVE";
    
    /**
     * the XML tag for the font of the train which the cursor will move.
     */
    public static final String SELECTED = "SELECTED";
    
    /**
     * the XML tag for the font of trains which have completed their work.
     */
    public static final String TIEDUP = "TIEDUP";

    /**
     * the singleton that holds the FontDefinitions
     */
    static private FontList FontManager;

    /**
     * the name of the Font family being used by the Frame
     */
    static private Font FontFamily;

    
    /**
     * the accessor to the Singleton
     * 
     * @return the Singleton
     */
    public static FontList instance() {
        if (FontManager == null) {
            FontManager = new FontList();
        }
        return FontManager;
    }
    
    
    /**
     * generates a copy of the list, then uses it to
     * create an AbstractManagerTableModel for editing.
     * @return the AbstractManagerTableModel
     */
    public AbstractManagerTableModel createModel() {
        return new FontManagerModel(makeCopy());
    }
    
    /**
     * clears out the manager's state.
     */
    public void init() {
        super.init();
        add(new FontDefinition(FONT_LABEL, "Grid Labels", Color.white));
        add(new FontDefinition(FONT_TRAIN, "Train Identities", Color.white));
        add(new FontDefinition(ONCALL, "On Call Train", Color.lightGray));
        add(new FontDefinition(ACTIVE, "Active Train", Color.orange));
        add(new FontDefinition(SELECTED, "Selected Train", Color.cyan));
        add(new FontDefinition(TIEDUP, "Tied Down Train", Color.magenta));
        XMLReader.registerFactory(FontDefinition.XML_TAG, new FontDefinitionFactory());
    }

    /**
     * searches the FontList for a FontDefinition with a particular key
     * (internal name)
     * @param tag is the search key
     * @return the first FontDefinition with a matching key that
     * is found or null.
     */
    public FontDefinition findElementbyKey(String tag) {
        FontDefinition cd;
        for (Iterator<AbstractListElement> i = TheList.iterator(); i.hasNext(); ) {
            cd = (FontDefinition) i.next();
            if (cd.getElementKey().equals(tag)) {
                return cd;
            }
        }
        return null;
    }
    
    /**
     * is called from XML parsing to add a new font definition
     * or replace the value of an existing one.
     * @param def is the FontDefinition to add/replace
     */
    public void replaceFont(FontDefinition def) {
        FontDefinition exists;
        String name;
        exists = findElementbyKey(def.getElementKey());
        if (exists == null) {
            add(def);
        }
        else {
            name = def.getFontName();
            if ((name != null) && (name.trim().length() != 0)) {
                exists.setFontName(name.trim());
            }
            if (def.getFontColor() != null) {
                exists.setFontColor(def.getFontColor());
            }
            if (!def.getFontSize().equals("")) {
                exists.setFontSize(def.getFontSize());
            }
            if (!def.getFontStyle().equals("")) {
                exists.setFontStyle(def.getFontStyle());
            }
        }        
    }

    /**
     * attempts to retrieve a FontDefinition through a key in the
     * FontList.  It first looks for a FontDefinition known by the
     * primaryKey.  If found, it is returned.  If the primaryKey
     * cannot be found, a search is made for the defaultKey.  If it is found,
     * it is returned.  If neither is found null is returned
     * @param defaultKey is the key of the default FontDefinition
     * @param primaryKey is the key of the current FontDefinition.  It could be empty
     * or null.
     * @return a FontDefinition
     */
    public static FontDefinition getDefinition(String defaultKey, String primaryKey) {
        if ((primaryKey != null) && (primaryKey.length() != 0)) {
            FontDefinition cd = FontManager.findElementbyKey(primaryKey);
            if (cd == null) {
                return FontManager.findElementbyKey(defaultKey);
            }
            return cd;
        }
        return FontManager.findElementbyKey(defaultKey);
    }

    /**
     * retrieves the Color used by a Fontfinder.
     * @param defaultKey is the key to use if there is no match on primaryKey
     * @param primaryKey is the key of the preferred FontDefinition to look for.
     * It may be empty or null.
     * @return a Color.  It will not be null.
     */
    public static Color safeGetColor(String defaultKey, String primaryKey) {
        return getDefinition(defaultKey, primaryKey).getFontColor();
    }

    /**
     * retrieves the Name used by a Fontfinder.
     * @param defaultKey is the key to use if there is no match on primaryKey
     * @param primaryKey is the key of the preferred FontDefinition to look for.
     * It may be empty or null.
     * @return a name (not a tag, as the keys are the tag).  It will not be null.
     */
    public static String safeGetName(String defaultKey, String primaryKey) {
        return new String(getDefinition(defaultKey, primaryKey).getFontName());
    }
    
    /**
     * retrieves the Size used by a Fontfinder.
     * @param defaultKey is the key to use if there is no match on primaryKey
     * @param primaryKey is the key of the preferred FontDefinition to look for.
     * It may be empty or null.
     * @return a size.  It will not be null.
     */
//    public static int safeGetSize(String defaultKey, String primaryKey) {
//        return getDefinition(defaultKey, primaryKey).getFontSize();
//    }

    /**
     * retrieves the Style used by a Fontfinder.
     * @param defaultKey is the key to use if there is no match on primaryKey
     * @param primaryKey is the key of the preferred FontDefinition to look for.
     * It may be empty or null.
     * @return a Style.  It will not be null.
     */
//    public static int safeGetStyle(String defaultKey, String primaryKey) {
//        return getDefinition(defaultKey, primaryKey).getFontStyle();
//    }

    /**
     * retrieves the Font defined by the JFrame family, the current size,
     * and the current style
     * @param defaultKey is the key of the default FontDefinition
     * @param primaryKey is the key of the current FontDefinition
     * @return the font named primaryKey (if it exists) or named defaultKey
     * (if the primary does not exist)
     */
    public static Font safeGetFont(String defaultKey, String primaryKey) {
        return getDefinition(defaultKey, primaryKey).getFont(FontFamily.getFamily());
    }
    
    /**
     * retrieves the Tag that matches a name.
     * @param name is the name of the font being searched for
     * @return a name (not a tag, as the keys are the tag).  It will not be null.
     */
    public static String getFontKey(String name) {
        return new String(((FontDefinition) FontManager.findElement(name)).getElementKey());
    }

    /**
     * retrieves the Font family being used by the Swing JFrame
     * 
     * @return the Font family
     */
    public static Font getFrameFont() {
        return FontFamily;
    }

    /**
     * sets the default font family for the JFrame
     * @param family is the name of the family
     */
    public static void setFontFamily(Font family) {
      FontFamily = family;
    }
}
/* @(#)FontFactory.java */