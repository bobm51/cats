/* Name: LightFrill.java
 *
 * What:
 *  A concrete class for representing signals based on lights as Iocns
 *  on the dispatcher's panel.
 */
package cats.gui.frills;

import cats.common.Sides;
import cats.gui.LightMast;

import java.awt.Graphics;
import java.awt.Polygon;

/**
 * A concrete class for representing signals based on lights, as Icons
 * on the dispatcher's panel.
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2004, 2009</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */

public class LightFrill
    extends SignalFrill {
  /**
   * constructs the LightFrill.
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
   * 03/27/12 - always draw a single signal head to be prototypical
   * @
   * 
   * @see FrillLoc
   */
  public LightFrill(FrillLoc position, int orientation, int heads) {
    super(position, orientation, 1);
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
    g.fillOval(x, y, Radius, Radius);
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
    if (LightMast.TheMastType.getFlagValue()) {
      int delta = Radius;

      // If oriented for left moving or right moving traffic, then the mast
      // is a horizontal line; otherwise, it is a vertical line.
      if ( (Orient == Sides.LEFT) || (Orient == Sides.RIGHT)) {
        if (Orient == Sides.LEFT) {
          delta = -delta;
        }
        g.drawLine(x, y + (Radius / 2), x + delta, y + (Radius / 2));
        g.drawLine(x, y + (Radius / 4), x, y + Radius - (Radius / 4));
      }
      else {
        if (Orient == Sides.TOP) {
          delta = -delta;
        }
        g.drawLine(x + (Radius / 2), y, x + (Radius / 2), y + delta);
        g.drawLine(x + (Radius / 4), y, x + Radius - (Radius /4), y);
      }      
   }
    else {
      Polygon points = new Polygon();
      // Plant one corner of the side on the ground.
      points.addPoint(x, y);

      // Plant the other corner of the side on the ground.
      if ( (Orient == Sides.LEFT) || (Orient == Sides.RIGHT)) {
        y += Base;
      }
      else {
        x += Base;
      }
      points.addPoint(x, y);

//   Determine the apex, the point on the head.
      switch (Orient) {
        case Sides.RIGHT:
          x += Base;
          y -= Base / 2;
          break;

        case Sides.BOTTOM:
          x -= Base / 2;
          y += Base;
          break;

        case Sides.LEFT:
          x -= Base;
          y -= Base / 2;
          break;

        default:
          x -= Base / 2;
          y -= Base;
      }
      points.addPoint(x, y);
      g.fillPolygon(points);
    }
  }
}
/* @(#)LightFrill.java */