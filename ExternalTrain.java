/*
 * Name: ExternalTrain
 * 
 * What:
 * This is a Java interface for specifying the functions that can be performed
 * on a data structure external to CATS that represents a train.  Its initial
 * implementation is to extract and manipulate Operations trains, but is
 * provided so that CATS could wrok with other external entities in the future.
 */
package cats.trains;

/**
 * This is a Java interface for specifying the functions that can be performed
 * on a data structure external to CATS that represents a train.  Its initial
 * implementation is to extract and manipulate Operations trains, but is
 * provided so that CATS could wrok with other external entities in the future.
 *
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */
public interface ExternalTrain {

	/**
	 * is invoked to tell the external database that the train has moved.
	 * @param location is a String naming where the train moved to.  CATS will
	 * use the "Station" Strings in Block definitions as location names.  They
	 * should correspond to names known by the database.  It is undefined what
	 * shouod happen if the database does not know about location or the train
	 * is not expected to visit location.  The database could safely return false.
	 * @return true if the database accepted the train moving to location
	 */
	public boolean moveTrain(String location);

	/**
	 * is invoked to create the initial switch list for a train.
	 * @return true if the train was built successfully.
	 */
	public boolean build();
	
	/**
	 * is invoked to tell the database that the train has completed its
	 * run.
	 * @return true if the termination was successful
	 */
	public boolean terminate();
	
	/**
	 * is invoked to rerun a train.  This essentially resets the database entry
	 * for the train.
	 * @return true if the train was reset
	 */
	public boolean rerun();
	
	/**
	 * is invoked to retrieve the status of the train.  The status is dependent
	 * upon what the database return.  For Operations, see jmri.jmrit.operations.trains.train
	 * @return the status of the train as a String
	 */
	public String getStatus();
	
	/**
	 * is invoked to retrieve the current length of the train.
	 * @return the length.  If the query failed, -1 is returned.
	 */
	public int getLength();
	
	/**
	 * is invoked to retrieve the current weight of the train.
	 * @return the weight.  If the query failed, -1 is returned.
	 */	
	public int getWeight();
}
/* @(#)ExternalTrain.java */