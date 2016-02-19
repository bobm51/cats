package cats.jmri.configurexml;

import jmri.configurexml.JmriConfigureXmlException;
import cats.jmri.MeterLnSensorManager;
import org.jdom2.Element;

/**
 * Provides load and store functionality for
 * configuring MeterLnSensorManagers.
 * <P>
 * Uses the store method from the abstract base class, but
 * provides a load method here.
 * <p>
 * Much of this is derived from LnSensorManager.
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
public class MeterLnSensorManagerXml extends jmri.managers.configurexml.AbstractSensorManagerConfigXML {

    /**
     * is the constructor
     */
    public MeterLnSensorManagerXml() {
        super();
    }

    public void setStoreElementClass(Element sensors) {
        sensors.setAttribute("class",this.getClass().getName());
//      JDOMfixer.addsetAttribute(sensors, "class",this.getClass().getName());
    }

    public void load(Element element, Object o) {
        log.error("Invalid method called");
    }

    public boolean load(Element sensors) throws JmriConfigureXmlException {
        // create the master object
        MeterLnSensorManager mgr = MeterLnSensorManager.instance();
        // load individual sensors
         try {
            loadSensors(sensors);
         } catch (jmri.configurexml.JmriConfigureXmlException jcxe){
 	  log.warn("error while loading sensors");
         }

        // Request the status of these sensors from the layout, if appropriate.
        mgr.updateAll();
        return true;
    }

    static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(MeterLnSensorManagerXml.class.getName());

	@Override
	public boolean load(Element sharedSensors, Element perNodeSensors)
			throws JmriConfigureXmlException {
		return this.load(sharedSensors);
	}
}
/* @(#)MeterLnSensorManagerXml.java */
