/* Name: CrewMenu.java
 *
 * What:
 *  This file contains the class definition for an CrewMenu object.  An
 *  CrewMenu constructs the menu and sub-menus for editing an
 *  a list of Crew members.  This is structured similarly to a StoreMenu.
 *  The difference is that StoreMenu uses an XML document reader to load
 *  the abstract store, while this reads a flat file.  Done properly,
 *  StoreMenu would be abstract and there would be two sub-classes: one
 *  for reading XML formatted Stores and one for reading flat formatted
 *  stores.  Since the Crew is the only flat file format, this did not
 *  do things the proper way.
 *
 */
package cats.gui.store;

import cats.crew.Callboard;
import cats.layout.store.AbstractStore;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

/**
 *  This file contains the class definition for a CrewMenu object.  A
 *  CrewMenu constructs the menu and sub-menus for editing an
 *  a list of Crew members.  This is structured similarly to a StoreMenu.
 *  The difference is that StoreMenu uses an XML document reader to load
 *  the abstract store, while this reads a flat file.  Done properly,
 *  StoreMenu would be abstract and there would be two sub-classes: one
 *  for reading XML formatted Stores and one for reading flat formatted
 *  stores.  Since the Crew is the only flat file format, this did not
 *  do things the proper way.
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A model railroad dispatching program</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * @author Rodney Black
 * @version $Revision$
 */
public class CrewMenu extends JMenu {

  /**
   * is the concrete store to be edited.
   */
  private AbstractStore MyStore;

  /**
   * the constructor.
   *
   * @param title is the kind of contents of the Store.
   * @param store is the Store being edited.
   */
  public CrewMenu(String title, AbstractStore store) {
    super(title);
    MyStore = store;

    JMenuItem subMenu;
    subMenu = new JMenuItem("Edit " + title);
    subMenu.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        MyStore.editData();
      }
    });
    subMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_MASK));
    add(subMenu);
    subMenu = new JMenuItem("Load " + title);
    subMenu.addActionListener(new Load_ActionAdapter());
    add(subMenu);
  }

  /**
   * is an ActionAdapter for the Load MenuItem.
   */
  class Load_ActionAdapter
      implements ActionListener {

    public void actionPerformed(ActionEvent e) {
      int result;
      JFileChooser chooser = new JFileChooser(new File("."));
      chooser.setDialogTitle("Select callboard file to read:");
      result = chooser.showOpenDialog(null);
      File fileobj = chooser.getSelectedFile();
      if (result == JFileChooser.APPROVE_OPTION) {
        Callboard.readCrew(fileobj);
      }
      else if (result == JFileChooser.CANCEL_OPTION) {
        System.out.println("Not opening file for reading");
      }
    }
  }

}
/* @(#)CrewMenu.java */
