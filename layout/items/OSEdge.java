/* Name: OSEdge.java
 *
 * What:
 *   This class is the container for all the information about an OS
 *   Section Edge - a specialization of PtsEdge that implemants a
 *   set of SwitchPoints, under dispatcher control.
 */

package cats.layout.items;

/**
 * "is a" SecEdge which represents switch points - an edge on which
 * multiple tracks (routes) terminate - under dispatcher control.
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2004, 2009, 2010</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */
public class OSEdge
    extends PtsEdge {

  /**
   * constructs a SecEdge with only its Edge identifier.
   *
   * @param edge identifies the side of the GridTile the SecEdge is on.
   * @param shared describes the shared edge, if it is not adjacent.
   *    A null value means it is adjacent.
   * @param points are describes the routes which terminate on the PtsEdge.
   */
  public OSEdge(int edge, Edge shared, SwitchPoints points) {
    super(edge, shared, points);
  }

  /**
   * used by the dispatcher to request that the points be moved.  Since the
   * dispatcher controls OS points, this moves the points.
   *
   * @param trk identifies the requested aligned Track.
   */
  public void setAlignment(int trk) {
    movePoints(trk);
  }

  /*
   * receives the state of the Track, the same as the state of the Block.
   * For an OSEdge, it must account for the state of the Block
   * direction of travel of a Train and for a diverging route.
   *
   * @param state is the Track's state.
   *
   * @see cats.layout.items.Track
   * @see cats.layout.items.Indication
   */
  public void setState(int state) {
    /**
     * it seems like this operation could be done in Block.
     * @todo check this out sometime
     */
    if ((state == Block.TRK_IDLE) && (MyBlock.getDiscipline() != Block.CTC) &&
        (MyBlock.getDiscipline() != Block.DTC)){
      state = Block.TRK_RESERVED;
    }
    adjustFasciaLights(state);
    refreshState();
  }

  /**
   * locks or unlocks the turnout decoders associated with the SecEdge.
   * This does nothing for any edge but one with SwitchPoints.
   * @param lock is true to lock the decoder commands and false to
   * unlock them.
   */
  public void setEdgeLock(boolean lock) {
    MyPoints.setRouteLock(CurrentTrk, lock);
  }
  
  /**
   * tests if a decoder command for moving switch points on the SecEdge
   * have been locked.  Edges without points are always unlocked.
   * 
   * @param newTrk is the track being requested
   * @return true if the commands are locked and false is not.
   */
  public boolean isEdgeLocked(int newTrk) {
    return MyPoints.isRouteLocked(newTrk);
  }

  static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(
      OSEdge.class.getName());
}
/* @(#)OSEdge.java */