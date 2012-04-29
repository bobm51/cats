/*
 * Name: TemplateStore
 *
 * What:
 *  This class provides a repository for holding all the SignalTemplates.
 */
package cats.layout;

import java.util.Enumeration;
import java.util.Vector;

/**
 * Holds all the SignalTemplates.
 *
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2004, 2010</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */

public class TemplateStore {

  /**
   * the singleton, which is known by all clients.
   */
  public static TemplateStore SignalKeeper = new TemplateStore();

  /**
   * The container for holding all the SignalTemplates.
   */
  private Vector<SignalTemplate> TheStore;

  /**
   * the constructor.
   */
  public TemplateStore() {
    TheStore = new Vector<SignalTemplate>();
//    SignalTemplate.init();
  }

  /**
   * is a query for if any SignalTemplates have been defined or not.
   *
   * @return true if none have been defined or false if at least one has
   * been defined.
   */
  public boolean isEmpty() {
    return TheStore.isEmpty();
  }

  /**
   * adds a new SignalTemplate, if one with the same name doesn't already
   * exist.
   *
   * @param temp is the SignalTemplate to be added.
   * @return false if a template with the nmae exists; otherwise,
   * true.
   *
   * @see SignalTemplate
   */
  public boolean add(SignalTemplate temp) {
    SignalTemplate st;

    for (Enumeration<SignalTemplate> iter = TheStore.elements(); iter.hasMoreElements(); ) {
      st = iter.nextElement();
      if (st.getName().equals(temp.getName())) {
        return false;
      }
    }
    TheStore.add(temp);
    return true;
  }

  /**
   * finds the SignalTemplate.
   *
   * @param tag is the tag that identifies the SignalTemplate being
   * requested.
   *
   * @return the SignalTemplate that matches or null.
   *
   * @see SignalTemplate
   */
  public SignalTemplate find(String tag) {
    SignalTemplate temp = null;

    for (Enumeration<SignalTemplate> iter = TheStore.elements(); iter.hasMoreElements(); ) {
      temp = iter.nextElement();
      if (temp.getName().equals(tag)) {
        return temp;
      }
    }
    return null;
  }

  /**
   * locates the SignalTemplate at a specific index.
   *
   * @param index is the index of the SignalTemplate being requested.
   *
   * @return the SignalTemplate at that index or null, if the index
   * is invalid.
   *
   * @see SignalTemplate
   */
  public SignalTemplate elementAt(int index) {
    if ( (index >= 0) && (index < TheStore.size())) {
      return TheStore.elementAt(index);
    }
    return null;
  }

  /**
   * locates the index of a SignalTemplate.
   *
   * @param name is the name of the SignalTemplate.
   *
   * @return the index, if a SignalTemplate with name exists or -1.
   */
  public int indexOf(String name) {
    SignalTemplate temp = null;
    int index = 0;

    for (Enumeration<SignalTemplate> iter = TheStore.elements(); iter.hasMoreElements(); ) {
      temp = iter.nextElement();
      if (temp.getName().equals(name)) {
        return index;
      }
      ++index;
    }
    return -1;
  }
}
/* @(#)TemplateStore.java */