/* Name: OccupancyFilterCounter.java
 *
 * What:
 *  This file defines a Singleton that counts the number of
 *  occupancy events that are too short to trigger occupancy
 *  detection.
 * Special Considerations:
 */
package cats.layout;

/**
 *  This file defines a Singleton that counts the number of
 *  occupancy events that are too short to trigger occupancy
 *  detection.
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */
public class OccupancyFilterCounter extends PegCounter {
  
  /**
   * is the PegCounter for filtered occupancy events
   */
  private static OccupancyFilterCounter OccupancyFilter;
  
  /**
   * is the JMRI User Name for the associated Memory object
   */
  public static final String JMRI_NAME = "OccupancyFilter";
  
  /**
   * is the ctor
   */
  private OccupancyFilterCounter() {
    super(JMRI_NAME);
  }
  
  /**
   * is used to locate the singleton
   * 
   * @return the singleton.  If it does not exist, this method
   * creates it.
   */
  public static OccupancyFilterCounter instance() {
    if (OccupancyFilter == null) {
      OccupancyFilter = new OccupancyFilterCounter();
    }
    return OccupancyFilter;
  }
}
/* @(#)OccupancyFilterCounter.java */