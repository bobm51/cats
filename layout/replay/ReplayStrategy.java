/* Name: ReplayStrategy.java
 *
 * What:
 *   This interface is the basis of the Strategy design pattern
 *   for replaying (restoring) the state of a CTC panel.
 *   <p>
 *   The purpose of a ReplayHandler is to read a file containing
 *   events that were recorded during an operating session (or partial
 *   operating session) and restore the current session to that state
 *   or at least close to it.  The problem being solved is that the
 *   events and format in which they are recorded has changed as CATS
 *   has evolved.  Thus, an old recording cannot be replayed on a newer
 *   version of CATS and vice versa.  Though it is impossible to replay
 *   new recordings on old versions of CATS, the converse should be
 *   possible.  The way it is done is that CATS marks each recording
 *   with a version number.  The ReplayHandler reads the first record
 *   for the version number and invokes the corresponding parser onthe
 *   records.  The strategy pattern implements the switch for the
 *   corresponding parser.
 *
 * Special Considerations:
 *   For this to work right, the layout must be the same as when
 *   CATS crashed.  In other words, the XML file should not be
 *   edited from the one recorded.
 * 
 */
package cats.layout.replay;

import java.util.StringTokenizer;

/**
 *   This interface is the basis of the Strategy design pattern
 *   for replaying (restoring) the state of a CTC panel.
 *   <p>
 *   The purpose of a ReplayHandler is to read a file containing
 *   events that were recorded during an operating session (or partial
 *   operating session) and restore the current session to that state
 *   or at least close to it.  The problem being solved is that the
 *   events and format in which they are recorded has changed as CATS
 *   has evolved.  Thus, an old recording cannot be replayed on a newer
 *   version of CATS and vice versa.  Though it is impossible to replay
 *   new recordings on old versions of CATS, the converse should be
 *   possible.  The way it is done is that CATS marks each recording
 *   with a version number.  The ReplayHandler reads the first record
 *   for the version number and invokes the corresponding parser onthe
 *   records.  The strategy pattern implements the switch for the
 *   corresponding parser.
 *
 * Special Considerations:
 *   For this to work right, the layout must be the same as when
 *   CATS crashed.  In other words, the XML file should not be
 *   edited from the one recorded.
 *   
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */

public interface ReplayStrategy {

  /**
   * Assign a crew to a train.
   * @param tokens is a tokenized String, containing the crew
   * being assigned and the train being assigned to.
   * @return null if everything went well or an error message.
   */
  public String processAssign(StringTokenizer tokens);

  /**
   * All log records have been read.  Cleanup.
   */
  public void processFinish();
  
  /**
   * move a train.
   * @param tokens is a tokenized String, containing the train,
   * where it is coming from and where it is going to.
   * @return null if everything went well or an error message.
   */
  public String processMove(StringTokenizer tokens);

  /**
   * Rerun a train.
   * @param tokens is a tokenized String, containing the train
   * being rerun.
   * @return null if everything went well or an error message.
   */
  public String processRerun(StringTokenizer tokens);

  /**
   * Terminate a train.
   * @param tokens is a tokenized String, containing the train
   * being terminated.
   * @param status is either "TERMINATED" or "TIED_DOWN"
   * @return null if everything went well or an error message.
   */
  public String processTerminated(StringTokenizer tokens, String status);

  /**
   * Process Track Authority being added or removed from a block.
   * @param tokens is a tokenized String, containing the 
   * name of the block and its sense.
   * @return null if everything went well or an error message.
   */
  public String processTNT(StringTokenizer tokens);

  /**
   * Mark or remove a block as OOS.
   * @param tokens is a tokenized String, containing the name of the
   * block taken out of service or put back in service.
   * @return null if everything went well or an error message.
   */
  public String processOOS(StringTokenizer tokens);

  /**
   * process a record add event.  For the record descriptions, the
   * FIELD_KEY descriptor is not added because it is used in CATS
   * to describe the class of object each record represents.  In the
   * data records, the FIELD_KEY pair is removed because it is the
   * link back to the object represented.
   * @param tokens are the named fields in the record to be added
   * @return null if everything went well or an error message.
   */
  public String processAddRecord(StringTokenizer tokens);
  
  /**
   * process a record removal event.
   * @param tokens are the names of the fields
   * @return null if everything went well or an error message
   */
  public String processDeleteRecord(StringTokenizer tokens);

  /**
   * process a record change event. 
   * @param tokens are the names of the fields
   * @return null if everything went well or an error message
   */
  public String processChangeRecord(StringTokenizer tokens);
}
/* @(#)ReplayStrategy.java */