/* Name: Detector.java
 *
 * What:
 *  This is a class for holding the information about a track detector.
 **/
package cats.layout.items;

import cats.layout.items.IOSpec;
import cats.layout.xml.*;

/**
 * holds the information about a track occupancy detector:
 * <ul>
 * <li>its IOSpec
 * <li>if the IOSpec reports an occupied or unoccupied event
 * </ul>
 * This class is needed only for tagging an IOSpec read from an XML document.
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */

public class Detector
    implements XMLEleObject {

  /**
   * is the XML tag.
   */
  private String XmlTag;

  /**
   * is the IOSpec being wrapped.
   */
  private IOSpec MySpec;

  /**
   * the constructor.
   *
   * @param tag is the XML tag for identifying what the IOSPec is for.
   */
  public Detector(String tag) {
    XmlTag = tag;
  }

  /**
   * returns the IOSpec encapsulated.
   *
   * @return the IOSpec.  null is not valid.
   */
  public IOSpec getSpec() {
    return MySpec;
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
    return new String(XmlTag + " XML elements cannot not have text values ("
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
    if (IOSpec.XML_TAG.equals(objName)) {
      MySpec = (IOSpec) objValue;
    }
    else {
      resultMsg = new String(objName + " is not a valid embedded object in a"
                             + XmlTag + " XML element.");
    }
    return resultMsg;
  }

  /**
   * returns the XML Element tag for the XMLEleObject.
   *
   * @return the name by which XMLReader knows the XMLEleObject (the
   * Element tag).
   */
  public String getTag() {
    return XmlTag;
  }

  /**
   * tells the XMLEleObject that no more setValue or setObject calls will
   * be made; thus, it can do any error chacking that it needs.
   *
   * @return null, if it has received everything it needs or an error
   * string if something isn't correct.
   */
  public String doneXML() {
    if (MySpec == null) {
      return new String("Missing " + IOSpec.XML_TAG + " element in a " +
                        XmlTag + " XML element.");
    }
    return null;
  }

  /**
   * registers a DetectorFactory with the XMLReader.
   * @param tag is the XML name for the class of Detector.
   */
  static public void init(String tag) {
    XMLReader.registerFactory(tag, new DetectorFactory(tag));
  }

}

/**
 * is a Class known only to the Detector class for creating Detectors from
 * an XML document.
 */
class DetectorFactory
    implements XMLEleFactory {

  /**
   * is the XML element tag to register and apply to objects created.
   */
  private String DetectorTag;

  /**
   * is the factory constructor.
   *
   * @param tag is the XML element tag for Dectectors created by this
   * factory.
   */
  public DetectorFactory(String tag) {
    DetectorTag = tag;
  }

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
    return new String(DetectorTag + " XML elements cannot have an attribute.");
  }

  /*
   * tells the factory that the attributes have been seen; therefore,
   * return the XMLEleObject created.
   *
   * @return the newly created XMLEleObject or null (if there was a problem
   * in creating it).
   */
  public XMLEleObject getObject() {
    return new Detector(DetectorTag);
  }
}