/* Name: Prop.java
 *
 * What: This file contains helper methods for identifying simple properties
 *  which are arrays of strings.
 *
 * Special Considerations:
 */
package cats.common;

/**
 * defines a class for searching for manipulating arrays of Strings.
 *
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */
public class Prop {
	
	/**
	 * a constant used in searching to indicate that the thing
	 * being looked for was not found.
	 */
  public static final int NOT_FOUND = -1;

  /**
   * searches an array of Strings for a particular String.
   *
   * @param query is the String being looked for.
   * @param array is the array being searched.
   *
   * @return the index of the String in the array, if found, or NOT_FOUND.
   */
  public static int findString(String query, String[] array) {
    for (int index = 0; index < array.length; ++index) {
      if (query.equals(array[index])) {
        return index;
      }
    }
    return NOT_FOUND;
  }

  /**
   * searches an array of ints for a particular int.
   *
   * @param query is the int being looked for.
   * @param array is the array being searched.
   *
   * @return the index of the int in the array, if found, or NOT_FOUND.
   */
  public static int findInt(int query, int[] array) {
    for (int index = 0; index < array.length; ++index) {
      if (query == array[index]) {
        return index;
      }
    }
    return NOT_FOUND;
  }
}
