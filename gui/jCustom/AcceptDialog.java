/* Name: AcceptDialog.java
 *
 * What:
 *  This class is an extension to JDialog which wraps a JPanel with
 *  an Accept Button and a Cancel Button.  The caller (or sub-class)
 *  supplies the JPanel.  Thus, this class forms a uniform look and feel
 *  for Dialogs.
 */
package cats.gui.jCustom;

import cats.apps.Crandic;
import cats.gui.WindowFinder;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Frame;
import java.awt.Point;

/**
 * is an extension to JDialog, wrapping a JPanel with an Accept JButton
 * and a Cancel JButton.  The class supports a static routine (factory)
 * for constructing the JDialog, displaying the JDialog (in modal model),
 * and returning which JButton was pushed to exit the JDialog.  Thus,
 * this class provides a uniform look and feel for Dialogs.
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */

public class AcceptDialog {

  /**
   *   Result button from JDialog box
   */
  public boolean Result;

  /**
   *   The button to push to change the line width
   */
  protected JButton Accept = new JButton("Accept");

  /**
   *   The button to Cancel the change
   */
  private JButton Cancel = new JButton("Cancel");

  /**
   *   The Buttons, as a group
   */
  private JPanel Buttons = new JPanel();

  /**
   *   The JDialog used for setting the name values.  It is outside
   *   of the constructor so that the anonymous classes can find it.
   */
  public static JDialog Dialog;

  /**
   * is the constructor.  Since the caller provides the JPanel, the
   * caller has control over the contents of the JDialog.
   *
   * @param contents is the JComponent forming the body of the JDialog
   * @param title is a title for the JDialog.
   *
   * @see javax.swing.JDialog
   * @see javax.swing.JButton
   */
  public AcceptDialog(JComponent contents, String title) {
    Dialog = new JDialog( (Frame) null, title, true);
    Container dialogContentPane = Dialog.getContentPane();
    Buttons.add(Accept);
    Buttons.add(Cancel);
    Accept.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        acceptExit();
      }
    });
    Cancel.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        Result = false;
        Dialog.dispose();
      }
    });
    dialogContentPane.setLayout(new BorderLayout());
    dialogContentPane.add("North", contents);
    dialogContentPane.add("South", Buttons);
    Dialog.getRootPane().setDefaultButton(Accept);
  }

  /**
   * is the cleanup for accepting the changes made on the JDialog contents.
   * It is called from two places - when the Accept button is pushed and
   * when something in the contents simulates pushing the Accept button.
   */
  public void acceptExit() {
    Result = true;
    Dialog.dispose();
  }

  /**
   * creates the JDialog, displays it, and tells the caller what closing
   * button was pushed.
   *
   * @param contents is the JPanel forming the body of the JDialog
   * @param title is the title of the JDialog.
   *
   * @return true if the user pushed the Accept button or false if the
   * user pushed the Cancel button.
   */
  static public boolean select(JPanel contents, String title) {
    AcceptDialog dialog = new AcceptDialog(contents, title);
    Dialog.pack();
    Dialog.setVisible(true);
    Crandic.restoreCursor();
    return dialog.Result;
  }

  /**
   * creates the JDialog, positions it, displays it, and tells the caller
   * what closing button was pushed.
   *
   * @param contents is the JComponent forming the body of the JDialog
   * @param title is the title of the JDialog
   * @param pt is the position on the screen of the upper left corner
   *
   * @return true if the user pushed the Accept button or false if the user
   * pushed the Cancel button.
   */
  static public boolean select(JComponent contents, String title, Point pt) {
    AcceptDialog dialog = new AcceptDialog(contents, title);
    Point offset = WindowFinder.getLocation().getLocationOnScreen();
    int x = Math.max(40, pt.x + offset.x - Dialog.getSize().width);
    int y = Math.max(40, pt.y + offset.y - Dialog.getSize().height);
    Dialog.pack();
    Dialog.setLocation(x, y);
    Dialog.setVisible(true);
    Crandic.restoreCursor();
    return dialog.Result;
  }
}
/* @(#)AcceptDialog.java */