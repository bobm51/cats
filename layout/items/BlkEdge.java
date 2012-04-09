/* Name: BlkEdge.java
 *
 * What:
 *   This class is the container for all the information about one
 *   of a Section's Edges which "is a" Block boundary.
 */

package cats.layout.items;

import cats.common.Sides;
import cats.trains.Train;
import java.awt.Point;
import java.util.Enumeration;

/**
 * is a Block boundary, without a visible Signal.
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2004, 2009, 2010</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */
public class BlkEdge
extends SecEdge {
  
  // The following define the Entry/Exit status of the BlkEdge.
  /**
   * means no status has been assigned.
   */
  public static final int NO_RESERVATION = 0;
  
  /**
   * means the Edge is reserved for a train entering the Block from
   * the adjacent Block (Section).
   */
  public static final int ENTER_RESERVATION = 1;
  
  /**
   * means the Edge is reserved for a train to exit the Block through
   * the Edge.
   */
  public static final int EXIT_RESERVATION = 2;
  
  /**
   * a placeholder for the Signal associated with a Block boundary.
   */
  private Signal MySignal;
  
  /**
   * is the Reservation state of the BlkEdge.
   */
  protected int Reservation;

  /**
   * is the strategy for requesting a reservation on the
   * the protected block.
   */
  protected RequestStrategy MyRequestStrategy;

  /**
   * is the strategy for reserving the requested Block.
   */
  protected ReserveStrategy MyReserveStrategy;

  /**
   * is the strategy for determining what to do when the
   * Block is unoccupied.
   */
  protected UnoccupyStrategy MyUnoccupyStrategy;
  
  /**
   * constructs a BlkEdge with only its Edge identifier.
   *
   * @param edge identifies the side of the GridTile the SecEdge is on.
   * @param shared describes the shared edge, if it is not adjacent.
   *    A null value means it is adjacent.
   * @param block is the Block definition.  If it is null, then the
   *   a default block is created, which may be overwritten during the
   *   bind process.
   * @param blkSignal is a Signal.  It may not be null.
   */
  public BlkEdge(int edge, Edge shared, Block block, Signal blkSignal) {
    super(edge, shared);
    if (block == null) {
      MyBlock = new Block("");
    }
    else {
      block.registerEdge(this);
      MyBlock = block;
    }
    MySignal = blkSignal;
    if (MySignal != null) {
      MySignal.registerEdge(this);
    }
    Reservation = NO_RESERVATION;
    strategyFactory();
  }
  
  /**
   * is used to determine if a SecEdge is a Block boundary.
   *
   * @return true if it is and false if it is not.
   */
  
  public boolean isBlock() {
    return true;
  }
  
  /**
   * is used to determine if a SecEdge is on a HomeSignal or not.
   *
   * @return true if it is and false if it is not.  The boundaries of
   * ABS Blocks are always Control Points because ABS does not support
   * route reservations.
   */
  public boolean isHomeSignal() {
    return MyBlock.getDiscipline() == Block.ABS;
  }
  
  /**
   * is used to determine if a SecEdge is a Control Point.
   *
   * @return true if it is and false if it is not.
   */
  public boolean isControlPoint() {
    return MySignal.isCP();
  }
  
  /**
   * is used to determine if a visible signal is protecting entrance
   * into the Section.
   *
   * @return true if a Signal is located on the SecEdge; false if it
   * is not.
   */
  public boolean hasSignal() {
    return ( (MyBlock.getDiscipline() == Block.ABS) || MySignal.isRealSignal());
  }
  
  /**
   * is used to add Tracks to their Blocks.  In the XML file, only one
   * SecEdge in a Block contains the Block definition.  This method is called
   * for that Block, which sets the rest of the Track registration process
   * in motion.  It tells each Track with a termination on the SecEdge
   * which block the Track belongs to.  The Track, in turn, tells both temination
   * ends which Block they belong to.  In addition to propagating the Block
   * identity to the other end(s), it will echo back and try to tell this
   * SecEdge which Block it is in.  Thus, the other form of getBlock()
   * must stop the recursion.
   */
  public void setBlock() {
    propagateBlock(MyBlock);
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
   * <li> if the SecEdge is a Block boundary and the block is not the
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
    if (MyBlock != block) {
      if (MyBlock.getDiscipline() == Block.UNDEFINED) {
        MyBlock = block;
        MyBlock.addBlockEdge(this);
      }
      else {
        Point p = MySection.getCoordinates();
        log.warn("Side " + MyEdge + " in section " + p.x + "," + p.y +
        " has a second definition for its Block.");
      }
    }
    else if (MyBlock != null) {
      MyBlock.addBlockEdge(this);
    }
    MySignal.setBlockDiscipline(MyBlock.getDiscipline());
    strategyFactory();
  }
  
  /**
   * enables or disables Fleeting through the Block from this BlkEdge.
   *
   * @param fleet is true if Fleeting should be enabled and false if
   * it should be disabled.
   */
  protected void setFleet(boolean fleet) {
    MyBlock.setFleeting(fleet);
  }
  
  /*
   * is called from a SecEdge in another Section, looking for the next signal
   * in the direction of travel.  Since this edge has a Signal (virtual),
   * the Signal is returned.
   *
   * @param holder is a place to put the slowest speed found in the
   * direction of travel.
   *
   * @return the next Signal encountered in the direction of travel.  It
   * may be null.
   */
  public Signal signalProbe(Indication holder) {
    if (Destination != null) {
      return MySignal;
    }
    return null;
  }
  
  /**
   * is called to locate the Indication's feeder.  It is called during
   * initialization only.
   */
  public void locateFeeder() {
    if (Destination == null) {
      System.out.println("The BlkEdge at " + MySection.getCoordinates() + ":" + MyEdge +
          " is not associated with a Track.");
    }
    Indication protSpeed = new Indication(Destination.getSpeed(),
        Block.TRK_RESERVED);
    Signal feeder = super.signalProbe(protSpeed);
    ProtSpeed = protSpeed.getProtSpeed();
    if (feeder != null) {
      feeder.setPredecesor(MySignal);
    }
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
    Indication newInd = new Indication(ProtSpeed, state);
    TrkBlkState = state;
    boolean panelOn = true;
    
    if (state <= Block.TRK_IDLE) {
      switch (Reservation) {
      case NO_RESERVATION:
        if ((MyBlock.getDiscipline() == Block.CTC) ||
            (MyBlock.getDiscipline() == Block.DTC)) {
          panelOn = false;
        }
        else {
          state = Block.TRK_RESERVED;
        }
        break;
        
      case ENTER_RESERVATION:
        state = Block.TRK_RESERVED;
        break;
        
      default:
        state = Block.TRK_EXIT;
      }
    }
    newInd.setPanelOn(panelOn);
    newInd.setCondition(state);
    MySignal.trackState(newInd);
  }
  
  /**
   * is called to set or clear a reservation through the Track that this
   * BlkEdge terminates, from this BlkEdge.
   *
   * For a BlkEnd, this is sets the Reservation to set the signal and
   * protect the track.
   *
   * @param reserve is true if the the reservation is being made and false
   * if it is being cleared.
   */
  protected void setEnterReservation(boolean reserve) {
    Reservation = (reserve) ? ENTER_RESERVATION : NO_RESERVATION;
  }
  
  /**
   * is called to set or clear an exit reservation through the Track that this
   * SecEdge terminates, out of this SecEdge.
   *
   * @param reserve is true if the the reservation is being made and false
   * if it is being cleared.
   */
  protected void setExitReservation(boolean reserve) {
    Reservation = (reserve) ? EXIT_RESERVATION : NO_RESERVATION;
    super.setExitReservation(reserve);
    setState(TrkBlkState);
  }
  
  /**
   * clears any Signal history.  For a plain edge, there is nothing to do.
   */
  public void clrEdgeHistory() {
    if ( (MySignal != null)) {
      MySignal.clrSignalHistory();
    }
  }
  
  /**
   * Sets a reservation from the BlkEdge protected by a signal.  If
   * the next block is not protected by a signal, then this signal
   * must protect it, as well.  Consequently, the state of the next
   * block influences the safety of the route.
   * <p>
   * If the next block is protected by an intermediate, then the
   * request should propagate through the next block, but because
   * of the intermediate signal, the state of the next block does
   * not influence the route through this block.
   * <p>
   * If the next block is protected by a control point, then the
   * propagation ends.
   * <p>
   * A route reservation can be initiated only on a BlkEdge protected
   * by a Control Point (CTC and DTC) or a BlkEdge protected by a
   * Home Signal (APB).
   * <p>
   * There is a gap in this algorithm in that the reservation stops at
   * a Control Point and if the block on the other side is idle, it
   * is accepted.  However, the next block could have a route exit.
   * The algorithm should really scan for an opposing route to the next
   * Home Signal.
   * 
   * @return the state of the blocks being protected.
   */
  private int requestRoute() {
  int status = MyBlock.determineState();
    BlkEdge nextBlk;
    SecEdge egress = null;
    
    // All reservations stop at a Block boundary that is an exit
    if (Reservation == EXIT_RESERVATION) {
      status = Block.TRK_EXIT;
    }
    else {
      egress = findEgress();
      if ( (egress != null) && egress.isBlock()) {
        // The immediate block can be reserved.  Should the reservation
        // propagate to the next Block?
        nextBlk = (BlkEdge) egress.getNeighbor();
        if (nextBlk != null) {
          status = Math.max(status,
              nextBlk.MyRequestStrategy.requestReservation());
        }
        // else there is no next block.  So, this is the end of the world - 
        // at least of the layout
      }
      else if (egress == null) {  // the end of the block is not aligned to a track
        status = Block.TRK_FOULED;
      }
      else {  // the egress has no neighbor
        status = Block.TRK_IDLE;
      }
    }
    return status;
}
  
  /**
   * requests a reservation from this BlkEdge to the Next.
   * @return true if there is no opposing traffic between BlkEdge
   * and the next and there is a signal (Intermediate or
   * Control Point) between this BlkEdge and the next.
   */
public boolean setupReservation() {
  BlkEdge nextBlk;
  if (requestRoute() <= Block.TRK_IDLE) {
    nextBlk = reserveBlock(Track.SINGLE_MOVE);
    if (nextBlk != null) {
      nextBlk.MyReserveStrategy.makeReservation(Track.SINGLE_MOVE);
    }
    return true;
  }
  return false;
}

/**
 * reserves the block, based on the signals associated with the BlkEdge.
 * @param reservation is the kind of reservation to make.
 */
public void resumeReservation(int reservation) {
  MyReserveStrategy.makeReservation(reservation);
}

/**
 * Cancels the reservation through a block.
 *
 */
public void removeReservation() {
  BlkEdge nextBlk = reserveBlock(Track.NO_TRAFFIC_SET);
  if (nextBlk != null) {
    nextBlk.MyReserveStrategy.makeReservation(Track.NO_TRAFFIC_SET);
  }
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
public BlkEdge reserveBlock(int reserve) {
  SecEdge egress = null;
  BlkEdge nextBlk = null;
  Enumeration<SecEdge> e = makeRoute();
  setEnterReservation(reserve != Track.NO_TRAFFIC_SET);
  while (e.hasMoreElements()) {
    egress = e.nextElement();
    egress.Wire.setTraffic(reserve);
  }
  if (MyBlock == null) {
    log.warn("The block edge at " + getSection().getCoordinates() + " is not in a block.");    
  }
  else {
    getBlock().setRoute(reserve != Track.NO_TRAFFIC_SET, this, egress);
  }
  if (egress == null) {
    log.warn("Cannot set a route through " + getSection().getCoordinates() + ":" +
        Sides.EDGENAME[MyEdge]);
  }
  else {
    egress.setExitReservation(reserve != Track.NO_TRAFFIC_SET);
    if (egress.isBlock()) {
      nextBlk = (BlkEdge) egress.getNeighbor();
    }
  }
  return nextBlk;
}

/**
 * tells the BlkEdge that a train supposedly entering through that
 * BlkEdge has exited the Block.
 */
public void removeOccupancy() {
  MyUnoccupyStrategy.unoccupyBlock();
}

/**
 * searches for a Train in the peer Block.
 *
 * @return the first Train found or null.
 */
public Train trainSearch() {
  Section s;
  Train t = null;
  if ( (Joint != null) && ( (Joint.getBlock()) != null)) {
    for (Enumeration<SecEdge> e = ( (BlkEdge) Joint).makeRoute();
    e.hasMoreElements(); ) {
      s = e.nextElement().MySection;
      if (s.hasTrain()) {
        return s.getTrain();
      }
    }
  }
  return t;
}

/**
 * is called to determine if the Joint is set for an exit reservation.
 *
 * @return true if it is and false if it is not.
 */
public boolean expectingTrain() {
  return ( (Joint != null) &&
      (( (BlkEdge) Joint).Reservation == EXIT_RESERVATION));
}

/**
 * is called to determine if the Joint is set for an entry reservation -
 * a Train entering (or entered) from this BlkEdge. 
 * @return true if a Train is not expected to enter the Block
 * through the BlkEdge.
 */
public boolean notExpectingTrain() {
  return ( (Joint == null) ||
      ( (BlkEdge) Joint).Reservation == ENTER_RESERVATION);
}

/**
 * is called to determine if the adjacent Block is occupied.
 *
 * @return true if the Joint is occupied.
 */
public boolean neighborOccupied() {
  return ( (Joint != null) && ( (BlkEdge) Joint).MyBlock.getOccupied());
}

/**
 * tells a BlkEdge that its neighbor is occupied or not.  If
 * it is, then the signal can be turned on or off.
 * 
 * @param lit is true to light the signal and false to turn
 * it off.
 */
private void lightSignal(boolean lit) {
  if (MySignal != null) {
    MySignal.setApproachState(lit);
  }
}

/**
 * tells the neighbor to turn on or turn off its Signal.
 * 
 * @param lit is true to turn on the signal and false to turn off
 * the signal.
 */
public void triggerSignal(boolean lit) {
  if ((Joint != null) && ((BlkEdge) Joint).hasSignal()) {
    ((BlkEdge) Joint).lightSignal(lit);
  }
}

/**
 * is called to determine if there is a clear route
 * (one without any fouled tracks) across the block.
 * 
 * @return true is there is a clear route.
 */
public boolean hasClearRoute() {
  return !MySignal.isShowingFouled();
}

/**
 * creates an Enumeration for traversing the rails in a route through
 * a Block
 *
 * @return an enumeration that returns the exit edge of the rail in each
 * Section crossed.
 */
public Route makeRoute() {
  return new Route();
}

/**
 * crosses the tracks in a Block, beginning at this BlkEdge
 * until
 * <ul>
 * <li>the opposing BlkEdge is encountered
 * <li>the track ends on the edge of the layout
 * <li>a switch is aligned against the track
 * </ul>
 * 
 * @return the SecEdge which is on the other end of the Route.
 * This cannot be null because if a track has one end, it has to
 * have at least a matching opposite.
 */
public SecEdge findEgress() {
  SecEdge egress = null;
  Route e = makeRoute();
  while (e.hasMoreElements()) {
    if (e.isFouled()) {
      egress = null;
      break;
    }
    egress = e.nextElement();
  }
  if (e.isFouled()) {
    return null;
  }
  return egress;
}

/**
 * An inner class for providing an Enumeration over the track sections
 * in a route through the Block.
 */
private class Route
implements Enumeration<SecEdge> {
  
  /**
   * the next edge to be given out.
   */
  private SecEdge NextEdge = null;
  
  /**
   * is true if the route through NextEdge is not aligned
   */
  private boolean Fouling = false;
  
  /**
   * The constructor.
   */
  public Route() {
    NextEdge = traverse();
  }
  
  public boolean hasMoreElements() {
    return NextEdge != null;
  }

  /**
   * returns the fouling status of the route
   * @return true if the route is fouled and false if it is not
   */
  public boolean isFouled() {
    return Fouling;
  }
  
  /**
   * returns the next Section exit edge, in the path being taken through
   * the Block.  There are multiple exit conditions:
   * 1. if NextEdge is a BlkEdge, then we have reached the Block boundary
   * 2. if NextEdge has no neighbor, then we have reached the Block
   *    boundary.
   * 3. if NextEdge is a PtsEdge and the route from the path into the
   *    Section is fouling, then we have reached the Block boundary.
   */
  public SecEdge nextElement() {
    if (hasMoreElements()) {
      SecEdge exitEdge = NextEdge;
      SecEdge entryEdge;
      NextEdge = null;
      if (!Fouling && !exitEdge.isBlock()) {
        if ( (entryEdge = exitEdge.getNeighbor()) != null) {
          NextEdge = entryEdge.traverse();
          if (NextEdge == null) {
            if (entryEdge.Destination != null) {
              NextEdge = entryEdge.Destination.getDestination(entryEdge);
            }
            Fouling = true;
          }
        }
      }
      return exitEdge;
    }
    return null;
  }
}

/**
 * The following are objects that implement the Strategy design
 * pattern for handling
 * <ul>
 * <li>reservation requests
 * <li>reserving Blocks
 * <li>releasing Blocks
 * <li>Block unoccupied events
 * </ul>
 * There are two factors that determine the strategy: the kind
 * of Signal (none, Intermediate, Control Point) and the signal
 * discipline (ABS, APB, CTC, TWC)
 */

/**
 * the factory for determining which strategies should be instantiated.
 */
public void strategyFactory() {
  int discipline = (MyBlock != null) ? MyBlock.getDiscipline() :
    Block.ABS;
  int signalType = (MySignal != null) ? MySignal.SignalType :
    Signal.VIRTUAL_SIGNAL;
  switch (signalType) {
    case Signal.VIRTUAL_SIGNAL:
      if (discipline == Block.ABS) {
        MyRequestStrategy = new NullRequestStrategy();
        MyReserveStrategy = new NullReserveStrategy();
        MyUnoccupyStrategy = new NullUnoccupyStrategy();
      }
      else {
        MyRequestStrategy = new VirtualRequestStrategy();
        MyReserveStrategy = new PropagateReserveStrategy();
        MyUnoccupyStrategy = new StickUnoccupyStrategy();
      }
      break;
      
    case Signal.INTERMEDIATE:
      if (discipline == Block.ABS) {
        MyRequestStrategy = new NullRequestStrategy();
        MyReserveStrategy = new NullReserveStrategy();
        MyUnoccupyStrategy = new NullUnoccupyStrategy();
      }
      else {
        MyRequestStrategy = new IntermediateRequestStrategy();
        MyReserveStrategy = new IntermediateReserveStrategy();
        MyUnoccupyStrategy = new StickUnoccupyStrategy();
      }
      break;
      
    default: // Control Point  
      MyRequestStrategy = new NullRequestStrategy();
      MyReserveStrategy = new NullReserveStrategy();
      if (discipline == Block.ABS) {
        MyUnoccupyStrategy = new NullUnoccupyStrategy();
      }
      else {
        MyUnoccupyStrategy = new APBUnoccupyStrategy();
    }
  }
}
/**
 * ***** Strategies to use when requesting a reservation *******
 */
private interface RequestStrategy {
  /** 
   * @return if the reservation request is propogated, then the
   * status of the next block; otherwise, Block.Idle, the least
   * restrictive status.
   */
  public int requestReservation();
}

/**
 * Used by ABS signals and Control Points.  Reservation requests
 * end on either.
 */
private class NullRequestStrategy implements RequestStrategy {
  /**
   * An ABS Block does not propagate a reservation request, but
   * does not deny one, either.  The same is true of a Control
   * Point.
   * @return if the reservation request is propogated, then the
   * status of the next block; otherwise, Block.TRK_RESERVED, the least
   * restrictive status.
   */
  public int requestReservation() {
    return Block.TRK_RESERVED;
  }
}

/**
 * Used by Blocks protected by Virtual Signals.  The status returned
 * is the most restrictive of this Block and any Blocks further
 * down the track.
 */
private class VirtualRequestStrategy implements RequestStrategy {
  /**
   * The request is propagated and includes the status of the
   * protected block and any following blocks.
   * @return if the reservation request is propogated, then the
   * status of the next block; otherwise, Block.Idle, the least
   * restrictive status.
   */
  public int requestReservation() {
    return requestRoute();
  }
}

/**
 * Used by Blocks protected by Intermediate Signals.  The status returned
 * is the least restrictive, unless a block later on has a train
 * set to come this way.
 */
private class IntermediateRequestStrategy implements RequestStrategy {
  /**
   * The request is propagated solely to ensure that an opposing
   * route has not been set up.
   * @return if the reservation request is propogated, then the
   * status of the next block; otherwise, Block.TRK_RESERVED, the least
   * restrictive status.
   */
  public int requestReservation() {
    return (requestRoute() == Block.TRK_EXIT ? Block.TRK_EXIT :
      Block.TRK_RESERVED);
  }
}

/**
 * ***** Strategies to use when making or clearing a reservation *******
 */
private interface ReserveStrategy {
  /** 
   * @param reserve is the kind of reservation to make.
   */
  public void makeReservation(int reserve);
}

/**
 * Used by ABS signals and Control Points.  Reservation do not
 * pass through either.  This class implements the "Null"
 * strategy.
 */
private class NullReserveStrategy implements ReserveStrategy {
  /**
   * The reservation goes no farther.
   * @param reserve is the kind of reservation to make.
   */
  public void makeReservation(int reserve) {
  }
}

/**
 * Used by Intermediate Signals.  Because an intermediate is an
 * automatic signal, it can tell a falsehood if asked if a reservation
 * can be made through it.  Thus, the neighbor may attempt to make a
 * reservation, but it should be quietly ignored.
 */
private class IntermediateReserveStrategy implements ReserveStrategy {
  /**
   * If the Block can accept a reservation, then the Block is
   * reserved and the reservation is propogated.
   * @param reserve is the kind of reservation to make.
   */
  public void makeReservation(int reserve) {
    BlkEdge nextBlk;
    if (!MySignal.isShowingFouled() &&
        (MyBlock.determineState() <= Block.TRK_IDLE)) {
      nextBlk = reserveBlock(reserve);
      if (nextBlk != null) {
        nextBlk.MyReserveStrategy.makeReservation(reserve);
      }      
    }
  }  
}

/**
 * Used by Blocks not protected by Signals
 */
private class PropagateReserveStrategy implements ReserveStrategy {
  /**
   * If the Block can accept a reservation, then the Block is
   * reserved and the reservation is propogated.
   * @param reserve is the kind of reservation to make.
   */
  public void makeReservation(int reserve) {
    BlkEdge nextBlk;
    if (MyBlock.determineState() <= Block.TRK_IDLE) {
       nextBlk = reserveBlock(reserve);
       if (nextBlk != null) {
         nextBlk.MyReserveStrategy.makeReservation(reserve);
       }
    }
  }
}


/**
 * ***** Strategies to use when a block becomes unoccupied *******
 */
private interface UnoccupyStrategy {
  public void unoccupyBlock();
}

/**
 * Used by ABS signals.  This class implements the "Null"
 * strategy.
 */
private class NullUnoccupyStrategy implements UnoccupyStrategy {
  /**
   * Since ABS Blocks do not have route reservations, there
   * is nothing to do when the Block is unoccupied.
   */
  public void unoccupyBlock() {
  }
}

/**
 * Used by Blocks not protected by Signals and by Intermediates.
 */
private class StickUnoccupyStrategy implements UnoccupyStrategy {
  /**
   * Used by Blocks without Signals and with Intermediates.
   * This is the implementation
   * of the "traffic stick".  If there is a reservation, then the
   * adjacent Block dictates whether the reservation should be
   * retained or removed.  If the adjacent Block has an exit
   * reservation, then the reservation should be retained; otherwise,
   * it should be removed.
   */
  public void unoccupyBlock() {
    BlkEdge neighbor;
    if (Reservation == ENTER_RESERVATION) {
      neighbor = (BlkEdge) getNeighbor();
      if ((neighbor == null) || (neighbor.Reservation != EXIT_RESERVATION)) {
          reserveBlock(Track.NO_TRAFFIC_SET);
      }
    }
  }
}

/**
 * Used by APB and CTC Blocks.
 */
private class APBUnoccupyStrategy implements UnoccupyStrategy {
  /**
   * Used by Control Points on APB and CTC blocks.  When the Block becomes
   * unoccupied, the route is removed through the block.  With TWC, the Block
   * remains reserved until the dispatcher manually clears it.
   */
  public void unoccupyBlock() {
    reserveBlock(Track.NO_TRAFFIC_SET);
  }
}

static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(
    BlkEdge.class.getName());
}
/* @(#)BlkEdge.java */