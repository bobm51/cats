package cats.gui;

import cats.trains.TrainStore;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;
/**
 *  TrainLabel is a Singleton object with a boolean value.  It is set
 *  to true if CATS should try to mark trains on the panel with the engine
 *  number and false if it should be the train symbol.
 * <p>Title: cats</p>
 * <p>Description: A program for designing dispatcher panels</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */


public class TrainLabel extends BooleanGui {

  /**
   * is the XML tag
   */
  static final String XMLTag = "TRAINLABEL";
  
  /**
   * is the label on the JCheckBoxMenuItem
   */
  static final String Label = "Engine Labels";
  
  /** the singleton **/
  public static TrainLabel TrainLabelType;
  
  /**
   * constructs the factory.
   */
  public TrainLabel() {
    super(Label, XMLTag, false);
    this.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, KeyEvent.CTRL_MASK));
    TrainLabelType = this;
  }

  /**
   * is the ActionListener for setting or clearing the flag.
   */

  public void actionPerformed(ActionEvent arg0) {
    TrainStore.TrainKeeper.setTrainLabel(getState());
    Screen.tryRefresh();
  }
}
/* @(#)TrainLabel.java */