/*
 * Name: RREventObserver.java
 *
 * What:
 *   This file contains an interface required by all Objects which register
 *   with the MsgFilter to receive RREvents.
 */
package cats.layout;

import jmri.jmrix.loconet.LocoNetMessage;

/**
 * defines the interface for Objects which register with the MsgFilter to
 * receive the RREvents from the layout.
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */
 public interface RREventObserver {

   /**
    * is the interface through which the MsgFilter delivers the RREvent.
    *
    * @param msg is the LocoNetMessage received.
    */
   public void acceptMessage(LocoNetMessage msg);
 }
