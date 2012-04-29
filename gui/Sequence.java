/* Name: Sequence.java
 *
 * What: This file contains the Sequence class.  It provides a numeric
 *  variable, an accessor method, and a menu item that allows it to
 *  be changed from the menu.
 *
 * Special Considerations:
 */

package cats.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import javax.swing.JMenu;
import javax.swing.JMenuItem;


/**
 *  This file contains the Sequence class.  It provides a numeric
 *  variable, an accessor method, and a menu item that allows it to
 *  be changed from the menu.  The values are non-sequential integers.
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */

public class Sequence extends JMenu {
  
  /**
   * is the property tag to recognize that the value changed.
   */
  public static final String CHANGE_TAG = "ValueChange";
  
  /**
   * is the list of integers
   */
  private int[] SequenceList;
  
  /**
   * is the menu item selections.
   */
  private SequenceClass[] Selections;
  
  /**
   * is the Bean for alerting listeners when the selection changes.
   */
  private PropertyChangeSupport SequenceBean;
  
  /**
   * is the index of the default value.
   */
  protected int DefaultIndex;
  
  /**
   * is the index of the current value.
   */
  protected int SelectedIndex;
  
  // The tag for this Object in an XML Element
  protected String XMLTag;

  /**
   * initializes the JMenu.
   * @param label is the text on the JMenu
   */
  public Sequence(String label) {
      super(label);
  }
  
  /**
   * constructs the menu items and initializes the current value.
   *
   * @param label is the identifier it has in the menu.
   * @param values is the list of acceptable values.
   * @param first is the initial value.
   * @param xmlTag is the tag for the counter in the XML file
   */
  public Sequence(String label, int[] values, int first,
          String xmlTag) {
      super(label);
      setSequence(values, first);
      SequenceBean = new PropertyChangeSupport(this);
      XMLTag = xmlTag;
  }
  
  /**
   * registers Listeners with the Bean.
   *
   * @param listener is the object that wants to be notified when the
   * value changes.
   */
  public void registerAdjustmentListener(PropertyChangeListener listener) {
      SequenceBean.addPropertyChangeListener(listener);
  }
  
  /**
   * returns the current value.
   *
   * @return the selected numeric value.
   */
  public int getAdjustment() {
      return SequenceList[SelectedIndex];
  }

  /**
   * returns the index of the currently selected value.
   * @return the index
   */
  public int getSelection() {
      return SelectedIndex;
  }
  
  /**
   * select a different entry from the list
   * @param newSelection is the index of the new selection
   */
  public void setSelection(int newSelection) {
      if (newSelection != SelectedIndex) {
          // Enable the current selection.
          Selections[SelectedIndex].setEnabled(true);
          
          // Disable the new selection, to indicate that it is active.
          Selections[newSelection].setEnabled(false);
          SequenceBean.firePropertyChange(CHANGE_TAG, SequenceList[SelectedIndex],
                  SequenceList[newSelection]);
          SelectedIndex = newSelection; 
      }
  }
  
  /**
   * returns the number of possible selections
   * @return the size of the Selection list
   */
  public int getSequenceSize() {
      return Selections.length;
  }
  
  /**
   * stores the values which can be selected.
   * @param values are the values.
   * @param first is the index of the default value.
   */
  public void setSequence(int[] values, int first) {
      Selections = new SequenceClass[values.length];
      DefaultIndex = SelectedIndex = first;
      SequenceList = values;
      for (int i = 0; i < Selections.length; ++i) {
          Selections[i] = new SequenceClass(i);
          Selections[i].setText(String.valueOf(values[i]));
          add(Selections[i]);
      }
      Selections[SelectedIndex].setEnabled(false);        
  }
  
  /**
   * is an inner class for holding each of the possible values.
   */
  private class SequenceClass
  extends JMenuItem {
      
      private int MySetting;
      
      /**
       * is the constructor
       * 
       * @param value is the new value of the adjuster.
       */
      public SequenceClass(int value) {
          MySetting = value;
          addActionListener(new ActionListener() {
              public void actionPerformed(ActionEvent e) {
                  setSelection(MySetting);
              }
          }
          );
      }
  } 
  
  /**
   * returns the XML Element tag for the XMLEleObject.
   *
   * @return the name by which XMLReader knows the XMLEleObject (the Element
   * tag).
   */
  public String getTag() {
      return XMLTag;
  }
  
  /**
   * tells the caller if the state has been saved or not.
   *
   * @return true if the state has not been changed since the last save
   */
  public boolean isSaved() {
      return (SelectedIndex == DefaultIndex);
   }
}
/* @(#)Sequence.java */