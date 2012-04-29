/* Name: Tracker.java
 *
 * What: This file contains the Tracker class.  It provides a flag for
 *   indicating if automatic train tracking should be on or not and a
 *   menu for changing the value.
 *
 * Special Considerations:
 */
package cats.gui;

/**
 * This file contains the Tracker class.  It provides a flag for
 * indicating if automatic train tracking should be on or not and a
 * menu for changing the value.
 * <p>
 * When tracking is enabled, CATS will attempt to find a Train label on an
 * adjacent Block when a Block is occupied.  If found, it will move the
 * Train label to the newly occupied Block.  This seems like it should
 * always be enabled, but if the detection system is not reliable (for
 * example, it generates false detections), this automatic movment would
 * confuse the location of trains.
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2004, 2007</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */
public class Tracker
    extends BooleanGui {

  /**
   * is the tag for identifying a Tracker Object in the XMl file.
   */
  static final String XMLTag = "TRACKERTAG";
  
  /**
   * is the label on the JCheckBoxMenuItem
   */
  static final String Label = "Train Tracker";


  /**
   * is how other classes locate the Tracker state.
   */
  public static Tracker AutoTracker;


  /**
   * constructs the menu items and initializes the Tracking value.
   */
  public Tracker() {
    super(Label, XMLTag, true);
    AutoTracker = this;
  }

  /**
   * returns the current Tracking setting.
   *
   * @return true if Tracking is enabled and false if it is not.
   */
  public boolean getTracking() {
    return getFlagValue();
  }
}
/* @(#)Tracker.java */