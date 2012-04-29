/* Name: RequestParser.java
 *
 * What:
 *   This class defines a Singleton facility for receiving request messages
 *   from TrainStatus clients.  It parses the messages and updates the
 *   TrainStore (or any other stores) as appropriate.  The parsing is based
 *   on the CATS ReplayHandler.
 *
 * Special Considerations:
 *   For this to work right, the TrainStat client and server should be using
 *   the same version of the event logger, so that the server can decode
 *   the messages sent by the client.
 * 
 */
package cats.network;

import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import cats.common.Constants;
import cats.crew.Callboard;
import cats.layout.store.AbstractStore;
import cats.layout.store.FieldPair;
import cats.layout.store.GenericRecord;
import cats.layout.store.StoredObject;
import cats.trains.Train;
import cats.trains.TrainStore;

/**
 *   This class defines a Singleton facility for receiving request messages
 *   from TrainStatus clients.  It parses the messages and updates the
 *   TrainStore (or any other stores) as appropriate.  The parsing is based
 *   on the CATS ReplayHandler.
 * 
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2008, 2009, 2010, 2011</p>
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
public class RequestParser {

  /**
   * is the singleton parser
   */
  private static RequestParser TheParser;

  /**
   * is a flag used for debugging this file
   */
  private boolean Debug = false;

  /**
   * are the separator characters between tokens
   */
  static private final String SEPARATORS = Constants.FS_STRING;

  /**
   * is a common access point for finding the Singleton.
   * @return the Singleton StatusParser
   */
  public static RequestParser instance() {
    if (TheParser == null) {
      TheParser = new RequestParser();
    }
    return TheParser;
  }

  /**
   * Parses a request, received from a CATS client.
   * @param line is the request
   */
  public void parseLine(String line){
    String tag;
    String result = null;
    StringTokenizer tokens;
    tokens = new StringTokenizer(line, SEPARATORS);
    if (Debug) {
      System.out.println(line);
    }
    try {
      tag = tokens.nextToken();
      if (Constants.ADD_TRAIN_REQUEST.equals(tag)) {
//        result = processAddTrain(tokens);
        result = processAddRequest(tokens, TrainStore.TrainKeeper);
      }
      else if (Constants.CHANGE_TRAIN_REQUEST.equals(tag)) {
        result = processChangeTrain(tokens);
      }
      else if (Constants.ADD_CREW_REQUEST.equals(tag)) {
        result = processAddRequest(tokens, Callboard.Crews);
      }
      else if (Constants.CHANGE_CREW_REQUEST.equals(tag)) {
        result = processChangeRequest(tokens, Callboard.Crews);
      }
      else if (Constants.DELETE_CREW_REQUEST.equals(tag)) {
        result = processDeleteRequest(tokens, Callboard.Crews);
      }
      else {
        result = new String("Unrecognized TrainStat request " + Constants.QUOTE + line + Constants.QUOTE);
      }
      if (result != null) {
        System.out.println(tag + " " + result);
        log.info(tag + " " + result);
      }
    }
    catch (NoSuchElementException nsee) {
      System.out.println("received bad request <" + line + ">");
    }
  }

//  /**
//   * parses an "add train request" and processes it.  The format of the request is
//   * a list of all the fields, as properties ("tag=value")
//   * @param tokens is the request, broken into tokens
//   * @return null if there are no problems or an error string
//   * if something is wrong
//   */
//  private String processAddTrain(StringTokenizer tokens) {
//    GenericRecord newTrain = new GenericRecord();
//    toFieldPairs(tokens, newTrain);
//    TrainStore.TrainKeeper.addUntrustedRecord(newTrain);
//    return null;
//  }

  /**
   * parses a "change train request" and processes it.  The format of the request is
   * a list of all the fields, as properties ("tag=value").  Train change records
   * cannot be handled like other kinds because the Status field does not exist explicitly.
   * It is synthesized from a bit set.
   * 
   * @param tokens is the request, broken into tokens
   * @return null if there are no problems or an error string
   * if something is wrong
   */
  private String processChangeTrain(StringTokenizer tokens) {
    String oldStatus = null;
    String newStatus;
    String trainName = null;
    Train oldTrain = null;
    GenericRecord newTrain = new GenericRecord();
    toFieldPairs(tokens, newTrain);
    newStatus = (String) newTrain.findValue(Train.WORKING_STATUS);
    trainName = (String) newTrain.findValue(Train.TRAIN_SYMBOL);
    if (trainName != null) {
      oldTrain = TrainStore.TrainKeeper.getTrain(trainName);
      if (oldTrain != null) {
        oldStatus = oldTrain.getTrainStatus();
      }
    }
    TrainStore.TrainKeeper.changeRecord(newTrain);
    if ((oldTrain != null) && (newStatus != null)) {
      if ((oldStatus != null) && !oldStatus.equals(newStatus)) {
        if (newStatus.equals(Constants.TERMINATED_STATE)) {
          oldTrain.remove();
        }
        else if (newStatus.equals(Constants.TIED_DOWN_STATE)) {
          oldTrain.tieDown();
        }
        else if (newStatus.equals(Constants.WAITING_STATE)) {
          oldTrain.rerun();
        }
      }
    }
    return null;
  }

  /**
   * handles a request to add a record to a store.  The format of the request is
   * a list of all the fields, as properties ("tag=value").
   * @param tokens is the input received from a TrainStat client, with the request
   * identifier stripped off
   * @param store is the store that the record is being added to
   * @returns an error string if something goes wrong and the record cannot be
   * added
   */
  private String processAddRequest(StringTokenizer tokens, AbstractStore store) {
    GenericRecord newRecord = new GenericRecord();
    toFieldPairs(tokens, newRecord);
    store.addUntrustedRecord(newRecord);
    return null;
  }
 
  /**
   * handles a request to change the fields in a record.  The format of the request is
   * a list of all the fields, as properties ("tag=value").  The first field contains
   * the key.
   * @param tokens is the input received from a TrainStat client, with the request
   * identifier stripped off
   * @param store is the store containing the record that is beiong changed
   * @returns an error string if something goes wrong and the record cannot be
   * changed
   */  
  private String processChangeRequest(StringTokenizer tokens, AbstractStore store) {
    GenericRecord changedRecord = new GenericRecord();
    toFieldPairs(tokens, changedRecord);
    store.changeRecord(changedRecord);
    return null;
  }
  
  /**
   * handles a request to delete a record.  The format of the request is a String
   * with the key field value as it only parameter.
   * @param tokens is the input received from a TrainStat client, with the request
   * identifier stripped off
   * @param store is the store that the record is being added to
   * @returns an error string if something goes wrong and the record cannot be
   * added
   */  
  private String processDeleteRequest(StringTokenizer tokens, AbstractStore store) {
    GenericRecord delRecord;
    String keyField = tokens.nextToken();
    StoredObject so;
    if (keyField != null) {
      delRecord = store.findRecord(store.getKeyTag(), keyField);
      if (delRecord == null) {
        log.warn("Could not find " + keyField + " in " + store.getTag());
      }
      else {
        so = delRecord.getActiveReference();
        if (so != null) {
          so.destructor();
        }
      }
    }
    return null;
  }
  
  /**
   * constructs an GenericRecord of FieldPairs from the tokenized String
   * There will be one FieldPair per token.
   * Because of the FieldPair constructor, format checking is very loose.
   * @param tokens is the tokenized String
   * @param rec is the blank AbstractRecord
   */
  private void toFieldPairs(StringTokenizer tokens, GenericRecord rec) {
      while (tokens.hasMoreTokens()) {
          rec.add(new FieldPair(tokens.nextToken()));
      }
  }  
  
  static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(
      RequestParser.class.getName());
}
/* @(#)RequestParser.java */