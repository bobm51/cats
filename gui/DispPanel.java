/* Name: DispPanel.java
 *
 * What:
 *  This is the root of the screens displayed.
 */
package cats.gui;

import cats.apps.Crandic;
import cats.crew.Callboard;
import cats.gui.jCustom.ManagerEditPane;
import cats.gui.store.CrewMenu;
import cats.gui.store.LawPane;
import cats.gui.store.StoreMenu;
import cats.gui.tests.*;
import cats.gui.trains.*;
import cats.jmri.JmriPrefixManager;
import cats.jobs.JobStore;
import cats.layout.ColorList;
import cats.layout.FlashRate;
import cats.layout.FontList;
import cats.layout.Hours;
import cats.layout.OccupancySpectrum;
import cats.layout.items.Block;
import cats.layout.items.PtsEdge;
import cats.layout.replay.ReplayHandler;
import cats.trains.TrainStore;
import cats.layout.Logger;
import cats.network.OperationsClient;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.filechooser.FileFilter;

/**
 * is the root object for the GUI.  It contains the menu bar, the menu
 * items, and the main drawing surface.
 *
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2004, 2009, 2011</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */

public class DispPanel
    extends JPanel {
  /**
   * this table is used to construct the default value for the movement log.
   * It is a three character abbreviation of each month.
   */
  private static final String Month[] = {
      "jan",
      "feb",
      "mar",
      "apr",
      "may",
      "jun",
      "jul",
      "aug",
      "sep",
      "oct",
      "nov",
      "dec"
  };

  /**
   * is the singleton which is the Dispatcher Panel.
   */
  public static DispPanel ThePanel;

  /**
   * is the JLayeredPane on which the track plan is drawn and labels
   * placed.
   */
  public JLayeredPane Layers;

  /**
   * the menu item under Files for opening a layout definition.  It
   * is enabled if no definition has been selected and is disabled
   * when one is successfully read in.
   */
  private JMenuItem jMenuFileOpen;

  /**
   * the menu item under Files for requesting the name of the log
   * file.  It is enabled or disabled, depending upon if logging is
   * disabled or enabled (it is enabled if logging has not been started
   * and disabled if logging has been started).
   */
  private JMenuItem jMenuFileSave;

  /**
   * the menu item under Files for requesting the name of a replay
   * log.  It is disabled until the layout has been read in.
   */
  private JMenuItem jMenuFileReplay;
  /**
   * the menu item under Appearance for changing fonts.  It needs an
   * explicit name because sub-menus are added to it after it is
   * created.  These deferred actions are because the default font is
   * not known at the time it is created.
   */
//  private JMenu jMenuAppFonts;

  /**
   * is a string for holding a file name.  Initially, it holds the name
   * of the configuration file.  After the configuration file is loaded,
   * it holds the name of the movement log file.
   */
  private String FileName;

 /**
   * constructs the frame.
   *
   * @param width is the initial width of the panel in pixels.
   * @param height is the initial height of the panel in pixels.
   */
  public DispPanel(int width, int height) {
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    ThePanel = this;
    WindowFinder.setLocation(this);
    try {
      jbInit(width, height);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * builds the GUI.
   *
   * @param width is the initial size of the panel in pixels.
   * @param height is the initial height of the panel in pixels.
   * @throws Exception
   */
  private void jbInit(int width, int height) throws Exception {
    Layers = new JLayeredPane();
    Layers.setPreferredSize(new Dimension(width, height));
    Layers.add(new CTCcanvas(), new Integer(0));
    CTCcanvas.Surface.setSize(width, height);
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    add(Layers);
  }


  /**
   * adds a MenuItem to the Fonts pulldown.
   *
   * @param item is the MenuItem.  It should already have an ActionListener
   * attached to it.
   */
//  public void addFontItem(JMenuItem item) {
//    jMenuAppFonts.add(item);
//  }

  /**
   * File | Open menu item handler.
   * <p>
   * This method is used to read in an existing layout configuration from a
   * file.  It presents a browse window for selecting a file.  If one is
   * selected, then the open option is disabled and the save option is
   * enabled.
   *
   * @param e is the event triggering this method.
   */
  void jMenuFileOpen_actionPerformed(ActionEvent e) {
    int result;
    XmlFileFilter fFilter = new XmlFileFilter(".xml", "XML file");
    JFileChooser chooser = new JFileChooser(new File("."));
    chooser.addChoosableFileFilter(fFilter);
    chooser.setFileFilter(fFilter);
    chooser.setDialogTitle("Select configuration file to read:");
    result = chooser.showOpenDialog(null);
    File fileobj = chooser.getSelectedFile();
    if (result == JFileChooser.APPROVE_OPTION) {
      Screen.init(fileobj);
    }
    else if (result == JFileChooser.CANCEL_OPTION) {
      System.out.println("Not opening file for reading");
    }
  }

  /**
   * File | Save menu item handler.
   * <p>
   * This method is used to initiate saving the history log.
   *
   * @param e is the event triggering this method.
   */
  void jMenuFileSave_actionPerformed(ActionEvent e) {
    saveLog();
  }
  /**
   * handles saving train movements to a file.  It pops up a file
   * selection dialog.  If a file is named, it is used and the file
   * can be neither closed not reopened until CATS ends.
   *
   * @return true if the user wants to continue or false to cancel the
   *         operation.
   */
  private boolean saveLog() {
    Calendar now = Calendar.getInstance();
    FileName = new String(Month[now.get(Calendar.MONTH)] +
                          now.get(Calendar.DATE) + ".data");
    if ( (FileName = saveLog(FileName)) != null) {
      Logger.instance().setFileName(FileName);
      jMenuFileSave.setEnabled(false);
    }
    return true;
  }

  /**
   * handles the user interface for selecting a file to save the log in,
   * checking the file, and initiating the log.
   *
   * @param title1 is the title for the file selection Dialog
   *
   * @return the fully qualified filename, or null.
   */
  private String saveLog(String title1) {
    int result;
    JFileChooser chooser = new JFileChooser(new File("."));
    chooser.setDialogTitle(title1);
    result = chooser.showSaveDialog(null);
    File fileobj = chooser.getSelectedFile();
    if (result == JFileChooser.APPROVE_OPTION) {
      if (fileobj.exists()) {
        if (fileobj.isFile()) {
          result = JOptionPane.showConfirmDialog( (Component)null,
                                                 "Overwrite " + fileobj.getName()
                                                 + "?",
                                                 "Overwrite File?",
                                                 JOptionPane.YES_NO_OPTION);
          if (result == JOptionPane.YES_OPTION) {
            if (!fileobj.delete()) {
              JOptionPane.showMessageDialog( (Component)null,
                                            fileobj.getName() +
                                            " could not be deleted",
                                            "Deletion Error",
                                            JOptionPane.ERROR_MESSAGE);
              return null;
            }
          }
          else {
            return null;
          }
        }
        else {
          JOptionPane.showMessageDialog( (Component)null,
                                        fileobj.getName() + " is not a file",
                                        "File Error",
                                        JOptionPane.ERROR_MESSAGE);
          return null;
        }
      }
    }
    else {
      return null;
    }
    try {
      if (!fileobj.createNewFile()) {
        JOptionPane.showMessageDialog( (Component)null,
                                      fileobj.getName() +
                                      " could not be created.",
                                      "File Error",
                                      JOptionPane.ERROR_MESSAGE);
        return null;
      }
    }
    catch (IOException except) {
      JOptionPane.showMessageDialog( (Component)null,
                                    "Error creating " + fileobj.getName(),
                                    "File Error",
                                    JOptionPane.ERROR_MESSAGE);
      return null;
    }
    return new String(fileobj.getAbsolutePath());
  }

  /**
   * File | Replay menu item handler.
   * <p>
   * This method is used to read in an existing log from a
   * file.  It presents a browse window for selecting the log.
   *
   * @param e is the event triggering this method.
   */
  void jMenuFileReplay_actionPerformed(ActionEvent e) {
    int result;
    String msg;
    JFileChooser chooser = new JFileChooser(new File("."));
    chooser.setDialogTitle("Select log file to read:");
    result = chooser.showOpenDialog(null);
    File fileobj = chooser.getSelectedFile();
    if (result == JFileChooser.APPROVE_OPTION) {
      msg = ReplayHandler.playback(fileobj);
      if (msg != null) {
        System.out.println(msg);
      }
    }
    else if (result == JFileChooser.CANCEL_OPTION) {
      System.out.println("Not opening file for reading");
    }
  }


  /**
   * adds a JLabel to the screen.
   *
   * @param label is the JLabel being added.
   */
  public void addLabel(JLabel label) {
    Layers.add(label, new Integer(2));
  }

  /**
   * removes a JLabel from the screen.
   *
   * @param label is the JLabel being removed.
   */
  public void removeLabel(JLabel label) {
    Layers.remove(label);
  }

  /**
   * disables the File->Open menu item and enables the File->Save menu
   * item when a layout description is successfully read in.
   */
  public void layoutDescribed() {
    jMenuFileOpen.setEnabled(false);
    jMenuFileSave.setEnabled(true);
    jMenuFileReplay.setEnabled(true);
//    Screen.DispatcherPanel.distribute(getBounds());
  }

  /**
   * constructs the menubar and its sub-menus
   * @return the newly constructed menubar.
   */
  public JMenuBar createMenus() {
    JMenuBar jMenuBar = new JMenuBar();
    JMenu jMenu;
    JMenu item;
    JMenuItem jMenuItem;
    JPanel panel;
    TraceFactory tFactory;
    CounterFactory cFactory;
    FlashRate rFactory;

    // The Files drop down.
    jMenu = new JMenu("File");
    jMenuBar.add(jMenu);
    jMenuFileOpen = new JMenuItem("Open");
    jMenuFileOpen.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jMenuFileOpen_actionPerformed(e);
      }
    });
    jMenu.add(jMenuFileOpen);

    jMenuFileSave = new JMenuItem("Start Recording");
    jMenuFileSave.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        saveLog();
      }
    });
    jMenuFileSave.setEnabled(false);
    jMenu.add(jMenuFileSave);

    // add the Replay menu item
    jMenuFileReplay = new JMenuItem("Replay");
    jMenuFileReplay.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e) {
        jMenuFileReplay_actionPerformed(e);
      }
    });
    jMenuFileReplay.setEnabled(false);
    jMenu.add(jMenuFileReplay);
    
    jMenu.addSeparator();
    jMenuItem = new JMenuItem("Exit");
    jMenuItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        int result = JOptionPane.showConfirmDialog( (Component)null,
            "Do you really want to end the session?",
            "Choose yes or no",
            JOptionPane.YES_NO_OPTION
            );
        if (result ==JOptionPane.YES_OPTION) {
          Logger.finishUp();
          System.exit(0);
        }
      }
    });
    jMenu.add(jMenuItem);

    //The Appearance drop down
    jMenu = new JMenu("Appearance");
    jMenuBar.add(jMenu);
    new PaletteFactory();  // for backwards compatibility
//    item.setText("Colors");
//    jMenu.add(item);
    jMenuItem = new JMenuItem("Colors ...");
    jMenuItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (ManagerEditPane.editList(ColorList.instance().createModel())) {
          ColorList.instance().commit();
          Screen.tryRefresh();
          ThePanel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
          Crandic.saveCursor();
        }
      }
    });
    jMenu.add(jMenuItem);
    
//    jMenuAppFonts = new JMenu("Fonts");
//    jMenu.add(jMenuAppFonts);
    jMenuItem = new JMenuItem("Fonts ...");
    jMenuItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (ManagerEditPane.editList(FontList.instance().createModel())) {
          FontList.instance().commit();
          Screen.tryRefresh();
          ThePanel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
          Crandic.saveCursor();
        }
      }
    });
    jMenu.add(jMenuItem);

    jMenuItem = new JMenuItem("Grid Size");
    jMenu.add(jMenuItem);
    jMenuItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (GridTile.creatSizeDialog()) {
          // recompute the display based on the new grid size
        }
      }
    });

    item = (new LineFactory());
    item.setText("Line Widths");
    jMenu.add(item);

    jMenuItem = new JMenuItem("Refresh Screen");
    jMenu.add(jMenuItem);
    jMenuItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        Screen.tryRefresh();
        ThePanel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        Crandic.saveCursor();
      }
    });

    jMenuItem = new JMenuItem("Refresh Layout");
    jMenu.add(jMenuItem);
    jMenuItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        PtsEdge.restoreAlignment();
        Block.startUp();
        JmriPrefixManager.readBack();
      }
    });

    item = new JMenu("Test Layout");
    jMenu.add(item);
    item.add(new SignalTest("Signal Indication Test"));
    item.add(new AspectTest("Signal Aspect Test"));
    jMenuItem = new JMenuItem("Lock Light Test");
    item.add(jMenuItem);
    jMenuItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        PtsEdge.testLockLights(true);
      }
    });
    jMenuItem = new JMenuItem("Unlock Light Test");
    item.add(jMenuItem);
    jMenuItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        PtsEdge.testLockLights(false);
      }
    });
    
    jMenuItem = new JMenuItem("Throw Turnout Test");
    item.add(jMenuItem);
    jMenuItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        PtsEdge.testThrown(true);
      }
    });
    
    jMenuItem = new JMenuItem("Close Turnout Test");
    item.add(jMenuItem);
    jMenuItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        PtsEdge.testThrown(false);
      }
    });
    
    jMenuItem = new JMenuItem("Set Turnouts to Normal");
    item.add(jMenuItem);
    jMenuItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        PtsEdge.setAllNormal();
      }
    });

    tFactory = new TraceFactory("Trace Items");
    jMenu.add(tFactory);

    cFactory = new CounterFactory("Adjustments");
    jMenu.add(cFactory);
    OccupancySpectrum.instance();

    jMenu.add(new TrainLabel());
    jMenu.add(new Tracker());
    jMenu.add(new LightMast());
    jMenu.add(new DirectionArrow());
    jMenu.add(new RowWrap());
    jMenu.add(new DecoderInterlock());
    jMenu.add(new LocalEnforcement());
    
    // Compression is created so that its factory is registered, but
    // it is not a live object because the screen columns are computed
    // when the file is loaded and not again.
    jMenuItem = new Compression();
 
    rFactory = new FlashRate("Flash Rate");
    rFactory.turnOn(false);
    jMenu.add(rFactory);

    jMenu = new JMenu("Network");
    jMenuBar.add(jMenu);
    jMenu.add(NetworkStatus.instance());
    jMenu.addSeparator();
    panel = new JPanel();
    panel.add(new JLabel("Server Port:"));
    panel.add(ServerPort.instance());
    jMenu.add(panel);
    jMenu.add(StartTrainStat.instance());
    jMenu.add(ClientRefresh.instance());
    jMenu.addSeparator();
    jMenuItem = new JMenuItem("Operations ...");
    jMenuItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
    	  if (NetworkAddressDialog.select(OperationsClient.instance(), "Operations IP Address")) {
    		  OperationsClient.instance().reconcileConnection();
    	  }
      }
    });
    jMenu.add(jMenuItem);
    
    jMenu.addMenuListener(
        new MenuListener() {
          public void menuSelected(MenuEvent arg0) {
            NetworkStatus.instance().update();
          }

          public void menuDeselected(MenuEvent arg0) {
          }

          public void menuCanceled(MenuEvent arg0) {
          }});
    
    // The Trains drop down
    jMenu = new StoreMenu("Trains", "Lineup", TrainStore.TrainKeeper,
        KeyEvent.VK_T);
    jMenuBar.add(jMenu);

    jMenuItem = new JMenuItem("Rerun Train");
    jMenu.add(jMenuItem);
    jMenuItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        TrainPicker.rerunTrain();
      }
    });

    // The Crew drop down
    jMenu = new CrewMenu("Crew", Callboard.Crews);
    jMenuBar.add(jMenu);
    jMenuItem = new JMenuItem("Legal Hours");
    jMenuItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ae) {
        LawPane.editHours(Hours.getHours());
      }
    });
    jMenu.add(jMenuItem);

    // The Jobs drop down
    jMenu = new StoreMenu("Jobs", "Jobs", JobStore.JobsKeeper,
        KeyEvent.VK_J);
    jMenuBar.add(jMenu);

    // The Help pulldown
    jMenu = new JMenu("Help");
    jMenuBar.add(jMenu);

    jMenuItem = new JMenuItem("About");
    jMenu.add(jMenuItem);
    jMenuItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        DispPanel_AboutBox dlg = new DispPanel_AboutBox();
        Dimension dlgSize = dlg.getPreferredSize();
        Dimension frmSize = getSize();
        Point loc = getLocation();
        dlg.setLocation( (frmSize.width - dlgSize.width) / 2 + loc.x,
                        (frmSize.height - dlgSize.height) / 2 + loc.y);
        dlg.setModal(true);
        dlg.pack();
        dlg.setVisible(true);
      }
    });
    return jMenuBar;
  }

  /**
   * is a filter for selecting certain file suffixes.
   */
  class XmlFileFilter extends FileFilter {
    private String Desc = null;
    private String Ext = null;

    /**
     * is the constructor.
     *
     * @param extension is the file extension being matched.
     * @param description is a text description of files with that
     * extension.
     */
    public XmlFileFilter(String extension, String description) {
      Desc = description;
      Ext = extension;
    }

    /**
     * retrieves the description information.
     *
     * @return the description of files with teh extension.
     */
    public String getDescription() {
      return new String(Desc);
    }

    /**
     * is the actual filter.
     *
     * @param f is the file name being tested.
     *
     * @return true if it passes the filter.
     */
    public boolean accept(File f) {
      if (f == null) {
        return false;
      }
      if (f.isDirectory()) {
          return true;
      }
      return f.getName().toLowerCase().endsWith(Ext);
    }
  }
}
