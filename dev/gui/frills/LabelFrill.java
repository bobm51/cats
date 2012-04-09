/* Name: LabelFrill.java
 *
 * What:
 *   This file defines a class for writing Strings on the GridTile.
 */
package cats.gui.frills;

import cats.gui.DispPanel;
import cats.gui.jCustom.FontFinder;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Point;
import java.awt.Rectangle;
import javax.swing.JLabel;

//import java.awt.Shape;

/**
 * LabelFrill defines a class for writing Strings on the GridTile.
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2004, 2009</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */

public class LabelFrill
    extends Frills {

  /**
   * is the Label to print.
   */
  protected String Lab;

  /** is where to print it in the GridTile.  It can spill over into
   * other GridTiles.
   */

  protected FrillLoc Where;

  /**
   * is the kind of font to use for printing the label.
   */
  protected FontFinder MyFont;

  /**
   * is the actual label.
   */
  protected JLabel MyLabel;

  /**
   * constructs the LabelFrill.
   *
   * @param label is the String to be written.
   * @param position is where to write the String.
   * @param font is the CtcFont to use
   *
   * @see FrillLoc
   */
  public LabelFrill(String label, FrillLoc position, FontFinder font) {
    Lab = new String(label);
    MyLabel = new JLabel(label);
    MyFont = font;
    Where = position;
    DispPanel.ThePanel.addLabel(MyLabel);
  }

  /*
   * is the method called by a GridTile, describing itself - the area on
   * the screen to be written to.
   *
   * @param g is the Graphics context on which to draw
   */
  public void decorate(Graphics g) {
//    Font f1 = FontFactory.Fonts.findFont("FONT_LABEL").grabFont();
    Font f2 = MyFont.getFont();
//    MyLabel.setFont(MyFont.getFont());
    MyLabel.setFont(f2);
    MyLabel.setForeground(MyFont.getColor());
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
  public void setDrawing(Rectangle bounds, Rectangle clip) {
    Point anchor;
    Font myFont = MyFont.getFont();
    FontMetrics currentMetrics = DispPanel.ThePanel.getFontMetrics(
        myFont);
    Dimension size = new Dimension(currentMetrics.stringWidth(Lab) + 4,
                                   currentMetrics.getHeight());
    anchor = Where.locateCorner(bounds, size);
    MyLabel.setSize(size);
    MyLabel.setLocation(anchor);
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
    Font myFont = MyFont.getFont();
    FontMetrics currentMetrics = DispPanel.ThePanel.getFontMetrics(
        myFont);
    int h;
    int w;
    h = Math.min(square.height, currentMetrics.getHeight());
    w = Math.min(square.width, currentMetrics.stringWidth(Lab) + 4);
    return (new Dimension(w, h));
  }

  /**
   * is a method to change the Font tag through which the Label gets its Font.
   *
   * @param font is the key for the font description.
   *
   */
  public void setPalette(String font) {
    MyFont.setCurrent(font);
  }
}
/* @(#)LabelFrill.java */