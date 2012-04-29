/* Name: PtsEdge.java
 *
 * What:
 *   This class is the container for all the information about one
 *   of a Section's Edges which has multiple routes, corresponding
 *   to switch points.
 */

package cats.layout.items;

import cats.common.Sides;
import cats.gui.GridTile;
import cats.gui.LocalEnforcement;
import cats.layout.items.Block;
import cats.layout.items.IOSpec;
import cats.layout.DecoderObserver;
import cats.rr_events.VerifyEvent;

import java.util.Enumeration;
import java.util.Vector;

/**
 * "is a" SecEdge which represents switch points - an edge on which
 * multiple tracks (routes) terminate.
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2004, 2009, 2010, 2011</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */
public class PtsEdge
    extends SecEdge {

  /**
   * is a list of all PtsEdges on the layout.
   */
  private static Vector<PtsEdge> PtsKeeper = new Vector<PtsEdge>();

  /**
   * defines the number of SecEdges at the other end of routes that
   * terminate on this SecEdge.
   */
  public static final int MAX_ROUTES = Sides.EDGENAME.length;
  
  /**
   * the Signal protecting the Points from all directions.
   */
  private OSSignal MySignal;

  /**
   * the signal that precedes the points.
   */
  private Signal PredSignal;

  /**
   * the Signals protecting entry into the security plant from the frog
   * end.
   */
  private Signal FrogSignals[];

  /**
   * the Signals which receive down stream Indications, from the next Signal
   * in the direction of travel through the frog.  One of these is selected
   * for determining the signal on the approach to the Points.
   */
  private FrogSignal Feeders[];

  /**
   * the speeds of each route through the frog.
   */
  private int FrogSpeeds[] = new int[MAX_ROUTES];

  /**
   * the state of the Block containing the OSEdge.
   */
  protected Indication OSIndication;

  /**
   * describes the routes through the points, how to sense the turnout
   * position, how to throw the turnout, and how to set the lock light.
   */
  protected SwitchPoints MyPoints;

  /**
   * the Tracks terminating on this edge.  There can be a maximum of three
   * tracks (one to each of the other edges), but the array contains four
   * because each track is identified by the edge the other end terminates on.
   */
  protected Track[] MyTracks = new Track[MAX_ROUTES];

  /**
   * are the SignalWires for each route.
   */
  protected SignalWire[] MyWires = new SignalWire[MAX_ROUTES];

  /**
   * the index of the currently selected track.  If it is the same as
   * MyEdge, then the selection is not known.
   */
  protected int CurrentTrk;

  /**
   * is a flag indicating if the points can be moved or not.  If true, then
   * the local crew cannot move them.
   */
  protected boolean Locked;

  /**
   * the Track with the Normal route through the points.
   */
  protected int NormalRoute;

  /**
   * constructs a SecEdge with only its Edge identifier.
   *
   * @param edge identifies the side of the GridTile the SecEdge is on.
   * @param shared describes the shared edge, if it is not adjacent.
   *    A null value means it is adjacent.
   * @param points describes the routes which terminate on the PtsEdge.
   */
  public PtsEdge(int edge, Edge shared, SwitchPoints points) {
    super(edge, shared);
    RouteInfo info;
    IOSpec decoder;
    IOSpec[] localCtrl = new IOSpec[MAX_ROUTES];
    boolean toggle;

    MyPoints = points;
    Locked = false;
    NormalRoute = MyPoints.getNormal();
    if (points != null) { //they should always be not null.

      // Though it may not be correct, start out with all fascia lock
      // indicators in the "locked" position.  It would be best to set
      // them to the current state, but the state cannot be read on some
      // systems (e.g. Loconet).
      if ( (decoder = MyPoints.getLockCmds(SwitchPoints.LOCKONCMD)) != null) {
        decoder.sendCommand();
      }

      // register an observer for the Unlock event
      if ( (decoder = MyPoints.getLockCmds(SwitchPoints.UNLOCKREPORT)) != null) {
        new UnLockReceiver(decoder);
      }

      // register an observer for the Lock event
      if ( (decoder = MyPoints.getLockCmds(SwitchPoints.LOCKREPORT)) != null) {
        new LockReceiver(decoder);
        Locked = true;
      }

      for (int align = 0; align < MAX_ROUTES; ++align) {
        if ( (info = MyPoints.getRouteList(align)) != null) {
          // register an observer for the local control requesting movement
          if ( (decoder = info.getRouteReport(RouteInfo.ROUTEREQUEST)) != null) {
            localCtrl[align] = decoder;
          }

          // register an observer for the points moving into position
          if ( (decoder = info.getRouteReport(RouteInfo.SELECTEDREPORT)) != null) {
            new InPlaceReceiver(align, decoder);
          }
          // register an observer for the points moving out of position
          if ( (decoder = info.getRouteReport(RouteInfo.NOTSELECTEDREPORT)) != null) {
            new ReleaseReceiver(align, decoder);
          }
        }
      }

      // Determine which switches are push buttons that just toggle between
      // selected routes
      for (int rte = 0; rte < MAX_ROUTES; ++rte) {
        toggle = false;
        if (localCtrl[rte] != null) {
          for (int r = rte + 1; r < MAX_ROUTES; ++r) {
            if ( (localCtrl[r] != null) && (localCtrl[rte].equals(localCtrl[r]))) {
              toggle = true;
              localCtrl[r] = null;
            }
          }
          if (toggle) {
            new ToggleReceiver(localCtrl[rte]);
          }
          else {
            new LocalReceiver(rte, localCtrl[rte]);
          }
        }
      }
    }
    MySignal = new OSSignal(this);
    FrogSignals = new Signal[MAX_ROUTES];
    Feeders = new FrogSignal[MAX_ROUTES];
    OSIndication = new Indication();
    OSIndication.setCondition(Block.TRK_RESERVED);
    PtsKeeper.add(this);
  }

  /*
   * binds the edge to its mate.  It also converts the
   * default speed to a real speed on each diverging route to "medium".
   */
  public void bind() {
    super.bind();
    for (int trk = 0; trk < MyTracks.length; ++trk) {
      if ( (MyTracks[trk] != null) && (trk != NormalRoute) &&
          (MyTracks[trk].getSpeed() == Track.DEFAULT)) {
        MyTracks[trk].setSpeed(Track.MEDIUM);
      }
    }
    for (int rte = 0; rte < MAX_ROUTES; ++rte) {
      if (MyTracks[rte] != null) {
        Feeders[rte] = new FrogSignal(this);
      }
    }
    if (NormalRoute > -1) {
      Feeders[NormalRoute].setNormal();
    }
  }

  /**
   * steps through the Tracks that have a termination on this Secedge,
   * telling them which Block they are members of.
   */
  public void propagateBlock(Block block) {
    if (block != null) {
      for (int edge = 0; edge < MyTracks.length; ++edge) {
        if (MyTracks[edge] != null) {
          MyTracks[edge].setBlock(block);
        }
      }
    }
  }

  /**
   * remembers the Tracks that terminate on this SecEdge and returns true
   * if the Track is the default route through the edge.
   *
   * @param trk is one of the Tracks terminating on the edge.
   *
   * @return true if the Track is on the Normal route.
   */
  public boolean setDestination(Track trk) {
    boolean normal = false;
    int other = trk.getTermination(this);
    if (other >= 0) {
      MyTracks[other] = trk;
      if (MyWires[other] == null) {
        MyWires[other] = trk;
      }
      if ( (NormalRoute == other) || (Destination == null)) {
        normal = true;
        Destination = trk;
        CurrentTrk = other;
        Wire = MyWires[other];
//        movePoints(other);
        for (other = 0; other < MyTracks.length; ++other) {
          if ( (MyTracks[other] != null) && (CurrentTrk != other)) {
            MyTracks[other].switchEnd(this, false);
          }
        }
      }
      else {
        MyTracks[other].switchEnd(this, false);
      }
    }
    return normal;
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
    MyWires[which] = link;
  }

  /**
   * is called from a SecEdge across the Section, looking for the next signal
   * in the direction of travel.  Since, this SecEdge is a PtsEdge, there
   * will be a probe for each route.  All but the normal route will not
   * have a next signal.
   *
   * @param rte is the Track of the path taken to get across the Section.
   * It is needed by the PtsSec for identifying which of the routes is
   * being probed.
   *
   * @param holder is a place to put the slowest speed found in the
   * direction of travel.
   *
   * @return the next Signal encountered in the direction of travel.  It
   * may be null.
   */
  public Signal neighborProbe(Track rte, Indication holder) {
    int probe;
    for (probe = 0; probe < MAX_ROUTES; ++probe) {
      if (MyTracks[probe] == rte) {
        break;
      }
    }
    return Feeders[probe];
  }

  /**
   * is called from a SecEdge in another Section, looking for the next signal
   * in the direction of travel.  Since this SecEdge is not a Block and is
   * not an OSEdge, it has no signal, so it crosses the Section on the
   * normal route and passes the request to the next SecEdge in the
   * direction of travel.
   *
   * @return the next Signal encountered in the direction of travel.  It
   * may be null.
   */
  public Signal signalProbe(Indication holder) {
    return MySignal;
  }
  /**
   * is called to locate the Signal's feeders.  It is called during
   * initialization only.
   */
  public void locateFeeder() {
    Indication speedHolder = new Indication(Track.DEFAULT, Block.TRK_RESERVED);
    Signal nextSignal;
    if (Joint != null) {
      if ( (nextSignal = Joint.signalProbe(speedHolder)) != null) {
        nextSignal.setPredecesor(MySignal);
      }
      ProtSpeed = speedHolder.getProtSpeed();
    }
    for (int route = 0; route < MAX_ROUTES; ++route) {
      if ( (MyTracks[route] != null) &&
          ( (MyTracks[route].getDestination(this)) != null)) {
        speedHolder.setProtectedSpeed(MyTracks[route].getSpeed());
        if ( (nextSignal = MyWires[route].findSignal(this, speedHolder)) != null) {
          nextSignal.setPredecesor(Feeders[route]);
        }
        FrogSpeeds[route] = speedHolder.getProtSpeed();
      }
    }
  }

  /**
   * is called to determine if the local control is enabled or not.
   *
   * @return true if the local control is active or false if it is
   * ignored because the position of the points are locked.
   */
  protected boolean isLocked() {
    boolean locked = true;
    if (!Locked) {
//      switch (TrkBlkState) {
      switch(MyBlock.determineState()) {
      case Block.TRK_AND_TIME:
      case Block.TRK_OOS:
      case Block.TRK_UNLOCKED:
        locked = false;
        break;

      case Block.TRK_IDLE:
        locked = (MyBlock.getDiscipline() == Block.CTC) || (MyBlock.getDiscipline() == Block.DTC);
        break;

      default:
      }
    }
    return locked;
  }

  /**
   * adjusts the fascia lock/unlock lights, based on the dispatcher
   * granting/removing track and time and the previous value of
   * track and time.
   * @param state is the Track's state
   */
  protected void adjustFasciaLights(int state) {
    IOSpec cmd;
    if (!OSIndication.isTrackandTime() && (state == Block.TRK_AND_TIME)) {
      // Turn off the Locked light and turn on the Unlocked light
      if ( (cmd = MyPoints.getLockCmds(SwitchPoints.LOCKOFFCMD)) != null) {
        cmd.sendCommand();
      }
      if ( (cmd = MyPoints.getLockCmds(SwitchPoints.LOCKONCMD)) != null) {
        cmd.sendUndoCommand();
      }
      if ( (cmd = MyPoints.getLockCmds(SwitchPoints.UNLOCKONCMD)) != null) {
        cmd.sendCommand();
      }
    }
    else if ( (OSIndication.isTrackandTime()) && (state != Block.TRK_AND_TIME)) {
      // Turn off the Unlocked light and turn on the Locked light
      if ( (cmd = MyPoints.getLockCmds(SwitchPoints.UNLOCKOFFCMD)) != null) {
        cmd.sendCommand();
      }
      if ( (cmd = MyPoints.getLockCmds(SwitchPoints.UNLOCKONCMD)) != null) {
        cmd.sendUndoCommand();
      }
      if ( (cmd = MyPoints.getLockCmds(SwitchPoints.LOCKONCMD)) != null) {
        cmd.sendCommand();
      }
    }    
    OSIndication.setCondition(state);
  }
  
  /**
   * receives the state of the Track.  For a plain Section, there is
   * nothing to do.  It is here for several reasons.
   * <ol>
   * <li>it is invoked during initialization
   * <li>it converts TRK_IDLE for ABS and APB to TRK_RESERVED
   * </ol>
   *
   * @param state is the Track's state.
   *
   * @see cats.layout.items.Track
   * @see cats.layout.items.Indication
   */
  public void setState(int state) {
//    Indication initial = new Indication();
    adjustFasciaLights(state);
    if ((state == Block.TRK_IDLE) && (MyBlock.getDiscipline() != Block.CTC) &&
        (MyBlock.getDiscipline() != Block.DTC)){
      state = Block.TRK_RESERVED;
    }
    super.setState(state);
//    initial.setCondition(state);
//    MySignal.trackState(initial);
    OSIndication.setCondition(state);
    MySignal.trackState(OSIndication);
    if (Feeders[CurrentTrk] != null) {
//      Feeders[CurrentTrk].trackState(initial);
      Feeders[CurrentTrk].trackState(OSIndication);
    }
  }

  /**
   * used by the dispatcher to request that the points be moved.  Since the
   * dispatcher controls only OS Points, this does nothing.
   *
   * @param trk identifies the requested aligned Track.
   */
  public void setAlignment(int trk) {
  }

  /**
   * moves the points for the calling Track.  This moves the points.  For
   * a turnout with exact position reporting, alignPoints would get
   * invoked only after the message is received that the points moved.
   *
   * what this code should really do is handle all 4 cases:
   * <ol>
   * <li> neither contact nor release reports
   * <li> both contact and release reports
   * <li> contact report on new route only
   * <li> release report on old route only
   *</ol>
   * This implementation handles neither and contact.
   *
   * @param trk identifies the Track.
   */
  protected void movePoints(int trk) {
    RouteInfo fromRte = MyPoints.getRouteList(CurrentTrk);
    RouteInfo toRte = MyPoints.getRouteList(trk);
    MySection.setSelTrack(MyTracks[trk]);
    if ( ( (fromRte == null) ||
          (fromRte.getRouteReport(RouteInfo.NOTSELECTEDREPORT) == null)) &&
        ( (toRte == null) ||
         (toRte.getRouteReport(RouteInfo.SELECTEDREPORT) == null))) {
      alignPoints(trk);
    }
    else { // open the gap to show the rails are in motion.
      MyTracks[CurrentTrk].switchEnd(this, false);
      Destination = null;
      EdgeTile.requestUpdate();
    }
    // The commands must be sent out last, in case the reports are
    // listening for the commands
    MyPoints.selectRoute(trk);
  }

  /**
   * adjusts all the links when the points are moved.
   *
   * @param newRoute specifies which of the Tracks the points are aligned to.
   */
  protected void alignPoints(int newRoute) {
    if (newRoute != CurrentTrk) {
      MyTracks[CurrentTrk].switchEnd(this, false);
      if (Feeders[CurrentTrk] != null) {
        Feeders[CurrentTrk].setRelay(null);
      }
      CurrentTrk = newRoute;
      Destination = MyTracks[CurrentTrk];
      Wire = MyWires[CurrentTrk];
      MyTracks[CurrentTrk].switchEnd(this, true);
      MySignal.setRelay(FrogSignals[CurrentTrk]);
      if (Feeders[CurrentTrk] != null) {
        Feeders[CurrentTrk].setRelay(PredSignal);
      }
      refreshState();
      EdgeTile.requestUpdate();
//      MyBlock.resumeReservation();
    }
  }

  /**
   * is invoked when points move into place to verify that the
   * movement is expected.  If not, a countermanding command is sent.
   * This enforces that the crew cannot move the points, unless
   * permitted by the dispatcher.
   * 
   * @param trk is the index of the track that the points are (have
   * been commanded) to move to.
   */
  private void verifyPointsinPlace(int trk) {
    if (LocalEnforcement.TheEnforcementType.getFlagValue() &&
        ((MyBlock.getDiscipline() == Block.CTC) || (MyBlock.getDiscipline() == Block.DTC)) &&
        !MyBlock.getTrkNTime() && !MyBlock.getOOS()) {
//      MyPoints.verifyMovement(trk);
      VerifyEvent request = new VerifyEvent(MyPoints, trk, true);
      request.queUp();
    }
  }
  
  /**
   * is invoked when points move out of place to verify that the
   * movement is expected.  If not, a countermanding command is sent.
   * This enforces that the crew cannot move the points, unless
   * permitted by the dispatcher.
   * 
   * @param trk is the index of the track that the points are (have
   * been commanded) to move to.
   */
  private void verifyPointsRelease(int trk) {
    if (LocalEnforcement.TheEnforcementType.getFlagValue() &&
        ((MyBlock.getDiscipline() == Block.CTC) || (MyBlock.getDiscipline() == Block.DTC)) &&
        !MyBlock.getTrkNTime() && !MyBlock.getOOS()) {
//      MyPoints.verifyNoMovement(trk);
      VerifyEvent request = new VerifyEvent(MyPoints, trk, false);
      request.queUp();
    }
  }
  
  /**
   * Registers the Signal that precedes the Points Signal.
   *
   * @param pts is the Signal a train encounters before it approaches
   * the OS security element from the points end.
   */
  public void setPtsSignal(Signal pts) {
    PredSignal = pts;
    Feeders[NormalRoute].initialSignal(pts);
  }

  /**
   * Registers a Signal that requests Indications for a route through the
   * frog.  The requesting Signal is protecting entry into the route through
   * the frog.
   *
   * @param reqSig is the Signal requesting to be fed Indications.
   * @param frogSig is the Signal that received the request.
   */
  public void setFrogSignal(Signal reqSig, FrogSignal frogSig) {
    int rte;
    Indication ind;
    for (rte = 0; rte < MAX_ROUTES; ++rte) {
      if (Feeders[rte] == frogSig) {
        break;
      }
    }
    FrogSignals[rte] = reqSig;
    if (rte == NormalRoute) {
      MySignal.initialSignal(reqSig);
    }
    else {
      ind = new Indication();
      ind.setCondition(Block.TRK_FOULED);
      FrogSignals[rte].nextSignal(ind);

    }
  }

  /**
   * computes a new state based on the Points alignment and Track state.
   * Since MySignal feeds the Signal protecting entry into the OS
   * security plant via the current algnment, that Frog Signal can be
   * ignored.  The Feeders are also updated, but because only one is active,
   * not as much care is needed in copying the Indication.
   */
  protected void refreshState() {
    Indication ind;
    OSIndication.setProtectedSpeed(ProtSpeed);
    MySignal.trackState(OSIndication);
    for (int rte = 0; rte < MAX_ROUTES; ++rte) {
      if (FrogSignals[rte] != null) {
        if (CurrentTrk != rte) {
          ind = new Indication(OSIndication);
//          ind.setNoRoute();
          ind.setCondition(Block.TRK_FOULED);
          FrogSignals[rte].nextSignal(ind);
        }
      }
      if (Feeders[rte] != null) {
        OSIndication.setProtectedSpeed(FrogSpeeds[rte]);
        Feeders[rte].trackState(OSIndication);
      }
    }
  }

  /**
   * steps through the list of PtsEdges, setting them all to the 
   * last known alignment.
   */
  public static void restoreAlignment() {
    PtsEdge turnOut;
    for (Enumeration<PtsEdge> e = PtsKeeper.elements(); e.hasMoreElements(); ) {
      turnOut = e.nextElement();
      turnOut.MyPoints.restorePoints();
    }
  }

  /**
   * steps through the list of PtsEdges, setting them all to the a
   * particular state.  The intent of this method is to test the turnouts
   * on a layout.  If tested for throw, then closed, then
   * if the layout is refreshed, CATS will know the positions
   * of all tunrouts.
   * 
   * @param state is true to throw them all or false to close
   * them all.
   */
  public static void testThrown(boolean state) {
    PtsEdge turnOut;
    for (Enumeration<PtsEdge> e = PtsKeeper.elements(); e.hasMoreElements(); ) {
      turnOut = e.nextElement();
      turnOut.testTurnout(state);
    }
  }

  /**
   * positions all the turnout decoders controlled by the
   * PtsEdge to a particular state.
   * 
   * @param state is the state being tested.  It is true
   * for thrown and false for closed.
   */
  public void testTurnout(boolean state) {
    if (!MyBlock.getOccupied() && (MyPoints != null)) {
      MyPoints.testRoutes(state);
    }
  }

  /**
   * positions all turnouts to the normal (straight thru) route.  If
   * the turnout has feedback, stimulate the method that moves the
   * points.  If it doesn't, then stimulate the method that moves
   * the points and changes the graphics.
   * <p>
   * This does not move the points if the block is occupied
   *
   */
  public static void setAllNormal() {
    PtsEdge turnOut;
    for (Enumeration<PtsEdge> e = PtsKeeper.elements(); e.hasMoreElements(); ) {
      turnOut = e.nextElement();
      if (!turnOut.MyBlock.getOccupied() &&(turnOut.MyPoints != null)) {
        if (turnOut.MyPoints.hasFeedback()) {
          turnOut.MyPoints.selectRoute(turnOut.NormalRoute);
        }
        else {
          turnOut.setAlignment(turnOut.NormalRoute);
        }
      }
    }
  }
  
  /**
   * steps through the list of PtsEdges, setting all the Lock lights.  The
   * intent of this method is to initialize the lights on a layout that
   * does not report state when the system is powered up or to test
   * all the lights.
   * <p>
   * The actions here are a little confusing because there could be 2
   * ways to turn off the existing light.  For example, to turn on
   * the Lock light, the Unlock light should be turned off.  If the
   * command to turn it off (UNLOCKOFF) is defined, it is sent.
   * Alternatively, if the UNLOCKON has a cancel operation, it is sent.
   * @param locked is true if the light is to show "locked"; false, if
   * it is to show "unlocked".
   */
  public static void testLockLights(boolean locked) {
    PtsEdge turnOut;
    IOSpec decoder;
    for (Enumeration<PtsEdge> e = PtsKeeper.elements(); e.hasMoreElements(); ) {
      turnOut = e.nextElement();
      if (locked) {
        if ( (decoder = turnOut.MyPoints.getLockCmds(SwitchPoints.UNLOCKOFFCMD)) != null) {
          decoder.sendCommand();
        }
        if ( (decoder = turnOut.MyPoints.getLockCmds(SwitchPoints.UNLOCKONCMD)) != null) {
          decoder.sendUndoCommand();
        }
        if ( (decoder = turnOut.MyPoints.getLockCmds(SwitchPoints.LOCKONCMD)) != null) {
          decoder.sendCommand();
        }
      }
      else {
        if ( (decoder = turnOut.MyPoints.getLockCmds(SwitchPoints.LOCKOFFCMD)) != null) {
          decoder.sendCommand();
        }
        if ( (decoder = turnOut.MyPoints.getLockCmds(SwitchPoints.LOCKONCMD)) != null) {
          decoder.sendUndoCommand();
        }
        if ( (decoder = turnOut.MyPoints.getLockCmds(SwitchPoints.UNLOCKONCMD)) != null) {
          decoder.sendCommand();
        }
      }
    }
  }

  /**
   * is an inner class for receiving requests for local movement of the points.
   * It has a one-to-one relationship with a single alignment of the points.
   */
  private class LocalReceiver
      implements DecoderObserver {

    /**
     * is the Track being controlled.
     */
    private int TrkControlled;

    /**
     * is the constructor.
     *
     * @param c is the track being controlled.
     * @param decoder is the IOSpec describing the messages that the receiver
     * is looking for.
     */
    public LocalReceiver(int c, IOSpec decoder) {
      TrkControlled = c;
      decoder.registerListener(this);
    }

    /**
     * is the interface through which the MsgFilter delivers the RREvent.
     * This routine probably should tell the enclosing Section that the
     * alignment has changed.  Not doing so means that the dispatcher may
     * have to click multiple times to make a change.
     */
    public void acceptIOEvent() {
      if (!isLocked()) {
        movePoints(TrkControlled);
        GridTile.doUpdates();
      }
    }
  }

  /**
   * is an inner class for receiving requests for local movement of the points.
   * It is a toggle report so that every time it is received, the points move.
   */
  private class ToggleReceiver
      implements DecoderObserver {

    /**
     * is the constructor.
     *
     * @param decoder is the IOSpec describing the messages that the receiver
     * is looking for.
     */
    public ToggleReceiver(IOSpec decoder) {
      decoder.registerListener(this);
    }

    /**
     * is the interface through which the MsgFilter delivers the RREvent.
     * This routine probably should tell the enclosing Section that the
     * alignment has changed.  Not doing so means that the dispatcher may
     * have to click multiple times to make a change.
     */
    public void acceptIOEvent() {
      int newRoute;
      if (!isLocked()) {
        newRoute = CurrentTrk;
        do {
          if ( (++newRoute) == MyTracks.length) {
            newRoute = 0;
          }
        }
        while (MyTracks[newRoute] == null);
        movePoints(newRoute);
        GridTile.doUpdates();
      }
    }
  }

  /**
   * is an inner class for receiving reports that the points are in position.
   * It has a one-to-one relationship with a single alignment of the points.
   */
  private abstract class AlignmentReceiver
      implements DecoderObserver {

    /**
     * is the Track that is aligned.
     */
    protected int TrkControlled;
    protected IOSpec Decoder;

    /**
     * is the constructor.
     *
     * @param c is the track being aligned.
     * @param decoder is the IOSpec describing the messages that the receiver
     * is looking for.
     */
    public AlignmentReceiver(int c, IOSpec decoder) {
      Decoder = decoder;
      TrkControlled = c;
      decoder.registerListener(this);
    }

    /*
     * is the interface through which the MsgFilter delivers the RREvent.
     */
    public void acceptIOEvent() {
      MyBlock.clrRoute();
      doAlignment();
      GridTile.doUpdates();
    }

    /**
     * is the routine that handles the report.
     */
    protected abstract void doAlignment();
  }

  /**
   * is an inner class that reports on the points being moved into position.
   */
  private class InPlaceReceiver
      extends AlignmentReceiver {

    /**
     * is the constructor.
     *
     * @param c is the track being aligned.
     * @param decoder is the IOSpec describing the messages that the receiver
     * is looking for.
     */

    public InPlaceReceiver(int c, IOSpec decoder) {
      super(c, decoder);
    }

    /**
     * tells the PtsEdge that the points are aligned
     */
    protected void doAlignment() {
      log.debug("Triggered: " + Decoder.getName() + " " + Decoder.getPolarity());
      verifyPointsinPlace(TrkControlled);
      alignPoints(TrkControlled);
      MySection.setSelTrack(MyTracks[TrkControlled]);
    }
  }

  /**
   * is an inner class that reports on the points being moved out of position.
   */
  private class ReleaseReceiver
      extends AlignmentReceiver {

    /**
     * is the constructor.
     *
     * @param c is the track being aligned.
     * @param decoder is the IOSpec describing the messages that the receiver
     * is looking for.
     */

    public ReleaseReceiver(int c, IOSpec decoder) {
      super(c, decoder);
    }

    /**
     * tells the PtsEdge that the points are not aligned
     */
    protected void doAlignment() {
      int otherTrk;  // This cannot be computed when the class is instantiated because
                     // MyTracks may not have been filled in.  It is the index into
                     // MyTracks of the other route.  This does not work for a three-way
                     // turnout.
      
      for (otherTrk = 0; otherTrk < MyTracks.length; ++otherTrk) {
        if ((MyTracks[otherTrk] != null) && (otherTrk != TrkControlled)) {
          break;
        }
      }
      RouteInfo toRte = MyPoints.getRouteList(otherTrk);
      if ( (toRte == null) ||
          (toRte.getRouteReport(RouteInfo.SELECTEDREPORT) == null)) {
        verifyPointsRelease(TrkControlled);
        alignPoints(otherTrk);
        MySection.setSelTrack(MyTracks[otherTrk]);
      }
      else {
        // show the points no longer aligned for TrkControlled
      }
    }
  }

  /**
   * is an inner class for receiving reports that the manual controls on
   * the switch machine have been unlocked.
   */
  private class UnLockReceiver
      implements DecoderObserver {

    /**
     * is the constructor.
     *
     * @param decoder is the IOSpec describing the messages that the receiver
     * is looking for.
     */
    public UnLockReceiver(IOSpec decoder) {
      decoder.registerListener(this);
    }

    /*
     * is the interface through which the MsgFilter delivers the RREvent.
     */
    public void acceptIOEvent() {
      Locked = false;
      GridTile.doUpdates();
    }
  }

  /**
   * is an inner class for receiving reports that the manual controls on
   * the switch machine have been locked.
   */
  private class LockReceiver
      implements DecoderObserver {

    /**
     * is the constructor.
     *
     * @param decoder is the IOSpec describing the messages that the receiver
     * is looking for.
     */
    public LockReceiver(IOSpec decoder) {
      decoder.registerListener(this);
    }

    /*
     * is the interface through which the MsgFilter delivers the RREvent.
     */
    public void acceptIOEvent() {
      Locked = true;
      GridTile.doUpdates();
    }
  }

  static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(
      PtsEdge.class.getName());
}
/* @(#)PtsEdge.java */
