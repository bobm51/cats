/* Name: Crew.java
 *
 * What:
 *   This class conatins the information about a Crew member.
 */
package cats.crew;

import java.util.Iterator;

import cats.common.Constants;
import cats.gui.store.TimeSpec;
import cats.jobs.Job;
import cats.layout.store.FieldPair;
import cats.layout.store.GenericRecord;
import cats.layout.store.StoredObject;
import cats.trains.Train;
import cats.trains.TrainStore;

/**
 * This class contains the information about a Crew member.  Though based on using
 * an AbstractStore for the edit support, it also has legacy code for reading
 * in the older crew lists.
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2004, 2010, 2011</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */
public class Crew
    implements StoredObject {
  /**
   * is the tag for identifying a Crew description in the XMl file.
   */
  static final String XML_TAG = "CREW";

  /**
   * is the property tag for picking out the Crew's name.
   */
  static final String CREW_NAME = "CREW_NAME";

  /**
   * is the property tag for identifying when the crew went on duty.
   */
  static final String TIME_ON_DUTY = "TIME_ON";

  /**
   * is the property tag for identifying the amount of time left before going
   * dead on hours field.
   */
  static final String TIME_LEFT = "TIME_LEFT";

  /**
       * is the property tag for identifying the time at which dead on hours happens.
   */
  static final String EXPIRES = "EXPIRES";

  /**
   * is the property tag for identifying the Train the crew is assigned to.
   */
  static final String TRAIN_ID = "TRAIN_ID";
  
  /**
   * is the property tag for identifying the font for showing the crew
   */
  static final String FONT = "FONT";

  /**
   * is used to locate the fields that describe the Crew entry.  The
   * common.prop class has some useful utilities for pulling out
   * entries.
   */
  protected static String[] CREW_PROPS = {
      CREW_NAME,
      TIME_ON_DUTY,
      TIME_LEFT,
      EXPIRES,
      TRAIN_ID,
      FONT
  };
  /**
   * is the Train that the Crew has been assigned to operate.
   */
  private Train Assignment;

  /**
   * is the name of the Job the crew has been assigned to.
   */
  private Job JobAssignment;

  /**
   * is a GenericRecord describing the Crew.
   */
  private GenericRecord CrewFields;

  /**
   * is the number of minutes after midnight that the crew picked up the train.
   */
  private int StartTime;

  /**
   * constructs a Crew, with a given set of initial values.
   */
  public Crew() {
    StartTime = TimeSpec.UNKNOWN_TIME;
  }

  /**
   * is the method used to give an Object its description.
   *
   * @param description is the GenericRecord describing the Object.
   */
  public void linkDescription(GenericRecord description) {
    CrewFields = description;
    String trainName = new String((String) description.findValue(TRAIN_ID)).trim();
    if (!trainName.equals("")) {
      trainChange(trainName);
    }
    // should look at start time, as well.  What type is it?
  }

  /**
   * is invoked to tell the Object that it is being deleted.  It should
   * clean up any associations it has with other Objects, then remove it
   * description from its Store.
   */
  public void destructor() {
    Job temp;
    trainChange(null);
    if ((null != JobAssignment) && (JobAssignment.getCrewName().equals(getCrewName()))) {
      temp = JobAssignment;
      JobAssignment = null;
      temp.setCrew("");
    }
    Callboard.Crews.delRecord(CrewFields);
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
    broadcastRec.add(description.findPair(CREW_NAME));
    for (Iterator<FieldPair> iter = description.iterator(); iter.hasNext(); ) {
      newPair = iter.next();
      fieldTag = newPair.FieldTag;
      oldPair = CrewFields.findPair(fieldTag);
      if (oldPair != null) {
        if (!newPair.FieldValue.equals(oldPair.FieldValue)) {
//        if (newPair.FieldValue.equals(oldPair.FieldValue)) {
//          // need to keep the key for the change broadcast
//          if (CREW_NAME.equals(fieldTag)) {
//            iter.remove();
//          }
//        }
//        else {
          if (TRAIN_ID.equals(fieldTag)) {
            trainChange((String) newPair.FieldValue);
            if (description.findPair(TIME_ON_DUTY) == null) {
              broadcastRec.add(new FieldPair(TIME_ON_DUTY, TimeSpec.convertMinutes(StartTime)));
            }
          }
          else if (GenericRecord.STORED_OBJECT.equals(fieldTag)) {
            newPair.FieldValue = this;
          }
          CrewFields.replacePair(newPair);
          broadcastRec.add(newPair);
        }
      }
    }
    
    // the record must have more than the Crew name
    if (broadcastRec.size() > 1) {
      Callboard.Crews.broadcastChange(broadcastRec);
    }
  }
  
  /**
   * is invoked to request that the value of some field be changed.
   * @param change is the FieldPair of the change.  The FieldPair tag identifies
   * the field being changed.  The FieldPair value is the new value of the field.
   */
//  public void requestChange(FieldPair change) {
//    String tag = change.FieldTag;
//    if (tag.equals(CREW_NAME)) {
//      trainChange(new String((String) change.FieldValue));
//    }
//    CrewFields.replaceValue(change.FieldTag, change.FieldValue);
//
//  }
  

  /**
   * returns the name of the Crew.
   *
   * @return the name of the Crew from the GenericRecord.
   */
  public String getCrewName() {
    return new String( (String) CrewFields.findValue(CREW_NAME));
  }

  /**
   * retrieves the Extra flag.
   *
   * @return true, if the crew can have a named train.  False, if not.
   */
  public boolean getExtraFlag() {
    if (JobAssignment != null) {
      return JobAssignment.isExtra();
    }
    return true;
  }

  /**
   * changes the train that the crew is assigned to. This is the external entry into
   * a Crew, for triggering further actions.  Thus, the GenericRecord must also
   * be updated and the change broadcast.
   *
   * @param train is the Train assignment.  It can be null.
   *
   * @see cats.trains.Train
   */
  @SuppressWarnings("boxing")
  public void setTrain(Train train) {
    FieldPair pair = new FieldPair(TRAIN_ID);
    GenericRecord broadcastRec = new GenericRecord();
    if (train == null) {
      pair.FieldValue = "";
      StartTime = TimeSpec.UNKNOWN_TIME;
    }
    else {
      pair.FieldValue = train.getSymbol();
      StartTime = train.getOnDuty();
    }
    Assignment = train;
    Callboard.Crews.broadcastTimestamp(Constants.ASSIGN_TAG, new String(Constants.QUOTE + getCrewName()
        +Constants.QUOTE + Constants.FS + Constants.RUNNING + Constants.FS + Constants.QUOTE + (
        train == null ? Constants.NOTHING : train.getSymbol())+ Constants.QUOTE));
    CrewFields.replacePair(pair);
    // correct type on TIME_ON_DUTY?
//    CrewFields.replacePair(new FieldPair(TIME_ON_DUTY, StartTime));
    CrewFields.replacePair(new FieldPair(TIME_ON_DUTY, TimeSpec.convertMinutes(StartTime)));
    broadcastRec.add(CrewFields.findPair(CREW_NAME));
    broadcastRec.add(CrewFields.findPair(TRAIN_ID));
//    broadcastRec.add(CrewFields.findPair(TIME_ON_DUTY));
    broadcastRec.add(new FieldPair(TIME_ON_DUTY, TimeSpec.convertMinutes(StartTime)));
    Callboard.Crews.broadcastChange(broadcastRec);
  }

  /**
   * retrieves the Train assignment.
   *
   * @return the current Train.  It could be null.
   *
   * @see cats.trains.Train
   */
  public Train getTrain() {
    return Assignment;
  }

  /**
   * sets a reference to the Job the crew is working.
   *
   * @param job is the job.
   */
  public void setJob(Job job) {
    JobAssignment = job;
    if (!getExtraFlag() && (null != Assignment)) {
      Assignment.setCrew(null);
    }
  }

  /**
   * Retrieves the Job the crew is working.
   *
   * @return a reference to the Job.
   */
  public Job getJob() {
    return JobAssignment;
  }

  /**
   * retrieves the Time that the Crew went on duty.
   *
   * @return the number of minutes past minutes that the crew ent on duty.
   */
  public int getOnDutyTime() {
    return StartTime;
  }

//  /**
//   * is invoked to retrieve the field tag of the unique field for
//   * the Generic Record describing the object
//   * 
//   * @return the tag of the unique field
//   */
//  public String getUniqueFieldTag() {
//    return CREW_NAME;
//  }
  
  /**
   * is invoked to retrieve internal values, formatted in a String
   * as tag=value subStrings.  The derived class decides which
   * values (and tags) it wants to expose.
   * @return a String containing "tag=value" substrings.
   */
  public String getHiddenValues() {
    return "";
  }

  /**
   * changes the name of the train that the crew is running.
   * @param tName is the name (key into TrainStore) of the new train.  It may
   * be null or empty.
   */
  private void trainChange(String tName) {
    String dTime;
    Train newTrain = TrainStore.TrainKeeper.getTrain(tName);
    if (Assignment != newTrain) {
      if ( (Assignment != null) && (Assignment.getCrew() == this)) {
        Assignment.setCrew(null);
      }
      Assignment = newTrain;
      if (Assignment != null) {
        Assignment.setCrew(this);
        StartTime = Assignment.getOnDuty();
      }
      else {
        StartTime = TimeSpec.UNKNOWN_TIME;
      }
      Callboard.Crews.broadcastTimestamp(Constants.ASSIGN_TAG, new String(Constants.QUOTE + getCrewName() +
          Constants.QUOTE + Constants.FS + Constants.RUNNING + Constants.FS + Constants.QUOTE + (
              Assignment == null ? "nothing" : 
                Assignment.getSymbol()) + Constants.QUOTE));
    }
    else {
      dTime = ((String) CrewFields.findValue(TIME_ON_DUTY)).trim();
      if (dTime.equals("")) {
        StartTime = TimeSpec.UNKNOWN_TIME;
      }
      else {
        StartTime = TimeSpec.convertString(dTime, null);
      }
    }
    CrewFields.replaceValue(TIME_ON_DUTY, TimeSpec.convertMinutes(StartTime));
  }
}

/* @(#) Crew.java */
