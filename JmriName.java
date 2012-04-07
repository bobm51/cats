/**
 * Name: JmriName.java
 *
 * What:
 *  This is a class for holding the list of Names defined for
 *  JMRI.
 * 
 */
package cats.jmri;

import java.util.ArrayList;
import java.util.Iterator;
import cats.layout.xml.XMLEleFactory;
import cats.layout.xml.XMLEleObject;
import cats.layout.xml.XMLReader;

/**
 *  This is a class for holding the list of Names defined for
 *  JMRI.  The bad part is that the contents of this file are
 *  tightly coupled to JMRI in 2 ways - the 2 character name
 *  prefixes and the structure of the JMRI classes as to where
 *  the managers reside.  But, we can be abstract for only
 *  so long before we have to do something concrete.
 *  <p>
 *  The good part is that this is quite flexible.  Any part of
 *  any item (prefix, class, or type) can be redefined, which
 *  means it can be easily replace without changing the code.
 *  However, if (when) another type is added to JMRI, this code
 *  will have to be modified.
 *  <p>
 *  The Digitrax SE8C ("LH") is not defined here because I could
 *  find no manager to invoke to create one.
 * <p>Title: designer</p>
 * <p>Description: A program for designing dispatcher panels</p>
 * <p>Copyright: Copyright (c) 2006, 2009</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */
public class JmriName implements XMLEleObject {
    /**
     * is the tag for identifying a Jmri Name in the XML file.
     */
    static final String XML_TAG = "JMRINAME";
    
    /**
     * is the XML tag for the 2 character name attribute.
     */
    static final String XML_PREFIX = "JMRIPREFIX";
    
    /**
     * is the JMRI type for objects with Prefix.
     */
    static final String XML_TYPE = "XMLTYPE";

    /**
     * are constants for defining the types.  If anything
     * is added here, be sure to change IOSPec.doneXML
     */
    /**
     * tag for CATS decoder chain
     */
    public static final String CHAIN = "Decoder Chain";
    
    /**
     * tag for JMRI SignalHead
     */
    public static final String HEAD = "Signal Head";
    
    /**
     * tag for JMRI Light
     */
    public static final String LIGHT = "Light";
    
    /**
     * tag for JMRI Memory Recorder
     */
    public static final String MEMORY = "Memory";
    
    /**
     * tag for JMRI Power Manager
     */
    public static final String POWER = "Power Manager";
    
    /**
     * tag for JMRI Reporter
     */
    public static final String REPORTER = "Reporter";
    
    /**
     * tag for JMRI Route
     */
    public static final String ROUTE = "Route";
    
    /**
     * tag for JMRI Sensor
     */
    public static final String SENSOR = "Sensor";
    
    /**
     * tag for JMRI Turnout
     */
    public static final String TURNOUT = "Turnout";

    /**
     * is the list of JmriNames
     */
    private static ArrayList<JmriName> JmriNameList;
    
    /**
     * is the JMRI Name.  This is 2 characters long, following
     * JMRI naming convention.
     */
    private String MyName;
    
    /**
     * is the object class that the name refers to.
     */
    private String MyClass;
    
    /**
     * locates the class manager.
     */
    private String MyType;
    
    /**
     * is the ctor.
     * 
     * @param name is the two character JMRI name.
     * @param c is the JMRI class for objects with the JMRI name
     * @param t is the JMRI type of the name.
     */
    public JmriName(String name, String c, String t) {
      MyName = new String(name);
      if (c != null) {
          MyClass = new String(c);
      }
      if (t != null) {
          MyType = new String(t);
      }
    }
    
    /**
     * retrieves the 2 character identifier.
     * @return the JMRI prefix
     */
//    public String getJmriID() {
//        return new String(MyName);
//    }
    
    /**
     * retrieves the fully qualified Jmri class name
     * @return where in the jar file to find the class
     * definition of the manager for the JMRI object.
     */
    public String getJmriClass() {
      if (MyClass != null) {
        return new String(MyClass);
      }
      return null;
    }
    
    /**
     * sets the class name of the JMRI manager for objects of
     * this decoder.
     * @param c is the JMRI (CATS) class name.
     */
//    public void setJmriClass(String c) {
//        if (c != null) {
//            MyClass = new String(c);
//        }
//        else {
//            MyClass = null;
//        }
//    }
    
    /**
     * retrieves the JMRI type definition.  See DefinedTypes.
     * @return the JMRI type that this prefix represents.
     */
    public String getJmriType() {
        return new String(MyType);
    }
        
    /**
     * is a predicate for comparing two JmriNames.  If the 
     * 2 character identifiers match, then they are
     * presumed to be the same.  This is different than in
     * designer because the 2 character identifier is the
     * search key and they must be unique.
     * 
     * @param candidate is a JmriName being compared to.
     * @return true if the 2 character identifier matches.
     */
    private boolean matches(JmriName candidate) {
        candidate.MyName.toUpperCase();
        return (MyName.equals(candidate.MyName));
    }
    
    /**
     * adds or replaces a prefix.  First, if the prefix is not found,
     * it is automatically added to the handler table.  If a prefix is
     * found, then the classes are compared.  If they are the same,
     * then CATS wants to use a standard JMRI handler, so the class
     * is nulled out (CATS assumes that it has been configured to
     * use the JMRI handler; thus, should not load one).  If they are
     * not the same, the new replaces the old (providing a way to
     * use new JMRI hanlders or redefine the prefixes without
     * chnging CATS).
     * 
     * @param newPrefix is the new/replacement prefix
     * definition.
     */
    public static void addPrefix(JmriName newPrefix) {
        JmriName existing;
        for (Iterator<JmriName> iter = JmriNameList.iterator(); iter.hasNext(); ) {
            existing = iter.next();
            if (existing.matches(newPrefix)) {
              if ((newPrefix.MyClass != null) && (newPrefix.MyClass.equals(existing.MyClass))) {
                existing.MyClass = null;
              }
              else {
                existing.MyClass = newPrefix.MyClass;
              }
              return;
            }
        }
        JmriNameList.add(newPrefix);
    }

    /**
     * searches the list of JMRI names for one with a matching
     * 2 character identifier.
     * @param prefix is the 2 character identifier being searched
     * for
     * @return the first JMRI name that matches or null.
     */
    public static JmriName getName(String prefix) {
      JmriName name;
      prefix.toUpperCase();
      for (Iterator<JmriName> iter = JmriNameList.iterator(); iter.hasNext(); ) {
        name = iter.next();
        if (name.MyName.equals(prefix)) {
          return name;
        }
      }
      return null;
    }

    /**
     * searches the list of JMRI names for one whose prefix manages
     * a particular class of JMRI objects
     * @param manager is the name of the manager class
     * @return  the prefix managed by the manager class name.  If there
     * is more than one, the ealiest found is returned.
     */
//    public static String getPrefix(String manager) {
//      JmriName name;
//      for (Iterator iter = JmriNameList.iterator(); iter.hasNext(); ) {
//        name = (JmriName) iter.next();
//        if (name.MyClass.equals(manager)) {
//          return name.MyClass;
//        }
//      }
//      return null;
//    }

    /**
     * resets the list of prefixes.
     */
    public static void reset() {
        JmriNameList = new ArrayList<JmriName>();
        JmriNameList.add(new JmriName("AL", "jmri.jmrix.acela.AcelaLightManager", LIGHT));
        JmriNameList.add(new JmriName("AS", "jmri.jmrix.acela.AcelaSensorManager", SENSOR));
        JmriNameList.add(new JmriName("AT", "jmri.jmrix.acela.AcelaTurnoutManager", TURNOUT));
        JmriNameList.add(new JmriName("CL", "jmri.jmrix.cmri.serial.SerialLightManager", LIGHT));
        JmriNameList.add(new JmriName("CS", "jmri.jmrix.cmri.serial.SerialSensorManager", SENSOR));
        JmriNameList.add(new JmriName("CT", "jmri.jmrix.cmri.serial.SerialTurnoutManager", TURNOUT));
        JmriNameList.add(new JmriName("DT", "jmri.jmrix.srcp.SRCPTurnoutManager", TURNOUT));
        JmriNameList.add(new JmriName("ET", "jmri.jmrix.easydcc.EasyDccTurnoutManager", TURNOUT));
        JmriNameList.add(new JmriName("GH", "jmri.jmrix.grapevine.SerialSignalHead", HEAD));
        JmriNameList.add(new JmriName("GL", "jmri.jmrix.grapevine.SerialLightManager", LIGHT));
        JmriNameList.add(new JmriName("GS", "jmri.jmrix.grapevine.SerialSensorManager", SENSOR));
        JmriNameList.add(new JmriName("GT", "jmri.jmrix.grapevine.SerialTurnoutManager", TURNOUT));
//        JmriNameList.add(new JmriName("IC", "cats.layout.items.IOSpecChain", CHAIN));
//        JmriNameList.add(new JmriName("IH", "cats.jmri.CustomSignalHead", HEAD));
        JmriNameList.add(new JmriName("IM", "jmri.MemoryManager", MEMORY));
        JmriNameList.add(new JmriName("IR", "jmri.ReporterManager", REPORTER));
        JmriNameList.add(new JmriName("IR", "jmri.RouteManager", ROUTE));
        JmriNameList.add(new JmriName("IS", "jmri.managers.InternalSensorManager", SENSOR));
        JmriNameList.add(new JmriName("IT", "jmri.managers.InternalTurnoutManager", TURNOUT));
        JmriNameList.add(new JmriName("LH", "jmri.jmrix.loconet.SE8cSignalHead", HEAD));
        JmriNameList.add(new JmriName("LL", "jmri.jmrix.loconet.LnLightManager", LIGHT));
        JmriNameList.add(new JmriName("LR", "jmri.jmrix.loconet.LnReporterManager", REPORTER));
        JmriNameList.add(new JmriName("LS", "jmri.jmrix.loconet.LnSensorManager", SENSOR));
        JmriNameList.add(new JmriName("LT", "jmri.jmrix.loconet.LnTurnoutManager", TURNOUT));
        JmriNameList.add(new JmriName("KL", "jmri.jmrix.maple.SerialLightManager", LIGHT));
        JmriNameList.add(new JmriName("CS", "jmri.jmrix.maple.SerialSensorManager", SENSOR));
        JmriNameList.add(new JmriName("KT", "jmri.jmrix.maple.SerialTurnoutManager", TURNOUT));
//        JmriNameList.add(new JmriName("ML", "cats.jmri.MeterLnLightManager", LIGHT));
//        JmriNameList.add(new JmriName("MR", "cats.jmri.MeterLnReporterManager", REPORTER));
//        JmriNameList.add(new JmriName("MS", "cats.jmri.MeterLnSensorManager", SENSOR));
//        JmriNameList.add(new JmriName("MS", "jmri.jmrix.can.cbus.CbusSensorManager", SENSOR));
        JmriNameList.add(new JmriName("MT", "jmri.jmrix.can.cbus.CbusTurnoutManager", TURNOUT));
        JmriNameList.add(new JmriName("NS", "jmri.jmrix.nce.NceSensorManager", SENSOR));
        JmriNameList.add(new JmriName("NT", "jmri.jmrix.nce.NceTurnoutManager", TURNOUT));
        JmriNameList.add(new JmriName("OL", "jmri.jmrix.oaktree.SerialLightManager", LIGHT));
        JmriNameList.add(new JmriName("OS", "jmri.jmrix.oaktree.SerialSensorManager", SENSOR));
        JmriNameList.add(new JmriName("OT", "jmri.jmrix.oaktree.SerialTurnoutManager", TURNOUT));
        JmriNameList.add(new JmriName("PL", "jmri.jmrix.powerline.SerialLightManager", LIGHT));
        JmriNameList.add(new JmriName("PS", "jmri.jmrix.powerline.SerialSensorManager", SENSOR));
        JmriNameList.add(new JmriName("PT", "jmri.jmrix.powerline.SerialTurnoutManager", TURNOUT));
        JmriNameList.add(new JmriName("PT", "jmri.jmrix.xpa.XpaTurnoutManager", TURNOUT));
        JmriNameList.add(new JmriName("RR", "jmri.jmrix.rps.RpsReporterManager", REPORTER));
        JmriNameList.add(new JmriName("RS", "jmri.jmrix.rps.RpsSensorManager", SENSOR));
        JmriNameList.add(new JmriName("ST", "jmri.jmrix.sprog.SprogTurnoutManager", TURNOUT));
        JmriNameList.add(new JmriName("TT", "jmri.jmrix.tmcc.SerialTurnoutManager", TURNOUT));
        JmriNameList.add(new JmriName("UT", "jmri.jmrix.ecos.EcosTurnoutManager", TURNOUT));
        JmriNameList.add(new JmriName("VL", "jmri.jmrix.secsi.SerialLightManager", LIGHT));
        JmriNameList.add(new JmriName("VS", "jmri.jmrix.secsi.SerialSensorManager", SENSOR));
        JmriNameList.add(new JmriName("VT", "jmri.jmrix.secsi.SerialTurnoutManager", TURNOUT));
        JmriNameList.add(new JmriName("XL", "jmri.jmrix.lenz.XNetLightManager", LIGHT));
        JmriNameList.add(new JmriName("XS", "jmri.jmrix.lenz.XNetSensorManager", SENSOR));
        JmriNameList.add(new JmriName("XT", "jmri.jmrix.lenz.XNetTurnoutManager", TURNOUT));
        JmriNameList.add(new JmriName("XT", "jmri.jmrix.xpa.XpaTurnoutManager", TURNOUT));
    }
    
    /**
     * is the method through which the object receives the text field.
     *
     * @param eleValue is the Text for the Element's value.
     *
     * @return if the value is acceptable, then null; otherwise, an error
     * string.
     */
    public String setValue(String eleValue) {
        MyClass = new String(eleValue);
        return null;
    }
    
    /**
     * is the method through which the object receives embedded Objects.
     *
     * @param objName is the name of the embedded object
     * @param objValue is the value of the embedded object
     *
     * @return null if the Object is acceptible or an error String
     * if it is not.
     */
    public String setObject(String objName, Object objValue) {
        return new String("A " + XML_TAG + " cannot contain an Element ("
                + objName + ").");
        
    }
    
    /**
     * returns the XML Element tag for the XMLEleObject.
     *
     * @return the name by which XMLReader knows the XMLEleObject (the
     * Element tag).
     */
    public String getTag() {
        return new String(XML_TAG);
    }
    
    /**
     * tells the XMLEleObject that no more setValue or setObject calls will
     * be made; thus, it can do any error checking that it needs.
     *
     * @return null, if it has received everything it needs or an error
     * string if something isn't correct.
     */
    public String doneXML() {
        String result = null;
        if (MyName == null){
            result = new String("A JMRI Name is missing the 2 character prefix.");
        }
        else if (MyClass == null) {
            result = new String("JMRI Name " + MyName + " is missing its class definition");
        }
        else if (MyType == null) {
            result =new String("JMRI Name " + MyName + " is missing its JMRI type definition");
        }
        else {
            addPrefix(this);
        }
        return result;
    }
    
    /**
     * registers a JmriNameFactory with the XMLReader.
     */
    static public void init() {
        XMLReader.registerFactory(XML_TAG, new JmriNameFactory());
        reset();
    }
}

/**
 * is a Class known only to the JmriName class for creating PhysicalSignals from
 * an XML document.  Its purpose is to pick up the location of the SecSignal
 * in the GridTile, its orientation, and physical attributes on the layout.
 */
class JmriNameFactory
implements XMLEleFactory {
    
    /**
     * is the 2 character JMRI Name prefix.
     */
    private String Prefix;
    
    /**
     * is the type of object identified by Prefix.
     */
    private String JMRIType;
    
    /*
     * tells the factory that an XMLEleObject is to be created.  Thus,
     * its contents can be set from the information in an XML Element
     * description.
     */
    public void newElement() {
        Prefix = "";
        JMRIType = "";
    }
    
    /*
     * gives the factory an initialization value for the created XMLEleObject.
     *
     * @param tag is the name of the attribute.
     * @param value is it value.
     *
     * @return null if the tag:value are accepted; otherwise, an error
     * string.
     */
    public String addAttribute(String tag, String value) {
        String resultMsg = null;
        
        if (JmriName.XML_PREFIX.equals(tag)) {
            Prefix = new String(value);
        }
        else if (JmriName.XML_TYPE.equals(tag)) {
            JMRIType = new String(value);
        }
        else {
            resultMsg = new String("A " + JmriName.XML_TAG +
                    " XML Element cannot have a " + tag +
            " attribute.");
        }
        return resultMsg;
    }
    
    /*
     * tells the factory that the attributes have been seen; therefore,
     * return the XMLEleObject created.
     *
     * @return the newly created XMLEleObject or null (if there was a problem
     * in creating it).
     */
    public XMLEleObject getObject() {
        return new JmriName(Prefix, null, JMRIType);
    }
}
/* @(#)JmriName.java */
