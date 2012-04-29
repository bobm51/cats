/* Name: Tile.java
 *
 * What:
 *   This file contains a class for constructing the objects that occupy
 *   a grid square on the screen.  Objects of this sub-class form the body
 *   of the screen.
 */
package cats.gui;

import cats.common.Sides;
import cats.gui.DispPanel;
import cats.gui.frills.Frills;
import cats.gui.jCustom.AcceptDialog;
import cats.layout.items.Section;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.ListIterator;
import java.util.Vector;

/**
 * contains the graphic rectangle that encloses a Section of Track.  Because
 * the Component being drawn on is a JPanel (derived from JComponent), which
 * is part of Swing and Swing uses double buffering, no double buffering
 * is done at this level.
 *
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2004, 2009</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */
public class GridTile
    implements MouseUser {

    /**
     * the next few variables simulate a private, inner class for
     * collecting Screen updates and executing them all at once.
     * <p>
     * The design goes to great lengths to avoid synchronization problems
     * because of activities happening in different threads.  It maintains
     * 2 Vectors of update requests: one is accepting updates while the
     * other is processing updates.  There is a flag that is set if a
     * update completion request arrives while a Vector is being processed.
     * Each GridTile keeps a flag indicating if it has a request queued
     * or not.  This flag is intended to keep multiple entries out of the
     * Vectors.
     */

  /**
   * is the Vector of updates being collected.
   */
  private static Vector<GridTile> ToDoList = new Vector<GridTile>();

  /**
   * is the Vector of updates being processed.
   */
  private static Vector<GridTile> ProcessingList = new Vector<GridTile>();

  /**
   * is the flag indicating that a flush was received while a list
   * was being processed.
   */
  private static boolean MissedFlush = false;

  /**
   * is a flag indicating that the internal data structures are
   * initialized.
   */
  private static boolean Instantiated = false;
  
  /**
   * the size of a GridTile.
   */
  private static Dimension Size = new Dimension(30, 30);

  /**
   * the number of pixels of blank space for a Block boundary.
   */
  static final int BlockGap = 2;

  /**
   * The width text box.
   */
  private static JTextField GwidthText = new JTextField(String.valueOf(Size.
      width), 3);

  /**
   * The height text box.
   */
  private static JTextField GheightText = new JTextField(String.valueOf(Size.
      height), 3);

  /**
   * The label for identifying the width text box.
   */
  private static final JLabel GwidthLab = new JLabel("Grid Width");

  /**
   * The label for identifying the height text box.
   */
  private static final JLabel GheightLab = new JLabel("Grid Height");

  /***************** the rest have an instance in each instantiation ***/

  /**
   * an array of flags - one for each of the four sides of the GridTile.  True
   * means the side forms the end of a Block, so a gap exists between the
   * side of the GridTile and visible track.
   */
  private boolean[] Boundaries = {
      false,
      false,
      false,
      false};

  /**
   * is the list of things painted into the GridTile.
   */
  private ArrayList<Frills> MyFrills;

  /**
   * is the size of the Graphics context in pixels.  This is the size of
   * the instantiated GridTile, which may be different from the static
   * variable Size.
   */
  private Rectangle GBounds;

  /**
   * is the Clipping Rectangle.  It can be different from GBounds because
   * it accounts for showing the gap due to Block boundaries.
   */
  private Rectangle ClipRect;

  /**
   * is the size of the area requested for a repaint.  This is larger
   * than a GridTile because the Frills often spill over in to an
   * adjacent Gridtile.
   */
  private Rectangle PaintBounds;
  
  /**
   * is the Section represented (drawn) by the GridTile.
   */
  private Section MySection;

  /**
   * constructs the grid square and remembers what Section it is displaying.
   *
   * @param section is the Section of track and Items being represented.
   *
   * @see cats.layout.items.Section
   */
  public GridTile(Section section) {
//    super();
    MySection = section;
    MyFrills = new ArrayList<Frills>();
    GBounds = new Rectangle(0, 0, Size.width, Size.height);
    PaintBounds = new Rectangle(GBounds);
  }

  /**
   * returns the default size of a grid square in pixels.
   *
   * @return the current size
   */
  static public Dimension getGridSize() {
    return Size;
  }

  /**
   * sets the default size of a grid square in pixels.
   * @param newSize is the new size of a Grid.
   */
  static public void setGridSize(Dimension newSize) {
    Size = new Dimension(newSize);
  }

  /**
   * returns the current Clipping Rectangle for the GridTile.  This
   * is the area in which Tracks are drawn, though other Frills may
   * spill out of it.
   * @return the clipping rectangle.
   */
  public Rectangle getClip() {
    return ClipRect;
  }

  /**
   * returns the current boundaries of the GridTile.
   *
   * @return GBounds.
   */
  public Rectangle getSize() {
    return GBounds;
  }

  /**
   * returns the Minimum Rectangle that the GridTile could fit in.
   * This method steps through all the Frills that it contains and
   * determines what space they occupy.
   *
   * @return the minimum height and width that is painted.
   */
  public Dimension getMinSize() {
    Dimension defSize = new Dimension(0, 0);
    Dimension testSize;
    for (ListIterator<Frills> iter = MyFrills.listIterator(); iter.hasNext(); ) {
      testSize = iter.next().getDefSize(Size);
      defSize.width = Math.max(defSize.width, testSize.width);
      defSize.height = Math.max(defSize.height, testSize.height);
    }
    return defSize;
  }

  /**
   * tells the GridTile where it is located and how big it is.
   *
   * @param description is describes it.
   */
  public void setRec(Rectangle description) {
//    System.out.println("GridTile: Section " + MySection.getCoordinates().toString() +
//        " is at " + description.toString());
    GBounds = description;
    PaintBounds = new Rectangle(description);
    PaintBounds.width += Size.width;
    ClipRect = new Rectangle(description);
    if (Boundaries[Sides.RIGHT]) {
      ClipRect.width -= BlockGap;
    }
    if (Boundaries[Sides.BOTTOM]) {
      ClipRect.height -= BlockGap;
    }
    if (Boundaries[Sides.LEFT]) {
      ClipRect.width -= BlockGap;
      ClipRect.x += BlockGap;
    }
    if (Boundaries[Sides.TOP]) {
      ClipRect.height -= BlockGap;
      ClipRect.y += BlockGap;
    }
    if (PaintBounds.x >= Size.width) {
      PaintBounds.x -= Size.width;
      PaintBounds.width += Size.width;
    }
    for (ListIterator<Frills> iter = MyFrills.listIterator(); iter.hasNext(); ) {
      iter.next().setDrawing(GBounds, ClipRect);
    }
  }

  /**
   * adds a Frill to the list of decorators for the Tile.
   *
   * @param frill is the Frill being added.
   *
   * @see cats.gui.frills.Frills
   */
  public void addFrill(Frills frill) {
    MyFrills.add(0, frill);
    if ((GBounds != null) && (ClipRect != null)) {
      frill.setDrawing(GBounds, ClipRect);
      frill.getDefSize(Size);
    }
  }

  /**
   * deletes a Frill from the list of decorators for the Tile.
   *
   * @param frill is the Frill being deleted.
   *
   * @see cats.gui.frills.Frills
   */
  public void delFrill(Frills frill) {
    MyFrills.remove(frill);
  }

  /**
   * sets one of the sides to be a block boundary.
   *
   * @param side is the side of the GridTile (see Edge for definitions).
   * @param block is true if the side is a block boundary, false if it is not
   */
  public void setSide(int side, boolean block) {
    Boundaries[side] = block;
  }

  /**
   * fills in the contents of the Tile.
   *
   * @param g is the Graphics context where the Tile appears.
   */
  public void paintComponent(Graphics g) {
    Frills frill;
    for (ListIterator<Frills> iter = MyFrills.listIterator(); iter.hasNext(); ) {
      frill = iter.next();
      frill.decorate(g);
    }
  }

  /**
   * asks the GridTile to alert Swing that it needs to be repainted.
   */
  public void requestUpdate() {
    ToDoList.add(this);
  }

  /**
   * asks the GridTile to alert Swing that it needs to be repainted.
   */
  public void issueUpdate() {
    setRec(GBounds);
    DispPanel.ThePanel.
    repaint(PaintBounds.x, PaintBounds.y, PaintBounds.width,
        PaintBounds.height);
  }

  /**
   * is a request to see if the GridTile contains a Point.
   *
   * @param pt is the Point whose GridTile is being looked for.
   *
   * @return true if the GridTile contains the Point or false if it does not.
   */
  public boolean contains(Point pt) {
    return GBounds.contains(pt);
  }

  /**
   * handles presenting the JDialog when a user requests changing the
   *  Grid size.
   *
   * @return true if the user changes either the height or the width.  false
   * if neither changes.
   */
  static public boolean creatSizeDialog() {
    JPanel panel = new JPanel();
    int oldWidth = Size.width;
    int oldHeight = Size.height;
    JPanel gPanel1 = new JPanel();
    JPanel gPanel2 = new JPanel();
    boolean returnValue = false;
    gPanel1.add(GwidthLab);
    gPanel1.add(GwidthText);
    gPanel2.add(GheightLab);
    gPanel2.add(GheightText);
    panel.add(gPanel1);
    panel.add(gPanel2);
    if (AcceptDialog.select(panel, "Grid Size:")) {
      try {
        int w = Integer.parseInt(GwidthText.getText());
        int h = Integer.parseInt(GheightText.getText());
        if ( (w > 15) && (w < 300) && (h > 15) && (h < 300)) {
          Size = new Dimension(h, w);
//          Ctc.RootCTC.getLayout().showMe();
          GwidthText.setText(String.valueOf(w));
          GheightText.setText(String.valueOf(h));
          returnValue = ( (oldWidth != w) || (oldHeight != h));
        }
        else {
          JOptionPane.showMessageDialog( (Component)null,
                                        "Sizes are out of range.",
                                        "Size Error",
                                        JOptionPane.ERROR_MESSAGE);
        }
      }
      catch (NumberFormatException nfe) {
        JOptionPane.showMessageDialog( (Component)null,
                                      "Sizes must be integers.",
                                      "Size Error",
                                      JOptionPane.ERROR_MESSAGE);
      }
    }
    return returnValue;
  }

  /**
   *   This method asks each Frill in the GridTile if the mouse is
   *   selecting it.  Though Tracks are Frills, they do not accept
   *   mouse selection.  The reason is that it is too difficult to
   *   accurately and easily position the mouse over a line.  Consequently,
   *   if no Frill wants the MouseEvent, it is passed to the enclosing
   *   Section for processing and it can deliver the Mouse to a Track.
   *
   * @param event provides information on the Mouse Press
   *
   * @return  Any Object that wants the Mouse release event; or null
   */
  public MouseUser startMouse(MouseEvent event) {
    MouseUser result = null;
    ListIterator<Frills> iter = MyFrills.listIterator();
//    System.out.println("Section at " + MySection.getCoordinates().toString());
    while (iter.hasNext()) {
      result = iter.next().mouseDown(event);
      if (result != null) {
        break;
      }
    }
    if (result == null) {
      result = this;
    }
    return result;
  }

  /**
   *   This method is called to tell the GridTile that the user released
   *   the Mouse button
   *
   * @param event provides information on the Mouse button release
   */
  public void finishMouse(MouseEvent event) {
    MySection.sectionMouse(event);
    doUpdates();
  }

  /**
   * is called by Screen when the geometry of the Panel has been
   * computed.
   *
   */
  public static void InitializationDone() {
    Instantiated = true;
    doUpdates();
  }
    
  /**
   * is the method called to execute all the updates that have
   * been queued.
   */
  public static void doUpdates() {
    boolean proceed = Instantiated;
    while (proceed) {
      synchronized (ProcessingList) {
        if (ProcessingList.isEmpty()) {
          ProcessingList = ToDoList;
          ToDoList = new Vector<GridTile>();
        }
        else {
          proceed = false;
          MissedFlush = true;
        }
      }
      if (proceed) {
        for (Enumeration<GridTile> e = ProcessingList.elements();
             e.hasMoreElements(); ) {
          e.nextElement().issueUpdate();
        }
        synchronized (ProcessingList) {
          ProcessingList.clear();
          if (!MissedFlush) {
            proceed = false;
            MissedFlush = false;
          }
        }
      }
    }
  }
}