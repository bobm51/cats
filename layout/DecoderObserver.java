/*
 * Name: DecoderObserver.java
 *
 * What:
 *   This file contains an interface required by all Objects which register
 *   with an IOSpec to be notified when the decoder fires.
 */
package cats.layout;

/**
 * defines the interface for Objects which register with an IOSPec to
 * receive notification that the message was received from the layout.
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */
 public interface DecoderObserver {

   /**
    * is the interface through which the IOSpec delivers the notification.
    */
   public void acceptIOEvent();
 }
