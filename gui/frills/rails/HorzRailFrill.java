/* Name: HorzRailFrill.java
 *
 * What:
 *   This file defines a class for drawing Horizontal tracks on the Screen.
 */
package cats.gui.frills.rails;

import cats.common.Sides;
import cats.gui.LineFactory;
import java.awt.Dimension;
import java.awt.Polygon;
import java.awt.Rectangle;

/**
 *   HorzRailFrill defines a class for drawing Horizontal tracks on a
 *   GridTile.
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 *
 * @see cats.gui.frills.rails.RailFrill
 */

public class HorzRailFrill
    extends RailFrill {

  /**
   * constructs the HorzRailfrill.
   */
  public HorzRailFrill() {
    super(Sides.RIGHT, Sides.LEFT, LineFactory.Lines.findLine(LineFactory.LEVEL));
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

  /**
   * sets (or clears) the direction of travel (Arrowhead).
   *
   * @param dot is 0 - 3 or NO_DOT.  The numbers correspond to the Edge
   * the ArrowHead points to and NO_DOT means there is no ArrowHead.
   * Because ArrowHeads appear only on Block boundaries, the ArrowHead
   * will point to a Block gap, so line drawing must be adjusted
   * accordingly.
   */
  public void setDOT(int dot) {
    super.setDOT(dot);
    if ((Dot == Sides.LEFT) || (Dot == Sides.RIGHT)) {
      buildArrowHead(Dot);
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
//    int xpoints[] = new int[4];
//    int ypoints[] = new int[4];
//    int w = HIT_WIDTH;
    super.setDrawing(bounds, clip);
    if (Gaps[Sides.LEFT]) {
      MyClip.x += PointGap;
      MyClip.width -= PointGap;
    }
    if (Gaps[Sides.RIGHT]) {
      MyClip.width -= PointGap;
    }
    if (Dot == Sides.RIGHT) {
      MyClip.width -= ArrowWidth;
      buildArrowHead(Dot);
    }
    else if (Dot == Sides.LEFT) {
      MyClip.x += ArrowWidth;
      MyClip.width -= ArrowWidth;
      buildArrowHead(Dot);
    }

//    xpoints[0] = xpoints[1] = bounds.x;
//    xpoints[2] = xpoints[3] = bounds.x + bounds.width;
//    ypoints[0] = ypoints[2] = bounds.y + (bounds.height / 2) - w;
//    ypoints[1] = ypoints[3] = bounds.y + (bounds.width / 2) + w;
//    HitZone = new Polygon(xpoints, ypoints, 4);
  }

  /*
   * constructs an ArrowHead pointing to an Edge.
   *
   * @param edge is the Edge (RIGHT, BOTTOM, etc.)
   */
  protected void buildArrowHead(int edge) {
    int xPoints[] = new int[3];
    int yPoints[] = new int[3];
    if (edge == Sides.RIGHT) {
      xPoints[0] = OldClip.x + OldClip.width;
      yPoints[0] = (int) Math.round(Pt[0][Y]);
      xPoints[1] = xPoints[0] - ArrowWidth;
    }
    else if (edge == Sides.LEFT) {
      xPoints[0] = OldClip.x;
      yPoints[0] = (int) Math.round(Pt[1][Y]);
      xPoints[1] = xPoints[0] + ArrowWidth;
    }
    yPoints[1] = yPoints[0] - ArrowWidth;
    xPoints[2] = xPoints[1];
    yPoints[2] = yPoints[0] + ArrowWidth;
    ArrowHead = new Polygon(xPoints, yPoints, 3);
  }
}