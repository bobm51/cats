/* Name: Crandic.java
 *
<<<<<<< HEAD
 * What: 
 *   The root program for creating the dispatcher program.
=======
 * What:
 *   The root program for creating the dispatcher program.
>>>>>>> a85268e3fa45aed3d4d3c6f3607c88e422a45b9b
 */

package cats.apps;

import apps.Apps;
import cats.common.VersionList;
import cats.gui.DispPanel;
import cats.gui.FontFactory;
import cats.gui.KeyHandler;
import cats.gui.Resizer;
import cats.gui.Screen;
import cats.gui.store.ClassSpec;
import cats.jmri.JmriName;
import cats.jmri.JmriPrefixManager;
import cats.layout.FastClock;
import cats.layout.FontList;
import cats.layout.Hours;
import cats.layout.Logger;
import cats.layout.SignalTemplate;
import cats.layout.store.FieldInfo;
import cats.layout.store.GenericRecord;
import cats.layout.xml.*;
import cats.network.OperationsClient;
import cats.rr_events.RREventManager;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import java.io.File;
import java.net.URL;
import java.text.MessageFormat;

import javax.swing.JOptionPane;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;

import jmri.ConfigureManager;
import jmri.LogixManager;
import jmri.InstanceManager;
import jmri.JmriException;
import jmri.util.JmriJFrame;
import jmri.util.swing.WindowInterface;

/**
 * This is the starting point for creating the dispatcher panel.
 * <P>
 * If an argument is provided at startup, it will be used as the name of
 * the configuration file.  Note that this is just the name, not the path;
 * the file is searched for in the usual way, first in the preferences tree
 * and then in xml/
 * <p>
 * This file is derived from CornwallRR.java, in the jmri repository.  It was
 * originally written by Bob Jacobsen, Copyright 2003, for Nick Kulp.
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2004, 2005, 2006, 2007, 2008, 2009, 2010, 2013, 2016, 2018</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */
public class Crandic
extends Apps {
  
  /**
   * is the XML tag at the beginning of a layout description.
   */
  static final String DocumentTag = "DOCUMENT";
  
  /**
   * is the earliest version of the XML file that will work with this
   * version of CATS.
   */
  static final double EARLY = 0.07;
  
  /**
   * is the latest version of the XML file that will work with this
   * version of CATS.  This should match the version field in the
   * designer.
   */
  static final double LAST = new Double(VersionList.DESIGNER_VERSION);
  
  /**
   * is the version of the panel design program used to create the XML file.
   * 0.07 is the last version to not record the version identity in the
   * file.  So, if the version attribute tag is missing, then the file was
   * 0.07 or before.  It was probably not before because 0.07 was the first
   * version distributed.
   */
  private static String Version = "0.07";
  
  /**
   * is the property tag for specifying the Frame width in pixels.
   */
  private static final String WidthTag = "-WIDTH=";
  
  /**
   * is the property tag for specifying the Frame height in pixels.
   */
  private static final String HeightTag = "-HEIGHT=";
  
  /**
   * is the XML attribute for screen width in pixels
   */
  private static final String MY_WIDTH = "WIDTH";
  
  /**
   * is the XML attribute for screen height in pixels
   */
  private static final String MY_HEIGHT = "HEIGHT";
  
  /**
   * is the XML attribute for the X coordinate of the upper left corner
   */
  private static final String X = "X";
  
  /**
   * is the XML attribute for the Y coordinate of the upper left corner
   */
  private static final String Y = "Y";
  
  /**
   * is the default, initial width of the panel, in pixels.  The
   * width can be specified on the command line or in the layout
   * XML file.
   */
  private static int RowWidth = 640;
  
  /**
   * is the default, initial height of the panel, in pixels.  The
   * height can be specified on the command line or in the layout
   * XML file.
   */
  private static int ColHeight = 400;
  
  /**
   * is the default, initial X coordinate of the upper left corner
   * of the panel, in pixels.  It can be changed in the layout
   * XML file.
   */
  private static int XCoord = 0;
  
  /**
   * is the default, initial Y coordinate of the upper left corner
   * of the panel, in pixels.  It can be changed in the layout
   * XML file.
   */
  private static int YCoord = 0;
  
  /**
   * is the Thread that the RR Event Manager runs in.
   */
  private static Runnable EThread;
  
  /**
   * is the Thread that the movement logger runs in.
   */
  private static Runnable LThread;

  /**
   * is the cursor.  It is saved when the panel is loaded.
   * So, if the cursor can be specified in the XML file,
   * it will be picked up after the file is read.
   */
  private static Cursor MyCursor = Cursor.getDefaultCursor();
  
  /**
   * is the Dispatcher panel drawing area
   */
  private static DispPanel MyPanel;

  /**
   * is the dispatcher window
   */
  private static JFrame DispPanel;
  
  @Override
  protected void createMenus(JMenuBar menuBar, WindowInterface wi) {
    super.createMenus(menuBar, wi);
    menuBar.add(new jmri.jmris.ServerMenu());
  }
  
  /***********************************************************
  protected void systemsMenu(JMenuBar menuBar, JFrame frame) {
      menuBar.add(new jmri.jmrix.ActiveSystemsMenu());
  }
 ****************************************************************/
  
  @Override
  protected String line1() {
    return MessageFormat.format("Generic Dispatcher Panel, based on JMRI {0}",
        new Object[] {jmri.Version.name()});
  }
  
  @Override
  protected String line2() {
    return "http://cats4ctc.org";
  }
  
    /**
     *
     * @return
     */
    @Override
  protected String logo() {
    return "";
  }
  
  Crandic() {
    super();
    log.debug("CTOR done");
    XMLReader.registerFactory(DocumentTag, new DocumentFactory());
  }
  
  /**
   * The world starts here.
   * @param args is the command line arguments.
   */
  // Main entry point
  public static void main(String args[]) {
    String layout = null;
    
    // show splash screen early
    splash(true);
    Apps.setStartupInfo("CATS");

//    initLog4J();
//    log.info(apps.Apps.startupInfo("JMRI"));
    log.info("CATS version " + VersionList.CATS_VERSION);
    setConfigFilename("SimpleConfig2.xml", args);
    Crandic crandic = new Crandic();
    JmriJFrame f = new JmriJFrame("CTC Panel");
    createFrame(crandic, f);
    
    log.info("main initialization done");
    splash(false);
    // start automation
      
      // See if a layout has been specified.
      for (int i = 0; i < args.length; ++i) {
        if (args[i].startsWith(HeightTag)) {
          if (args[i].length() > HeightTag.length()) {
            String height = args[i].substring(HeightTag.length());
            try {
              RowWidth = Integer.parseInt(height);
            }
            catch (NumberFormatException nfe) {
              log.warn("An integer is needed for specifying the Frame height.");
            }
          }
        }
        else if (args[i].startsWith(WidthTag)) {
          if (args[i].length() > WidthTag.length()) {
            String width = args[i].substring(WidthTag.length());
            try {
              ColHeight = Integer.parseInt(width);
            }
            catch (NumberFormatException nfe) {
              log.warn("An integer is needed for specifying the Frame width.");
            }
          }
        }
        else {
          if (layout != null) {
            log.warn("Multiple layouts are being requested!  Using the last.");
          }
          layout = args[i];
        }
      }
      JFrame.setDefaultLookAndFeelDecorated(true);
      // load definitions
      SignalTemplate.init();
      ClassSpec.init();
      new JmriPrefixManager();
      JmriName.init();
      DispPanel = new JFrame("Dispatcher Panel");
      DispPanel.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
      MyPanel = new DispPanel(RowWidth, ColHeight);
      DispPanel.setJMenuBar(MyPanel.createMenus());
      DispPanel.setContentPane(MyPanel);
      DispPanel.addWindowListener(new WindowAdapter() {
        @Override
        public void windowClosed(WindowEvent e) {
        	Logger.finishUp();
        }
      });
      MyPanel.addComponentListener(new Resizer());
      FontFactory.Fonts = new FontFactory(f.getFont());
      FontList.setFontFamily(MyPanel.getFont());
      FontList.instance();
      OperationsClient.instance();
      FastClock.TheClock = new FastClock();
      Hours.HourStore = new Hours();
      FieldInfo.init(GenericRecord.EDITRECORD);
      GenericRecord.init(GenericRecord.DATARECORD);
      EThread = new RREventManager();
      RREventManager.EventThread = new Thread(EThread);
      RREventManager.EventThread.setName("RREventManager");
      RREventManager.EventThread.setDaemon(true);
      RREventManager.EventThread.start();
      RREventManager.EventThread.setPriority(Thread.NORM_PRIORITY);
      
      LThread = Logger.instance();
      Logger.LogThread = new Thread(LThread);
      Logger.LogThread.setName("CATSLogger");
      Logger.LogThread.setDaemon(true);
      Logger.LogThread.start();
      Logger.LogThread.setPriority(Thread.NORM_PRIORITY);
      
      DispPanel.addKeyListener(KeyHandler.ArrowHandler);
      DispPanel.pack();
      DispPanel.setVisible(true);
      if (layout != null) {
        initializePanel(layout);
      }
    }
  
  /**
   * return the version number of the panel design program used to create
   * the XML file.  It can be used to recognize old file formats.
   *
   * @return the identifier for this Version of CATS.
   */
  public static String getVersion() {
    return new String(Version);
  }

  /**
   * loads the layout XML file.  This requires jumping through
   * hoops so that the panel is loaded on the Swing Event
   * Dispatch Thread.
   * @param layout is the XML file that describes the dispatcher panel.
   */
  @SuppressWarnings("Convert2Lambda")
  private static void initializePanel(final String layout) {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        Screen.init(new File(layout));
      }
    });
  }

  /**
   * restores the cursor
   *
   */
  public static void restoreCursor() {
    DispPanel.setCursor(MyCursor);
  }

  /**
   * saves the current cursor.
   */
  public static void saveCursor() {
    MyCursor = DispPanel.getCursor();
  }

  /**
   * confirms that the user wants to end the session and
   * if affirmative, kills the Java VM
   */
  static public void terminateSession() {
	  if (handleQuit()) {
		  System.out.println("CATS shutting down");		  
	  }
  }
  
  class DocumentFactory
  implements XMLEleFactory, XMLEleObject {
    
    /**
     * is the VERSION attribute tag
     */
    private static final String VersionTag = "VERSION";
    
    /**
     * is the include file tag
     */
    private static final String IncludeFileTag = "INCLUDEFILE";

    /**
     * tells the dispatcher panel that it needs to adjust its
     * screen size
     */
    private boolean adjustScreen = false;
    
    /*
     * tells the factory that an XMLEleObject is to be created.  Thus,
     * its contents can be set from the information in an XML Element
     * description.
     */
    @Override
    public void newElement() {
    }
    
    /*
     * gives the factory an initialization value for the created XMLEleObject.
     *
     * @param tag is the name of the attribute.
     * @param value is it value.
     *
     * @return null if the tag:value are accepted; otherwise, an error
     * string.
     */
    @Override
    @SuppressWarnings("ConvertToStringSwitch")
    public String addAttribute(String tag, String value) {
      if (VersionTag.equals(tag)) {
        double v;
        Version = new String(value);
        v = Double.parseDouble(Version);
        if ( (v < Crandic.EARLY) || (v > Crandic.LAST)) {
          JOptionPane.showMessageDialog( (Component)null,
              "The XML file may not be the correct format for this version of CATS.",
              "XML file format warning",
              JOptionPane.WARNING_MESSAGE
          );          
        }
      }
      else if (MY_WIDTH.equals(tag)) {
        int w;
        try {
          w = Integer.parseInt(value);
          RowWidth = w;
          adjustScreen = true;
        }
        catch (NumberFormatException nfe) {
          return new String(value + " is not an legal screen width.");
        }
      }
      else if (MY_HEIGHT.equals(tag)) {
        int h;
        try {
          h = Integer.parseInt(value);
          ColHeight = h;
          adjustScreen = true;
        }
        catch (NumberFormatException nfe) {
          return new String(value + " is not an legal screen height.");
        }
      }
      else if (X.equals(tag)) {
        int x;
        try {
          x = Integer.parseInt(value);
          XCoord = x;
          adjustScreen = true;
        }
        catch (NumberFormatException nfe) {
          return new String(value + " is not an legal X coordinate.");
        }
      }
      else if (Y.equals(tag)) {
        int y;
        try {
          y = Integer.parseInt(value);
          YCoord = y;
          adjustScreen = true;
        }
        catch (NumberFormatException nfe) {
          return new String(value + " is not an legal Y coordinate.");
        }
      }
      else if (IncludeFileTag.equals(tag)) {
    	  // Much of this is based on the JMRI LoadXmlConfigAction.loadFile
    	  //        URL pURL = InstanceManager.configureManagerInstance().find(value);
    	  boolean results = false;
    	  URL pURL = InstanceManager.getNullableDefault(ConfigureManager.class).find(value);
    	  if (pURL!=null) {
    		  try {
    			  //          InstanceManager.configureManagerInstance().load(pURL);
    			  results = InstanceManager.getDefault(ConfigureManager.class).load(pURL);
    			  if (results) {
    				  InstanceManager.getDefault(LogixManager.class).activateAllLogixs();
                      InstanceManager.getDefault(jmri.jmrit.display.layoutEditor.LayoutBlockManager.class).initializeLayoutBlockPaths();
                      new jmri.jmrit.catalog.configurexml.DefaultCatalogTreeManagerXml().readCatalogTrees();
    			  }
    		  } catch (JmriException e) {
    			  log.warn("failed to load " + value + " JMRI panel file");
    		  }
    	  }
    	  else {
    		  log.warn("Could not find "+value+" JMRI panel file");
    	  }
      }
      else {
      return new String(tag + " is not an attribute for a " +
          DocumentTag);
      }
      return null;
    }
    
    /*
     * tells the factory that the attributes have been seen; therefore,
     * return the XMLEleObject created.
     *
     * @return the newly created XMLEleObject or null (if there was a problem
     * in creating it).
     */
    @Override
    public XMLEleObject getObject() {
      return this;
    }
    
    /*
     * is the method through which the object receives the text field.
     *
     * @param eleValue is the Text for the Element's value.
     *
     * @return if the value is acceptable, then null; otherwise, an error
     * string.
     */
    @Override
    public String setValue(String eleValue) {
      return new String(DocumentTag + " cannot have a text field (" +
          eleValue + ").");
    }
    
    /*
     * is the method through which the object receives embedded Objects.
     *
     * @param objName is the name of the embedded object
     * @param objValue is the value of the embedded object
     *
     * @return null if the Object is acceptible or an error String
     * if it is not.
     */
    @Override
    public String setObject(String objName, Object objValue) {
      return null;
    }
    
    /**
     * returns the XML Element tag for the XMLEleObject.
     *
     * @return the name by which XMLReader knows the XMLEleObject (the
     * Element tag).
     */
    @Override
    public String getTag() {
      return new String(DocumentTag);
    }
    
    /**
     * tells the XMLEleObject that no more setValue or setObject calls will
     * be made; thus, it can do any error chacking that it needs.
     *
     * @return null, if it has received everything it needs or an error
     * string if something isn't correct.
     */
    @Override
    public String doneXML() {
      if (adjustScreen) {
        JRootPane jrp = DispPanel.getRootPane();
        jrp.setSize(RowWidth, ColHeight);
        DispPanel.setBounds(XCoord, YCoord, RowWidth, ColHeight);
      }
      return null;
    }
  }
  
  static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(Crandic.class.getName());
}
/* @(#)Crandic.java */
