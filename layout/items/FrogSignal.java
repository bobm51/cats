/* Name: FrogSignal.java
 *
 * What:
 *   This class defines an invisible Signal for each of the routes through
 *   the frog end of an OS security plant.
 */

package cats.layout.items;

/**
 * defines an invisible Signal, for the route through the frog end of a
 * turnout; thus, there is one of these for each of the tracks exiting
 * the turnout.  What makes these Signals different is that the PrevSignal
 * field is dynamic.  If the points are not aligned for this track, then
 * there is no previous signal to forward the Indication to.  However, this
 * signal is bound to the next Signal upstream from this one, so that this
 * Signal is always up to date on the upstream conditions and ready to
 * set the OSEdge's Indication when points are aligned for it.  It has a
 * subtle role in connecting the Signals that protect entry into the
 * security plant.  When a probe is made from the frog direction, the
 * FrogSignal is returned, to aid in identifying the route of the probe.
 * Subsequently, when the setPredecesor() method is called, this Signal
 * tells the OSEdge.
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */
public class FrogSignal
extends Signal {
  
  /**
   * is the OSEdge that owns the FrogSignal.
   */
  private PtsEdge MyEdge;
  
  /**
   * is true if the FrogSignal is on the normal route.
   */
  private boolean NormalRoute = false;
  
  /** is the constructor.
   *
   * @param edge is the owning OSEdge.
   */
  public FrogSignal(PtsEdge edge) {
    MyEdge = edge;
  }
  
  /**
   * This method overrides the base method.  The prior signal is dynamic
   * because it is on the other side of the points.  If the points are
   * aligned for this route, then there may be a prior signal.  If the
   * points are not aligned, then there is no prior signal.  The OSEdge
   * will not pass the probe, so this signal will never receive the
   * base method.  However, the override is used to capture the
   * Signal that protects entry into the frog. When a Signal alerts
   * a FrogSignal to wanting Indications, the FrogSignal passes the
   * request to the OSEdge.
   *
   * @param pred is the Indication before this one in the direction of
   * travel.
   */
  public void setPredecesor(Signal pred) {
    MyEdge.setFrogSignal(pred, this);
    pred.setHS();
  }
  
  /**
   * does much of what setPredecesor does, but is a different name,
   * as explained in the prolog to the overridden method.
   *
   * @param pred is the Indication before this one in the direction of
   * travel.
   */
  public void setRelay(Signal pred) {
    super.setPredecesor(pred);
  }
  
  /**
   * simply accepts the initial previous Signal.
   *
   * @param pred is the Signal.
   */
  public void initialSignal(Signal pred) {
    super.setPredecesor(pred);
  }
  
  /**
   * tells the FrogSignal it is on the Normal route, so should not
   * adjust the Indication that it relays.
   */
  public void setNormal() {
    NormalRoute = true;
  }
  
  /**
   * receives the Indication for the Block/Track/Edge.
   *
   * @param state is the Block's state.
   *
   * @see cats.layout.items.Block
   */
  public void trackState(Indication state) {
    TrackState.copy(state);
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
    if (!NormalRoute) {
      NextIndication.copy(next);
      super.nextSignal(NextIndication);
    }
    else {
      super.nextSignal(next);
    }
  }
  
  /**
   * sends the Signal's Indication to the Signal that it feeds.
   */
  protected void tumbleDown() {
    MyIndication.copy(TrackState);
    if (NextIsReal) {
      MyIndication.setNextSpeed(NextIndication.getProtSpeed());
      MyIndication.setAdvanced(NextIndication.getNextSpeed() == Track.STOP);
    }
    else {
      MyIndication.mergeState(NextIndication);
    }
    PreviousIndication.copy(MyIndication);
    if (PrevSignal != null) {
      PrevSignal.nextSignal(MyIndication);
    }
  }
}
/* @(#)FrogSignal.java */