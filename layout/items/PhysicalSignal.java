/* Name: PhysicalSignal.java
 *
 * What:
 *   This class is the container for all the information about the physical
 *   signal on the layout.
 */

package cats.layout.items;

import cats.common.Prop;
import cats.gui.TraceFactory;
import cats.gui.TraceFlag;
import cats.layout.xml.*;
import cats.layout.AspectMap;
import cats.layout.TemplateStore;
import java.util.Enumeration;
import java.util.Vector;

import jmri.SignalHead;

/**
 * is a container for all the information about the physical signal on the
 * layout.
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2004, 2009</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */
public class PhysicalSignal
implements XMLEleObject {
  /**
   * is the tag for indentifying a PhysicalSignal in the XMl file.
   */
  static final String XML_TAG = "PHYSIGNAL";
  
  /**
   * is the list of all physical signals.
   */
  private static Vector<PhysicalSignal> SignalKeeper = new Vector<PhysicalSignal>();
  
  /**
   * is where to find the value of the trace flag.
   */
  private static TraceFlag TraceSignal = null;

  /**
   * is a table for translating CATS signal presentation names to JMRI
   * presentation names.
   */
  private static final String AppearanceName[] = {
    "off",
    "green",
    "yellow",
    "red",
    "flashing green",
    "flashing yellow",
    "flashing red",
    "lunar",
    "flashing lunar",
    "horizontal",
    "diagonal",
    "vertical"
  };
  
  /**
   * is the JMRI presentation value (@see jmri.SignalHead.java) that
   * corresponds to each String.
   */
  private static final int JMRIAppearance[] = {
    SignalHead.DARK,
    SignalHead.GREEN,
    SignalHead.YELLOW,
    SignalHead.RED,
    SignalHead.FLASHGREEN,
    SignalHead.FLASHYELLOW,
    SignalHead.FLASHRED,
    SignalHead.LUNAR,
    SignalHead.FLASHLUNAR,
    SignalHead.RED,
    SignalHead.YELLOW,
    SignalHead.GREEN
  };

  /**
   * is a marker for meaning that CATS has not defined an appearance
   * with the name.
   */
  public static final int NO_SUCH_APPEARANCE = 0xff;
  
  /**
   * is (temporarily) the number of heads in the Signal.  This field
   * should come from the Signal Type object.
   */
  private int Heads = 1;
  
  /**
   * is the Name of the SignalTemplate being described.
   */
  private String TemplateName;
  
  /**
   * is the commands to the decoder.
   */
  private AspectTable DecoderList;
  
  /**
   * describes which Aspects are supported.
   */
  private AspectMap MyMap;
  
  /**
   * is the Rule that the last commands were sent for.
   */
  private int LastRule = -1;
  
  /**
   * is a flag set to true when in test mode.
   */
  private boolean SignalTest = false;

  /**
   * is a flag set to true when the Signal is lit.
   */
  private boolean SignalLit = true;
  
  /**
   * is the name of the containing signal.
   */
  private String SignalName = "";
  
  /**
   * constructs a SecSignal, given no other information
   */
  public PhysicalSignal() {
    SignalKeeper.add(this);
  }
  
  /**
   * retrieves the name of the SignalTemplate.
   *
   * @return the name of the SignalTemplate so that it can be located
   * in the TemplateStore.
   */
  public String getTemplateName() {
    return new String(TemplateName);
  }
  
  /**
   * sets the name of the SignalTemplate.
   *
   * @param template is the name of the SignalTemplate
   */
  public void setTemplateName(String template) {
    TemplateName = template;
    MyMap = TemplateStore.SignalKeeper.find(TemplateName).getAspectMap();
  }
  
  /**
   * sets the decoder command list.
   *
   * @param dlist is the new list.
   *
   * @see AspectTable
   */
  public void setCommandTable(AspectTable dlist) {
    DecoderList = dlist;
  }
  
  /**
   * changes the Signal on the layout.  Note: this routine needs to be
   * more robust.  It assumes that an aspect has been defined for each
   * indication that the signal will be asked to display.
   *
   * @param indication is the message to be conveyed by the Signal.
   */
  void setAspect(Indication indication) {
    int rule = indication.getIndication(); // This is the Indication that we will use.

    if (MyMap.getAdvanced()) {
      rule = indication.getAdvancedIndication();
    }
    
    // Just in case an indication is not defined, find one more restrictive
    // that is defined
    for (; MyMap.getPresentation(rule, 0) == null; ++rule) {
    }
    if (!SignalTest && (rule != LastRule)) {
      sendSignalCommands(rule);
      LastRule = rule;
    }
  }
  
  /**
   * sends the commands to the PhysicalSignal
   *
   * @param rule is the signal rule (aspect) being shown
   *
   * @see cats.layout.AspectMap
   */
  private void sendSignalCommands(int rule) {
    String state;
    SignalHead sh;
    String msg;
    if (DecoderList != null) {
      for (int head = 0; head < Heads; ++head) {
        state = MyMap.getPresentation(rule, head);
        sh = DecoderList.getHead(head);
        if (sh == null) {
          if ((SignalName != null) && (SignalName.length() > 0)) {
            msg = "Signal " + SignalName ;
          }
          else {
            msg = "PhysicalSignal: a " + TemplateName + " signal";
          }
          log.warn(msg +  " is missing the decoder definitions for "
              + state);
        }
        else {
          int appearance = toJMRI(state);
          if (appearance == NO_SUCH_APPEARANCE) {
            if ((SignalName != null) && (SignalName.length() > 0)) {
              msg = "Signal " + SignalName;
            }
            else {
              msg = "PhysicalSignal: " ;
            }
            log.warn(msg + " does not have a JMRI appearance for " +
                state);
          }
          else {
            sh.setAppearance(appearance);
            if (TraceSignal.getTraceValue()) {
              System.out.println(SignalName + " head " + (head + 1) +
                  " indication is " +
                  AspectMap.IndicationNames[rule][AspectMap.
                                                  LABEL] +
                                                  " color is " + state);
            }
          }
        }
      }
    }
  }
 
  /**
   * turns the signal on or off for approach lighting.
   * 
   * @param lit is true for on and false for off.
   */
  public void lightUpSignal(boolean lit) {
    SignalHead decoders;
    SignalLit = lit;
    if (MyMap.getApproachLight()) {
      if (DecoderList != null) {
        for (int head = 0; head < Heads; ++head) {
          decoders = DecoderList.getHead(head);
          if (decoders != null) {
            decoders.setLit(lit);
          }
        }
      }
    }
  }
  
  /**
   * refreshes the signals on the layout
   */
  public void refresh() {
    LastRule = -1;
    SignalTest = false;
    lightUpSignal(SignalLit);
  }
  
  /**
   * puts the PhysicalSignal in a test mode.  It stays in the test mode
   * until it the refresh() method is invoked.
   *
   * @param testInd is the Indication being tested.
   */
  public void testSignal(int testInd) {
    boolean lit = SignalLit;
    lightUpSignal(true);
    SignalLit = lit;
    sendSignalCommands(testInd);
    SignalTest = true;
  }
  
  /**
   * sets all the heads on a mast to a particular aspect.  If a head does
   * not support that presentation, then the presentation is ignored.
   *
   * @param aspect is the presentation being requested.  It is a string
   * describing the color or oreientation of the signal.
   */
  public void testSignal(String aspect) {
    SignalHead decoders;
    if (DecoderList != null) {
      for (int head = 0; head < Heads; ++head) {
        decoders = DecoderList.getHead(head);
        if (decoders != null) {
          decoders.setLit(true);
          decoders.setAppearance(toJMRI(aspect));
          if (TraceSignal.getTraceValue()) {
            System.out.println(SignalName + ", head " + head +
                ": color is " + aspect);
          }
        }
      }
    }
  }
 
  /**
   * tells the PhysicalSignal the name of the signal.
   * @param name is the name of the signal
   */
  public void setName(String name) {
    SignalName = name;
    if (DecoderList != null) {
      DecoderList.setSignalName(SignalName);
      Heads = DecoderList.getListCount();
    }
  }
  
  /*
   * is the method through which the object receives the text field.
   *
   * @param eleValue is the Text for the Element's value.
   *
   * @return if the value is acceptable, then null; otherwise, an error
   * string.
   */
  public String setValue(String eleValue) {
    TemplateName = new String(eleValue);
    return null;
  }
  
  /*
   * is the method through which the object receives embedded Objects.
   *
   * @param objName is the name of the embedded object
   * @param objValue is the value of the embedded object
   *
   * @return null if the Object is acceptible or an error String
   * if it is not.
   */
  public String setObject(String objName, Object objValue) {
    String resultMsg = null;
    if (AspectTable.XML_TAG.equals(objName)) {
      setCommandTable( (AspectTable) objValue);
    }
    else {
      resultMsg = new String("A " + XML_TAG + " cannot contain an Element ("
          + objName + ").");
    }
    return resultMsg;
  }
  
  /*
   * returns the XML Element tag for the XMLEleObject.
   *
   * @return the name by which XMLReader knows the XMLEleObject (the
   * Element tag).
   */
  public String getTag() {
    return new String(XML_TAG);
  }
  
  /*
   * tells the XMLEleObject that no more setValue or setObject calls will
   * be made; thus, it can do any error chacking that it needs.
   *
   * @return null, if it has received everything it needs or an error
   * string if something isn't correct.
   */
  public String doneXML() {
    if (TemplateName != null) {
      setTemplateName(TemplateName);
    }
    return null;
  }
  
  /**
   * registers a PhySignalFactory with the XMLReader.
   */
  static public void init() {
    XMLReader.registerFactory(XML_TAG, new PhySignalFactory());
    AspectTable.init();
    TraceSignal = TraceFactory.Tracer.createTraceItem("Signal Changes",
        "SIG_TRACE");
    
  }
  
  /**
   * puts all signals in a test mode until the panel is refreshed.
   *
   * @param testMode is the Indication rule being tested.
   */
  public static void signalTest(int testMode) {
    int count = 0;
    for (Enumeration<PhysicalSignal> e = SignalKeeper.elements(); e.hasMoreElements(); ) {
      e.nextElement().testSignal(testMode);
      if ( (++count) == 10) {
        count = 0;
        
        // Sleep a while so the serial port is not overwhelmed.
        try {
          Thread.sleep(5);
        }
        catch (InterruptedException ie) {
          break;
        }
      }
    }
  }
  
  /**
   * puts all the signals in a test mode ubtil the panel is refreshed.
   *
   * @param testMode is the color or orientation being tested.
   */
  public static void signalTest(String testMode) {
    int count = 0;
    for (Enumeration<PhysicalSignal> e = SignalKeeper.elements(); e.hasMoreElements(); ) {
      e.nextElement().testSignal(testMode);
      if ( (++count) == 10) {
        count = 0;
        
        // Sleep a while so the serial port is not overwhelmed.
        try {
          Thread.sleep(5);
        }
        catch (InterruptedException ie) {
          break;
        }
      }
    }
  }
  
/**
 * translates a CATS appearance String to a JMRI appearance constant
 * @param appearance is the name of the appearance in CATS (see AppearanceName)
 * @return the JMRI appearance constant that it corresponds to.  0xff means there
 * is no translation.
 */
  public static int toJMRI(String appearance) {
    int index = Prop.findString(appearance, AppearanceName);
    if (index == Prop.NOT_FOUND) {
      return NO_SUCH_APPEARANCE;
    }
    return JMRIAppearance[index];
  }

  /**
   * searches for a JMRI appearance constant and returns the CATS
   * name for the constant.
   * 
   * @param appearance is the JMRI appearance constant
   * @return the matching CATS name.  A special string is returned
   * if the constant cannot be found.
   */
  public static String toCATS(int appearance) {
    int index =Prop.findInt(appearance, JMRIAppearance);
    if (index == Prop.NOT_FOUND) {
      return "Appearance not found";
    }
    return new String(AppearanceName[index]);
  }
  static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(PhysicalSignal.class.getName());

}

/**
 * is a Class known only to the PhysicalSignal class for creating PhysicalSignals from
 * an XML document.
 */
class PhySignalFactory
implements XMLEleFactory {
  
  /*
   * tells the factory that an XMLEleObject is to be created.  Thus,
   * its contents can be set from the information in an XML Element
   * description.
   */
  public void newElement() {
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
    
    resultMsg = new String("A " + PhysicalSignal.XML_TAG +
        " XML Element cannot have a " + tag +
    " attribute.");
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
    return new PhysicalSignal();
  }
}
/* @(#)PhysicalSignal.java */