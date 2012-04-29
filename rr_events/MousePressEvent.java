/* Name: MousePressEvent.java
 *
 * What:
 *   This class queues a mousePressed event.
 */
package cats.rr_events;

import cats.gui.Screen;
import java.awt.event.MouseEvent;

/**
 *   This class queues a mousePressed event.
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */
public class MousePressEvent
    extends RREvent {

  /**
   * is the MouseEvent being queued.
   */
  private MouseEvent Mevent;

  /**
   * is the constructor.
   * @param me is the MouseEvent.
   */
  public MousePressEvent(MouseEvent me) {
    Mevent = me;
  }

  /*
   * Performs the command encapsulated by this object.
   */
  public void doIt() {
    Screen.DispatcherPanel.mousePressedAction(Mevent);
  }
}
/* @(#)MousePressEvent.java */