/* Name: HeadStates.java
 *
 * What:
 *  This class collects all the presentation states and decoder instructions
 *  for creating them, into one data structure.
 */
package cats.layout.items;

import cats.jmri.SWSignalHead;
import cats.jmri.HWSignalHead;
import cats.jmri.JmriPrefixManager;
import cats.layout.FlashRate;
import cats.layout.xml.*;
import java.util.Enumeration;
import java.util.Vector;

import jmri.InstanceManager;
import jmri.SignalHead;

/**
 * is a class for collecting the list of signal head presentation states
 * and instructions into a single data structure.  Objects of this class
 * are intended to be non-dynamic.  They are created, HeadAspects are added
 * to them, but they are not edited.
 * <p>
 * There is one element (an ApsectCommand) for each color
 * or position that the SignalHead can show.
 * @see cats.layout.items.AspectCommand
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A model railroad dispatching program</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */

public class HeadStates extends Vector<AspectCommand>
implements XMLEleObject {
  /**
   * is the name (property tag) for the decoder that turns
   * a signal off.  It must be the same as "off" in the designer
   * AspectDialog.
   */
  public static final String DECODER_DARK = "off";
  
  /**
   * is the tag for identifying the HeadStates element in the XMl file.
   */
  static final String XML_TAG = "HEADSTATES";

  /**
   * is the tag for identifying a JMRI SignalHead system name.
   */
  static final String XML_SIGNALHEAD = "SIGNALHEAD";
 
  /**
   * is the attribute tag identifying the aspect
   * as needing software assistance.
   */
  static final String ASSISTANCE = "ASSISTANCE";

  /**
   * is the SignalHead that implements the HeadStates
   */
  private SignalHead MyHead;
  
  /**
   * is the username of a SignalHead.  This
   * is so that scripts have a constant System Name.
   */
  private String UserName;
  
  /**
   * is true if software handles flashing lights.
   */ 
  private boolean SWFlashing = false;
  
  /**
   * adds a signal presentation String and its commands to the list.
   *
   * @param state is the String an commands.  No checking is done to
   * ensure the String has not already been added.
   */
  public void addElement(AspectCommand state) {
    super.addElement(state);
  }
  
  /**
   * finds the IOInterface for a given presentation label.
   *
   * @param label is the presentation label.
   *
   * @return the IOInterface for that label (if it exists) or null
   * (if it does not exist).
   *
   * @see cats.layout.items.IOInterface
   */
  public IOInterface findDecoders(String label) {
    AspectCommand aState;
    
    for (Enumeration<AspectCommand> e = elements(); e.hasMoreElements(); ) {
      aState = e.nextElement();
      if (aState.getPresentationLabel().equals(label)) {
        return aState.getCommands();
      }
    }
    return null;
  }
  
  /**
   * remembers if software performs the flashing.
   * 
   * @param sw is true if software times flashing.
   */
  public void setAssist(boolean sw) {
    SWFlashing = sw;
    if (SWFlashing) {
      FlashRate.TheFlashRate.turnOn(true);
    }
  }
  
  /**
   * is called to determine if software performs
   * flashing.
   * @return true if software does the timing; false
   * if hardware has internal timing.
   */
  public boolean getAssist() {
    return SWFlashing;
  }

  /**
   * sets the JMRI System Name for a SignalHead
   * 
   * @param name is the System Name by which the
   * SignalHead is known.
   */
  public void setName(String name) {
    UserName = name;
  }
  
  /**
   * returns the SignalHead that implements the HeadStates.  If the user name
   * is referenced in the XML file, the specified SignalHead is returned.  If one was
   * not specified and there are decoder definitions, a custom SignalHead
   * is created, loaded with the definitions, and it is returned.
   * 
   * @param sysName is the name given to the Signal by the user.
   * 
   * @return the SignalHead
   */
  public SignalHead getSignalHead(String sysName) {
    AspectCommand command;
    if ((UserName == null) && (sysName == null)) {
      MyHead = (SWFlashing) ? 
          new SWSignalHead(SWSignalHead.createSignalHeadName()) : 
            new HWSignalHead(SWSignalHead.createSignalHeadName());
          InstanceManager.getDefault(jmri.SignalHeadManager.class).register(MyHead);
    }
    else if (UserName != null) {
      if (sysName == null) {
        if ((MyHead = JmriPrefixManager.findHead(UserName)) == null) {
          MyHead = (SWFlashing) ? 
              new SWSignalHead(SWSignalHead.createSignalHeadName(), UserName):
                new HWSignalHead(SWSignalHead.createSignalHeadName(), UserName);
              InstanceManager.getDefault(jmri.SignalHeadManager.class).register(MyHead);
        }
      }
      else {
        if (((MyHead = JmriPrefixManager.findHead(UserName)) == null) &&
            ((MyHead =JmriPrefixManager.findHead(sysName)) == null)) {
          MyHead = (SWFlashing) ? new SWSignalHead(sysName, UserName):
            new HWSignalHead(sysName, UserName);
          InstanceManager.getDefault(jmri.SignalHeadManager.class).register(MyHead);              
        }
      }          
    }
    else if ((MyHead = JmriPrefixManager.findHead(sysName)) == null) {
      MyHead = (SWFlashing) ? new SWSignalHead(sysName):
        new HWSignalHead(sysName);
      InstanceManager.getDefault(jmri.SignalHeadManager.class).register(MyHead);            
    }
    
    if ((this.size() != 0) && (MyHead != null) &&
        (SWSignalHead.class.isAssignableFrom(MyHead.getClass()))) {
      for (Enumeration<AspectCommand> e = elements(); e.hasMoreElements(); ) {
        command = e.nextElement();
        ((SWSignalHead) MyHead).addIOSpec(command);
      }
    }
    return MyHead;
  }

  /**
   * returns the unnamed SignalHead that implements the HeadStates.
   * See the parameterized version for details.
   * @return the SignalHead
   */
  public SignalHead getSignalHead() {
    return getSignalHead(SWSignalHead.createSignalHeadName());
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
    return new String("A " + XML_TAG + " cannot contain a text field ("
        + eleValue + ").");
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
    if (AspectCommand.XML_TAG.equals(objName)) {
      addElement((AspectCommand) objValue);
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
    return null;
  }
  
  /**
   * registers a HeadStatesFactory with the XMLReader.
   */
  static public void init() {
    XMLReader.registerFactory(XML_TAG, new HeadStatesFactory());
    AspectCommand.init();
  }
}

/**
 * is a Class known only to the HeadStates class for creating HeadStates from
 * an XML document.  Its purpose is to accumulate all the AspectCommands for
 * the signal head.
 */
class HeadStatesFactory
implements XMLEleFactory {
  
  /**
   * if false, then no software assistance is needed to
   * present the appearance.  If true, then software
   * is needed for flashing.
   */
  private boolean SWAssist = false;

  /**
   * is the name of a JMRI SignalHead object to use for driving
   * the lights.
   */
  private String JMRIUserName;

  /*
   * tells the factory that an XMLEleObject is to be created.  Thus,
   * its contents can be set from the information in an XML Element
   * description.
   */
  public void newElement() {
    SWAssist = false;
    JMRIUserName = null;
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
    
    if (HeadStates.ASSISTANCE.equals(tag)) {
      SWAssist = true;
    }
    else if (HeadStates.XML_SIGNALHEAD.equals(tag)) {
      JMRIUserName = value;
    }
    else {
      resultMsg = new String("A " + HeadStates.XML_TAG +
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
    HeadStates hs = new HeadStates();
    if (JMRIUserName != null) {
      hs.setName(JMRIUserName);
    }
    hs.setAssist(SWAssist);
    return hs;
  }
}
/* @(#)HeadStates.java */