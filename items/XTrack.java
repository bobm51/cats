/* Name: XTrack.java
 *
 * What:
 *   This class is the container for all the information about a crossing
 *   Track.  A crossing track is derived from a Track.  What makes it different
 *   from a Track is that it knows about the perpendicular Track and can
 *   tell it when its state changes.
 */

package cats.layout.items;

/**
 *   This class is the container for all the information about a crossing
 *   Track.  A crossing track is derived from a Track.  What makes it different
 *   from a Track is that it knows about the perpendicular Track and can
 *   tell it when its state changes.
 *   <p>
 *   These tracks are not included in single slips and double slips because
 *   the crossing tracks are in the same block and the block takes care of
 *   interlocking.
 *   <p>
 *   A crossing track is modeled as a Track with Points.  There is an internal
 *   signal on either side of the crossing - one for each direction of travel.
 *   These signals are inserted in both "wires" that connect the signals that
 *   span the tracks.  They interject the the "fouling" event when the cross
 *   track is in use.  Unlike other signals, these are gates to prevent crossing
 *   the perpendicular track, rather than entry into the trackon the other side.
 *   When the perpendicular track is busy, this track looks like switch points
 *   that are fouled.  This means 2 operations happen:
 *   <ul>
 *   <li> the signals are set to indicate fouling.  Since these are virtual
 *   signals, they feed the fouling state to the "previous" signal encountered
 *   by a train.
 *   <li> the Track is marked as not aligner.  This prevents a reservation from
 *   being made through the Track.
 *   </ul>
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2008, 2009</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */
public class XTrack extends Track {

  /**
   * is the perpendicular XTrack
   */
  private XTrack XTrack;
  
  /**
   * are the signals protecting the crossing.
   */
  private Signal[] Xsignal = {
      new Signal(),
      new Signal()
  };

  /**
   *   are the track stimuli into each Signal's Indication
   */
  private Indication[] Xindication = {
      new Indication(),
      new Indication()
  };

  /**
   * is a flag indicating that the block containing the track uses the same
   * occupancy detector as the crossing track.
   */
  private boolean SharedDetector;
  
  /**
   * constructs the XTrack from scratch.
   *
   * @param speed is the timetable track speed.
   */
  public XTrack(int speed) {
    super(speed);
    for (int s = 0; s < Xsignal.length; ++s) {
      Xsignal[s].HomeSignal = true;
      Xindication[s] = new Indication(speed, Block.TRK_RESERVED);
    }
    SharedDetector = false;
  }

  /**
   * tells a XTrack who the perpendicular Track is.
   * 
   * @param t is the perpendicular Track.
   */
  public void setCrossTrack(XTrack t) {
    XTrack = t;
  }

  /**  
   * copies the members of a Track to this object.  It 
   * is intended that this function be called on a Track that
   * has not been completely initialized because copying Objects
   * might not yield the desired result.
   * 
   * @param t is the Track whose contents are being copied.
   */
  public void copy(Track t) {
    TrkSpeed = t.TrkSpeed;
    TrafficSet = t.TrafficSet;
    TrkState = t.TrkState;
    TrackString = t.TrackString;
    TrackType = t.TrackType;
  }
  
  /**
   * overrides the base class method to intercept the cross traffic
   * stimuli.  If the perpendicular track is busy, then the busy
   * overrides all stimuli.
   * 
   * If the cross tracks share an occupancy detector, then some special
   * considerations are made.  If they share a detector and the dispatcher
   * has set a route through the other track, this track should not affect
   * the other track.
   */
  protected void mergeTrkStates() {
    boolean blockXtraffic = false;
    if ((TrafficSet != NO_TRAFFIC_SET) || (TrkState != Block.TRK_IDLE)) {
      blockXtraffic = true;
    }
    if (!SharedDetector || Aligned[0] || Aligned[1]) {
      XTrack.setCrossState(blockXtraffic);
    }
    super.mergeTrkStates();
  }

  /**
   * is used to receive the state of the cross track.  When the
   * busy condition on the perpendicular track is cleared, the
   * condition of this track needs to be updated.
   * @param traffic is false if it is idle (a train can pass thru
   * from this track) and true if the cross track is in use or
   * reserved for use.
   */
  private void setCrossState(boolean traffic) {
    setBlocking(traffic ? Block.TRK_FOULED : Block.TRK_RESERVED);
    if (!traffic) {
      super.mergeTrkStates();
    }
  }
  
  /*
   * searches for the signal on the other end of the wire from a
   * SecEdge.  The SignalWire is an Xtrack, so the signal is the one
   * protecting the crossing on the side of the caller.  Furthermore,
   * the signal must locate its feeder by initiating a probe from the
   * other side of the crossing.
   * <p>
   * This method also determines if the crossing track shares the occupancy
   * detector.  This function is unrelated, but it is done here because
   * the discovery needs to be performed after all tracks have been assigned
   * blocks.
   *
   * @param caller is the end originating the probe.
   * @param holder is the Indication where the slowest speed is kept.
   *
   * @return the next Signal encountered in the direction of the probe.
   * It could be null.
   *
   * @see cats.layout.items.SecEdge
   * @see cats.layout.items.Signal
   */
  public Signal findSignal(SecEdge caller, Indication holder) {
    int edge;
    if (TrackEnds[0] == caller) {
      edge = 0;
    }
    else if (TrackEnds[1] == caller) {
      edge = 1;
    }
    else {
        return null;
    }
    Signal feeder = getDestination(caller).neighborProbe(this,
        Xindication[edge]);
    if (feeder != null) {
      feeder.setPredecesor(Xsignal[edge]);
    }
    holder.setProtectedSpeed(Track.getSlower(Xindication[edge].getProtSpeed(),
                                             holder.getProtSpeed()));
    
    // Discover if the tracks share a common oocupancy detector
    if ((TrackBlock != null) && (TrackBlock.getDetectorName() != null)) {
      if ((XTrack != null) && (XTrack.TrackBlock != null) &&
          (XTrack.TrackBlock.getDetectorName() != null)) {
        SharedDetector = TrackBlock.getDetectorName().equals(XTrack.TrackBlock.getDetectorName());
      }
    }
    
    // back to our regularly scheduled program
    return Xsignal[edge];
  }

  /*
   * adds or removes the traffic condition.
   *
   * @param trk is the Track holding the reservation.
   *
   * @param traffic describes the traffic direction.
   */
  public void setTraffic(int traffic) {
    int condition = (traffic != Track.NO_TRAFFIC_SET) ? Block.TRK_FOULED : Block.TRK_RESERVED;
    XTrack.setBlocking(condition);
    super.setTraffic(traffic);
  }

  /**
   * sets the Signals on the crossing route to blocked or clears
   * the blocking condition.
   *
   * @param blocked is the condition to apply to the Indication.
   */
  private void setBlocking(int blocked) {
    Xindication[0].setCondition(blocked);
    Xindication[1].setCondition(blocked);
    Xsignal[0].trackState(Xindication[0]);
    Xsignal[1].trackState(Xindication[1]);
    setFoul(blocked == Block.TRK_FOULED);
  }
  
  /**
   * fouls or unfouls a straight track
   * @param foul is true to mark the track as fouled and false to mark it
   * as not fouled
   */
  public void setFoul(boolean foul) {
    Aligned[0] = !foul;
    Aligned[1] = !foul;
  }
}
/* @(#)XTrack.java */