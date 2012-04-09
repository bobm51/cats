/**
 * Name: NetworkProtocol
 * 
 * What:
 *   This file contains constants, classes, and method signatures
 *   for the messages passed back and forth between the Train Status
 *   server application and clients.
 *   
 * Special Considerations:
 */
package cats.network;

/**
 *   This file contains constants, classes, and method signatures
 *   for the messages passed back and forth between the Train Status
 *   server application and clients.
 * 
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
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

public interface NetworkProtocol {
  /**
   * is the universal end of line terminator
   */
  public static final String EOL = "\r\n";

  /**
   * is the name of the service
   */
  public static final String CLIENT_NAME = "TrainStat";
  
  /**
   * is the default port to communicate with the server over.
   */
  public static final int DEFAULT_PORT = 54321; 

  /**
   * is the String sent by the server when the connection
   * is taken down.
   */
  public static final String DISCONNECT = "Disconnect";
  
  /**
   * is the String that identifies formating
   */
  public static final String FORMAT_TAG = "Format\t";
  
  /**
   * is the String that identifies data
   */
  public static final String DATA_TAG = "Data\t";
  
  /**
   * is the method invoked to send something over the network
   * 
   * @param line is the message to send
   */
  public void sendMessage(String line);

}
/* @(#)NetworkProtocol.java */