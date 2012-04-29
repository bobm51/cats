/* Name: XEdge.java
 *
 * What:
 *   This class is a SecEdge for one half of a double crossover.  It is adjacent
 *   to a twin that forms the other half.
 *   <p>
 *   A XEdge is part PtsEdge in that two tracks terminate on it and part
 *   BlkEdge in that it forms a Block boundary.  However, unlike PtsEdge, there
 *   is no preferred (normal) route; there are no moving points; and it can
 *   (actually should) be a Block boundary.
 *   <p>
 *   The design is that it is a composition of two BlkEdge derivatives.  The
 *   XEdge handles the basic track drawing and the BlkEdges handle signaling,
 *   reservations, occupancy, etc.
 */
package cats.layout.items;

import cats.common.Prop;
import cats.common.Sides;
import cats.layout.xml.XMLEleFactory;
import cats.layout.xml.XMLEleObject;
import cats.layout.xml.XMLReader;

/**
 *   This class is a SecEdge for one half of a double crossover.  It is adjacent
 *   to a twin that forms the other half.
 *   <p>
 *   A XEdge is part PtsEdge in that two tracks terminate on it and part
 *   BlkEdge in that it forms a Block boundary.  However, unlike PtsEdge, there
 *   is no preferred (normal) route; there are no moving points; and it can
 *   (actually should) be a Block boundary.
 *   <p>
 *   The design is that it is a composition of two BlkEdge derivatives.  The
 *   XEdge handles the basic track drawing and the BlkEdges handle signaling,
 *   reservations, occupancy, etc.
 * 
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2009, 2010</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */
public class XEdge extends SecEdge {
  
  /**
   * is the tag for identifying a XEdge Object in the XML file.
   */
  static final String XML_TAG = "CROSSINGEDGE";

  /**
   * the state of a track from across the Section that may terminate on the SecEdge:
   * <ol>
   * <li>
   * NO_TRACK means no track from the identified edge terminates on the SecEdge
   * <li>
   * NO_BLOCK means a track from the identified edge terminates on the SecEdge
   * and it does not have a block boundary
   * <li>
   * BLOCK means that a track from the identified edge terminates on the SecEdge
   * and it is a block boundary
   * </ol>
   */
  static public final String[] TRACKSTATE = {
      "NO_TRACK",
      "NO_BLOCK",
      "BLOCK"
  };

  /**
   * Constants for identifying the above
   */
  /**
   * no track ends on this edge
   */
  static public final int NO_TRACK = 0;
  
  /**
   * a track without a block boundary ends on the edge
   */
  static public final int NO_BLOCK = 1;
  
  /**
   * a track with a block boundary ends on the edge
   */
  static public final int BLOCK = 2;
  
  /**
   * indexes of the Tracks that form the cross over.  The selector is
   * the edge that the XEdge resides on.
   */
//  static private final int[][] Rails = {
//    {1, 3}, // right side
//    {0, 2}, // bottom side
//    {1, 3}, // left side
//    {0, 2}  // top side
//  };
  
  /**
   * is a lookup table for specifying which Track in the adjoining
   * Section matches up with a Track.  The Tracks are identified by
   * the SecEdge that the other end terminates on.  For example, suppose
   * this SecEdge is 0 (RIGHT) and the track is "LOWERSLASH".  Then, the
   * Track is identified as 1 (it terminates on the BOTTOM edge - 1).
   * The "key" (index) of the table is the identity of a Track in this
   * SecEdge and the value is the identity of the complementary Track
   * in the adjoining SecEdge.
   */
  static private final int[] COMPLEMENT = {
      Sides.LEFT,
      Sides.TOP,
      Sides.RIGHT,
      Sides.BOTTOM
  };
  
  /**
   * the composing BlkEdges.  Only two are instantiated.  Four
   * are reserved to facilitate finding the two.
   */
  private XEdgeInterface MyEdges[] = new XEdgeInterface[Sides.EDGENAME.length];
  
  /**
   * the state of each of the four possible tracks.
   */
  private int[] TrackEnd = {
          NO_TRACK,
          NO_TRACK,
          NO_TRACK,
          NO_TRACK
  };
  
  /**
   * ctor
   *
   * @param edge identifies the side of the GridTile the SecEdge is on.
   * It will always be -1 on construction and filled in later through
   * a call to completeEdge.
   * @param shared describes the shared edge, if it is not adjacent.
   *    A null value means it is adjacent.  It should always be null.
   */
  public XEdge(int edge, Edge shared) {
    super(edge, shared);
  }

  /**
   * sets up the internal connections.
   * 
   * @param edge is the number of the edge SecEdge is on.
   */
  public void completeEdge(int edge) {
    MyEdge = edge;
    for (int i = 0; i < TrackEnd.length; ++i) {
      if (TrackEnd[i] != NO_TRACK) {
        setXedge(i, TrackEnd[i]);
      }
    }
  }
  
  /**
   * constructs one of the two composing SecEdges
   * 
   * @param oppEnd is the side of the Section that the opposite
   * end of the track terminates on.
   * @param type id the kind of SecEdge to use on that end
   */
  private void setXedge(int oppEnd, int type) {
    if (type == BLOCK) {
      MyEdges[oppEnd] = new XBlkEdge(MyEdge, null, MyBlock, new Signal());
    }
    else {
      MyEdges[oppEnd] = new XSecEdge(MyEdge, null);
    }
  }

  /**
   * marks one of the tracks as fouled so that traffic is prohibited from
   * traversing it.
   * @param fouled is true when the crossing track should be inhibited
   * and false when traffic is allowed.
   * @param instigator is the XEdgeInterface that is making/clearing a reservation.
   */
  private void foulXing(boolean fouled, XEdgeInterface instigator) {
    for (int i = 0; i < MyEdges.length; ++i) {
      if ((MyEdges[i] != null) && (MyEdges[i] != instigator)){
        MyEdges[i].setFouled(fouled);
        break;
      }
    }
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
    super.setBlock(block);
    if (MyBlock != null) {
      for (int i = 0; i < MyEdges.length; ++i) {
        if (MyEdges[i] != null) {
          MyEdges[i].setBlock(block);
        }
      }
    }
  }

  /**
   * tells the sub-component where its Section is, so that the sub-component
   * can register itself and retrieve anything else it needs from the Section.
   *
   * @param sec is the Section containing the sub-component.
   *
   * @see Section
   */
  public void addSelf(Section sec) {
    super.addSelf(sec);
    for (int i = 0; i < MyEdges.length; ++i) {
      if (MyEdges[i] != null) {
        MyEdges[i].setSection(sec);
      }
    }    
  }

  /**
   * binds this SecEdge to its mate.  For an XEdge to exist,
   * we know:
   * <ul>
   * <li> its mate exists
   * <li> its mate is adjacent
   * <li> its mate is also a XEdge
   * </ul>
   * Consequently, there are fewer conditions to consider than
   * for a generic SecEdge.
   */
  public void bind() {
    Section nSec;
    XEdge nEdge = null;
    if (Joint == null) {
      nSec = findAdjacentSection(MySection.getCoordinates(), MyEdge);
      Joint = nSec.getEdge(findOtherEdge(MyEdge));
      if (Joint == null) {
        log.warn("Double crossover at " + nSec.getCoordinates() + " is incorrectly formed.");
      }
      else {
        nEdge = (XEdge) Joint;
        nEdge.Joint = this;
//        MyEdges[Rails[MyEdge][0]].bindTo(nEdge.MyEdges[Rails[MyEdge][1]]);
//        MyEdges[Rails[MyEdge][1]].bindTo(nEdge.MyEdges[Rails[MyEdge][0]]);
        for (int edge = 0; edge < MyEdges.length; ++edge) {
          if (MyEdges[edge] != null) {
            MyEdges[edge].bindTo(nEdge.MyEdges[COMPLEMENT[edge]]);
          }
        }
      }
    }
    // else it has already been bound
  }
  
  public boolean setDestination(Track trk) {
    int oppositeSide = trk.getTermination(this);
    MyEdges[oppositeSide].setDestination(trk);
    trk.replaceEdge(this, (SecEdge) MyEdges[oppositeSide]);
    return false;
  }
  
  /**
   * is called to locate the Indication's feeder.  It is called during
   * initialization only.
   */
  public void locateFeeder() {
    for (int i = 0; i < MyEdges.length; ++i) {
      if (MyEdges[i] != null) {
        MyEdges[i].locateFeeder();
      }
    }        
  }
  
  /**
   * sets the state of one of the tracks that could terminate on the SecEdge
   * @param tEnd is the track identifier (edge ot the other end of the track)
   * @param state is one of the values from above
   */
  public void setTrackEnd(int tEnd, int state) {
      TrackEnd[tEnd] = state;
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
   * registers a DoubleXEdgeFactory with the XMLReader.
   */
  static public void init() {
    XMLReader.registerFactory(XML_TAG, new DoubleXEdgeFactory());
  }

  /*****************************************************************************/
  /**
   * The interface that the SecEdge and BlkEdge forms of the composite edge
   * must adhere to
   */
  private interface XEdgeInterface {
    /**
     * aligns the SecEdge for the calling Track.  If the perpendicular
     * track has a reservation, then this edge should be set to not aligned.
     *
     * @param fouled is true if the cross track has a reservation or false
     * if it does not.
     */
    public void setFouled(boolean fouled);
    
    /**
    * Is a method in SecEdge
    * 
    * @param block is the Block identity being propagated.
    */
    public void setBlock(Block block);
    
    /**
     * sets the Section, for upward references
     * 
     * @param sec is the Section containing the Edge.
     */
    public void setSection(Section sec);
    
    /**
     * binds half of a double crossover to the other half.
     * @param edge is the other half.
     */
    public void bindTo(XEdgeInterface edge);
    
    /**
     * comes from SecEdge
     *
     * @param trk is the SecEdge at the other end.
     *
     * @return false because there is only one route through the edge.
     * Returning true will be misleading because it implies that the
     * edge can be switched.
     */
    public boolean setDestination(Track trk);
    
    /**
     * comes from SecEdge for linking the signals together
     */
    public void locateFeeder();

  }
  
  /*****************************************************************************/
  
  /**
   * a composing BlkEdge.  It has two virtual signals - the usual one that is
   * the gate for entering the block and a second that is the gate for crossing
   * the perpendicular track.  The latter is usually open (allowing a train to
   * cross into the next SecEdge), unless the perpendicular track is busy.
   */
  private class XBlkEdge extends BlkEdge implements XEdgeInterface {

    /**
     * the signal to prevent a train from leaving the Section.
     */
    private Signal ExitSignal = new Signal();
    
    /**
     * the indication on that Signal.
     */
    private Indication ExitIndication = new Indication(Track.NORMAL, Block.TRK_RESERVED);

    public XBlkEdge(int edge, Edge shared, Block block, Signal blkSignal) {
      super(edge, shared, block, blkSignal);
      ExitSignal.HomeSignal = false;
    }
    
    /**
     * aligns the SecEdge for the calling Track.  If the perpendicular
     * track has a reservation, then this edge should be set to not aligned.
     *
     * @param fouled is true if the cross track has a reservation or false
     * if it does not.
     */
    public void setFouled(boolean fouled) {
      Destination.alignTrkEnd(this, !fouled);
      ExitIndication.setCondition((fouled) ? Block.TRK_FOULED : Block.TRK_RESERVED);
      ExitSignal.trackState(ExitIndication);
      
      // Should this XBlkEdge also tell its peer?
    }

    public void setSection(Section sec) {
      MySection = sec;
    }
    
    /**
     * Sets a reservation from the BlKEdge protected by a signal to
     * the exit from the Block.
     *
     * @param reserve is the kind of reservation.
     * <ul>
     * <li>  NO_TRAFFIC_SET is used to remove the reservation.
     * <li>  SINGLE_MOVE is for one train to cross the Block.
     * <li>  DTC_RESERVATION is for holding the Block after the train
     * has crossed it.
     * </ul>
     * @return the BlkEdge on the next Block, which could be null.
     */
//    public BlkEdge reserveBlock(int reserve) {
//      BlkEdge nextBlock = super.reserveBlock(reserve);
//      foulXing(reserve != Track.NO_TRAFFIC_SET, this);
//      return nextBlock;
//    }

    /**
     * receives the state of the Track.
     * For a BlkEdge, it must account for the state of the Block
     * and direction of travel of a Train.
     *
     * @param state is the Track's state.
     *
     * @see cats.layout.items.Track
     * @see cats.layout.items.Indication
     */
    public void setState(int state) {
      super.setState(state);
      foulXing(TrkBlkState != Block.TRK_IDLE, this);
    }

    /**
     * binds half of a double crossover to the other half.
     * @param edge is the other half.
     */
    public void bindTo(XEdgeInterface edge) {
      if (Joint == null) {
        Joint = (XBlkEdge) edge;
        edge.bindTo(this);
      }
    }
    
    /**
     * is called from a SecEdge across the Section, looking for the next signal
     * in the direction of travel.  This SecEdge inserts the ExitSignal and
     * continues the probe using it.
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
      Signal feeder = super.neighborProbe(rte, ExitIndication);
      if (feeder != null) {
        feeder.setPredecesor(ExitSignal);
      }
      holder.setProtectedSpeed(Track.getSlower(ExitIndication.getProtSpeed(),
          holder.getProtSpeed()));
      return ExitSignal;
    } 
  }
  /*****************************************************************************/
  /**
   * a composing SecEdge.  It has one virtual signal - the usual one that is
   * the gate for crossing the perpendicular track.  It is usually open (allowing a
   * train to cross into the next SecEdge), unless the perpendicular track is busy.
   */
  private class XSecEdge extends SecEdge implements XEdgeInterface {

    /**
     * the signal to prevent a train from leaving the Section.
     */
    private Signal ExitSignal = new Signal();
    
    /**
     * the indication on that Signal.
     */
    private Indication ExitIndication = new Indication(Track.NORMAL, Block.TRK_RESERVED);

    public XSecEdge(int edge, Edge shared) {
      super(edge, shared);
      ExitSignal.HomeSignal = false;
    }
    
    /**
     * aligns the SecEdge for the calling Track.  If the perpendicular
     * track has a reservation, then this edge should be set to not aligned.
     *
     * @param fouled is true if the cross track has a reservation or false
     * if it does not.
     */
    public void setFouled(boolean fouled) {
      Destination.alignTrkEnd(this, !fouled);
      ExitIndication.setCondition((fouled) ? Block.TRK_FOULED : Block.TRK_RESERVED);
      ExitSignal.trackState(ExitIndication);
    }

    public void setSection(Section sec) {
      MySection = sec;
    }
    
    /*
     * receives the state of the Track.
     * For a BlkEdge, it must account for the state of the Block
     * and direction of travel of a Train.
     *
     * @param state is the Track's state.
     *
     * @see cats.layout.items.Track
     * @see cats.layout.items.Indication
     */
    public void setState(int state) {
      super.setState(state);
      foulXing(TrkBlkState != Block.TRK_IDLE, this);
    }

    /**
     * binds half of a double crossover to the other half.
     * @param edge is the other half.
     */
    public void bindTo(XEdgeInterface edge) {
      if (Joint == null) {
        Joint = (XSecEdge) edge;
        edge.bindTo(this);
      }
    }
    
    /**
     * is called from a SecEdge across the Section, looking for the next signal
     * in the direction of travel.  This SecEdge inserts the ExitSignal and
     * continues the probe using it.
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
      Signal feeder = super.neighborProbe(rte, ExitIndication);
      if (feeder != null) {
        feeder.setPredecesor(ExitSignal);
      }
      holder.setProtectedSpeed(Track.getSlower(ExitIndication.getProtSpeed(),
          holder.getProtSpeed()));
      return ExitSignal;
    } 
  }
  
  static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(
  XEdge.class.getName());

}

/**
 * is a Class known only to the XEdge class for creating DoubleXEdges
 * from an XML document.
 */
class DoubleXEdgeFactory
implements XMLEleFactory {
  
  /**
   * is the XEdge created from the XML file
   */
  private XEdge newEdge;
  
  /**
   * tells the factory that an XMLEleObject is to be created.  Thus,
   * its contents can be set from the information in an XML Element
   * description.
   */
  public void newElement() {
    newEdge = new XEdge(-1, null);
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
    int trk;
    int state;
    if ((trk = Sides.findSide(tag)) == Prop.NOT_FOUND) {
      resultMsg = "Unknown XEdge track identifier: " + tag;
    }
    else if ((state = Prop.findString(value, XEdge.TRACKSTATE)) == Prop.NOT_FOUND){
      resultMsg = "Unknown condition for XEdge track: " + value;
    }
    else {
      newEdge.setTrackEnd(trk, state);
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
    return newEdge;
  }
}
/* @(#)XEdge.java */