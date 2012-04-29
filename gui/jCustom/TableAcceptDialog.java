/* Name: TableAcceptDialog.java
 *
 * What:
 *  This class is an extension to AcceptDialog for StoreEditPanes.
 *  When the Accept button is pushed, it tells the JTable to stop editing.
 *  This action saves the results of the edited cell; otherwise, it is lost.
 */
package cats.gui.jCustom;

import cats.gui.store.StoreEditPane;

/**
 *  This class is an extension to AcceptDialog for StoreEditPanes.
 *  When the Accept button is pushed, it tells the JTable to stop editing.
 *  This action saves the results of the edited cell; otherwise, it is lost.
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */

public class TableAcceptDialog extends AcceptDialog
{
  
  /**
   * is the JTable performing the editing
   */
  private StoreEditPane MyPane;
  
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
  public TableAcceptDialog(StoreEditPane contents, String title) {
    super(contents, title);
    MyPane = contents; 
  }
  
  /**
   * is the cleanup for accepting the changes made on the JDialog contents.
   * It is called from two places - when the Accept button is pushed and
   * when something in the contents simulates pushing the Accept button.
   */
  public void acceptExit() {
    MyPane.stopEditing();
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
  static public boolean select(StoreEditPane contents, String title) {
    TableAcceptDialog dialog = new TableAcceptDialog(contents, title);
    Dialog.pack();
    Dialog.setVisible(true);
    return dialog.Result;
  }
}
