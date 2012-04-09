/* Name: EdgeBuilder.java
 *
 * What:
 *   This class implements the Builder design pattern.  It takes a
 *   for a SecEdge and its contents and constructs the appropriate
 *   sub-class.
 */

package cats.layout.items;

import cats.gui.GridTile;
import cats.gui.CtcFont;
import cats.layout.xml.*;

/**
 * is an implementation of the Builder design pattern.  It constructs
 * the appropriate SecEdge specialized class from the contents read
 * from an XML file.
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2004, 2009</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */
public class EdgeBuilder
    implements Itemizable {

  /**
   * the Edge identification of this SecEdge.
   */
  private int MyEdge;

  /**
   * a description of the detection Block terminating on the SecEdge.
   */
  private Block MyBlock;

  /**
   * the Signal definitions for Blocks.
   */
  private SecSignal Signal;

  /**
   * the SwitchPoints definition for PtsEdge.
   */
  private SwitchPoints MyPoints;

  /**
   * the Double Cross Over definition for an edge.
   */
  private XEdge CrossOver;
  
  /**
   * a description of the Shared SecEdge.  The description is created by
   * either the XML reader or the Dialog.  In the case of Paste, the SecEdge
   * will be linked to null.
   * <p>
   * If this field is non-null, then the SecEdge is not linked into the
   * trackplan and DescribeEdge contains the Section coordinates and edge
   * of the adjoining SecEdge.  If this field is null, then the Joiner
   * contains a referance to the adjoining SecEdge.
   */
  protected Edge DescribeEdge;

  /**
   * constructs an EdgeBuilder with only its Edge identifier.  The rest of
   * the values will be read from the XML document.
   *
   * @param edge identifies the side of the GridTile the SecEdge is on.
   */
  public EdgeBuilder(int edge) {
    MyEdge = edge;
  }

  /*
   * tells the sub-component where its Section is, so that the sub-component
   * can replace itself and retrieve anything else it needs from the Section.
   */
  public void addSelf(Section sec) {
    if (MyBlock != null) {
      if ((Signal == null) ||!Signal.isRealSignal()) {
        BlkEdge bEdge = new BlkEdge(MyEdge, DescribeEdge, MyBlock, new Signal());
        bEdge.addSelf(sec);
      }
      else {
        CPEdge cEdge = new CPEdge(MyEdge, DescribeEdge, MyBlock, Signal);
        cEdge.addSelf(sec);
      }
    }
    else if (Signal != null) {
      CPEdge cEdge = new CPEdge(MyEdge, DescribeEdge, MyBlock, Signal);
      cEdge.addSelf(sec);
    }
    else if (MyPoints != null) {
      if (MyPoints.getSpur()) {
        PtsEdge pEdge = new PtsEdge(MyEdge, DescribeEdge, MyPoints);
        pEdge.addSelf(sec);
      }
      else {
        OSEdge oEdge = new OSEdge(MyEdge, DescribeEdge, MyPoints);
        oEdge.addSelf(sec);
      }
    }
    else if (CrossOver != null) {
      CrossOver.completeEdge(MyEdge);
      CrossOver.addSelf(sec);
    }
    else {
      SecEdge sEdge = new SecEdge(MyEdge, DescribeEdge);
      sEdge.addSelf(sec);
    }
  }

  /**
   * registers the SecEdge's icon on the painting surface.
   *
   * @param tile is where the Frills are painted.
   *
   * @see cats.gui.GridTile
   */
  public void install(GridTile tile) {
  }

  /*
   * asks the sub-component if it has anything to paint on the Screen.
   *
   * @return true if it does and false if it doen't.
   */
  public boolean isVisible() {
    return false;
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
    return new String(SecEdge.XML_TAG +
                      " XML Elements do not have Text fields ("
                      + eleValue + ").");
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
    if (SecSignal.XML_TAG.equals(objName)) {
      Signal = (SecSignal) objValue;
    }
    else if (Block.XML_TAG.equals(objName)) {
      MyBlock = (Block) objValue;
    }
    else if (SecEdge.SHARED.equals(objName)) {
      DescribeEdge = (Edge) objValue;
    }
    else if (SwitchPoints.XML_TAG.equals(objName)) {
      MyPoints = (SwitchPoints) objValue;
    }
    else if (XEdge.XML_TAG.equals(objName)) {
      CrossOver = (XEdge) objValue;
    }
    else {
      resultMsg = new String(objName + " is not recognized as an Element of "
                             + objName + " XML Elements.");
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
    return new String(SecEdge.XML_TAG);
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
   * registers a SecEdgeFactory with the XMLReader.
   */
  static public void init() {
    XMLReader.registerFactory(SecEdge.XML_TAG, new BuilderFactory());
  }

  static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(
      SecEdge.class.getName());
}

/**
 * is a Class known only to the SecEdge class for creating SecEdges from
 * an XML document.  Its purpose is to pick up the values in the SecEdge.
 * Because Secedges come in three forms - plain, block, and switch points -
 * and the distinguishing characteristics are XML embedded objects, the
 * SecEdgeFactory traps the embedded objects to determine which kind of
 * SecEdge to create.  Thus, the factory is both a factory and an
 * object.
 */
class BuilderFactory
    implements XMLEleFactory {

  /**
   * the Side of the Grid that the SecEdge being created resides on.
   */
  private int EdgeId;
  /*
   * tells the factory that an XMLEleObject is to be created.  Thus,
   * its contents can be set from the information in an XML Element
   * description.
   */
  public void newElement() {
    EdgeId = CtcFont.NOT_FOUND;
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
    if (tag.equals(SecEdge.EDGE)) {
      EdgeId = Edge.toEdge(value);
      if (EdgeId == CtcFont.NOT_FOUND) {
        return new String(value + " is not a valid value for a " +
                          SecEdge.XML_TAG + " XML Attribute.");
      }
      return null;
    }
    return new String(tag + " is not a valid XML Attribute for a " +
                      SecEdge.XML_TAG);
  }

  /*
   * tells the factory that the attributes have been seen; therefore,
   * return the XMLEleObject created.
   *
   * @return the newly created XMLEleObject or null (if there was a problem
   * in creating it).
   */
  public XMLEleObject getObject() {
    return new EdgeBuilder(EdgeId);
  }

}