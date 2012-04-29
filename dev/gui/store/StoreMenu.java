/* Name: StoreMenu.java
 *
 * What:
 *  This file contains the class definition for an AbstractStoreMenu object.  An
 *  AbstractStoreMenu constructs the menu and sub-menus for editing an
 *  AbstractStore.
 *
 */
package cats.gui.store;

import cats.gui.XmlFileFilter;
import cats.layout.store.AbstractStore;
import cats.layout.xml.XMLReader;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.Component;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;


/**
 *  This file contains the class definition for an AbstractStoreMenu object.  An
 *  AbstractStoreMenu constructs the menu and sub-menus for editing an
 *  AbstractStore.
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A model railroad dispatching program</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * @author Rodney Black
 * @version $Revision$
 */
public class StoreMenu extends JMenu {

  /**
   * is the concrete store to be edited.
   */
  private AbstractStore MyStore;

  /**
   * is the name of the data being edited.
   */
  private String DataKind;

  /**
   * is theEdit MenuItem
   */
  private JMenuItem Edit;
  
  /**
   * the constructor.
   *
   * @param bar is the title on the menu bar.
   * @param title is the kind of contents of the Store.
   * @param store is the Store being edited.
   */
  public StoreMenu(String bar, String title, AbstractStore store) {
    super(bar);
    DataKind = title;
    MyStore = store;

    JMenuItem subMenu;
    subMenu = new JMenuItem("Edit " + title);
    subMenu.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        MyStore.editData();
      }
    });
    Edit = subMenu;
    add(subMenu);
    subMenu = new JMenuItem("Load " + title);
    subMenu.addActionListener(new Load_ActionAdapter());
    add(subMenu);
  }

  /**
   * the constructor with a shortcut.
   *
   * @param bar is the title on the menu bar.
   * @param title is the kind of contents of the Store.
   * @param store is the Store being edited.
   * @param acc is the key accelerator
   */
  public StoreMenu(String bar, String title, AbstractStore store,
      int acc) {
    this(bar, title, store);
    Edit.setAccelerator(KeyStroke.getKeyStroke(acc, KeyEvent.CTRL_MASK));
  }

  /**
   * is an ActionAdapter for the Load MenuItem.
   */
  class Load_ActionAdapter
      implements ActionListener {

    public void actionPerformed(ActionEvent e) {
      int result;
      String errReport;
      XmlFileFilter fFilter = new XmlFileFilter(".xml", "XML file");
      JFileChooser chooser = new JFileChooser(new File("."));
      chooser.addChoosableFileFilter(fFilter);
      chooser.setFileFilter(fFilter);
      chooser.setDialogTitle("Select " + DataKind + " file to read:");
      result = chooser.showOpenDialog(null);
      File fileobj = chooser.getSelectedFile();
      if (result == JFileChooser.APPROVE_OPTION) {
        if (fileobj.exists() && fileobj.canRead()) {
          errReport = XMLReader.parseDocument(fileobj);
          if (errReport != null) {
            JOptionPane.showMessageDialog( (Component)null,
                                          errReport, "Open Error",
                                          JOptionPane.ERROR_MESSAGE);
          }
        }
        else {
          JOptionPane.showMessageDialog( (Component)null, fileobj
                                        + " does not exist", "Open Error",
                                        JOptionPane.ERROR_MESSAGE);
        }
      }
      else if (result == JFileChooser.CANCEL_OPTION) {
        System.out.println("Not opening file for reading");
      }
    }
  }

}
/* @(#)StoreMenu.java */