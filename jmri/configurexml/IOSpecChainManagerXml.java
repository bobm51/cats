/**
 * Name: IOSpecChainManagerXml.java
 * 
 * What:
 *     This file contains a class to satisfy the JMRI Manager.
 */
package cats.jmri.configurexml;

import org.jdom.Element;

import cats.jmri.IOSpecChainManager;

/**
 * This file contains a class to satisfy the JMRI Manager.
 * So, it is not guaranteed to work outside of CATS.
 *
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A model railroad dispatching program</p>
 * <p>Copyright: Copyright (c) 2006, 2009</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version 1.0
 */
public class IOSpecChainManagerXml extends jmri.managers.configurexml.AbstractSensorManagerConfigXML {

  /**
   * is the constructor
   */
  public IOSpecChainManagerXml() {
      super();
  }

  public void setStoreElementClass(Element sensors) {
    sensors.setAttribute("class",this.getClass().getName());
//    JDOMfixer.addsetAttribute(sensors, "class",this.getClass().getName());
  }

  public void load(Element element, Object o) {
      log.error("Invalid method called");
  }

  public boolean load(Element sensors) {
      // create the master object
      IOSpecChainManager.instance();
      return true;
  }

  static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(MeterLnSensorManagerXml.class.getName());
}
