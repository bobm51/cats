/* Name: AspectCommand.java
 *
 * What:
 *  This class of objects binds a signal presentation state String with
 *  the list of decoder commands needed to make it happen.
 */
package cats.layout.items;

import cats.jmri.JmriPrefixManager;
import cats.layout.xml.*;

/**
 * is a class for binding a signal presentation String with the list of
 * decoder commands needed to make it happen.  Objects of this class are
 * intended to be non-dynamic.  They are created, the String and list
 * of commands is added to it, then nothing is edited.
 *
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2004, 2010</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */

public class AspectCommand
    implements XMLEleObject {
  /**
   * is the tag for indentifying a SecSignal in the XMl file.
   */
  static final String XML_TAG = "ASPECTCOMMAND";

  /**
   * is the XML tag for the head presentation label.
   */
  static final String NAME_TAG = "PLABEL";

  /**
   * the String for identifying the head's presentation.
   */
  private String AspectLabel;

  /**
   * the list of commands.
   */
  private IOInterface Commands;

  /**
   * the constructor.
   *
   * @param label is the head presentation String used to identify
   * the commands.
   */
  public AspectCommand(String label) {
    AspectLabel = label;
  }

  /**
   * retrieves the presentation label.
   *
   * @return the label.
   */
  public String getPresentationLabel() {
    String label = null;
    if (AspectLabel != null) {
      label = new String(AspectLabel);
    }
    return label;
  }

  /**
   * retrieves the command list.
   *
   * @return the command list.
   *
   * @see IOSpecChain
   */
  public IOInterface getCommands() {
    return Commands;
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
   * There is some code in here for supporting legacy XML files
   * before IOSpecChain was created.
   *
   * @param objName is the name of the embedded object
   * @param objValue is the value of the embedded object
   *
   * @return null if the Object is acceptible or an error String
   * if it is not.
   */
  public String setObject(String objName, Object objValue) {
    String resultMsg = null;
    IOSpecChain temp;
    if (IOInterface.class.isInstance(objValue)) {
      if (Commands == null) {
        Commands = (IOInterface) objValue;
      }
      else if (IOSpecChain.class.isInstance(Commands)){
        ((IOSpecChain) Commands).addSpec((IOInterface) objValue);
      }
      else {
        temp = JmriPrefixManager.findChain(IOSpecChain.CHAIN_PREFIX,"0");
        temp.addSpec(Commands);
        temp.addSpec((IOInterface) objValue);
        Commands = temp;
      }
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
   * registers a AspectCommandFactory with the XMLReader.
   */
  static public void init() {
    XMLReader.registerFactory(XML_TAG, new AspectCommandFactory());
    IOSpec.init();
    IOSpecChain.init();
  }
}

/**
 * is a Class known only to the AspectCommand class for creating an
 * AspectCommand from an XML document.  Its purpose is to pick up the
 * signal presentation name and the decoder command list.
 */
class AspectCommandFactory
    implements XMLEleFactory {

  /**
   * is the head presentation label, from the attribute.
   */
  String AttrLabel;

  /*
   * tells the factory that an XMLEleObject is to be created.  Thus,
   * its contents can be set from the information in an XML Element
   * description.
   */
  public void newElement() {
    AttrLabel = null;
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
    if (AspectCommand.NAME_TAG.equals(tag)) {
      AttrLabel = new String(value);
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
    if (AttrLabel == null) {
      System.out.println("Missing attribute in " + AspectCommand.XML_TAG
                         + " XML Element.");
      return null;
    }
    return new AspectCommand(AttrLabel);
  }

}
