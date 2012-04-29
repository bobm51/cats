/*
 * Name: ColorList.java
 *
 * What:
 *  This class defines a list of ColorDefinition.  It holds all the Colors used by the
 *  layout and the names by which they are known.
 *  <p>
 *  A complication with user editable lists is that what the user defines, the user can
 *  delete.  So, to protect CATS, each ColorDefinition has a "key" field that is unique
 *  for the instantiation of the ColorList.  The key field is encoded so that reserved
 *  (required) ColorDefinitions cannot be deleted.  The key field is the mechanism that
 *  clients use for retrieving ColorDefinitions.
 *  <p>
 *  There is a problem in the way the unique keys are generated.  Clients have the keys,
 *  yet the user may delete a ColorDefinition, leading to the classic dangling pointer
 *  problem.  This class contains helper routines to return the default for the class
 *  of color from the reserved set, if a dangling pointer is detected.  However, if
 *  the key is reused (which can happen), the dangling poiner will reference an
 *  incorrect ColorDefiniton.
 */
package cats.layout;

import java.awt.Color;
import java.util.Iterator;

import cats.gui.jCustom.AbstractManagerTableModel;
import cats.gui.jCustom.ColorManagerModel;
import cats.layout.xml.XMLReader;

/**
 *  This class defines a list of ColorDefinition.  It holds all the Colors used by the
 *  layout and the names by which they are known.
 *  <p>
 *  A complication with user editable lists is that what the user defines, the user can
 *  delete.  So, to protect CATS, each ColorDefinition has a "key" field that is unique
 *  for the instantiation of the ColorList.  The key field is encoded so that reserved
 *  (required) ColorDefinitions cannot be deleted.  The key field is the mechanism that
 *  clients use for retrieving ColorDefinitions.
 *  <p>
 *  There is a problem in the way the unique keys are generated.  Clients have the keys,
 *  yet the user may delete a ColorDefinition, leading to the classic dangling pointer
 *  problem.  This class contains helper routines to return the default for the class
 *  of color from the reserved set, if a dangling pointer is detected.  However, if
 *  the key is reused (which can happen), the dangling poiner will reference an
 *  incorrect ColorDefiniton.
 *
 * <p>Title: designer</p>
 * <p>Description: A program for designing dispatcher panels</p>
 * <p>Copyright: Copyright (c) 2009, 2010</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */

public class ColorList extends AbstractListManager {
  
  /**
   * the XML tag for the color of a Block with a train in it.
   */
  public static final String OCCUPIED = "OCCUPIED";
  
  /**
   * the XML tag for the color of a Block which does not have a train
   * in it and has not been reserved for another use.
   */
  public static final String EMPTY = "EMPTY";
  
  /**
   * the XML tag for the color of a Block which is reserved for a train
   * to pass through it.
   */
  public static final String RESERVED = "RESERVED";
  
  /**
   * the XML tag for the color of a Block which has been taken out of service.
   */
  public static final String OOSERVICE = "OOSERVICE";
  
  /**
   * the XML tag for the color of a Block which is being used for local
   * switching.
   */
  public static final String LOCAL = "LOCAL";
  
  /**
   * the tag for the color of a Block which has an active track warrant.
   */
  public static final String DTC = "DTC";
  
  /**
   * the XML tag for the color of an undetected Block - dark territory.
   */
  public static final String DARK = "DARK";
  
  /**
   * the XML tag for the color of a Depot.
   */
  public static final String DEPOT = "DEPOT";
  
  /**
   * the XML tag for the color of a signal that does not have a route.
   */
  public static final String NO_ROUTE = "NO_ROUTE";
  
  /**
   * the XML tag for the color of a signal showing stop.
   */
  public static final String STOP = "STOP";
  
  /**
   * the XML tag for the color of a signal showing slow down.
   */
  public static final String APPROACH = "APPROACH";
  
  /**
   * the XML tag for the color of a signal showing go.
   */
  public static final String CLEAR = "CLEAR";
  
  /**
   * the XML tag for the color of a non-existing signal.
   */
  public static final String NOSIGNAL = "NOSIGNAL";
  
  /**
   * the XML tag of the background color.
   */
  public static final String BACKGROUND = "BACKGROUND";
  
  /**
   * the XML tag of the stack color.
   */
  public static final String STACK = "STACK";
  
  /**
   * the singleton that holds the ColorDefinitions
   */
  static private ColorList ColorManager;
  
  /**
   * the accessor to the Singleton
   * 
   * @return the Singleton
   */
  public static ColorList instance() {
      if (ColorManager == null) {
          ColorManager = new ColorList();
      }
      return ColorManager;
  }
     
  /**
   * generates a copy of the list, then uses it to
   * create an AbstractManagerTableModel for editing.
   * @return the AbstractManagerTableModel
   */
  public AbstractManagerTableModel createModel() {
      return new ColorManagerModel(makeCopy());
  }
  
  /**
   * clears out the manager's state.
   */
  public void init() {
      super.init();
      add(new ColorDefinition(OCCUPIED, "Block Occupied", Color.red));
      add(new ColorDefinition(EMPTY, "Block Empty", Color.white));
      add(new ColorDefinition(RESERVED, "Block Reserved", Color.green));
      add(new ColorDefinition(OOSERVICE, "Block Out of Service", Color.magenta));
      add(new ColorDefinition(LOCAL, "Track Authority", Color.blue));
      add(new ColorDefinition(DTC, "DTC trail", Color.orange));
      add(new ColorDefinition(DARK, "Dark", Color.gray));
      add(new ColorDefinition(DEPOT, "Depot", Color.cyan));
      add(new ColorDefinition(NO_ROUTE, "Idle Aspect", Color.white));
      add(new ColorDefinition(STOP, "Stop Aspect", Color.red));
      add(new ColorDefinition(APPROACH, "Approach Aspect", Color.yellow));
      add(new ColorDefinition(CLEAR, "Clear Aspect", Color.green));
      add(new ColorDefinition(NOSIGNAL, "No Physical Signal", Color.gray));
      add(new ColorDefinition(BACKGROUND, "Background", Color.black));
      XMLReader.registerFactory(ColorDefinition.XML_TAG, new ColorDefinitionFactory());
  }

    /**
     * searches the ColorList for a ColorDefinition with a particular key
     * (internal name)
     * @param tag is the search key
     * @return the first ColorDefinition with a matching key that
     * is found or null.
     */
    public ColorDefinition findElementbyKey(String tag) {
        ColorDefinition cd;
        for (Iterator<AbstractListElement> i = TheList.iterator(); i.hasNext(); ) {
            cd = (ColorDefinition) i.next();
            if (cd.getElementKey().equals(tag)) {
                return cd;
            }
        }
        return null;
    }
    
    /**
     * is called from XML parsing to add a new color definition
     * or replace the value of an existing one.
     * @param def is the ColorDefinition to add/replace
     */
    public void replaceColor(ColorDefinition def) {
        ColorDefinition exists;
        String name;
        exists = findElementbyKey(def.getElementKey());
        if (exists == null) {
            add(def);
        }
        else {
            name = def.getColorName();
            if ((name != null) && (name.trim().length() != 0)) {
                exists.setColorName(name.trim());
            }
            if (def.getColorValue() != null) {
                exists.setColorValue(def.getColorValue());
            }
        }        
    }

    /**
     * attempts to retrieve a ColorDefinition through a key in the
     * ColorList.  It first looks for a ColorDefinition known by the
     * primaryKey.  If found, it is returned.  If the primaryKey
     * cannot be found, a search is made for the defaultKey.  If it is found,
     * it is returned.  If neither is found BLACK is returned
     * @param defaultKey is the key of the default ColorDefinition
     * @param primaryKey is the key of the current ColorDefinition.  It could be empty
     * or null.
     * @return a ColorDefinition
     */
    public static ColorDefinition getDefinition(String defaultKey, String primaryKey) {
      ColorList list = instance();
      if ((primaryKey != null) && (primaryKey.length() != 0)) {
        ColorDefinition cd = list.findElementbyKey(primaryKey);
        if (cd == null) {
          return list.findElementbyKey(defaultKey);
        }
        return cd;
      }
      return list.findElementbyKey(defaultKey);
    }

    /**
     * retrieves the Color used by a Colorfinder.
     * @param defaultKey is the key to use if there is no match on primaryKey
     * @param primaryKey is the key of the preferred ColorDefinition to look for.
     * It may be empty or null.
     * @return a Color.  It will not be null.
     */
    public static Color safeGetColor(String defaultKey, String primaryKey) {
        return getDefinition(defaultKey, primaryKey).getColorValue();
    }

    /**
     * retrieves the Name used by a Colorfinder.
     * @param defaultKey is the key to use if there is no match on primaryKey
     * @param primaryKey is the key of the preferred ColorDefinition to look for.
     * It may be empty or null.
     * @return a name (not a tag, as the keys are the tag).  It will not be null.
     */
    public static String safeGetName(String defaultKey, String primaryKey) {
        return new String(getDefinition(defaultKey, primaryKey).getColorName());
    }
    
    /**
     * retrieves the Tag that matches a name.
     * @param name is the namr of a color
     * @return a name (not a tag, as the keys are the tag).  It will not be null.
     */
    public static String getColorKey(String name) {
        return new String(((ColorDefinition) ColorManager.findElement(name)).getElementKey());
    }
}    
/* @(#)ColorList.java */