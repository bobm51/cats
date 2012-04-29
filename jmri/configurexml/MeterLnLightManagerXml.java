// MeterLnLightManagerXml.java

package cats.jmri.configurexml;

import cats.jmri.MeterLnLightManager;
import org.jdom.Element;

/**
 * Provides load and store functionality for
 * configuring MeterLightManagers.
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
 * <p>Copyright: Copyright (c) 2004, 2009</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */
public class MeterLnLightManagerXml
    extends jmri.managers.configurexml.AbstractLightManagerConfigXML {

  /**
   * is the constructor
   */
  public MeterLnLightManagerXml() {
    super();
  }

  public void setStoreElementClass(Element lights) {
//    JDOMfixer.addsetAttribute(lights, "class", "cats.jmri.configurexml.MeterLnLightManagerXml");
//    JDOMfixer.addsetAttribute(lights, "class",this.getClass().getName());
    lights.setAttribute("class", "cats.jmri.configurexml.MeterLnLightManagerXml");
  }

  public void load(Element element, Object o) {
    log.error("Invalid method called");
  }

  public boolean load(Element lights) {
    // create the master object
    MeterLnLightManager.instance();
    // load individual lights
    loadLights(lights);
    return true;
  }

  static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(
      MeterLnLightManagerXml.class.getName());
}