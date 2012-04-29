/* Name: ReplayVersion3.java
 *
 * What:
 *   This class implements the ReplayStrategy for version 4 of
 *   the recording format.
 *
 * Special Considerations:
 * 
 */
package cats.layout.replay;

import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import cats.common.Constants;
import cats.crew.Callboard;
import cats.crew.Crew;
import cats.jobs.Job;
import cats.jobs.JobStore;
import cats.layout.items.Block;
import cats.layout.store.AbstractStore;
import cats.layout.store.FieldPair;
import cats.layout.store.GenericRecord;
import cats.trains.Train;
import cats.trains.TrainStore;

/**
 *   This class implements the ReplayStrategy for version 4 of
 *   the recording format.
 *
 *   
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2008, 2011</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */

public class ReplayVersion3 implements ReplayStrategy{
  
  /**
   * Assign a crew to a train.
   * @param tokens is a tokenized String, containing the crew
   * being assigned and the train being assigned to.
   * @return null if everything went well or an error message.
   */
  public String processAssign(StringTokenizer tokens) {
    String job;
    String crew;
    String assignment = null;
    Crew c;
    Job j;
    Train t;
    
    String tStamp = ReplayHandler.getTimeStamp(tokens);
    if (tStamp == null) {
      return "A job/train assignment record is too short";
    }
    try {
      
      // extract the crew name
      crew = ReplayHandler.fetchQuotedString(tokens);

      job = tokens.nextToken();

      if (!Constants.REASSIGNED.equals(job)) {
        assignment = ReplayHandler.fetchQuotedString(tokens);
      }
      
      // set the clock
      ReplayHandler.handleTimeStamp(tStamp);
      
      // Verify the crew
      c = Callboard.Crews.findCrew(crew);
      if (c == null) {
        c = Callboard.Crews.addCrew(crew);
      }
      
      if (Constants.ASSIGNMENT.equals(job)) { // job assignment
        j = JobStore.JobsKeeper.findJob(assignment);
        if (null == j) {
          return "unknown job assignment: " + assignment;
        }
        j.setCrew(crew);
      }
      else if (Constants.RUNNING.equals(job)){  // train assignment 
        if (Constants.NOTHING.equals(assignment)) {
          c.setTrain(null);
        }
        else {
          t = TrainStore.TrainKeeper.getTrain(assignment);
          if (t == null) {
            t = TrainStore.TrainKeeper.createTrain(assignment);
          }
          if (c.getTrain() != null) {
            c.getTrain().setCrew(null);
          }
          c.setTrain(t);
          if (t.getCrew() != null) {
            t.getCrew().setTrain(null);
          }
          t.setCrew(c);
        }
      }
      else {  //reassigned
        c.setJob(null);
      }
    }
    catch (NoSuchElementException nsee) {
      return "Truncated assignment record.";
    }
    return null;
    
  }
  
  /**
   * All log records have been read.  Cleanup.
   */
  public void processFinish() {
    ReplayHandler.processFinish();
  }
  
  /**
   * move a train.
   * @param tokens is a tokenized String, containing the train,
   * where it is coming from and where it is going to.
   * @return null if everything went well or an error message.
   */
  public String processMove(StringTokenizer tokens) {
    String s = null;
    String tStamp = ReplayHandler.getTimeStamp(tokens);
    if (tStamp == null) {
      return "A move record is too short";
    }
    
    try {
      // Extract the Train
      s = ReplayHandler.fetchQuotedString(tokens);
      Train train = TrainStore.TrainKeeper.getTrain(s);
      if (train == null) {
        train = TrainStore.TrainKeeper.createTrain(s);
      }
      
      // extract departure location
      s = ReplayHandler.fetchQuotedString(tokens);
      
      // extract "to"
      s = tokens.nextToken();
      if (!"to".equals(s)) {
        return "Badly formatted movement record";
      }
      
      // extract arrival location
      s = ReplayHandler.fetchQuotedString(tokens);
      
      // extracts the x coordinate of the destination Section
      if (tokens.hasMoreTokens()){
        s = ReplayHandler.fetchQuotedString(tokens);
        if (null == (s = train.moveTrain(s))) {
          // set the clock
          ReplayHandler.handleTimeStamp(tStamp);
        }
        return s;        
      }
    }
    catch (NoSuchElementException nsee) {
      return "Truncated move record.";
    }
    return null;    
  }

  /**
   * Rerun a train.
   * @param tokens is a tokenized String, containing the train
   * being rerun.
   * @return null if everything went well or an error message.
   */
  public String processRerun(StringTokenizer tokens) {
    String train;
    Train t;
    String tStamp = ReplayHandler.getTimeStamp(tokens);
    if (tStamp == null) {
      return "A train rerun record is too short";
    }
    try {
      // Extract the Train
      train = ReplayHandler.fetchQuotedString(tokens);
      t = TrainStore.TrainKeeper.getTrain(train);
      if (t == null) {
        t = TrainStore.TrainKeeper.createTrain(train);
      }
      
      // set the clock
      ReplayHandler.handleTimeStamp(tStamp);
      t.rerun();
    }
    catch (NoSuchElementException nsee) {
      return "Truncated move record.";
    }
    return null;
    
  }

  /**
   * Terminate a train.
   * @param tokens is a tokenized String, containing the train
   * being terminated.
   * @param status is either "TERMINATED" or "TIED_DOWN"
   * @return null if everything went well or an error message.
   */
  public String processTerminated(StringTokenizer tokens, String status) {
    String train;
    Train t;
    String tStamp = ReplayHandler.getTimeStamp(tokens);
    if (tStamp == null) {
      return "A train terminated record is too short.";
    }
    try {
      // Extract the Train
      train = ReplayHandler.fetchQuotedString(tokens);
      t = TrainStore.TrainKeeper.getTrain(train);
      if (t != null) {
        // set the clock
        ReplayHandler.handleTimeStamp(tStamp);
        if (Constants.TERMINATED_TAG.equals(status)) {
          t.remove();
        }
        else {
          t.tieDown();
        }
      }
    }
    catch (NoSuchElementException nsee) {
      return "Truncated terminated record.";
    }
    return null;
    
  }

  /**
   * Process Track Authority being added or removed from a block.
   * @param tokens is a tokenized String, containing the 
   * name of the block and its sense.
   * @return null if everything went well or an error message.
   */
  public String processTNT(StringTokenizer tokens) {
    String blockName;
    String sense;
    Block b;
    boolean add = false;
    String tStamp = ReplayHandler.getTimeStamp(tokens);
    if (tStamp == null) {
      return "A Track Authority record is too short.";
    }
    try {
      // Extract the on or off name
      sense = tokens.nextToken();
      if (Constants.ADD_MARKER.equals(sense)){
        add = true;
      }
      else if (Constants.REMOVE_MARKER.equals(sense)){
        add = false;
      }
      else {
        return "Unknown Track Authority action " + sense;
      }
      blockName = ReplayHandler.fetchQuotedString(tokens);
      if ((b = Block.findBlock(blockName)) != null) {
        // set the clock
        ReplayHandler.handleTimeStamp(tStamp);
        b.setTNT(add);
      }
    }
    catch (NoSuchElementException nsee) {
      return "Truncated Track Authority record.";
    }
    return null;
    
  }

  /**
   * Mark or remove a block as OOS.
   * @param tokens is a tokenized String, containing the name of the
   * block taken out of service or put back in service.
   * @return null if everything went well or an error message.
   */
  public String processOOS(StringTokenizer tokens) {
    String blockName;
    String sense;
    Block b;
    boolean add = false;
    String tStamp = ReplayHandler.getTimeStamp(tokens);
    if (tStamp == null) {
      return "An OOS record is too short.";
    }
    try {
      // Extract the on or off name
      sense = tokens.nextToken();
      if (Constants.ADD_MARKER.equals(sense)){
        add = true;
      }
      else if (Constants.REMOVE_MARKER.equals(sense)){
        add = false;
      }
      else {
        return "Unknown OOS action " + sense;
      }
      blockName = ReplayHandler.fetchQuotedString(tokens);
      ReplayHandler.fetchQuotedString(tokens);
      if ((b = Block.findBlock(blockName)) != null) {
        // set the clock
        ReplayHandler.handleTimeStamp(tStamp);
        b.setOOS(add);
      }
    }
    catch (NoSuchElementException nsee) {
      return "Truncated OOS record.";
    }
    return null;    
  }

  /**
   * process a record add event.  For the record descriptions, the
   * FIELD_KEY descriptor is not added because it is used in CATS
   * to describe the class of object each record represents.  In the
   * data records, the FIELD_KEY pair is removed because it is the
   * link back to the object represented.
   * <p>
   *  Version 3 did not have this event.
   * @param tokens are the named fields in the record to be added
   * @return null if everything went well or an error message.
   */
  public String processAddRecord(StringTokenizer tokens) {
    String storeName;
    GenericRecord updates = new GenericRecord();
    AbstractStore store = null;
    try {
        // Extract the timestamp
        tokens.nextToken();
        
        // Extract the store
        storeName = tokens.nextToken();
        ReplayHandler.toFieldPairs(tokens, updates);
        if (TrainStore.TrainKeeper.getDataID().equals(storeName)) {
          store = TrainStore.TrainKeeper;
        }
//        else if (TrainStore.TrainKeeper.getFieldID().equals(storeName)) {
//          return "Unexpected change to Train presentation format";
//        }
        else if (JobStore.JobsKeeper.getDataID().equals(storeName)) {
          store = JobStore.JobsKeeper;
        }
//        else if (JobStore.JobsKeeper.getFieldID().equals(storeName)) {
//          return "Unexpected change to Jobs presentation format";          
//        }
        else if (Callboard.Crews.getDataID().equals(storeName)) {
          store = Callboard.Crews;
        }
//        else if (Callboard.Crews.getFieldID().equals(storeName)) {
//          return "Unexpected change to Crew presentation format";
//        }
        else {
          return "unknown store: " + storeName;
        }
        store.changeRecord(updates);
    }
    catch (NoSuchElementException nsee) {
        return "short add record";
    }
    return null;
  }
  
  /**
   * process a record removal event. Version 3 did not have this event.
   * @param tokens are the names of the fields
   * @return null if everything went well or an error message
   */
  public String processDeleteRecord(StringTokenizer tokens) {
    String storeName;
    GenericRecord del = new GenericRecord();
    AbstractStore store = null;
    FieldPair element;
    try {
      // Extract the timestamp
      tokens.nextToken();
      
      // Extract the store
      storeName = tokens.nextToken();
      ReplayHandler.toFieldPairs(tokens, del);
      if (TrainStore.TrainKeeper.getDataID().equals(storeName)) {
        store = TrainStore.TrainKeeper;
      }
      else if (TrainStore.TrainKeeper.getFieldID().equals(storeName)) {
        return "Unexpected change to Train presentation format";
      }
      else if (JobStore.JobsKeeper.getDataID().equals(storeName)) {
        store = JobStore.JobsKeeper;
      }
      else if (JobStore.JobsKeeper.getFieldID().equals(storeName)) {
        return "Unexpected change to Jobs presentation format";          
      }
      else if (Callboard.Crews.getDataID().equals(storeName)) {
        store = Callboard.Crews;
      }
      else if (Callboard.Crews.getFieldID().equals(storeName)) {
        return "Unexpected change to Crew presentation format";
      }
      else {
    	  return "unknown data store";
      }
      element = del.elementAt(0);
      del = store.findRecord(element.FieldTag, (String) element.FieldValue);
      if (null != del) {
        store.delRecord(del);
      }
    }
    catch (NoSuchElementException nsee) {
      return "short add record";
    }
    return null;
  }

  /**
   * process a record change event. Version 3 did not have this event.
   * @param tokens are the names of the fields
   * @return null if everything went well or an error message
   */
  public String processChangeRecord(StringTokenizer tokens) {
    String storeName;
    GenericRecord updates = new GenericRecord();
    AbstractStore store = null;
    try {
        // Extract the timestamp
        tokens.nextToken();
        
        // Extract the store
        storeName = tokens.nextToken();
        ReplayHandler.toFieldPairs(tokens, updates);
        if (TrainStore.TrainKeeper.getDataID().equals(storeName)) {
          store = TrainStore.TrainKeeper;
        }
        else if (JobStore.JobsKeeper.getDataID().equals(storeName)) {
          store = JobStore.JobsKeeper;
        }
        else if (Callboard.Crews.getDataID().equals(storeName)) {
          store = Callboard.Crews;
        }
        else {
          return "unknown store: " + storeName;
        }
        store.changeRecord(updates);
    }
    catch (NoSuchElementException nsee) {
        return "short add record";
    }
    return null;
  }
}
/* @(#)ReplayVersion3.java */
