/* Name: UpperBackFrill.java
 *
 * What:
 *   This file defines a class for drawing Diagonal tracks on the Screen.
 *   The tracks run from the Top edge to the Right edge.
 */
package cats.gui.frills.rails;

import cats.common.Sides;
import cats.gui.LineFactory;
import java.awt.Dimension;
import java.awt.Polygon;
import java.awt.Rectangle;

/**
 *   LowerBackFrill defines a class for drawing Diagonal tracks on a
 *   GridTile.  The tracks run fromthe Left edge to the Bottom edge.
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

public class UpperBackFrill
    extends RailFrill {

  /**
   * constructs the UpperBackfrill.
   */
  public UpperBackFrill() {
    super(Sides.RIGHT, Sides.TOP,
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
    if ( (Dot == Sides.RIGHT) || (Dot == Sides.TOP)) {
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
      MyClip.height = (bounds.height / 2) - PointGap - (clip.y - bounds.y);
    }
    if (Gaps[Sides.TOP]) {
      MyClip.x = xedge(Sides.TOP, bounds) + PointGap;
      MyClip.width = clip.width - (MyClip.x - clip.x);
    }
    if (Dot == Sides.RIGHT) {
      buildArrowHead(Dot);
    }
    else if (Dot == Sides.TOP) {
      buildArrowHead(Dot);
    }

//    xpoints[0] = bounds.x - (bounds.width / 2) - w;
//    xpoints[1] = bounds.x + (bounds.width / 2) + w;
//    xpoints[2] = xpoints[3] = bounds.x + bounds.width;
//    ypoints[0] = ypoints[1] = bounds.y;
//    ypoints[2] = bounds.y + (bounds.width / 2) - w;
//    ypoints[3] = bounds.y + (bounds.width / 2) + w;
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
      xPoints[0] = MyClip.x + MyClip.height;
      yPoints[0] = (int) Math.round(Pt[0][Y]);
      xPoints[1] = xPoints[0] - ArrowWidth;
      yPoints[1] = yPoints[0];
      xPoints[2] = xPoints[0];
      yPoints[2] = yPoints[0] - ArrowWidth;
    }
    else if (edge == Sides.TOP) {
      xPoints[0] = (int) Math.round(Pt[1][X]);
      yPoints[0] = MyClip.y;
      xPoints[1] = xPoints[0];
      yPoints[1] = yPoints[0] + ArrowWidth;
      xPoints[2] = xPoints[0] + ArrowWidth;
      yPoints[2] = yPoints[0];
    }
    ArrowHead = new Polygon(xPoints, yPoints, 3);
  }
}