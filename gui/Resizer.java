/* Name: Resizer.java
 *
 * What:
 *   This class handles resizing a component through the native interface.
 *
 * Special Considerations:
 *   This class is application independent and can be reused.
 */
package cats.gui;

import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.JPanel;

/**
 * handles resizing a component through the native interface.
 * In this case, the component is the Dispatcher Window.
 *
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */
public class Resizer
    extends ComponentAdapter {
  private Rectangle Boundary;

  /**
   * is the method called when the component's size is changed.
   *
   * @param e is the AWT event describing the change.
   *
   * @see java.awt.event.ComponentAdapter
   * @see java.awt.event.ComponentEvent
   */
  public void componentResized(ComponentEvent e) {
    adjustSize( (JPanel) (e.getComponent()));
  }

  /**
   * is the routine which determines the new size and forces the GridTiles
   * to be reformatted.
   *
   * @param frame is the drawing area being resized.
   */
  public void adjustSize(JPanel frame) {
    Insets i = frame.getInsets();
    Rectangle rec = new Rectangle(frame.getBounds());
    rec.x = i.left;
    rec.y = i.top;
    rec.width -= (i.left + i.right);
    rec.height -= (i.top + i.bottom);
    System.out.println("size=" + frame.getTopLevelAncestor().getBounds());
    CTCcanvas.Surface.setSize(rec.width, rec.height);
    if (Screen.DispatcherPanel != null) {
      Screen.DispatcherPanel.distribute(rec);
    }
    Boundary = rec;
    CTCcanvas.Surface.repaint(Boundary.x, Boundary.y, Boundary.width,
                                  Boundary.height);
  }
}