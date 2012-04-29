/* Name: MsgFilter.java
 *
 * What:
 *   This class defines a Singleton object for receiving Loconet messages
 *   and processing them.  Processing involves looking at the message
 *   address and checking to
 *   see if an object with the Loconet address has registered to receive
 *   it.  If so, then a RREvent is queued for the Object.
 *
 * Special Considerations:
 *   This class is specific to Loconet.
 */
package cats.layout;

import cats.rr_events.LoconetEvent;
import cats.rr_events.RREventManager;
import java.util.Enumeration;
import java.util.Vector;
import jmri.jmrix.loconet.LnConstants;
import jmri.jmrix.loconet.LnTrafficController;
import jmri.jmrix.loconet.LocoNetListener;
import jmri.jmrix.loconet.LocoNetMessage;

/**
 * This class defines a Singleton object for receiving Loconet messages
 * and processing them.  Processing involves looking at the message
 *  address and checking to
 * see if an object with the Loconet address has registered to receive
 * it.  If so, then a RREvent is queued for the Object.
 * <p>
 * Here are the salient points to this class.
 * <ul>
 * <li>
 *     It follows the Singleton design pattern - there is only one of these.
 * <li>
 *     It uses the Observer design pattern.  The thing being observed is
 *     the Loconet message stream.  When a packet with the requested
 *     address is seen, then the observer is notified via
 *     an RREvent.
 * <li>
 *     There are potentially a significant number of objects looking
 *     for particular events, so rather than forward all Loconet messages
 *     to all objects, this function filters the messages and directs
 *     them to only interested objects.  This filtering is expected to
 *     keep the event processing time down.
 * <li>
 *     RREvents are not posted to the object directly, but through the
 *     queue mechanism.  Thus, the detection process runs in a separate
 *     thread from the processing.  Since the delivery of queue elements
 *     is synchronized, this should prevent contention problems with
 *     mouse events and Swing updates.  It also keeps the Swing action
 *     processing small, which should provide better user response.
 *     The RREvents are based on the Command pattern and specific RREvents
 *     are generated using a Factory pattern.
 * </ul>
 *
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2004, 2009, 2010</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */
public class MsgFilter
implements LocoNetListener {
  
  /**
   * is the Singleton.
   */
  private static MsgFilter LocoNetFilter;
  
  /**
   * the lists of Observers of each kind of message.  Because thses lists
   * are assumed not to change over time, no provisions have been made
   * for removing an entry.
   */
  private static Vector<Observer> OPC_INPUT_REP_Observers;
  private static Vector<Observer> OPC_SW_REP_Observers;
  private static Vector<Observer> OPC_SW_REQ_Observers;
  
  /**
   * is the class constructor
   */
  private MsgFilter() {
    @SuppressWarnings("deprecation")
	LnTrafficController ltc = LnTrafficController.instance();
    if (ltc == null) {
      log.error("No Loconet interface has been defined");
    }
    else {
      ltc.addLocoNetListener(~0, this);
      OPC_INPUT_REP_Observers = new Vector<Observer>();
      OPC_SW_REP_Observers = new Vector<Observer>();
      OPC_SW_REQ_Observers = new Vector<Observer>();
    }
  }
  
  /**
   * is called when a Loconet message is received.  This is the heart of
   * what the MsgFilter does.  It takes apart the Loconet message, looks
   * for the message address, then routes the message to possibly
   * multiple observers.
   *
   * @param msg is a Loconet message.
   */
  public void message(LocoNetMessage msg) {
    Observer obs;
    
    int opCode = msg.getOpCode();
    int addr;
    
    if (opCode == LnConstants.OPC_INPUT_REP) {
      addr = msg.sensorAddr();
      for (Enumeration<Observer> e = OPC_INPUT_REP_Observers.elements(); e.hasMoreElements();) {
        obs = (e.nextElement());
        if (obs.isAddressMatch(addr)) {
          RREventManager.EventQue.append(new LoconetEvent(obs.Client, msg));
        }
      }
    }
    else if (opCode == LnConstants.OPC_SW_REP) {
      addr = swAddr(msg);
      for (Enumeration<Observer> e = OPC_SW_REP_Observers.elements(); e.hasMoreElements();) {
        obs = (e.nextElement());
        if (obs.isAddressMatch(addr)) {
          RREventManager.EventQue.append(new LoconetEvent(obs.Client, msg));
        }
      }
    }
    else if (opCode == LnConstants.OPC_SW_REQ) {
      addr = swAddr(msg);
      for (Enumeration<Observer> e = OPC_SW_REQ_Observers.elements(); e.hasMoreElements();) {
        obs = (e.nextElement());
        if (obs.isAddressMatch(addr)) {
          RREventManager.EventQue.append(new LoconetEvent(obs.Client, msg));
        }
      }
    }
  }
  
  /**
   * is the routine all observers call to register who they are and what to
   * send them.
   * 
   * @param address
   *          is the decoder address
   * @param receiver
   *          is the Object that wants to process Loconet messages
   */
  public static void registerObserver(int address, RREventObserver receiver) {
    Observer observer = new Observer(address, receiver);
    createFilter();
    OPC_INPUT_REP_Observers.add(observer);
  }

  /**
   * is the routine that OPC_SW_REP message observers call to register
   * themselves.
   * @param address is the Loconet address being observed
   * @param receiver is the Object that wants to process the message
   */
  public static void registerSWREPObserver(int address, RREventObserver receiver) {
    Observer observer = new Observer(address, receiver);
    createFilter();
    OPC_SW_REP_Observers.add(observer);
  }

  /**
   * is the routine that OPC_SW_REQ message observers call to register
   * themselves.
   * @param address is the Loconet address being observed
   * @param receiver is the Object that wants to process the message
   */
  public static void registerSWREQObserver(int address, RREventObserver receiver) {
    Observer observer = new Observer(address, receiver);
    createFilter();
    OPC_SW_REQ_Observers.add(observer);
  }

  /**
   * is the routine called during startup to establish the Singleton.
   */
  public static void createFilter() {
    if (LocoNetFilter == null) {
      LocoNetFilter = new MsgFilter();
    }
  }

  /**
   * extracts the Loconet address field from an OPC_SW_REQ or
   * OPC_SW_REP message and returns it as an integer.
   * 
   * @param msg is a Loconet OPC_SW_REQ or OPC_SW_REP message
   * @return the Loconet address
   */
  public int swAddr(LocoNetMessage msg) {
    int sw1 = msg.getElement(1);
    int sw2 = msg.getElement(2);
    return ((sw2 & 0x0F) * 128) + (sw1 & 0x7F);
  }
  
  /**
   * an internal class to associate a Loconet decoder address with the
   * RREventObserver that wants to receive messages to that address.
   *
   * This scheme works with only OPC_SW_REQ and OPC_SW_REP messages
   * because there is an extension bit in OPC_INPUT_REP messages.
   */
  private static class Observer {
    
    /**
     * is the Loconet address of the sensor.
     */
    public int ObsAddress;
    
    /**
     * is the sensor object.
     */
    public RREventObserver Client;
    
    /**
     * the constructor
     * @param address is the Loconet address
     * @param client is the object that wants to process
     * the message.
     */
    public Observer(int address, RREventObserver client) {
      ObsAddress = address;
      Client = client;
    }
    
    /**
     * is a predicate for testing that the address in a message
     * matches the one in the observer.
     * @param address is the address being compared.
     * @return true if this Observer is interested in the
     * Loconet message.
     */
    public boolean isAddressMatch(int address) {
      return address == ObsAddress;
    }
  }
  
  static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(
      MsgFilter.class.getName());
}
/* @(#)MsgFilter.java */