/* Name: Hours.java
 *
 * What;
 *  Hours is the number of hours a crew may work before they go
 *  "dead on the law".
 */
package cats.layout;

import cats.gui.store.TimeSpec;
import cats.layout.xml.XMLEleFactory;
import cats.layout.xml.XMLEleObject;
import cats.layout.xml.XMLReader;

/**
 *  Hours is the number of hours a crew may work before they go
 *  "dead on the law".
 *
 * <p>Title: designer</p>
 * <p>Description: A program for designing dispatcher panels</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */

public class Hours
    implements XMLEleObject {

  /**
   * is the tag for identifying a FieldInfo Object in the XMl file.
   */
  static final String XML_TAG = "HOURSLIMIT";

  /**
   * is the hours the crew may work as a String formatted like HH:mm.
   */
  static private int LegalHours;

  /**
   * is the class reference for finding the Hours.
   */
  public static Hours HourStore;

  /**
   * the constructor.
   *
   */
  public Hours() {
    LegalHours = TimeSpec.UNKNOWN_TIME;
    init();
  }

  /**
   * is called to set the Hours.
   *
   * @param hours is the new value.  It is either an empty String or
   * formatted as HH:mm.
   */
  public static void setHours(String hours) {
    if (hours.trim().length() == 0){
      LegalHours = TimeSpec.UNKNOWN_TIME;
    }
    else {
      LegalHours = TimeSpec.convertString(hours, TimeSpec.MIDNIGHT);
    }
  }

  /**
   * retrives the hours the crew may work.
   *
   * @return the number of minutes the crew may work.  If
   */
  public static int getHours() {
    return LegalHours;
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
    LegalHours = TimeSpec.convertString(eleValue, TimeSpec.MIDNIGHT);
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
    String resultMsg = new String("A " + XML_TAG + " cannot contain an Element ("
                           + objName + ").");
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
   * registers an HoursFactory with the XMLReader.
   */
  static public void init() {
    XMLReader.registerFactory(XML_TAG, new HoursFactory());
  }
}

/**
 * is a Class known only to the Hours class for creating Hours
 * from an XML document.
 */
class HoursFactory
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

      resultMsg = new String("A " + Hours.XML_TAG +
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
    return Hours.HourStore;
  }
}
/* @(#)Hours.java */