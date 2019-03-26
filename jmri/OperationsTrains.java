/*
 * Name: OperationsTrains
 * 
 * What:
 * This file is the interface to JMRI Operations for updating the trains when
 * CATS recognizes that a train has moved and retrieving the results (if any)
 * of switching.
 * <p>
 * The format of the requests is defined in jmri.jmris.simpleserver.SimpleOperationsServer.
 * The format of the responses is defined in jmri.jmris.AbstractOperationsServer.
 * <p>
 * In general, the format is:
 * <ul>
 * <li>OPERATIONS</li>
 * <li>' ' (space)</li>
 * <li>a request/response tag</li>
 * <li>' ' (space)</li>
 * <li>the operations name of a train (CATS train symbol)</li>
 * <li>jmri.jmris.simpleserver.SimpleOperationsServer.DELIMITER</li>
 * <li>any additional data</li>
 * </ul>
 * Lists (e.g. trains and locations) are a little bit different.  Each element is on a line by itself.
 * The last element is followed by a period.
 */
package cats.jmri;

import java.util.ArrayList;
import javax.management.Attribute;

import cats.gui.TraceFactory;
import cats.gui.TraceFlag;
import cats.network.OperationsConnection;
import cats.trains.Train;
import cats.trains.TrainStore;

/**
 * This file is the interface to JMRI Operations for updating the trains when
 * CATS recognizes that a train has moved and retrieving the results (if any)
 * of switching.
 * <p>
 * The format of the requests and responses is defined in jmri.jmris.simpleserver.SimpleOperationsServer.
 * <p>
 * In general, the format is:
 * <ul>
 * <li>OPERATIONS</li>
 * <li>"\t" (tab) </li>
 * <li>TRAIN=train </li>
 * <li>"\t" (tab) </li>
 * <li>a number of "\t"tag=value pairs</li>
 * </ul>
 * Lists (e.g. trains and locations) are a little bit different.  Each element is on a line by itself.
 * The last element is followed by a period.
 *
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2011, 2012</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */
public class OperationsTrains {

	/**
	 * taken from Operations
	 */
	final String REQUEST_DELIMITER = " , ";
	
  /**
   * creates the checkbox under Appearance->Trace Items for controlling tracing
   */
  private static TraceFlag TraceOperations = null;
  
  /**
   * is the network connection to Operations
   */
  private OperationsConnection MyConnection = null;
  
  /**
   * is the singleton bridge to Operations
   */
  private static OperationsTrains Singleton = null;

  /**
   * is the ctor
   */
  private OperationsTrains() {
  }
  
  /**
   * is the only way to access the singleton.  If it does not exist, it is created.
   * Because it is created once, the first access should be after JMRI Operations
   * has loaded.
   * @return the Singleton
   */
  public static OperationsTrains instance() {
    if (Singleton == null) {
      Singleton = new OperationsTrains();
      TraceOperations = TraceFactory.Tracer.createTraceItem("Operations", "OPS_TRACE");
    }
    return Singleton;
  }

  /**
   * sets a reference to the Operations connection
   * @param connect is the connection.  It will be null when the connection is dropped.
   * @return true if the new connection is null or the old connection is null.  In other
   * words, false is returned if a connection is being replaced without this object
   * being notified that the existing connection was dropped.
   */
  public boolean setConnection(OperationsConnection connect) {
    if ((MyConnection != null) && (connect != null)) {
      MyConnection = connect;
      return false;
    }
    MyConnection = connect;
    return true;
  }
  
  /**
   * is invoked to inquire as to if Operations is enabled or not
   * @return true if Operations is enabled
   */
  public boolean isEnabled() {
    return MyConnection != null;
  }
  
  /**
   * tells Operations that a train has moved
   * @param train is the name of the train that moved
   * @param location is the train's new location
   */
  public void moveTrain(String train, String location) {
    ArrayList<Attribute> request = new ArrayList<Attribute>();
    request.add(new Attribute(jmri.jmris.simpleserver.SimpleOperationsServer.TRAIN, train));
    request.add(new Attribute(jmri.jmris.simpleserver.SimpleOperationsServer.TRAINLOCATION, location));
    requestAll(request);
    sendRequest(request);
  }

  /**
   * is a request to refresh all trains from Operations
   */
  public void refreshTrains() {
    for (String train : TrainStore.TrainKeeper.listKeys()) {
      requestStats(train);
    }
  }

  /**
   * constructs a list of requests for a trains attributes.  This does not add the train's name, so it
   * is intended to add things to a list and not be the complete list.
   * @param list is the ArrayList where the list is constructed
   */
  private void requestAll(ArrayList<Attribute> list) {
    list.add(new Attribute(jmri.jmris.simpleserver.SimpleOperationsServer.TRAINLENGTH, null));
    list.add(new Attribute(jmri.jmris.simpleserver.SimpleOperationsServer.TRAINWEIGHT, null));
    list.add(new Attribute(jmri.jmris.simpleserver.SimpleOperationsServer.TRAINCARS, null));
    list.add(new Attribute(jmri.jmris.simpleserver.SimpleOperationsServer.TRAINLEADLOCO, null));
    list.add(new Attribute(jmri.jmris.simpleserver.SimpleOperationsServer.TRAINCABOOSE, null));
}
  /**
   * tells Operations that a train has terminated
   * @param train is the name of the train that terminated
   */
  public void terminateTrain(String train) {
    ArrayList<Attribute> request = new ArrayList<Attribute>();
    request.add(new Attribute(jmri.jmris.simpleserver.SimpleOperationsServer.TRAIN, train));
    request.add(new Attribute(jmri.jmris.simpleserver.SimpleOperationsServer.TERMINATE, null));
    sendRequest(request);
  }
  
  /**
   * sends a request to Operations for the locations that it knows about.  The
   * response is asynchronous and will come back in a later message.  The response
   * will not be tagged; thus, CATS will have to remember that it sent the request.
   */
  public void requestLocations() {
    ArrayList<Attribute> request = new ArrayList<Attribute>();
    request.add(new Attribute(jmri.jmris.simpleserver.SimpleOperationsServer.LOCATIONS, null));
    sendRequest(request);
  }
  
  /**
   * sends a request to Operations for the trains that it knows about.  The
   * response is asynchronous and will come back in a later message.  The response
   * will not be tagged; thus, CATS will have to remember that it sent the request.
   */
  public void requestTrains() {
    ArrayList<Attribute> request = new ArrayList<Attribute>();
    request.add(new Attribute(jmri.jmris.simpleserver.SimpleOperationsServer.TRAINS, null));
    sendRequest(request);
  }
  
  /**
   * sends a request to Operations for the status of a train.  The response is
   * asynchronous and will come back in a later message.
   * @param train is the name of the train whose status is requested.
   */
  public void requestStats(String train) {
    ArrayList<Attribute> request = new ArrayList<Attribute>();
    request.add(new Attribute(jmri.jmris.simpleserver.SimpleOperationsServer.TRAIN, train));
    requestAll(request);
    sendRequest(request);
  }
  
  /**
   * formats and sends a request to Operations
   * @param request is a list of requests
   */
  private void sendRequest(ArrayList<Attribute> request) {
    String req = jmri.jmris.simpleserver.SimpleOperationsServer.constructOperationsMessage(request);
    if (isEnabled()) {
      MyConnection.sendMessage(req);
      if (TraceOperations.getFlagValue()) {
        log.info("Sent to Operations: " + req);
      }
    }
  }

  /**
   * parses and processes a response to a train location query.
   * @param trainName is the train's symbol in CATS
   * @param tokens is the new location
   */
  private void processLocationResponse(String trainName, String Location) {
    
  }
  
  /**
   * updates a train's length
   * @param train is the train's symbol in CATS
   * @param length is the new length
   */
  private void processLengthResponse(String trainName, String length) {
    if ((trainName != null) && (length != null)) {
          TrainStore.TrainKeeper.changeField(trainName, Train.LENGTH, length);
    }
  }
  
  /**
   * updates a trains weight
   * @param trainName is train's symbol in CATS 
   * @param weight is the new weight
   */
  private void processWeightResponse(String trainName, String weight) {
    if ((trainName != null) && (weight != null)) {
      TrainStore.TrainKeeper.changeField(trainName, Train.WEIGHT, weight);
    }
  }
  
  /**
   * updates the number of cars in a train
   * @param trainName is train's symbol in CATS 
   * @param cars is the new number of cars
   */
  private void processCarsResponse(String trainName, String cars) {
    if ((trainName != null) && (cars != null)) {
      TrainStore.TrainKeeper.changeField(trainName, Train.CARS, cars);
    }
  }
  
  /**
   * updates the lead engine number in a train
   * @param trainName is train's symbol in CATS 
   * @param cars is the new lead engine number
   */
  private void processEngineResponse(String trainName, String engine) {
    if ((trainName != null) && (engine != null)) {
      TrainStore.TrainKeeper.changeField(trainName, Train.ENGINE, engine);
    }
  }
  
  /**
   * updates the caboose in a train
   * @param trainName is train's symbol in CATS 
   * @param caboose is the new caboose
   */
  private void processCabooseResponse(String trainName, String caboose) {
    if ((trainName != null) && (caboose != null)) {
      TrainStore.TrainKeeper.changeField(trainName, Train.CABOOSE, caboose);
    }
  }
  
  /**
   * updates a train's status
   * @param trainName is the train's symbol in CATS
   * @param status is the train's new status
   */
  private void processStatusResponse(String trainName, String status) {
    
  }
  
  /**
   * parses a messages from Operations and processes that message
   * @param response is the message from Operations
   */
  public void processOperationsResponse(String response) {
    ArrayList<Attribute> responses = parseOperationsMessage(response);
    String trainName = null;
    String tag;
    String value;
    if (TraceOperations.getFlagValue()) {
      log.info("Received from Operations: " + response);
    }
    for (Attribute reply : responses) {
      tag = reply.getName();
      value = (String) reply.getValue();
      if (jmri.jmris.simpleserver.SimpleOperationsServer.TRAIN.equals(tag)) {
        trainName = (String) reply.getValue();
      }
      else if (trainName != null) {
        if (jmri.jmris.simpleserver.SimpleOperationsServer.TRAINLOCATION.equals(tag)) {
          processLocationResponse(trainName, value);
        }
        else if (jmri.jmris.simpleserver.SimpleOperationsServer.TRAINLENGTH.equals(tag)) {
          processLengthResponse(trainName, value);
        }
        else if (jmri.jmris.simpleserver.SimpleOperationsServer.TRAINWEIGHT.equals(tag)) {
          processWeightResponse(trainName, value);
        }
        else if (jmri.jmris.simpleserver.SimpleOperationsServer.TRAINCARS.equals(tag)) {
          processCarsResponse(trainName, value);
        }
        else if (jmri.jmris.simpleserver.SimpleOperationsServer.TRAINSTATUS.equals(tag)) {
          processStatusResponse(trainName, value);
        }
        else if (jmri.jmris.simpleserver.SimpleOperationsServer.TRAINLEADLOCO.equals(tag)) {
          processEngineResponse(trainName, value);
        }
        else if (jmri.jmris.simpleserver.SimpleOperationsServer.TRAINCABOOSE.equals(tag)) {
          processCabooseResponse(trainName, value);
        }
      }
    }
  }
  
  /**
   * taken from Operations before it is deprecated
   * @param message is the message from operations
   * @return an ArrayList containing the message as pairs
   */
  public ArrayList<Attribute> parseOperationsMessage(String message) {
      ArrayList<Attribute> contents = new ArrayList<Attribute>();
      int start;
      int end;
      int equals;
      String request;
      if ((message != null) && message.startsWith(jmri.jmris.simpleserver.SimpleOperationsServer.OPERATIONS)) {
          for (start = message.indexOf(REQUEST_DELIMITER);
                  start > 0;
                  start = end) {  // step through all the requests/responses in the message
              start += REQUEST_DELIMITER.length();
              end = message.indexOf(REQUEST_DELIMITER, start);
              if (end > 0) {
                  request = message.substring(start, end);
              } else {
                  request = message.substring(start, message.length());
              }

              //convert a request/response to an Attribute and add it to the result
              equals = request.indexOf(jmri.jmris.simpleserver.SimpleOperationsServer.FIELDSEPARATOR);
              if ((equals > 0) && (equals < (request.length() - 1))) {
                  contents.add(new Attribute(request.substring(0, equals), request.substring(equals + 1, request.length())));
              } else {
                  contents.add(new Attribute(request, null));
              }
          }
      }
      return contents;
  }

  static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(OperationsTrains.class.getName());
}
/* @(#)OperationsTrains.java */