/* Name: SecEdge.java
 *
 * What:
 *   This class is the container for all the information about one
 *   of a Section's Edges.
 */

package cats.layout.items;

import cats.common.Sides;
import cats.gui.GridTile;
import cats.gui.Screen;
import java.awt.Dimension;
import java.awt.Point;

/**
 * is a container for all the information about one of a Section's Edges.
 * <p>
 * All Edges have several pieces of information:
 *<ul>
 *<li>
 * a link to a shared SecEdge - the one in the logically adjacent Section.
 *<li>
 * if the SecEdge is the end of a detection Block, then it contains
 * information about the Block, which it shares with all other SecEdges
 * that are also in the Block and are Block boundaries.
 *<li>
 * if the SecEdge is a Block boundary and has a Signal protecting entry
 * into the Block through the edge, then the Signal information.
 *<li>
 * if multiple Tracks terminate on the SecEdge (forming switch points),
 * then the information for controllng the points.  To keep things simple,
 * Block boundaries and switch points are mutually exclusive.  Thus,
 * a SecEdge may be
 * <ul>
 * <li>
 *  a Block boundary
 * <li>
 *  a set of switch Points (two or three terminations)
 * <li>
 *  neither of the above
 * </ul>
 * <li>
 * a description of where to find the companion SecEdge, needed for
 * setting up the Joiner.
 * </ul>
 * <p>
 * The design intent is that every Track has two terminations.
 * Each of those terminations is associated with a SecEdge for describing
 * where to find the connecting Track, how to set the Signals, how to
 * move the switch points, etc.  Therefore, a SecEdge may be created when a
 * Track is defined (if the SecEdge exists when the Track is defined, then
 * it is used and another is not created).
 * <p>
 * For two SecEdges to be joined,
 * they must be compatible.  They are incompatible only when one has switch
 * points and the other has a Block boundary.  The following enumeration
 * describes the result of joining two SecEdges:
 * <ol>
 *  <li>
 *   nothing, nothing: nothing
 * <li>
 *   nothing, points: nothing (but a Block boundary cannot be set)
 * <li>
 *   nothing, block boundary: block boundary for both
 * <li>
 *   points, nothing: nothing (but a Block boundary cannot be set)
 * <li>
 *   points, points: nothing (but a Block boundary cannot be set)
 * <li>
 *   points, block boundary: invalid
 * <li>
 *   block boundary, nothing: block boundary
 * <li>
 *   block boundary, points: invalid
 * <li>
 *   block boundary, block boundary: block boundary
 * </ol>
 *   When joining two SecEdges, there are several cases to consider:
 * <ul>
 * <li>
 * the connecting SecEdge does not exist and the Section does not exist.
 * The joint is not created, leaving the description for later use.  This
 * implies that the description field and joint reference are mutually
 * exclusive (neither can exist or only one can exist).
 * <li>
 * the connecting SecEdge does not exist, but the Section does.  In
 * anticipation of Track being layed in the connecting Section, a new SecEdge
 * is created in the connecting Section and the next case is used.
 * <li>
 * the connecting SecEdge exists, but is not linked anywhere: if the two
 * SecEdges are compatible, then the joint is created, using the above
 * rules.  This may affect the connecting SecEdge.
 * <li>
 * the connecting SecEdge exists and is linked somewhere:  this means
 * the orginal XML file has a problem.
 * </ul>
 * Finally, there is a lot of state information implied in the SecEdge and
 * its joint.
 * <ol>
 * <li>
 * if Connection is null, then the SecEdge is unlinked to anything.
 * <ul>
 *    <li>
 *    if DescribeEdge is null, then the SecEdge should be linked to its
 *    geographic neighbor.
 *    <li>
 *    if DescribeEdge is not null, then DescribeEdge tells where the
 *    link should be.
 * </ul>
 * <li>
 * if the SecEdge is not a Block boundary, then both MyBlock and Signal
 * are null.
 * <li>
 * if the SecEdge is a Block boundary then MyBlock is not null and MyBlock
 * describes the Block.  Signal will have a value if a Signal protects
 * entry into the Block through the SecEdge.
 * <li>
 * if the SecEdge is a Block boundary and linked, then both SecEdges will
 * have MyBlock or neither will have a MyBlock.
 * </ol>
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2004, 2009</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */
public class SecEdge
    implements Itemizable {
  static final String XML_TAG = "SEC_EDGE";
  static final String EDGE = "EDGE";
  static final String SHARED = "SHARED";
  static final String X = "X";
  static final String Y = "Y";
  // The following are picked up from the XML file and used to create
  // a specialized SecEdge.  They are static because they are not common
  // to all SecEdges, so the space they occupy should be confined to the
  // SecEdge each is a component of.  This is safe because the SecEdge
  // definitionis not recursive, so only one is processed at a time.

  /**
   * the Edge identification of this SecEdge.
   */
  protected int MyEdge;

  /**
   * the Section containing the SecEdge.  This field
   * is typically populated when the SecEdge is added to the Section.
   */
  protected Section MySection;

  /**
   * a description of the detection Block terminating on the SecEdge.
   */
  protected Block MyBlock;

  /**
   * the GridTile that the SecEdge paints.
   */
  protected GridTile EdgeTile;

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
   * the connection to the logically adjacent Track.
   */
  protected SecEdge Joint;

  /**
   * the Track which has a termination on this SecEdge.
   */
  protected Track Destination;

  /**
   * the signal connection to the other side of the Section.
   */
  protected SignalWire Wire;

  /**
   * the state of the Block and Track.
   */
  protected int TrkBlkState;

  /**
   * the Speed of the Track being protected.
   */
  protected int ProtSpeed = Track.DEFAULT;

  /**
   * constructs a SecEdge with only its Edge identifier.  The rest of
   * the values will be read from the XML document or set by the JDialog.
   *
   * @param edge identifies the side of the GridTile the SecEdge is on.
   * @param shared describes the shared edge, if it is not adjacent.
   *    A null value means it is adjacent.
   */
  public SecEdge(int edge, Edge shared) {
    MyEdge = edge;
    DescribeEdge = shared;
    TrkBlkState = Block.TRK_IDLE;
  }

  /**
   * returns the enclosing Section.
   * @return the Section containing the SecEdge
   *
   * @see Section
   */
  public Section getSection() {
    return MySection;
  }

  /**
   * returns the Edge identifier.
   *
   * @return RIGHT, BOTTOM, ...
   */
  public int getEdge() {
    return MyEdge;
  }

  /**
   * is used to determine if a SecEdge is a Block boundary.
   *
   * @return true if it is and false if it is not.
   */

  public boolean isBlock() {
    return false;
  }

  /**
   * is used to determine if a SecEdge is a Control Point.
   *
   * @return true if it is and false if it is not.
   */
  public boolean isControlPoint() {
    return false;
  }

  /**
   * is used to determine if a visible signal is protecting entrance
   * into the Section.
   *
   * @return true if a Signal is located on the SecEdge; false if it
   * is not.
   */
  public boolean hasSignal() {
    return false;
  }

  /**
   * is called from a Track to tell its termination ends which Blocks
   * they are in.  There are several cases to consider:
   * <ol>
   * <li> if the SecEdge isn't a Block boundary, then the Block identity
   *      must be sent to all tracks terminating on the SecEdge and to
   *      the shared SecEdge (if it exists) in a linked Section.
   * <li> if the SecEdge is a Block boundary, then the propagating
   *      is complete, because the shared SecEdge is in a different
   *      Block.
   * <li> if the SecEdge is a Block boundary and block is the same
   *      as MyBlock, then the method call is an "echo" from a Track.
   * <li> if the SecEdge is a Block boundary, and the block is not the
   *      same, then if MyBlock does not define a discipline, then block
   *      is the real Block, so it replaces MyBlock.
   * <li> if the SecEdge is a Block boundary. and the block is not the
   *      same and MyBlock does define a discipline, then there is a
   *      conflict.  The Blocks is defined differently at two ends.
   * </ol>
   * One other case can be ignored (MyBlock has a defined discipline and
   * block doesn't) because Blocks with an undefined discipline are never
   * entered into BlockKeeper.
   *
   * @param block is the Block identity being propagated.
   */
  public void setBlock(Block block) {
    if (MyBlock == null) {
      MyBlock = block;
      propagateBlock(block);
      if (Joint != null) {
        Joint.propagateBlock(block);
      }
    }
    else if (MyBlock != block) {
      if (MyBlock.getDiscipline() == Block.UNDEFINED) {
        MyBlock = block;
      }
      else {
        Point p = MySection.getCoordinates();
        log.warn("Side " + MyEdge + " in section " + p.x + "," + p.y +
                 " has a second definition for its Block.");
      }
    }
  }

  /**
   * returns the Block information.
   *
   * @return the Block.  If the SecEdge isn't a Block boundary, then nothing
   * is returned.
   *
   * @see Block
   */
  public Block getBlock() {
    return MyBlock;
  }

  /**
   * steps through the Tracks that have a termination on this Secedge,
   * telling them which Block they are members of.
   * @param block is the Block the SecEdge belongs to.
   */
  public void propagateBlock(Block block) {
//    if (Wire != null) {
//      Wire.setBlock(block);
//    }
    if (Destination != null) {
      Destination.setBlock(block);
    }
  }

  /**
   * finds the complementary Edge.
   *
   * @param edge is the Edge whose complement is desired.
   *
   * @return the complementary Edge.
   */
  static int findOtherEdge(int edge) {
    if ( (edge >= 0) && (edge <= Sides.EDGENAME.length)) {
      switch (edge) {
        case Sides.RIGHT:
          edge = Sides.LEFT;
          break;

        case Sides.BOTTOM:
          edge = Sides.TOP;
          break;

        case Sides.LEFT:
          edge = Sides.RIGHT;
          break;

        case Sides.TOP:
          edge = Sides.BOTTOM;
          break;

        default:
      }
    }
    return edge;
  }

  /**
   * returns the SecEdge sharing the edge.
   *
   * @return the SecEdge that shares the edge.
   *
   */
  public SecEdge getNeighbor() {
    return Joint;
  }

  /**
   * attempts to locate the Section touching this edge.
   *
   * @param p is the coordinates of the requesting Section
   * @param edge is an edge on the requesting Section
   *
   * @return the neighbor Section (if there is one) or null
   */
  static Section findAdjacentSection(Point p, int edge) {
    boolean valid = true;
    Dimension LayoutSize = Screen.DispatcherPanel.getLayoutSize();
    int x = p.x;
    int y = p.y;
    switch (edge) {
      case Sides.RIGHT:
        if (++x > LayoutSize.width) {
          valid = false;
        }
        break;

      case Sides.BOTTOM:
        if (++y > LayoutSize.height) {
          valid = false;
        }
        break;

      case Sides.LEFT:
        if (--x < 1) {
          valid = false;
        }
        break;

      case Sides.TOP:
        if (--y < 1) {
          valid = false;
        }
        break;

      default:
    }
    if (valid && (x > 0) && (y > 0)) {
      return Screen.DispatcherPanel.locateSection(x, y);
    }
    return null;
  }

  /**
   * binds this SecEdge to its mate.
   * <p>
   * The cases to consider are:
   * <ul>
   * <li> the mate is in a non-adjacent Section.
   * <li> the Section (either implict or explicit) does not exist.
   * <li> the Section exists, but the SecEdge doesn't.
   * <li> the Section and SecEdge exist, but the SecEdge is already bound.
   * <li> the Section and SecEdge exist and the Secedge is not bound.
   * </ul>
   * Only in the last case is a new binding created.
   */
  public void bind() {
    Section nSec;
    SecEdge nEdge = null;
    if (Joint == null) {
      if (DescribeEdge == null) {
        if ( (nSec = findAdjacentSection(MySection.getCoordinates(), MyEdge))
            != null) {
          nEdge = nSec.getEdge(findOtherEdge(MyEdge));
        }
      }
      else {
        if ( (nSec = Screen.DispatcherPanel.locateSection(DescribeEdge.EdgeX,
            DescribeEdge.EdgeY)) != null) {
          nEdge = nSec.getEdge(DescribeEdge.EdgeEdge);
        }
      }
      if (nEdge != null) {
        if (nEdge.Joint == null) {
          Joint = nEdge;
          nEdge.Joint = this;
          if ((isBlock() != nEdge.isBlock()) && (nSec != null)) {
            log.warn("Edge " + nEdge.MyEdge + " in Section " +
                     nSec.getCoordinates().x
                     + "," + nSec.getCoordinates().y +
                     " does not match its adjoining Block boundary.");
          }
        }
        else if ((nEdge.Joint != this) && (nSec != null)){
          log.warn("Edge " + nEdge.MyEdge + " in Section " +
                   nSec.getCoordinates().x
                   + "," + nSec.getCoordinates().y +
                   " has multiple connections.");
        }
      }
    }
    // else it has already been bound.
  }

  /**
   * remembers the Tracks that terminate on this SecEdge and returns true
   * if the Track is the default route through the edge.
   *
   * @param trk is the SecEdge at the other end.
   *
   * @return false because there is only one route through the edge.
   * Returning true will be misleading because it implies that the
   * edge can be switched.
   */
  public boolean setDestination(Track trk) {
    Destination = trk;
    if (Wire == null) {
      Wire = trk;
    }
    return false;
  }

  /**
   * remembers the wire for getting to the other side of the Section for
   * linking Signals.
   *
   * @param link is the connection.
   * @param which is the index of the other edge, for identifying the
   * tracks in switch points
   */
  public void setSignalWire(SignalWire link, int which) {
    Wire = link;
  }

  /**
   * is called from a SecEdge across the Section, looking for the next signal
   * in the direction of travel.  Since, this SecEdge is not an OSEdge, the
   * next signal is found by jumping into the neighboring Section.
   *
   * @param rte is the Track of the path taken to get across the Section.
   * It is needed by the OSSec for identifying which of the routes is
   * being probed.
   *
   * @param holder is a place to put the slowest speed found in the
   * direction of travel.
   *
   * @return the next Signal encountered in the direction of travel.  It
   * may be null.
   */
  public Signal neighborProbe(Track rte, Indication holder) {
    if (Joint != null) {
      return Joint.signalProbe(holder);
    }
    return null;
  }

  /**
   * is called from a SecEdge in another Section, looking for the next signal
   * in the direction of travel.  Since this SecEdge is not a block and is
   * not SwitchPoints, it has no signal, so it crosses the Section and passes
   * the request to the next SecEdge in the direction of travel.
   *
   * @param holder is a place to put the slowest speed found in the
   * direction of travel.
   *
   * @return the next Signal encountered in the direction of travel.  It
   * may be null.
   */
  public Signal signalProbe(Indication holder) {
    if (Destination != null)  {
      holder.setProtectedSpeed(Track.getSlower(Destination.getSpeed(),
                                               holder.getProtSpeed()));
      return Wire.findSignal(this, holder);
    }
    return null;
  }

  /**
   * is called to locate the Indication's feeder.  It is called during
   * initialization only.  Since, a SecEdge doesn't have a Signal,
   * there is nothing to do.
   */
  public void locateFeeder() {
  }

  /**
   * is called to set or clear a reservation through the Track that this
   * SecEdge terminates, from this SecEdge.
   *
   * For a simple SecEnd, there is nothing to do.
   *
   * @param reserve is true if the the reservation is being made and false
   * if it is being cleared.
   *
   * @return the SecEdge at the other end of the track
   */
//  protected SecEdge setEnterReservation(boolean reserve) {
//    Destination.setTraffic(reserve);
//    return Destination.traverse(this);
//  }

  /**
   * is called to set or clear an exit reservation through the Track that this
   * SecEdge terminates, out of this SecEdge.
   *
   * @param reserve is true if the the reservation is being made and false
   * if it is being cleared.
   */
  protected void setExitReservation(boolean reserve) {
    Destination.setArrow(reserve, this);
  }

  /**
   * yet another method for following the Tracks to the Block boundary.
   *
   * @return the BlkEdge on the far boundary of the Block, if it exists.
   * It should exist because a track with one end makes no sense.
   */
//  public BlkEdge blockProbe() {
//    SecEdge nextEdge = null;
//    if ( (Destination != null) &&
//        ( (nextEdge = Destination.traverse(this)) != null)) {
//      return nextEdge.crossSection();
//    }
//    return null;
//  }

  /**
   * yet another method for following the Tracks into an adjacent Section,
   * in search of a Block boundary.  Because a plain SecEdge is not a BlkEdge,
   * this method tries to traverse the adjacent Section.
   *
   * @return the results of crossing through the adjacent Section, if
   * it exists.  If it doesn't exist, then null.
   */
//  public BlkEdge crossSection() {
//    if (Joint == null) {
//      return null;
//    }
//    return Joint.blockProbe();
//  }

  /**
   * traverses the Section using the current switch alignment.
   *
   * @return the SecEdge on the other side of the Block, if it is possible
   * to get there, or null if it isn't.
   */
  public SecEdge traverse() {
    if (Destination != null) {
      return Destination.traverse(this);
    }
    return null;
  }

  /**
   * is called to return the Station name the SecEdge is associated with.
   * 
   * is called to identify where the SecEdge is on the dispatcher's panel.
   * If the tracks terminating on the SecEdge are in a Block and the Block
   * has a name, then the Block's name is returned.  Otherwise, if the Section
   * has a name, then it's name is returned.  Otherwise, the Section's
   * coordinates are returned.
   *
   * @return a String locating the SecEdge on the dispatcher's panel.
   */
  public String identify() {
//    String id;
//    if ( (Destination != null) && (Destination.getBlock() != null) &&
//        ( (id = Destination.getBlock().getBlockName()) != null)) {
//      return new String(id);
//    }
//    if ( (id = MySection.getName().getName()) != null) {
//      return new String(id);
//    }
//    return new String(MySection.getCoordinates().x +
//                      "," + MySection.getCoordinates().y);
    if ((Destination != null) && (Destination.getBlock() != null)) {
      return Destination.getBlock().getStationName();
    }
    return null;
  }

  /**
   * computes the speed limit across the Block.
   *
   * @param limit is the Indication holding the speed limit through the
   * protected block(s) and the next block(s)
   */
  protected void getSpeedLimit(Indication limit) {
    int prot = Destination.getSpeed();
    int next = Track.STOP;
    SecEdge enterEdge = this;
    SecEdge exitEdge;

    do {
      prot = Track.getSlower(prot, enterEdge.Destination.getSpeed());
    }
    while ( ( (exitEdge = enterEdge.traverse()) != null) &&
           ( (enterEdge = exitEdge.getNeighbor()) != null) &&
           !enterEdge.hasSignal());
    limit.setProtectedSpeed(prot);
    limit.setNextSpeed(next);
  }

  /*
   * tells the sub-component where its Section is, so that the sub-component
   * can replace itself and retrieve anything else it needs from the Section.
   */
  public void addSelf(Section sec) {
    MySection = sec;
    sec.replaceEdge(this, MyEdge);
  }

  /**
   * registers the SecEdge's icon on the painting surface.
   *
   * @param tile is where the Frills are painted.
   *
   * @see cats.gui.GridTile
   */
  public void install(GridTile tile) {
    EdgeTile = tile;
  }

  /**
   * tells the SecEdge that it does not appear on the painting surface.
   * For a plain Edge, this does nothing.
   */
  public void hidden() {

  }

  /*
   * asks the sub-component if it has anything to paint on the Screen.
   *
   * @return true if it does and false if it doen't.
   */
  public boolean isVisible() {
    return false;
  }

  /**
   * receives the state of the Track.  For a plain Section, there is
   * nothing to do.
   *
   * @param state is the Track's state.
   *
   * @see cats.layout.items.Track
   * @see cats.layout.items.Indication
   */
  public void setState(int state) {
    TrkBlkState = state;
  }

  /**
   * aligns the SecEdge for the calling Track.  For a plain edge, there
   * is nothing to do.
   *
   * @param trk identifies the Track.
   */
  public void setAlignment(int trk) {

  }

  /**
   * clears any Signal history.  For a plain edge, there is nothing to do.
   */
  public void clrEdgeHistory() {

  }

  /**
   * locks or unlocks the turnout decoders associated with the SecEdge.
   * This does nothing for any edge but one with SwitchPoints.
   * @param lock is true to lock the decoder commands and false to
   * unlock them.
   */
  public void setEdgeLock(boolean lock) {    
  }
  
  /**
   * tests if a decoder command for moving switch points on the SecEdge
   * have been locked.  Edges without points are always unlocked.
   * 
   * @param newTrk is the track being requested
   * @return true if the commands are locked and false is not.
   */
  public boolean isEdgeLocked(int newTrk) {
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
    return new String(XML_TAG + " XML Elements do not have Text fields ("
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
    return new String(XML_TAG + " XML Elements do not have embedded objects ("
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
   * registers a SecEdgeFactory with the XMLReader.
   */
  static public void init() {
    EdgeBuilder.init();
    Edge.init();
    SecSignal.init();
    SwitchPoints.init();
    Block.init();
    XEdge.init();
  }

  static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(
      SecEdge.class.getName());
}
/* @(#)SecEdge.java */
