/* Name: CtcLine.java
 *
 * What: This file contains the CtcLine class - a wrapper around the Java
 *   Stroke class, providing a level of indireciton, so that all uses
 *   of a particular Stroke will change when the referenced line changes.
 *   It includes just the line width, though it would be easy to add other
 *   Stroke attributes.
 *
 * Special Considerations:
 */
package cats.gui;

import cats.gui.jCustom.AcceptDialog;
import cats.layout.xml.*;
import java.awt.Component;
import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * defines a class for associating the widths of objects on the dispatcher
 * panel with a consistent width.  These are not constants, but
 * variables.  The reason is so that the user can modify them to suit
 * the characteristics of the display.
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */
public class CtcLine
    implements java.awt.event.ActionListener, XMLEleFactory, XMLEleObject {

  // The current value of the line width
  private float TheWidth = 2.0f;

  // The default value of the line width
  // private float DefWidth;

  // What to place on the popup title bar
  private String Title;

  // The tag for this Object in an XML Element
  private String XMLTag;

  // The Label in the Dialog
  private JLabel WidthLab = new JLabel("Width of line: ");

  // The text field for entering the new width
  private JTextField WidthText = new JTextField(String.valueOf(TheWidth), 4);

  // The Label and text field, as a group
  private JPanel Content = new JPanel();

  /**
   * construct an object that can be used for a displayable object.
   *
   * @param     title is the title on the JMenu and JDialog.
   * @param     xmlTag is the XML tag for this Object.
   */
  public CtcLine(String title, String xmlTag) {
    Title = title;
    XMLTag = xmlTag;
    Content.add(WidthLab);
    Content.add(WidthText);
  }

  /**
   * retrieve the Line width.
   *
   * @return The current width of the line.
   */
  public float grabLine() {
    return TheWidth;
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
    return new String(XMLTag + " does not have any XML attributes");
  }

  /*
   * tells the factory that the attributes have been seen; therefore,
   * return the XMLEleObject created.
   *
   * @return the newly created XMLEleObject or null (if there was a problem
   * in creating it).
   */
  public XMLEleObject getObject() {
    return this;
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
    String resultMsg = null;
    try {
      TheWidth = new Float(eleValue).floatValue();
//      DefWidth = TheWidth;
      WidthText.setText(String.valueOf(TheWidth));
    }
    catch (NumberFormatException nfe) {
      resultMsg = new String(XMLTag + " must be in floating point format");
    }
    return resultMsg;
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
    return new String("XML objects are not accepted by " + XMLTag +
                      " elements.");
  }

  /*
   * returns the XML Element tag for the XMLEleObject.
   *
   * @return the name by which XMLReader knows the XMLEleObject (the
   * Element tag).
   */
  public String getTag() {
    return XMLTag;
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
   * presents a Dialog and adjust the line width by what is selected.
   */
  public void actionPerformed(ActionEvent e) {
    if (AcceptDialog.select(Content, Title)) {
      try {
        TheWidth = new Float(WidthText.getText()).floatValue();
      }
      catch (NumberFormatException nfe) {
        JOptionPane.showMessageDialog( (Component)null,
                                      "Width must be in x.y format",
                                      "Width Error",
                                      JOptionPane.ERROR_MESSAGE);
      }
      WidthText.setText(String.valueOf(TheWidth));
      Screen.tryRefresh();
    }
    else {
      WidthText.setText(String.valueOf(TheWidth));
    }
  }
}
