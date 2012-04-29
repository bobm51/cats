/**
 * Name: AbstractConnection
 * 
 * What:
 *   This file contains an object that handles socket traffic.  It has two
 *   threads - one for listening to the socket and one for pulling
 *   messages from a message queue and sending them to the remote connection.
 *   <p>
 *   The JMRI loconetovertcp code provided a guide for the architecture
 *   of this class.
 *    
 * Special Considerations:
 */
package cats.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Iterator;
import java.util.LinkedList;

/**
 *   This file contains an object that handles socket traffic.  It has two
 *   threads - one for listening to the socket and one for pulling
 *   messages from a message queue and sending them to the remote connection.
 *   <p>
 *   The JMRI loconetovertcp code provided a guide for the architecture
 *   of this class.
 *   
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2008, 2009</p>
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

public abstract class AbstractConnection extends Thread 
implements NetworkProtocol {
  /**
   * is the message in an IO Exception when the link resets (other end
   * disconnected)
   */
  private static final String CONNECTION_DROP = "Connection reset";
  
  /**
   * is the queue by which objects on the application queue up messages
   * for the remote end.
   */
  private LinkedList<String> MsgQueue;

  /**
   * is the list of objects listening to the connection
   */
  private LinkedList<ConnectionListener> Listeners;
  
  /**
   * is the asynchronous thread which empties the msgQueue and
   * send messages to the remote end.
   */
  private Thread TxThread;
  
  /**
   * is the socket to the remote end
   */
  private Socket Sock;
  
  /**
   * is the stream over which the application receives messages
   * from the remote end.
   */
  private BufferedReader InStream;
  
  /**
   * is the stream over which the application sends messages to
   * the remote end.
   */
  private PrintStream OutStream;
    
  /**
   * the ctor
   * @param s is the socket used by the connection
   */
  public AbstractConnection (Socket s) {
    Sock = s;
    Listeners = new LinkedList<ConnectionListener>();
    MsgQueue = new LinkedList<String>();
    setDaemon(true);
    start();
  }

  /**
   * adds a listener to the list of objects interested in the
   * status of the connection.
   * @param listener is the listener
   */
  public void addListener(ConnectionListener listener) {
      Listeners.add(listener);
  }

  /**
   * removes a ConnectionListener
   * @param listener is the object no longer interested
   * in the state of the connection.
   */
  public void removeListener(ConnectionListener listener) {
      Listeners.remove(listener);
  }

  /**
   * tailors the abstract class for a specific use.  It handles
   * a message received from the far end.
   * @param msg is the message received from the far end.
   */
  protected abstract void processMessage(String msg);
  
  /**
   * An asynchronous thread that waits for something from the remote end
   * and processes it.
   */
  public void run() {
    log.info("Connection established from " + Sock);
    try {
      InStream = new BufferedReader(new InputStreamReader(Sock.getInputStream()));
      OutStream = new PrintStream(Sock.getOutputStream(), true);
      TxThread = new Thread(new ClientTxHandler(this));
      TxThread.setDaemon(true);
      TxThread.setName(CLIENT_NAME + ":" +
              Sock.getRemoteSocketAddress().toString());
      TxThread.start();
      String line;
      while(!isInterrupted()) {
        line = InStream.readLine();
        if (line == null){
          log.debug(CLIENT_NAME + ": Remote Connection Closed");
          interrupt();
        }
        else
        {
          log.debug(CLIENT_NAME + ": Received: " + line);
          processMessage(line);
        }
      }
    }
    catch (IOException e) {
      if (!CONNECTION_DROP.equals(e.getMessage())) {
        log.warn("IO Error on socket " + e);
      }
    }
    TxThread.interrupt();
    TxThread = null;
    InStream = null;
    // Do not null out the output handle or the message queue pointer -
    // due to race conditions, the ClientTxHandler thread may be in
    // the middle of its while loop when the interrupt arrives. 
//    OutStream = null;
    MsgQueue.clear();
//    MsgQueue = null;
    try {
      Sock.close();
    }
    catch (IOException ex1) {}

    for (Iterator<ConnectionListener> iter = Listeners.iterator(); iter.hasNext();) {
        iter.next().connectionDropped(this);
    }
    log.info(CLIENT_NAME +  ": Exiting");
    log.info("Connection from " + Sock + " dropped");
  }
  
  /**
   * is an inner class for receiving messages from the application
   * and sending them to the remote end.
   */
  class ClientTxHandler implements Runnable {
    String  msg;
    StringBuffer outBuf = new StringBuffer();
    Thread parentThread;

    /**
     * is the ctor
     * @param creator
     */
    ClientTxHandler(Thread creator) {
      parentThread = creator;
    }

    /**
     * waits for a message to send, then sends it.
     */
    public void run() {

      try {
        while (!isInterrupted()) {
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
//            outBuf.append("RECEIVE ");
            outBuf.append(msg.toString());
            log.debug(CLIENT_NAME + ": Send: " + outBuf.toString());
            outBuf.append(EOL);
            OutStream.write(outBuf.toString().getBytes());
            OutStream.flush();
          }
        }
      }
      catch (IOException ex) {
        log.error(CLIENT_NAME + ": IO Exception" );
      }
      catch (InterruptedException ex) {
        log.debug(CLIENT_NAME + ": Interrupted Exception" );
      }
      
      // Interrupt the Parent to let it know we are exiting for some reason
      parentThread.interrupt();

      parentThread = null;
      msg = null;
      outBuf = null;
      log.info(CLIENT_NAME + ": Exiting" );
    }
  }

  /**
   * is the public interface for sending messages over the socket
   * @param msg is the message to send
   */
  public void sendMessage(String msg){
    synchronized(MsgQueue)
    {
      MsgQueue.add(msg);
      MsgQueue.notify();
    }
  }

static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(
    AbstractConnection.class.getName());
}
/* @(#)AbstractConnection.java */