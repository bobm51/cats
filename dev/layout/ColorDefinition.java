/*
 * Name: ColorDefinition.java
 *
 * What:
 *  This class provides an association between a Color and a name.  The name may be reserved or
 *  user defined.
 */
package cats.layout;

import java.awt.Color;
import cats.layout.xml.XMLEleFactory;
import cats.layout.xml.XMLEleObject;

/**
 *  This class provides an association between a Color and a name.  The name may be reserved or
 *  user defined.
 *
 * <p>Title: designer</p>
 * <p>Description: A program for designing dispatcher panels</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */

public class ColorDefinition extends AbstractListElement {
    /**
     *   The XML tag for recognizing the ColorDefinition description.
     */
    public static final String XML_TAG = "COLORDEFINITION";

    /**
     * the XML attribute tag on the Color key
     */
    public static final String KEY_TAG = "COLORKEY";
    
    /**
     * the XML attribute tag on the name - the String seen by the user
     */
    public static final String NAME_TAG = "COLORNAME";
    
    /**
     * the XML attribute tag on the color value
     */
    public static final String COLOR_TAG = "COLORTAG";

    /**
     * the unique, internal name of the ColorDefinition
     */
    private final String ColorKey;
    
    /**
     * the user visible name of the ColorDefinition
     */
    private String ColorName;

    /**
     * the Color
     */
    private Color ColorValue;

    /**
     * the ctor invoked from the GUI, when the Name and Color are
     * unknown.
     * 
     * @param key is the key by which the ColorDefinition will be known.
     * It must not be null and it should be unique.
     */
    public ColorDefinition(String key) {
        this(key, "", Color.BLACK);
    }
    
    /**
     * the ctor.  
     * @param key is the internal name of the ColorDefinition.  It is unique.
     * @param name is the name of the definition.
     * @param color is the color.
     */
    public ColorDefinition(String key, String name, Color color) {
        ColorKey = key;        
        ColorName = name;
        ColorValue = color;
   }

    /**
     * retrieves the key (internal) name field.
     * @return a copy of the key.
     */
    public String getElementKey() {
        return new String(ColorKey);
    }
    
    /**
     * changes the name of the color.
     * @param name is the new name.  It must not be null
     * or zero length.
     */
    public void setColorName(String name) {
        if ((name != null) && (name.length() != 0)) {
            ColorName = new String(name);
        }
    }

    /**
     * retrieves the name of the Color
     * @return the String by which the Color is known.
     */
    public String getColorName() {
        return new String(ColorName);
    }
    
    /**
     * changes the value of the color.
     * @param value is the new color.  It must not be null.  It can be the same as
     * some other ColorDefinition.
     */
    public void setColorValue(Color value) {
        if (value != null) {
            ColorValue = value;
        }
    }

    /**
     * retrieves the Color
     * @return the Color associated with the name.  It will not be null.
     */
    public Color getColorValue() {
        return ColorValue;
    }
    
    /**
     * creates a copy of the ColorDefinition.
     * @return the copy
     */
    public AbstractListElement copy() {
        return new ColorDefinition(ColorKey, ColorName, ColorValue);
    }

    /**
     * @return the field by which the Color is known to the world.
     */
    public String getElementName() {
        return getColorName();
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
      return new String(" A " + XML_TAG + "  cannot have an XML value.");
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
      return new String("XML objects are not accepted by " + XML_TAG +
                        " elements.");
    }

    /*
     * returns the XML Element tag for the XMLEleObject.
     *
     * @return the name by which XMLReader knows the XMLEleObject (the
     * Element tag).
     */
    public String getTag() {
      return XML_TAG;
    }

    /*
     * tells the XMLEleObject that no more setValue or setObject calls will
     * be made; thus, it can do any error chacking that it needs.
     *
     * @return null, if it has received everything it needs or an error
     * string if something isn't correct.
     */
    public String doneXML() {
      ColorList.instance().replaceColor(this);
      return null;
    }

//    /**
//     * writes the Object's contents to an XML file.
//     *
//     * @param parent is the Element that this Object is added to.
//     *
//     * @return null if the Object was written successfully; otherwise, a String
//     *         describing the error.
//     */
//    public String putXML(Element parent) {
//        Element thisObject = new Element(XML_TAG);
//        thisObject.setAttribute(KEY_TAG, ColorKey);
//        thisObject.setAttribute(NAME_TAG, ColorName);
//        thisObject.setAttribute(COLOR_TAG, String.valueOf(ColorValue.getRGB()));
//        parent.addContent(thisObject);
//        Saved = true;
//        return null;
//    }
//
//    public String putXML(PrintStream outStream, String indent) {
//          return null;
//    }
}

/**
 * is a Class known only to the ColorDefinition class for creating ColorDefinitions from
 * an XML document.
 */
class ColorDefinitionFactory
    implements XMLEleFactory {

    /**
     * is the internal tag on the ColorDefinition
     */
    private String Tag;
    
  /**
   * is the Name of the definition.
   */
  private String Name;

  /**
   * is the Value of the Definition
   */
  private Color Value;

  /*
   * tells the factory that an XMLEleObject is to be created.  Thus,
   * its contents can be set from the information in an XML Element
   * description.
   */
  public void newElement() {
      Tag = null;
      Name = null;
      Value = null;
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
    String resultMsg = null;
    if (ColorDefinition.KEY_TAG.equals(tag)) {
        Tag = new String(value).trim();
    }
    else if (ColorDefinition.NAME_TAG.equals(tag)) {
        Name = new String(value).trim();
    }
    else if (ColorDefinition.COLOR_TAG.equals(tag)) {
        try {
            Value = Color.decode(value);
          }
          catch (NumberFormatException ne) {
            resultMsg = new String(value + " does not specify a color in element " +
                                   ColorDefinition.XML_TAG);
          }
    }
    else {
        resultMsg = new String("Unrecognized attribute (" + tag + ") in "
                + ColorDefinition.XML_TAG + " definition.");

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
      if ((Tag == null) || (Tag.length() == 0) || (Name == null) || (Name.length() == 0) || (Value == null)) {
          return null;
      }
      return new ColorDefinition(Tag, Name, Value);
  }
}
/* @(#)ColorDefinition.java */