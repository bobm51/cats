/*
 * Name: FontDefinition.java
 *
 * What:
 *  This class provides an association between a Font and a name.  The name may be reserved or
 *  user defined.
 */
package cats.layout;

import java.awt.Color;
import java.awt.Font;
import cats.gui.CtcFont;
import cats.gui.store.SizeType;
import cats.gui.store.StyleType;
import cats.layout.xml.XMLEleFactory;
import cats.layout.xml.XMLEleObject;

/**
 *  This class provides an association between a Font and a name.  The name may be reserved or
 *  user defined.
 *
 * <p>Title: designer</p>
 * <p>Description: A program for designing dispatcher panels</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */

public class FontDefinition extends AbstractListElement {
    /**
     *   The XML tag for recognizing the FontDefinition description.
     */
    public static final String XML_TAG = "FONTDEFINITION";

    /**
     * the XML attribute tag on the Font key
     */
    public static final String KEY_TAG = "FONTKEY";
    
    /**
     * the XML attribute tag on the name - the String seen by the user
     */
    public static final String NAME_TAG = "FONTNAME";
    
    /**
     * the XML attribute tag on the font color
     */
    public static final String COLOR_TAG = "FONTCOLOR";

    /**
     * the XML attribute tag on the font size
     */
    public static final String SIZE_TAG = "FONTSIZE";
    
    /**
     * the XML attribute tag on the font style
     */
    public static final String STYLE_TAG = "FONTSTYLE";
    
    /**
     * the unique, internal name of the FontDefinition
     */
    private final String FontKey;
    
    /**
     * the user visible name of the FontDefinition
     */
    private String FontName;

    /**
     * the Color
     */
    private Color FontColor;
    
    /**
     * the size
     */
    private String FontSize;
    
    /**
     * the style
     */
    private String FontStyle;
    
    /**
     * the Font.  Creating it has been problematic.  It appears that it may
     * need to be created on a GUI thread.  This file could be cleaned up to
     * remove FontSize and FontStyle.
     */
    private Font DefFont;
    
    /**
     * the ctor invoked from the GUI, when the characteristics are
     * unknown.
     * 
     * @param key is the key by which the FontDefinition will be known.
     * It must not be null and it should be unique.
     */
    public FontDefinition(String key) {
        int index;
        FontKey = key;
        FontName = "";
        FontColor = Color.WHITE;
        DefFont = FontList.getFrameFont();
        FontSize = String.valueOf(DefFont.getSize());
        index = CtcFont.findInt(DefFont.getStyle(), StyleType.EMPHASIS_INDEX);
        if (index == CtcFont.NOT_FOUND) {
            FontStyle = "PLAIN";
        }
        else {
            FontStyle = StyleType.FONT_EMPHASIS[index];
        }
    }

    /**
     * another ctor
     * @param key is the internal name of the FontDefinition.  It is unique.
     * @param name is the user visible name of the definition.
     * @param color is the color of the definition
     */
    public FontDefinition(String key, String name, Color color) {
        this(key);
        FontName = name;
        FontColor = color;
    }
    
    /**
     * the ctor.  
     * @param key is the internal name of the FontDefinition.  It is unique.
     * @param name is the user visible name of the definition.
     * @param color is the color.
     * @param size is the point size of the font
     * @param style is the style of the font
     */
    public FontDefinition(String key, String name, Color color, String size, String style) {
//    FontKey = key;        
//    FontName = name;
//    FontColor = color;
//      FontSize = size;
//      FontStyle = style;
      this(key, name, color);
      setFontSize(size);
      setFontStyle(style);
    }

    /**
     * retrieves the key (internal) name field.
     * @return a copy of the key.
     */
    public String getElementKey() {
        return new String(FontKey);
    }
    
    /**
     * changes the name of the font.
     * @param name is the new name.  It must not be null
     * or zero length.
     */
    public void setFontName(String name) {
        if ((name != null) && (name.length() != 0)) {
            FontName = new String(name);
        }
    }

    /**
     * retrieves the name of the Font
     * @return the String by which the Font is known.
     */
    public String getFontName() {
        return new String(FontName);
    }
    
    /**
     * changes the size of the Font.
     * @param size is the size of the font
     */
    public void setFontSize(String size) {
        FontSize = size;
        float fSize = Float.parseFloat(size);
        DefFont = DefFont.deriveFont(fSize);
    }

    /**
     * retrieves the size
     * @return the size of the font associated with the name.  It will not be null.
     */
    public String getFontSize() {
        return FontSize;
    }
    
    /**
     * changes the Color of the Font.
     * @param color is the color of the font
     */
    public void setFontColor(Color color) {
        FontColor = color;
    }

    /**
     * retrieves the Color
     * @return the Color of the font associated with the name.  It will not be null.
     */
    public Color getFontColor() {
        return FontColor;
    }
    
    /**
     * changes the style of the Font.
     * @param style is the style of the font
     */
    public void setFontStyle(String style) {
        FontStyle = style;
        int index;
        int s;
        index = CtcFont.findString(FontStyle, StyleType.FONT_EMPHASIS);
        if (index == CtcFont.NOT_FOUND) {
            s = Font.PLAIN;
        }
        else {
            s = StyleType.EMPHASIS_INDEX[index];
        }
        DefFont = DefFont.deriveFont(s);
    }

    /**
     * retrieves the style
     * @return the style of the font associated with the name.  It will not be null.
     */
    public String getFontStyle() {
        return FontStyle;
    }

    /**
     * creates a Font based on the family named font, with a style and
     * size from its internal settings
     * @param font is the name of a Font family
     * @return a member of the Font family
     */
    public Font getFont(String font) {
//        int style;
//        int size;
//        int index;
//        index = CtcFont.findString(FontStyle, StyleType.FONT_EMPHASIS);
//        if (index == CtcFont.NOT_FOUND) {
//            style = Font.PLAIN;
//        }
//        else {
//            style = StyleType.EMPHASIS_INDEX[index];
//        }
//        size = Integer.parseInt(FontSize);
//        return new Font(font, style, size);
        return DefFont;
    }
    
    /**
     * creates a copy of the FontDefinition.
     * @return the copy
     */
    public AbstractListElement copy() {
        return new FontDefinition(FontKey, FontName, FontColor, FontSize, FontStyle);
    }

    /**
     * @return the field by which the Font is known to the world.
     */
    public String getElementName() {
        return getFontName();
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
      FontList.instance().replaceFont(this);
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
//        thisObject.setAttribute(KEY_TAG, FontKey);
//        thisObject.setAttribute(NAME_TAG, FontName);
//        thisObject.setAttribute(COLOR_TAG, String.valueOf(FontColor.getRGB()));
//        thisObject.setAttribute(SIZE_TAG, FontSize);
//        thisObject.setAttribute(STYLE_TAG, FontStyle);
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
 * is a Class known only to the FontDefinition class for creating FontDefinitions from
 * an XML document.
 */
class FontDefinitionFactory
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
   * is the Color of the Definition
   */
  private Color MyColor;
  
  /**
   * is the Size of the Font
   */
  private String Size;
  
  /**
   * is the Style of the Font.
   */
  private String Style;

  /*
   * tells the factory that an XMLEleObject is to be created.  Thus,
   * its contents can be set from the information in an XML Element
   * description.
   */
  public void newElement() {
      Tag = null;
      Name = null;
      MyColor = null;
      Size = null;
      Style = null;
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
    if (FontDefinition.KEY_TAG.equals(tag)) {
        Tag = new String(value).trim();
    }
    else if (FontDefinition.NAME_TAG.equals(tag)) {
        Name = new String(value).trim();
    }
    else if (FontDefinition.COLOR_TAG.equals(tag)) {
        try {
            MyColor = Color.decode(value);
          }
          catch (NumberFormatException ne) {
            resultMsg = new String(value + " does not specify a color in element " +
                                   FontDefinition.XML_TAG);
          }
    }
    else if (FontDefinition.SIZE_TAG.equals(tag)) {
        if (CtcFont.findString(value, SizeType.FONT_SIZE) != CtcFont.NOT_FOUND) {
            Size = value;
        }
        else {
            resultMsg = new String(value + " is not a legal font size in " + FontDefinition.XML_TAG);
        }
    }
    else if (FontDefinition.STYLE_TAG.equals(tag)) {
        if (CtcFont.findString(value, StyleType.FONT_EMPHASIS) != CtcFont.NOT_FOUND) {
            Style = value;
        }
        else {
            resultMsg = new String(value + " is not a legal font style in " + FontDefinition.XML_TAG);
        }        
    }
    else {
        resultMsg = new String("Unrecognized attribute (" + tag + ") in "
                + FontDefinition.XML_TAG + " definition.");

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
      if ((Tag == null) || (Tag.length() == 0) || (Name == null) || (Name.length() == 0) || (MyColor == null)
              || (Size == null) || (Style == null)) {
          return null;
      }
      return new FontDefinition(Tag, Name, MyColor, Size, Style);
  }
}
/* @(#)FontDefinition.java */