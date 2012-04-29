/* Name: CtcFont.java
 *
 * What: This file contains the CtcFont class - a wrapper around the Java
 *   Font class, providing a level of indirection, so that all uses
 *   of a particular Font will change when the referenced Font changes.
 *   It includes the Font family and Font size.
 *
 * Special Considerations:
 */
package cats.gui;

import cats.layout.FontDefinition;
import cats.layout.FontList;
import cats.layout.xml.*;
import java.awt.Font;

/**
 * defines a class for associating the fonts of objects on the dispatcher
 * panel with fonts.  These are not constants, but
 * variables.  The reason is so that the user can modify them to suit
 * the characteristics of the display.
 *
 * It also contains a couple of class-independent, static methods for
 * working with lists of strings.
 *
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2004, 2011</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */
public class CtcFont
    implements XMLEleFactory, XMLEleObject {
  /**
   * is returned when a font cannot be found.
   */
  public static final int NOT_FOUND = -1;
  private static final String STYLE = "FONT_STYLE";
  private static final String SIZE = "FONT_SIZE";
  private static final String[] FONT_STYLE = {
      "BOLD",
      "ITALIC",
      "PLAIN"
  };
  private static final int[] STYLE_INDEX = {
      Font.BOLD,
      Font.ITALIC,
      Font.PLAIN
  };

  private static String FONT_SIZE[];

  private static final int[] SIZE_INDEX = {
      8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25,
      26, 27, 28, 29, 30
  };

  //The current font
  private Font TheFamily;

  // The current index into the arrays of Font name
  // private int TheName;

  // The current index into the arrays of Font size
  private String TheSize;

  // The current index into the arrays of Font style
  private String TheStyle;

  // The tag for this Object in an XML Element
  private String XMLTag;

  // The name of the default Font.
  private String FontName;

  // a true flag meaning that all the attributes and the text field were
  // received.
  private boolean Complete;

  /**
   * construct an object that can be used for a displayable object.
   *
   * @param     xmlTag is the XML tag for this Object.
   * @param     defFont is the default Font.
   */
  public CtcFont(String xmlTag, Font defFont) {

    FONT_SIZE = new String[SIZE_INDEX.length];
    for (int index = 0; index < SIZE_INDEX.length; ++index) {
      FONT_SIZE[index] = new String(String.valueOf(SIZE_INDEX[index]));
    }
    TheFamily = defFont;
    FontName = TheFamily.getFamily();
    initStyle(TheFamily.getStyle());
    initSize(TheFamily.getSize());
    XMLTag = xmlTag;
  }
  
  /**
   * retrieve the Font.
   *
   * @return The current value of the Font.
   */
  public Font grabFont() {
    return TheFamily;
  }

  /**
   * initializes the Font style.  If the style is not an accepted value,
   * then it is ignored.  So, this method converts the internal number
   * to an index into the style arrays.
   *
   * @param style is the numeric value of the initial style.
   */
  private void initStyle(int style) {
    int where = findInt(style, STYLE_INDEX);
    if (where == NOT_FOUND) {
      TheStyle = "PLAIN";
    }
    else {
        TheStyle = FONT_STYLE[where];
    }
  }

  /**
   * initializes the Font size.  If the size is not within the accepted
   * values, it replaces the smallest of the accepted values.
   *
   * @param size is the numeric value of the initial size.
   */
  private void initSize(int size) {
    int where = findInt(size, SIZE_INDEX);
    if (where != NOT_FOUND) {
      TheSize = String.valueOf(size);
    }
    else {
      FONT_SIZE[0] = String.valueOf(size);
    }
  }

  /**
   * locates a particular Font.  If the environment supports it,
   * then change the current properties of the Font.  This method assumes
   * that the parameters have been validated before being called.
   *
   * @param family is an index into the List of Font names
   * @param style is the Font style
   * @param size is the Font size
   *
   * @return null if the Font is found; otherwise, return an error String
   */
  private String locateFont(int family, String style, String size) {
	  Font newFont = new Font(FontName, STYLE_INDEX[findString(style, FONT_STYLE)], Integer.parseInt(size));
	  TheFamily = newFont;
	  TheStyle = style;
	  TheSize = String.valueOf(size);
	  return null;
  }

  /*
   * tells the factory that an XMLEleObject is to be created.  Thus,
   * its contents can be set from the information in an XML Element
   * description.
   */
  public void newElement() {
    Complete = false;
  }

  /**
   * gives the factory an initialization value for the created XMLEleObject.
   *
   * @param tag is the name of the attribute.
   * @param attrValue is it value.
   *
   * @return null if the tag:value are accepted; otherwise, an error
   * string.
   */
  public String addAttribute(String tag, String attrValue) {
    String resultMsg = null;
    if (tag.equals(STYLE)) {
      if ( (findString(attrValue, FONT_STYLE)) == NOT_FOUND) {
        resultMsg = new String("Unrecognized Font Style: " + attrValue);
      }
      else {
        TheStyle = attrValue;
      }
    }
    else if (tag.equals(SIZE)) {
      if ( (findString(attrValue, FONT_SIZE)) == NOT_FOUND) {
        resultMsg = new String("Unrecognized Font Size: " + attrValue);
      }
      else {
        TheSize = attrValue;
      }
    }
    else {
      resultMsg = new String("Unrecognized Font attribute: " + tag);
    }
    return resultMsg;
  }

  /*
   * tells the factory that the attributes have been seen; therefore,
   * return the XMLEleObject created.
   *
   * @return the newly created XMLEleObject or null (if there was a problem
   * in creating it).
   */
  public XMLEleObject getObject() {
    return this;
  }

  /*
   * is the method through which the object receives the text field.
   *
   * @param eleValue is the Text for the Element's value.
   *
   * @return if the value is acceptable, then null; otherwise, an error
   * string.
   */
  public String setValue(String eleValue) {
    Complete = true;
    return locateFont(0, TheStyle, TheSize);
  }

  /*
   * is the method through which the object receives embedded Objects.
   *
   * @param objName is the name of the embedded object
   * @param objValue is the value of the embedded object
   *
   * @return null if the Object is acceptible or an error String
   * if it is not.
   */
  public String setObject(String objName, Object objValue) {
    return new String("XML objects are not accepted by " + XMLTag +
                      " elements.");
  }

  /*
   * returns the XML Element tag for the XMLEleObject.
   *
   * @return the name by which XMLReader knows the XMLEleObject (the
   * Element tag).
   */
  public String getTag() {
    return XMLTag;
  }

  /*
   * tells the XMLEleObject that no more setValue or setObject calls will
   * be made; thus, it can do any error chacking that it needs.
   *
   * @return null, if it has received everything it needs or an error
   * string if something isn't correct.
   */
  public String doneXML() {
    FontDefinition fDef;
    if (Complete) {
        fDef = FontList.instance().findElementbyKey(XMLTag);
        if (fDef != null) {
            fDef.setFontSize(TheSize);
            fDef.setFontStyle(TheStyle);
        }
        return null;
    }
    return new String(XMLTag + " is incomplete.");
  }


  /**
   * searches an array of Strings for a particular String.
   *
   * @param query is the String being looked for.
   * @param array is the array being searched.
   *
   * @return the index of the String in the array, if found, or NOT_FOUND.
   */
  public static int findString(String query, String[] array) {
    for (int index = 0; index < array.length; ++index) {
      if (query.equals(array[index])) {
        return index;
      }
    }
    return NOT_FOUND;
  }

  /**
   * searches an array of ints for a particular int.
   *
   * @param query is the int being looked for.
   * @param array is the array being searched.
   *
   * @return the index of the int in the array, if found, or NOT_FOUND.
   */
  public static int findInt(int query, int[] array) {
    for (int index = 0; index < array.length; ++index) {
      if (query == array[index]) {
        return index;
      }
    }
    return NOT_FOUND;
  }
}
/* @(#)CtcFont.java */
