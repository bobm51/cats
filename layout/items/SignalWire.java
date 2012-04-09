/*
 * Name: SignalWire.java
 *
 * What:
 *   This file contains an interface for wiring Signals together.
 */
package cats.layout.items;

/**
 * defines the interface for wiring Signals.
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */
 public interface SignalWire {

   /**
    * sets the Block that encompasses the Track.  A side affect of telling
    * the Track what Block it is, is that it registers itself with that
    * Block.
    *
    * @param block is the Block.
    */
//   public void setBlock(Block block);

   /**
    * searches for the signal on the other end of the wire from a
    * SecEdge.  If the SignalWire is a Track, then the next signal cannot be
    * closer than the SecEdge found by traversing the Track.  If the
    * SignalWire is an Xtrack, then the signal is the one protecting the
    * crossing on the side of the caller.  Furthermore, the signal
    * must locate its feeder by initiating a probe from the other side
    * of the crossing.
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
   public Signal findSignal(SecEdge caller, Indication holder);

    /**
    * adds or removes the traffic condition.
    *
    * @param traffic is the reason for the traffic.
    * <ul>
    * <li> It could be NO_TRAFFIC_SET (none),
    * <li> SINGLE_MOVE (a train will be coming through),
    * <li> or DTC_RESERVATION (a track warrant is active).
    * </ul>
    */
   public void setTraffic(int traffic);
 }
