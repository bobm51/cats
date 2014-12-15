/* Name: SignalFrill.java
 *
 * What:
 *   This file defines a class for placing a Signal Icon on the GridTile.  test commit
 */
package cats.gui.frills;

import cats.gui.GridTile;
import cats.gui.MouseUser;
import cats.gui.jCustom.ColorFinder;
import cats.common.*;
import cats.layout.ColorList;
import cats.layout.items.PanelSignal;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;

/**
 *   defines a class for placing a Signal Icon on the GridTile.
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2004, 2009</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */

public abstract class SignalFrill
    extends Frills
    implements MouseUser {

  /**
   * defines the X offset of the upperleft corner of the signal head
   * for each orientation.  true means there is an offset of Base pixels.
   */
  protected static final boolean HEADXOFFSET[] = {
      true, // RIGHT - base is to the left
      false, // BOTTOM - base is above
      true, // LEFT - base is to the right
      false // TOP - base is below
  };

  /** defines the Y offset of the upperleft corner of the signal head
   * for each orientation.  true means there is an offset of Base pixels.
   */
  protected static final boolean HEADYOFFSET[] = {
      false, // RIGHT - base is to the left
      true, // BOTTOM - base is above
      false, // LEFT - base is to the right
      true // TOP - base is below
  };

  /**
   * defines the X offset of one corner of the base of the "mast".  true
   * means it is Radius + Base pixels from the anchor.
   */
  protected static final boolean BASEXOFFSET[] = {
      false, // RIGHT - head is to the right
      false, // BOTTOM - head is below
      true, // LEFT - head is to the left
      false // TOP - head is above
  };

  /** defines the Y offset of one corner of the base of the "mast".  true
   * means it is Radius + Base fromthe anchor.
   */
  protected static final boolean BASEYOFFSET[] = {
      false, // RIGHT - head is to the right
      false, // BOTTOM - head is below
      false, // LEFT - head is to the left
      true // TOP - head is above
  };

  /**
   * defines the number of pixels in the radius of the signal head.
   * This should be changeable.
   */
  static final int Radius = 8;

  /**
   * defines the height of the base of the signal mast in pixels.
   */
  static final int Base = Radius;

  /**
   * defines the orientation of the head, relative to the base:
   * <ul>
   * <li>
   *     RIGHT means the head is to the right of the base
   * <li>
   *     BOTTOM means the head is below the base
   * <li>
   *     LEFT means the head is to the left os the base
   * <li>
   *     TOP means the head is above the base
   * </ul>
   */
  protected int Orient;

  /**
   * defines the dimensions of the Bounding Box containing the signal Icon.
   */
  protected Dimension BBox;

  /**
   * is where to place the Signal Icon (see FrillLoc).
   */
  protected FrillLoc Where;

  /**
   * is the number of heads to draw.
   */
  protected int Heads;

  /**
   * is the upper left corner of the Signal Icon in the GridTile.
   */
  private Point Anchor;

  /**
   * is the actual Color.
   */
  private ColorFinder SigColor;

  /**
   * is the logic controlling the Icon's color.
   */
  private PanelSignal Owner;
  
  private boolean useStackColor = false;

  /**
   * constructs the SignalFrill.
   *
   * @param position is where to place the upper left corner of the Icon.
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
  public SignalFrill(FrillLoc position, int orientation, int heads) {
    Where = position;
    if ( (orientation == Sides.LEFT) || (orientation == Sides.RIGHT)) {
      BBox = new Dimension( (heads * Radius) + Base, Radius);
    }
    else {
      BBox = new Dimension(Radius, (heads * Radius) + Base);
    }
    Orient = orientation;
    Heads = heads;
    SigColor = new ColorFinder(ColorList.NO_ROUTE);
  }

  /**
   * tells the SignalFrill where to route Mouse events.
   *
   * @param owner is the PanelSignal that owns the Signal being represented.
   */
  public void setOwner(PanelSignal owner) {
    Owner = owner;
  }
  public void setStackColor(boolean stackInProgress) {
      useStackColor = false;
      if(stackInProgress) {
          useStackColor = true;
      }
  }
  /*
   * is the method called by a GridTile, describing itself - the area on
   * the screen to be written to.
   *
   * @param g is the Graphics context on which to draw
   */
  public void decorate(Graphics g) {
    int x;
    int y;
    if (Where != null) {
      Color oldColor = g.getColor();
      g.setColor(SigColor.getColor());
      x = Anchor.x;
      y = Anchor.y;
      if (Orient == Sides.RIGHT) {
        x += Radius;
      }
      else if (Orient == Sides.BOTTOM) {
        y += Radius;
      }
      for (int head = 0; head < Heads; ++head) {
        drawHead(x, y, g);
        if (HEADXOFFSET[Orient]) {
          x += Radius;
        }
        if (HEADYOFFSET[Orient]) {
          y += Radius;
        }
      }

      if (BASEXOFFSET[Orient]) {
        x += Base;
      }
      else {
        x = Anchor.x;
      }
      if (BASEYOFFSET[Orient]) {
        y += Base;
      }
      else {
        y = Anchor.y;
      }
      
      String saveColor = SigColor.getColorTag();
      
      if(useStackColor) {
        SigColor.setColor("STACK");
        g.setColor(SigColor.getColor());
      }
      
      drawMast(x, y, g);
      g.setColor(oldColor);
      SigColor.setColor(saveColor);
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
    Anchor = Where.locateCorner(bounds, new Dimension(BBox));
  }

  /**
   * changes the Location on the GridTile of where the Signal Icon is placed.
   *
   * @param where is the FrillLoc describing where to place the Signal Icon.
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
    return BBox;
  }

  /**
   * set the Tag for selecting the color.
   *
   * @param color is the Color tag.
   */
  public void setSignalColor(String color) {
    SigColor.setColor(color);
  }

  /**
   * is a method for determining if the Mouse is positioned over it
   * when a button is clicked.  If so, and if it accepts the button
   * push, it returns a MouseUser object to handle the button push.
   *
   * @param event is the MouseEvent.
   *
   * @return this, if the Mouse is over the Signal Icon, so that it
   *         can be called when the mouse button is relesed; otherwise,
   *         return null, so that other Frills can be queried.
   */
  public MouseUser mouseDown(MouseEvent event) {
    Rectangle rec = new Rectangle(Anchor, BBox);
//    System.out.println("Signal: " + rec.toString() + " Mouse: " + event.getPoint().toString());
    if (rec.contains(event.getPoint())) {
      return this;
    }
    return null;
  }

  /**
   *   This method tells the Object that accepted the Mouse Press
   *   that the user released the mouse button.  In this case,
   *   an event is sent to the associated Signal (assuming the
   *   Mouse did not move off the Icon).
   *
   * @param event provides information on the Mouse button release
   *
   */
  public void finishMouse(MouseEvent event) {
    Rectangle rec = new Rectangle(Anchor, BBox);
    if (rec.contains(event.getPoint())) {
      Owner.signalMouse(event);
      GridTile.doUpdates();
    }
  }

  /*
   * the following are what make this class abstract.
   */
  /**
   * paint a single head.
   * <p>
   * @param x is the x coordinate of the upper left corner of the head.
   *
   * @param y is the y coordinate of the upper left corner of the head.
   *
   * @param g is the Graphics to draw the head on.
   */
  public abstract void drawHead(int x, int y, Graphics g);

  /**
   * paint the signal mast.
   *<p>
   * @param x is the x coordinate of the upper left corner of the mast.
   *
   * @param y is the y coordinate of the upper left corner of the mast.
   *
   * @param g is the Graphics to draw the head on.
   */
  public abstract void drawMast(int x, int y, Graphics g);

}
/* @(#)SignalFrill.java */