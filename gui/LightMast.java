package cats.gui;

/**
 *  LightMast is a Singleton object with a boolean value.  It is set
 *  to true if the base (mast) of panel signals is to be painted
 *  as an inverted tee.  If false, the base is painted as a triangle.
 * <p>Title: cats</p>
 * <p>Description: A program for designing dispatcher panels</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */

public class LightMast
extends BooleanGui {

  /**
   * is the tag for identifying a LightMast Object in the XMl file.
   */
  static final String XMLTag = "TEEMAST";
  
  /**
   * is the label on the JCheckBoxMenuItem
   */
  static final String Label = "Tee Base";

  /**
   * is the singleton.
   */
  public static LightMast TheMastType;

  /**
   * constructs the factory.
   */
  public LightMast() {
    super(Label, XMLTag, false);
    TheMastType = this;
  }
}
/* @(#)LightMast.java */