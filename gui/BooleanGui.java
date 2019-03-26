/*
 * Name: BoolenaGui.java
 * 
 * What:
 *  This file contains the BooleanGui class.  Each instantiation has
 *  a flag, a JMenuItem for changing the state of the flag, and an
 *  accessor method for retrieving the state of the flag.
 */
package cats.gui;

import cats.layout.xml.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBoxMenuItem;
/**
 *  This file contains the BooleanGui class.  Each instantiation has
 *  a flag, a JMenuItem for changing the state of the flag, and an
 *  accessor method for retrieving the state of the flag.
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */

@SuppressWarnings("serial")
public class BooleanGui extends JCheckBoxMenuItem implements ActionListener,
XMLEleObject, XMLEleFactory {

  /**
   * is the XML tag
   */
  private String XMLTag;
  
  /**
   * is the intial (default) polarity of the flag.
   */
  private boolean MyDefault;
  
  /**
   * constructs the factory.
   *
   * @param label is the JMenuItem label for the factory.
   * @param tag is the XML tag.
   * @param polarity is the default polarity of the flag.
   */
  public BooleanGui(String label, String tag, boolean polarity) {
    super(label, polarity);
    addActionListener(this);
    XMLTag = tag;
    MyDefault = polarity;
    XMLReader.registerFactory(tag, this);
  }

  /**
   * is the ActionListener for setting or clearing the flag.
   */
  public void actionPerformed(ActionEvent arg0) {
  }
  
  /**
   * is the accessor for retrieving the value of the flag.
   * @return the current trace setting
   */
  public boolean getFlagValue() {
    return getState();
  }
  
  /**
   * is the setter for changing the value of the
   * flag.
   * 
   * @param newState is the new value of the 
   * flag.
   */
  public void setFlagValue(boolean newState) {
    setState(newState);
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
    return new String(XMLTag + " elements cannot have values.");
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
    return new String("XML objects are not accepted by " + XMLTag +
    " elements.");
  }

  /**
   * returns the XML Element tag for the XMLEleObject.
   *
   * @return the name by which XMLReader knows the XMLEleObject (the
   * Element tag).
   */
  public String getTag() {
    return XMLTag;
  }

  /**
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
   * tells the factory that an XMLEleObject is to be created.  Thus,
   * its contents can be set from the information in an XML Element
   * description.
   */
  public void newElement() { 
    setFlagValue(!MyDefault);
  }

  /**
   * gives the factory an initialization value for the created XMLEleObject.
   *
   * @param tag is the name of the attribute.
   * @param value is it value.
   *
   * @return null if the tag:value are accepted; otherwise, an error
   * string.
   */
  public String addAttribute(String tag, String value) {
    return new String("Attributes are not accepted in " + XMLTag +
    " elements");

  }

  /**
   * tells the factory that the attributes have been seen; therefore,
   * return the XMLEleObject created.
   *
   * @return the newly created XMLEleObject or null (if there was a problem
   * in creating it).
   */
  public XMLEleObject getObject() {
    return this;
  }
}
/* @(#)BooleanGui.java */