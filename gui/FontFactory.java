/*
 * Name: FontFactory
 *
 * What: This file contains a Singleton, Factory object for constructing
 *  CtcFonts - letter descriptions whose values can be changed while the
 *  dispatcher program is running (so that the user can adjust the look).
 *  In addition to creating and returning a CtcFont object, it creates a
 *  MenuItem for editing the CtcFont object and adds the MenuItem to the
 *  Fonts pulldown.  It also adds the mnemonic for the object to the XML
 *  parser.
 */
package cats.gui;

import cats.layout.xml.*;
import java.awt.Font;
import java.util.Vector;
import java.util.Enumeration;

/**
 *  This file contains a Singleton, Factory object for constructing
 *  CtcFonts - letter descriptions whose values can be changed while the
 *  dispatcher program is running (so that the user can adjust the look).
 *  In addition to creating and returning a CtcFont object, it creates a
 *  MenuItem for editing the CtcFont object and adds the MenuItem to the
 *  Fonts pulldown.  It also adds the mnemonic for the object to the XML
 *  parser so that the user can set the font value in the configuration
 *  file.
 *  <p>
 *  To add a new CtcFont Object, add a creation line (see createFont).
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2004, 2009</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */

public class FontFactory
    implements XMLEleFactory, XMLEleObject {

  /**
   * is a Singelton containing the Font descriptions for each label type.
   */
  public static FontFactory Fonts;

  /**
   * is the XML tag for identifying a Font element.
   */
  private static final String FONT = "FONT";

  /**
   * is the XML Tag for labeling Train identities.
   */
  public static final String FONT_TRAIN = "FONT_TRAIN";

  /**
   * is the XML Tag for labeling Section.
   */
  public static final String FONT_LABEL = "FONT_LABEL";

  /**
   * the list of Label kinds.
   */
  private Vector<MnenomicEntry> Mnenomic;

  /**
   * is the constructor.
 * @param font is the font being created.
   */
  public FontFactory(Font font) {
    MnenomicEntry entry;
    Enumeration<MnenomicEntry> e;
    Mnenomic = new Vector<MnenomicEntry>();

    createFont(FONT_TRAIN, font);
    createFont(FONT_LABEL, font);
    // Register with XML
    XMLReader.registerFactory(FONT, this);
    e = Mnenomic.elements();
    while (e.hasMoreElements()) {
      entry = (e.nextElement());
      XMLReader.registerFactory(entry.MnenomicName, entry.FontObject);
    }
  }

  /**
   * creates a new CtcFont object.
   *
   * @param mnenomic is the name to identify the object in a configuration file
   * @param defFont is the default Font
   * @return the font meeting the requirements.
   */
  public CtcFont createFont(String mnenomic, Font defFont) {
    CtcFont f = new CtcFont(mnenomic, defFont);
    if (findFont(mnenomic) == null) {
      Mnenomic.addElement(new MnenomicEntry(mnenomic, f));
    }
    else {
      System.out.println("Duplicate Font mnenomic: " + mnenomic);
    }
    return f;
  }

  /** Name: findFont
   *
   * What:
   *  This method searches the table of mnenomics for an entry.
   *
   * Inputs:
   *  mnenomic is the mnenomic which may be in the table
   *
   * Returns:
   *  null if an entry doesn't exist in the table or the associated
   *  CtcFont, if it does.
   * @param mnenomic describes the font
   * @return the font described by mnenomic
   */
public CtcFont findFont(String mnenomic) {
    MnenomicEntry entry;
    Enumeration<MnenomicEntry> e = Mnenomic.elements();
    while (e.hasMoreElements()) {
      entry = (e.nextElement());
      if (entry.MnenomicName.equals(mnenomic)) {
        return entry.FontObject;
      }
    }
    return null;
  }

  /**
   * tells the factory that an XMLEleObject is to be created.  Thus,
   * its contents can be set from the information in an XML Element
   * description.
   *
   * For the FontFactory, this does nothing.
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
    return new String("Attributes are not accepted in " + FONT +
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
    return new String("There is no text field for " + FONT + " elements.");
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
    return new String(objName + " is not a valid XML Element for a " +
                      FONT + ".");
  }

  /*
   * returns the XML Element tag for the XMLEleObject.
   *
   * @return the name by which XMLReader knows the XMLEleObject (the
   * Element tag).
   */
  public String getTag() {
    return FONT;
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
    CtcFont FontObject;

    MnenomicEntry(String name, CtcFont object) {
      MnenomicName = name;
      FontObject = object;
    }
  }
}
/* @(#)FontFactory.java */