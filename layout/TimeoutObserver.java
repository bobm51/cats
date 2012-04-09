/*
 * Name: TimeoutObserver.java
 *
 * What:
 *   This file contains an interface required by all Objects which register
 *   with the MsgFilter to receive TimeoutEvents.
 */
package cats.layout;

/**
 * defines the interface for Objects which register with the MsgFilter to
 * receive the TimeoutEvents from the layout.
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */
 public interface TimeoutObserver {

   /**
    * is the interface through which the MsgFilter delivers the TimeoutEvent.
    */
   public void acceptTimeout();
 }
