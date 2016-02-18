/* Name: Track.java
 *
 * What:
 *   This class is the container for all the information about a piece of
 *   Track.
 */

package cats.layout.items;

import cats.common.Prop;
import cats.common.Sides;
import cats.gui.DirectionArrow;
import cats.gui.GridTile;
import cats.gui.frills.rails.*;
import cats.layout.ColorList;
import cats.layout.xml.*;
import java.awt.Point;

/**
 * is a container for identifying Tracks in an XML document.
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2004, 2009, 2013</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */
public class Track
    implements XMLEleObject, SignalWire {
  /**
   *   The XML tag for recognizing the Track description.
   */
  public static final String XML_TAG = "TRACK";

  /**
   * The XML attribute tag for identifying the authorized speed.
   */
  public static final String SPEED_TAG = "SPEED";

  /**
   * The 4 authorized speeds for a Track segment, ordered by fastest
   * to slowest.
   */
  public static final String[] TrackSpeed = {
      "Default",
      "Normal",
      "Limited",
      "Medium",
      "Slow",
      "None",
      "Approach"
  };

  /**
   * the index of "Default"
   */
  public static final int DEFAULT = 0;

  /**
   * the index of "Normal"
   */
  public static final int NORMAL = 1;

  /**
   * the index of "Limited"
   */
  public static final int LIMITED = 2;

  /**
   * the index of "Medium"
   */
  public static final int MEDIUM = 3;

  /**
   * the index of "Slow"
   */
  public static final int SLOW = 4;
  
  /**
   * the index of "None"
   */
  public static final int NONE = 5;
  
  /**
   * the index of "Approach" speed
   */
  public static final int APPROACH = 6;
  
  /**
   * Though not in the list, a signal may be showing Stop.
   */
  public static final int STOP = TrackSpeed.length;

  /**
   * the authorized track speed.  It is an index into TrackSpeed.
   */
  protected int TrkSpeed;

  /**
   * The names of the 6 type of Tracks
   */
  public static final String TrackName[] = {
      "VERTICAL", // TOP-BOTTOM
      "HORIZONTAL", // RIGHT-LEFT
      "UPPERSLASH", // LEFT-TOP
      "LOWERSLASH", // RIGHT-BOTTOM
      "UPPERBACKSLASH", // RIGHT-TOP
      "LOWERBACKSLASH" // LEFT-BOTTOM
  };

  /**
   * the SecEdges that are the ends of each of the above Track Types.
   */
  protected static final int Termination[][] = {
      {
      Sides.BOTTOM, Sides.TOP}
      , {
      Sides.RIGHT, Sides.LEFT}
      , {
      Sides.LEFT, Sides.TOP}
      , {
      Sides.RIGHT, Sides.BOTTOM}
      , {
      Sides.RIGHT, Sides.TOP}
      , {
      Sides.BOTTOM, Sides.LEFT}
  };

  /**
   * The index of vertical track.
   */
  public static final int VERTICAL = 0;

  /**
   * The index of horizontal track.
   */
  public static final int HORIZONTAL = 1;

  /**
   * is the Name of the kind of Track.
   */
  protected String TrackString;

  /**
   * the kind of Track.
   */
  protected int TrackType;

  /**
   * the SecEdges at each end of the Track
   */
  protected SecEdge[] TrackEnds;

  /**
   * flags indicating if the PtsEdge on an end has the switch points
   * aligned for this track.  Fr a crossing, Aligned is set to false
   * if the cross track is in use.
   */
  protected boolean[] Aligned = {
      true,
      true
  };

  /**
   * the Block that the Track belongs to.
   */
  protected Block TrackBlock;

  /**
   * the GridTile where the Track is shown.
   */
  private GridTile TrkTile;

  /**
   * the Frill that shows the Track.
   */
  private RailFrill TrackFrill;

  /**
   * is the dominating condition on the track.  This is the index
   * of a bit in the Block's Blocker variable.
   * 
   * The legal values are:
   * <ul>
   * <li>TRK_RESERVED
   * <li>TRK_IDLE
   * <li>TRK_OCCUPIED
   * <li>TRK_AND_TIME
   * <li>TRK_OOS
   * <li>TRK_WARRANT
   * </ul>
   */
  protected int TrkState;

  /**
   * Following are the kinds of reservation through a Track.
   * NO_RESERVATION is handled above.
   */
  /**
   * no reservation has been made for traffic.
   */
  public static final int NO_TRAFFIC_SET = 0;

  /**
   * the reservation is for a single train movement.
   */
  public static final int SINGLE_MOVE = NO_TRAFFIC_SET + 1;

  /**
   * the reservation is for DTC.
   */
  public static final int DTC_RESERVATION = SINGLE_MOVE + 1;

  /**
   * describes the traffic reservation through the track.
   */
  protected int TrafficSet;

  /**
   * constructs the Track from scratch.
   *
   * @param speed is the timetable track speed.
   */
  public Track(int speed) {
    TrkSpeed = speed;
    TrackEnds = new SecEdge[2];
    TrafficSet = NO_TRAFFIC_SET;;
    TrkState = Block.TRK_IDLE;
  }

  /**
   * returns the Track's ends.
   * @return an a 2 element array, listing which SecEdges the
   * track touches.
   */
  public int[] getTrackEnds() {
    return Termination[TrackType];
  }

  /**
   * is a query to test the kind of Track.
   *
   * @param query is the candidate type of track.
   *
   * @return true if TrackType is the same as query.
   */
  public boolean queryTrackType(int query) {
    return TrackType == query;
  }

  /**
   * returns the SecEdge on one end of the Track.
   *
   * @param e is the index of the edge
   *
   * @return the SecEdge at index e,  e must be 0 or 1.
   */
  public SecEdge getEnd(int e) {
    if (e == 0) {
      return TrackEnds[0];
    }
    if (e == 1) {
      return TrackEnds[1];
    }
    return null;
  }

  /**
   * returns the prescribed track speed.
   *
   * @return the authorized maximum speed over the track.
   */
  public int getSpeed() {
    return TrkSpeed;
  }

  /**
   * sets the prescribed track speed.  This is used to provide a speed
   * when the "default" value is read in.
   *
   * @param speed is the new speed.
   */
  public void setSpeed(int speed) {
    if ( (speed > 0) && (speed < TrackSpeed.length)) {
      TrkSpeed = speed;
    }
  }

  /**
   * is called to determine which of two speeds is slower.
   *
   * @param speed1 is one speed
   * @param speed2 is the other
   *
   * @return the slower of the two, based on the Speed constants, above.
   */
  public static int getSlower(int speed1, int speed2) {
    if ( (speed1 < DEFAULT) || (speed1 > STOP)) {
      return speed2;
    }
    if ( (speed2 < DEFAULT) || (speed2 > STOP)) {
      return speed1;
    }
    return (speed1 < speed2) ? speed2 : speed1;
  }

  /**
   * finds which end (of the two) the SecEdge desired is.
   *
   * @param edge should be one of the two Terminations.
   *
   * @return the index to the end or -1 if it isn't one of the two.
   */
  private int findEdge(SecEdge edge) {
    if (TrackEnds[0] == edge) {
      return 0;
    }
    else if (TrackEnds[1] == edge) {
      return 1;
    }
    return -1;
  }

  /*
   * searches for the signal on the other end of the wire from a
   * SecEdge.  If the SignalWire is a Track, then the next signal cannot be
   * closer than the SecEdge found by traversing the Track.  If the
   * SignalWire is an Xtrack, then the signal is the one protecting the
   * crossing on the side of the caller.  Furthermore, the signal
   * must locate its feeder by initiating a probe from the other side
   * of the crossing.
   *
   * @param probe is the end originating the probe.
   *
   * @return the next Signal encountered in the direction of the probe.
   * It could be null.
   *
   * @see cats.layout.items.SecEdge
   * @see cats.layout.items.Signal
   */
  public Signal findSignal(SecEdge caller, Indication holder) {
    SecEdge nextEdge = getDestination(caller);
    if (nextEdge != null) {
      return nextEdge.neighborProbe(this, holder);
    }
    return null;
  }

  /**
   * determines if the caller is one termination of the track.  If so,
   * it returns the other end.
   * @param caller is the SecEdge wanting to know the edge
   * at the other end.
   *
   * @return the SecEdge at the other end, if the caller is one termination;
   * otherwise, null.
   *
   * @see cats.layout.items.SecEdge
   */
  public SecEdge getDestination(SecEdge caller) {
    if (TrackEnds[0] == caller) {
      return TrackEnds[1];
    }
    if (TrackEnds[1] == caller) {
      return TrackEnds[0];
    }
    return null;
  }

  /**
   * determines which SecEdge is attached to the "other" end of the Track.
   * This is similar to getDestination except, not only must the "other"
   * end exist, but if it is a PtsEdge, the points must be aligned for
   * this Track.
   * @param caller is the edge where traversal begins.
   *
   * @return the SecEdge at the other end, if the caller is one termination
   * and it is aligned for the Edge; otherwise, return null;
   *
   * @see cats.layout.items.SecEdge
   */
  public SecEdge traverse(SecEdge caller) {
    int other = -1;
    if (TrackEnds[0] == caller) {
      other = 1;
    }
    if (TrackEnds[1] == caller) {
      other = 0;
    }
    if ( (other != -1) && Aligned[other]) {
      return TrackEnds[other];
    }
    return null;
  }

  /**
   * is called to tell the Track when one of its ends is fouled (a train
   * cannot pass through it) or not.  An edge can be fouled for many reasons,
   * such as it is switch points and the points are aligned to another track;
   * it is part of a crossing and the perpendicular track is occupied or has
   * a reservation or has track authority or ...
   *
   * @param edge is one of the ends, the one being fouled or unfouled.
   * @param aligned is true if the track not fouled and false if it is.
   */
  public void alignTrkEnd(SecEdge edge, boolean aligned) {
    int which = findEdge(edge);
    if ( (which != -1) && (Aligned[which] != aligned)) {
      Aligned[which] = aligned;
    }    
  }
  
  /**
   * is called to tell the Track when one of its ends is fouled because
   * the end is PtsEdge and the points have moved.
   *
   * @param edge is one of the ends, the one being fouled or unfouled.
   * @param aligned is true if the track not fouled and false if it is.
   */
  public void switchEnd(PtsEdge edge, boolean aligned) {
    int which = findEdge(edge);
    if ( (which != -1) && (Aligned[which] != aligned)) {
      Aligned[which] = aligned;
      if (TrackFrill != null) {
        TrackFrill.setGap(Termination[TrackType][which], !aligned);
      }
    }
  }

  /**
   * is called to determine if Track is locked or not.  Locked means that
   * the points should not be moved,
   *
   * @return true if the Track is locked.
   */
  public boolean isUnLocked() {
    return ( (TrkState <= Block.TRK_IDLE) && (TrafficSet == NO_TRAFFIC_SET));
  }

  /**
   * is called when a Track receives a selection event.  If the points
   * can be moved, it aligns the ends.
   */
  public void selectTrack() {
    if (isUnLocked()) {
      if (!TrackEnds[0].isEdgeLocked(Termination[TrackType][1])) {
        TrackEnds[0].setAlignment(Termination[TrackType][1]);
      }
      if (!TrackEnds[1].isEdgeLocked(Termination[TrackType][0])){
        TrackEnds[1].setAlignment(Termination[TrackType][0]);
      }
    }
  }

  /**
   * propagates the state of the Block to the Track.
   *
   * @param state is the state of the Block.
   * @see cats.layout.items.Indication
   */
  public void setTrkState(int state) {
    TrkState = state;
    mergeTrkStates();
  }

  /**
   * merges the local track conditions with the Block conditions to
   * determine the state of the track.  There are two local conditions:
   * <ol>
   * <li> traffic has been routed through the track
   * <li> the cross track is in use
   * </ol>
   * Either of these overrides the Block state.  The Block state should be
   * preserved because the local conditions may clear.
   */
  protected void mergeTrkStates() {
    int state = TrkState;
    String value;
    if ((TrafficSet != NO_TRAFFIC_SET) && (state == Block.TRK_IDLE)) {
      state = (TrafficSet == SINGLE_MOVE)  ? Block.TRK_RESERVED :
          Block.TRK_WARRANT;
    }
    if (Aligned[0]) {
      TrackEnds[0].setState(state);
    }
    if (Aligned[1]) {
      TrackEnds[1].setState(state);
    }
    if (TrackFrill != null) {
      switch (state) {
        case Block.TRK_AND_TIME:
          value = ColorList.LOCAL;
          break;

        case Block.TRK_OOS:
          value = ColorList.OOSERVICE;
          break;

        case Block.TRK_OCCUPIED:
          value = ColorList.OCCUPIED;
          break;

        case Block.TRK_RESERVED:
          value = ColorList.RESERVED;
          break;

        case Block.TRK_WARRANT:
          value = ColorList.DTC;
          break;

        default:
          if ((TrackBlock != null ) && TrackBlock.isDarkTerritory()) {
            value = ColorList.DARK;
          }
          else {
            value = ColorList.EMPTY;
          }
      }
      TrackFrill.setColor(value);
      TrkTile.requestUpdate();
    }
  }

  /**
   * determines if the caller is one termination of the track.  If so,
   * it returns the Side of the other termination.
   * @param caller is the SecEdge wnating to know where the
   * other end is.
   *
   * @return the Side of the other end, if it exists.
   *
   * @see cats.common.Sides
   */
  public int getTermination(SecEdge caller) {
    if (TrackEnds[0] == caller) {
      return Termination[TrackType][1];
    }
    if (TrackEnds[1] == caller) {
      return Termination[TrackType][0];
    }
    return -1;
  }

  /**
   * sets the SecEdges on the ends of the Track.  The edges are set to
   * line up as in Termination, for ease of later reference.
   *
   * @param edge1 is one edge.
   * @param edge2 is the other
   */
  public void setEdges(SecEdge edge1, SecEdge edge2) {
    boolean valid = false;
    int edge = edge1.getEdge();
    if (Termination[TrackType][0] == edge) {
      TrackEnds[0] = edge1;
      if (Termination[TrackType][1] == edge2.getEdge()) {
        valid = true;
      }
      TrackEnds[1] = edge2;
    }
    else if (Termination[TrackType][1] == edge) {
      TrackEnds[1] = edge1;
      if (Termination[TrackType][0] == edge2.getEdge()) {
        valid = true;
      }
      TrackEnds[0] = edge2;
    }
    else {
      TrackEnds[0] = edge1;
      TrackEnds[1] = edge2;
    }
    if (!valid) {
      Point pt = edge2.getSection().getCoordinates();
      log.warn("One end for a Track in Section (" + pt.x + "," +
               pt.y + ") does not terminate on the correct edge.");
    }
  }

  /**
   * replaces the SecEdge at one end of the track.  This method is
   * invoked by Double Crossings to replace a XEdge with one of its
   * component SecEdges.
   * @param oldEdge is the SecEdge (XEdge) being replaced
   * @param newEdge is the replacement SecEdge
   */
  public void replaceEdge(SecEdge oldEdge, SecEdge newEdge) {
    if (TrackEnds[0] == oldEdge) {
      TrackEnds[0] = newEdge;
    }
    else if (TrackEnds[1] == oldEdge) {
      TrackEnds[1] = newEdge;
    }
    else {
      log.warn("Double Crossing is malformed");
    }
  }
  
  /**
   * sets the Block that encompasses the Track.  A side affect of telling
   * the Track what Block it is, is that it registers itself with that
   * Block.
   *
   * @param block is the Block.
   */
  public void setBlock(Block block) {
    if (TrackBlock == null) {
      TrackBlock = block;
      block.registerTrack(this);
      TrackEnds[0].setBlock(block);
      TrackEnds[1].setBlock(block);
    }
    else if (TrackBlock != block) {
      Point pt = TrackEnds[0].getSection().getCoordinates();
      log.warn("A track in Section (" + pt.x + "," + pt.y +
               ") has multiple Block definitions.");
    }
  }

  /**
   * adds or removes the traffic arrowhead.
   *
   * @param arrow is true if the arrowhead should be added and false if it
   * should be removed.
   * @param edge is the BlkEdge making the request.
   */
  public void setArrow(boolean arrow, SecEdge edge) {
    if (DirectionArrow.TheArrowType.getFlagValue() && (TrackFrill != null)) {
      int e = findEdge(edge);
      if (arrow) {
        TrackFrill.setDOT(Termination[TrackType][e]);
      }
      else {
        TrackFrill.setDOT(RailFrill.NO_DOT);
      }
    }
  }

  /**
   * locks or unlocks the decoders on each end of the Track.  Because
   * there may be multiple lockers, telling a decoder to unlock may
   * not actually unlock it.
   * @param lock is true to lock the decoders; false to unlock
   * the decoders.
   * @see cats.layout.items.LockedDecoders
   */
  public void lockEnds(boolean lock){
    TrackEnds[0].setEdgeLock(lock);
    TrackEnds[1].setEdgeLock(lock);
  }
  
  /**
   * adds or removes the traffic condition.
   *
   * @param traffic describes the traffic direction.
   */
  public void setTraffic(int traffic) {
    if (TrafficSet == NO_TRAFFIC_SET) {
      if (traffic != NO_TRAFFIC_SET) {
        lockEnds(true);
      }
    }
    else if (traffic == NO_TRAFFIC_SET) {
      lockEnds(false);
    }
    TrafficSet = traffic;
    mergeTrkStates();
  }

  /**
   * clears the history of any associated signals.
   */
  public void clrTrkHistory() {
    if (TrackEnds[0] != null) {
      TrackEnds[0].clrEdgeHistory();
    }
    if (TrackEnds[1] != null) {
      TrackEnds[1].clrEdgeHistory();
    }
  }

  /**
   * returns the Block the Track is contained in.
   *
   * @return the Block.  It should not be null, but if the layout is not
   * configured correctly, it will be.
   */
  public Block getBlock() {
    return TrackBlock;
  }

  /**
   * adds the icon for the Track to the GridTile
   * @param tile is the Tile being decorated.
   */
public void install(GridTile tile) {
    TrkTile = tile;
    if (tile != null) {
      switch (TrackType) {
        case 0:
          TrackFrill = new VertRailFrill();
          break;

        case 1:
          TrackFrill = new HorzRailFrill();
          break;

        case 2:
          TrackFrill = new UpperSlashFrill();
          break;

        case 3:
          TrackFrill = new LowerSlashFrill();
          break;

        case 4:
          TrackFrill = new UpperBackFrill();
          break;

        case 5:
          TrackFrill = new LowerBackFrill();
          break;

        default:
          Point pt = TrackEnds[0].getSection().getCoordinates();
          log.warn("Unknown Track type in Section (" + pt.x + "," +
                   pt.y + ").");
      }
      if (TrackFrill != null) {
        TrackFrill.setTrack(this);
        tile.addFrill(TrackFrill);
        if (!Aligned[0]) {
          TrackFrill.setGap(Termination[TrackType][0], true);
        }
        if (!Aligned[1]) {
          TrackFrill.setGap(Termination[TrackType][1], true);
        }
        TrackFrill.setColor(ColorList.EMPTY);
      }
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
  TrackString = eleValue;
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
    String resultMsg = new String("A " + XML_TAG +
                                  " cannot contain an Element ("
                                  + objName + ").");
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
    String resultMsg = null;
    TrackType = Prop.findString(TrackString, TrackName);
    if (TrackType == Prop.NOT_FOUND) {
      resultMsg = new String(TrackType +
                             " is not a valid XML text element for "
                             + XML_TAG + ".");
    }
    return resultMsg;
  }

  /**
   * registers a TrackFactory with the XMLReader.
   */
  static public void init() {
    XMLReader.registerFactory(XML_TAG, new TrackFactory());
  }

  static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(
      Track.class.getName());
}

/**
 * is a Class known only to the Track class for creating Tracks from
 * an XML document.
 */
class TrackFactory
    implements XMLEleFactory {

  /**
   * is the authorized speed across the track.
   */
  private int Speed;

  /*
   * tells the factory that an XMLEleObject is to be created.  Thus,
   * its contents can be set from the information in an XML Element
   * description.
   */
  public void newElement() {
    Speed = Track.DEFAULT;
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
    if (Track.SPEED_TAG.equals(tag)) {
      if ( (Speed = Prop.findString(value, Track.TrackSpeed)) ==
          Prop.NOT_FOUND) {
        Speed = 0;
        resultMsg = new String(tag + " is not a recognized track speed.");
      }
    }
    else {
      resultMsg = new String(tag + " is not a valid XML Attribute for a " +
                             Track.XML_TAG);
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
    return new Track(Speed);
  }
}
/* @(#)Track.java */