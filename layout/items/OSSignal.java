/* Name: OSSignal.java
 *
 * What:
 *   This class defines an invisible Signal at the Points of an OS section.
 */

package cats.layout.items;

/**
 * defines an invisible Signal, at the Points of an OS Section.  Its duties
 * includes affecting the Indication of the protecting Signals at the Block
 * boundaries, due to the alignment of the Switch Points.  It is always
 * virtual.  It is unique in that it protects against exit
 * from a Block, rather than entry into a Block; thus, faces
 * the "wrong" way.
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */
public class OSSignal
    extends Signal {

  /**
   * is the OSEdge that owns the OSSignal.
   */
  private PtsEdge MyEdge;

  /** is the constructor.
   *
   * @param edge is the owning OSEdge.
   */
  public OSSignal(PtsEdge edge) {
    MyEdge = edge;
  }

  /**
   * tells the Indication where to feed its state.  Because the "next"
   * Signal is dynamic due to the alignment of the Points, the Signal
   * protecting the points substitutes for receiving forwarding request.
   * When it receives it. it tells the owning OSEdge.
   *
   * @param pred is the Indication before this one in the direction of
   * travel.
   */
  public void setPredecesor(Signal pred) {
    MyEdge.setPtsSignal(pred);
    pred.setHS();
  }

  /**
   * does much of what setPredecesor does, but is a different name,
   * as explained in the prolog to the overridden method.
   *
   * @param pred is the Signal before this one in the direction of
   * travel.
   */
  public void setRelay(Signal pred) {
    if (pred != null) {
      super.setPredecesor(pred);
//      pred.nextSignal(MyIndication);
    }
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
   * receives the Indication for the Block/Track/Edge.
   *
   * @param state is the Block's state.
   *
   * @see cats.layout.items.Block
   */
  public void trackState(Indication state) {
    TrackState.copy(state);
//    tumbleDown();
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