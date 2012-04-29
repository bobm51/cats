/* Name: RouteInfo,java
 *
 * What;
 *  RouteInfo is a passive data structure for holding the information needed
 *  for aligning turnout points to a particular route.
 */
package cats.layout.items;

import cats.common.Sides;
import cats.gui.CtcFont;
import cats.layout.xml.*;

/**
 * is a passive data structure for holding the information needed for
 * aligning turnouts to a particular route.   The data structure is an array
 * of Vector of IOSpec, with one entry for each of:
 * <ul>
 * <li>
 *   the request from a manual switch on the fascia for selecting the route.
 * <li>
 *   the command, sent to the stationary decoder, to select the route.
 * <li>
 *   the report from a sensor at the turnout, indicating that the turnout
 *   has been aligned for the route.
 * <li>
 *   the report from a sensor at the turnout, indicating that the turnout
 *   has not been aligned for the route.
 * </ul>
 *
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2004, 2009</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */

public class RouteInfo
    implements XMLEleObject {

  /**
   * is the tag for identifying a SwitchPoints Object in the XML file.
   */
  static final String XML_TAG = "ROUTEINFO";

  /**
   * is the attribute tag for identifying the route, by its other end.
   */
  static final String ROUTE_ID = "ROUTEID";

  /**
   * is the tag for identifying the Normal attribute.
   */
  static final String NORMAL_TAG = "NORMAL";

  /**
   * is the String for confirming that the route is the Normal route.
   */
  static final String ISNORMAL = "true";

  /**
   * is the enumerated value for no feedback on the route
   */
  static final int NO_FEEDBACK = 0;
  
  /**
   * is the enumerated value for positive feedback (feedback on make or break)
   */
  static final int POSITIVE_FEEDBACK = 1;
  
  /**
   * is the enumerated value fo exact feedback (feedback on make and break)
   */
  static final int EXACT_FEEDBACK = 2;
  
  /**
   * is a table of labels and XML tags for the lists of decoder information.
   */
  public static final String[][] RouteName = {
      {
      "Select Route Request", "ROUTEREQUEST"}
      , {
      "Route Selected Report", "SELECTEDREPORT"}
      , {
      "Route Unselected Report", "NOTSELECTEDREPORT"}
  };

  /**
   * is the association between a label and XML tag for the list
   * of commands for moving the switch points.
   */
  public static final String[] RouteCommand = {
      "Select Route Command", "ROUTECOMMAND"
  };

  /**
   * is an index into the above for selecting a label.
   */
  public static final int LAB_INDEX = 0;

  /**
   * is an index into the above for selecting an XML tag.
   */
  public static final int TAG_INDEX = 1;

  /**
   * the information about the route.
   */
  private Detector[] RouteSpecs = new Detector[RouteName.length];

  /**
   * is the decoder command for setting the route.
   */
  private IOSpec RouteCmd;

  /**
   * are indexes into the above.
   */
  public static final int ROUTEREQUEST = 0;
  /**
   * indexes the IOSpec for when the route is selected
   */
  public static final int SELECTEDREPORT = 1;
  /**
   * indexes the IOSPec for when the route is de-selected.
   */
  public static final int NOTSELECTEDREPORT = 2;

  /**
   * the SecEdge where the other end of the route terminates.
   */
  private int Destination;

  /**
   * a true flag meaning that this route is the normal or through route.
   * Only one route in the SwitchPoints can be the normal route.
   */
  private boolean NormalRoute = false;

  /**
   * the constructor.
   *
   * @param dest is the name of the edge that the other end terminates on.
   */
  public RouteInfo(int dest) {
    Destination = dest;
  }

  /**
   * returns the Route identity.
   *
   * @return the Route identity.  It will be a valid integer between
   * 0 and Edge.EDGENAME.length.
   */
  public int getRouteId() {
    return Destination;
  }

  /**
   * returns one of the decoder report lists.
   *
   * @param id is the index of the report requested.  If it refers to
   * a non-existent list, nothing happens.
   *
   * @return the list.  It can be null.
   */
  public IOSpec getRouteReport(int id) {
    if ( (id >= 0) && (id < RouteName.length) && (RouteSpecs[id] != null)) {
      return RouteSpecs[id].getSpec();
    }
    return null;
  }

  /**
   * returns the command for moving the points.
   *
   * @return the command.
   */
  public IOSpec getRouteCmd() {
    return RouteCmd;
  }

  /**
   * changes the NormalRoute flag.
   *
   * @param normal is true if the route is the normal route or false if
   * it is a diverging route through the turnout.
   */
  public void setNormal(boolean normal) {
    NormalRoute = normal;
  }

  /**
   * retrieves the NormalRoute flag.
   *
   * @return true if the route is the normal route through the turnout or
   * false if it is a diverging route.
   */
  public boolean getNormal() {
    return NormalRoute;
  }

  /**
   * returns the kind of feedback on the route
   * @return NO_FEEDBACK if there are no feedback reports
   * <p>POSITIVE_FEEDBACK if there is only one report
   * <p>EXACT_FEEDBACK if there are two reports
   */
  public int getFeedback() {
    if (((RouteSpecs[SELECTEDREPORT] == null) ||
        (RouteSpecs[SELECTEDREPORT].getSpec() == null)) &&
        ((RouteSpecs[NOTSELECTEDREPORT] == null) ||
        (RouteSpecs[NOTSELECTEDREPORT].getSpec() == null))) {
      return NO_FEEDBACK;
    }
    else if (((RouteSpecs[SELECTEDREPORT] != null) &&
        (RouteSpecs[SELECTEDREPORT].getSpec() != null)) &&
        ((RouteSpecs[NOTSELECTEDREPORT] != null) &&
        (RouteSpecs[NOTSELECTEDREPORT].getSpec() != null))) {
      return EXACT_FEEDBACK;
    }
    return POSITIVE_FEEDBACK;
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
    return new String("A " + XML_TAG + " cannot contain a text field ("
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
    int field;
    for (field = 0; field < RouteName.length; ++field) {
      if (RouteName[field][TAG_INDEX].equals(objName)) {
        RouteSpecs[field] = (Detector) objValue;
        break;
      }
    }
    if (field == RouteName.length) {
      if (RouteCommand[TAG_INDEX].equals(objName)) {
        RouteCmd = ((Detector) objValue).getSpec();
        RouteCmd.registerLock();
      }
      else {
        resultMsg = new String("A " + XML_TAG + " cannot contain an Element ("
                               + objName + ").");
      }
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
   * be made; thus, it can do any error chacking that it needs.
   *
   * @return null, if it has received everything it needs or an error
   * string if something isn't correct.
   */
  public String doneXML() {
    return null;
  }

  /**
   * registers a RouteInfoFactory and the constituent ItemVectors
   * with the XMLReader.
   */
  static public void init() {
    XMLReader.registerFactory(XML_TAG, new RouteInfoFactory());
    for (int vec = 0; vec < RouteName.length; ++vec) {
      Detector.init(RouteName[vec][TAG_INDEX]);
    }
    Detector.init(RouteCommand[TAG_INDEX]);
  }
}

/**
 * is a Class known only to the RouteInfo class for creating Routes
 * from an XML document.
 */
class RouteInfoFactory
    implements XMLEleFactory {

  /**
   * the index of the edge on which the route terminates.
   */
  int Term;

  /**
   * is true if the normal route attribute is seen.
   */
  boolean Normal;

  /*
   * tells the factory that an XMLEleObject is to be created.  Thus,
   * its contents can be set from the information in an XML Element
   * description.
   */
  public void newElement() {
    Term = -1;
    Normal = false;
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
    int edge;

    if (RouteInfo.ROUTE_ID.equals(tag)) {
      if ( (edge = Edge.toEdge(value)) != CtcFont.NOT_FOUND) {
        Term = edge;
      }
      else {
        resultMsg = new String(value + " is not a valid value for a " +
                               tag + " attribute.");
      }
    }
    else if (RouteInfo.NORMAL_TAG.equals(tag) &&
             RouteInfo.ISNORMAL.equals(value)) {
      Normal = true;
    }
    else {
      resultMsg = new String("A " + RouteInfo.XML_TAG +
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
    RouteInfo route;
    if ( (Term >= 0) && (Term < Sides.EDGENAME.length)) {
      route = new RouteInfo(Term);
      route.setNormal(Normal);
      return route;
    }
    System.out.println("Missing edge attribute for a " + RouteInfo.XML_TAG
                       + " XML element.");
    return null;
  }
}
/* @(#)RouteInfo.java */