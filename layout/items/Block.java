/* Name Block.java
 *
 * What:
 *  This class holds the information needed for a detection block.
 */
package cats.layout.items;

import cats.common.Constants;
import cats.common.Prop;
import cats.gui.jCustom.JListDialog;
import cats.gui.CtcFont;
import cats.gui.CounterFactory;
import cats.gui.GridTile;
import cats.gui.Sequence;
import cats.gui.Tracker;
import cats.layout.DecoderObserver;
import cats.layout.Logger;
import cats.layout.OccupancyFilterCounter;
import cats.layout.OccupancySpectrum;
import cats.layout.store.AbstractStoreWatcher;
import cats.layout.xml.*;
import cats.trains.Train;
import cats.trains.TrainStore;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.BitSet;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;
import javax.swing.Timer;

/**
 * contains the information needed for a detection block:
 * <ul>
 * <li>the name of the block
 * <li>the IOSpec of the detector
 * <li>the discipline used on the block
 * <li>if the block is hidden on the dispatcher panel or not
 * </ul>
 * <p>
 * The purpose of a Block is to hold the state of the Block and distibute it to
 * the Tracks contained within the Block.
 * <p>
 * The states of a Block (and Track and Edge) are defined in this file.
 * <ol>
 * <li>TRK_RESERVED means
 * <ul>
 * <li>a route has been reserved through the Block for an approaching train.
 * <li>the signal on the entry end is showing CLEAR
 * <li>the signal on the exit end is showing STOP
 * <li>the tracks constituting the route on the panel are CLEAR
 * <li>the dispatcher cannot change the route (move any switch points)
 * <li>local controls are locked so that they cannot change the route
 * <li>turnout lock lights are on
 * <li>the reservation can be removed by the dispatcher (CTC)
 * </ul>
 * <li>TRK_IDLE means there is no reservation and no reasons why a reservation
 * cannot be made.
 * <ul>
 * <li>all signals are STOP when using CTC
 * <li>the signals on entry and exit are CLEAR when using ABS or APB
 * <li>all tracks constituting the route on the panel are white
 * <li>the dispatcher can change the route (move any switch points)
 * <li>local controls are unlocked when not CTC
 * <li>turnout lock lights are off when not CTC
 * <li>the dispatcher can make a reservation on a through route (CTC)
 * </ul>
 * <li>TRK_OCCUPIED means at least one train is present in the Block
 * <ul>
 * <li>all signals are STOP
 * <li>all tracks constituting the route on the panel are white
 * <li>the dispatcher cannot change the route (move any switch points)
 * <li>local controls are locked
 * <li>turnout lock lights are on
 * <li>the dispatcher can clear a reservation (CTC)
 * </ul>
 * <li>TRK_AND_TIME means the dispatcher has granted permission for crew to
 * perform local switching in the block
 * <ul>
 * <li>the entry and exit signals are STOP AND PROCEED
 * <li>all tracks constitutiing the block are purple
 * <li>the dispatcher cannot change the route
 * <li>local controls are unlocked
 * <li>turnout lock lights are off
 * <li>reservations are prohibited
 * </ul>
 * <li>TRK_OOS means that the dispatcher has taken the track out of service for
 * maintenance
 * <ul>
 * <li>all signals are STOP
 * <li>all tracks constituting the block are blue
 * <li>the dispatcher cannot change the route
 * <li>local controls are unlocked
 * <li>turnout lock lights are off
 * <li>reservations are prohibited
 * </ul>
 * <li>TRK_FOULED means that the leg of the route ends in a turnout aligned to
 * another direction. This state applies to pieces of a Block and not all Tracks
 * in the block.
 * <ul>
 * <li>all signals feeding into the turnout are STOP
 * <li>the tracks on the partial route reflect the state of the block, but are
 * not CLEAR
 * <li>depending upon the state, the dispatcher may throw turnouts
 * <li>depending upon the state, local control may be enabled
 * <li>depending upon the state, the turnout lock light may be off
 * <li>reservations are prohibited
 * </ul>
 * <li>TRK_UNLOCKED means that the local crew has unlocked a turnout.
 * <ul>
 * <li>all signals protecting the unlocked turnout are set to STOP
 * <li>the turnout is shown as unknown on the dispatcher panel
 * <li>the dispatcher cannot change the turnout
 * <li>the local crew can change the turnout
 * <li>the lock light is off
 * <li>reservations are prohibited
 * </ul>
 * <li>TRK_EXIT applies to only the signal protecting the exit from a route
 * <ul>
 * <li>the signal protecting the exit is STOP
 * <li>the reservation cannot be cleared from the exit end
 * <li>a new reservation cannot be set through the exit end
 * </ul>
 * </ol>
 * 
 * <p>
 * Title: CATS - Crandic Automated Traffic System
 * </p>
 * <p>
 * Description: A program for dispatching trains on Pat Lana's Crandic model
 * railroad.
 * <p>
 * Copyright: Copyright (c) 2004, 2010
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author Rodney Black
 * @version $Revision$
 */

public class Block implements XMLEleObject {

	/**
	 * a list of all Blocks
	 */
	private static Vector<Block> BlockKeeper = new Vector<Block>();

	/**
	 * The XML tag for recognizing the Block description.
	 */
	public static final String XML_TAG = "BLOCK";

	/**
	 * The XML attribute tag for the signaling discipline.
	 */
	static final String DISCIPLINE = "DISCIPLINE";

	/**
	 * The XML attribute tag for the Block's name.
	 */
	static final String NAME = "NAME";
  
  /**
   * The XML attribute tag for the Block's Station.
   */
  static final String STATION = "STATION";
  
	/**
	 * The XML attribute tag for the Visible flag.
	 */
	static final String VISIBLE = "VISIBLE";

	/**
	 * The XML object tag for the occupied IOSpec.
	 */
	static final String OCCUPIED = "OCCUPIEDSPEC";

	/**
	 * The XML object tag for the unoccupied IOSpec.
	 */
	static final String UNOCCUPIED = "UNOCCUPIEDSPEC";

	/**
	 * The "true" string.
	 */
	static final String TRUE = "true";

	/**
	 * The "false" string.
	 */
	static final String FALSE = "false";

	/**
	 * the pop up selection to occupy the Block.
	 */
	private static final String OCCUPY = "Occupy Block";

	/**
	 * the pop up selection to unoccupy the Block.
	 */
	private static final String UNOCCUPY = "Unoccupy Block";

	/**
	 * the pop up selection to add track and time.
	 */
	private static final String ADD_TNT = "Grant Track Authority";

	/**
	 * the pop up selection to remove track and time.
	 */
	private static final String REMOVE_TNT = "Remove Track Authority";

	/**
	 * the pop up selection to take a block out of service.
	 */
	private static final String ADD_OOS = "Grant Out of Service";

	/**
	 * the pop up selection to remove out of service.
	 */
	private static final String REMOVE_OOS = "Remove Out of Service";

	/**
	 * the pop up selection to position a train.
	 */
	private static final String ADD_TRAIN = "Position Train";

	/**
	 * the Labels for the Menu Items. The order of these is closely tied to the
	 * logic of blockMenu, so if they are changed, blockmenu will have to
	 * change.
	 */
	private static final String[] MenuItems = { OCCUPY, UNOCCUPY, ADD_TNT,
			REMOVE_TNT, ADD_OOS, REMOVE_OOS, ADD_TRAIN };

	/**
	 * The signal disciplines that have been defined.
	 */
	public static final String[] DisciplineName = { "Unknown", "CTC", "ABS",
			"APB-2", "APB-3", "DTC" };

	/**
	 * The default discipline, which is simply a placeholder.
	 */
	public static final int UNDEFINED = 0;

	/**
	 * the index for using CTC rules in the Block.
	 */
	public static final int CTC = 1;

	/**
	 * the index for using ABS rules in the Block.
	 */
	public static final int ABS = 2;

	/**
	 * the index for using one block, APB rules in the Block.
	 */
	public static final int APB_1 = 3;

	/**
	 * the index for using two block, APB rules inthe Block.
	 */
	public static final int APB_2 = 4;

	/**
	 * the index for using DTC rules in the Block.
	 */
	public static final int DTC = 5;

	/**
	 * the Discipline defined for the Block.
	 */
	private int Discipline = UNDEFINED; // default is unknown - it can be
										// overridden.

	/**
	 * means that there is nothing in the Block to prevent a Clear signal
	 */
	public static final int TRK_RESERVED = -1;

	/**
	 * records the default condition is Stop, entry prohibited.
	 */
	public static final int TRK_IDLE = 0;

	/**
	 * records that the occupancy detector has tripped.
	 */
	public static final int TRK_OCCUPIED = 1;

	/**
	 * records that the dispatcher granted "track and time".
	 */
	public static final int TRK_AND_TIME = 2;

	/**
	 * records out of service for maintenance.
	 */
	public static final int TRK_OOS = 3;

	/**
	 * records an active track warrant.
	 */
	public static final int TRK_WARRANT = 4;

	/**
	 * the list of Stop conditions.
	 */
	private BitSet Blocker = new BitSet(TRK_WARRANT + 1);

	// Other reasons for showing a STOP indication
	/**
	 * the points on a Track are not aligned
	 */
	public static final int TRK_FOULED = TRK_WARRANT + 1;

	/**
	 * a turnout is unlocked.
	 */
	public static final int TRK_UNLOCKED = TRK_FOULED + 1;

	/**
	 * traffic is prohibited from entering the Block.
	 */
	public static final int TRK_EXIT = TRK_UNLOCKED + 1;

  /**
   * the conditions for not moving a train out of a Block.
   */
  private static BitSet IgnoreTrain = new BitSet(TRK_WARRANT + 1);
  
	// The constants for the priority lists
	/**
	 * the highest priority - neighbor is exit with object
	 */
	private static final int ACTIVE_EXIT = 0;

	/**
	 * neighbor is exit without object
	 */
	private static final int INACTIVE_EXIT = ACTIVE_EXIT + 1;

	/**
	 * neighbor is no entrance or exit with object
	 */
	private static final int ACTIVE_EMPTY = INACTIVE_EXIT + 1;

	/**
	 * neighbor is no entrance or exit without object
	 */
	private static final int INACTIVE_EMPTY = ACTIVE_EMPTY + 1;

	/**
	 * neighbor is entrance with object
	 */
	private static final int ACTIVE_ENTER = INACTIVE_EMPTY + 1;

	/**
	 * neighbor is entrance without object
	 */
	private static final int INACTIVE_ENTER = ACTIVE_ENTER + 1;

	/**
	 * neighbor is fouling exit with object
	 */
	private static final int FOUL_EXIT = INACTIVE_ENTER + 1;

	/**
	 * neighbor is fouling with neither entrance or exit
	 */
	private static final int FOUL_EMPTY = FOUL_EXIT + 1;

	/**
	 * neighbor is fouling entrance with object
	 */
	private static final int FOUL_ENTER = FOUL_EMPTY + 1;

	/**
	 * the number of priority levels
	 */
	private static final int PRI_LEVELS = FOUL_ENTER + 1;

	/**
	 * is the number of seconds to delay before considering an occupancy report
	 * to be stable.
	 */
	private static Sequence Debouncer;

  /**
	 * is the number of milliseconds to wait after sending a command before
	 * sending the next. This meters the output so as to not overrrun the
	 * network.
	 */
	private static Sequence Restrictor;
  
  /**
   * is the list of listeners
   */
  private static LinkedList<AbstractStoreWatcher> Observers;
    
	/**
	 * the name of the Block.
	 */
	private String BlockName;

  /**
   * the name of the Station.
   */
  private String StationName;

	/**
	 * a flag, where true means the dispatcher panel should display the Block
	 * and false means it should be hidden.
	 */
	private boolean Visible = true;

	/**
	 * the IOSPec for identifying when the block is occupied.
	 */
	private IOSpec Occupied;

	/**
	 * the IOSPec for identifying when the Block is unoccupied. It should be the
	 * opposite of Occupied, but is included for those cases when it isn't.
	 */
	private IOSpec Unoccupied;

	/**
	 * is a list of all the Tracks enclosed in the Block.
	 */
	private Vector<Track> TrkList;

	/**
	 * is a list of all BlkEdges in the Block.
	 */
	private Vector<BlkEdge> EdgeList;

	/**
	 * is the BlkEdge that a train is entering (has entered) from.
	 */
	private BlkEdge EnterEdge;

	/**
	 * is the BlkEdge that a train is exiting (has entered) from.
	 */
	private SecEdge ExitEdge;

	/**
	 * is the SecEdge that the Block is defined in. It is needed as a starting
	 * point for assigning Tracks to the Block.
	 */
	BlkEdge Anchor;

	/**
	 * is the number of train labels in the block.
	 */
	private int NumTrainLabels;
	
	/**
	 * is a detection debounce timer. The detector must report occupied at least
	 * Debounce seconds before it is accepted. This is the timer that is the
	 * gate.
	 */
	private Timer MyDebouncer;

	/**
	 * is the timeout value of the debounce timer. It is remembered so that if
	 * the user changes the global value, the specific timer will be changed.
	 */
	private int LastTimeout;

	/**
	 * is true when waiting for the debounce timer to fire.
	 */
	private boolean Debouncing;

  /**
   * is the timestamp of when the block occupied event was seen
   */
  private long OccupyOn;
  
	/**
	 * is true when fleeting is enabled through the Block.
	 */
	private boolean FleetOn;

	/**
	 * the constructor.
	 * 
	 * @param name
	 *            is the name of the Block. It can be null.
	 */
	public Block(String name) {
		if (name != null) {
			BlockName = new String(name);
		}
		TrkList = new Vector<Track>();
		EdgeList = new Vector<BlkEdge>();
		EnterEdge = null;
		ExitEdge = null;
		Blocker.set(TRK_IDLE);
		FleetOn = false;
		NumTrainLabels = 0;
		if (Debouncer == null) {
		  Debouncer = CounterFactory.CountKeeper.findSequence(CounterFactory.DEBOUNCETAG);
    }
		if (Restrictor == null) {
      Restrictor = CounterFactory.CountKeeper.findSequence(
          CounterFactory.REFRESHTAG);
		}
    if (Observers == null) {
      Observers = new LinkedList<AbstractStoreWatcher>();
    }
	}

	/**
	 * return the Block's discipline.
	 * 
	 * @return an index into DisciplineName, describing the rules for setting
	 *         signals and controlling activity
	 */
	public int getDiscipline() {
		return Discipline;
	}

	/**
	 * sets the Block's Discipline.
	 * 
	 * @param discipline
	 *            should be a valid index into DisciplineName.
	 */
	public void setDiscipline(int discipline) {
		if ((discipline >= 0) && (discipline < DisciplineName.length)) {
			Discipline = discipline;
		}
	}

	/**
	 * retrieves the name of the Block.
	 * 
	 * @return the name of the Block
	 */
	public String getBlockName() {
		if (BlockName != null) {
			return new String(BlockName);
		}
		return null;
	}

  /**
   * return the Name of the Station the Block is at.
   * @return the Station name.
   */
  public String getStationName() {
      if (StationName != null) {
          return new String(StationName);
      }
      return null;
  }

  /**
   * saves the name of the Station at the Block.  Multiple Blocks
   * can be at the same Station (e.g. the main and a siding).
   * @param station is the name of the station that the Block belongs to
   */
  public void setStationName(String station) {
      if (station != null) {
      StationName = new String(station);  
      }
      else {
          StationName = null;
      }
  }

	/**
	 * return the value of Visible.
	 * 
	 * @return true if the Block is to be shown; false if it is to be hidden.
	 */
	public boolean getVisible() {
		return Visible;
	}

	/**
	 * sets the value of Visible.
	 * 
	 * @param visible
	 *            the value of Visible. True if the Block is to be shown on the
	 *            dispatcher panel or false if it is to be hidden.
	 */
	public void setVisible(boolean visible) {
		Visible = visible;
	}

	/**
	 * tells the caller if the Block is detected or not.
	 * 
	 * @return true if sensors have been defined for detecting block occupancy
	 *         and empty.
	 */
	public boolean isDarkTerritory() {
		return ((Occupied == null) || (Unoccupied == null));
	}

  /**
   * tells the caller the decoder address of the occupancy detector.
   * 
   * @return the JMRI name of the detector reporting occupied or null
   * if there is none.
   */
  public String getDetectorName() {
    if (Occupied != null) {
      return Occupied.getName();
    }
    return null;
  }
  
	/**
	 * adds a Track to the list of enclosed Tracks.
	 * 
	 * @param trk
	 *            is a Track. It must register only once.
	 * 
	 * @see cats.layout.items.Track
	 */
	public void registerTrack(Track trk) {
		TrkList.add(trk);
	}

  /**
   * locks (unlocks) the decoders associated with all turnouts
   * in the block.
   * @param locked is true to lock the decoders and false to unlock
   * them.
   * @see cats.layout.items.Track
   * @see cats.layout.items.LockedDecoders
   */
  private void lockTracks(boolean locked) {
    for (Enumeration<Track> iter = TrkList.elements(); iter.hasMoreElements(); ){
      iter.nextElement().lockEnds(locked);
    }
  }
	/**
	 * tells the Block which SecEdge it encloses.
	 * 
	 * @param edge
	 *            is the SecEdge in which the Block is defined.
	 */
	public void registerEdge(BlkEdge edge) {
		Anchor = edge;
	}

	/**
	 * adds a BlkEdge to the list of BlkEdges on the boundary of the Block.
	 * 
	 * @param newEdge
	 *            is the BlkEdge. It must be registered only once.
	 */
	public void addBlockEdge(BlkEdge newEdge) {
		EdgeList.add(newEdge);
	}

	/**
	 * presents the Menu of operations on the Block.
	 * 
	 * @param location
	 *            is the location of the Mouse on the screen.
	 * 
	 * @see cats.gui.jCustom.JListDialog
	 */
	public void blockMenu(Point location) {
		Vector<String> menu = new Vector<String>();
		Vector<Train> trains;
		Vector<String> names;
		Train t;
		String[] items;
		int selection;
		String title;

		if (getBlockName() != null) {
			title = new String("Block: " + getBlockName());
		} else {
			title = new String("Block: unknown");
		}
		if (getOccupied()) {
			menu.add(MenuItems[1]);
		} else {
			menu.add(MenuItems[0]);
		}
		if (!FleetOn) {
			if (getTrkNTime()) {
				menu.add(MenuItems[3]);
			} else if (getOOS()) {
				menu.add(MenuItems[5]);
			} else {
				menu.add(MenuItems[2]);
				menu.add(MenuItems[4]);
			}
		}
		menu.add(MenuItems[6]);
		// convert to an array so the window is more compact.
		items = new String[menu.size()];
		for (int i = 0; i < menu.size(); ++i) {
			items[i] = menu.elementAt(i);
		}
		if ((selection = JListDialog.select(items, title, location)) >= 0) {
			switch (CtcFont.findString(items[selection], MenuItems)) {
			case 0: // Occupy was selected
				occupyBlock(true);
				findTrain();
				break;

			case 1:
				occupyBlock(false);
				break;

			case 2:
        setTNT(true);
        lockTracks(true);
				break;

			case 3:
        setTNT(false);
        lockTracks(false);
        resumeReservation();
				break;

			case 4:
			  setOOS(true);
        lockTracks(true);
				break;

			case 5:
        setOOS(false);
        lockTracks(false);
        resumeReservation();
				break;

			case 6:
				trains = TrainStore.TrainKeeper.getCreated();
				names = new Vector<String>(trains.size());
				for (Enumeration<Train> e = trains.elements(); e
						.hasMoreElements();) {
					t = e.nextElement();
					names.add(t.getSymbol() + " " + t.getName());
				}
				if ((selection = JListDialog.select(names, "Select a Train",
						location)) >= 0) {
					t = trains.elementAt(selection);
					t.positionTrain(location);
				}
				break;

			default:
			}
		}
	}

  /**
   * enables or disables Track Authority on a block.
   * @param setIt is true to enable Track Authority.
   */
  public void setTNT(boolean setIt) {
    String sense = Constants.ADD_MARKER;
    if (setIt){
      Blocker.set(TRK_AND_TIME);
      distributeState();
      clrRoute();      
    }
    else {
      Blocker.clear(TRK_AND_TIME);
      distributeState();
      sense = Constants.REMOVE_MARKER;
    }
//    Logger.timeStamp(Logger.TNT_TAG, sense + Logger.FS + BlockName);
    broadcastUpdate(Constants.TNT_TAG, sense + Constants.FS + Constants.QUOTE + BlockName + Constants.QUOTE);
  }
  
  /**
   * enables or disables OOS on a Block.
   * @param setIt is true to take a track out of service.
   */
  public void setOOS(boolean setIt){
    String sense = Constants.ADD_MARKER;
    if (setIt){
      Blocker.set(TRK_OOS);
      distributeState();
      clrRoute();      
    }
    else {
      Blocker.clear(TRK_OOS);
      distributeState();
      sense = Constants.REMOVE_MARKER;
    }
//    Logger.timeStamp(Logger.OOS_TAG, sense + Logger.FS + BlockName); 
    broadcastUpdate(Constants.OOS_TAG, sense + Constants.FS + Constants.QUOTE + BlockName + Constants.QUOTE);
  }
  
	/**
	 * looks at adjacent blocks for a Train label which could be leaving the
	 * block and entering this one. The results may be
	 * <ul>
	 * <li>none - then there is no Train label to move to this Block
	 * <li>one - then assumes that the Train is entering this Block
	 * <li>more than one - then if one of them has an exit reservation, it will
	 * be selected; otherwise, none will be selected.
	 * </ul>
	 * 
   * A train that has tied down on a track without a
   * reservation is not a candidate.  A train in a Block
   * that is OOS or has Track and Time is not a candidate.
	 */
	private void findTrain() {
		AdjacentBlocks levels = new AdjacentBlocks();
		BlkEdge b = null;
		Train t = null;
		if (Tracker.AutoTracker.getTracking() && (NumTrainLabels == 0)) {
			for (Enumeration<BlkEdge> e = EdgeList.elements(); e.hasMoreElements();) {
				b = e.nextElement();
				if (b == EnterEdge) {
					if (b.trainSearch() != null) {
						levels.saveItem(b, ACTIVE_EXIT);
					} else {
						levels.saveItem(null, INACTIVE_EXIT);
					}
				} else if ((b != ExitEdge) && !b.notExpectingTrain()&& (b.hasClearRoute())) {
					if (((t = b.trainSearch()) != null)&& 
            !b.getNeighbor().getBlock().Blocker.intersects(IgnoreTrain)
                && !t.hasRun()) {
						levels.saveItem(b, ACTIVE_EMPTY);
					}
				}
			}
			if ((b = (BlkEdge) levels.findObject()) != null) {
        if ((t = b.trainSearch()) != null) {
          t.advanceTrain(b);
        }
			}
		}
	}

	/**
	 * is called to determine if a Train is positioned to Enter the Block. it is
	 * called when the Block's dicipline is APB and the block is reported to be
	 * occupied. If an adjacent Block is occupied, then the reason for this
	 * Block being occupied is that a Train entered from that adjacent Block.
	 * There could be multiple adjacent occupied Blocks. In that case, if one
	 * (and only one) has a reservation to enter this Block, then it is
	 * selected. If there is only one adjacent Block occupied, then it is
	 * selected.
	 * 
	 * @return the BlkEdge through which a Train could be entering the Block. A
	 *         null is returned if there is no adjacent Block occupied or there
	 *         are multiple candidates (with priority given to a Block with an
	 *         exit reservation into this Block).
	 */
	private BlkEdge findEntry() {
		AdjacentBlocks levels = new AdjacentBlocks();
		BlkEdge b = null;
		for (Enumeration<BlkEdge> e = EdgeList.elements(); e.hasMoreElements();) {
			b = e.nextElement();
			if (b.expectingTrain()) {
			  if (b.neighborOccupied()) {
			    if (b.hasClearRoute()) {
			      levels.saveItem(b, ACTIVE_EXIT);
			    }
			    else {
			      levels.saveItem(b, FOUL_EXIT);
			    }
			  } else {
			    if (b.hasClearRoute()) {
			      levels.saveItem(b, INACTIVE_EXIT);
			    }
			    else {
			      levels.saveItem(b, FOUL_EMPTY);
			    }
			  }
			} else if (b.notExpectingTrain()) {
				if (b.neighborOccupied()) {
					levels.saveItem(b, ACTIVE_ENTER);
				} else {
					levels.saveItem(b, INACTIVE_ENTER);
				}
			} else if (b.neighborOccupied()) {
				levels.saveItem(b, ACTIVE_EMPTY);
			}
		}
		return (BlkEdge) levels.findObject();
	}

	/**
	 * if the discipline is APB, checks for a train entering an unreserved
	 * Block. When this is true, it reserves the block, as if the dispatcher
	 * did.
	 * 
	 */
	private void checkReservation() {
    BlkEdge blkEdge = findEntry();
		if ((blkEdge != null) && (EnterEdge == null) && blkEdge.isControlPoint()) {
			blkEdge.setupReservation();
		}
	}

  /**
   * this method should be called after a blocking condition is cleared.
   * It looks at all the block edges, if there is only one from whose peer
   * has an exit reservation and is not a control point, then a reservation
   * is created through it.  This method should not be invoked if there is
   * already a reservation through the block edge, but doing so should not
   * hurt anything.
   */
	public void resumeReservation() {
	  BlkEdge enterEdge = null;
	  BlkEdge be;
	  for (Enumeration<BlkEdge> e = EdgeList.elements(); e.hasMoreElements();) {
	    be = e.nextElement();
	    if (be.expectingTrain()) {
        if (enterEdge != null) {
          return;
        }
	      enterEdge = be;
	    }
	  }
	  if ((enterEdge != null) && !enterEdge.isControlPoint()) {
	    enterEdge.resumeReservation(Track.SINGLE_MOVE);
	  }
	}
  
	/**
	 * sets or clears the occupancy detection state of the Block. It can be
	 * invoked from either a Detector or menu item, and no distinction is made
	 * between the two. By not making a distinction, the dispatcher can
	 * compensate for a lost detection message.
	 * 
	 * @param occupancy
	 *            is true if the Block is to be occupied and false if is is
	 *            vacated.
	 */
	public void occupyBlock(boolean occupancy) {
		Enumeration<SecEdge> e;
		SecEdge edge;
		int newState;
		boolean wasOccupied = Blocker.get(TRK_OCCUPIED);
		if (occupancy) {
      if ((Discipline == APB_1) || (Discipline == APB_2)) {
				checkReservation();
			}
			Blocker.set(TRK_OCCUPIED);
			if (EnterEdge == null) {
				distributeState();
			} 
      else { // tell only the reserved tracks that they are occupied.
				newState = determineState();
				e = EnterEdge.makeRoute();
				while (e.hasMoreElements()) {
					edge = e.nextElement();
					edge.Destination.setTrkState(newState);
				}
			}
      if (!wasOccupied) {
        lockTracks(true);
      }
		} 
    else {
			Blocker.clear(TRK_OCCUPIED);
			distributeState();
      lockTracks(false);
      if (EnterEdge == null) {  // Check for a reservation that was blocked
        resumeReservation();
      }
      else {
				if (Discipline == DTC) {  // this is needed to mark the warrant as satisfied
					EnterEdge.reserveBlock(Track.DTC_RESERVATION);
				} else if (FleetOn) {
//					EnterEdge.reserveBlock(Track.SINGLE_MOVE);
				} else {
          EnterEdge.removeOccupancy();
				}
			}
		}
    setApproach(occupancy);
	}

	/**
	 * sets the end points on a route through the Block.
	 * 
	 * @param reserve
	 *            is true if the route is being reserved or false if it is being
	 *            cleared.
	 * @param entryEdge
	 *            is the BlkEdge the train enters the Block through.
	 * @param exitEdge
	 *            is the SecEdge the train exits the Block through.
	 */
	public void setRoute(boolean reserve, BlkEdge entryEdge, SecEdge exitEdge) {
		if (reserve) {
			EnterEdge = entryEdge;
			ExitEdge = exitEdge;
		} else {
			EnterEdge = null;
			ExitEdge = null;
		}
	}

  /**
   * implements approach lighting.  This method finds the signals
   * at the edges of the adjacent blocks and turns them on or
   * off.
   * 
   * @param lit is true to turn on the signals and false
   * to turn off the signals.
   */
  private void setApproach(boolean lit) {
    BlkEdge myEdge;
    for (Enumeration<BlkEdge> iter = EdgeList.elements(); iter.hasMoreElements(); ) {
      myEdge = iter.nextElement();
      myEdge.triggerSignal(lit);
    }
  }
  
	/**
	 * clears the traffic direction through the Block. This is called in
	 * response to a Block wide event.
	 */
	public void clrRoute() {
		if (EnterEdge != null) {
			EnterEdge.reserveBlock(Track.NO_TRAFFIC_SET);
		}
		EnterEdge = null;
		ExitEdge = null;
	}

	/**
	 * activates a Track Warrant on the Block.
	 */
	public void setWarrant() {
		Blocker.set(TRK_WARRANT);
		distributeState();
	}

	/**
	 * removes a Track Warrent on a Block.
	 */
	public void clrWarrant() {
		Blocker.clear(TRK_WARRANT);
		distributeState();
		clrRoute();
	}

  /**
   * reports if the Block has a reservation or not.
   * 
   * @return true if a reservation goes through the BLock and
   * false if there is none.
   */
  public boolean isReserved() {
    return EnterEdge != null;
  }
  
	/**
	 * returns the Block Occupied status.
	 * 
	 * @return true if the Block dectector is tripped.
	 */
	public boolean getOccupied() {
		return Blocker.get(TRK_OCCUPIED);
	}

	/**
	 * returns the Track and Time status.
	 * 
	 * @return true if the dispatcher has granted Track and Time.
	 */
	public boolean getTrkNTime() {
		return Blocker.get(TRK_AND_TIME);
	}

	/**
	 * returns the Out of Service status.
	 * 
	 * @return true if the Block is out of service.
	 */
	public boolean getOOS() {
		return Blocker.get(TRK_OOS);
	}

	/**
	 * returns the DTC status.
	 * 
	 * @return true if a track warrant is active.
	 */
	public boolean getDTC() {
		return Blocker.get(TRK_WARRANT);
	}

	/**
	 * sets or clears the Fleeting status. If Fleeting is enabled, a reservation
	 * is made as soon as the Block become unoccupied.
	 * 
	 * @param fleeting
	 *            equal to true enables fleeting and false disables fleeting.
	 */
	public void setFleeting(boolean fleeting) {
		FleetOn = fleeting;
		Blocker.clear(TRK_WARRANT);
	}

	/**
	 * determines the state on the tracks, based on State.
	 * 
	 * @return an integer, representing the most dominating bit in State.
	 */
	public int determineState() {
		int state = TRK_RESERVED;

		if (getTrkNTime()) {
			state = TRK_AND_TIME;
		} else if (getOOS()) {
			state = TRK_OOS;
		} else if (getOccupied()) {
			state = TRK_OCCUPIED;
		} else if (getDTC()) {
			state = TRK_WARRANT;
		} else if (Blocker.get(TRK_IDLE)) {
			state = TRK_IDLE;
		}
		return state;
	}

	/**
	 * distributes the Block's state to all member Tracks.
	 */
	private void distributeState() {
		Track trk;
		int state = determineState();
		for (Enumeration<Track> e = TrkList.elements(); e.hasMoreElements();) {
			trk = e.nextElement();
			trk.setTrkState(state);
		}
	}

	/**
	 * clears the Block's history, so that it can be refreshed
	 */
	private void clearHistory() {
		Track trk;
		int delay;
		int state = determineState();
    setApproach(Blocker.get(TRK_OCCUPIED));
		for (Enumeration<Track> e = TrkList.elements(); e.hasMoreElements();) {
			trk = e.nextElement();
			trk.clrTrkHistory();
			trk.setTrkState(state);
		}
    // Sleep a while so the serial port is not overwhelmed.
    if ((delay = Restrictor.getAdjustment()) != 0) {
      try {
        Thread.sleep(delay);
      } catch (InterruptedException e) {
      }
    }
	}

	/**
	 * increments the counter of trains in the block.
	 */
	public void incrementTrains() {
		NumTrainLabels = NumTrainLabels + 1;
	}
	
	/**
	 * decrements the counter of trains in the block.
	 */
	public void decrementTrains() {
		NumTrainLabels = NumTrainLabels - 1;
	}

  /**
   * searches for the Block with a particular name.
   * 
   * @param name is the name of a Block it must not be null.
   * 
   * @return the Block with that name, if found.  Otherwise,
   * return null.
   */
  public static Block findBlock(String name) {
    Block blk;
    for (Enumeration<Block> e = BlockKeeper.elements(); e.hasMoreElements();) {
      blk = e.nextElement();
      if (name.equals(blk.BlockName)) {
        return blk;
      }
    }
   return null; 
  }

  /**
   * registers an observer for changes
   * @param observer is the object interested in changes
   */
  public static void registerObserver(AbstractStoreWatcher observer) {
    if (!Observers.contains(observer)) {
      Observers.add(observer);
    }
  }
  
  /**
   * deregisters an observer
   * @param observer is the object that is no longer interested
   * in chnages.
   */
  public static void deregisterObserver(AbstractStoreWatcher observer) {
    Observers.remove(observer);
  }

  /**
   * broadcasts a status update on a block to all observers
   * @param tag is a tag on the status
   * @param status is the status update
   */
  private void broadcastUpdate(String tag, String status) {
    String update = Logger.timeStamp(tag, status);
    for (Iterator<AbstractStoreWatcher> iter = Observers.iterator(); iter.hasNext(); ) {
      iter.next().broadcast(update);
    }       
  }
  
	/**
	 * is an inner class for receiving occupied reports.
	 */
	private class Occupier implements DecoderObserver {

		/**
		 * is the constructor.
		 * 
		 * @param decoder
		 *            is the IOSpec describing the message that the receiver is
		 *            looking for.
		 */
		public Occupier(IOSpec decoder) {
			decoder.registerListener(this);
		}

		/**
		 * is the interface through which the RREvent is delivered.
		 */
		public void acceptIOEvent() {
			int global_timeout;
			boolean wasOccupied = Blocker.get(TRK_OCCUPIED);
			if ((global_timeout = Debouncer.getAdjustment()) == 0) {
        if (OccupancySpectrum.instance().isRecording()) {
          OccupyOn = Calendar.getInstance().getTimeInMillis();
        }
        else {
          OccupyOn = 0;
        }
				occupyBlock(true);
				if (!wasOccupied) {
					findTrain();
				}
				GridTile.doUpdates();
			}
      else if (!Debouncing) {
        if (OccupancySpectrum.instance().isRecording()) {
          OccupyOn = Calendar.getInstance().getTimeInMillis();
        }
        else {
          OccupyOn = 0;
        }
				if (LastTimeout != global_timeout) {
					LastTimeout = global_timeout;
					MyDebouncer.setDelay(LastTimeout * 1000);
					MyDebouncer.setInitialDelay(LastTimeout * 1000);
				}
				MyDebouncer.start();
				Debouncing = true;
			}
		}
	}

	/**
	 * is an inner class for receiving unoccupied reports.
	 */
	private class Unoccupier implements DecoderObserver {

		/**
		 * is the constructor.
		 * 
		 * @param decoder
		 *            is the IOSpec describing the message that the receiver is
		 *            looking for.
		 */
		public Unoccupier(IOSpec decoder) {
			decoder.registerListener(this);
		}

		/**
		 * is the interface through which the RREvent is delivered.
		 */
		public void acceptIOEvent() {
      OccupancySpectrum recorder = OccupancySpectrum.instance();
      if (recorder.isRecording() && (OccupyOn != 0)) {
        recorder.classify(Calendar.getInstance().getTimeInMillis() - OccupyOn);
      }
			if (Debouncing) {
				MyDebouncer.stop();
				Debouncing = false;
        OccupancyFilterCounter.instance().bumpCounter();
			} else {
				occupyBlock(false);
				GridTile.doUpdates();
			}
		}
	}

	/*
	 * is the method through which the object receives the text field.
	 * 
	 * @param eleValue is the Text for the Element's value.
	 * 
	 * @return if the value is acceptable, then null; otherwise, an error
	 * string.
	 */
	public String setValue(String eleValue) {
		return null;
	}

	/*
	 * is the method through which the object receives embedded Objects.
	 * 
	 * @param objName is the name of the embedded object @param objValue is the
	 * value of the embedded object
	 * 
	 * @return null if the Object is acceptible or an error String if it is not.
	 */
	public String setObject(String objName, Object objValue) {
		String resultMsg = null;
		if (OCCUPIED.equals(objName)) {
			Occupied = ((Detector) objValue).getSpec();
			Debouncing = false;
			LastTimeout = Debouncer.getAdjustment();
			MyDebouncer = new Timer(LastTimeout, new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					MyDebouncer.stop();
					occupyBlock(true);
          findTrain();
					Debouncing = false;
					GridTile.doUpdates();
				}
			});
			MyDebouncer.setRepeats(false);
		} else if (UNOCCUPIED.equals(objName)) {
			Unoccupied = ((Detector) objValue).getSpec();
		} else {
			resultMsg = new String(XML_TAG + " XML elements cannot have "
					+ objName + " embedded objects.");
		}
		return resultMsg;
	}

	/*
	 * returns the XML Element tag for the XMLEleObject.
	 * 
	 * @return the name by which XMLReader knows the XMLEleObject (the Element
	 * tag).
	 */
	public String getTag() {
		return new String(XML_TAG);
	}

	/*
	 * tells the XMLEleObject that no more setValue or setObject calls will be
	 * made; thus, it can do any error checking that it needs. It should
	 * probably check that if an occupy IOSpec is listed that an unoccupy IOSpec
	 * is also listed.
	 * 
	 * @return null, if it has received everything it needs or an error string
	 * if something isn't correct.
	 */
	public String doneXML() {
		if (Discipline != UNDEFINED) {
			BlockKeeper.add(this);
		}
		if (Occupied != null) {
			new Occupier(Occupied);
		}
		if (Unoccupied != null) {
			new Unoccupier(Unoccupied);
		}
		return null;
	}

	/**
	 * registers a BlockFactory with the XMLReader.
	 */
	static public void init() {
		XMLReader.registerFactory(XML_TAG, new BlockFactory());
		Detector.init(OCCUPIED);
		Detector.init(UNOCCUPIED);
    IgnoreTrain.set(TRK_AND_TIME);
    IgnoreTrain.set(TRK_OOS);
	}

	/**
	 * walks through the list of Blocks to set the inital state of all the
	 * components of the Blocks.
	 */
	public static void startUp() {
		Block blk;
		for (Enumeration<Block> e = BlockKeeper.elements(); e.hasMoreElements();) {
			blk = e.nextElement();
			if (blk.Discipline != UNDEFINED) {
				blk.clearHistory();
			}
		}
	}

	/**
	 * This inner class is used to record the results of scanning for trains or
	 * occupied blocks. This just records the results.
	 * 
	 * There are 6 priority levels and the object owner adds Objects (either
	 * Trains or BlkEdges) to each level, in the priority known to the owner.
	 * When all Objects have been added, the owner asks for the results. This
	 * class scans the items recorded in priority order and returns the highest
	 * priority level with anything in it. If there is a single Object, that
	 * Object is returned. If there is more than one, null is returned.
	 */
	class AdjacentBlocks {

		/**
		 * the number of Objects found at each priority level
		 */
		int[] Hits;

		/**
		 * the Objects being recorded
		 */
		Object[] ItemSaved;

		/**
		 * The constructor.
		 */
		public AdjacentBlocks() {
			Hits = new int[PRI_LEVELS];
			ItemSaved = new Object[PRI_LEVELS];
		}

		/**
		 * adds an Object to the saved list.
		 * 
		 * @param itemFound
		 *            is the Object being saved. It can be null.
		 * @param priority
		 *            is the priority of the item
		 */
		public void saveItem(Object itemFound, int priority) {
			if ((priority >= 0) && (priority < PRI_LEVELS)) {
				Hits[priority]++;
				ItemSaved[priority] = itemFound;
			}
		}

		/**
		 * searches through the levels, looking for the highest priority level
		 * with something in it. If there is a single entry, then that entry is
		 * returned. If there is more than one, then null is returned.
		 * 
		 * @return the single entry from the highest priority level
		 */
		public Object findObject() {
			for (int level = 0; level < PRI_LEVELS; level++) {
				if (Hits[level] != 0) {
					if (Hits[level] == 1) {
						return ItemSaved[level];
					}
          return null;
				}
			}
			return null;
		}
	}

	/**
	 * walks through the list of Blocks (which are associated with SecEdges that
	 * have blocks). For each Block, it stimulates the SecEdge to tell all
	 * connected tracks to register themselves with the Block.
	 */
	public static void resolveBlocks() {
		for (Enumeration<Block> e = BlockKeeper.elements(); e.hasMoreElements();) {
			e.nextElement().Anchor.setBlock();
		}
	}
}

/**
 * is a Class known only to the Block class for creating Blocks from an XML
 * document.
 */
class BlockFactory implements XMLEleFactory {

	/**
	 * the Block's name in an attribute.
	 */
	private String AttrName;
  
  /**
   * the Block's Station name is an attribute.
   */
  private String AttrStation;

	/**
	 * the Block's discipline in an attribute.
	 */
	private int AttrDiscipline;

	/**
	 * the Block's visibility in an attribute.
	 */
	private boolean AttrVisible;

	/*
	 * tells the factory that an XMLEleObject is to be created. Thus, its
	 * contents can be set from the information in an XML Element description.
	 */
	public void newElement() {
		AttrName = null;
    AttrStation = null;
		AttrDiscipline = -1;
		AttrVisible = true;
	}

	/*
	 * gives the factory an initialization value for the created XMLEleObject.
	 * 
	 * @param tag is the name of the attribute. @param value is it value.
	 * 
	 * @return null if the tag:value are accepted; otherwise, an error string.
	 */
	public String addAttribute(String tag, String value) {
		String resultMsg = null;
		if (Block.NAME.equals(tag)) {
			AttrName = value;
    }
      else if (Block.STATION.equals(tag)) {
        AttrStation = value;
		} else if (Block.DISCIPLINE.equals(tag)) {
			int disp = Prop.findString(value, Block.DisciplineName);
			if (disp == Prop.NOT_FOUND) {
				resultMsg = new String(value + " is an invalid value for a "
						+ Block.XML_TAG + " XML element " + Block.DISCIPLINE
						+ " attribute.");
			} else {
				AttrDiscipline = disp;
			}
		} else if (Block.VISIBLE.equals(tag)) {
			if (Block.TRUE.equals(value)) {
				AttrVisible = true;
			} else if (Block.FALSE.equals(value)) {
				AttrVisible = false;
			} else {
				resultMsg = new String(value + " is an invalid value for a "
						+ Block.XML_TAG + " XML element " + Block.VISIBLE
						+ " attribute.");
			}
		} else {
			resultMsg = new String(Block.XML_TAG + " XML elements do not have "
					+ tag + " attributes.");
		}
		return resultMsg;
	}

	/*
	 * tells the factory that the attributes have been seen; therefore, return
	 * the XMLEleObject created.
	 * 
	 * @return the newly created XMLEleObject or null (if there was a problem in
	 * creating it).
	 */
	public XMLEleObject getObject() {
		Block newBlock = new Block(AttrName);
    newBlock.setStationName(AttrStation);
		newBlock.setDiscipline(AttrDiscipline);
		newBlock.setVisible(AttrVisible);
		return newBlock;
	}
}
/* @(#)Block.java */