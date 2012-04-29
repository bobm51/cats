/* Name: RailFrill.java
 *
 * What:
 *   This file defines a class for drawing tracks on a GridTile.  A track
 *   is a straight line connecting the mid-point of one edge of the GridTile
 *   to the mid-point of another edge.  However, the clipping Rectangle may
 *   be set to provide some blank space, representing a block boundary or
 *   a turnout thrown the other direction.
 */
package cats.gui.frills.rails;

import cats.common.Sides;
import cats.gui.frills.Frills;
import cats.gui.jCustom.ColorFinder;
import cats.gui.CtcLine;
import cats.gui.MouseUser;
import cats.layout.ColorList;
import cats.layout.items.Track;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.geom.Line2D;

/**
 *   RailFrill defines a class for drawing tracks on a GridTile.  A track
 *   is a straight line connecting the mid-point of one edge of the GridTile
 *   to the mid-point of another edge.  However, the clipping Rectangle may
 *   be set to provide some blank space, representing a block boundary or
 *   a turnout thrown the other direction.
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2004, 2009, 2010</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */

public class RailFrill
    extends Frills
    implements MouseUser {
  /**
   * is the number of pixels in the Gap due to points not being aligned.
   */
  protected static final int PointGap = 5;

  /**
   * is for identifying the X coordinate.
   */
  protected static final int X = 0;

  /**
   * is for identifying the Y coodinate.
   */
  protected static final int Y = 1;

  /**
   * is a constant for no direction of travel (that is no Arrowhead).
   */
  public static final int NO_DOT = -1;

  /**
   * are the two edges that the lines terminate on.
   */
  private int Ends[];

  /**
   * describes the two ends of the Track.  This could be a Point,
   * but because Graphics2D are being used, an array of double will
   * work.
   */
  protected double Pt[][];

  /**
   * is the clipping Rectangle.  It is a modification of the GridTile's
   * clipping Rectangle, by accounting for Tracks that are not aligned at
   * SwitchPoints.
   */
  protected Rectangle MyClip;

  /**
   * is the clipping Rectangle, prior to being adjusted for the ArrowHeads.
   */
  protected Rectangle OldClip;

  /**
   * the color of this rail.
   */
  private ColorFinder RailColor;

  /**
   * is the description of the Line style.
   */
  protected CtcLine MyLine;

  /**
   * flags which edges need additional gaps because the Track is not
   * aligned at a turnout.
   */
  protected boolean Gaps[] = { // space around the borders
      false,
      false,
      false,
      false
  };

  /**
   * is the number of pixels in one side of an arrowhead.
   */
  protected int ArrowWidth;

  /**
   * is the Edge which has an arrow head. NO_DOT means that neither is an
   * Arrowhead.  Obviously, since there is only one variable, only
   * one end can have an Arrowhead, which is fine, because a track
   * can have only one direction of travel.
   */
  protected int Dot;

  /**
   * is the ArrowHead.
   */
  protected Polygon ArrowHead;

  /**
   * constructs the RailFrill.  The numeric value of edge1 will always
   * be less than the numberic value of edge2.
   *
   * @param edge1 indicates one end
   * @param edge2 indicates the other
   * @param line is the description of the Line.
   *
   * @see cats.gui.CtcLine
   */
  public RailFrill(int edge1, int edge2, CtcLine line) {
    Ends = new int[2];
    Ends[0] = edge1;
    Ends[1] = edge2;
    Pt = new double[2][2];
    MyLine = line;
    Dot = NO_DOT;
    RailColor = new ColorFinder(ColorList.EMPTY);
  }

  /**
   * sets (or clears) flags on the edges that need gaps due to the Track
   * not being connected to Switch Points.  Block boundaries are fixed and
   * have been accounted for in the clipping rectangle.  Since Switch Points
   * cannot be on Block boundaries, the edges affected on the track must
   * be different from any that have gaps due to Block boundaries.
   *
   * @param edge is the edge being adjusted.
   * @param gap is true is the edge has a gap and false if it does not.
   */
  public void setGap(int edge, boolean gap) {
    Gaps[edge] = gap;
  }

  /**
   * sets the Track being represented.
   *
   * @param track is the Track.
   *
   * @see cats.layout.items.Track
   */
  public void setTrack(Track track) {
  }

  /*
   * is the method called by a GridTile, describing itself - the area on
   * the screen to be written to.
   *
   * @param g is the Graphics context on which to draw
   */
  public void decorate(Graphics g) {
    Graphics2D g2 = (Graphics2D) g;
    Color oldInk = g2.getColor();
    BasicStroke lineWidth;
    Stroke oldWidth = g2.getStroke();
    Rectangle oldClip = g.getClipBounds();
    g.setClip(MyClip);

    lineWidth = new BasicStroke(MyLine.grabLine(), BasicStroke.CAP_BUTT,
                                BasicStroke.JOIN_BEVEL);
    g2.setPaint(RailColor.getColor());
    g2.setStroke(lineWidth);
    g2.draw(new Line2D.Double(Pt[0][X], Pt[0][Y], Pt[1][X], Pt[1][Y]));
    if (Dot != NO_DOT) {
      drawArrowHead(g);
    }
    g2.setStroke(oldWidth);
    g2.setColor(oldInk);
    g.setClip(oldClip);
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
    MyClip = new Rectangle(clip);
    OldClip = new Rectangle(clip);
    Pt[0][X] = xEdge(Ends[0], bounds);
    Pt[0][Y] = yEdge(Ends[0], bounds);
    Pt[1][X] = xEdge(Ends[1], bounds);
    Pt[1][Y] = yEdge(Ends[1], bounds);
    ArrowWidth = Math.round(MyLine.grabLine() * 2);
  }

  /**
   * calculates the X offset of an edge as a double, for use in
   * Line2D.Double.
   *
   * @param edge denotes which edge of the rectangle the line terminates on
   * @param bounds describes the rectangle
   *
   * @return the X coordinate of the edge.
   */
  double xEdge(int edge, Rectangle bounds) {
    double result;
    switch (edge) {
      case Sides.RIGHT:
        result = (bounds.x + bounds.width);
        break;

      case Sides.BOTTOM:
      case Sides.TOP:
        result = (bounds.x + (bounds.width / 2.0));
        break;

      case Sides.LEFT:
        result = bounds.x;
        break;

      default:
        System.out.println("Illegal edge passed to xEdge");
        result = 0.0;
    }
    return result;
  }

  /**
   * calculates the Y offset of an edge as a double, for use in
   * Line2D.Double.
   *
   * @param edge denotes which edge of the rectangle the line terminates on
   * @param bounds describes the rectangle
   *
   * @return the Y coordinate of the edge.
   */
  double yEdge(int edge, Rectangle bounds) {
    double result;
    switch (edge) {
      case Sides.RIGHT:
      case Sides.LEFT:
        result = bounds.y + (bounds.height / 2.0);
        break;

      case Sides.BOTTOM:
        result = bounds.y + bounds.height;
        break;

      case Sides.TOP:
        result = bounds.y;
        break;

      default:
        System.out.println("Illegal edge passed to yEdge");
        result = 0.0;
    }
    return result;
  }

  /**
   * calculates the X offset of an edge as an int, for use in
   * drawLine.
   *
   * @param edge denotes which edge of the rectangle the line terminates on
   * @param bounds describes the rectangle
   *
   * @return the X coordinate of the edge.
   */
  int xedge(int edge, Rectangle bounds) {
    int result;
    switch (edge) {
      case Sides.RIGHT:
        result = (bounds.x + bounds.width);
        break;

      case Sides.BOTTOM:
      case Sides.TOP:
        result = (bounds.x + (bounds.width / 2));
        break;

      case Sides.LEFT:
        result = bounds.x;
        break;

      default:
        System.out.println("Illegal edge passed to xEdge");
        result = 0;
    }
    return result;
  }

  /**
   * calculates the Y offset of an edge as an int, for use in
   * drawLine.
   *
   * @param edge denotes which edge of the rectangle the line terminates on
   * @param bounds describes the rectangle
   *
   * @return the Y coordinate of the edge.
   */
  int yedge(int edge, Rectangle bounds) {
    int result;
    switch (edge) {
      case Sides.RIGHT:
      case Sides.LEFT:
        result = (bounds.y + (bounds.height / 2));
        break;

      case Sides.BOTTOM:
        result = (bounds.y + bounds.height);
        break;

      case Sides.TOP:
        result = (bounds.y);
        break;

      default:
        System.out.println("Illegal edge passed to yEdge");
        result = 0;
    }
    return result;
  }

  /**
   * sets the Color tag for the Rail's color.
   *
   * @param color is the Color tag
   */
  public void setColor(String color) {
    RailColor.setColor(color);
  }

  /**
   * Draws the ArrowHead, indicating direction of travel.  This method in
   * the base class performs the common things.  The overriding methods
   * perform the actual drawing.
   *
   * @param g is the Graphics context where drawing occurs.
   */
  public void drawArrowHead(Graphics g) {
    g.setClip(OldClip);
    g.fillPolygon(ArrowHead);
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
    Dot = dot;
  }

  /**
   * is a method for determining if the Mouse is positioned over it
   * when a button is clicked.  If so, and if it accepts the button
   * push, it returns a MouseUser object to handle the button push.
   *
   * @param event is the MouseEvent.
   *
   * @return null - it is too hard to accurately and quickly position the
   * Mouse on a line.
   */
  public MouseUser mouseDown(MouseEvent event) {
    return null;
  }

    /**
     *   This method tells the Object that accepted the Mouse Press
     *   that the user released the mouse button.  In this case,
     *   an event is sent to the associated Track (assuming the
     *   Mouse did not move off the Track).
     *
     * @param event provides information on the Mouse button release
     *
     */
    public void finishMouse(MouseEvent event) {
    }
}
/* @(#)RaillFrill.java */