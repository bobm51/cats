/*
 * Name: Constants.java
 * 
 * What:
 *   This file contains constants and helper routines.
 *   
 * Special Considerations:
 */
package cats.common;

/**
 * This file contains constants and class independent helper routines.
 * 
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2008, 2010, 2011</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
*/
public class Constants {

  /**
   * is a consistent string for true
   */
  public final static String TRUE = "true";
  
  /**
   * is a consistent string to use for false
   */
  public final static String FALSE = "false";
  
  /**
   * is the field Separator.  No string should include it.
   */
  public final static char FS = '\t';
  
  /**
   * is the field separator as a String
   */
  public final static String FS_STRING = new String("" + FS);

  /**
   * is the CATS property tag
   */
  public final static String CATS_TAG = "CATS";
  
  /**
   * is the marker for a record being added to a Store
   */
  public final static String ADD_TO_STORE = "Added";
  
  /**
   * is the marker for a record being removed from a Store
   */
  public final static String REMOVE_FROM_STORE = "Deleted";
     
  /**
   * is the marker for a record being added to a Store
   */
  public final static String CHANGE_STORE = "Changed";
      
  /**
   * is the double quote character, double quoted
   */
  public final static String QUOTE = new String("\"");
  
  /**
   * is the FS with a quote on either side
   */
  public final static String QUOTE_FS = new String(QUOTE + FS + QUOTE);
  
  /**
   * indicates what train a crew is running
   */
  public static final String RUNNING = "running";
  
  /**
   * is the String indicating the crew is running nothing
   */
  public static final String NOTHING = "nothing";
  
  /**
   * is the String indicating a train's location is unknown
   */
  public static final String UNKNOWN = "unknown";
  
  /**
   * is the message tag for identifying a train movement.
   */
  public static final String MOVE_TAG = "Move:";

  /**
   * is the message tag for identifying a crew assignment.
   */
  public static final String ASSIGN_TAG = "Assign:";

  /**
   * is the message tag for timestamping when a train terminates.
   */
  public static final String TERMINATED_TAG = "Terminated:";

  /**
   * is the message tag for when a train ties down.
   */
  public static final String TIEDDOWN_TAG = "TiedDown:";
  
  /**
   * is the message tag for timestamping when the session ended.
   */
  public static final String RERUN_TAG = "Rerun:";
  
  /**
   * is the message tag for timestamping taking a track out of
   * service or returning it to service.
   */
  public static final String OOS_TAG = "OOS:";

  /**
   * is the message tag for timstamping granting or removing
   * track authority.
   */
  public static final String TNT_TAG = "T&T:";
 
  /**
   * indicates what job a crew is assigned to
   */
  public static final String ASSIGNMENT = "assigned to";
  
  /**
   * indicates that a crew has been removed from a job.
   */
  public static final String REASSIGNED = "reassigned";

  /**
   * is the marker for Track Authority or OOS being added.
   */
  public static final String ADD_MARKER = "add";
  
  /**
   * is the marker for Track Authority or OOS being removed.
   */
  public static final String REMOVE_MARKER = "remove";

  /**
   * is the status value for a train waiting for crew
   */
  public static final String WAITING_STATE = "WAITING";

  /**
   * is the status value for a train working
   */
  public static final String WORKING_STATE = "WORKING";

  /**
   * is the status value for a train that has been tied down
   */
  public static final String TIED_DOWN_STATE = "TIED_DOWN";

  /**
   * is the status value for a train that has been terminated
   */
  public static final String TERMINATED_STATE = "TERMINATED";

  /**
   * is the message tag on a request to CATS to add a new train
   */
  public final static String ADD_TRAIN_REQUEST = "AddTrainRequest:";
  
  /**
   * is the message tag on a request to CATS to change the fields in a train
   */
  public final static String CHANGE_TRAIN_REQUEST = "ChangeTrainRequest:";
  
  /**
   * is the message tag on a request to CATS to add a new train crew
   */
  public final static String ADD_CREW_REQUEST = "AddCrewRequest:";
  
  /**
   * is the message tag on a request to CATS to change the fields in a train crew
   */
  public final static String CHANGE_CREW_REQUEST = "ChangeCrewRequest:";
  
  /**
   * is the message tag on a request to CATS to delete a train crew
   */
  public final static String DELETE_CREW_REQUEST = "DeleteCrewRequest:";
  
}
/* @(#)Constants.java */
