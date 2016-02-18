// MeterLnReporterManagerXml.java

package cats.jmri.configurexml;

import cats.jmri.MeterLnReporterManager;
import org.jdom2.Element;

/**
 * Provides load and store functionality for
 * configuring MeterReporterManagers.
 * <P>
 * Uses the store method from the abstract base class, but
 * provides a load method here.
 * <P>
 * Based on SerialLightManagerXml.java
 *
 *
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2006, 2009, 2016</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */
public class MeterLnReporterManagerXml
    extends jmri.managers.configurexml.AbstractReporterManagerConfigXML {

  /**
   * is the constructor
   */
  public MeterLnReporterManagerXml() {
    super();
  }
 
  /**
   * return the JMRI system ID.  Note that "M" is used by Maple, so
   * this could cause a conflict.
   * @return "M"
   */
  public char systemLetter() { return 'M'; }

  public void setStoreElementClass(Element reporters) {
    reporters.setAttribute("class", "cats.jmri.MeterLnReporterManagerXml");
//    JDOMfixer.addsetAttribute(reporters, "class", "cats.jmri.configurexml.MeterLnReporterManagerXml");
//    JDOMfixer.addsetAttribute(reporters, "class",this.getClass().getName());
  }

  public void load(Element element, Object o) {
    log.error("Invalid method called");
  }

  public boolean load(Element reporters) {
    // create the master object
    MeterLnReporterManager.instance();
    // load individual reporters
    loadReporters(reporters);
    return true;
  }

  static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(
      MeterLnReporterManagerXml.class.getName());
}