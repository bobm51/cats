/**
 * Name: TrainStatServer
 * 
 * What:
 *   This file contains a singleton object that handles accepting connection
 *   requests from remote computers and distributing changes in the trains'
 *   status to them.
 *   <p>
 *   This codes is derived from example16-1 (EchoServerThreaded) from "Javs
 *   Cookbook" by Ian Darwin.
 *   <p>
 *   The JMRI loconetovertcp code also provided a guide for the architecture
 *   of this class.
 *   <p>
 *   This class spawns a controlling thread for as long as the server is enabled.
 *   The controlling thread accepts connections from clients and spawns a
 *   ConnectionServer thread for each connection.  Each ConnectionServer
 *   thread spawns an additional thread attched to a message queue.  Therefore,
 *   the way CATS sends out updates is that it sends an update to this object,
 *   which distributes it to the child ConnectionServer thread via their queues.
 *   
 * Special Considerations:
 */
package cats.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.LinkedList;

import cats.common.Constants;
import cats.common.VersionList;
import cats.crew.Callboard;
import cats.jobs.JobStore;
import cats.layout.Logger;
import cats.layout.store.AbstractStore;
import cats.layout.store.AbstractStoreWatcher;
import cats.trains.TrainStore;

/**
 *   This file contains a singleton object that handles accepting connection
 *   requests from remote computers and distributing changes in the trains'
 *   status to them.
 *   <p>
 *   This codes is derived from example16-1 (EchoServerThreaded) from "Java
 *   Cookbook" by Ian Darwin.
 *   <p>
 *   The JMRI loconetovertcp code also provided a guide for the architecture
 *   of this class.
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

public class TrainStatServer implements ConnectionListener,
NetworkProtocol {
  /**
   * is the name of the TrainStat service
   */
  private static final String SERVICENAME = "TrainStatus";
  
  /**
   * is the name of the message distributor thread
   */
  private static final String DISTRIBUTOR_NAME = "Distributor";
  
  /**
   * is the Singleton.  It manages the connections.
   */
  private static TrainStatServer Singleton;
  
  /**
   * is the TCP/IP socket through which messages are received
   * from the clients.
   */
  private ServerSocket serverSocket;
  
  /**
   * is the queue by which objects on the application queue up messages
   * for the remote end.
   */
  private LinkedList<String> MsgQueue;
  
  /**
   * is the TCP/IP information on the TrainStat clients.
   */
  private LinkedList<ConnectionServer> Clients;
  
  /**
   * is the asynchronous thread which empties the msgQueue and
   * send messages to the remote end.
   */
  private Thread TxThread;
  
  /**
   * is the thread that listens to the receiver for clients
   * registering.
   */
  private Thread SocketListener;
  
  /**
   * is the port number currently in use.
   */
  private int PortNumber = DEFAULT_PORT;
  
  /**
   * is the bridge to the Stores.
   */
  private NetworkStoreWatcher Eyes;
  
  /**
   * is the ctor.
   */
  private TrainStatServer() {
    Clients = new LinkedList<ConnectionServer>() ;
    MsgQueue = new LinkedList<String>();
    Eyes = new NetworkStoreWatcher();
  }
  
  /**
   * creates the Singleton NetworkManager object.
   * 
   * @return the Singleton.  It is created if it does not exist.
   */
  public static TrainStatServer instance() {
    if (Singleton == null) {
      Singleton = new TrainStatServer();
    }
    return Singleton;
  }
  
  /**
   * is called to retrieve the port number.
   * @return the port number
   */
  public int getPortNumber() {
    return PortNumber;
  }
  
  /**
   * is invoked to change the TCP/IP port number
   * used for talking to the TrainStat server.
   * @param port is the port
   */
  public void setPortNumber(int port){
    PortNumber = port;
  }
  
  /**
   * starts up the TrainStatServer
   *
   *@param portNumber is the port number to listen on.
   */
  
  /**
   * starts up the TranStat server.
   */
  public void enable(){
    if (SocketListener == null) {
      MsgQueue.clear();
      SocketListener = new Thread(new ClientListener()) ;
      SocketListener.setDaemon(true);
      SocketListener.setName(SERVICENAME);
      SocketListener.start();
      TxThread = new Thread(new TxHandler());
      TxThread.setDaemon(true);
      TxThread.setName(DISTRIBUTOR_NAME);
      TxThread.start();
      Eyes.register();
    }
  }
  
  /**
   * stops the TrainStat service.
   */
  public void disable(){
    if (SocketListener != null) {
      Eyes.unregister();
      SocketListener.interrupt();
      SocketListener = null ;
      try {
        if( serverSocket != null )
          serverSocket.close();
      }
      catch (IOException ex) {
      }
      
      TxThread.interrupt();
      
      // Now close all the client connections
      Object[] clientsArray ;
      
      synchronized( Clients ){
        clientsArray = Clients.toArray();
        Clients.clear();
      }
      for( int i = 0; i < clientsArray.length ; i++ ) {
        ((NetworkProtocol) clientsArray[i]).sendMessage(DISCONNECT);
        // Sleep awhile?  This is called from the dispatch thread.
        ((ConnectionServer)clientsArray[i]).interrupt();
      }
    }
  }

  /**
   * returns the connection status
   * @return true if ClientListener is listening to
   * the socket.
   */
  public boolean isConnected() {
    return (SocketListener != null);
  }
  
  /**
   * is an inner class that listens to the TCP/IP port
   * for messages from one or more TrainStat clients.
   */
  class ClientListener implements Runnable {
    
    /**
     * is the thread that listens to the socket
     */
    public void run(){
      Socket newClientConnection;
      ConnectionServer client;
      try {
        serverSocket = new ServerSocket(getPortNumber());
      }
      catch (IOException e) {
        String diagnostic = "Failed to open server socket: " +
        e.getMessage();
        log.warn(diagnostic);
      }
//    jmri.util.SocketUtil.setReuseAddress(serverSocket, true);
      if (serverSocket != null) {
        try {
          while (!SocketListener.isInterrupted()) {
            newClientConnection = serverSocket.accept();
            client = new ConnectionServer(newClientConnection);
            client.addListener(TrainStatServer.instance());
            addClient(client);
            client.sendMessage(CLIENT_NAME + Constants.FS + "Version" +
                Constants.FS + Logger.VERSION + Constants.FS +
                Constants.CATS_TAG + Constants.FS_STRING + VersionList.CATS_VERSION);
            dumpAllStores(client);
          }
          serverSocket.close();
        }
        catch (IOException ex) {
          if (ex.toString().indexOf("socket closed") == -1) {
            log.error(SERVICENAME + ": IO Exception: ", ex);
          }
        }
        serverSocket = null;
      }
      SocketListener = null;
    }
  }
  
  /**
   * registers a TrainStat client
   * @param handler is the client
   */
  protected void addClient(ConnectionServer handler) {
    synchronized (Clients ) {
      Clients.add(handler);
    }
  }
  
  /**
   * unregisters a TrainStat client
   * @param handler is the client
   */
  protected void removeClient(AbstractConnection handler) {
    synchronized(Clients) {
      Clients.remove(handler);
    }
  }
  
  /**
   * is called to retrieve the number of TranStat clients
   * @return the number of clients listening for the status
   * of trains.
   */
  public int getClientCount() {
    synchronized (Clients) {
      return Clients.size() ;
    }
  }

  /**
   * is called when the connection to a socket handler thread drops
   */
  public void connectionDropped(AbstractConnection connection) {
    removeClient(connection);
  }
  
  /**
   * is an inner class for receiving messages from the application
   * and sending them to the remote ends.
   */
  private class TxHandler implements Runnable {
    String  msg;
    StringBuffer outBuf = new StringBuffer();

    /**
     * is the ctor
     * @param creator
     */
    public TxHandler() {
    }

    /**
     * waits for a message to send, then sends it.
     */
    public void run() {
      try {
        while (true) {
          msg = null;

          synchronized (MsgQueue) {
            if(MsgQueue.isEmpty()) {
              MsgQueue.wait();
            }

            if (!MsgQueue.isEmpty()) {
                msg = MsgQueue.removeFirst();
            }
          }

          if (msg != null) {
            outBuf.setLength(0);
            outBuf.append(msg.toString());
//            outBuf.append(EOL);
            synchronized(Clients) {
              for (Iterator<ConnectionServer> iter = Clients.iterator(); iter.hasNext(); ) {
                iter.next().sendMessage(outBuf.toString());
              }
            }
          }
        }
      }
      catch (InterruptedException ex) {
        log.debug(DISTRIBUTOR_NAME + ": Interrupted Exception" );
      }
        // Interrupt the Parent to let it know we are exiting for some reason
      msg = null;
      outBuf = null;
      TxThread = null;
      log.info(DISTRIBUTOR_NAME + ": Exiting" );
    }
  }

  /**
   * is the public interface for sending messages over the network
   * @param msg is the message to send
   */
  public void sendMessage(String msg){
    synchronized(MsgQueue)
    {
      MsgQueue.add(msg);
      MsgQueue.notify();
    }
  }

  /************************************************************************
   * The next methods handle extracting information from the data stores
   */
  /**
   * dumps the format and contents of a Store
   * 
   * @param store is the data store being sent
   * @param connection is where to send the store
   */
  private static void dumpStore(AbstractStore store, NetworkProtocol connection) {
    String storeId = store.getFieldID() + Constants.FS;
//    connection.sendMessage(FORMAT_TAG + storeId + store.dumpFieldNames());
    for (Iterator<String> iter = store.dumpFieldContents().iterator(); iter.hasNext(); ) {
      connection.sendMessage(AbstractStoreWatcher.buildAddString(storeId +
          iter.next()));
    }
    storeId = store.getDataID() + Constants.FS_STRING;
    for (Iterator<String> iter = store.dumpStoreContents().iterator(); iter.hasNext(); ) {
      connection.sendMessage(AbstractStoreWatcher.buildAddString(storeId +
          iter.next()));
    }
  }
  
  /**
   * sends the state of all stores to the far end(s)
   * 
   * @param connection is where to send the stores
   */
  public static void dumpAllStores(NetworkProtocol connection) {
//    Vector inactive = TrainStore.TrainKeeper.getRun();
    dumpStore(TrainStore.TrainKeeper, connection);
//    for (Iterator iter = inactive.iterator(); iter.hasNext(); ) {
//      connection.sendMessage(AbstractStoreWatcher.buildChangeString(((Train)iter.next()).buildStatusString()));
//    }
    dumpStore(JobStore.JobsKeeper, connection);
    dumpStore(Callboard.Crews, connection);
  }
  
  /**
   * The next class is private to TrainStatServer.  It is the bridge
   * between the Stores and the network.  It registers itself with the
   * Stores and forwards all messages to the TrainStat clients.
   */
  private class NetworkStoreWatcher extends AbstractStoreWatcher {
    
    /**
     * This method takes any messages constructed by the AbstractStoreWatcher
     * and passes them to the network distributor.
     * 
     * @param msg is a message from an AbstractStore
     */
    protected void forward(String msg) {
      sendMessage(msg);
    }
  }
  
  static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(
      TrainStatServer.class.getName());
}
/* @(#)TrainStatServer.java */