/**
 * Name: SWSignalHeadXml
 *
 * What:
 * Provides load and store functionality for
 * configuring SWSignalHeads.
 * <P>
 * Modeled after SE8cSignalHeadXml
 *
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */
package cats.jmri.configurexml;

import org.jdom.Attribute;
import org.jdom.Element;

import cats.jmri.SWSignalHead;

import jmri.InstanceManager;
import jmri.SignalHead;

/**
 * Provides load and store functionality for
 * configuring SWSignalHeads.  This method facilitates storing
 * SWSignalHead references through saving a JMRI Signal Table.
 * Once saved, it can be loaded by loading a JMRI panel.  The
 * SWSignalHead thus loaded will be empty, but scripts will
 * be able to access it.
 * <P>
 * Modeled after SE8cSignalHeadXml
 *
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2004, 2009</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */

public class SWSignalHeadXml extends jmri.managers.configurexml.AbstractNamedBeanManagerConfigXML {

  /**
   * creates a SWSignalHead from a JDOM Element.
   * 
   * @param e is the Element that describes the SWSignalHead
   */
  public boolean load(Element e) throws Exception {
    Attribute u = e.getAttribute("userName");
    Attribute s = e.getAttribute("systemName");
    SignalHead h;
    if (s == null) {
      if (u == null) {
        h = new SWSignalHead(SWSignalHead.createSignalHeadName());
      }
      else {
        h = new SWSignalHead(SWSignalHead.createSignalHeadName(),
            u.getValue());        
      }
    }
    else {
      if (u == null) {
        h = new SWSignalHead(s.getValue());
      }
      else {
        h = new SWSignalHead(s.getValue(), u.getValue());        
      }
    }
    InstanceManager.signalHeadManagerInstance().register(h);
    return true;
  }

  /**
   * creates a SWSignalHead from a JDOM element.  This is an
   * illegal configuration.
   * 
   * @param e is the Element that describes the SWSignalHead
   * @param o is unknown.
   */
  public void load(Element e, Object o) throws Exception {
    log.error("Invalid method called");
  }

  /**
   * saves a reference to a SWSignalHead in an XML file.
   * 
   * @param o is the SWSignalHead.
   */
  public Element store(Object o) {
    SWSignalHead p = (SWSignalHead)o;

    Element element = new Element("signalhead");
    element.setAttribute("class", this.getClass().getName());

    // include contents
    element.setAttribute("systemName", p.getSystemName());
    if (p.getUserName() != null) {
      element.setAttribute("userName", p.getUserName());
    }
    return element;
  }
  static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(SWSignalHeadXml.class.getName());
}
/* @(#)SWSignalHeadXml.java */