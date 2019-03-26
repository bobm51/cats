/* Name: Adjuster.java
 *
 * What: This file contains the Adjuster class.  It provides a numeric
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
import javax.swing.JMenuItem;

/**
 *  This file contains the Adjuster class.  It provides a numeric
 *  variable, an accessor method, and a menu item that allows it to
 *  be changed from the menu.  The values are sequential integers.
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2004, 2010</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */
@SuppressWarnings("serial")
public class Adjuster
    extends Sequence {

  /**
   * is the index of the current value.
   */
  private int AdjustIndex;

  /**
   * is the lowest value in the range.
   */
  private int Lowest;

  /**
   * is the menu item selections.
   */
  private AdjustClass[] Selections;

  /**
   * is the Bean for alerting listeners when the selection changes.
   */
  private final PropertyChangeSupport AdjusterBean;
  
  /**
   * constructs the menu items and initializes the Adjust value.  The
   * following relationship should be true, but is not checked:
   * lower <= first <= upper.
   *
   * @param label is the identifier it has in the menu.
   * @param lower is the lowest value.
   * @param upper is the highest value.
   * @param first is the initial value.
   * @param xmlTag is the tag of the counter in the XML file
   */
  public Adjuster(String label, int lower, int upper, int first,
          String xmlTag) {
      super(label);
      setRange(lower, upper, first);
      AdjusterBean = new PropertyChangeSupport(this);
      XMLTag = xmlTag;
  }

  /**
   * registers Listeners with the Bean.
   *
   * @param listener is the object that wants to be notified when the
   * value changes.
   */
  @Override
  public void registerAdjustmentListener(PropertyChangeListener listener) {
    AdjusterBean.addPropertyChangeListener(listener);
  }

  /**
   * returns the current value.
   *
   * @return the selected numeric value.
   */
  @Override
  public int getAdjustment() {
    return AdjustIndex + Lowest;
  }
  
  /**
   * generates the values which can be selected.  The values range
   * from "lower" to "upper" inclusively, in increments of 1.
   * @param lower is the lowest integer that can be selected
   * @param upper is the highest integer that can be selected
   * @param first is the default selection value.
   */
  public void setRange(int lower, int upper, int first) {
      Selections = new AdjustClass[upper - lower + 1];
      DefaultIndex = SelectedIndex = first - lower;
      Lowest = lower;
      for (int i = 0; i < Selections.length; ++i) {
          Selections[i] = new AdjustClass(i);
          Selections[i].setText(String.valueOf(lower + i));
          add(Selections[i]);
      }     
      Selections[SelectedIndex].setEnabled(false);
  }

  /**
   * is an inner class for holding each of the possible values.
   */
  private class AdjustClass
      extends JMenuItem {

    private int MySetting;

    /**
     * is the constructor
     * 
     * @param value is the new value of the adjuster.
     */
    public AdjustClass(int value) {
      MySetting = value;
      addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          // Enable the current selection.
          Selections[AdjustIndex].setEnabled(true);

          // Disable the new selection, to indicate that it is active.
          setEnabled(false);
          AdjusterBean.firePropertyChange(CHANGE_TAG, AdjustIndex + Lowest,
                                         MySetting + Lowest);
          AdjustIndex = MySetting;
        }
      }
      );
    }
  }
}
/* @(#)Adjuster.java */
