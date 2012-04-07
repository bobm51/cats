/* Name: MsgStructs.java
 *
 * What:
 *   This class contains definitions for the messages CATS generates to report
 *   state changes.
 *
 * Special Considerations:
 *   These structures list the fields in each message.  They can be used as
 *   containers, filled in by parsers, and passed to other objects.
 * 
 */
package cats.common;

import java.util.ArrayList;

/**
 *   This class contains definitions for the messages CATS generates to report
 *   state changes.  This class is self-contained and intended to be used by
 *   CATS and any program that interprets messages.
 *
 * Special Considerations:
 *   These structures list the fields in each message.  They can be used as
 *   containers, filled in by parsers, and passed to other objects.
 * 
 * <p>Title: TrainStat - Train Status Monitor</p>
 * <p>Description: A program for displaying in real time, the status of a CATS train.
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

public class MsgStructs {
    
  /**
   * Name: OOS_struct
   * 
   * What:
   *   The message sent when track is taken or out of service.  See also
   *   cats.layout.items.Block.setTNT
   */
  public class OOS_Struct {
	  /**
	   * the struct identifier
	   */
      public final String tag = Constants.OOS_TAG;
      
      /**
       * when the operation was requested
       */
      public String timeStamp;
      
      /**
       * Constants.ADD_MARKER or Constants.REMOVE_MARKER
       */
      public String sense;
      
      /**
       * the quoted name of the block affected
       */
      public String blockName;
  }
  
  /**
   * Name: TNT_struct
   * 
   * What:
   *   The message sent when track authority is granted or removed.  See also
   *   cats.layout.items.Block.setOOS
   */
  public class TNT_Struct {
	  /**
	   * the struct identifier
	   */
      public final String tag = Constants.TNT_TAG;
      
      /**
       * when the operation was requested
       */
      public String timeStamp;
      
      /**
       * Constants.ADD_MARKER or Constants.REMOVE_MARKER
       */
      public String sense;

      /**
       * the quoted name of the block affected
       */
      public String blockName;
  }

  /**
   * Name: Add_Struct
   *
   * What:
   *   The form of a message sent when a record is added to a store.
   *   There are multiple property fields.  If the record already exists,
   *   then only new fields are added to the record.  Existing fields are
   *   left untouched.
   *   
   *   See also cats.layout.store.AbstractStore.broadcastAdd
   */
  public class Add_Struct {
	  /**
	   * the struct identifier
	   */
      public final String tag = Constants.ADD_TO_STORE;

      /**
       * when the operation was requested
       */
      public String timeStamp;
      
      /**
       * the XML tag on the Store where the record was added
       */
      public String xmlTag;
      
      /**
       * the fields in the record
       */
      public ArrayList<String> properties;
  }
  
  /**
   * Name: Change_Struct
   *
   * What:
   *   The form of a message sent when a record in a store is changed.
   *   There are multiple property fields.  If a matching record is
   *   not found, one is created with the values from the message.  If
   *   a matching record is found, then all fields in the message are
   *   placed in the record.
   *   
   *   See also cats.layout.store.AbstractStore.broadcastChange
   */
  public class Change_Struct {
	  /**
	   * the struct identifier
	   */
      public final String tag = Constants.CHANGE_STORE;
      
      /**
       * when the operation was requested
       */
      public String timeStamp;
      
      /**
       * the XML tag on the Store where the record was changed
       */
      public String xmlTag;

      /**
       * the fields in the record
       */
      public ArrayList<String> properties;
  }
  
  /**
   * Name: Remove_Struct
   *
   * What:
   *   The form of a message sent when a record in a store is deleted.
   *   There are multiple property fields.  See also cats.layout.store.AbstractStore.delRecord
   */
  public class Remove_Struct {
	  /**
	   * the struct identifier
	   */
      public final String tag = Constants.CHANGE_STORE;
      
      /**
       * when the operation was requested
       */
      public String timeStamp;
      
      /**
       * the XML tag on the Store where the record was deleted
       */
      public String xmlTag;

      /**
       * the fields in the record
       */
      public ArrayList<String> properties;
  }  
  
  /**
   * Name: Crew_Change_Struct
   *
   * What:
   *   The form of a message sent when the crew changes trains.
   */
  public class Crew_Change_Struct {
	  /**
	   * the struct identifier
	   */
      public final String tag = Constants.ASSIGN_TAG;
      
      /**
       * when the operation was requested
       */
      public String timeStamp;
      
      /**
       * the quoted name of the crew being assigned
       */
      public String crewName;
      
      /**
       * a separator for printing
       */
      public final String running = Constants.RUNNING;
      
      /**
       * the quoted train symbol or "nothing"
       */
      public String trainId;
  }  
  
  /**
   * Name: Crew_Assignemt_Struct
   *
   * What:
   *   The form of a message sent when the crew changes jobs.
   */
  public class Crew_Assignment_Struct {
	  /**
	   * the struct identifier
	   */
      public final String tag = Constants.ASSIGN_TAG;
      
      /**
       * when the operation was requested
       */
      public String timeStamp;
      
      /**
       * the quoted name of the crew being assigned
       */
      public String crewName;  // crewName is quoted
      
      /**
       * a separator for printing - "assigned to" or "reassigned"
       */
      public String running;
      
      /**
       * the optional, quoted job name
       */
      public String job;
  }  
  
 /**
   * Name: Move_Train_Struct
   *
   * What:
   *   The form of a message sent when a train moves.
   */
  public class Move_Train_Struct {
	  /**
	   * the struct identifier
	   */
      public final String tag = Constants.MOVE_TAG;
      
      /**
       * when the operation was requested
       */
      public String timeStamp;
      
      /**
       * the quoted, train symbol
       */
      public String trainId;
      
      /**
       * where the train left from - station, coordinates, or "unknown".
       * departure is quoted
       */
      public String departure;
      
      /**
       * a separator string
       */
      public final String to = "to";
      
      /**
       * where the train moved to - station, coordinates, or "unknown".
       * arrival is quoted
       */
      public String arrival;
      
      /**
       * optional destination coordinates (x,y):edge
       */
      public String coordinates;
  }
  
  /**
   * Name: Change_Train_Struct
   *
   * What:
   *   The form of a message sent when the state of a train changes.
   */
  public class Change_Train_Struct {
	  /**
	   * the struct identifier - "Terminated:" or "TiedDown:" or "Rerun:"
	   */
      public String tag;
      
      /**
       * when the operation was requested
       */
      public String timeStamp;
      
      /**
       * the quoted train's symbol
       */
      public String trainID;
      
      /**
       * The Train's state as a bit set
       */
      public String state;
      
      /**
       * where the train is at - coordinates or "unknown", quoted
       */
      public String location;
      
      /**
       * debug information
       */
      public String debug;
  }
}
/* @(#)MsgStructs.java */