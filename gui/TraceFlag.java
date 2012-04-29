/* Name: TraceFlag.java
 *
 * What: This file contains the TraceFlag class.  Each instantiation has
 *  a flag, a JMenuItem for changing the state of the flag, and an
 *  accessor method for retrieving the state of the flag.
 *
 * Special Considerations:
 */
package cats.gui;

/**
 *  This file contains the TraceFlag class.  Each instantiation has
 *  a flag, a JMenuItem for changing the state of the flag, and an
 *  accessor method for retrieving the state of the flag.
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */
public class TraceFlag
    extends BooleanGui {
  
  /**
   * constructs the factory.
   * @param label is the menu label for the flag in the Trace pulldown
   * @param tag is the XML tag.  This is not used, but is needed by
   * the BooleanGui parent type.
   */
  public TraceFlag(String label, String tag) {
    super(label, tag, false);
  }

  /**
   * is the accessor for retrieving the value of the flag.
   * @return the current trace setting
   */
  public boolean getTraceValue() {
    return getFlagValue();
  }
}
/* @(#)TraceFlag.java */