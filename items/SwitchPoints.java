/* Name: SwitchPoints.java
 *
 * What:
 *  This class of Objects holds the information needed about Switch Points.
 */

package cats.layout.items;

import cats.common.Sides;
import cats.layout.xml.*;
/**
 * contains the information about Switch Points:
 * <ol>
 *    <li> the command to turn on the locked light.
 *    <li> the command to turn off the locked light.
 *    <li> the command to turn on the unlocked light.
 *    <li> the command to turn off the unlocked light.
 *    <li> the report when the turnout is locally unlocked.
 *    <li> the report when the turnout is locally locked.
 *    <li> the requests to move the turnout to each route.
 *    <li> the commands to move the turnout to each route.
 *    <li> the reports of the turnout reaching each route.
 *    <li> the reports of the turnout moving away from each route.
 * </ol>
 * No distinction is made between input messages and output messages.
 * So, if that changes in the future, this class will undergo some
 * major changes.
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2004, 2009</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */

public class SwitchPoints
    implements XMLEleObject {

  /**
   * is the tag for identifying a SwitchPoints Object in the XMl file.
   */
  static final String XML_TAG = "SWITCHPOINTS";

  /**
   * is the tag for identifying one of the messages in the XML file.
   */
  static final String MSG_TAG = "POINTSMSG";

  /**
   * is the tag for labeling the SwitchPoints as a spur or not.
   */
  static final String SPUR_TAG = "SPUR";

  /**
   * this table contains two loosely related items:
   * <ol>
   * <li>
   *     the labels for each IOSPEC as they appear on dialogs
   * <li>
   *     the tag for each IOSpec in the XML file
   * </ol>
   * The reason they are combined in this table is to reinforce that there
   * is a string of both uses for each IOSPEC.
   * <p>
   * LOCKOFFCMD and UNLOCKOFFCMD have been deprecated and should be removed.
   */
  public static final String[][] MessageName = {
      {
      "Turn Locked Light On", "LOCKONCMD"}
      , {
      "Turn Locked Light Off", "LOCKOFFCMD"}
      , {
      "Turn Unlocked Light On", "UNLOCKONCMD"}
      , {
      "Turn Unlocked Light Off", "UNLOCKOFFCMD"}
      , {
      "Turnout Unlocked Report", "UNLOCKREPORT"}
      , {
      "Turnout Locked Report", "LOCKREPORT"}
  };

  /**
   * the index into MessageName of the XML tag.
   */
  public static final int MSGXML = 1;

  /**
   * symbolic names for indexing each of the above
   */
  /**
   * a symbolic name for the index of the commands for turning on the
   * "Locked" light.
   */
  public static final int LOCKONCMD = 0;

  /**
   * a symbolic name for the index of the commands for turning off the
   * "Locked" light.
   */
  public static final int LOCKOFFCMD = 1;

  /**
   * a symbolic name for the index of the commands for turning on the
   * "Unlocked" light.
   */
  public static final int UNLOCKONCMD = 2;

  /**
   * a symbolic name for the index of the commands for turning off the
   * "Unlocked" light.
   */
  public static final int UNLOCKOFFCMD = 3;

  /**
   * a symbolic name for the index of the report sent when the turnout
   * is unlocked.
   */
  public static final int UNLOCKREPORT = 4;

  /**
   * a symbolic name for the index of the report sent when the turnout
   * is unlocked.
   */
  public static final int LOCKREPORT = 5;

  /**
   * the IOSpecs for each of the above.  Some of these entries
   * can be null.  For example, if there are no reports of the
   * turnout's position, then the last four will be null.  Others may
   * be null because the function is duplicated.  For example,
   * turning the lock light on may turn the unlock light off, but
   * both are included to handle the layout where different decoders
   * control the lock and unlock lights.
   */
  private IOSpec[] Decoders = new IOSpec[MessageName.length];

  /**
   * the list of requests, commands, and reports for each route.
   */
  private RouteInfo[] Routes = new RouteInfo[Sides.EDGENAME.length];

  /**
   * is a flag for indicating the SwitchPoints can aligned by the dispatcher
   * or not.  true means they are a spur (cannot be moved by the dispatcher)
   * and false means they are completely under dispatcher control.
   */
  private boolean Spur = false;

  /**
   * The index of the Normal route.
   */
  int NormalRoute;

  /**
   * The index of the last route selected.  It is needed to refresh the
   * layout from a test mode.
   */
  private int LastRoute;
  
  /**
   * the constructor.
   *
   * @param spur is true if the SwitchPoints are for a Spur and false if
   * they are for an OS section.
   */
  public SwitchPoints(boolean spur) {
    Spur = spur;
    NormalRoute = -1;
  }

  /**
   * fetches the Edge opposite of the SwitchPoints edge where the
   * Normal route terminates.
   *
   * @return NormalRoute.  It may not be set, if no Route has Normal
   * set.
   */
  public int getNormal() {
    return NormalRoute;
  }

  /**
   * returns the Spur flag.
   *
   * @return true if the SwitchPoints cannot be aligned by the dispatcher
   * or false if they can.
   */
  public boolean getSpur() {
    return Spur;
  }

  /**
   * retrieves a list of decoders.
   *
   * @param route is the side of the frog end of the points.
   *
   * @return the RouteInfo describing that leg of the turnout, which
   * may be null.
   */
  public RouteInfo getRouteList(int route) {
    if ( (Routes != null) && (route >= 0) && (route < Routes.length)) {
      return Routes[route];
    }
    return null;
  }

  /**
   * sets the points to a particular alignment.
   *
   * @param rt indexes the decoder commands to move the turnout(s)
   */
  public void selectRoute(int rt) {
    RouteInfo rtInfo;
    IOSpec decoder;

    LastRoute = rt;
    if ( (Routes != null) && (rt >= 0) && (rt < Routes.length) &&
        ( (rtInfo = Routes[rt]) != null) &&
        ( (decoder = rtInfo.getRouteCmd()) != null)) {
        decoder.sendCommand();
    }
  }

  /**
   * sets the points to their last known alignment.
   */
  public void restorePoints() {
    selectRoute(LastRoute);
  }

  /**
   * locks or unlocks the decoder commands.  When locked, commands that would
   * move the points are placed on the black list.  Unlocking removes them
   * from the list.
   * @param rt is the route which is being protected.
   * @param locked is true to lockout the other commands and true to remove
   * the lock
   */
  public void setRouteLock(int rt, boolean locked) {
    RouteInfo rtInfo;
    IOSpec decoder;
    if ((Routes != null) && (rt >= 0)  && (rt < Routes.length)) {
      for (int r = 0; r < Routes.length; ++r) {
//        if ((rt != r) && ((rtInfo = Routes[r]) != null) && 
        if (((rtInfo = Routes[r]) != null) && 
            ((decoder = rtInfo.getRouteCmd()) != null)) {
          if (locked) {
            decoder.lockOutCommand();
          }
          else {
            decoder.unlockOutCommand();
          }
        }
      }
    }
  }

  /**
   * reports if it is safe to send the command to set the points
   * to a route.
   * @param rt  is the route.
   * @return true if the decoder command has been put on the 
   * black list and false if it has not.
   */
  public boolean isRouteLocked(int rt) {
    RouteInfo rtInfo;
    IOSpec decoder;
    if ((Routes != null) && (rt >= 0)  && (rt < Routes.length)) {
      if (((rtInfo = Routes[rt]) != null) && 
          ((decoder = rtInfo.getRouteCmd()) != null)) {
        return decoder.isLockedOut();
      }
    }
    return false;
  }
  
  /**
   * tests that the decoders in a route are working by putting all of them
   * in a particular state.
   * 
   * @param state is the state to send to all decoders in all routes.
   */
  public void testRoutes(boolean state) {
    RouteInfo rtInfo;
    IOSpec decoder;

    if (Routes != null) {
      for (int rt = 0; rt < Routes.length; rt++) {
        if (((rtInfo = Routes[rt]) != null) &&
        ( (decoder = rtInfo.getRouteCmd()) != null)) {
            decoder.forceState(state);
        }
      }
    }
  }
  
  /**
   * retrieves the decoder command for controlling the lock/unlock
   * lights.
   *
   * @param light is the command for the light being requested.
   *
   * @return the IOSpec for the requested light.  It can be null if there
   * is no command for the light.
   */
  public IOSpec getLockCmds(int light) {
    if ( (light >= 0) && (light < Decoders.length)) {
      return Decoders[light];
    }
    return null;
  }

  /**
   * returns if the switch points have feedback defined.
   * @return true if the layout reports when the points move and
   * false if it does not.
   */
  public boolean hasFeedback()
  {
    RouteInfo rtInfo;
    if (Routes != null) {
      for (int rt = 0; rt < Routes.length; rt++) {
        if (((rtInfo = Routes[rt]) != null) &&
        ( rtInfo.getFeedback() != RouteInfo.NO_FEEDBACK)) {
            return true;
        }
      }
    }
    return false;
  }

  /**
   * checks that the points are supposed to be aligned to a particular
   * track.  If they are not, it moves them to the last commanded position.
   * This has the effect of countermanding any unauthorized local command
   * (when the points report in place movement.
   * 
   * @param trk is the index of the route the points are in (or commanded
   * to be in)
   */
  public void verifyMovement(int trk) {
    if (trk != LastRoute) {
      log.debug("Restoring " + trk + " to " + LastRoute);
      restorePoints();
    }
    else {
      log.debug("No correction");
    }
  }
  
  /**
   * is called when the points are moving from a position.  If the trk being
   * moved from is the last commanded position, then it moves them back.
   * This has the effect of countermanding any unauthorized local command
   * (when the points report release movement).
   * @param trk
   */
  public void verifyNoMovement(int trk) {
    if (trk == LastRoute) {
      restorePoints();
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
    SwitchMessage msg;
    RouteInfo newRoute;

    if (MSG_TAG.equals(objName)) {
      msg = (SwitchMessage) objValue;
      if ( (msg.Function >= 0) && (msg.Function < MessageName.length)) {
        Decoders[msg.Function] = msg.Message;
      }
      else {
        resultMsg = new String("A " + XML_TAG + " found an invalid message:"
                               + msg.Function);
      }
    }
    else if (RouteInfo.XML_TAG.equals(objName)) {
      newRoute = (RouteInfo) objValue;
      Routes[newRoute.getRouteId()] = newRoute;
      if (newRoute.getNormal()) {
        NormalRoute = newRoute.getRouteId();
        LastRoute = NormalRoute;
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
   * be made; thus, it can do any error chacking that it needs.
   *
   * @return null, if it has received everything it needs or an error
   * string if something isn't correct.
   */
  public String doneXML() {
    if ((NormalRoute == -1) && (Routes != null)) {
      return new String("The Normal Route has not been defined for Points.");
    }
    return null;
  }

  /**
   * registers a SwitchPointsFactory with the XMLReader.
   */
  static public void init() {
    XMLReader.registerFactory(XML_TAG, new SwitchPointsFactory());
    XMLReader.registerFactory(MSG_TAG, new SwitchMessageFactory());
    RouteInfo.init();
  }
  static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(SwitchPoints.class.getName());
}

/**
 * is an inner class for constructing messages.
 */
class SwitchMessage
    implements XMLEleObject {

  /**
   * is the function of the IOSpec.
   */
  public int Function = -1;

  /**
   * is the IOSpec.
   */
  public IOSpec Message;

  /**
   * is the String for capturing the message.
   */
  private String MsgName = null;

  /*
   * is the method through which the object receives the text field.
   *
   * @param eleValue is the Text for the Element's value.
   *
   * @return if the value is acceptable, then null; otherwise, an error
   * string.
   */
  public String setValue(String eleValue) {
    MsgName = new String(eleValue);
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
    if (IOSpec.XML_TAG.equals(objName)) {
      Message = (IOSpec) objValue;
    }
    else {
      resultMsg = new String("A " + SwitchPoints.MSG_TAG +
                             " cannot contain an Element ("
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
    return new String(SwitchPoints.MSG_TAG);
  }

  /*
   * tells the XMLEleObject that no more setValue or setObject calls will
   * be made; thus, it can do any error chacking that it needs.
   *
   * @return null, if it has received everything it needs or an error
   * string if something isn't correct.
   */
  public String doneXML() {
    for (int i = 0; i < SwitchPoints.MessageName.length; ++i) {
      if (SwitchPoints.MessageName[i][SwitchPoints.MSGXML].equals(MsgName)) {
        Function = i;
        return null;
      }
    }
    return new String("A " + SwitchPoints.MSG_TAG +
                      " cannot contain a text field ("
                      + MsgName + ").");
  }
}

/**
 * is a Class known only to the SwitchPoints class for creating SwitchPoints
 * from an XML document.
 */
class SwitchPointsFactory
    implements XMLEleFactory {

  /**
   * is true if a "Spur=true" attribute is found.
   */
  boolean FoundSpur;

  /*
   * tells the factory that an XMLEleObject is to be created.  Thus,
   * its contents can be set from the information in an XML Element
   * description.
   */
  public void newElement() {
    FoundSpur = false;
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

    if (SwitchPoints.SPUR_TAG.equals(tag)) {
      if (Block.TRUE.equals(value)) {
        FoundSpur = true;
      }
    }
    else {
      resultMsg = new String("A " + SwitchPoints.XML_TAG +
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
    return new SwitchPoints(FoundSpur);
  }
}

/**
 * is a Class known only to the SwitchPoints class for creating messages
 * from an XML document.
 */
class SwitchMessageFactory
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

    resultMsg = new String("A " + SwitchPoints.MSG_TAG +
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
    return new SwitchMessage();
  }
}
/* @(#)SwitchPoints.java */