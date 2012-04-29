/*
 * Name: LineFactory
 *
 * What: This file contains a Singleton, Factory object for constructing
 *  CtcLines - line width descriptions whose values can be changed while
 *  the dispatcher program is running (so that the user can adjust the look).
 *  In addition to creating and returning a CtcLine object, it creates a
 *  MenuItem for editing the CtcLine object and adds the MenuItem to the
 *  Lines pulldown.  It also adds the mnemonic for the object to the XML
 *  parser.
 */
package cats.gui;

import cats.layout.xml.*;
import java.util.Vector;
import java.util.Enumeration;
import javax.swing.*;

/**
 *  This file contains a Singleton, Factory object for constructing
 *  CtcLines - line descriptions whose values can be changed while the
 *  dispatcher program is running (so that the user can adjust the look).
 *  In addition to creating and returning a CtcLine object, it creates a
 *  MenuItem for editing the CtcLine object and adds the MenuItem to the
 *  Lines pulldown.  It also adds the mnemonic for the object to the XML
 *  parser so that the user can set the line width in the configuration
 *  file.
 *  <p>
 *  To add a new CtcLine Object, add a creation line (see createLine).
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2004, 2009</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */

public class LineFactory extends JMenu
    implements XMLEleFactory, XMLEleObject {

  /**
   * is the Singleton for determining the Line widths.
   */
  public static LineFactory Lines;

  /**
   * is the XML Tag for a Line Element.
   */
  private static final String LINE = "LINE";

  /**
   * is the XML tag for a Diagonal line.
   */
  public static final String DIAGONAL = "DIAGONAL";

  /**
   * is the XML tag for a horizontal or vertical line.
   */
  public static final String LEVEL = "LEVEL";

  /**
   * the list of lines for whose width can be adjusted.
   */
  private Vector<MnenomicEntry> Mnenomic;

  /**
   * is the class constructor.
   */
  public LineFactory() {
    MnenomicEntry entry;
    Enumeration<MnenomicEntry> e;
    Mnenomic = new Vector<MnenomicEntry>();

    createLine("Diagonal", DIAGONAL);
    createLine("non-Diagonal", LEVEL);
    Lines = this;
    // Register with XML
    XMLReader.registerFactory(LINE, this);
    e = Mnenomic.elements();
    while (e.hasMoreElements()) {
      entry = (e.nextElement());
      XMLReader.registerFactory(entry.MnenomicName, entry.LineObject);
    }
  }

  /**
   * creates a new CtcLine object.
   *
   * @param label    is the label of the entry in the Lines pulldown menu
   * @param mnenomic is the name to identify the object in a configuration file
   * @return the Object that creates the requested class of lines.
   *
   * @see cats.gui.CtcLine
   */
  public CtcLine createLine(String label, String mnenomic) {
    CtcLine l = new CtcLine(label, mnenomic);
    JMenuItem item = new JMenuItem();
    item.setText(label);
    item.addActionListener(l);
    add(item);
    if (findLine(mnenomic) == null) {
      Mnenomic.addElement(new MnenomicEntry(mnenomic, l));
    }
    else {
      System.out.println("Duplicate Line mnenomic: " + mnenomic);
    }
    return l;
  }

  /**
   * searches the table of mnenomics for an entry.
   *
   * @param mnenomic is the mnenomic which may be in the table
   *
   * @return null if an entry doesn't exist in the table or the associated
   *  CtcLine, if it does.
   *
   * @see cats.gui.CtcLine
   */
  public CtcLine findLine(String mnenomic) {
    MnenomicEntry entry;
    Enumeration<MnenomicEntry> e = Mnenomic.elements();
    while (e.hasMoreElements()) {
      entry = (e.nextElement());
      if (entry.MnenomicName.equals(mnenomic)) {
        return entry.LineObject;
      }
    }
    return null;
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
    return new String("Attributes are not accepted in " + LINE +
                      " elements");
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
    return new String("Text fields are not accepted in " + LINE +
                      " elements");
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
    MnenomicEntry entry;
    Enumeration<MnenomicEntry> e = Mnenomic.elements();
    while (e.hasMoreElements()) {
      entry = (e.nextElement());
      if (entry.MnenomicName.equals(objName)) {
        return null;
      }
    }
    return new String(objName + " is not a valid XML Element of " + LINE);
  }

  /*
   * returns the XML Element tag for the XMLEleObject.
   *
   * @return the name by which XMLReader knows the XMLEleObject (the
   * Element tag).
   */
  public String getTag() {
    return LINE;
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

  /* Name: MnenomicEntry
   *
   * What:
   *  A simple data structure for associating a mnenomic with its CtcFont
   *  Object.
   */
  class MnenomicEntry {
    String MnenomicName;
    CtcLine LineObject;

    MnenomicEntry(String name, CtcLine object) {
      MnenomicName = name;
      LineObject = object;
    }
  }
}
/* @(#)LineFactory.java */