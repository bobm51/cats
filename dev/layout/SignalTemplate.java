/*
 * Name: SignalTemplate.java
 *
 * What:
 *  This class contains the information about the physical characteristics
 *  of a signal - number of heads, possible states of each head, and so on.
 */
package cats.layout;

import cats.layout.xml.*;

/**
 *  This class contains the information about the physical characteristics
 *  of a signal - number of heads, possible states of each head, and so on.
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */

public class SignalTemplate
    implements XMLEleObject {

  /**
   * is the tag for indentifying a SignalTemplate in the XMl file.
   */
  static final String XML_TAG = "SIGNALTEMPLATE";

  /**
   * is the attribute tag for the number of heads in the XML file.
   */
  static final String TEMPLATEHEADS = "TEMPLATEHEADS";

  /**
   * is the kind of signal in the template.
   */
  static final String TEMPLATEKIND = "TEMPLATEKIND";

  /**
   * is the name of the template.
   */
 static final String TEMPLATENAME = "TEMPLATENAME";

  /**
   * is the value of TEMPLATEKIND for a lamp.
   */
  static final String LAMPKIND = "Lamp";

  /**
   * is the value of TEMPLATEKIND for a Semaphore.
   */
  static final String SEMKIND = "Semaphore";

  /**
   * the name of the SignalType.
   */
  private String TypeName;

  /**
   * the number of heads in the signal.
   */
  private int SignalHeads;

  /**
   * a true flag means the heads are lights.  A false flag means they are
   * semaphores.  This method is not really needed for
   * operating the layout.  However, it makes the screen presentation nicer
   * because a different icon can be used for semaphores as for lights.  Note
   * that all heads must be of the same type.
   */
  private boolean IsLights;

  /**
   * Each head has a list of Labels for identifying the states the head
   * can assume.  A state can be
   * <ul>
   * <li>
   *     a color (such as red, yellow, green)
   * <li>
   *     a dynamic color (such as flashing yellow), as long as the signal
   *     driver can accept a command that will handle the dynamic nature.
   *     The dispatcher panel does not perform low level timing.
   * <li>
   *     a semaphore position
   * </ul>
   * This program (and the dispatcher panel) makes no interpretation of
   * the state label.  The state label provides a bridge between the
   * aspect map and the signal driver.
   */

  /**
   * the table that maps indications to aspects.  The entries in the
   * map are entries in the state table.
   */
  private AspectMap AspectTbl;

  /**
   * the SignalTemplate constructor.
   *
   * @param heads is the number of heads (1 - 3)
   * @param lamp is true for lamp and false for semaphore.
   * @param name is the name (must not be blank)
   */
  public SignalTemplate(int heads, boolean lamp, String name) {
    SignalHeads = heads;
    IsLights = lamp;
    TypeName = new String(name);
  }

  /**
   * returns the name of the template.
   *
   * @return the template's name.
   */
  public String getName() {
    return new String(TypeName);
  }

  /**
   * returns the number of signal heads.
   *
   * @return the number of heads.
   */
  public int getNumHeads() {
    return SignalHeads;
  }

  /**
   * returns the kind of Signal.
   *
   * @return true is the Signal is composed of lights or false if it is
   * composed of semaphore blades.
   */
  public boolean isLights() {
    return IsLights;
  }

  /**
   * replaces the AspectMap.
   *
   * @param map is the new AspectMap.
   *
   * @see AspectMap
   */
//  public void setAspectMap(AspectMap map) {
//    AspectTbl = map;
//  }

  /**
   * retrieves the AspectMap.
   *
   * @return the signal presentation for each Indication.
   *
   * @see AspectMap
   */
  public AspectMap getAspectMap() {
    return AspectTbl;
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
    if (AspectMap.XML_TAG.equals(objName)) {
      AspectTbl = (AspectMap) objValue;
      return null;
    }
    return new String("A " + XML_TAG + " cannot contain an Element ("
                      + objName + ").");
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
   * registers a SignalTemplateFactory with the XMLReader.
   */
  static public void init() {
    XMLReader.registerFactory(XML_TAG, new SignalTemplateFactory());
    AspectMap.init();
  }
}

/**
 * is a Class known only to the SignalTemplate class for creating
 * SignalTemplates from an XML document.
 */
class SignalTemplateFactory
    implements XMLEleFactory {

  private String tempName;
  private boolean lamp;
  private int heads;

  /*
   * tells the factory that an XMLEleObject is to be created.  Thus,
   * its contents can be set from the information in an XML Element
   * description.
   */
  public void newElement() {
    tempName = null;
    lamp = true;
    heads = 1;
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

    if (SignalTemplate.TEMPLATEKIND.equals(tag)) {
      if (SignalTemplate.LAMPKIND.equals(value)) {
        lamp = true;
      }
      else if (SignalTemplate.SEMKIND.equals(value)) {
        lamp = false;
      }
      else {
        resultMsg = new String("A " + value + " is not a " +
                               SignalTemplate.XML_TAG + "XML " +
                               SignalTemplate.TEMPLATEKIND + " attribute.");
      }
    }
    else if (SignalTemplate.TEMPLATEHEADS.equals(tag))
    {
      try {
        heads = Integer.parseInt(value);
        if ((heads < 1) || (heads > 3)) {
          heads = 1;
          resultMsg = new String(value + ": Illegal value for " +
                                 SignalTemplate.XML_TAG + "XML " +
                                 SignalTemplate.TEMPLATEKIND + " attribute.");
        }
      }
      catch (NumberFormatException nfe) {
        resultMsg = new String(value + ": Illegal " +
                               SignalTemplate.XML_TAG + "XML " +
                               SignalTemplate.TEMPLATEKIND + " attribute.");
      }
    }
    else if (SignalTemplate.TEMPLATENAME.equals(tag)) {
      if ((value == null) || (value.trim().length() == 0)) {
        resultMsg = new String("A " + SignalTemplate.XML_TAG +
                               " XML " + SignalTemplate.TEMPLATENAME +
                               " attribute can not be blank.");
      }
      else {
        tempName = new String(value);
      }
    }
    else {
      resultMsg = new String("A " + SignalTemplate.XML_TAG +
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
    SignalTemplate st;
    if (tempName == null) {
      System.out.println("Missing " + SignalTemplate.XML_TAG + " " +
                         SignalTemplate.TEMPLATENAME + " attribute.");
      return null;
    }
    st = new SignalTemplate(heads, lamp, tempName);
    TemplateStore.SignalKeeper.add(st);
    return st;
  }
}
