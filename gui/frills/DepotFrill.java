/* Name: DepotFrill.java
 *
 * What:
 *   This file defines a class for placing a Depot Icon on the GridTile.
 */
package cats.gui.frills;

import cats.gui.jCustom.ColorFinder;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;

/**
 *   DepotFrill defines a class for placing a Depot Icon on the GridTile.
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2004, 2009</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */

public class DepotFrill
    extends Frills {

  static final int Side = 9; // pixels - this should be changeable.

  /**
   * is where to place the Depot Icon (see FrillLoc).
   */
  private FrillLoc Where;

  /**
   * is the coordinates of the upper left corner.
   */
  private Point Anchor;

  /**
   * is the color of the Depot.  It may be null.
   */
  private ColorFinder DepotColor;
  
  /**
   * constructs the DepotFrill.
   *
   * @param position is where to draw the depot.
   * @param color is the color of the icon.  It may be null.
   */
  public DepotFrill(FrillLoc position, ColorFinder color) {
    Where = position;
    DepotColor = color;
  }

  /*
   * is the method called by a GridTile, describing itself - the area on
   * the screen to be written to.
   *
   * @param g is the Graphics context on which to draw
   */
  public void decorate(Graphics g) {
    if (Where != null) {
      Color oldColor = g.getColor();
      g.setColor(DepotColor.getColor());
      g.fillRect(Anchor.x, Anchor.y, Side, Side);
      g.setColor(oldColor);
    }
  }

  /*
   * is the method called by a GridTile whenever it is moved or its size
   * changes.  The intent is to tell the Frill that it needs to recompute
   * the parameters on how it draws itself.
   *
   * @param bounds describes the boundary of the GridTile.
   * @clip describes the clipping area, the drawing area after considering
   *       Block gaps.
   */
  public void setDrawing(Rectangle bounds, Rectangle clip) {
    Anchor = Where.locateCorner(bounds, new Dimension(Side, Side));
  }

  /**
   * retrieves the FrillLoc describing where the Depot is placed.
   *
   * @return the description.
   *
   * @see FrillLoc
   */
//  public FrillLoc getLocation() {
//    return Where;
//  }

  /**
   * changes the Location on the GridTile of where the Depot Icon is placed.
   *
   * @param where is the FrillLoc describing where to place the Depot Icon.
   * It may be null, in which case the Icon will not be shown.
   *
   * @see FrillLoc
   */
  public void setLocation(FrillLoc where) {
    Where = where;
  }

  /*
   * is the method called by a GridTile, to determine the Dimensions of
   * the drawing area needed by the Frill.
   *
   * @param square is a Dimension describing the maximum space allocated
   * to the Frill.
   *
   * @return how much of the square is used.
   */
  public Dimension getDefSize(Dimension square) {
    return new Dimension(square.width / 2, square.height / 2);
  }
}
/* @(#)DepotFrill.java */