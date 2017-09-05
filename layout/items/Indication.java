/* Name: Indication.java
 *
 * What: This file contains the class definition for a Signal's Indication -
 *       its state.
 *
 * Special Considerations:
 *   A Signal has two major pieces: the Indication and the Aspect.
 *   The indication is the information the Signal is conveying.
 *   The aspect is the presentation of that information.  This
 *   class is concerned with the Indication.  The Aspect is performed
 *   by the driver for the Signal.
 */

package cats.layout.items;

import cats.layout.AspectMap;

/**
 * is a container for the state of a signal - its Indication.
 * <p>
 * An Indication is affected by the following composite heirarchy:
 * <ol>
 * <li> a Block has one or more Tracks
 * <li> a Track has exactly two ends, which terminate on Section Edges and
 * need not form part of the Block boundary.  Interior ends simply connect
 * Tracks together.
 * <li> ends which form the block boundaries and ends which are switchpoints
 * have Signals (the latter dynamically route the propagation of Indications).
 * <li> Signals may be visible (real) or virtual (placeholders in the
 * propagation of Indications through a Block).
 * </ol>
 * Thus, an Indication is an object being decorated by the Decorator pattern -
 * each object in the above heirarchy may duplicate the Indication and add
 * additional information to the duplicate, passing the Indication to
 * the next level down in the heirarchy.  The use of the Decorator pattern
 * allows each Signal protecting a Block to show Block wide status, yet
 * also show unique status.  For example, a Block may be unoccupied,
 * but the Block boundary ends associated with Tracks that are not aligned
 * through turnouts to another Block boundary end must have a red Indication,
 * while the two Signals protecting the route through the Block could show
 * Clear.
 * <p>
 * Signals are not the only users of the information in Indications, but objects
 * in the intermediate levels may allow or disallow actions based on the
 * Indication they have.  For example, if a Block is occupied, then the
 * switch points edge will not allow the points to move.
 * The following table summarizes what each object in heirarchy contributes
 * to the Indication and what each level uses in the Indication.
 * <table border>
 * <tr>
 *     <th>Block</th>
 *     <th>Track</th>
 *     <th>BlkEdge</th>
 *     <th>CPEdge</th>
 *     <th>Signal</th>
 * </tr>
 * <tr>
 *     <td>occupancy</td>
 *     <td>speed</td>
 *     <td>reservation</td>
 *     <td>fleeting</td>
 *     <td>next signal</td>
 * </tr>
 * <tr>
 *     <td>track and time</td>
 *     <td> </td>
 *     <td> </td>
 *     <td> </td>
 *     <td> </td>
 * </tr>
 * <tr>
 *     <td>out of service</td>
 *     <td> </td>
 *     <td> </td>
 *     <td> </td>
 *     <td> </td>
 * </tr>
 * <tr>
 *     <td>entry</td>
 *     <td> </td>
 *     <td> </td>
 *     <td> </td>
 *     <td> </td>
 * </tr>
 * </table>
 * <p>
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */
public class Indication {

  /**
   * is the number of warning signals to the stop signal.
   */
  private int WarnDepth;

  /**
   * is the speed of the block(s) being protected.
   */
  private int ProtSpeed;

  /**
   * is the speed of the block(s) protected by the next signal
   */
  private int NextSpeed;

  /**
   * is the state appearing at the edge being protected
   */
  private int Composite;

  /**
   * tells the PanelSignal if the signal should be colored or not.
   */
  private boolean PanelOn;

  /**
   * tells the AspectMap if the next speed should be replaced
   * with Advanced or not.
   */
  private boolean Advanced;
  
  /**
   * the no-argument constructor.
   */
  public Indication() {
    this(Track.NORMAL, Block.TRK_IDLE);
  }

  /**
   * creates an Indication based on a speed.
   *
   * @param speed is the speed allowed through the Block.  This is normally
   * TRK_SPEED, indicating the dispatcher has not requested a speed reduction.
   *
   * @param state is the initial state of the Edge being protected.
   */
  public Indication(int speed, int state) {
    WarnDepth = 0;
    ProtSpeed = speed;
    NextSpeed = Track.NORMAL;
    Composite = state;
    Advanced = false;
  }

  /**
   * creates an Indication based on another.
   *
   * @param master is the master copy being duplicated.
   */
  public Indication(Indication master) {
    WarnDepth = master.WarnDepth;
    ProtSpeed = master.ProtSpeed;
    NextSpeed = master.NextSpeed;
    Composite = master.Composite;
    PanelOn = master.PanelOn;
    Advanced = master.Advanced;
  }

  /**
   * returns the Indication's current value.  TRK_PROTECTED has different
   * meanings, depending upon the signaling discipline.  If it is CTC and set,
   * then signals should go to STOP.  If it is anything else, then it will
   * not force a STOP indication.  If it is not set, then no traffic direction
   * has been set, so it doesn't prevent turnouts from being changed.  If
   * it is set, then there is a direction, so turnouts should not be changed.
   * <p>
   * To handle these different conditions, a copy is made of Blocker and all
   * testing is done against the copy.  TRK_PROTECTED is cleared in the
   * copy, if the discipline is not CTC.
   *
   * @return the current state.
   */
  public int getIndication() {
    int result;

    if (Composite == Block.TRK_RESERVED) {
      result = AspectMap.getRule(ProtSpeed, NextSpeed);
    }
    else if (Composite == Block.TRK_AND_TIME) {
      // Track and Time should set signals to stop.
      result = AspectMap.findRule("R292");
    }
    else {
      result = AspectMap.findRule("R292");
    }
    return result;
  }

  /**
   * if the Advanced flag is set, this method converts the indication
   * to an equivalent Advanced indication.
   * @return the Indication adjusted for the next signal showing Approach
   */
  public int getAdvancedIndication() {
    if (Advanced && (Composite == Block.TRK_RESERVED) &&
        (NextSpeed != Track.STOP)) {
      return AspectMap.getRule(ProtSpeed, Track.APPROACH);
    }
    return getIndication();
  }
  
  /**
   * checks if the Indication is showing Track and Time.
   *
   * @return true if the Indication is showing Track and Time.
   */
  public boolean isTrackandTime() {
    return Composite == Block.TRK_AND_TIME;
  }

  /**
   * checks if the Indication permits traffic to be set.
   *
   * @return true if traffic can be set; false if traffic cannot be set.
   */
  public boolean isTraffic() {
    return ( (Composite == Block.TRK_RESERVED) ||
            ( (Composite == Block.TRK_IDLE) && !PanelOn));
  }

  /**
   * checks if the Indication is stop because of fouled
   * track.
   * 
   * @return true if the indication is TRK_FOULED.
   */
  public boolean isFouled() {
	  return Composite == Block.TRK_FOULED;
  }

  /**
   * checks if the Indication is showing an approach of some form.
   * This method is called for setting the color of an icon.
   * 
   * @return true if it is and false if it is not.
   */
  public boolean showingApproach() {
    return ((Composite == Block.TRK_RESERVED) && (NextSpeed == Track.STOP));
  }

  /**
   * merges the conditions from the next Signal (which is virtual) into
   * the conditions for this Signal.
   *
   * @param next is the Indication being merged
   */
  public void mergeState(Indication next) {
    // The conditional handles stretches with a CTC signal on one end
    // and non-CTC on the other.  Track and Time takes precedence
    // over all other conditions because the dispatcher is giving the
    // crew permission to ignore the signals.
    if ((next.Composite == Block.TRK_AND_TIME) || (Composite == Block.TRK_AND_TIME)) {
      Composite = Block.TRK_AND_TIME;
    }
//    else if (next.Composite > Block.TRK_IDLE) {
//      Composite = Math.max(Composite, next.Composite);
//    }
    else {
      Composite = Math.max(Composite, next.Composite);
    }
    ProtSpeed = Track.getSlower(ProtSpeed, next.ProtSpeed);
    NextSpeed = next.NextSpeed;
    Advanced = next.Advanced;
  }

  /**
   * makes this Indication a copy of another.
   *
   * @param other is the Indication to copy.
   */
  public void copy(Indication other) {
    WarnDepth = other.WarnDepth;
    ProtSpeed = other.ProtSpeed;
    NextSpeed = other.NextSpeed;
    Composite = other.Composite;
    PanelOn = other.PanelOn;
    Advanced = other.Advanced;
  }

  /**
   * clears the warning buffer count.
   */
  public void clrCushion() {
    WarnDepth = 0;
  }

  /**
   * retrieves the warning cushion, number of yellows from the stop.
   *
   * @return the number of yellow signals to the Stop.
   */
  public int getCushion() {
    return WarnDepth;
  }

  /**
   * sets the warning cushion.
   *
   * @param cushion is the number of yellow signals to the Stop.
   */
  public void setCushion(int cushion) {
    WarnDepth = cushion;
  }

  /**
   * sets the speed of the block(s) being protected
   *
   * @param speed is the slowest speed of any track segment to the
   * next signal.
   */
  public void setProtectedSpeed(int speed) {
    ProtSpeed = speed;
  }

  /**
   * returns the speed of the block(s) being protected.
   *
   * @return the slowest speed of any track segment to the next signal.
   */
  public int getProtSpeed() {
    return (Composite == Block.TRK_RESERVED ? ProtSpeed : Track.STOP);
  }

  /**
   * sets the speed of the next block(s)
   *
   * @param speed is the slowest speed of any track segment in the block(s)
   * protected by the next signal.
   */
  public void setNextSpeed(int speed) {
    NextSpeed = speed;
  }
  
  /**
   * returns the speed of the next block(s) being protected.
   *
   * @return the slowest speed of any track segment to the next signal.
   */
  public int getNextSpeed() {
    return NextSpeed;
  }


  /**
   * sets the signal Stop conditions
   *
   * @param condition is the state of the Edge/Track/Block.
   */
  public void setCondition(int condition) {
    Composite = condition;
  }

  /**
   * sets the PanelOn flag (or clears it)
   *
   * @param onState is true if the panel icon should be colored the same
   * as the physical signal; false if it should be IDLE.
   */
  public void setPanelOn(boolean onState) {
    PanelOn = onState;
  }

  /**
   * returns the state of the PanelOn flag.
   *
   * @return true if the panel icon should be colored the same as
   * the physical signal; false, if it should be idle.
   */
  public boolean isPanelOn() {
    return PanelOn;
  }

  /**
   * sets the Advanced flag when the next signal (third block) is
   * showing Approach.
   * @param advancedState is true to set the flag or false to clear it.
   */
  public void setAdvanced(boolean advancedState) {
    Advanced = advancedState;
  }
  
  /**
   * returns the state of the Advanced flag.  
   * @return true when the next signal is showing Approach.
   */
  public boolean getAdvanced() {
    return Advanced;
  }
  
  /**
   * compares the contents of an Indication against this one's contents.
   *
   * To compare indications completely, I needed to include the
   * Advanced flag in the test.
   * 
   * @param other is the Indication being compared.  If other is null,
   * false is returned.
   *
   * @return true if they are the same and false if they are different.
   */
  public boolean equals(Indication other) {
    int mySpeed = (Composite == Block.TRK_RESERVED ? ProtSpeed : Track.STOP);
    return ( (other != null) && (Composite == other.Composite) &&
            ( (other.Composite == Block.TRK_RESERVED ? other.ProtSpeed :
               Track.STOP) == mySpeed) &&
            (NextSpeed == other.NextSpeed) &&
            (Advanced == other.Advanced));
  }
}
/* @(#)Indication.java */