package cats.gui;

/**
 *  RowWrap is a Singleton object with a boolean value.  It is set
 *  to true if CATS should try to be intelligent about where to wrap
 *  one row of track to the next row.  True means it should.  False
 *  turns row wrap off.
 * <p>Title: cats</p>
 * <p>Description: A program for designing dispatcher panels</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */

public class RowWrap
extends BooleanGui {
  /**
   * is the tag for identifying a RowWrap Object in the XML file.
   */
  static final String XMLTag = "ROWWRAPTAG";
  
  /**
   * is the label on the JCheckBoxMenuItem
   */
  static final String Label = "Automatic Wrapping";

  /**
   * is the singleton.
   */
  public static RowWrap TheWrapType;

  /**
   * constructs the factory.
   */
  public RowWrap() {
    super(Label, XMLTag, true);
    TheWrapType = this;
  }
}
/* @(#)RowWrap.java */