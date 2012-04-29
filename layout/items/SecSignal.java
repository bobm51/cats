/* Name: SecSignal.java
 *
 * What:
 *   This class is the container for all the information about a Section's
 *   Signal.
 */

package cats.layout.items;

import cats.layout.SignalTemplate;
import cats.layout.TemplateStore;
import cats.gui.GridTile;
import cats.layout.xml.*;

/**
 * is a container for all the information about a Section's Signal.
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */
public class SecSignal
    extends Signal
    implements XMLEleObject {
  /**
   * is the tag for indentifying a SecSignal in the XMl file.
   */
  static final String XML_TAG = "SECSIGNAL";

  /**
   * is the Name of the SecSignal.
   */
  protected String SigName;

  /**
   * the number of heads in the Icon.
   */
  int IconHeads;

  /**
   * true if the Icon is a lamp signal.
   */
  boolean IconLamp;

  /**
   * describes the Signal's Icon on the dispatcher's panel.
   */
  private PanelSignal MyIcon;

  /**
   * describes the actual signal on the layout.
   */
  private PhysicalSignal MySignal;

  /**
   * where the Signal is painted.
   */
  private GridTile SignalTile;

  /**
   * constructs a SecSignal, given no other information
   */
  public SecSignal() {
    super();
  }

  /**
   * is a way of estimating if the Signal is an intermediate or is
   * protecting turnouts.  If the next signal feeding a real signal is
   * also real, then the block is simple.
   *
   * @return true if the Block being protected is simple (no turnouts
   * or overlapping blocks) or false if not.
   */
//  public boolean isSimple() {
//    return NextIsReal;
//  }

  /**
   * propagates the HomeSignal attribute to the predecessor
   * Signal.  Since this is a real Signal, the Control Point
   * attribute does not propagate.
   */
  public void setHS() {
    HomeSignal = true;
    if (upgradeSignalType(ProtectedEdge.getBlock().getDiscipline())) {
      ProtectedEdge.strategyFactory();
    }
  }

  /**
   * changes the SecSignal's name.
   *
   * @param name is the new name.  Null is valid.
   */
  public void setSigName(String name) {
    SigName = name;
  }

  /**
   * retrieves the SecSignal's name.
   *
   * @return the SecSignal's name.  Null is a valid value.
   */
  public String getSigName() {
    if (SigName != null) {
      return new String(SigName);
    }
    return null;
  }

  /**
   * tells the Indication where to feed its state.  The preceding
   * Indication may be dynamic, if it is on the other side of switch
   * points.
   *
   * @param pred is the Indication before this one in the direction of
   * travel.
   */
  public void setPredecesor(Signal pred) {
    PrevSignal = pred;
    pred.NextIsReal = true;
  }

  /*
   * sends the Signal's Indication to the Signal that it feeds.  Since this
   * is a real Signal, it has a major difference from all the other
   * Signals, which are virtual.  It contains code that sets the
   * icon on the dispatcher panel and the layout.
   */
  protected void tumbleDown() {
    super.tumbleDown();
    if ( (MyIcon != null) && (SignalTile != null)) {
      MyIcon.setIndication(MyIndication);
      SignalTile.requestUpdate();
    }
    if (MySignal != null) {
      MySignal.setAspect(MyIndication);
    }
  }

  /**
   * retrieves the SecSignal's Icon information.
   *
   * @return the SecSignal's Icon.  Null is a valid value.
   *
   * @see PanelSignal
   */
  public PanelSignal getSigIcon() {
    return MyIcon;
  }

  /**
   * changes the SecSignal's Icon.  It is used to free PanelSignals
   * which are hidden.
   *
   * @param icon is the new Icon information.  Null is valid.
   *
   * @see PanelSignal
   */
  public void setSigIcon(PanelSignal icon) {
    MyIcon = icon;
    SignalType = (MySignal == null) ? VIRTUAL_SIGNAL : INTERMEDIATE;
  }

  /**
   * retrieves the actual signal's description.
   *
   * @return the signal's description.  Null is a valid value.
   *
   * @see PhysicalSignal
   */
  public PhysicalSignal getSigDescription() {
    return MySignal;
  }
  
  /**
   * adjusts the SignalType based on the discipline of the block(s)
   * being protected.
   * 
   * @param discipline is the signal discipline of the Block
   * being protected.
   */
  public void setBlockDiscipline(int discipline) {
    upgradeSignalType(discipline);
  }

  /**
   * ensures that an APB signal protecting an interlocking plant
   * is a Control Point.
   * @param discipline is the signal discipline of the Block
   * being protected.
   * @return true if an Intermediate is propmoted to a Control Point.
   */
  private boolean upgradeSignalType(int discipline) {
    if (isIntermediate() && isHS() && 
        ((discipline == Block.APB_1) || (discipline == Block.APB_2))) {
      SignalType = CONTROL_POINT;
      return true;
    }
    return false;
  }
  
  /*
   * receives the Indication for the Block/Track/Edge.
   *
   * @param state is the Block's state.
   *
   * @see cats.layout.items.Block
   */
  public void trackState(Indication state) {
    TrackState = state;
    tumbleDown();
  }

  /*
   * receives the Indication of the next Signal, which is virtual, in the
   * direction of travel.
   *
   * @param next is the next Signal's Indication.
   *
   * @see cats.layout.items.Indication
   */
  protected void nextSignal(Indication next) {
    NextIndication = next;
    tumbleDown();
  }

  /**
   * clears the Signal's history.  For a virtual Signal, there is none.
   */
  public void clrSignalHistory() {
    if (MySignal != null) {
      MySignal.refresh();
    }
  }

  /**
   * tells a Signal if it should be on due to approach lighting
   * or not.
   * 
   * @param lit is true for on and false for off.
   */
  public void setApproachState(boolean lit) {
    if (MySignal != null) {
      MySignal.lightUpSignal(lit);
    }
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
    SigName = new String(eleValue);
    if (MySignal != null) {
      MySignal.setName(SigName);
    }
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
    String resultMsg = null;
    if (PanelSignal.XML_TAG.equals(objName)) {
      MyIcon = (PanelSignal) objValue;
      SignalType = CONTROL_POINT;
    }
    else if (PhysicalSignal.XML_TAG.equals(objName)) {
      MySignal = (PhysicalSignal) objValue;
      
      // Setting the name triggers creation of the physical signal
      MySignal.setName(SigName);
      if (SignalType != CONTROL_POINT) {
        SignalType = INTERMEDIATE;
      }
    }
    else {
      resultMsg = new String("A " + XML_TAG + " cannot contain an Element ("
                             + objName + ").");
    }
    return resultMsg;
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
    return null;
  }

  /**
   * tells the SecSignal to link itself into the various data structures.
   *
   * @param tile is the GridTile where painting happens.
   */
  public void install(GridTile tile) {
    int heads = 1;
    boolean lamp = true;

    SignalTile = tile;
    if (MyIcon != null) {
      if (MySignal != null) {
        SignalTemplate template = TemplateStore.SignalKeeper.
            find(MySignal.getTemplateName());
        if (template != null) {
          heads = template.getNumHeads();
          if (heads == 0) {
            heads = 1;
          }
          else {
            lamp = template.isLights();
          }
          MyIcon.setParms(lamp, heads);
        }
      }
      else {
        MyIcon.makeDark();
      }
      MyIcon.install(tile);
    }
  }

  /**
   * registers a SecSignalFactory with the XMLReader.
   */
  static public void init() {
    XMLReader.registerFactory(XML_TAG, new SecSignalFactory());
    PanelSignal.init();
    PhysicalSignal.init();
  }
}

/**
 * is a Class known only to the SecSignal class for creating SecSignals from
 * an XML document.  Its purpose is to pick up the location of the SecSignal
 * in the GridTile, its orientation, and physical attributes on the layout.
 */
class SecSignalFactory
    implements XMLEleFactory {

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
    String resultMsg = null;

    resultMsg = new String("A " + SecSignal.XML_TAG +
                           " XML Element cannot have a " + tag +
                           " attribute.");
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
    return new SecSignal();
  }
}
/* @(#)SecSignal.java */