/*
 * Name: PaletteFactory
 *
 * What: This file contains a Singleton, Factory object for constructing
 *  Palettes - color descriptions whose values can be changed while the
 *  dispatcher program is running (so that the user can adjust the Colors).
 *  In addition to creating and returning a Palette object, it creates a
 *  MenuItem for editing the Palette object and adds the MenuItem to the
 *  Color pulldown.  It also adds the nmemonic for the object to the parser
 *  so that the user can set the Color value in the configuration file.
 */
package cats.gui;

import cats.layout.xml.*;
import java.util.Vector;
import java.util.Enumeration;
import java.awt.Color;
import javax.swing.*;

/**
 *  This file contains a Singleton, Factory object for constructing
 *  Palettes - color descriptions whose values can be changed while the
 *  dispatcher program is running (so that the user can adjust the Colors).
 *  In addition to creating and returning a Palette object, it creates a
 *  MenuItem for editing the Palette object and adds the MenuItem to the
 *  Color pulldown.  It also adds the nmemonic for the object to the parser
 *  so that the user can set the Color value in the configuration file.
 *  <p>
 *  To add a new Palette Object, add a creation line (see createPalete).
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2004, 2009</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */

public class PaletteFactory extends JMenu
    implements XMLEleFactory, XMLEleObject {

  /**
   * is the Singleton through which the current values of colors are found.
   */
  public static PaletteFactory Palettes;

  /**
   * The XML tag for recognizing a Palette definition.
   */
  private static final String COLOR = "COLOR";

  /**
   * the XML tag for the color for trains which have not been run.
   */
  public static final String ONCALL = "ONCALL";

  /**
   * the XML tag for the color of trains which are working.
   */
  public static final String ACTIVE = "ACTIVE";

  /**
   * the XML tag for the color of the train which the cursor will move.
   */
  public static final String SELECTED = "SELECTED";

  /**
   * the XML tag for the color of trains which have completed their work.
   */
  public static final String TIEDUP = "TIEDUP";

  /**
   * the XML tag for the color of a Block with a train in it.
   */
  public static final String OCCUPIED = "OCCUPIED";

  /**
   * the XML tag for the color of a Block which does not have a train
   * in it and has not been reserved for another use.
   */
  public static final String EMPTY = "EMPTY";

  /**
   * the XML tag for the color of a Block which is reserved for a train
   * to pass through it.
   */
  public static final String RESERVED = "RESERVED";

  /**
   * the XML tag for the color of a Block which has been taken out of service.
   */
  public static final String OOSERVICE = "OOSERVICE";

  /**
   * the XML tag for the color of a Block which is being used for local
   * switching.
   */
  public static final String LOCAL = "LOCAL";

  /**
   * the tag for the color of a Block which has an active track warrant.
   */
  public static final String DTC = "DTC";

  /**
   * the XML tage for the color of an undetected Block - dark territory.
   */
  public static final String DARK = "DARK";

  /**
   * the XML tag for the color of a Depot.
   */
  public static final String DEPOT = "DEPOT";

  /**
   * the XML tag for the color of the label containing the Block name.
   */
  public static final String LABELS = "LABELS";

  /**
   * the XML tag for the color of a signal that does not have a route.
   */
  public static final String NO_ROUTE = "NO_ROUTE";
  
  /**
   * the XML tag for the color of a signal showing stop.
   */
  public static final String STOP = "STOP";

  /**
   * the XML tag for the color of a signal showing slow down.
   */
  public static final String APPROACH = "APPROACH";

  /**
   * the XML tag for the color of a signal showing go.
   */
  public static final String CLEAR = "CLEAR";

  /**
   * the XML tag for the color of a non-existing signal.
   */
  public static final String NOSIGNAL = "NOSIGNAL";

  /**
   * the XML tag of the background color.
   */
  public static final String BACKGROUND = "BACKGROUND";

  private Vector<MnenomicEntry> Mnenomic;

  /**
   * is the constructor.
   */

  public PaletteFactory() {
    MnenomicEntry entry;
    Enumeration<MnenomicEntry> e;
    Mnenomic = new Vector<MnenomicEntry>();
    createPalete(Color.lightGray, ONCALL);
    createPalete(Color.orange, ACTIVE);
    createPalete(Color.cyan, SELECTED);
    createPalete(Color.magenta, TIEDUP);
    createPalete(Color.red, OCCUPIED);
    createPalete(Color.white, EMPTY);
    createPalete(Color.green, RESERVED);
    createPalete(Color.magenta, OOSERVICE);
    createPalete(Color.blue, LOCAL);
    createPalete(Color.orange, DTC);
    createPalete(Color.gray, DARK);
    createPalete(Color.cyan, DEPOT);
    createPalete(Color.white, LABELS);
    createPalete(Color.white, NO_ROUTE);
    createPalete(Color.red, STOP);
    createPalete(Color.yellow, APPROACH);
    createPalete(Color.green, CLEAR);
    createPalete(Color.gray, NOSIGNAL);
    createPalete(Color.black, BACKGROUND);

    Palettes = this;

    // Register with XML
    XMLReader.registerFactory(COLOR, this);
    e = Mnenomic.elements();
    while (e.hasMoreElements()) {
      entry = (e.nextElement());
      XMLReader.registerFactory(entry.MnenomicName, entry.PaletteObject);
    }
  }

  /**
   * creates a new Palette object.
   *
   * @param c        is the initial value of the Color.
   * @param mnenomic is the name to identify the object in a configuration file
   * @return the newly created Palette
   *
   * @see Palette
   */
  public Palette createPalete(Color c, String mnenomic) {
    Palette p = new Palette(c, mnenomic);
    if (findPalette(mnenomic) == null) {
      Mnenomic.addElement(new MnenomicEntry(mnenomic, p));
    }
    else {
      System.out.println("Duplicate Color mnenomic: " + mnenomic);
    }
    return p;
  }

  /**
   * searches the table of mnenomic for an entry.
   *
   * @param  mnenomic is the mnenomic which may be in the table
   *
   * @return null if an entry doesn't exist in the table or the associated
   *  Palette, if it does.
   *
   * @see Palette
   */
  public Palette findPalette(String mnenomic) {
    MnenomicEntry entry;
    Enumeration<MnenomicEntry> e = Mnenomic.elements();
    while (e.hasMoreElements()) {
      entry = (e.nextElement());
      if (entry.MnenomicName.equals(mnenomic)) {
        return entry.PaletteObject;
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
  public String addAttribute(String attrName, String attrValue) {
    return new String("Attributes are not accepted in " + COLOR +
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
    return new String("Text fields are not accepted in " + COLOR +
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
    return new String(objName + "is not a valid XML Element for " + COLOR);
  }

  /*
   * returns the XML Element tag for the XMLEleObject.
   *
   * @return the name by which XMLReader knows the XMLEleObject (the
   * Element tag).
   */
  public String getTag() {
    return COLOR;
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

  /* Name: MnenomicEntry
   *
   * What:
   *  A simple data structure for associating a mnenomic with its Palette
   *  Object.
   */
  class MnenomicEntry {
    String MnenomicName;
    Palette PaletteObject;

    MnenomicEntry(String name, Palette object) {
      MnenomicName = name;
      PaletteObject = object;
    }
  }
}
