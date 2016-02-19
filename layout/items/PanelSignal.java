/* Name: PanelSignal.java
 *
 * What:
 *   This class is the container for all the information about displaying
 *   a Section's signal.
 */

package cats.layout.items;

import cats.gui.CtcFont;
import cats.gui.GridTile;
import cats.gui.Palette;
import cats.gui.PaletteFactory;
import cats.gui.frills.FrillLoc;
import cats.gui.frills.SignalFrill;
import cats.gui.frills.LightFrill;
import cats.gui.frills.SemaphoreFrill;
import cats.layout.AspectMap;
import cats.layout.ColorList;
import cats.layout.xml.*;
import java.awt.event.MouseEvent;

/**
 * is a container for all the information about displaying a Section's signal.
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2004, 2013</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */
public class PanelSignal
    implements XMLEleObject {
  /**
   * is the tag for indentifying a PanelSignal in the XMl file.
   */
  static final String XML_TAG = "PANELSIGNAL";

  /**
   * is the XML tag for identifying the Signal's location in the GridTile.
   */
  static final String SIG_LOCATION = "SIGLOCATION";

  /**
   * is the XML tag for identifying the Signal's orientation.
   */
  static final String SIG_ORIENT = "SIGORIENT";

  /**
   * is the XML tag for indentifying the Signal's type.
   */
  static final String SIG_PANEL_TYPE = "SIGPANTYPE";

  /**
   * is the values of the Signal's type.
   */
  static final String[] SIG_TYPE = {
      "LAMP1",
      "LAMP2",
      "LAMP3",
      "SEM1",
      "SEM2",
      "SEM3"
  };

  /**
   * is a look up table for the number of heads corresponding to each of the
   * above.
   */
  static final int[] SIGNAL_HEADS = {
      1, 2, 3, 1, 2, 3
  };

  /**
   * is a look up table for the kind of signal, corresponding to the above.
   */

  static final boolean[] LAMP = {
      true, true, true, false, false, false
  };

  /**
   * is the names of the various colors a PanelSignal can show.  These
   * Strings must match Palette names, as defined in PaletteFactory.
   */
  private static final String[] IconColors = {
      "EMPTY",
      "CLEAR",
      "APPROACH",
      "STOP",
      "NOSIGNAL"
  };

  /**
   * the Palettes for each of the possible Indications, plus one for
   * Signal not set.
   */
  private static Palette[] PanelColor;

  /**
   *  The GridTile where painting happens.
   */
  private GridTile SignalTile;

  /**
   *  The Frill for displaying the Signal Icon.
   */
  private SignalFrill TheFrill;

  /**
   *  The location of the Signal Icon in the GridTile.
   */
  private FrillLoc MyLocation;

  /**
   * The orientation of the Signal Icon.
   */
  private int MyOrientation;

  /**
   * The number of heads.
   */
  private int MyHeadCount;

  /**
   * The kind of Icon - true for light and false for semaphore.
   */
  private boolean MyLamp;

  /**
   * The IDLE color of a CTC signal.  Default is false.
   */
  private boolean DarkSignal;

  /**
   *  The CPEdge protected by the Signal.
   */
  private CPEdge Protected;

  /**
   * The Indication being displayed.
   */
  private Indication PanelIndication;

  /**
   * constructs a PanelSignal, given no other information
   */
  public PanelSignal() {
    MyLamp = true;
    DarkSignal = false;
    MyHeadCount = 1;
    PanelIndication = new Indication();
  }

  /**
   * retrieves the PanelSignal's location in its GridTile.
   *
   * @return the PanelSignal's location in the Grid.  Null is a valid value,
   * indicating that the PanelSignal is not being shown.
   *
   * @see cats.gui.frills.FrillLoc
   */
//  public FrillLoc getLocation() {
//    return MyLocation;
//  }

  /**
   * changes the location of the PanelSignal in its GridTile.
   *
   * @param loc is the new location.
   *
   * @see cats.gui.frills.FrillLoc
   */
  public void setLocation(FrillLoc loc) {
    MyLocation = loc;
  }

  /**
   * retrieves the Icon's orientation.
   *
   * @return the PanelSignal's orientation.  UNKNOWN is a valid value.
   */
//  public int getSigOrient() {
//    return MyOrientation;
//  }

  /**
   * changes the Icon's orientation.
   *
   * @param orient is the new orientation.  UNKNOWN is valid.
   */
  public void setOrient(int orient) {
    MyOrientation = orient;
  }

  /**
   * retrieves the number of heads.
   *
   * @return the number of heads.  0 is invalid.
   */
  public int getHeadCount() {
    return MyHeadCount;
  }

  /**
   * retrieves the signal type.
   *
   * @return true for lights or false for semaphore.
   */
//  public boolean isLampType() {
//    return MyLamp;
//  }

  /** sets the type of Icon and number of heads.
   *
   * @param type is true for lights and false for semaphore.
   * @param num is the number of heads (1-3).
   */
  public void setParms(boolean type, int num) {
    MyLamp = type;
    MyHeadCount = num;
  }

  /**
   * sets the PanelSignal to Dark.
   */
  public void makeDark() {
    DarkSignal = true;
  }

  /**
   * returns the Signal Icon.
   *
   * @return the Signal Frill so that it can be told where to send mouse
   * actions.  The result should not, but may be, null.
   */
  public SignalFrill getIcon() {
    return TheFrill;
  }

  /**
   * tells the PanelSignal what CPEdge it is protecting, so that it knows
   * whose method to call when the user mouse clicks on the Icon.
   * @param protect is the CPEdge being protected.
   */
  public void protectEdge(CPEdge protect) {
    Protected = protect;
  }

  /**
   * handles a mouse button push when positioned over a Signal Icon.
   *
   * If the left mouse button is pushed, then it is interpreted as a request
   * to set traffic through the Block, beginning at this CPEdge being
   * protected.  If the right button is pushed, then a menu is presented.
   *
   * @param me is the MouseEvent recording the button push/release.
   *
   * @see java.awt.event.MouseEvent
   */
  public void signalMouse(MouseEvent me) {
//    if (PanelIndication.isTraffic()) {
      Protected.edgeMouse(me);
//    }
//    PanelIndication.setTrkTraffic();
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
   * be made; thus, it can do any error chacking that it needs.
   *
   * @return null, if it has received everything it needs or an error
   * string if something isn't correct.
   */
  public String doneXML() {
    return null;
  }

  /**
   * tells the PanelSignal where its Icon is shown.
   *
   * @param tile is the GridTile where painting happens.
   *
   * @see cats.gui.GridTile
   */
  public void install(GridTile tile) {
    String finder = null;
    SignalTile = tile;
    if (tile != null) {
      if (MyLamp) {
        TheFrill = new LightFrill(MyLocation, MyOrientation, MyHeadCount);
      }
      else {
        TheFrill = new SemaphoreFrill(MyLocation, MyOrientation, MyHeadCount);
      }
      SignalTile.addFrill(TheFrill);
      finder = DarkSignal ? ColorList.NOSIGNAL : ColorList.NO_ROUTE;
      TheFrill.setSignalColor(finder);
      TheFrill.setOwner(this);
    }
  }

  /**
   * tells the Icon what color it is.
   * @param ind is the Indication being shown.
   */
  public void setIndication(Indication ind) {
    if (TheFrill != null) {
      String color;
      int state = ind.getIndication();
      if ( !ind.isPanelOn()) {
        color = DarkSignal ? ColorList.NOSIGNAL : ColorList.NO_ROUTE;
      }
      else {
//        if (state < Indication.APP) {
//          color = PanelColor[CLEAR];
//        }
//        else if (state < Indication.RES) {
//          color = PanelColor[APPROACH];
//        }
        if (state < AspectMap.findRule("R291")) {
          if (ind.showingApproach()) {
            color = ColorList.APPROACH;
          }
          else {
            color = ColorList.CLEAR;
          }
        }
        else {
          color = ColorList.STOP;
        }
      }
      TheFrill.setSignalColor(color);
      SignalTile.requestUpdate();
      PanelIndication.copy(ind);
    }
  }

  /**
   * asks the sub-component to create a copy of itself.
   *
   * @return an identical copy of itself.
   */
//  public PanelSignal copy() {
//    PanelSignal newCopy = new PanelSignal();
//    newCopy.MyLocation = MyLocation;
//    newCopy.MyOrientation = MyOrientation;
//    newCopy.MyHeadCount = MyHeadCount;
//    newCopy.MyLamp = MyLamp;
//    return newCopy;
//  }

  /**
   * removes itself from the GridTile.
   */
//  public void uninstall() {
//    if ( (SignalTile != null) && (TheFrill != null)) {
//      SignalTile.delFrill(TheFrill);
//    }
//  }

  /**
   * registers a PanelSignalFactory with the XMLReader.
   */
  static public void init() {
    XMLReader.registerFactory(XML_TAG, new PanelSignalFactory());
  }

  /**
   * sets up the mapping from color name to Palette.
   */
  public static void setPalette() {
    PanelColor = new Palette[IconColors.length];
    for (int color = 0; color < PanelColor.length; ++color) {
      PanelColor[color] = PaletteFactory.Palettes.findPalette(IconColors[color]);
    }
  }

}

/**
     * is a Class known only to the PanelSignal class for creating PanelSignals from
 * an XML document.  Its purpose is to pick up the location of the SecSignal
 * in the GridTile, its orientation, and physical attributes on the layout.
 */
class PanelSignalFactory
    implements XMLEleFactory {

  FrillLoc Location;
  int Orientation;
  boolean Lamp;
  int Heads;

  /*
   * tells the factory that an XMLEleObject is to be created.  Thus,
   * its contents can be set from the information in an XML Element
   * description.
   */
  public void newElement() {
    Location = null;
    Orientation = CtcFont.NOT_FOUND;
    Lamp = true;
    Heads = 1;
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

    if (PanelSignal.SIG_ORIENT.equals(tag)) {
      Orientation = Edge.toEdge(value);
    }
    else if (PanelSignal.SIG_LOCATION.equals(tag)) {
      Location = FrillLoc.newFrillLoc(value);
    }
    else if (PanelSignal.SIG_PANEL_TYPE.equals(tag)) {
      int type = CtcFont.findString(value, PanelSignal.SIG_TYPE);
      if (type != CtcFont.NOT_FOUND) {
        Lamp = PanelSignal.LAMP[type];
        Heads = PanelSignal.SIGNAL_HEADS[type];
      }
    }
    else {
      resultMsg = new String("A " + PanelSignal.XML_TAG +
                             " XML Element cannot have a " + tag +
                             " attribute.");
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
    PanelSignal p = new PanelSignal();
    p.setLocation(Location);
    p.setOrient(Orientation);
    p.setParms(Lamp, Heads);
    return p;
  }
}