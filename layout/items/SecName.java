/* Name: SecName.java
 *
 * What:
 *   This class is the container for all the information about a Section's
 *   name.
 */

package cats.layout.items;

import cats.gui.GridTile;
import cats.gui.frills.FrillLoc;
import cats.gui.frills.LabelFrill;
import cats.gui.jCustom.FontFinder;
import cats.layout.FontList;
import cats.layout.xml.*;

/**
 * is a container for all the information about a Section's Name (the label
 * on the Grid for the Section).
 *
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */
public class SecName
    implements Itemizable {
  static final String XML_TAG = "SEC_NAME";
  static final String NAME_LOC = "LOC_NAME";
  static final String NAME_NAME = "NAME";
  static final String NAME_FONT = "FONT_NAME";

  /**
   *   The String for the name.
   */
  private String MyName;

  /**
   *  Its location in the Grid
   */

  private FrillLoc MyLocation;

  /**
   *  The GridTile where painting happens.
   */
  private GridTile NameTile;

  /**
   *  The JLabel containing the Section's name.
   */
  private LabelFrill NameFrill;

  /**
   * The FontFinder through which the font of the label is found
   */
  private FontFinder NameFont;
  
  /**
   * constructs a SecName, given the location for the label.
   *
   * @param loc is the location of the label.
   */
  public SecName(FrillLoc loc) {
    MyLocation = loc;
    NameFont = new FontFinder(FontList.FONT_LABEL);
  }

  /**
   * retrieves the Section's name.
   *
   * @return the Section's name.
   */
  public String getName() {
    if (MyName != null) {
      return new String(MyName);
    }
    return null;
  }

  /**
   * retrieves the Section's location.
   *
   * @return the Section's name's location in the Grid.
   *
   * @see cats.gui.frills.FrillLoc
   */
  public FrillLoc getLocation() {
    return MyLocation;
  }


  /**
   * retrieves the FontFinder used for the Section label
   * @return the FontFinder for the Section label
   */
  public FontFinder getFontFinder() {
      return NameFont;
  }
  
  /**
   * changes the name and locations of the SecName.
   *
   * @param name is the new name.
   * @param loc is the new location.
   * @param font is the key of the FontDefinition
   *
   * @see cats.gui.frills.FrillLoc
   */
  public void setAll(String name, FrillLoc loc, String font) {
    MyName = new String(name);
    MyLocation = loc;
    NameFont = new FontFinder(font, FontList.FONT_LABEL);
    NameFrill = new LabelFrill(name, loc, NameFont);
  }

  /*
   * tells the sub-component where its Section is, so that the sub-component
   * can replace itself and retrieve anything else it needs from the Section.
   */
  public void addSelf(Section sec) {
    sec.replaceSecName(this);
  }

  /*
   * adds the SecName to the GridTile.
   */
  public void install(GridTile tile) {
    NameTile = tile;
    if ( (NameTile != null) && (NameFrill != null)) {
      NameTile.addFrill(NameFrill);
    }
  }

  /*
   * asks the sub-component if it has anything to paint on the Screen.
   *
   * @return true if it does and false if it doen't.
   */
  public boolean isVisible() {
    return true;
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
    return new String("A " + XML_TAG + " cannot have a text field.");
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
    return new String("A " + XML_TAG + " cannot contain an Element ("
                      + objName + ").");
  }

  /*
   * returns the XML Element tag for the XMLEleObject.
   *
   * @return the name by which XMLReader knows the XMLEleObject (the
   * Element tag).
   */
  public String getTag() {
    return new String(XML_TAG);
  }

  /*
   * tells the XMLEleObject that no more setValue or setObject calls will
   * be made; thus, it can do any error chacking that it needs.
   *
   * @return null, if it has received everything it needs or an error
   * string if something isn't correct.
   */
  public String doneXML() {
    if ( (MyLocation != null) && (MyName != null)) {
      return null;
    }
    return new String("Incomplete " + XML_TAG + " XML specification");
  }

  /**
   * registers a SecNameFactory with the XMLReader.
   */
  static public void init() {
    XMLReader.registerFactory(XML_TAG, new SecNameFactory());
  }
}

/**
 * is a Class known only to the SecName class for creating SecNames from
 * an XML document.  Its purpose is to pick up the location of the Label
 * in the GridTile.
 */
class SecNameFactory
    implements XMLEleFactory {

  FrillLoc Location;
  String Name;
  String MyFont;
  
 
  /*
   * tells the factory that an XMLEleObject is to be created.  Thus,
   * its contents can be set from the information in an XML Element
   * description.
   */
  public void newElement() {
    Location = null;
    Name = null;
    MyFont = FontList.FONT_LABEL;
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
    if (tag.equals(SecName.NAME_LOC)) {
      if ( (Location = FrillLoc.newFrillLoc(value)) == null) {
        resultMsg = new String(value + " is not a valid attribute value for a "
                               + SecName.XML_TAG + " XML Element.");
      }
    }
    else if (SecName.NAME_NAME.equals(tag)) {
      Name = new String(value);
    }
    else if (SecName.NAME_FONT.equals(tag)) {
        MyFont = new String(value);
    }
    else {
      resultMsg = new String(tag + " is not a valid attribute for a " +
                             SecName.XML_TAG);
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
    if (Location != null) {
      SecName newName = new SecName(Location);
      newName.setAll(Name, Location, MyFont);
      return newName;
    }
    return null;
  }
}