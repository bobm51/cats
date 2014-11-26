/* Name: frills.java
 *
 * What:
 *   This file contains an abstract class used for all Objects which draw
 *   on a GridTile.  The concept is that the Objects register with the
 *   GridTile and when the GridTile is told to paint itself, it invokes
 *   an abstract method for each registered Object
 *   (Observer pattern).  The parameters passed to the method describe
 *   where the Object can draw.
 *
 *   This name, frills, comes from the movie Office Space, where the
 *   character played by Jennifer Aniston is a waitress who can't keep
 *   a job, partially because she refuses to conform to management
 *   dictates that she wear little badges and emblems (called "frills")
 *   indicating that she is a team player and has achieved management
 *   goals.  Thus, these Objects apply "frills" to the GridTiles.
 */
package cats.gui.frills;

import cats.gui.MouseUser;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.Graphics;
import java.awt.Rectangle;

/**
 *   This file contains an abstract class used by all Objects which draw
 *   on a GridTile.  The concept is that the Objects register with the
 *   GridTile and when the GridTile is told to paint itself, it invokes
 *   a an abstract method for each registered Object
 *   (Observer pattern).  The parameters passed to the method describe
 *   where the Object can draw.
 *
 *   This name, frills, comes from the movie Office Space, where the
 *   character played by Jennifer Aniston is a waitress who can't keep
 *   a job, partially because she refuses to conform to management
 *   dictates that she wear little badges and emblems (called "frills")
 *   indicating that she is a team player and has achieved management
 *   goals.  Thus, these Objects apply "frills" to the GridTiles.
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2004, 2009</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */
public abstract class Frills {
  /**
   * is the method called by a GridTile, to determine the Dimensions of
   * the drawing area needed by the Frill.
   *
   * @param square is a Dimension describing the maximum space allocated
   * to the Frill.
   *
   * @return how much of the square is used.  The default is to use it all.
   */
  public Dimension getDefSize(Dimension square) {
    return square;
  }

  /**
   * is the method called by a GridTile, describing itself - the area on
   * the screen to be written to.
   *
   * @param g is the Graphics context on which to draw
   */
  abstract public void decorate(Graphics g);

   public void setStackColor(boolean stackInProgress) {
       return;
   }

  /**
   * is the method called by a GridTile whenever it is moved or its size
   * changes.  The intent is to tell the Frill that it needs to recompute
   * the parameters on how it draws itself.
   *
   * @param bounds describes the boundary of the GridTile.
   * @param clip describes the clipping area, the drawing area after
   *        considering Block gaps.
   */
  abstract public void setDrawing(Rectangle bounds, Rectangle clip);

  /**
   * is a method for re-evaluating the colors.  It is intended to be called
   * when a Palette changes.
   */
//  abstract public void refreshColors();

  /**
   * is a method for determining if the Mouse is positioned over it
   * when a button is clicked.  If so, and if it accepts the button
   * push, it returns a MouseUser object to handle the button push.
   *
   * @param event is the MouseEvent.
   *
   * @return unless overriden in a derived class, the return value is
   * that the Frill does not care about MouseEvents.
   */
  public MouseUser mouseDown(MouseEvent event) {
    return null;
  }
}
/* @(#)Frills.java */