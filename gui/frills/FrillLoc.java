/* Name: FrillLoc.java
 *
 * What:
 *   This file defines a class for representing where in a GridTile a
 *   Frill is to be positioned.
 */
package cats.gui.frills;

import cats.gui.CtcFont;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

/**
 *   FrillLoc defines a class for representing where in a GridTile
 *   a Frill is to be positioned.
 * <p>Title: designer</p>
 * <p>Description: A program for designing dispatcher panels</p>
 * <p>Copyright: Copyright (c) 2003, 2009</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */

public abstract class FrillLoc {
  // These map a String to a location on the Grid
  static final String LocName[] = {
      "LEFTUP",
      "LEFTCENT",
      "LEFTLOW",
      "LOWLEFT",
      "LOWCENT",
      "LOWRIGHT",
      "RIGHTLOW",
      "RIGHTCENT",
      "RIGHTUP",
      "UPRIGHT",
      "UPCENT",
      "UPLEFT",
      "CENT"
  };

  // The FrillLocs corresponding to the above
  static final FrillLoc[] FrillLocation  = {
      new LeftUp(), new LeftCent(), new LeftLow(),
      new LowLeft(), new LowCent(), new LowRight(),
      new RightLow(), new RightCent(), new RightUp(),
      new UpRight(), new UpCent(), new UpLeft(),
      new Cent()
  };

  /**
   * converts a name to an index.  This depends upon CtcFont.
   *
   * @param match is the String to search for.
   *
   * @return the index corresponding to the FrillLoc name or UNKNOWN.
   */
  public static int findFrillLoc(String match) {
    return CtcFont.findString(match, LocName);
  }

  /**
   * converts an index into FrillLocation to the Object.
   *
   * @param index is the index into the array
   *
   * @return the FrillLoc corresponding to the index or null (if it is
   * out of bounds).
   */
  public static FrillLoc newFrillLoc(int index) {
    if ((index >= 0) && (index < FrillLocation.length)) {
      return FrillLocation[index];
    }
    return null;
  }

  /**
   * converts a name to a FrillLoc.
   *
   * @param name is the FrillLoc to look for.
   *
   * @return the FrillLoc (if found) or null.
   */
  public static FrillLoc newFrillLoc(String name) {
    return newFrillLoc(findFrillLoc(name));
  }

  /**
   * computes the X coordinate of the upper left corner of a rectangle
   * (represented as a Dimension) in a Bounding Box, when positioned on
   * the Left edge of the Bounding Box.
   *
   * @param limits is the Bounding Box.
   * @param box is the rectangle.
   *
   * @return the X coordinate,
   */
  final static int xLeft(Rectangle limits, Dimension box) {
    return limits.x + 1;
  }

  /**
   * computes the X coordinate of the upper left corner of a rectangle
   * (represented as a Dimension) in a Bounding Box, when positioned just
   * off the Left edge of the Bounding Box.
   *
   * @param limits is the Bounding Box.
   * @param box is the rectangle.
   *
   * @return the X coordinate,
   */
  final static int xOffLeft(Rectangle limits, Dimension box) {
    return limits.x + (limits.width / 4);
  }

  /**
   * computes the X coordinate of the upper left corner of a rectangle
   * (represented as a Dimension) in a Bounding Box, when positioned in
   * the center of the Bounding Box.
   *
   * @param limits is the Bounding Box.
   * @param box is the rectangle.
   *
   * @return the X coordinate,
   */
  final static int xCenter(Rectangle limits, Dimension box) {
    return limits.x + ((limits.width - box.width) / 2);
  }

  /**
   * computes the X coordinate of the upper left corner of a rectangle
   * (represented as a Dimension) in a Bounding Box, when positioned
   * just off the Right edge of the Bounding Box.
   *
   * @param limits is the Bounding Box.
   * @param box is the rectangle.
   *
   * @return the X coordinate,
   */
  final static int xOffRight(Rectangle limits, Dimension box) {
    return limits.x + ((3 * limits.width) / 4) - box.width;
  }

  /**
   * computes the X coordinate of the upper left corner of a rectangle
   * (represented as a Dimension) in a Bounding Box, when positioned
   * on the Right edge of the Bounding Box.
   *
   * @param limits is the Bounding Box.
   * @param box is the rectangle.
   *
   * @return the X coordinate,
   */
  final static int xRight(Rectangle limits, Dimension box) {
    return limits.x + limits.width - box.width - 1;
  }

  /**
   * computes the Y coordinate of the upper left corner of a rectangle
   * (represented as a Dimension) in a Bounding Box, when positioned
   * on the Top edge of the Bounding Box.
   *
   * @param limits is the Bounding Box.
   * @param box is the rectangle.
   *
   * @return the Y coordinate,
   */
  final static int yUp(Rectangle limits, Dimension box) {
    return limits.y + 1;
  }

  /**
   * computes the Y coordinate of the upper left corner of a rectangle
   * (represented as a Dimension) in a Bounding Box, when positioned
   * just below the Top edge of the Bounding Box.
   *
   * @param limits is the Bounding Box.
   * @param box is the rectangle.
   *
   * @return the Y coordinate,
   */
  final static int yOffUp(Rectangle limits, Dimension box) {
    return limits.y + (limits.height / 4);
  }

  /**
   * computes the Y coordinate of the upper left corner of a rectangle
   * (represented as a Dimension) in a Bounding Box, when positioned
   * in the center of the Bounding Box.
   *
   * @param limits is the Bounding Box.
   * @param box is the rectangle.
   *
   * @return the Y coordinate,
   */
  final static int yCenter(Rectangle limits, Dimension box) {
    return limits.y + ((limits.height - box.height) / 2) - 1;
  }

  /**
   * computes the Y coordinate of the upper left corner of a rectangle
   * (represented as a Dimension) in a Bounding Box, when positioned
   * just above the bottom of the Bounding Box.
   *
   * @param limits is the Bounding Box.
   * @param box is the rectangle.
   *
   * @return the Y coordinate,
   */
  final static int yOffLow(Rectangle limits, Dimension box) {
    return limits.y + ((3 * limits.height) / 4) - box.height;
  }

  /**
   * computes the Y coordinate of the upper left corner of a rectangle
   * (represented as a Dimension) in a Bounding Box, when positioned
   * on the bottom of the Bounding Box.
   *
   * @param limits is the Bounding Box.
   * @param box is the rectangle.
   *
   * @return the Y coordinate,
   */
  final static int yLow(Rectangle limits, Dimension box) {
    return limits.y + limits.height - box.height - 1;
  }

  /**
   * determines where in a Bounding Box, the upper left corner of a
   * rectangle (described by a Dimension) should be placed.
   *
   * @param boundingBox is the Bounding Box.
   * @param rec is the rectangle
   *
   * @return a Point describing the coordinates of the upper left corner.
   */
  abstract public Point locateCorner(Rectangle boundingBox, Dimension rec);

}

/**
 * the FrillLoc for something on the left edge, down from the top.
 */
final class LeftUp
    extends FrillLoc {
  public Point locateCorner(Rectangle boundingBox, Dimension rec) {
    return new Point(xLeft(boundingBox, rec), yOffUp(boundingBox, rec));
  }

  public String toString() {
    return new String(LocName[0]);
  }
}

  /**
   * the FrillLoc for something on the left edge, center.
   */
  final class LeftCent
      extends FrillLoc {
    public Point locateCorner(Rectangle boundingBox, Dimension rec) {
      return new Point(xLeft(boundingBox, rec), yCenter(boundingBox, rec));
    }

    public String toString() {
      return new String(LocName[1]);
    }
  }

  /**
   * the FrillLoc for something on the left edge, above the bottom.
   */
  final class LeftLow
      extends FrillLoc {
    public Point locateCorner(Rectangle boundingBox, Dimension rec) {
      return new Point(xLeft(boundingBox, rec), yOffLow(boundingBox, rec));
    }

    public String toString() {
      return new String(LocName[2]);
    }
  }

  /**
   * the FrillLoc for something on the lower edge, left of the center.
   */
  final class LowLeft
      extends FrillLoc {
    public Point locateCorner(Rectangle boundingBox, Dimension rec) {
      return new Point(xOffLeft(boundingBox, rec), yLow(boundingBox, rec));
    }

    public String toString() {
      return new String(LocName[3]);
    }
  }

  /**
   * the FrillLoc for something on the lower edge, center.
   */
  final class LowCent
      extends FrillLoc {
    public Point locateCorner(Rectangle boundingBox, Dimension rec) {
      return new Point(xCenter(boundingBox, rec), yLow(boundingBox, rec));
    }

    public String toString() {
      return new String(LocName[4]);
    }
  }

  /**
   * the FrillLoc for something on the lower edge, right of center.
   */
  final class LowRight
      extends FrillLoc {
    public Point locateCorner(Rectangle boundingBox, Dimension rec) {
      return new Point(xOffRight(boundingBox, rec), yLow(boundingBox, rec));
    }

    public String toString() {
      return new String(LocName[5]);
    }
  }

  /**
   * the FrillLoc for something on the Right edge, off the bottom.
   */
  final class RightLow
      extends FrillLoc {
    public Point locateCorner(Rectangle boundingBox, Dimension rec) {
      return new Point(xRight(boundingBox, rec), yOffLow(boundingBox, rec));
    }

    public String toString() {
      return new String(LocName[6]);
    }
  }

  /**
   * the FrillLoc for something on the Right edge, center.
   */
  final class RightCent
      extends FrillLoc {
    public Point locateCorner(Rectangle boundingBox, Dimension rec) {
      return new Point(xRight(boundingBox, rec), yCenter(boundingBox, rec));
    }

    public String toString() {
      return new String(LocName[7]);
    }
  }

  /**
   * the FrillLoc for something on the Right edge, just below the top.
   */
  final class RightUp
      extends FrillLoc {
    public Point locateCorner(Rectangle boundingBox, Dimension rec) {
      return new Point(xRight(boundingBox, rec), yOffUp(boundingBox, rec));
    }

    public String toString() {
      return new String(LocName[8]);
    }
  }

  /**
   * the FrillLoc for something on the top edge, just off the Right.
   */
  final class UpRight
      extends FrillLoc {
    public Point locateCorner(Rectangle boundingBox, Dimension rec) {
      return new Point(xOffRight(boundingBox, rec), yUp(boundingBox, rec));
    }

    public String toString() {
      return new String(LocName[9]);
    }
  }

  /**
   * the FrillLoc for something on the top edge, center.
   */
  final class UpCent
      extends FrillLoc {
    public Point locateCorner(Rectangle boundingBox, Dimension rec) {
      return new Point(xCenter(boundingBox, rec), yUp(boundingBox, rec));
    }

    public String toString() {
      return new String(LocName[10]);
    }
  }

  /**
   * the FrillLoc for something on the top edge, off the Left edge.
   */
  final class UpLeft
      extends FrillLoc {
    public Point locateCorner(Rectangle boundingBox, Dimension rec) {
      return new Point(xOffLeft(boundingBox, rec), yUp(boundingBox, rec));
    }

    public String toString() {
      return new String(LocName[11]);
    }
  }

  /**
   * the FrillLoc for something in the center.
   */
  final class Cent
      extends FrillLoc {
    public Point locateCorner(Rectangle boundingBox, Dimension rec) {
      return new Point(xCenter(boundingBox, rec), yCenter(boundingBox, rec));
    }

    public String toString() {
      return new String(LocName[12]);
    }
  }
  /* @(#)FrillLoc.java */