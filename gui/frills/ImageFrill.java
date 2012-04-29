/* Name: ImageFrill.java
 *
 * What:
 *  ImageFrill is a class of objects for painting images on Tiles.
 */
package cats.gui.frills;

import cats.gui.DispPanel;
import java.awt.AWTError;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Image;
import java.awt.Toolkit;

/**
 * a class for painting images on Tiles.  These images can be anything
 * of the Java AWT Image class, read through a getImage(<filename>)
 * method.  The image can be a logo, thumbnail, or anything else to
 * make the dispatcher panel a little more fancy.
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2004, 2009</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */

public class ImageFrill
    extends Frills {

  /**
   * the image.
   */
  private Image MyImage;
  /**
   * is the Bounding Rectangle on the GridTile.
   */
  private Rectangle Bounds;

  /**
   * the constructor.  The upper left corner of the image is drawn in
   * the upper left corner of the GridTile.
   *
   * @param fileName is the path to the file containing the Image.  If the
   * file cannot be read then a message is logged; thus, the caller should
   * verify that the file exists before instantiating an ImageFrill.
   */
  public ImageFrill(String fileName) {
    try {
      MyImage = Toolkit.getDefaultToolkit().getImage(fileName);
    }
    catch (AWTError err) {
      log.warn("Failure to open Image file " + fileName);
      System.out.println("Failure to open Image " + fileName);
    }
  }

  /*
   * is the method called by a GridTile, describing itself - the area on
   * the screen to be written to.
   *
   * @param g is the Graphics context on which to draw
   */
  public void decorate(Graphics g) {
    Rectangle oldClip = g.getClipBounds();
    if (MyImage != null) {
      if (MyImage.getHeight(DispPanel.ThePanel) > 0) {
        Bounds.height = MyImage.getHeight(DispPanel.ThePanel);
      }
      if (MyImage.getWidth(DispPanel.ThePanel) > 0) {
        Bounds.width = MyImage.getWidth(DispPanel.ThePanel);
      }

      g.setClip(Bounds);
//      System.out.println("ImageFrill: is painting the image.");
      g.drawImage(MyImage, Bounds.x, Bounds.y, DispPanel.ThePanel);
      g.setClip(oldClip);
    }
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
    Bounds = new Rectangle(bounds);
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
    return square;
  }

  /**
   * is a method for re-evaluating the colors.  It is intended to be called
   * when a Palette changes.
   */
  public void refreshColors() {

  }

  static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(
      ImageFrill.class.getName());
}
/* @(#)ImageFrill.java */