/* Name: Depot.java
 *
 * What:
 *   This class is the container for all the information about a Section's
 *   Depot (Station).
 */

package cats.layout.items;

import cats.gui.GridTile;
import cats.gui.frills.FrillLoc;
import cats.gui.frills.DepotFrill;
import cats.gui.jCustom.ColorFinder;
import cats.layout.ColorList;
import cats.layout.xml.*;

/**
 * is a container for all the information about a Section's Depot or Station.
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */
public class Depot
    implements Itemizable {
  static final String XML_TAG = "STATION";
  
  /**
   * the attribute tag
   */
  static final String COLOR_TAG = "COLORTAG";

  /**
   *  The GridTile where painting happens.
   */
  private GridTile DepotTile;

  /**
   *  The DepotFrill containing the Section's name.
   */
  private DepotFrill TheFrill;

  /**
   *  The location of the Depot Icon in the GridTile
   */
  private FrillLoc MyLocation;

  /**
   * The customized color for the Depot
   */
  private ColorFinder MyColor;

  /**
   * constructs a Depot with no initial color override.
   */
  public Depot() {
      this(ColorList.DEPOT);
  }
  
  /**
   * constructs a Depot, given the key for the default ColorDefinition
   * 
   * @param color is the ColorDefinition key for the color of the
   * depot.  It can be null, in which case, theefault Depot color
   * will be used.
   */
  public Depot(String color) {
      MyColor = new ColorFinder(color, ColorList.DEPOT);
  }

  /**
   * retrieves the Depot's location.
   *
   * @return the Depot's location in the Grid.  Null is a valid value,
   * indicating that the Depot is not being shown.
   *
   * @see cats.gui.frills.FrillLoc
   */
  public FrillLoc getLocation() {
    return MyLocation;
  }

  /*
   * tells the sub-component where its Section is, so that the sub-component
   * can replace itself and retrieve anything else it needs from the Section.
   */
  public void addSelf(Section sec) {
    sec.replaceStation(this);
  }

  /*
   * adds the DepotFrill to the GridTile.
   */
  public void install(GridTile tile) {
    DepotTile = tile;
    if (DepotTile != null) {
      TheFrill = new DepotFrill(MyLocation, MyColor);
      DepotTile.addFrill(TheFrill);
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
    MyLocation = FrillLoc.newFrillLoc(eleValue);
    return null;
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
   * be made; thus, it can do any error checking that it needs.
   *
   * @return null, if it has received everything it needs or an error
   * string if something isn't correct.
   */
  public String doneXML() {
    String resultMsg = null;
    if (MyLocation == null) {
      resultMsg = new String("Missing location value for " + XML_TAG +
                             " XML Element.");
    }
    return resultMsg;
  }

  /**
   * registers a SecNameFactory with the XMLReader.
   */
  static public void init() {
    XMLReader.registerFactory(XML_TAG, new DepotFactory());
  }
}

/**
 * is a Class known only to the Depot class for creating Depots from
 * an XML document.  Its purpose is to pick up the location of the Depot
 * in the GridTile.
 */
class DepotFactory
    implements XMLEleFactory {

  FrillLoc Location;
  String DepotColor;

  /*
   * tells the factory that an XMLEleObject is to be created.  Thus,
   * its contents can be set from the information in an XML Element
   * description.
   */
  public void newElement() {
    Location = null;
    DepotColor = null;
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
      String msg = null;
      if ((Depot.COLOR_TAG.equals(tag))) {
          DepotColor = new String(value);
      }
      else {
          new String("A " + Depot.XML_TAG +
                      " XML Element has no Attributes ("
                      + tag + ").");
      }
      return msg;
  }

  /*
   * tells the factory that the attributes have been seen; therefore,
   * return the XMLEleObject created.
   *
   * @return the newly created XMLEleObject or null (if there was a problem
   * in creating it).
   */
  public XMLEleObject getObject() {
    return new Depot(DepotColor);
  }
}