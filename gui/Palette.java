/* Name: Palette.java
 *
 * What: This file contains the Palette class - a wrapper around the Java
 *   Color class, providing a level of indireciton, so that all uses
 *   of a particular Color will change when the referenced Color changes.
 *
 * Special Considerations:
 */
package cats.gui;

import cats.layout.ColorDefinition;
import cats.layout.ColorList;
import cats.layout.FontDefinition;
import cats.layout.FontList;
import cats.layout.xml.*;
import java.awt.Color;

/**
 * defines a class for associating the color of objects on the dispatcher
 * panel with colors.  These are not constants, but
 * variables.  The reason is so that the user can modify them to suit
 * the characteristics of the display.
 *
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2004, 2009</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */
public class Palette
//    implements java.awt.event.ActionListener, XMLEleFactory, XMLEleObject {
  implements XMLEleFactory, XMLEleObject {

  //The current color
  private Color TheColor;

  // The tag for this Object in an XML Element
  private String XMLTag;

  /**
   * construct an object that holds the Color of an Object.
   *
   * @param	color is the initial value of the color.
   * @param     xmlTag is the XML tag for this Object
   */
  public Palette(Color color, String xmlTag) {
    TheColor = color;
    XMLTag = xmlTag;
  }

  /*
   * tells the factory that an XMLEleObject is to be created.  Thus,
   * its contents can be set from the information in an XML Element
   * description.
   */
  public void newElement() {
  }

  /*
   * gives the factory an initialization value for the created XMLEleObject.
   *
   * @param tag is the name of the attribute.
   * @param value is it value.
   *
   * @return null if the tag:value are accepted; otherwise, an error
   * string.
   */
  public String addAttribute(String tag, String value) {
    return new String("Attributes are not accepted in " + XMLTag +
                      " elements");
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
    String resultMsg = null;
    try {
      TheColor = Color.decode(eleValue);
    }
    catch (NumberFormatException ne) {
      resultMsg = new String(eleValue + " does not specify a color in element " +
                             XMLTag);
    }
    return resultMsg;
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
    FontDefinition font;
    ColorDefinition def = ColorList.instance().findElementbyKey(XMLTag);
    if (def != null) {
        def.setColorValue(TheColor);
    }
    font = FontList.instance().findElementbyKey(XMLTag);
    if (font != null) {
        font.setFontColor(TheColor);
    }
    return null;
  }
}
