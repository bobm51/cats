/**
 * Name: HWSignalHeadXml
 *
 * What:
 * Provides load and store functionality for
 * configuring HWSignalHeads.
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

import jmri.InstanceManager;
import jmri.SignalHead;
import jmri.configurexml.JmriConfigureXmlException;

import org.jdom2.Attribute;
import org.jdom2.Element;

import cats.jmri.HWSignalHead;
import cats.jmri.SWSignalHead;

/**
 * Provides load and store functionality for
 * configuring HWSignalHeads.  This method facilitates storing
 * HWSignalHead references through saving a JMRI Signal Table.
 * Once saved, it can be loaded by loading a JMRI panel.  The
 * HWSignalHead thus loaded will be empty, but scripts will
 * be able to access it.
 * <P>
 * Modeled after SE8cSignalHeadXml
 *
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2004, 2009, 2016</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */

public class HWSignalHeadXml extends jmri.managers.configurexml.AbstractNamedBeanManagerConfigXML {

  /**
   * creates an HWSignalHead from a JDOM Element.
   * 
   * @param e is the Element that describes the HWSignalHead
   */
  public boolean load(Element e) throws JmriConfigureXmlException {
    Attribute u = e.getAttribute("userName");
    Attribute s = e.getAttribute("systemName");
    SignalHead h;
    if (s == null) {
      if (u == null) {
        h = new HWSignalHead(SWSignalHead.createSignalHeadName());
      }
      else {
        h = new HWSignalHead(SWSignalHead.createSignalHeadName(),
            u.getValue());        
      }
    }
    else {
      if (u == null) {
        h = new HWSignalHead(s.getValue());
      }
      else {
        h = new HWSignalHead(s.getValue(), u.getValue());        
      }
    }
    InstanceManager.getDefault(jmri.SignalHeadManager.class).register(h);
    return true;
  }

//  /**
//   * creates an HWSignalHead from a JDOM element.  This is an
//   * illegal configuration.
//   * 
//   * @param e is the Element that describes the HWSignalHead
//   * @param o is unknown.
//   */
//  public void load(Element e, Object o) throws Exception {
//    log.error("Invalid method called");
//  }

  /**
   * saves a reference to an HWSignalHead in an XML file.
   * 
   * @param o is the HWSignalHead.
   */
  public Element store(Object o) {
	    HWSignalHead p = (HWSignalHead)o;

	    Element element = new Element("signalhead");
	    element.setAttribute("class", this.getClass().getName());

//	    // include contents
//	    element.setAttribute("systemName", p.getSystemName());
//	    if (p.getUserName() != null) {
//	      element.setAttribute("userName", p.getUserName());
//	    }
	    // include contents
	    element.addContent(new Element("systemName").addContent(p.getSystemName()));

	    storeCommon(p, element);

	    return element;
  }
  static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(HWSignalHeadXml.class.getName());
  
@Override
public void load(org.jdom2.Element e, Object o) throws JmriConfigureXmlException {
	this.load(e);
}

}
/* @(#)HWSignalHeadXml.java */