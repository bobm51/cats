package cats.gui;

/**
 *  DirectionArrow is a Singleton object with a boolean value.  It is set
 *  to false if the arrow showing train direction should not be painted.
 *  If true, the arrow will be painted.
 * <p>Title: cats</p>
 * <p>Description: A program for designing dispatcher panels</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */

public class DirectionArrow
extends BooleanGui {
  /**
   * is the tag for identifying a DirectionArrow Object in the XML file.
   */
  static final String XMLTag = "DIRECTIONARROW";
  
  /**
   * is the label on the JCheckBoxMenuItem
   */
  static final String Label = "Direction Arrow";

  /**
   * is the singleton.
   */
  public static DirectionArrow TheArrowType;

  /**
   * constructs the factory.
   */
  public DirectionArrow() {
    super(Label, XMLTag, true);
    TheArrowType = this;
  }
}
/* @(#)DirectionArrow.java */