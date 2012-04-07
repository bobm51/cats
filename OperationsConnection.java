/* Name: OperationsConnection.java
 *
 * What:
 *   This file manages the connection between a JMRI Operations application
 *   and CATS.
 *  
 *  Special Considerations:
 */
package cats.network;

import java.net.Socket;

import cats.jmri.OperationsTrains;



/**
 *   This file manages the connection between a JMRI Operations application
 *   and CATS.
 * 
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */

public class OperationsConnection extends AbstractConnection {
 
    /**
     * is the ctor
     * @param s is the socket forming the connection to the remote
     * end
     */
    OperationsConnection(Socket s) {
        super(s);
    }

    /**
     * handles a message from Operations regarding a Train.
     * 
     * @param msg is the message from Operations
     */
    protected void processMessage(String msg) {
    	OperationsTrains.instance().processOperationsResponse(msg);
    }

    static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(OperationsConnection.class.getName());
}
/* @(#)OperationsConnection.java */