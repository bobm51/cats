/*
 * Name: OperationsTrains
 * 
 * What:
 * This file is the interface to JMRI Operations for updating the trains when
 * CATS recognizes that a train has moved and retrieving the results (if any)
 * of switching.
 */
package cats.jmri;

import java.util.StringTokenizer;

import cats.gui.TraceFactory;
import cats.gui.TraceFlag;
import cats.network.OperationsConnection;
import cats.trains.Train;
import cats.trains.TrainStore;

/**
 * This file is the interface to JMRI Operations for updating the trains when
 * CATS recognizes that a train has moved and retrieving the results (if any)
 * of switching.
 *
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */
public class OperationsTrains {

	/**
	 * is the tag for network messages identifying an Operations message
	 */
	private static final String OPS_TAG = "OPERATIONS";
	
	/**
	 * is the tag for notifying Operations that a train moved or requesting its
	 * location
	 */
	private static final String MOVE_TAG = "TRAINLOCATION";

	/**
	 * is the tag for identifying a response to a Location query
	 */
	private static final String LOCATION_REPONSE_TAG = "LOCATION";
	
	/**
	 * is the tag for requesting all the locations that Operations knows
	 * about
	 */
	private static final String LOCATIONS_TAG = "LOCATIONS";
	
	/**
	 * is the tag for requesting the length of a train
	 */
	private static final String LENGTH_REQUEST_TAG = "TRAINLENGTH";

	/**
	 * is the tag for identifying a response to a length query
	 */
	private static final String LENGTH_RESPONSE_TAG = "LENGTH";
	
	/**
	 * is the tag for requesting the weight of a train
	 */
	private static final String WEIGHT_REQUEST_TAG = "TRAINWEIGHT";

	/**
	 * is the tag for identifying a response to a weight query
	 */
	private static final String WEIGHT_RESPONSE_TAG = "WEIGHT";
	
	/**
	 * is the tag for requesting the cars in a train
	 */
	private static final String CARS_REQUEST_TAG = "TRAINCARS";

	/**
	 * is the tag for identifying a response to a cars query
	 */
	private static final String CARS_RESPONSE_TAG = "CARS";
	
	/**
	 * is the tag for requesting the status of a train
	 */
	private static final String STATUS_REQUEST_TAG = "TRAINSTATUS";
	
	/**
	 * is the tag identifying a response to a Status request
	 */
	private static final String STATUS_RESPONSE_TAG = "STATUS";
	
	/**
	 * is the tag for requesting the list of trains
	 */
	private static final String TRAINS_TAG = "TRAINS";
	
	/**
	 * is the tag for requesting that a train be terminated
	 */
	private static final String TERMINATE_TAG = "TERMINATE";
	
    /**
     * are the separator characters between tokens
     */
    static private final String SEPARATORS = " \t\n\r";
    
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
		sendRequest(MOVE_TAG + " " + train + " " + location);
	}

	/**
	 * is a request to refresh all trains from Operations
	 */
	public void refreshTrains() {
		for (String train : TrainStore.TrainKeeper.listKeys()) {
			requestLength(train);
		}
	}
	
	/**
	 * tells Operations that a train has terminated
	 * @param train is the name of the train that terminated
	 */
	public void terminateTrain(String train) {
		sendRequest(TERMINATE_TAG + " " + train);
	}
	
	/**
	 * sends a request to Operations for the known location of a train.  The response is
	 * asynchronous and will come back in a later message.
	 * @param train is the train whose location is being requested
	 */
	public void requestLocation(String train) {
		sendRequest(MOVE_TAG + " " + train);
	}
	
	/**
	 * sends a request to Operations for the locations that it knows about.  The
	 * response is asynchronous and will come back in a later message.  The response
	 * will not be tagged; thus, CATS will have to remember that it sent the request.
	 */
	public void requestLocations() {
		sendRequest(LOCATIONS_TAG);
	}
	
	/**
	 * sends a request to Operations for the trains that it knows about.  The
	 * response is asynchronous and will come back in a later message.  The response
	 * will not be tagged; thus, CATS will have to remember that it sent the request.
	 */
	public void requestTrains() {
		sendRequest(TRAINS_TAG);
	}
	
	/**
	 * sends a request to Operations for the length of a train.  The response is
	 * asynchronous and will come back in a later message.
	 * @param train is the name of the train whose length is requested.
	 */
	public void requestLength(String train) {
		sendRequest(LENGTH_REQUEST_TAG + " " + train);
	}
	
	/**
	 * sends a request to Operations for the weight of a train.  The response is
	 * asynchronous and will come back in a later message.
	 * @param train is the name of the train whose weigth is requested.
	 */
	public void requestWeight(String train) {
		sendRequest(WEIGHT_REQUEST_TAG + " " + train);
	}
	
	/**
	 * sends a request to Operations for the cars in a train.  The response is
	 * asynchronous and will come back in a later message.
	 * @param train is the name of the train whose car count is requested.
	 */
	public void requestCars(String train) {
		sendRequest(CARS_REQUEST_TAG + " " + train);
	}
	
	/**
	 * sends a request to Operations for the status of a train.  The response is
	 * asynchronous and will come back in a later message.
	 * @param train is the name of the train whose status is requested.
	 */
	public void requestStatus(String train) {
		sendRequest(STATUS_REQUEST_TAG + " " + train);
	}
	
	/**
	 * formats and sends a request to Operations
	 * @param request is the request (@see jmri.jmris.simpleserver.SimpleServer.java and
	 * @see jmri.jmris.simpleserver.SimpleOperationsServer.java
	 */
	private void sendRequest(String request) {
		String req = OPS_TAG + " " + request;
		if (isEnabled()) {
			MyConnection.sendMessage(req);
			if (TraceOperations.getFlagValue()) {
				log.info("Sent to Operations: " + req);
			}
		}
	}

	/**
	 * parses and processes a response to a train location query.  The format
	 * of the response is: "OPERATIONS" <train> <location>.
	 * @param tokens is the response, with "OPERATIONS" removed.
	 */
	private void processLocationResponse(StringTokenizer tokens) {
		
	}
	
	/**
	 * parses and processes a response to a train length query.  The format
	 * of the response is: "OPERATIONS" <train> "LENGTH" <length>.
	 * To keep from overunning Operations, a Weight request will be sent whenever
	 * a Length response is received.
	 * @param tokens is the response, with "OPERATIONS" removed.
	 */
	private void processLengthResponse(StringTokenizer tokens) {
		String trainName = tokens.nextToken();
		String lenTag;
		String length;
		if (trainName != null) {
			lenTag = tokens.nextToken();
			if ((lenTag != null) && LENGTH_RESPONSE_TAG.equals(lenTag)) {
				length = tokens.nextToken();
				if (length != null) {
					TrainStore.TrainKeeper.changeField(trainName, Train.LENGTH, length);
				}
			}
			requestWeight(trainName);
		}
	}
	
	/**
	 * parses and processes a response to a train weight query.  The format
	 * of the response is: "OPERATIONS" <train> "WEIGHT" <weight>.
	 * To keep from overunning Operations, a Cars request will be sent whenever
	 * a Weight response is received.
	 * @param tokens is the response, with "OPERATIONS" removed.
	 */
	private void processWeightResponse(StringTokenizer tokens) {
		String trainName = tokens.nextToken();
		String wtTag;
		String weight;
		if (trainName != null) {
			wtTag = tokens.nextToken();
			if ((wtTag != null) && WEIGHT_RESPONSE_TAG.equals(wtTag)) {
				weight = tokens.nextToken();
				if (weight != null) {
					TrainStore.TrainKeeper.changeField(trainName, Train.WEIGHT, weight);
				}
			}
			requestCars(trainName);
		}
	}
	
	/**
	 * parses and processes a response to a train cars query.  The format
	 * of the response is: "OPERATIONS" <train> "CARS" <cars>.
	 * @param tokens is the response, with "OPERATIONS" removed.
	 */
	private void processCarsResponse(StringTokenizer tokens) {
		String trainName = tokens.nextToken();
		String carsTag;
		String cars;
		if (trainName != null) {
			carsTag = tokens.nextToken();
			if ((carsTag != null) && CARS_RESPONSE_TAG.equals(carsTag)) {
				cars = tokens.nextToken();
				if (cars != null) {
					TrainStore.TrainKeeper.changeField(trainName, Train.CARS, cars);
				}
			}
		}
	}
	
	/**
	 * parses and processes a response to a train status query.  The format
	 * of the response is: "OPERATIONS" <train> "STATUS" <status>.
	 * @param tokens is the response, with "OPERATIONS" removed.
	 */
	private void processStatusResponse(StringTokenizer tokens) {
		
	}
	
	/**
	 * parses a messages from Operations and processes that message
	 * @param response is the message from Operations
	 */
	public void processOperationsResponse(String response) {
		StringTokenizer tokens = new StringTokenizer(response, SEPARATORS);
		String token;
		if (TraceOperations.getFlagValue()) {
			log.info("Received from Operations: " + response);
		}
		token = tokens.nextToken();
		if (OPS_TAG.equals(token)) {
			if (response.contains(LOCATION_REPONSE_TAG)) {
				processLocationResponse(tokens);
			}
			else if (response.contains(LENGTH_RESPONSE_TAG)) {
				processLengthResponse(tokens);
			}
			else if (response.contains(WEIGHT_RESPONSE_TAG)) {
				processWeightResponse(tokens);
			}
			else if (response.contains(CARS_RESPONSE_TAG)) {
				processCarsResponse(tokens);
			}
			else if (response.contains(STATUS_RESPONSE_TAG)) {
				processStatusResponse(tokens);
			}
		}
	}
	static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(OperationsTrains.class.getName());
}
/* @(#)OperationsTrains.java */