/* Name: JListDialog.java
 *
 * What:
 *  This class turns an array of Strings into a JList and presents it in
 *  an AcceptDialog.
 */
package cats.gui.jCustom;

import cats.gui.WindowFinder;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import javax.swing.JComponent;
import java.awt.Point;
import java.util.Vector;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * generates a JList from an array of Strings, inserts it into an AcceptDialog
 * and allows the user to select one of the Strings.
 * <p>
 * It constructs the JList from an array of strings (or a Vector), places
 * the JList in an Accept Dialog and displays it.  If the user makes a
 * selection and exits with the accept button or double clicks on an item,
 * the index of the selection is returned.  If it is called with a Vector
 * argument, then the Vector is converted to a JList.  The string form
 * should be used if there are a small number of selections, because
 * the method attempts to put a scroll bar around a vector.  If the number
 * of selections is small, there is a lot of wasted vertical space.
 *
 * This code is ugly.  The relationship between it and AcceptDialog could
 * be better isolated.  I would like to fix it when I get some more features
 * working.
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2004, 2009</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */

public class JListDialog {

  /**
   * is the AcceptDialog wrapper around the list.
   */
  private AcceptDialog AD;

  /**
   * is the JList presented to the user.
   */
  private JList TheList;

  /**
   * the consructor.
   *
   * @param body is the body of the AcceptDialog
   * @param contents is the JList contaiing the items being selected.  It
   *    must be included in body.
   * @param title is the title of the JDialog.
   * @param pt is the coordinates of the where the AcceptDialog
   *        should be placed on the screen.
   *
   */
  public JListDialog(JComponent body, JList contents, String title, Point pt) {
    TheList = contents;
    AD = new AcceptDialog(body, title);
    Point offset = WindowFinder.getLocation().getLocationOnScreen();
    int x;
    int y;
    TheList.setSelectedIndex( -1);
    TheList.addMouseListener(new Clicker(TheList, AD));
    JDialog dialog = AcceptDialog.Dialog;
	dialog.pack();
    x = Math.max(40, pt.x + offset.x - dialog.getSize().width);
    y = Math.max(40, pt.y + offset.y - dialog.getSize().height);
    dialog.setLocation(x, y);
    dialog.setVisible(true);
  }

  /**
   * creates the JList, displays it, and tells the calling program
   * what happened.
   *
   * @param contents is the list of strings forming the body of the JList
   * @param title is the title of the JDialog.
   * @param pt is the coordinates of the where the AcceptDialog
   *        should be placed on the screen.
   *
   * @return the index of the item selected.
   *
   * @see javax.swing.JList
   * @see cats.gui.jCustom.AcceptDialog
   */
  static public int select(String[] contents, String title, Point pt) {
    JList list = new JList(contents);
    JListDialog dialog = new JListDialog(list, list, title, pt);
    if (dialog.AD.Result) {
      return dialog.TheList.getSelectedIndex();
    }
    return -1;
  }

  /**
   * creates the JList, displays it, and tells the calling program
   * what happened.
   *
   * @param caption are strings to convert to JLabels
   * @param contents is the list of strings forming the body of the JList
   * @param title is the title of the JDialog.
   * @param pt is the coordinates of the where the AcceptDialog
   *        should be placed on the screen.
   *
   * @return the index of the item selected.
   *
   * @see javax.swing.JList
   * @see cats.gui.jCustom.AcceptDialog
   */
  static public int select(String[] caption, String[] contents, String title,
                           Point pt) {
    JPanel fixed = new JPanel();
    JList list = new JList(contents);
    JPanel panel = new JPanel();
    JListDialog dialog;
    fixed.setLayout(new BoxLayout(fixed, BoxLayout.Y_AXIS));
    list.setSelectedIndex( -1);
    for (int e = 0; e < caption.length; ++e) {
      fixed.add(new JLabel(caption[e]));
    }
    panel.add(fixed);
    panel.add(list);
    dialog = new JListDialog(panel, list, title, pt);
    if (dialog.AD.Result) {
      return dialog.TheList.getSelectedIndex();
    }
    return -1;
  }

  /**
   * creates the JList, displays it, and tells the calling program
   * what happened.
   *
   * @param contents is the list of strings forming the body of the JList
   * @param title is the title of the JDialog.
   * @param pt is the coordinates of the where the AcceptDialog
   *        should be placed on the screen.
   *
   * @return the index of the item selected.
   *
   * @see javax.swing.JList
   * @see cats.gui.jCustom.AcceptDialog
   */
  static public int select(Vector<String> contents, String title, Point pt) {
    JListDialog dialog;
    JList list = new JList(contents);
    JScrollPane jsp = new JScrollPane(list);
    list.setSelectedIndex( -1);
    dialog = new JListDialog(jsp, list, title, pt);
    if (dialog.AD.Result) {
      return dialog.TheList.getSelectedIndex();
    }
    return -1;
  }

  /**
   * a mouseListener to handle double clicks.
   */
  class Clicker
      extends MouseAdapter {

    /**
     * the list from which a selection is being made
     */
    private JList DList;

    /**
     * is the AcceptDialog wrapping the list.
     */
    private AcceptDialog ADialog;

    /**
     * constructor
     *
     * @param list is the JList being monitored.
     * @param aDialog is the AcceptDialog wrapping the list.
     */
    public Clicker(JList list, AcceptDialog aDialog) {
      DList = list;
      ADialog = aDialog;
    }

    /**
     * handles a mouse click.
     *
     * @param me is the AWT event.
     */
    public void mouseClicked(MouseEvent me) {
      if (me.getClickCount() > 1) {
        DList.setSelectedIndex( ( (JList) me.getSource()).locationToIndex(
            me.getPoint()));
        ADialog.acceptExit();
      }
      else {
        DList.setSelectedIndex( ( (JList) me.getSource()).locationToIndex(
            me.getPoint()));
      }
    }
  }
}