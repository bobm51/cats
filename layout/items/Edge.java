/* Name: Edge.java
 *
 * What:
 *   This class holds the description of a SecEdge, while it is being
 *   read from an XML file.
 */

package cats.layout.items;

import cats.common.*;
import cats.layout.xml.*;

/**
 * holds the information needed for locating a SecEdge in a Node, while
 * the information is being read from an XML file.
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */
public class Edge
    implements XMLEleObject {

  /**
   *   the X coordinate
   */
  int EdgeX;

  /**
   *   the Y coordinate
   */
  int EdgeY;

  /**
   * the Edge
   */
  int EdgeEdge;

  /**
   * constructs the Edge, given the X and Y coordinates.
   *
   * @param x is the X coordinate of the Section.
   * @param y is the Y coordinate of the Section.
   */
  public Edge(int x, int y) {
    this(x, y, Prop.NOT_FOUND);
  }

  /**
   * constructs the Edge, given the X and Y coordinates and the edge.
   *
   * @param x is the X coordinate of the Section.
   * @param y is the Y coordinate of the Section.
   * @param edge is the edge on the Section.
   */
  public Edge(int x, int y, int edge) {
    EdgeX = x;
    EdgeY = y;
    EdgeEdge = edge;
  }
  /**
   * is a predicate for determining if two Edges reference the same place.
   *
   * @param test is the edge being compared with.
   *
   * @return true is all fields have the same value; otherwise, false.
   */
  public boolean equals(Edge test) {
    return ((test != null) && (EdgeX == test.EdgeX) && (EdgeY == test.EdgeY)
            && (EdgeEdge == test.EdgeEdge));
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
    int edge = toEdge(eleValue);
    if (edge != Prop.NOT_FOUND) {
      EdgeEdge = edge;
      return null;
    }
    return new String(SecEdge.SHARED +
                      " XML Elements do not have Text field values of " + eleValue);
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
    return new String(SecEdge.SHARED + " XML Elements do not have Elements.");
  }

  /*
   * returns the XML Element tag for the XMLEleObject.
   *
   * @return the name by which XMLReader knows the XMLEleObject (the
   * Element tag).
   */
  public String getTag() {
    return new String(SecEdge.SHARED);
  }

  /*
   * tells the XMLEleObject that no more setValue or setObject calls will
   * be made; thus, it can do any error chacking that it needs.
   *
   * @return null, if it has received everything it needs or an error
   * string if something isn't correct.
   */
  public String doneXML() {
    if (EdgeEdge == Prop.NOT_FOUND) {
      return new String("A " + SecEdge.SHARED + " is missing its Text field.");
    }
    return null;
  }

  /**
   * converts an Edge to a SecEdge.
   *
   * @return a SecEdge in a Section (if it exists) or null.
   */
//  public SecEdge toSecEdge() {
//    return SecEdge.findEdge(EdgeX, EdgeY, EdgeEdge);
//  }

  /**
   * registers a SecEdgeFactory with the XMLReader.
   */
  static public void init() {
    XMLReader.registerFactory(SecEdge.SHARED, new EdgeFactory());
  }

  /**
   * converts a String to an Edge identification.
   *
   * @param str is the String being converted.
   *
   * @return an Edge (RIGHT, BOTTOM, LEFT, or TOP) or CtcFont.NOT_FOUND
   */
  public static int toEdge(String str) {
    return Prop.findString(str, Sides.EDGENAME);
  }

  /**
   * converts an Edge to a String.
   *
   * @param edge is the edge being converted.
   *
   * @return the String corresponding to the Edge or "NOT_FOUND"
   */
  public static String fromEdge(int edge) {
    if ( (edge >= 0) && (edge < Sides.EDGENAME.length)) {
      return new String(Sides.EDGENAME[edge]);
    }
    return "NOT_FOUND";
  }
}

/**
 * is a Class known only to the SecName class for creating SecNames from
 * an XML document.  Its purpose is to pick up the location of the Label
 * in the GridTile.
 */
class EdgeFactory
    implements XMLEleFactory {

  String XAttrib;
  String YAttrib;

  /*
   * tells the factory that an XMLEleObject is to be created.  Thus,
   * its contents can be set from the information in an XML Element
   * description.
   */
  public void newElement() {
    XAttrib = null;
    YAttrib = null;
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
    if (tag.equals(SecEdge.X)) {
      XAttrib = new String(value);
    }
    else if (tag.equals(SecEdge.Y)) {
      YAttrib = new String(value);
    }
    else {
      resultMsg = new String(tag + " is not a valid attribute for a " +
                             SecEdge.SHARED);
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
    if ( (XAttrib != null) && (YAttrib != null)) {
      return new Edge(Integer.parseInt(XAttrib), Integer.parseInt(YAttrib));
    }
    System.out.println("Missing XML Attributes for " + SecEdge.SHARED);
    return null;
  }
}
