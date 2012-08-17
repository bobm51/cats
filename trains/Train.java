/* Name: Train.java
 *
 * What:
 *   This class contains the information about a train.
 */
package cats.trains;

import cats.common.Constants;
import cats.common.Sides;
import cats.crew.*;
import cats.jmri.OperationsTrains;
import cats.layout.FontList;
import cats.layout.Logger;
import cats.gui.crew.CrewPicker;
import cats.gui.CtcFont;
import cats.gui.frills.FrillLoc;
import cats.gui.frills.TrainFrill;
import cats.gui.jCustom.FontFinder;
import cats.gui.jCustom.JListDialog;
import cats.gui.Screen;
import cats.gui.store.TimeSpec;
import cats.gui.TrainLabel;
import cats.layout.FastClock;
import cats.layout.items.SecEdge;
import cats.layout.items.Section;
import cats.layout.store.FieldPair;
import cats.layout.store.GenericRecord;
import cats.layout.store.StoredObject;
import cats.layout.xml.*;
import java.awt.event.MouseEvent;
import java.awt.event.KeyEvent;
import java.awt.Point;
import java.util.BitSet;
import java.util.Iterator;

/**
 *  This class contains the information about a train.
 * <p>Title: CATS - Computer Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2004, 2010, 2011</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */
public class Train
implements StoredObject, XMLEleObject {
	/**
	 * is the tag for identifying a Train in the XML file.
	 */
	static final String XML_TAG = "TRAIN";

	/**
	 * is the property tag for picking out the Train's name.
	 */
	public static final String TRAIN_NAME = "TRAIN_NAME";

	/**
	 * is the property tag for picking out the Train's identity.
	 */
	public static final String TRAIN_SYMBOL = "TRAIN_SYMBOL";

	/**
	 * is the property tag for picking out the lead engine.
	 */
	public static final String ENGINE = "ENGINE";

	/**
	 * is the property tag for picking out the transponding flag.
	 */
	public static final String TRANSPONDING = "TRANSPONDING";

	/**
	 * is the property tag for picking out the Caboose.
	 */
	public static final String CABOOSE = "CABOOSE";

	/**
	 * is the property tag for picking out the Crew.
	 */
	public static final String CREW = "CREW";

	/**
	 * is the property tag for the time the crew goes on duty.
	 */
	public static final String ONDUTY = "ONDUTY";

	/**
	 * is the property tag for the time the train departs.
	 */
	public static final String DEPARTURE = "DEPARTURE";

	/**
	 * is the property tag for the TrainStat Font of the train.
	 */
	public static final String FONT = "FONT";

	/**
	 * is the property tag for the length of the train
	 */
	public static final String LENGTH = "LENGTH";

	/**
	 * is the property tag for the weight of the train
	 */
	public static final String WEIGHT = "WEIGHT";
	
	/**
	 * is the property tag for the number of cars in the train
	 */
	public static final String CARS = "CARS";

	/**
	 * is the property tag for the working status of the train
	 */
	public static final String WORKING_STATUS = "STATUS";

	/**
	 * is the property tag on the hidden value for the Station
	 */
	private static final String STATION = "STATION";

	/**
	 * is the property tag on the hidden value for the train's Location
	 */
	private static final String LOCATION = "LOCATION";

	/**
	 * is the String used to identify a Train as unassigned.
	 */
	public static final String UNASSIGNED = "UNASSIGNED";

	/**
	 * is used to locate the fields that describe the Train.  The
	 * common.prop class has some useful utilities for pulling out
	 * entries.
	 */
	//  public static final String[] TRAIN_PROP = {
	//      TRAIN_NAME,
	//      TRAIN_SYMBOL,
	//      ENGINE,
	//      CABOOSE,
	//      CREW
	//  };

	/**
	 * is the pop up selection to change the crew on a Train.
	 */
	private static final String CHANGE_CREW = "Change Crew";

	/**
	 * is the pop up selection to tie down the Train.
	 */
	private static final String TIE_DOWN = "Tie Down Train";

	/**
	 * is the pop up selection to rerun the Train.
	 */
	private static final String RERUN_TRAIN = "Run Train again";

	/**
	 * is the pop up selection to remove the Train from the panel.
	 */
	private static final String REMOVE_TRAIN = "Remove Train";

	/**
	 * is for generating the pop up items.
	 */
	private static final String[] MenuItems = {
		TIE_DOWN,
		RERUN_TRAIN,
		REMOVE_TRAIN,
		CHANGE_CREW
	};

	/**
	 * Strings that describe the status of the train
	 */
	/**
	 * indicates that the Train has not run or is running
	 */
	public static final String TRAIN_ACTIVE = "TRAIN_ACTIVE";

	/**
	 * indicates that the Train has been tied down
	 */
	public static final String TRAIN_TIED_DOWN = "TRAIN_TIED_DOWN";

	/**
	 * indicates that the train has terminated and been removed
	 */
	public static final String TRAIN_TERMINATED = "TRAIN_TERMINATED";

	/**
	 * indicates that the train is being rerun
	 */
	public static final String TRAIN_RERUN = "TRAIN_RERUN";

	/*
	 * The following identify bits for selecting Trains.  They are not private
	 * because the Roster uses them for constructing selection masks.
	 */
	/**
	 * is set when the Train is created.
	 */
	static final int CREATED_BIT = 0;

	/**
	 * is set when the Train is Positioned on the layout.
	 */
	static final int POSITIONED_BIT = 1;

	/**
	 * is set when the Train has a crew assigned to it.  This bit could be
	 * set in conjunction with other bits.
	 */
	static final int ASSIGNED_BIT = 2;

	/**
	 * is set when the Train has the focus - will move in response to the
	 * keyboard.
	 */
	static final int FOCUS_BIT = 3;

	/**
	 * is set when the Train has completed its work, but still trips a
	 * detector on the layout.
	 */
	static final int TIEDUP_BIT = 4;

	/**
	 * is set when the Train has completed its work and is removed from
	 * the layout - is not detecting.
	 */
	static final int REMOVED_BIT = 5;

	/**
	 * is the number of bits used by the above.
	 */
	static final int STATE_BITS = 6;

	/**
	 * selects Trains in the Created State only.
	 */
	static BitSet Created;

	/**
	 * selects Trains in the Created and Positioned States only.
	 */
	static BitSet Unassigned;

	/**
	 * selects the Trains being run.
	 */
	static BitSet Active;

	/**
	 * selects the Trains that have completed their runs.
	 */
	static BitSet Done;

	/**
	 * selects Train in the Removed state only.
	 */
	static BitSet Removed;

	/**
	 * is the Crew assigned to the Train.
	 */
	private Crew MyCrew;

	/**
	 * is the lead engine on the train.
	 */
	private String LeadEngine;

	/**
	 * is the state of the Train.
	 */
	private BitSet TrainState;

	/**
	 *  is the Label showing the Train's identity on the screen.
	 */
	private TrainFrill TrainLab;

	/**
	 * is the Section where the Train is located.
	 */
	private Section Location;

	/**
	 * is the SecEdge that the train is sitting on.
	 */
	private SecEdge Side;

	/**
	 * is the name of the station where the train sits.
	 */
	private String Station;

	/**
	 * is the GenericRecord defining the attributes of a Train that can
	 * be read from an XML file or edited.
	 */
	private GenericRecord TrainFields;

	/**
	 * is the time that the crew went on duty.
	 */
	private int TimeOnDuty;

	/**
	 * constructs a Train, with a given set of initial values.
	 */
	public Train() {
		TrainState = new BitSet(STATE_BITS);
		TrainState.set(CREATED_BIT);
		TimeOnDuty = TimeSpec.UNKNOWN_TIME;
		LeadEngine = new String(" ");
	}

	/**
	 * is the method used to give an Object its description.
	 *
	 * @param description is the GenericRecord describing the Object.
	 */
	public void linkDescription(GenericRecord description) {
		String value;
		TrainFields = description;
		value = new String((String) description.findValue(CREW)).trim();
		if (!value.equals("")) {
			crewChange(value);
		}
		value = new String((String) description.findValue(ENGINE)).trim();
		if (!value.equals("")) {
			engineChange(value);
		}
		//    value = new String((String) description.findValue(LOCATION)).trim();
		//    if (!value.equals("")) {
		//      locationChange(value);
		//    }
		value = new String((String) description.findValue(GenericRecord.STATUS)).trim();
		if (!value.equals(GenericRecord.UNCHANGED)) {
			statusChange(value);
		}
	}

	/**
	 * is invoked to tell the Object that the description has been updated.
	 *
	 * @param description is the updated GenericRecord describing the Object.
	 */
	public void updateDescription(GenericRecord description) {
		String fieldTag;
		FieldPair newPair;
		FieldPair oldPair;
		GenericRecord broadcastRec = new GenericRecord();
		broadcastRec.add(TrainFields.findPair(TRAIN_SYMBOL));
		for (Iterator<FieldPair> iter = description.iterator(); iter.hasNext(); ) {
			newPair = iter.next();
			fieldTag = newPair.FieldTag;
			oldPair = TrainFields.findPair(fieldTag);
			if (oldPair != null) {
				if (!newPair.FieldValue.equals(oldPair.FieldValue)) {
					// need to keep the key for the change broadcast
					//        if (newPair.FieldValue.equals(oldPair.FieldValue)) {
					//          if (!TRAIN_SYMBOL.equals(fieldTag)) {
					//            iter.remove();
					//          }
					//        }
					//        else {
					if (CREW.equals(fieldTag)) {
						crewChange((String) newPair.FieldValue);
						broadcastRec.add(new FieldPair(ONDUTY, TimeSpec.convertMinutes(getOnDuty())));
					}
					else if (ENGINE.equals(fieldTag)) {
						engineChange((String) newPair.FieldValue);
					}
					else if (GenericRecord.STORED_OBJECT.equals(fieldTag)) {
						newPair.FieldValue = this;
					}
					TrainFields.replacePair(newPair);
					broadcastRec.add(newPair);
				}
			}
		}
		// the record must have more than the key field
		if (broadcastRec.size() > 1) {
			TrainStore.TrainKeeper.broadcastChange(broadcastRec);
		}
	}

	/**
	 * is invoked to tell the Object that it is being deleted.  It should
	 * clean up any associations it has with other Objects, then remove its
	 * description from its Store.
	 *
	 * For a Train, it is never called because Trains are never deleted.
	 */
	public void destructor() {
	}

	/**
	 * is invoked to request that the value of some field be changed.
	 * @param change is the FieldPair of the change.  The FieldPair tag identifies
	 * the field being changed.  The FieldPair value is the new value of the field.
	 */
	//  public void requestChange(FieldPair change) {
	//    String tag = change.FieldTag;
	//    if (tag.equals(CREW)) {
	//      crewChange(new String((String) change.FieldValue));
	//    }
	//    else if (tag.equals(ENGINE)) {
	//      engineChange(new String((String) change.FieldValue));
	//    }
	//    else if (tag.equals(LOCATION)) {
	//      locationChange(new String((String) change.FieldValue));
	//    }
	//    else if (tag.equals("STATUS")) {
	//      statusChange(new String((String) change.FieldValue));     
	//    }
	//    TrainFields.replaceValue(change.FieldTag, change.FieldValue);
	//  }

	/**
	 * returns the name of the Train.
	 *
	 * @return the name of the Train.
	 */
	public String getName() {
		return new String( (String) TrainFields.findValue(TRAIN_NAME));
	}

	/**
	 * changes the name of the Train.
	 *
	 * @param name is the new name.
	 */
	public void setName(String name) {
		TrainFields.replaceValue(TRAIN_NAME, name);
	}

	/**
	 * returns the timetable identity of the Train.
	 *
	 * @return the identity of the Train.
	 */
	public String getSymbol() {
		return new String( (String) TrainFields.findValue(TRAIN_SYMBOL));
	}

	/**
	 * returns the lead engine.
	 *
	 * @return the lead engine.
	 */
	public String getEngine() {
		return new String( (String) TrainFields.findValue(ENGINE));
	}

	/**
	 * sets the Engine number.
	 *
	 * @param engine is the engine number.  It can be null.
	 */
	public void setEngine(String engine) {
		TrainFields.replaceValue(ENGINE, new String(engine));
		LeadEngine = new String(engine);
		if (TrainLabel.TrainLabelType.getFlagValue()) {
			changeLabel(true);
		}
	}

	/**
	 * is called from the editor and network to change the lead
	 * locomotive
	 * @param newEngine is the sting that identifies the new engine.
	 * It can be null.
	 */
	public void engineChange(String newEngine) {
		if (!LeadEngine.equals(newEngine)) {
			setEngine(newEngine);
		}    
	}

	/**
	 * returns the Caboose number.
	 *
	 * @return the caboose decoder address.
	 */
	public String getCaboose() {
		return new String( (String) TrainFields.findValue(CABOOSE));
	}

	/**
	 * sets the caboose decoder address.
	 *
	 * @param caboose is the transponding decoder address of the caboose.
	 */
	public void setCaboose(String caboose) {
		TrainFields.replaceValue(CABOOSE, new String(caboose));
	}

	/**
	 * sets the time at which the crew went on duty.  It remembers the last
	 * time it was called.  If it had not been called before (this is the first
	 * crew assignment), then the train description may contain the time
	 * that the crew went on duty.  The description is used to account for
	 * "off layout transit time".  If the description field does not have
	 * an on duty time or a crew had been assigned to the train previously (this
	 * is a relief crew), then the time on duty is the current time.
	 */
	private void setOnDuty() {
		String crewTime;
		if (TimeOnDuty == TimeSpec.UNKNOWN_TIME) {
			crewTime = ( (String) TrainFields.findValue(ONDUTY)).trim();
			if (crewTime.length() == 0) {
				TimeOnDuty = TimeSpec.currentTime();
			}
			else {
				TimeOnDuty = TimeSpec.convertString(crewTime, getDeparture());
			}
		}
		else {
			TimeOnDuty = TimeSpec.currentTime();
		}
	}

	/**
	 * is called to find out when the crew went on duty.
	 *
	 * @return the number of minutes past midnight for when the crew went on duty
	 * on the train.  If crew has not been assigned to the Train,
	 * TimeSpec.UNKNOW_TIME is returned.
	 */
	public int getOnDuty() {
		return TimeOnDuty;
	}

	/**
	 * checks the ONDUTY field.  If it is relative, it is converted to absolute.
	 */
	public void adjustOnDuty() {
		String trainTime = getDeparture();
		String crewTime = ( (String) TrainFields.findValue(ONDUTY)).trim();
		if ( (crewTime.length() != 0) &&
				( (crewTime.charAt(0) == '-') || (crewTime.charAt(0) == '+'))
				&& (trainTime != null)) {
			TrainFields.replaceValue(ONDUTY,
					TimeSpec.convertMinutes(TimeSpec.convertString(
							crewTime, trainTime)));
		}
	}

	/**
	 * is called to get the scheduled departure time.  The value returned will
	 * be null (meaning that there is no scheduled departure time) or an absolute
	 * TimeSpec.  Though the departure time may have been read as a relative
	 * time, this method will convert it to absolute (using the operating session
	 * start time as a base) and store it in the DEPARTURE field.  Thus, the
	 * first call to this method anchors the scheduled departure time.
	 *
	 * @return null or the absolute departure time.
	 */
	public String getDeparture() {
		String trainTime = ( (String) TrainFields.findValue(DEPARTURE)).trim();
		if (trainTime.length() == 0) {
			trainTime = null;
		}
		else if ( (trainTime.charAt(0) == '+') || (trainTime.charAt(0) == '-')) {
			trainTime = TimeSpec.convertMinutes(TimeSpec.convertString(trainTime,
					FastClock.TheClock.getStartTime()));
			TrainFields.replaceValue(DEPARTURE, trainTime);
		}
		return trainTime;
	}

	/** constructs an array of Strings, which each element containing
	 * one of the Strings describing the Train.
	 * @return a Vector containing information about the Train
	 */
	//  public String[] getInfo() {
	//    String info[] = new String[TRAIN_PROP.length];
	//
	//    /* The following must match the order in TRAIN_PROP */
	//    info[0] = new String(getName());
	//    info[1] = new String(getSymbol());
	//    info[2] = new String(getEngine());
	//    info[3] = new String(getCaboose());
	//    if (MyCrew != null) {
	//      info[4] = MyCrew.getCrewName();
	//    }
	//    else {
	//      info[4] = UNASSIGNED;
	//    }
	//    return info;
	//  }

	/**
	 * assigns the Crew to the Train.  It does not unassign any current
	 * crew assignment.  It just sets up the assignment.  It does not make
	 * any method calls to a crew.  Thus, the caller is responsible for
	 * unassigning any crew and registering the train with the new crew.
	 *
	 * @param crew is the crew.  It could be null.
	 *
	 * @see cats.crew.Crew
	 */
	public void setCrew(Crew crew) {
		FieldPair pair = TrainFields.findPair(CREW);
		GenericRecord broadcastRec = new GenericRecord();
		broadcastRec.add(TrainFields.findPair(TRAIN_SYMBOL));
		MyCrew = crew;
		if (MyCrew == null) {
			TrainState.clear(ASSIGNED_BIT);
			if (TrainState.get(FOCUS_BIT)) {
				TrainState.clear(FOCUS_BIT);
				TrainStore.TrainKeeper.clrFocus();
			}
			pair.FieldValue = "";
			TimeOnDuty = TimeSpec.UNKNOWN_TIME;
		}
		else {
			TrainState.set(ASSIGNED_BIT);
			pair.FieldValue = MyCrew.getCrewName();
			setOnDuty();
		}
		setLabel();
		if (Location != null) { // see if the Train has been placed on the layout
			Location.getTile().requestUpdate();
		}
		broadcastRec.add(pair);
		pair = TrainFields.findPair(ONDUTY);
		pair.FieldValue = TimeSpec.convertMinutes(getOnDuty());
		broadcastRec.add(pair);
		TrainStore.TrainKeeper.broadcastChange(broadcastRec);    
	}

	/**
	 * replaces the Crew that is assigned to the Train.  This is different
	 * from setCrew() in that if a Crew is currently assigned to the Train,
	 * then setTrain(null) is called on that Crew, to clear the existing
	 * assignment.
	 *
	 * @param crew is the new crew assignment.  It could be null.
	 *
	 * @see cats.crew.Crew
	 */
	public void replaceCrew(Crew crew) {
		if (MyCrew != null) {
			MyCrew.setTrain(null);
		}
		setCrew(crew);
		if (crew != null) {
			if (crew.getTrain() != null) {
				crew.getTrain().setCrew(null);
			}
			crew.setTrain(this);
		}
	}

	/**
	 * is called from the editor or network to change the crew
	 * running the Train.
	 * @param crewName is the name of the new Crew (which could be empty or
	 * null)
	 */
	public void crewChange(String crewName) {
		Crew newCrew = Callboard.Crews.findCrew(crewName);
		if ( (newCrew != null) && !newCrew.getExtraFlag()) {
			newCrew = null;
		}
		if (MyCrew != newCrew) {
			replaceCrew(newCrew);
		}   
	}

	/**
	 * returns the Crew assigned to the Train.
	 *
	 * @return the Crew.  It could be null.
	 *
	 * @see cats.crew.Crew
	 */
	public Crew getCrew() {
		return MyCrew;
	}

	/**
	 * set the Transponding flag.
	 *
	 * @param trans is true if the lead locomotive has transponding and
	 * false if it does not.
	 */
	public void setTransponding(Boolean trans) {
		TrainFields.replaceValue(TRANSPONDING, trans);
	}

	/**
	 * returns the Transponding flag.
	 *
	 * @return true if the lead locomotive reports its engine number and
	 * false if it doesn't.
	 */
	public Boolean getTransponding() {
		return (Boolean) TrainFields.findValue(TRANSPONDING);
	}

	/**
	 * is a predicate for asking if a Train is in a certain state.
	 *
	 * @param selector is the Train state being searched for.  If any one
	 * of the bits in the BitSet are set, then it matches.
	 *
	 * @param filter is the inverse of selector.  If any one of the bits
	 * in the BitSet are set, then it is rejected.
	 *
	 * @return a Vector containing all Trains whose state is selector.
	 */
	boolean selectTrain(BitSet selector, BitSet filter) {
		if ( ( (selector == null) || (TrainState.intersects(selector))) &&
				( (filter == null) || ! (TrainState.intersects(filter)))) {
			return true;
		}
		return false;
	}

	/**
	 * is a query to ask if the Train has not been removed.
	 *
	 * @return true if the Train has not been positioned or is still on
	 * the layout.
	 */
	public boolean isNotRemoved() {
		return selectTrain(null, Removed);
	}

	/**
	 * is a query to determine if a Train has completed its run.
	 *
	 * @return true if it has been terminated or tied up.
	 */
	public boolean hasRun() {
		return selectTrain(Done, null);
	}

	/**
	 * returns the TrainLabel so that the Section can add it to the
	 * GridTile.
	 *
	 * @return TrainLab
	 */
	public TrainFrill getIcon() {
		return TrainLab;
	}

	/**
	 * moves a Train to the Tiedup state.  This method should be called
	 * when the Train has completed its work, but still occupies a Block
	 * on the layout.
	 */
	public void tieDown() {
		logState(Constants.TIEDDOWN_TAG);
		replaceCrew(null);
		if (TrainState.get(POSITIONED_BIT)) {
			//      if (MyCrew != null) {
			//        MyCrew.setTrain(null);
			//        MyCrew = null;
			//      }
			//      TimeOnDuty = TimeSpec.UNKNOWN_TIME;
			TrainState.clear();
			TrainState.set(TIEDUP_BIT);
			TrainState.set(POSITIONED_BIT);
			setLabel();
			Location.getTile().requestUpdate();
		}
		else {
			TrainState.clear();
			TrainState.set(TIEDUP_BIT);
		}
		if (TrainFields != null) {
			TrainFields.setStatus(Train.TRAIN_TIED_DOWN);      
		}
		OperationsTrains.instance().terminateTrain(getSymbol());
	}

	/**
	 * moves a Train to the Removed state.  This method should be called
	 * only if the Train is no longer on the Layout.
	 */
	public void remove() {
		//    FieldPair pair = TrainFields.findPair(CREW);
		logState(Constants.TERMINATED_TAG);
		replaceCrew(null);
		//    if (MyCrew != null) {
		//      MyCrew.setTrain(null);
		//      MyCrew = null;
		//      pair.FieldValue = "";
		//      if (TrainState.get(FOCUS_BIT)) {
		//        TrainStore.TrainKeeper.clrFocus();
		//      }
		//    }
		//    TimeOnDuty = TimeSpec.UNKNOWN_TIME;
		TrainState.clear();
		TrainState.set(REMOVED_BIT);
		if (Location != null) {
			Location.addTrain(null);
			Location = null;
			Station = null;
		}
		if (TrainLab != null) {
			TrainLab.removeFrill();
		}
		if (TrainFields != null) {
			TrainFields.setStatus(Train.TRAIN_TERMINATED);
		}
		TrainLab = null;
		OperationsTrains.instance().terminateTrain(getSymbol());
	}

	/**
	 * moves Train from Removed or Tied Up State to the Created state.
	 */
	public void rerun() {
		TrainState.clear(TIEDUP_BIT);
		TrainState.clear(REMOVED_BIT);
		if (!TrainState.get(POSITIONED_BIT)) {
			TrainState.set(CREATED_BIT);
		}
		if (TrainFields != null) {
			TrainFields.setStatus(GenericRecord.UNCHANGED);
		}
		setLabel();
		//    Logger.timeStamp(Logger.RERUN_TAG, Logger.FS + getSymbol());
		//    TrainStore.TrainKeeper.broadcastUpdate(Logger.RERUN_TAG, Logger.FS + getSymbol());
		//    TrainStore.TrainKeeper.broadcastTimestamp(Logger.RERUN_TAG, Logger.FS + getSymbol());
		logState(Constants.RERUN_TAG);
	}

	/**
	 * is called from the gui or network to change the status of a Train
	 * @param status is a String identifying the Trains's new status
	 */
	public void statusChange(String status) {
		if (null != status) {
			if (status.equals(Constants.TERMINATED_STATE)) {
				remove();
			}
			else if (status.equals(Constants.TIED_DOWN_STATE)) {
				tieDown();
			}
			else if (status.equals(Constants.WAITING_STATE)) {
				rerun();
			}
		}
	}

	/**
	 * prints some key properties of the Train.
	 * 
	 * @param logTag is the tag to use in the log file
	 */
	private void logState(String logTag) {
		String info = new String(Constants.QUOTE + getSymbol() + Constants.QUOTE + 
				Constants.FS + TrainState.toString() + Constants.FS + Constants.QUOTE);
		if (Location == null) {
			info = info.concat("unknown" + Constants.QUOTE + Constants.FS);
		}
		else {
			info = info.concat(Logger.formatLocation(Location.getCoordinates(), null) + Constants.QUOTE +
					Constants.FS + Location.hasTrain() + Constants.FS);
		}
		if (TrainLab == null) {
			info = info.concat("label lost");
		}
		else {
			info = info.concat("label exists");
		}
		//    Logger.timeStamp(logTag, info);
		//    TrainStore.TrainKeeper.broadcastUpdate(logTag, info);
		TrainStore.TrainKeeper.broadcastTimestamp(logTag, info);
	}

	/**
	 * is a query for if the Train is working.
	 *
	 * @return true if the Train has a crew assigned and is positioned on the
	 * layout; otherwise, return false.
	 */
	public boolean isActive() {
		return (TrainState.get(ASSIGNED_BIT) && TrainState.get(POSITIONED_BIT));
	}

	/**
	 * tells the Train to set its color to indicate if the arrow keys move it
	 * or not.
	 *
	 * @param moves is true if it gets the arrow keys and false if it does not.
	 */
	public void setFocus(boolean moves) {
		if (moves) {
			TrainState.set(FOCUS_BIT);
			setLabel();
			Location.getTile().requestUpdate();
		}
		else {
			TrainState.clear(FOCUS_BIT);
			if (isActive()) {
				setLabel();
				Location.getTile().requestUpdate();
			}
		}
	}

	/**
	 * returns a copy of itself.
	 *
	 * @return an identical copy.
	 */
	//  public Train copy() {
	//    Train dup = new Train(getName(), getSymbol(), getEngine(), getCaboose());
	//    dup.Transponding = Transponding;
	//    return dup;
	//  }

	/**
	 * is called when the dispatcher presses a mouse button
	 * on top of the Train's label.  If the left button is used, then the
	 * label can be moved.  If the right button is used, then a popup menu
	 * is generated.
	 *
	 * @param me is the MouseEvent recording the button push/release.
	 *
	 * @see java.awt.event.MouseEvent
	 */
	public void trainButton(MouseEvent me) {
		int mods = me.getModifiers();
		if ( (mods & MouseEvent.BUTTON3_MASK) != 0) {
			TrainStore.TrainKeeper.singleTrainEdit(this);
		}
		else if ((mods & MouseEvent.BUTTON2_MASK) != 0) {
			trainMenu(me.getPoint());      
		}
		else if ( (mods & MouseEvent.BUTTON1_MASK) != 0) {
			moveTrain(Screen.DispatcherPanel.locatePt(me.getPoint()));
		}
	}

	/**
	 * presents the Menu of operations on the train.
	 *
	 * @param location is the location of the Mouse on the screen.
	 *
	 * @see cats.gui.jCustom.JListDialog
	 */
	public void trainMenu(Point location) {
		String menu[];
		Crew newCrew;
		int selection;
		if (hasRun()) {
			menu = new String[2];
		}
		else {
			menu = new String[3];
			menu[2] = MenuItems[3];
		}
		if (TrainState.get(TIEDUP_BIT)) {
			menu[0] = MenuItems[1];
		}
		else {
			menu[0] = MenuItems[0];
		}
		menu[1] = MenuItems[2];

		if ( (selection = JListDialog.select(setToolTip(), menu, "Train Operation",
				location))
				>= 0) {

			switch (CtcFont.findString(menu[selection], MenuItems)) {
			case 0: // Tie Down was selected
				tieDown();
				break;

			case 1: // Rerun Train was selected.
				rerun();
				break;

			case 2: // Remove Train was selected
				remove();
				break;

			default: // Change crew was selected
				newCrew = CrewPicker.pickOne(Callboard.Crews.getExtras(),
						"Crew Change", location);
				if (newCrew != null) {
					replaceCrew(newCrew);
				}
			}
		}
	}

	/**
	 * selects what to use for the label on the panel.
	 * 
	 * @param useEngine is true to use the train's symbol
	 * and false to use the engine.
	 */
	protected void changeLabel(boolean useEngine) {
		if (TrainLab != null) {
			String engine = getEngine();
			if (useEngine && (engine != null) && (engine.length() != 0)) {
				TrainLab.changeId(engine);
			}
			else {
				TrainLab.changeId(getSymbol());
			}
			if (Location != null) {
				Location.getTile().requestUpdate();
			}
		}
	}

	/**
	 * sets the text, forground color, and font size of the JLabel.
	 */
	private void setLabel() {
		String font;
		if (TrainLab != null) {
			if (TrainState.get(TIEDUP_BIT)) {
				font = FontList.TIEDUP;
			}
			else if (TrainState.get(FOCUS_BIT)) {
				font = FontList.SELECTED;
			}
			else if (TrainState.get(ASSIGNED_BIT)) {
				font = FontList.ACTIVE;
			}
			else {
				font = FontList.ONCALL;
			}
			TrainLab.setPalette(font);
			//      TrainLab.setFont(FontFactory.Fonts.findFont(FontFactory.FONT_TRAIN).grabFont());
		}
	}

	/**
	 * sets up the ToolTip that contains the Train's name and crew
	 *
	 * This sets the tool tip, but hides TrainLabels from mouse actions,
	 * such as button pushes and drag.
	 *
	 * @return the Strings containing information about the Train
	 */
	private String[] setToolTip() {
		String[] tip = new String[3];
		String s;
		if (TrainLab != null) {
			if (getName() != null) {
				tip[0] = new String(getName());
			}
			else {
				tip[0] = "";
			}
			if (getEngine() != null) {
				tip[1] = "engine is " + getEngine();
			}
			else {
				tip[1] = "";
			}
			if ( (MyCrew != null) && ( (s = MyCrew.getCrewName()) != null)) {
				tip[2] = "crew is " + s;
			}
			else {
				tip[2] = "";
			}
			//      TrainLab.addTip(tip);
		}
		return tip;
	}

	/**
	 * positions the Train as a result of selecting a location in a Block
	 * menu.
	 *
	 * @param location are the coordinates on the screen of the mouse at
	 * the time the right button was pushed.
	 *
	 * @see java.awt.Point
	 */
	public void positionTrain(Point location) {
		Section section = Screen.DispatcherPanel.locatePt(location);
		String label = getEngine();
		if (!TrainLabel.TrainLabelType.getFlagValue() ||
				(label == null) || (label.length() == 0)) {
			label = getSymbol();
		}
		TrainLab = new TrainFrill(label, FrillLoc.newFrillLoc("UPCENT"),
				new FontFinder(FontList.ONCALL, FontList.FONT_TRAIN));
		setLabel();
		TrainLab.setOwner(this);
		if (section != null) {
			Side = null;
			Location = section;
			Station = Location.addTrain(this);
			TrainState.clear(CREATED_BIT);
			TrainState.set(POSITIONED_BIT);
			recordMove(null, null, Side, Location);
		}
	}

	/**
	 * moves the Train to the next Block in the direction of an arrow key.
	 *
	 * @param arrow is an arrow key.
	 *
	 * @see java.awt.event.KeyEvent
	 */
	public void moveTrain(int arrow) {
		int direction;
		SecEdge exitEdge;
		SecEdge enterEdge;
		Section newLoc = Location;
		switch (arrow) {
		case KeyEvent.VK_RIGHT:
			direction = Sides.RIGHT;
			break;

		case KeyEvent.VK_LEFT:
			direction = Sides.LEFT;
			break;

		case KeyEvent.VK_DOWN:
			direction = Sides.BOTTOM;
			break;

		case KeyEvent.VK_UP:
			direction = Sides.TOP;
			break;

		default:
			direction = -1;
		}
		if (direction >= 0) {
			if ( (exitEdge = Location.getPath(Side, direction)) != null) {
				if ( (enterEdge = exitEdge.getNeighbor()) != null) {
					while ( (enterEdge != null) && !enterEdge.getSection().hasTrain()) {
						newLoc = enterEdge.getSection();
						if ( ( (exitEdge = enterEdge.traverse()) == null) ||
								exitEdge.isBlock()) {
							break;
						}
						enterEdge = exitEdge.getNeighbor();
					}
					if ( (Location != newLoc) && !newLoc.hasTrain() && (exitEdge != null)) {
						advanceTrain(enterEdge);
					}
				}
			}
		}
	}

	/**
	 * moves the Train to a new Section.
	 *
	 * @param sec is the new Section.  If null, nothing happens.
	 */
	public void moveTrain(Section sec) {
		if (sec != null) {
			recordMove(Side, Location, null, sec);
			if (Location != null) {
				Location.addTrain(null);
				Location = sec;
				Side = null;
				Station = Location.addTrain(this);
			}
		}
	}

	/**
	 * moves the train to a new Section.
	 * 
	 * @param where is a String created by the Logger (@see cats.layout.Logger.formatLocation().
	 * 
	 * @return null if the String parsed without error or a String describing the error
	 */
	public String moveTrain(String where) {
		int x;
		int xpos;
		int y;
		int ypos;
		Section sec;
		if (where.charAt(0) == '(') {
			xpos = where.indexOf(',');
			if (xpos > 1) {
				x = Integer.parseInt(where.substring(1, xpos));
				ypos = where.indexOf(')');
				if (ypos > (xpos + 1)) {
					y = Integer.parseInt(where.substring(xpos + 1, ypos));
					sec = Screen.DispatcherPanel.locateSection(x, y);
					if (sec != null) {                         
						// positionTrain must be called the first time the Train
						// is placed on the layout to initialize the Train's internal
						// data
						if (getIcon() == null) {
							// There seems to be a bug in Screen.locatePt() in which
							// the upper left corner of a Section is not found.
							Point p = sec.getTile().getClip().getLocation();
							++p.x;
							++p.y;
							positionTrain(p);
						}
						else {
							moveTrain(sec);
						}
						if ((where.length() > (ypos + 1)) && (where.charAt(ypos + 1) == ':')) {
							advanceTrain(sec.getEdge(Integer.parseInt(where.substring(ypos + 2))));
						}
						return null;
					}
					return "unknown location: (" + x + "," + y + ")";
				}
			}
		}
		return "badly formatted train location";
	}

	/**
	 * advances the train to a SecEdge.
	 *
	 * @param edge is the SecEdge through which the train entered the
	 * Section.
	 */
	public void advanceTrain(SecEdge edge) {
		if (edge != null) {
			recordMove(Side, Location, edge, edge.getSection());
			if (Location != null) {
				Location.addTrain(null);
				Location = edge.getSection();
				Side = edge;
				Station = Location.addTrain(this);
			}
		}
	}

	//  /**
	//   * is called from the editor or network to update a Train's
	//   * location
	//   * @param location is a String describing the Train's location.
	//   */
	//  public void locationChange(String location) {
	//    if (null != location) {
	//      moveTrain(location);
	//    }    
	//  }

	/**
	 * records a Train's movement.  There are two sets of parameters for
	 * both of the location the train departs from and the location the
	 * train arrives at.  One of the parameters is the SecEdge the
	 * train is on.  If it is not null, it is used for obtaining a location
	 * description.  If the SecEdge is null, then the method looks at the
	 * other parameter, the Section.  If both are null, then the string
	 * "unknown" is used.
	 *
	 * @param departure is the SecEdge the train begins in (it can be null).
	 * @param depart is the Section the train departs from (it can be null).
	 *    recordMove looks at it, if departure is null.
	 * @param arrival is the SecEdge the train ends up in (it can be null).
	 * @param arrive is the Section the train ends up in (it can be null).
	 *    recordMove looks at it if arrival is null.
	 *
	 */
	private void recordMove(SecEdge departure, Section depart, SecEdge arrival,
			Section arrive) {
		String movement;
		Point dest = null;
		String destEdge = null;
		String station;

		movement = new String(Constants.QUOTE + getSymbol() + Constants.QUOTE_FS);
		if ( (departure != null) && ( (station = departure.identify()) != null)) {
			movement = movement.concat(station);
		}
		else if (depart != null) {
			station = depart.getStation();
			if (station == null) {
				movement = movement.concat(depart.toString());
			}
			else {
				movement = movement.concat(station);
			}
		}
		else {
			movement = movement.concat(Constants.UNKNOWN);
		}
		movement = movement.concat(Constants.QUOTE + Constants.FS_STRING + "to" + Constants.FS + Constants.QUOTE);
		if ( (arrival != null) && ( (station = arrival.identify()) != null)) {
			movement = movement.concat(station);
			dest = arrival.getSection().getCoordinates();
			destEdge = String.valueOf(arrival.getEdge());
			OperationsTrains.instance().moveTrain(getSymbol(), station);
		}
		else if (arrive != null) {
			station = arrive.getStation();
			if (station == null) {
				movement = movement.concat(arrive.toString());
			}
			else {
				movement = movement.concat(station);
				OperationsTrains.instance().moveTrain(getSymbol(), station);
			}
			dest = arrive.getCoordinates();
		}
		else {
			movement = movement.concat(Constants.UNKNOWN);
		}
		if (dest == null) {
			movement = movement.concat(Constants.QUOTE);
		}
		else {
			movement = movement.concat(Constants.QUOTE_FS + Logger.formatLocation(dest, destEdge) + Constants.QUOTE);
		}
		//    Logger.timeStamp(Logger.MOVE_TAG, movement);
		//    TrainStore.TrainKeeper.broadcastUpdate(Logger.MOVE_TAG, movement);
		TrainStore.TrainKeeper.broadcastTimestamp(Constants.MOVE_TAG, movement);
	}

	//  /**
	//   * is a method for reading in an XML file containing a lineup of
	//   * Trains.
	//   *
	//   * @param lineUp is the XML file to be read.
	//   */
	//  static public void readTrains(File lineUp) {
	//    String errReport;
	//    if (lineUp.exists() && lineUp.canRead()) {
	//      errReport = XMLReader.parseDocument(lineUp);
	//      if (errReport != null) {
	//        JOptionPane.showMessageDialog( (Component)null,
	//                                      errReport,
	//                                      "XML Error",
	//                                      JOptionPane.ERROR_MESSAGE);
	//      }
	//      else {
	//        // There is nothing to do if there is no error because XMLReader
	//        // did all the work
	//      }
	//    }
	//    else {
	//      JOptionPane.showMessageDialog( (Component)null,
	//                                    lineUp + " does not exist",
	//                                    "Missing Description",
	//                                    JOptionPane.ERROR_MESSAGE);
	//    }
	//  }

	/*
	 * is the method through which the object receives the text field.
	 *
	 * @param eleValue is the Text for the Element's value.
	 *
	 * @return if the value is acceptable, then null; otherwise, an error
	 * string.
	 */
	public String setValue(String eleValue) {
		return new String("A " + XML_TAG + " cannot contain a text field ("
				+ eleValue + ").");
	}

	/*
	 * is the method through which the object receives embedded Objects.
	 *
	 * @param objName is the name of the embedded object
	 * @param objValue is the value of the embedded object
	 *
	 * @return null if the Object is acceptible or an error String
	 * if it is not.
	 */
	public String setObject(String objName, Object objValue) {
		return new String("A " + XML_TAG + " cannot contain an Element ("
				+ objName + ").");
	}

	/*
	 * returns the XML Element tag for the XMLEleObject.
	 *
	 * @return the name by which XMLReader knows the XMLEleObject (the
	 * Element tag).
	 */
	public String getTag() {
		return new String(XML_TAG);
	}

	/*
	 * tells the XMLEleObject that no more setValue or setObject calls will
	 * be made; thus, it can do any error chacking that it needs.
	 *
	 * @return null, if it has received everything it needs or an error
	 * string if something isn't correct.
	 */
	public String doneXML() {
		return null;
	}

	/**
	 * registers a TrainFactory with the XMLReader.
	 */
	static public void init() {
		//    XMLReader.registerFactory(XML_TAG, new TrainFactory());
		Created = new BitSet(Train.STATE_BITS);
		Created.set(Train.CREATED_BIT);
		Unassigned = new BitSet(Train.STATE_BITS);
		Unassigned.set(Train.CREATED_BIT);
		Unassigned.set(Train.POSITIONED_BIT);
		Active = new BitSet(Train.STATE_BITS);
		Active.set(Train.POSITIONED_BIT);
		Done = new BitSet(Train.STATE_BITS);
		Done.set(Train.TIEDUP_BIT);
		Done.set(Train.REMOVED_BIT);
		Removed = new BitSet(Train.STATE_BITS);
		Removed.set(Train.REMOVED_BIT);
	}

	//  /**
	//   * is invoked to retrieve the field tag of the unique field for
	//   * the Generic Record describing the object
	//   * 
	//   * @return the tag of the unique field
	//   */
	//  public String getUniqueFieldTag() {
	//    return TRAIN_SYMBOL;
	//  }

	/**
	 * is invoked to return the working status of the train.
	 * @return
	 * <ul>
	 * <li> TERMINATED_STATE
	 * <li> TIED_DOWN_STATE
	 * <li> WORKING_STATE
	 * <li> WAITING_STATE
	 * </ul>
	 */
	public String getTrainStatus() {
		String status;
		if (TrainState.get(REMOVED_BIT)) {
			status = Constants.TERMINATED_STATE;
		}
		else if (TrainState.get(TIEDUP_BIT)) {
			status = Constants.TIED_DOWN_STATE;
		}
		else if (TrainState.get(ASSIGNED_BIT)) {
			status = Constants.WORKING_STATE;
		}
		else {
			status = Constants.WAITING_STATE;
		}
		return status;
	}

	/**
	 * is invoked to retrieve internal values, formatted in a String
	 * as tag=value subStrings.  The derived calss decides which
	 * values (and tags) it wants to expose.
	 * @return a String containing "tag=value" substrings.
	 */
	public String getHiddenValues() {
		String status = getTrainStatus();
		String contents;
		contents = new String(WORKING_STATUS + FieldPair.SEP + Constants.QUOTE + status + Constants.QUOTE);
		if (MyCrew != null) {
			contents = contents.concat(Constants.FS_STRING + CREW + FieldPair.SEP + Constants.QUOTE +
					MyCrew.getCrewName() + Constants.QUOTE);
		}
		if (Station != null) {
			contents = contents.concat(Constants.FS_STRING + STATION + FieldPair.SEP + Constants.QUOTE +
					Station + Constants.QUOTE);
		}
		if (Side != null) {
			contents = contents.concat(Constants.FS_STRING + LOCATION + FieldPair.SEP + Constants.QUOTE +
					Logger.formatLocation(Side.getSection().getCoordinates(), String.valueOf(Side.getEdge()))
					+ Constants.QUOTE);
		}
		else if (Location != null) {
			contents = contents.concat(Constants.FS_STRING + LOCATION + FieldPair.SEP + Constants.QUOTE +
					Logger.formatLocation(Location.getCoordinates(), null) + Constants.QUOTE);     
		}
		return contents;
	}
}

/**
 * is a Class known only to the Train class for creating
 * Trains from an XML document.
 */
//class TrainFactory
//    implements XMLEleFactory {
//
//  private GenericRecord newTrain;
//  /*
//   * tells the factory that an XMLEleObject is to be created.  Thus,
//   * its contents can be set from the information in an XML Element
//   * description.
//   */
//  public void newElement() {
//    newTrain = new GenericRecord(GenericRecord.DATARECORD, 5);
//  }
//
//  /*
//   * gives the factory an initialization value for the created XMLEleObject.
//   *
//   * @param tag is the name of the attribute.
//   * @param value is it value.
//   *
//   * @return null if the tag:value are accepted; otherwise, an error
//   * string.
//   */
//  public String addAttribute(String tag, String value) {
//    String resultMsg = null;
//    if (Train.TRAIN_NAME.equals(tag)) {
//      newTrain.replaceValue(Train.TRAIN_NAME, new String(value));
//    }
//    else if (Train.TRAIN_SYMBOL.equals(tag)) {
//      newTrain.replaceValue(Train.TRAIN_SYMBOL, new String(value));
//    }
//    else if (Train.ENGINE.equals(tag)) {
//      newTrain.replaceValue(Train.ENGINE, new String(value));
//    }
//    else if (Train.CABOOSE.equals(tag)) {
//      newTrain.replaceValue(Train.CABOOSE, new String(value));
//    }
//    else if (Train.TRANSPONDING.equals(tag)) {
//      newTrain.replaceValue(Train.TRANSPONDING, new String(value));
//    }
//    else {
//      resultMsg = new String(tag +
//                             " is not recognized as an XML attribute of a "
//                             + Train.XML_TAG + ".");
//    }
//    return resultMsg;
//  }
//
//  /*
//   * tells the factory that the attributes have been seen; therefore,
//   * return the XMLEleObject created.
//   *
//   * @return the newly created XMLEleObject or null (if there was a problem
//   * in creating it).
//   */
//  public XMLEleObject getObject() {
//    if (newTrain.findPair(Train.TRAIN_SYMBOL) == null) {
//      System.out.println("Missing " + Train.XML_TAG +
//                         Train.TRAIN_SYMBOL + " attribute.");
//      return null;
//    }
//    TrainStore.TrainKeeper.addRecord(GenericRecord.DATARECORD, newTrain);
//    return newTrain;
//  }
//}
/* @(#)Train.java */
