/* Name: VerifyEvent.java
 *
 * What:
 *   This class is a command (see the Command design pattern) to SwitchPoints
 *   to verify that the points can be moved to a track.  The request is queued and
 *   not executed inline because it could be executed in the listener call stack of
 *   an AbstractTurnout, which causes problems if the action is to countermand the
 *   Turnout changing state.
 */
package cats.rr_events;

import cats.layout.items.SwitchPoints;

/**
 *   This class is a command (see the Command design pattern) to SwitchPoints
 *   to verify that the points can be moved to a track.  The request is queued and
 *   not executed inline because it could be executed in the listener call stack of
 *   an AbstractTurnout, which causes problems if the action is to countermand the
 *   Turnout changing state.
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */
public class VerifyEvent extends RREvent {

  /**
   * are the SwitchPoints that receive the request.
   */
  private SwitchPoints Who;
  
  /**
   * is the track
   */
  private int Track;
  
  /**
   * is true when the points move into place and false when
   * the points move out of place.
   */
  private boolean Operation;
  
  /**
   * the ctor.
   * @param who is the SwitchPoints being verified
   * @param track is the track end requesting verification
   * @param operation is true when the points move into place and false
   * when the points move out of place.
   */
  public VerifyEvent(SwitchPoints who, int track, boolean operation) {
    Who = who;
    Track = track;
    Operation = operation;
  }
  
  /**
   * Performs the command encapsulated by this object.
   */
  public void doIt() {
    if (Operation) {
      Who.verifyMovement(Track);
    }
    else {
      Who.verifyNoMovement(Track);
    }
  }
}
/* @(#)VerifyEvent.java */