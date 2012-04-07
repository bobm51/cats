/**
 * Name: NetworkManager.java
 * 
 * What:
 *   This file defines a class that collects the information for controlling
 *   a network, coordinating taking the server up and down, setting the port,
 *   and refreshing the clients.
 *   <p>
 *   For efficiency, the port should be set in the XML file before the network
 *   is enabled.
 *   <p>
 *   I would have preferred to tailor the sub-components (the port text field,
 *   the enable checkbox, and the refresh button), but had existing base classes
 *   for the first two, so utilized them.
 *   
 * Special Considerations:
 */
package cats.gui;

import cats.network.TrainStatServer;


/**
 *   This file defines a class that collects the information for controlling
 *   a network, coordinating taking the server up and down, setting the port,
 *   and refreshing the clients.
 *   <p>
 *   For efficiency, the port should be set in the XML file before the network
 *   is enabled.
 *   <p>
 *   I would have preferred to tailor the sub-components (the port text field,
 *   the enable checkbox, and the refresh button), but had existing base classes
 *   for the first two, so utilized them.
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
public class NetworkManager {

  /**
   * is the Singleton.  
   */
  private static NetworkManager Singleton;
  
  /**
   * is the refresh requester
   */
  private ClientRefresh Refresher;
  
  /**
   * is the ctor
   */
  private NetworkManager() {
    Refresher = ClientRefresh.instance();
    Refresher.setEnabled(false);
  }

  /**
   * creates the Singleton NetworkManager object.
   * 
   * @return the Singleton.  It is created if it does not exist.
   */
  public static NetworkManager instance() {
      if (Singleton == null) {
          Singleton = new NetworkManager();
      }
      return Singleton;
  }

  /**
   * receives the new value for the port number
   * 
   * @param portNumber is the port number
   */
  public void newPortNumber(int portNumber) {
    if (StartTrainStat.instance().getFlagValue()) {
      TrainStatServer.instance().disable();
      startServer();      
    }
    // The disabled case can be ignored because the port number
    // will be sent to the server code just prior to it
    // being enabled.
  }

  /**
   * stimulates the network server to begin listening
   * for clients.  Before enabling the network connection
   * listener, it ensures that the port being monitored
   * is the one the user requested.
   */
  private void startServer() {
    TrainStatServer server = TrainStatServer.instance();
    if (server != null) {
      server.setPortNumber(ServerPort.instance().getPortValue());
      server.enable();
    }
  }
  
  /**
   * enables or disables the Train Status server.
   * @param enable is true to enable the Train Status server and
   * false to disable it.
   */
  public void enableNetworking(boolean enable) {
    Refresher.setEnabled(enable);
    if (enable) {
      startServer();
    }
    else {
      TrainStatServer.instance().disable();
    }
  }

  /**
   * is the stimulus to refresh the status on all network
   * clients.
   */
  public void refreshClients() {
    TrainStatServer.dumpAllStores(TrainStatServer.instance());
  }
}
/* @(#)NetworkManager.java */