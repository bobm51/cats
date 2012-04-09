/* Name: TrainFrill.java
 *
 * What:
 *   This file defines a class for writing Train identities on the Tile.
 */
package cats.gui.frills;

import cats.gui.DispPanel;
import  cats.gui.GridTile;
import cats.gui.MouseUser;
import cats.gui.jCustom.FontFinder;
import cats.trains.Train;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * TrainFrill defines a class for writing Train identities on the GridTile.
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2004, 2009</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */

public class TrainFrill
    extends LabelFrill
    implements MouseUser {

  /**
   * is the list of visible train labels
   */
  private static ArrayList<TrainFrill> TrainFrillList;
  
  /**
   * is the Train represented by the TrainFrill.
   */
  private Train Owner;

  /**
   * is the default Cursor.
   */
  private Cursor OldCursor;

  /**
   * constructs the TrainFrill.
   *
   * @param label is the String to be written.
   * @param position is where to write the String.
   * @param font is the CtcFont to use
   *
   * @see FrillLoc
   */
  public TrainFrill(String label, FrillLoc position, FontFinder font) {
    super(label, position, font);
    if (TrainFrillList == null) {
      TrainFrillList = new ArrayList<TrainFrill>();
    }
    TrainFrillList.add(this);
  }

  /**
   * tells the TrainFrill where to route Mouse events.
   *
   * @param owner is the Train being represented.
   */
  public void setOwner(Train owner) {
    Owner = owner;
  }

  /**
   * changes the Label shown for the Train's identity.
   *
   * @param newId is the new identity.  It must not be null.
   */
  public void changeId(String newId) {
    Lab = new String(newId);
    MyLabel.setText(newId);
    MyLabel.revalidate();
  }

  /**
   * changes the location of the TrainFrill in the GridTile.
   * @param newAnchor is the location of the upper left corner
   * of the label.
   */
  public void changeLoc(FrillLoc newAnchor) {
    Where = newAnchor;
  }

  /**
   * removes the TrainFrill from the layout (actually, it removes
   * the JLabel and the JVM will eventually garbage collect the Frill).
   */
  public void removeFrill() {
    TrainFrillList.remove(this);
    DispPanel.ThePanel.removeLabel(MyLabel);
  }

  /**
   * modifies the tooltip on the TrainFrill
   *
   * @param tip is the string that shows up on the ToolTip
   */
//  public void addTip(String tip) {
//    MyLabel.setToolTipText(tip);
//  }

  /**
   * is a method for determining if the Mouse is positioned over it
   * when a button is clicked.  If so, and if it accepts the button
   * push, it returns a MouseUser object to handle the button push.
   *
   * @param event is the MouseEvent.
   *
   * @return this, if the Mouse is over the Signal Icon, so that it
   *         can be called when the mouse button is relesed; otherwise,
   *         return null, so that other Frills can be queried.
   */
  public MouseUser mouseDown(MouseEvent event) {
    if (MyLabel.getBounds().contains(event.getPoint())) {
      int mods = event.getModifiers();
      if ( (mods & MouseEvent.BUTTON1_MASK) != 0) {
        OldCursor = ( (Component) DispPanel.ThePanel).getCursor();
        ( (Component) DispPanel.ThePanel).setCursor(new
            Cursor(Cursor.MOVE_CURSOR));
      }
      return this;
    }
    return null;
  }

  /**
   *   This method tells the Object that accepted the Mouse Press
   *   that the user released the mouse button.  In this case,
   *   an event is sent to the associated Train.
   *
   * @param event provides information on the Mouse button release
   *
   */
  public void finishMouse(MouseEvent event) {
    int mods = event.getModifiers();
    if ( (mods & MouseEvent.BUTTON1_MASK) != 0) {
      Owner.trainButton(event);
      GridTile.doUpdates();
    }
    else if (MyLabel.getBounds().contains(event.getPoint())) {
      Owner.trainButton(event);
      GridTile.doUpdates();
    }
    ( (Component) DispPanel.ThePanel).setCursor(OldCursor);
  }
  
  /**
   * is called to get an Iterator over the list of visible
   * train labels.
   * @return an Iterator over TrainFrillList
   */
  public static Iterator<TrainFrill> getTrainLabels() {
    if (TrainFrillList == null) {
      TrainFrillList = new ArrayList<TrainFrill>();
    }
    return TrainFrillList.iterator();
  }
}
/* @(#)TrainFrill.java */