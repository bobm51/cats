/* Name: LowerSlashFrill.java
 *
 * What:
 *   This file defines a class for drawing Diagonal tracks on the Screen.
 *   The tracks run from the Right edge to the Bottom edge.
 */
package cats.gui.frills.rails;

import cats.common.Sides;
import cats.gui.LineFactory;
import java.awt.Dimension;
import java.awt.Polygon;
import java.awt.Rectangle;

/**
 *   LowerBackFrill defines a class for drawing Diagonal tracks on a
 *   GridTile.  The tracks run fromthe Right edge to the Bottom edge.
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

public class LowerSlashFrill
    extends RailFrill {

  /**
   * constructs the LowerSlashfrill.
   */
  public LowerSlashFrill() {
    super(Sides.RIGHT, Sides.BOTTOM,
          LineFactory.Lines.findLine(LineFactory.DIAGONAL));
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
    return new Dimension(square);
  }

  /*
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
    if ( (Dot == Sides.RIGHT) || (Dot == Sides.BOTTOM)) {
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
    if (Gaps[Sides.RIGHT]) {
      MyClip.y = yedge(Sides.RIGHT, bounds) + PointGap;
      MyClip.height = clip.height - (MyClip.y - clip.y);
    }
    if (Gaps[Sides.BOTTOM]) {
      MyClip.x = xedge(Sides.BOTTOM, bounds) + PointGap;
      MyClip.width = clip.width - (MyClip.x - clip.x);
    }
    if (Dot == Sides.RIGHT) {
      buildArrowHead(Dot);
    }
    else if (Dot == Sides.BOTTOM) {
      buildArrowHead(Dot);
    }
//    xpoints[0] = xpoints[1] = bounds.x + bounds.width;
//    xpoints[2] = bounds.x - (bounds.width / 2) - w;
//    xpoints[3] = bounds.x + (bounds.width / 2) + w;
//    ypoints[0] = bounds.y + (bounds.height / 2) - w;
//    ypoints[1] = bounds.y + (bounds.height / 2) + w;
//    ypoints[2] = ypoints[3] = bounds.y + bounds.height;
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
      xPoints[0] = MyClip.x + MyClip.width;
      yPoints[0] = (int) Math.round(Pt[0][Y]);
      xPoints[1] = xPoints[0] - ArrowWidth;
      yPoints[1] = yPoints[0];
      xPoints[2] = xPoints[0];
      yPoints[2] = yPoints[0] + ArrowWidth;
    }
    else if (edge == Sides.BOTTOM) {
      xPoints[0] = (int) Math.round(Pt[1][X]);
      yPoints[0] = MyClip.y + MyClip.height;
      xPoints[1] = xPoints[0];
      yPoints[1] = yPoints[0] - ArrowWidth;
      xPoints[2] = xPoints[0] + ArrowWidth;
      yPoints[2] = yPoints[0];
    }
    ArrowHead = new Polygon(xPoints, yPoints, 3);
  }
}