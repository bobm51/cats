/* Name: ReplayVersion2.java
 *
 * What:
 *   This class implements the ReplayStrategy for version 3 of
 *   the recording format.
 *
 * Special Considerations:
 * 
 */
package cats.layout.replay;

import java.awt.Point;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import cats.common.Constants;
import cats.crew.Callboard;
import cats.crew.Crew;
import cats.gui.Screen;
import cats.jobs.Job;
import cats.jobs.JobStore;
import cats.layout.Logger;
import cats.layout.items.Block;
import cats.layout.items.Section;
import cats.trains.Train;
import cats.trains.TrainStore;

/**
 *   This class implements the ReplayStrategy for version 3 of
 *   the recording format.
 *
 *   
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */

public class ReplayVersion2 implements ReplayStrategy{
  
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
    String s;
    Crew c;
    Job j;
    Train t;
    
    String tStamp = ReplayHandler.getTimeStamp(tokens);
    if (tStamp == null) {
      return "A job/train assignment record is too short";
    }
    try {
      
      // extract the crew name
      crew = tokens.nextToken();
      for ( s = tokens.nextToken(); (!Constants.RUNNING.equals(s)) && 
        (!Constants.ASSIGNMENT.equals(s))&& (!Constants.REASSIGNED.equals(s)); s = tokens.nextToken()) {
        crew.concat(" " + s);
      }
      
      // extract the operation
      job = s;
      
      if (!Constants.REASSIGNED.equals(job)) {
        // extract the assignment
        assignment = tokens.nextToken();
        while (tokens.hasMoreTokens()) {
          assignment = assignment.concat(" " + tokens.nextToken());
        }
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
        j.setCrew(crew);
      }
      else if (Constants.RUNNING.equals(job)){  // train assignment      
        if ("nothing".equals(assignment)) {
//          if ((t = c.getTrain()) != null) {
//            t.tieDown();
//          }
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
    int x;
    int y;
    Section sec;
    String s = null;
    String tStamp = ReplayHandler.getTimeStamp(tokens);
    if (tStamp == null) {
      return "A move record is too short";
    }
    
    try {
      // Extract the Train
      s = tokens.nextToken();
      Train train = TrainStore.TrainKeeper.getTrain(s);
      if (train == null) {
        train = TrainStore.TrainKeeper.createTrain(s);
      }
      // looks for the replay marker
      while (tokens.hasMoreTokens()) {
        s = tokens.nextToken();
        if (Logger.TRACK_MARKER.equals(s)) {
          break;
        }
      }
      
      if (Logger.TRACK_MARKER.equals(s)) {
        // extracts the x coordinate of the destination Section
        if (tokens.hasMoreTokens()){
          s = tokens.nextToken();
          x = Integer.parseInt(s);
          
          // extracts the y coordinate of the destination Section
          if (tokens.hasMoreTokens()) {
            s = tokens.nextToken();
            y = Integer.parseInt(s);
            
            sec = Screen.DispatcherPanel.locateSection(x, y);
            if (sec != null) {              
              // set the clock
              ReplayHandler.handleTimeStamp(tStamp);              
              // extracts the edge number            
              if (tokens.hasMoreTokens()){
                s = tokens.nextToken();
                train.advanceTrain(sec.getEdge(Integer.parseInt(s)));
              }
              else {
                // positionTrain must be called the first time the Train
                // is placed on the layout to initialize the Train's internal
                // data
                if (train.getIcon() == null) {
                  // There seems to be a bug in Screen.locatePt() in which
                  // the upper left corner of a Section is not found.
                  Point p = sec.getTile().getClip().getLocation();
                  ++p.x;
                  ++p.y;
                  train.positionTrain(p);
                }
                else {
                  train.moveTrain(sec);
                }
              }
            }
          }
        }
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
      train = tokens.nextToken();
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
      train = tokens.nextToken();
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
      blockName = tokens.nextToken();
      while (tokens.hasMoreTokens()) {
        blockName = blockName.concat(tokens.nextToken());
      }
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
      blockName = tokens.nextToken();
      while (tokens.hasMoreTokens()) {
        blockName = blockName.concat(tokens.nextToken());
      }
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
    return null;
  }
  
  /**
   * process a record removal event. Version 3 did not have this event.
   * @param tokens are the names of the fields
   * @return null if everything went well or an error message
   */
  public String processDeleteRecord(StringTokenizer tokens) {
    return null;
  }

  /**
   * process a record change event. Version 3 did not have this event.
   * @param tokens are the names of the fields
   * @return null if everything went well or an error message
   */
  public String processChangeRecord(StringTokenizer tokens) {
    return null;
  }

}
/* @(#)ReplayVersion2.java */
