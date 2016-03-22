/* Name: Signal.java
 *
 * What:
 *   This class is the container for all the information about a generic
 *   Signal.
 */

package cats.layout.items;

/**
 * is a container for information about a generic Signal.
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */
public class Signal {
  
  /**
   * the following enumerate the kinds of Signals.
   */
  /**
   * a virtual signal.  It has not physical or panel signal.
   * It's purpose is to feed status to a real signal.
   */
  protected static final int VIRTUAL_SIGNAL = 0;
  
  /**
   * an intermediate.  It is on the layout, but not the panel.
   */
  protected static final int INTERMEDIATE = 1;
  
  /**
   * a control point.  It has is on the panel.  It should (but
   * need not be) on the panel.
   */
  protected static final int CONTROL_POINT = 2;

  /**
   * is the previous Signal in the line of traffic, encountered
   * before this one.  Its Indication is based on this one's Indication.
   */
  protected Signal PrevSignal;

  /**
   * is the Indication due to the Track's state.
   */
  protected Indication TrackState;

  /**
   * is the Indication due to the next Signal's state.
   */
  protected Indication NextIndication;

  /**
   * is the Indication of this Signal.
   */
  protected Indication MyIndication;

  /**
   * is the previous Indication of this Signal.
   */
  protected Indication PreviousIndication;

  /**
   * is true if the next Signal is a SecSignal (real Signal) or false
   * if it is a virtual Signal.  This information is needed to know whether
   * to incorporate the next Signal's indication into this one or to use
   * it for determining the Approach attributes.
   */
  protected boolean NextIsReal;

  /**
   * is true if the Signal protects an interlocking plant.
   */
  protected boolean HomeSignal;
  
  /**
   * is the kind of signal.
   */
  protected int SignalType;
 
  /**
   * is the BlkEdge the Signal is associated with
   */
  protected BlkEdge ProtectedEdge;
  
  /**
   * the constructor.
   */
  public Signal() {
    MyIndication = new Indication();
    PreviousIndication = new Indication(MyIndication);
    TrackState = new Indication();
    NextIndication = new Indication(Track.NORMAL, Block.TRK_RESERVED - 1);
    NextIndication.setNextSpeed(Track.STOP);
    NextIsReal = false;
    HomeSignal = false;
    SignalType = VIRTUAL_SIGNAL;
  }

  /**
   * associates a Block Edge with a signal
   * @param edge is the edge the signal is protecting
   */
  public void registerEdge(BlkEdge edge) {
    ProtectedEdge = edge;
  }
  
  /**
   * propagates the Home Signal attribute to the predecessor
   * Signal.
   */
  public void setHS() {
    if (PrevSignal != null) {
      PrevSignal.setHS();
    }
  }
  
  /**
   * is called to determine if the Signal protects an interlocking
   * plant.  If it does, then reservations propagate no further.
   * 
   * @return true if the signal protects complex tracks.
   */  
  public boolean isHS() {
    return HomeSignal;
  }

  /**
   * is called to determine if the signal is a placeholder or has
   * a presence on the layout.
   *
   * @return true if the signal exists on the panel or layout.
   */
  public boolean isRealSignal() {
    return SignalType != VIRTUAL_SIGNAL;
  }

  /**
   * is called to determine if the signal is an intermediate.  An
   * intermediate is a signal on the layout, but not the panel.
   * 
   * @return true if it is an intermediate.
   */
  public boolean isIntermediate() {
    return SignalType == INTERMEDIATE;
  }
  
  /**
   * is called to determine if the signal is a control point.
   * A control point is a signal on the panel.
   * 
   * @return true if it is an intermediate.
   */
  public boolean isCP() {
    return SignalType == CONTROL_POINT;
  }
  /**
   * adjusts the SignalType based on the discipline of the block(s)
   * being protected.
   * 
   * @param discipline is the signal discipline of the Block
   * being protected.
   */
  public void setBlockDiscipline(int discipline) {
  }

 
  /**
   * tells the Indication where to feed its state.  The preceding
   * Indication may be dynamic, if it is on the other side of switch
   * points.
   *
   * @param pred is the Indication before this one in the direction of
   * travel.
   */
  public void setPredecesor(Signal pred) {
    PrevSignal = pred;
    if (pred != null) {
      pred.NextIsReal = false;
      pred.nextSignal(MyIndication);
      if (HomeSignal) {
        PrevSignal.setHS();
      }
    }
  }

  /**
   * sends the Signal's Indication to the Signal that it feeds.
   * It starts with an Indication based on what the SecEdge's state.
   * It merges in the indication from the next signal the train will
   * see after this one and computes the merged indication.  If the new
   * indication is different from the old, the new is remembered and
   * passed to the signal the train would see before this one.
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
    if ( !PreviousIndication.equals(MyIndication)) {
      PreviousIndication.copy(MyIndication);
      if (PrevSignal != null) {
        PrevSignal.nextSignal(MyIndication);
      }
    }
  }

  /**
   * receives the Indication for the state of the Block/Track/Edge.
   *
   * @param state is the Indication, from the Edges's perspective.
   *
   * @see cats.layout.items.Block
   * @see cats.layout.items.Indication
   */
  public void trackState(Indication state) {
    TrackState = state;
    tumbleDown();
  }

  /**
   * receives the Indication of the next Signal, which is virtual, in the
   * direction of travel.
   *
   * @param next is the next Signal's Indication.
   *
   * @see cats.layout.items.Indication
   */
  protected void nextSignal(Indication next) {
    NextIndication.copy(next);
    tumbleDown();
  }

  /**
   * is used to determine if the signal is showing a Halt indication.
   *
   * @return true if it is Stop.
   */
//  public boolean isShowingHalt() {
//    return MyIndication.getBlockStatus();
//  }

  /**
   * is used to determine if the signal is showing a Halt indication
   * because the track is not aligned.
   * 
   * @return true if the Signal is Halt due to alignment.
   */
  public boolean isShowingFouled() {
	  return MyIndication.isFouled();
  }

  /**
   * tells a Signal if it should be on due to approach lighting
   * or not.
   * 
   * @param lit is true for on and false for off.
   */
  public void setApproachState(boolean lit) {
  }
  
  /**
   * clears the Signal's history.  For a virtual Signal, there is none.
   */
  public void clrSignalHistory() {
  }
}
/* @(#)Signal.java */