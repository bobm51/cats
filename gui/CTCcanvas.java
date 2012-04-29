/*
 * Name: CTCcanvas.java
 *
 * What:
 *   The place where the track diagram is drawn.
 */
package cats.gui;

import java.awt.Graphics;
import javax.swing.JLabel;
import javax.swing.JPanel;

import cats.layout.ColorList;

/**
 * This file contains the Swing component where the track diagram is drawn.
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */

public class CTCcanvas
    extends JPanel {

  /**
   * is the model for painting the trackplan.
   */
  private Screen Diagram;

  /**
   * is the drawing surface.
   */
  static public CTCcanvas Surface;

  /**
   * is the constructor.
   */
  public CTCcanvas() {
    super();
    setOpaque(true);
    Surface = this;
  }

  /**
   * is called to register the dispatcher panel in its
   * singleton.
   * 
   * @param screen is the dispatcher panel.
   */
  public void setScreen(Screen screen) {
    Diagram = screen;
    addMouseListener(screen);
  }

  /**
   * removes a JLabel from the track diagram.
   *
   * @param label is the JLabel.
   */
  public void rmLabel(JLabel label) {
    remove(label);
  }

  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    setBackground(ColorList.safeGetColor(ColorList.BACKGROUND, ColorList.BACKGROUND));
    if (Diagram != null) {
      Diagram.paint(g, g.getClipBounds());
    }
  }
}