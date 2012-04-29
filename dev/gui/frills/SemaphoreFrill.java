/* Name: SemaphoreFrill.java
 *
 * What:
 *  A concrete class for representing signals based on Semaphores as Iocns
 *  on the dispatcher's panel.
 */
package cats.gui.frills;

import cats.common.Sides;
import java.awt.Graphics;

/**
 * A concrete class for representing signals based on Semaphores, as Icons
 * on the dispatcher's panel.
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2004, 2009</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */

public class SemaphoreFrill
    extends SignalFrill {
  /**
   * constructs the SemaphoreFrill.
   *
   * @param position is where to write the String.
   * @param orientation is the relationship of the head to the mast.
   * <ul>
   * <li>
   *     RIGHT means the head is on the right (protects right hand movement)
   * <li>
   *     BOTTOM means the head is on the bottom
   * <li>
   *     LEFT means the head is on the left
   * <li>
   *     TOP means the head is on the top
   * </ul>
   * @param heads is the number of heads to be shown (1-3 heads are allowed)
   *
   * @see FrillLoc
   */
  public SemaphoreFrill(FrillLoc position, int orientation, int heads) {
    super(position, orientation, heads);
  }

  /*
   * paint a single head.
   * <p>
   * @param x is the x coordinate of the upper left corner of the head.
   *
   * @param y is the y coordinate of the upper left corner of the head.
   *
   * @param g is the Graphics to draw the head on.
   */
  public void drawHead(int x, int y, Graphics g) {
    if ( (Orient == Sides.LEFT) || (Orient == Sides.RIGHT)) {
      g.drawLine(x, y + (Radius / 2), x + Radius, y + (Radius / 2));
      if (Orient == Sides.LEFT) {
        g.drawLine(x + (Radius / 2), y + (Radius / 2), x, y);
      }
      else {
        g.drawLine(x + (Radius / 2), y + (Radius / 2), x + Radius, y);
      }
    }
    else {
      g.drawLine(x + (Radius / 2), y, x + (Radius / 2), y + Radius);
      if (Orient == Sides.BOTTOM) {
        g.drawLine(x + (Radius / 2), y + (Radius / 2), x, y + Radius);
      }
      else {
        g.drawLine(x + (Radius / 2), y + (Radius / 2), x + Radius, y);
      }
    }
  }

  /*
   * paint the signal mast.
   *<p>
   * @param x is the x coordinate of the upper left corner of the mast.
   *
   * @param y is the y coordinate of the upper left corner of the mast.
   *
   * @param g is the Graphics to draw the head on.
   */
  public void drawMast(int x, int y, Graphics g) {
    int delta = Radius;

    // If oriented for left moving or right moving traffic, then the mast
    // is a horizontal line; otherwise, it is a vertical line.
    if ( (Orient == Sides.LEFT) || (Orient == Sides.RIGHT)) {
      if (Orient == Sides.LEFT) {
        delta = -delta;
      }
      g.drawLine(x, y + (Radius / 2), x + delta, y + (Radius / 2));
    }
    else {
      if (Orient == Sides.TOP) {
        delta = -delta;
      }
      g.drawLine(x + (Radius / 2), y, x + (Radius / 2), y + delta);
    }
  }
}
/* @(#)SemaphoreFrill.java */