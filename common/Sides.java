/* Name: Sides.java
 *
 * What:
 *   This class holds some constants for referring to specific sides
 *   of a Section or GridTile.
 */

package cats.common;

/**
 * A class for holding constants for referring to specific sides
 * of a Section or GridTile.
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */
public class Sides {
  /**
   * is the array of Strings for identifying a Side (Edge).  These should all
   * be uppercase (because the mapping method converted candidates to upper
   * case) and in the same order of the constants.
   */
  public static final String EDGENAME[] = {
      "RIGHT",
      "BOTTOM",
      "LEFT",
      "TOP"
  };

  /**
   * identifies the Edge on the right.
   */
  public static final int RIGHT = 0;

  /**
   * identifies the Edge on the bottom.
   */
  public static final int BOTTOM = 1;

  /**
   * identifies the Edge on the left.
   */
  public static final int LEFT = 2;

  /**
   *   identifies the Edge on the top.
   */
  public static final int TOP = 3;

  /**
   * locates which Side is being referenced.
   *
   * @param side is possibly the name of a Side.
   *
   * @return the value of the Side or NOT_FOUND if invalid.
   */
  public static int findSide(String side) {
    return Prop.findString(side, EDGENAME);
  }
}
