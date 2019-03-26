/* Name: CounterFactory.java
 *
 * What: This file contains the CounterFactory class.  It is a singleton
 *  that holds the list of customizable, numeric variables.
 *
 * Special Considerations:
 */
package cats.gui;

import java.util.Hashtable;
import javax.swing.JMenu;

import cats.layout.xml.XMLEleFactory;
import cats.layout.xml.XMLEleObject;
import cats.layout.xml.XMLReader;

/**
 * This file contains the CounterFactory class.  It is a singleton
 * that holds the list of customizable, numeric variables.  A numeric
 * variable is an integer value that can be changed in real time,
 * from the menu.
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2004, 2009</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */
@SuppressWarnings("serial")
public class CounterFactory
    extends JMenu implements XMLEleFactory, XMLEleObject {
  
  /**
   * the XML Element tag.
   */
  public static final String XML_TAG = "COUNTER";
  
  /**
   * the attribute tag identifying the counter name
   */
  private static final String COUNTER_NAME = "NAME";
  
  /**
   * the attribute tag identifying the counter's value
   */
  private static final String COUNTER_VALUE = "VALUE";

  /**
   * are the values for the Sequences
   */
  private final int[][] SequenceRanges = {
      {0, 1, 2, 3, 4, 5},  // debounce (in seconds)
      {0, 10, 20, 30, 40, 50},  // delay between commands during refresh (ms)
      {0, 10, 20, 30, 40, 50}   // Loconet (ms)
  };

  /**
   * are the labels on menus
   */
  private final String[] SequenceLabels = {
      "Occupancy Debounce",     // debounce
      "Refresh Delay (msec)",   // refresh
      "Loconet Governor (msec)" // Loconet
  };
  
  /**
   * are the default indexes
   */
  private final int[] SequenceIndexes = {
      0,
      0,
      1
  };
  
  /**
   * are the XML tags for each of the above
   */
  /**
   * is the tag for the Debounce value
   */
  public final static String DEBOUNCETAG = "DEBOUNCE";
  
  /**
   * is the tag for the Refresh value
   */
  public final static String REFRESHTAG = "REFRESH";
  
  /**
   * is the tag for the Loconet governor
   */
  public final static String LNTAG = "LNGOVERNOR";
  private final String[] XmlTags = {
      DEBOUNCETAG,
      REFRESHTAG,
      LNTAG
  };
  
  /**
   * the singleton.
   */
  public static CounterFactory CountKeeper;

  /**
   * is the list of variables.
   */
  private Hashtable<String, Sequence> Counters;

  /**
   * are used in reading in counters from the XML file.
   */
  private String CounterName;
  private int CounterValue;

  /**
   * constructs the factory.
   *
   * @param label is the JMenu label for the factory.
   */
  public CounterFactory(String label) {
    super(label);
    Counters = new Hashtable<String, Sequence>();
    CountKeeper = this;
    XMLReader.registerFactory(XML_TAG, this);
    for (int seq = 0; seq < SequenceRanges.length; ++seq) {
      createSequence(seq);
    }
    exposeSequence(DEBOUNCETAG);
    exposeSequence(REFRESHTAG);
  }


  /**
   * creates a new numeric variable, returning the JMenuItem used to select it.
   *
   * @param label is the identifier it has in the menu.
   * @param lowest is the lowest value.
   * @param highest is the highest value.
   * @param first is the initial value.
   * @param xmlTag is the tag of the counter in the XML file
   *
   * @return the Variable.
   */
  public Sequence createAdjuster(String label, int lowest, int highest,
                             int first, String xmlTag) {
    Adjuster adjuster = null;
    if (!Counters.containsKey(xmlTag)) {
      add(adjuster = new Adjuster(label, lowest, highest, first, xmlTag));
      Counters.put(xmlTag, adjuster);
    }
    else {
      adjuster = (Adjuster) Counters.get(xmlTag);
    }
    return adjuster;
  }
  
  /**
   * creates a new numeric variable, returning the JMenuItem used to select it.
   *
   * @param label is the identifier it has in the menu.
   * @param values is the list of acceptable values.
   * @param first is the initial value.
   * @param xmlTag is the tag of the counter in the XML file
   *
   * @return the Variable.
   */
  public Sequence createSequence(String label, int values[], int first,
      String xmlTag) {
    Sequence sequence = null;
    if (!Counters.containsKey(xmlTag)) {
      Counters.put(xmlTag, sequence = new Sequence(label, values, first, xmlTag));
    }
    else {
      sequence = Counters.get(xmlTag);
    }
    return sequence;
  }

  /**
   * is a shorthand for creating a new Sequence from one of the
   * ones defined above.
   * 
   * @param i selects one of the Sequences defined above.
   * @return the Sequence thus created.
   */
  private Sequence createSequence(int i) {
    return createSequence(SequenceLabels[i], SequenceRanges[i],
        SequenceIndexes[i], XmlTags[i]);
  }

  /**
   * attempts to find a Sequence by its XML tag
   * @param tag is the XML tag
   * @return the Sequence (or null if it does not exist)
   */
  public Sequence findSequence(String tag) {
    return Counters.get(tag);
  }

  /**
   * adds a Sequence to the Menu.  By default, sequences are
   * created, but not added until needed.
   * @param tag
   */
  public void exposeSequence(String tag) {
    Sequence seq = Counters.get(tag);
    if (seq != null) {
      if (seq.getParent() == null) {
        add(seq);
      }
    }
  }
  
  /**
   * tells the factory that an XMLEleObject is to be created. Thus, its
   * contents can be set from the information in an XML Element description.
   *
   * For the FontFactory, this does nothing.
   */
  public void newElement() {
      CounterName = null;
      CounterValue = -1;
  }
  
  /*
   * gives the factory an initialization value for the created XMLEleObject.
   *
   * @param tag is the name of the attribute. @param value is it value.
   *
   * @return null if the tag:value are accepted; otherwise, an error string.
   */
  public String addAttribute(String tag, String value) {
      String resultMsg = null;
      if (COUNTER_NAME.equals(tag)) {
          CounterName = value;
      }
      else if (COUNTER_VALUE.equals(tag)) {
          try {
              CounterValue = Integer.parseInt(value);
          }
          catch (NumberFormatException nfe) {
              resultMsg = new String(value +
              " is not the index of a counter.");
          }           
      }
      else {
          resultMsg = new String(tag + " is not a counter attribute.");
      }
      return resultMsg;
  }
  
  /*
   * tells the factory that the attributes have been seen; therefore, return
   * the XMLEleObject created.
   *
   * @return the newly created XMLEleObject or null (if there was a problem in
   * creating it).
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
      return new String("There is no text field for " + XML_TAG + " elements.");
  }
  
  /*
   * is the method through which the object receives embedded Objects.
   *
   * @param objName is the name of the embedded object @param objValue is the
   * value of the embedded object
   *
   * @return null if the Object is acceptible or an error String if it is not.
   */
  public String setObject(String objName, Object objValue) {
      if (Counters.containsKey(objName)) {
          return null;
      }
      return new String(objName + " is not a valid XML Element for a " + XML_TAG
              + ".");
  }
  
  /*
   * returns the XML Element tag for the XMLEleObject.
   *
   * @return the name by which XMLReader knows the XMLEleObject (the Element
   * tag).
   */
  public String getTag() {
      return XML_TAG;
  }
  
  /*
   * tells the XMLEleObject that no more setValue or setObject calls will be
   * made; thus, it can do any error chacking that it needs.
   *
   * @return null, if it has received everything it needs or an error string
   * if something isn't correct.
   */
  public String doneXML() {
      String resultMsg = null;
      int seqLength;
      Sequence entry;
      if (CounterName != null) {
          if (Counters.containsKey(CounterName)) {
              entry = Counters.get(CounterName);
              seqLength = entry.getSequenceSize();
              if ((CounterValue < 0) || (CounterValue > seqLength)) {
                  resultMsg = new String("A counter cannot have an index of " + CounterValue);
              }
              else {
                  entry.setSelection(CounterValue);
              }
          }
          else {
              resultMsg = new String("Unknown counter: " + CounterName);
          }
      }
      else {
          resultMsg = new String("Missing the counter's name.");
      }
      return resultMsg;
  }
}
/* @(#)CounterFactory.java */