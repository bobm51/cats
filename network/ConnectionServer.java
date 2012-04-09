/**
 * Name: ConnectionServer
 * 
 * What:
 *   This file contains an object that handles traffic to and from a
 *   Train Status client.
 *   <p>
 *   This code is derived from the Handler class in example 16-1 
 *   EchoServerThreaded) from "Java Cookbook" by Ian Darwin.
 *   <p>
 *   The JMRI loconetovertcp code also provided a guide for the architecture
 *   of this class.
 *  
 *   
 * Special Considerations:
 */
package cats.network;

import java.net.Socket;

/**
 *   This file contains an object that handles traffic to and from a
 *   Train Status client.
 *   <p>
 *   This code is derived from the Handler class in example 16-1 
 *   EchoServerThreaded) from "Java Cookbook" by Ian Darwin.
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

public class ConnectionServer extends AbstractConnection {

  /**
   * is the ctor
   * @param s is the socket that forms the connection
   */
  ConnectionServer(Socket s) {
    super(s);
  }
  
  /**
   * processes a message received from the client
   * @param msg
   */
  protected void processMessage(String msg) {
	    log.debug(msg);
	    RequestParser.instance().parseLine(msg);
  }
static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(
    ConnectionServer.class.getName());
}
/* @(#)ConnectionServer.java */
