/* Name: FlashRate.java
 *
 * What;
 *  FastRate is a Singleton object with an Enumerated value.  It is set
 *  to the number of milliseconds for the on/off phase of a
 *  flashing light.  All CATS flashing lights use the same
 *  cycle.  JMRI flashing lights used the constant defined
 *  in DefaultSignalHead.
 */
package cats.layout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import cats.gui.jCustom.AcceptDialog;
import cats.layout.xml.XMLEleFactory;
import cats.layout.xml.XMLEleObject;
import cats.layout.xml.XMLReader;

/**
 *  FastRate is a Singleton object with an Enumerated value.  It is set
 *  to the number of milliseconds for the on/off phase of a
 *  flashing light.  All CATS flashing lights use the same
 *  cycle.  JMRI flashing lights used the constant defined
 *  in DefaultSignalHead.
 * <p>Title: designer</p>
 * <p>Description: A program for designing dispatcher panels</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */
public class FlashRate extends JMenuItem implements XMLEleObject {
  
  /**
   * is the tag for identifying a FLashRate Object in the XMl file.
   */
  private static final String XML_TAG = "FLASHRATE";
  
  /**
   * is the index of the default value
   */
  private static final int DEFAULT_RATE = 1;
  
  /**
   * the supported flashrates.
   */
  private static String[] RATES = {
    new String("500"),
    new String("750"),
    new String("1000"),
    new String("1250"),
    new String("1500"),
  };
  
  /**
   * is the singleton.
   */
  public static FlashRate TheFlashRate;
  
  /**
   * is the index into Rates of the current selection.
   */
  private int Rate = DEFAULT_RATE;
  
  /**
   * the constructor.
   *
   * @param title is the title on the drop down menu.
   */
  public FlashRate(String title) {
    super(title);
    if (TheFlashRate == null) {
      init();
      addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent ae) {
          JComboBox box = new JComboBox(RATES);
          JPanel p = new JPanel();
          p.add(new JLabel("Select rate (in milliseconds)"));
          box.setSelectedIndex(Rate);
          p.add(box);
          if (AcceptDialog.select(p, "Flash Rate")) {
            int r = box.getSelectedIndex();
            if (r != Rate) {
              Rate = r;
            }
          }
        }
      }
      );
      TheFlashRate = this;
    }
  }
  
  /**
   * returns the flash rate setting.
   *
   * @return the number of
   *  milliseconds in the current flash rate.
   */
  public int getRate() {
    return Integer.parseInt(RATES[Rate]);
  }
  
  /**
   * sets the Flash Rate.
   *
   * @param rate is the new String representation of the 
   * value in milliseconds.  It must be one of the values in RATES.
   */
  public void setRate(String rate) {
    for (int index = 0; index < RATES.length; ++index){
      if (RATES[index].equals(rate)) {
        Rate = index;
      }
    }
  }
  
  /**
   * enables or disables the MenuItem.
   * 
   * @param truth is true to enable the item and false to disable it
   */
  public void turnOn(boolean truth) {
    setEnabled(truth);
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
    setRate(eleValue);
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
   * registers a FastClock factory with the XMLReader.
   */
  static public void init() {
    XMLReader.registerFactory(XML_TAG, new FlashRateFactory());
  }
}

/**
 * is a Class known only to the FlashRate class for creating a true
 * value from an XML file.
 */
class FlashRateFactory
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
    return new String("A " + FastClock.XML_TAG +
        " XML Element cannot have a " + tag +
    " attribute.");
  }
  
  /*
   * tells the factory that the attributes have been seen; therefore,
   * return the XMLEleObject created.
   *
   * @return the newly created XMLEleObject or null (if there was a problem
   * in creating it).
   */
  public XMLEleObject getObject() {
    return FlashRate.TheFlashRate;
  }
}
/* @(#)FlashRate.java */