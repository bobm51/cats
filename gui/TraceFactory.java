/* Name: TraceFactory.java
 *
 * What: This file contains the TraceFactory class.  It is a singleton
 *  that holds the list of trace flags.
 *
 * Special Considerations:
 */
package cats.gui;

import java.util.Hashtable;
import javax.swing.JMenu;

/**
 * This file contains the TraceFactory class.  It is a singleton
 * that holds the list of trace flags.
 * <p>
 * The list of trace flags is distributed and dynamic.  To add a new
 * one, the user simply calls createTraceFlag with a String for
 * identifying the flag.  If it already exists, it is returned.  If it
 * doesn't exist it is created and returned.  It is added to the menu
 * when created.
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2004, 2009</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */
public class TraceFactory
    extends JMenu {

  /**
   * the singleton.
   */
  public static TraceFactory Tracer;

  /**
   * is the list of trace flags.
   */
  private Hashtable<String, TraceFlag> TraceFlags;

  /**
   * constructs the factory.
   *
   * @param label is the JMenu label for the factory.
   */
  public TraceFactory(String label) {
    super(label);
    TraceFlags = new Hashtable<String, TraceFlag>();
    Tracer = this;
  }

  /**
   * creates a new trace flag, returning the JMenuItem used to select it.
   *
   * @param label is the JMenuItem label identifying the flag.
   * @param tag is the tag used to identify the flag in the XML file.
   * This parameter is not used, but is needed by the TraceFlag
   * ctor.
   *
   * @return the TraceFlag.
   */
  public TraceFlag createTraceItem(String label, String tag) {
    TraceFlag flag = null;
    if (!TraceFlags.containsKey(label)) {
      TraceFlags.put(label, flag = new TraceFlag(label, tag));
      add(flag);
    }
    else {
      flag = TraceFlags.get(label);
    }
    return flag;
  }
}