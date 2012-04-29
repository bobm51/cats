/* Name: AspectTable.java
 *
 * What:
 *  This class defines an object that holds the information for sending to
 *  decoders that sets signals.  It the implementation of the indications
 *  defined in the physical Signal's AspectMap.
 */
package cats.layout.items;

import java.util.ArrayList;
import jmri.SignalHead;
import cats.jmri.SWSignalHead;
import cats.layout.xml.*;

/**
 * is a class for holding the instructions for sending to the decoders the
 * commands to set the lights/semaphore blades, to states corresponding
 * to the strings in the physical signal's AspectMap.  There is one
 * SignalHead for each head on the physical Signal.
 *
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2004, 2010</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */

public class AspectTable
    implements XMLEleObject {

  /**
   * is the tag for identifying an AspectTable in the XMl file.
   */
  static final String XML_TAG = "ASPECTTBL";

  /**
   * is the table of states and heads.  Each element in the
   * array is the instructions for one signal head.
   */
  private SignalHead[] Instructions;

  /**
   * is the list of HeadStates that are either external JMRI
   * SignalHeads or the decoder definitions for each "color"
   */
  private ArrayList<Object> States = new ArrayList<Object>(3);
  
  /**
   * is the constructor.
   */
  public AspectTable() {
  }

  /**
   * adds a SignalHead to the table.
   *
   * @param headList is the list of commands for the new head.
   *
   * @see HeadStates
   */
  public void addHead(SignalHead headList) {
    if (Instructions == null) {
      Instructions = new SignalHead[1];
      Instructions[0] = headList;
    }
    else {
      int n = Instructions.length;
      SignalHead[] temp = new SignalHead[n + 1];
      System.arraycopy(Instructions, 0, temp, 0, n);
      Instructions = temp;
      Instructions[n] = headList;
    }
  }

  /**
   * return the command lists.
   *
   * @return the number of heads with command lists.
   */
  public int getListCount() {
    if (Instructions != null) {
      return Instructions.length;
    }
    return 0;
  }

  /**
   * returns a JMRI SignalHead.
   * 
   * @param head is the index of the desired signal head.
   * @return the head that corresponds to the parameter.  It will be null
   * if the head does not exist.
   */  
  public SignalHead getHead(int head) {
    if ( (Instructions != null) && (head >= 0) && (head < Instructions.length)) {
      return Instructions[head];
    }
    return null;   
  }

  /**
   * provides the name of the Signal to the AspectTable so that it can
   * relay it to the composite SignalHeads.  This is the trigger for
   * creating the SignalHeads.  The SignalHeads use it for synthesizing
   * the system name for the SignalHead.
   * 
   * @param name is the name of the Signal, as provided by the user in
   * designer.
   */
  public void setSignalName(String name) {
    int head;
    Instructions = new SignalHead[States.size()];
    if ((name == null) || (name.length() == 0)) {
      for (head = 0; head < States.size(); ++head) {
        Instructions[head] = ((HeadStates)States.get(head)).getSignalHead();
      }
    }
    else if (1 == States.size()) {
      Instructions[0] = ((HeadStates)States.get(0)).
      getSignalHead(SWSignalHead.CATS_SIGNALHEAD + name);
    }
    else {
      for (head = 0; head < States.size(); ++head) {
        Instructions[head] = ((HeadStates)States.get(head)).
        getSignalHead(SWSignalHead.CATS_SIGNALHEAD + name + '_' + head);
      }      
    }
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
   *
   * @param objName is the name of the embedded object
   * @param objValue is the value of the embedded object
   *
   * @return null if the Object is acceptible or an error String
   * if it is not.
   */
  public String setObject(String objName, Object objValue) {
    String resultMsg = null;
    if (HeadStates.XML_TAG.equals(objName)) {
      States.add(objValue);
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
   * registers an AspectTableFactory with the XMLReader.
   */
  static public void init() {
    XMLReader.registerFactory(XML_TAG, new AspectTableFactory());
    HeadStates.init();
  }
}

/**
     * is a Class known only to the AspectTable class for creating AspectTables from
 * an XML document.  Its purpose is to collect the decoder instructions for
 * each head, for each head state.
 */
class AspectTableFactory
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

    resultMsg = new String("An " + AspectTable.XML_TAG +
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
    return new AspectTable();
  }
}
/* @(#)AspectTable.java */