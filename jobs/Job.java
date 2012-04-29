/* Name: Job.java
 *
 * What:
 *   This class conatins the information about a Job.
 */
package cats.jobs;

import java.util.Iterator;

import cats.common.Constants;
import cats.crew.Callboard;
import cats.crew.Crew;
import cats.jobs.JobStore;
import cats.layout.Logger;
import cats.layout.store.FieldPair;
import cats.layout.store.GenericRecord;
import cats.layout.store.StoredObject;
//import cats.layout.xml.XMLEleFactory;
//import cats.layout.xml.XMLEleObject;
//import cats.layout.xml.XMLReader;

/**
 * This class contains the information about a Job.  Though based on using
 * an AbstractStore for the edit support, it also has legacy code for reading
 * in the older Job descriptions.
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2004, 2010</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */
public class Job
//    implements StoredObject, XMLEleObject {
implements StoredObject {

//  /**
//   * is the tag for identifying a Job in the XML file.
//   */
//  static final String XML_TAG = "JOB";

  /**
   * is the property tag for picking out the Job's name.
   */
  public static final String JOB_NAME = "JOB_NAME";

  /**
   * is the property tag for picking out the Train flag.
   */
  public static final String RUNS_TRAIN = "RUNS_TRAIN";

  /**
   * is the editor label for the crew member
   */
  public static final String CREW_NAME = "CREW_NAME";

  /**
   * is the placeholder property tag for the Assistant column.
   */
  public static final String ASSISTANT = "ASSISTANT";

  
  /**
   * is the Font for drawing the job in
   */
  public static final String FONT = "FONT";

  /**
   * is used to locate the fields that describe the Train.  The
   * common.prop class has some useful utilities for pulling out
   * entries.
   */
  protected static String[] JOB_PROP = {
      JOB_NAME,
      RUNS_TRAIN,
      CREW_NAME,
      ASSISTANT,
      FONT
  };

  /**
   * is the crew working the job.
   */
  private Crew MyCrew;

  /**
   * is the GenericRecord defining the attributes of a Job that can
   * be read from an XML file or edited.
   */
  private GenericRecord JobFields;

  /**
   * constructs a Job, with a given set of initial values.
   */
  public Job() {
  }

  /**
   * is the method used to give an Object its initial description.
   *
   * @param description is the GenericRecord describing the Object.
   */
  public void linkDescription(GenericRecord description) {
    JobFields = description;
    String crewName = new String( (String) description.findValue(CREW_NAME)).trim();
    if (!crewName.equals("")) {
      crewChange(crewName);
    }
  }

  /**
   * is invoked to tell the Object that it is being deleted.  It should
   * clean up any associations it has with other Objects, then remove its
   * description from its Store.
   */
  public void destructor() {
    if ( (MyCrew != null) && (MyCrew.getJob() == this)) {
      MyCrew.setJob(null);
      writeStatus(new String(Constants.QUOTE + MyCrew.getCrewName() + Constants.QUOTE +
          Constants.FS + Constants.REASSIGNED));
    }
    JobStore.JobsKeeper.delRecord(JobFields);
  }

  /**
   * is invoked to tell the Object that the description should be updated.
   * This happens when a copy of the GenericRecord is changed.  The parameter
   * will usually not be the GenericRecord, so the StoredObject should pass
   * the changes along to its GenericRecord.
   * <p>
   * The parameter must have been validated to have all the required fields
   * prior to invoking this method and it is assumed to not be the
   * associated GenericRecord.
   *
   * @param description is the updated GenericRecord describing the Object.
   */
  public void updateDescription(GenericRecord description) {
    String fieldTag;
    FieldPair newPair;
    FieldPair oldPair;
    for (Iterator<FieldPair> iter = description.iterator(); iter.hasNext(); ) {
      newPair = iter.next();
      fieldTag = newPair.FieldTag;
      oldPair = JobFields.findPair(fieldTag);
      if (oldPair != null) {
        if (newPair.FieldValue.equals(oldPair.FieldValue)) {
          // need to keep the key for the change broadcast
          if (JOB_NAME.equals(fieldTag)) {
            iter.remove();
          }          
        }
        else {
          if (CREW_NAME.equals(fieldTag)) {
            crewChange((String) newPair.FieldValue);
          }
          else if (GenericRecord.STORED_OBJECT.equals(fieldTag)) {
            newPair.FieldValue = this;
          }
          JobFields.replacePair(newPair);
        }
      }
    }
    // the record must have the key field
    if (description.size() > 1) {
      JobStore.JobsKeeper.broadcastChange(description);
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
//      crewChange(new String((String) change.FieldValue));
//    }
//    JobFields.replaceValue(change.FieldTag, change.FieldValue);
//  }
  
  /**
   * writes any status reports to the log file.  This uses the real time
   * clock so that an accurate record is kept as to how long someone worked a job.
   * Note that only the log is written.
   * 
   * @param msg is the message to log
   */
  private void writeStatus(String msg) {
    if ( (msg != null) && Logger.isLogging()) {
      Logger.realTimeStamp(Constants.ASSIGN_TAG, msg);
    }
  }

  /**
   * returns the name of the Job.
   *
   * @return the name of the Train.
   */
  private String getJobName() {
    return new String( (String) JobFields.findValue(JOB_NAME));
  }

  /**
   * returns the "runs train value".
   *
   * @return true if the crew assigned to the Job appears on the extra board.
   */
  public boolean isExtra() {
    return ( (Boolean) JobFields.findValue(RUNS_TRAIN)).booleanValue();
  }

  /**
   * returns the name of the crew working the job.
   *
   * @return the crew name.
   */
  public String getCrewName() {
    return new String( (String) JobFields.findValue(CREW_NAME));
  }

  /**
   * changes the crew assigned to the job. This is the external entry into
   * a Job, for triggering further actions.  Thus, the GenericRecord must also
   * be updated and the change broadcast.
   * @param crew is the name of the crew
   */
  public void setCrew(String crew) {
    FieldPair newPair = new FieldPair(CREW_NAME, crew);
    GenericRecord broadcastRec = new GenericRecord();
    crewChange(crew);
    JobFields.replacePair(newPair);
    broadcastRec.add(JobFields.findPair(JOB_NAME));
    broadcastRec.add(JobFields.findPair(CREW_NAME));
    JobStore.JobsKeeper.broadcastChange(broadcastRec);
  }

  /**
   * perform a crew change on the job.  This performs the internal actions when the
   * crew changes; thus, it does not change the GenericRecord or broadcast the change.
   * @param cName is the name (key into the CrewStore) of the new crew performing the job.
   * It could be null or the empty string. 
   */
  private void crewChange(String cName) {
    Crew newCrew = Callboard.Crews.findCrew(cName);
    if (MyCrew != newCrew) {
      if ( (MyCrew != null) && (MyCrew.getJob() == this)) {
        MyCrew.setJob(null);
        writeStatus(new String(Constants.QUOTE + MyCrew.getCrewName() + Constants.QUOTE +
            Constants.FS + Constants.REASSIGNED));
      }
      MyCrew = newCrew;
      if (MyCrew != null) {
        MyCrew.setJob(this);
        writeStatus(new String(Constants.QUOTE + cName + Constants.QUOTE + Constants.FS +
            Constants.ASSIGNMENT + Constants.FS + Constants.QUOTE + getJobName() + Constants.QUOTE));
      }
    }   
  }
  
  /** constructs an array of Strings, which each element containing
   * one of the Strings describing the Job.
   * @return the attribute tags of the Job.
   */
//  public String[] getInfo() {
//    String info[] = new String[JOB_PROP.length];
//
//    /* The following must match the order in JOB_PROP */
//    info[0] = new String(getJobName());
//    info[1] = new String(String.valueOf(isExtra()));
//    if (MyCrew != null) {
//      info[3] = MyCrew.getCrewName();
//    }
//    else {
//      info[3] = "Unassigned";
//    }
//    return info;
//  }

  /*
   * is the method through which the object receives the text field.
   *
   * @param eleValue is the Text for the Element's value.
   *
   * @return if the value is acceptable, then null; otherwise, an error
   * string.
   */
//  public String setValue(String eleValue) {
//    return new String("A " + XML_TAG + " cannot contain a text field ("
//                      + eleValue + ")."); 
//  }

  /*
   * is the method through which the object receives embedded Objects.
   *
   * @param objName is the name of the embedded object
   * @param objValue is the value of the embedded object
   *
   * @return null if the Object is acceptible or an error String
   * if it is not.
   */
//  public String setObject(String objName, Object objValue) {
//    return new String("A " + XML_TAG + " cannot contain an Element ("
//                      + objName + ").");
//  }

  /*
   * returns the XML Element tag for the XMLEleObject.
   *
   * @return the name by which XMLReader knows the XMLEleObject (the
   * Element tag).
   */
//  public String getTag() {
//    return new String(XML_TAG);
//  }

  /*
   * tells the XMLEleObject that no more setValue or setObject calls will
   * be made; thus, it can do any error chacking that it needs.
   *
   * @return null, if it has received everything it needs or an error
   * string if something isn't correct.
   */
//  public String doneXML() {
//    return null;
//  }

  /**
   * registers a JobFactory with the XMLReader.
   */
//  static public void init() {
//    XMLReader.registerFactory(XML_TAG, new JobFactory());
//  }

//  /**
//   * is invoked to retrieve the field tag of the unique field for
//   * the Generic Record describing the object
//   * 
//   * @return the tag of the unique field
//   */
//  public String getUniqueFieldTag() {
//    return JOB_NAME;
//  }
  
  /**
   * is invoked to retrieve internal values, formatted in a String
   * as tag=value subStrings.  The derived class decides which
   * values (and tags) it wants to expose.
   * @return a String containing "tag=value" substrings.
   */
  public String getHiddenValues() {
    if (MyCrew == null) {
      return "";
    }
    return CREW_NAME + FieldPair.SEP + Constants.QUOTE + MyCrew.getCrewName() + Constants.QUOTE;
  }
}

/**
 * is a Class known only to the Job class for creating
 * Jobs from an XML document.
 */
//class JobFactory
//    implements XMLEleFactory {
//
//  private GenericRecord NewJob;
//  /*
//   * tells the factory that an XMLEleObject is to be created.  Thus,
//   * its contents can be set from the information in an XML Element
//   * description.
//   */
//  public void newElement() {
//    NewJob = new GenericRecord(GenericRecord.DATARECORD, 5);
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
//    if (Job.JOB_NAME.equals(tag)) {
//      NewJob.replaceValue(Job.JOB_NAME, new String(value));
//    }
//    else if (Job.RUNS_TRAIN.equals(tag)) {
//      NewJob.replaceValue(Job.RUNS_TRAIN, new String(value));
//    }
//    else if (Job.CREW_NAME.equals(tag)) {
//      NewJob.replaceValue(Job.CREW_NAME, new String(value));
//    }
//    else {
//      resultMsg = new String(tag +
//                             " is not recognized as an XML attribute of a "
//                             + Job.XML_TAG + ".");
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
//    JobStore.JobsKeeper.addRecord(GenericRecord.DATARECORD, NewJob);
//    return NewJob;
//  }
//}
/* @(#)Job.java */