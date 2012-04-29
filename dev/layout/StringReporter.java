/* Name: StringReporter.java
 *
 * What:
 *   This file contains an object derived from the JMRI AbstractReporter class
 *   that receives Strings and distibutes them to registered listeners.  The purpose
 *   behind this class is to broadcast changes in the layout to observers.  Event
 *   loggers and the train status screens are expected to be key users of this class.
 *  
 *  Special Considerations:
 */
package cats.layout;

import jmri.implementation.AbstractReporter;
import jmri.JmriException;

/**
 *   This file contains an object derived from the JMRI AbstractReporter class
 *   that receives Strings and distibutes them to registered listeners.  The purpose
 *   behind this class is to broadcast changes in the layout to observers.  Event
 *   loggers and the train status screens are expected to be key users of this class.
 * 
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2008, 2009</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

public class StringReporter extends AbstractReporter {

  /**
   * is not used, but is needed by JMRI.
   */
  private int ReporterState;
  
  /**
   * is the ctor
   * @param systemName is the JMRI system name by which the object is known.
   */
  public StringReporter(String systemName) {
    super(systemName);
    ReporterState = 0;
  }

  /**
   * is a two argument ctor
   * @param systemName is the JMRI system name by which the object is known
   * @param userName is the JMRI user name by which the object is known
   */
  public StringReporter(String systemName, String userName) {
    super(systemName, userName);
    ReporterState = 0;
  }

  /**
   * does not do much.
   * @return ReporterState;
   */
  public int getState() {
    return ReporterState;
  }

  /**
   * just saves the parameter.
   * @param s is the new value of the internal state.
   */
  public void setState(int s) throws JmriException {
    ReporterState = s;
  }  
  
  static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(StringReporter.class.getName());
}
/* @(#)StringReporter.java */
