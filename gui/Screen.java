/* Name: Screen.java
 *
 * What:
 *	 This file contains the class definition for translating the
 *   TrackPlan to its graphical representation on the Frame.
 *
 * Special Considerations:
 */
package cats.gui;

import cats.gui.CTCcanvas;
import cats.gui.frills.TrainFrill;
import cats.jmri.JmriPrefixManager;
import cats.layout.items.*;
import cats.layout.xml.*;
import cats.rr_events.MousePressEvent;
import cats.rr_events.MouseRelEvent;

import java.util.Iterator;
import java.util.Vector;
import java.util.Enumeration;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.File;
import javax.swing.JOptionPane;

/**
 * is the drawing surface for the dispatcher's panel.
 * <p>
 *   This version of the panel uses no Layout manager.  Thus,
 *   columns are exactly one Tile wide and rows are exactly one
 *   Tile high.  This simplifies in 2 ways:
 * <ol>
 * <li>the user has complete control over how it looks via
 *         the configuration file
 * <li>determining where "line breaks" are is easier
 * </ol>
 * However, tiles need not be the same width.  If all the tiles in a column can
 * be shrunk on the horizontal access, they are.
 * <p>
 * Notice that index 0 is not used.  This is an artifact carried over from
 * the designer program in that index 0 nodes are used as column and row
 * headers.
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2004, 2009, 2010</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 *
 */
public class Screen
    extends MouseAdapter
    implements XMLEleObject {

  /**
   * is the XML element Tag for the matrix of Sections.
   */
  public static final String XML_TAG = "TRACKPLAN";

  /**
   * the XML attribute tag for identifying the number of columns.
   */
  static String ColTag = "COLUMNS";

  /**
   * the XML attribute tag for identifying the number of rows.
   */
  static String RowTag = "ROWS"; // The XML attribute name for the rows

  /**
   * is a Singleton containing the layout description, its contents, and
   * links to where they are drawn.
   */
  public static Screen DispatcherPanel;

  /**
   * is the number of pixels separating horizontal bands of tiles
   */
  private static int Gap = 8;

  /**
   * is the matrix of track Sections that compose the layout.
   */
  private Section[][] MyGrid;

  /**
   * is the row number of the top visible Section in each column.
   */
  private int MinRow[];

  /**
   * is the row number of the bottom visible section in each column.
   */
  private int MaxRow[];

  /**
   * is the number of columns.
   */
  private int MaxX = 0;

  /** is true if the size of the GridTiles should be fixed.  It is false
   * to adjust the size of the GridTiles to fit in the visible Window.
   */
  private boolean FixSize = true;

  /**
   * is used to position the Sections on the Graphics screen.
   * It also provides a shortcut for locating which Section contains
   * a Graphics point.
   *
   * It is organized as bands, with each major dimension being a
   * band - a horizontal rectangle of the screen.  Each minor dimension
   * is also a Graphics Rectangle, containing a column of MyGrid.
   */
  private Rectangle[][] Format;

  /**
   * is the track Sections in each column.
   */
  private Rectangle[] Slice;

  /**
   * is the number of the top row in each slice.
   */
  private int[] TopRow;

  /**
   * is the number of the bottom row in each slice.
   */
  private int[] BottomRow;

  private MouseUser MouseFocus; // Section in which mouse pressed detected

  /**
   * This is a class constructor
   *
   * @param sHeight is the number of columns in the layout
   * @param sWidth is the number of rows in the layout
   */
  public Screen(int sHeight, int sWidth) {

    MyGrid = new Section[sHeight][];
    for (int y = 0; y < sHeight; ++y) {
      MyGrid[y] = new Section[sWidth];
    }
    DispatcherPanel = this;
  }

  /**
   * returns the number of columns and rows in the layout.
   *
   * @return the size of the layout.  This is not its geometry as being
   * painted, but as read in from the XML file, before rows are wrapped.
   */
  public Dimension getLayoutSize() {
    return new Dimension(MyGrid[0].length, MyGrid.length);
  }

  /**
   * instantiates and places a new Section on the Screen.
   *
   * @param y is the row of where the section is to be placed.
   * @param x is the column of where the section is to be placed.
   *
   * @return the new Section, if the coordinates are on the layout
   * and it has not already been instantiated.
   */
  public Section addSection(int y, int x) {
    if ( (y > 0) && (y < MyGrid.length) && (x > 0) && (x < MyGrid[y].length)
        && (MyGrid[y][x] == null)) {
      return (MyGrid[y][x] = new Section(new Point(x, y)));
    }
    return null;
  }

  /**
   * locates a Section on the layout.  This is not the same as its position
   * on the Screen, but its position relative to other Sections.
   *
   * @param x is the x coordinate of the desired Section.
   * @param y is the y coordinate of the desired Section.
   *
   * @return the Section at a particular coordinate location, if it exists.
   */
  public Section locateSection(int x, int y) {
    if ( (y > 0) && (y < MyGrid.length) && (x > 0) && (x < MyGrid[y].length)) {
      return MyGrid[y][x];
    }
    return null;
  }

  /**
   * determines the bounds on the display, based on the visible track.
   */
  public void findBounds() {
    int sWidth = MyGrid[0].length;
    int sHeight = MyGrid.length;
    Section sec;

    // This really doesn't belong here, but it needs to be done.
    PanelSignal.setPalette();

    //  **************  Be sure to collect Tracks into Blocks before
    //  **************  computing MaxX.  First, join the Section edges.
    for (int x = 1; x < sWidth; ++x) {
      for (int y = 1; y < sHeight; ++y) {
        if ( (sec = MyGrid[y][x]) != null) {
          sec.linkEdges();
        }
      }
    }

    // Now, identify the tracks in each Block.
    Block.resolveBlocks();

    // Wire up the Signals.
    for (int x = 1; x < sWidth; ++x) {
      for (int y = 1; y < sHeight; ++y) {
        if ( (sec = MyGrid[y][x]) != null) {
          sec.wireSignals();
        }
      }
    }

    // Determine the column of the rightmost visible Section.
    findMaxColumn:
    for (MaxX = sWidth - 1; MaxX > 0; --MaxX) {
      for (int y = 1; y < sHeight; ++y) {
        // if the Section contains visible track, then bail out.
        if ( (MyGrid[y][MaxX] != null) && MyGrid[y][MaxX].isVisible()) {
          break findMaxColumn;
        }
      }
    }
    // Because column 0 is not used, there will be one extra column.
    ++MaxX;

    Slice = new Rectangle[MaxX];
    TopRow = new int[MaxX];
    BottomRow = new int[MaxX];
    MinRow = new int[MaxX];
    MaxRow = new int[MaxX];
    for (int x = 1; x < MaxX; ++x) {
      // find in which row the lowest numbered visible Section resides
      for (int y = 0; y < MyGrid.length; ++y) {
        if ( (x < MyGrid[y].length) && (MyGrid[y][x] != null) &&
            MyGrid[y][x].isVisible()) {
          MinRow[x] = y;
          break;
        }
      }

      // find in which row the highest numbered visible Section resides
      for (int y = MyGrid.length - 1; y > 0; --y) {
        if ( (x < MyGrid[y].length) && (MyGrid[y][x] != null) &&
            MyGrid[y][x].isVisible()) {
          MaxRow[x] = y;
          break;
        }
      }

      // create the GridTiles for the visible Sections.
      for (int y = MinRow[x]; y <= MaxRow[x]; ++y) {
        if (MyGrid[y][x] != null) {
          MyGrid[y][x].showSec();
        }
      }

      // determine the Dimensions of the Slice in pixels
      if (MinRow[x] <= MaxRow[x]) {
        sHeight = (MaxRow[x] - MinRow[x] + 1) *
        GridTile.getGridSize().height;
        sWidth = 0;
        if (Compression.TheCompressionType.getFlagValue()) {
          for (int y = MinRow[x]; y <= MaxRow[x]; ++y) {
            if ( (x < MyGrid[y].length) && (MyGrid[y][x] != null) &&
                (MyGrid[y][x].getTile() != null)) {
              sWidth = Math.max(sWidth,
                  MyGrid[y][x].getTile().getMinSize().width);
            }
          }
        }
        else {
          sWidth = GridTile.getGridSize().width;
        }
      }
      else {
        sHeight = 0;
        sWidth = GridTile.getGridSize().width;
      }
      Slice[x] = new Rectangle(0, 0, sWidth, sHeight);
    }
  }

  /**
   *   breaks the Grid into "bands"
   *   and locates each "band" on a horizontal row of the Graphics
   *   display.
   * <p>
   *   There are at least two ways to distribute bands.  One is to
   *   see how many will fit horizontally, and insert "line breaks".
   *   The other is to use the current geometry and adjust sizes.
   *   This method will use both, as selected by FixSize.
   *
   *   @param clip describes the clipping area of the Graphics
   */
  public void distribute(Rectangle clip) {
    Dimension d = clip.getSize();
    int tileHeight;
    int left;
    int right;
    int x;
    int y;
    int xoffset;
    int yoffset;
    int bestX = d.width / 2;
//    System.out.println("Screen::distribute() drawing area = " + clip.toString());
    if (FixSize || (Format == null)) {
      /* Identify the logical places for breaking into bands.
       *
       * Start by putting as many Slices in the Band as will fit on
       * half the Screen.  Then add more, remembering which one is
       * the shortest in height.  If there are multiple choices,
       * use the one farthest right.
       */
      Vector<Integer> bands = new Vector<Integer>();
      int pixels;
      bestX = (RowWrap.TheWrapType.getFlagValue()) ? (d.width / 2) :
        d.width - 3;
      for (left = 1; left < MaxX; ) {
        int midX; // the Slice in the middle
        int localMin; // the height of the minimum Slice
        int minX; // the # of the minimum Slice
        pixels = 0;

        TopRow[left] = MyGrid.length;
        BottomRow[left] = 1;
        // first, fill up the left half of the screen
        for (right = left; right < MaxX; ++right) {
          if ((pixels + Slice[right].width) > bestX) {
            --right;
            break;
          }
          pixels += Slice[right].width;
          TopRow[left] = Math.min(TopRow[left], MinRow[right]);
          BottomRow[left] = Math.max(BottomRow[left], MaxRow[right]);
        }

        midX = right;
        if (right < (MaxX - 1)) {
          // walk thru the right half, watching for the minimum
          localMin = MaxRow[right] - MinRow[right];
          minX = right;
          for (++right; right < MaxX; ++right) {
            pixels += Slice[right].width;
            if (pixels > d.width) {
              break;
            }
            if ( (MaxRow[right] - MinRow[right]) <= localMin) {
              localMin = MaxRow[right] - MinRow[right];
              minX = right;
            }
          }
          if (right == MaxX) {
            minX = right - 1;
          }
          for (; midX <= minX; ++midX) {
            TopRow[left] = Math.min(TopRow[left], MinRow[midX]);
            BottomRow[left] = Math.max(BottomRow[left], MaxRow[midX]);
          }
        }
        else {
          minX = MaxX - 1;
        }
        //System.out.println("Band = " + firstRow + " to " + lastRow);
        bands.add(new Integer(minX - left + 1));

        /* Leapfrog minX and do it again */
        left = minX + 1;
      }

      Format = new Rectangle[bands.size()][];
      left = 1;
      y = 0;
      for (Enumeration<Integer> e = bands.elements(); e.hasMoreElements(); ) {
        int wide = e.nextElement().intValue();
        int top = TopRow[left];
        int bottom = BottomRow[left];
        Format[y] = new Rectangle[wide];
        for (x = 0; x < wide; ++x) {
          TopRow[left] = top;
          BottomRow[left] = bottom;
          ++left;
        }
        ++y;
      }
    }
    else { /* determine Section size */
      int wide = d.width / Format[0].length;
      for (y = 0; y < Format.length; ++y) {
        wide = Math.min(wide, d.width / Format[y].length);
      }
      GridTile.setGridSize(new Dimension(wide,
                                         d.height / (Format.length + Gap)));
    }

    /* Finally, adjust either the size or location of each Section */
    tileHeight = GridTile.getGridSize().height;
    x = 0;
    y = 0;
    xoffset = clip.x;
    yoffset = clip.y;
//    xoffset = 0;
//    yoffset = 0;
    for (left = 1; left < MaxX; ++left) {
      int soffset = yoffset;
      Slice[left].x = xoffset;
      Slice[left].y = yoffset;
      /* adjust all Sections in a single Slice */
      for (int i = TopRow[left]; i <= BottomRow[left]; ++i) {
        if ( (left < MyGrid[i].length) && (MyGrid[i][left] != null)
            && MyGrid[i][left].isVisible()) {
          MyGrid[i][left].getTile().setRec(new Rectangle(xoffset,
              soffset, Slice[left].width, tileHeight));
          //System.out.println("Setting tile to :" + xoffset + ',' +
          //soffset + ' ' + tileWidth + 'x' + tileHeight);
        }
        soffset += tileHeight;
      }
      Slice[left].height = soffset - yoffset;
      Format[y][x] = Slice[left];

      /* move to next Slice */
      xoffset += Slice[left].width;
      if ( (++x) == Format[y].length) {
        x = 0;
        xoffset = clip.x;
//        xoffset = 0;
        ++y;
        yoffset = soffset + Gap;
      }
    }
  }

  /**
   *   tests if Rectangle r has any Points
   *   on or within the Band with y.
   *
   *   @param y is the index of the Band
   *   @param r is the Rectangle in question
   *
   *   @return true if any piece of r is in the band
   */
  private boolean inBand(int y, Rectangle r) {
    if ( (Format[y][0].y <= (r.y + r.height)) &&
        ( (Format[y][0].y + Format[y][0].height) >= r.y)) {
      return true;
    }
    return false;
  }

  /**
   *   tests if Rectangle r has any Points
   *   on or within the Slice with xCoord.
   *   No checking is done on the y coordinate. This method assumes
   *   that the y coordinate is correct.
   *
   *   @param slice is the Slice being tested
   *   @param r is the Rectangle in question
   *
   *   @return true if any part of r is within the Slice.
   */
  private boolean inSlice(Rectangle slice, Rectangle r) {
    if ( (slice.x <= (r.x + r.width)) &&
        ( (slice.x + slice.width) >= r.x)) {
      return true;
    }
    return false;
  }

  /**
   * finds what section contains a Point.
   *
   * @param pt is a Point on the screen.
   *
   * @return a Section if it contains pt or null if it does not.
   */
  public Section locatePt(Point pt) {
    int y;
    int slice = 1;
    for (y = 0; y < Format.length; ++y) {
      if ( (Format[y][0].y <= pt.y) &&
          ( (Format[y][0].y + Format[y][0].height) >= pt.y)) {
        int x;
        for (x = 0; x < Format[y].length; ++x) {
          if ( (Format[y][x].x <= pt.x) &&
              ( (Format[y][x].x + Format[y][x].width) >= pt.x)) {
            int row;
            for (row = TopRow[slice]; row <= BottomRow[slice];
                 ++row) {
              if ( (slice < MyGrid[row].length) &&
                  (MyGrid[row][slice] != null) &&
                  (MyGrid[row][slice].getTile() != null) &&
                  MyGrid[row][slice].getTile().contains(pt)) {
                return MyGrid[row][slice];
              }
            }
            break;
          }
          ++slice;
        }
        break;
      }
      slice += Format[y].length;
    }
    return null;
  }

  /**
   * paints the screen (or a part of it).
   * <p>
   * This method determines which Sections are touched by the Rectangle
   * and asks them to redraw themselves.
   *
   * @param g is the graphics object on which to draw the dispatcher panel
   * @param rec is the boundary of the area to paint
   */
  public void paint(Graphics g, Rectangle rec) {
    int slice = 1;
    int row;
    for (int y = 0; y < Format.length; ++y) { /* scan Bands */
      if (inBand(y, rec)) { /* scan Slices in affected Bands */
        for (int x = 0; x < Format[y].length; ++x) {
          if (inSlice(Format[y][x], rec)) { /* repaint Slice */
            for (row = TopRow[slice]; row <= BottomRow[slice];
                 ++row) {
              if ( (slice < MyGrid[row].length) &&
                  (MyGrid[row][slice] != null) && MyGrid[row][slice].isVisible()) {
                MyGrid[row][slice].getTile().paintComponent(g);
              }
            }
          }
          ++slice;
        }
      }
      else {
        slice += Format[y].length;
      }
    }
  }

  /**
   * is the short routine to queue the MouseEvent.
   *
   * @param e describes the mouse button event.
   *
   * @see java.awt.event.MouseEvent
   * 
   * Modified to intercept the first mouse event during a stack request
   * and send it to the calling CPEdge
   */
  public void mousePressed(MouseEvent e) {
    if (CPEdge.StackingCP == null) {      
    MousePressEvent mpe = new MousePressEvent(e);
    mpe.queUp();
    } else {
        CPEdge.StackingCP.setEvent(e);
  }
  }

  /**
   * This is the method called when one of the Mouse keys is
   *   pressed.
   *   This method, in combination with mouseReleased() handles all
   *   mouse button clicks.  However, the pair do not match up presses
   *   and releases of different buttons.
   *
   * @param e describes the key.
   *
   * @see java.awt.event.MouseEvent
   */
  public void mousePressedAction(MouseEvent e) {
    for (Iterator<TrainFrill> i = TrainFrill.getTrainLabels(); i.hasNext(); ) {
      MouseFocus = i.next().mouseDown(e);
      if (MouseFocus != null) {
        return;
      }
    }
    Section sec = locatePt(e.getPoint());
    if (sec != null) {
      MouseFocus = sec.getTile().startMouse(e);
    }
  }

  /**
   * This is the method called when one of the Mouse keys is
   * released.  It queues the MouseEvent.
   *
   * @param e describes the key
   *
   * @see java.awt.event.MouseEvent
   */
  public void mouseReleased(MouseEvent e) {
    MouseRelEvent mre = new MouseRelEvent(e);
//    System.out.println("Screen: mouse release");
    mre.queUp();
  }

  /**
   * This is the method called when one of the Mouse keys is
   * released.
   *
   * @param e describes the key
   *
   * @see java.awt.event.MouseEvent
   */
  public void mouseReleasedAction(MouseEvent e) {
//  		System.out.println("Release at " + e.getPoint().toString());
    if (MouseFocus != null) {
      MouseFocus.finishMouse(e);
    }
    MouseFocus = null;
  }

  /**
   * checks for an instantiated Screen, and if it exists, refreshes
   * it.
   */
  public static void tryRefresh() {
    if (DispatcherPanel != null) {
      DispatcherPanel.redrawScreen();
      GridTile.doUpdates();
    }
  }

  /**
   * queues a request on each GridTile to repaint.
   */
  public void redrawScreen() {
    Section sec;
    GridTile tile;
    for (int y = 1; y < MyGrid.length; ++y) {
      for (int x = 1; x < MyGrid[y].length; ++x) {
        if ( ( (sec = MyGrid[y][x]) != null) &&
            ( (tile = sec.getTile()) != null)) {
          tile.requestUpdate();
        }
      }
    }
  }

  /*
   * is the method through which the object receives the text field.
   *
   * @param eleValue is the Text for the Element's value.
   *
   * @return if the value is acceptable, then null; otherwise, an error
   * string.
   */
  public String setValue(String eleValue) {
    return new String(XML_TAG + " XML Elements do not have Text fields ("
                      + eleValue + ").");
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
  public String setObject(String objName, Object objValue) {
    return null;
  }

  /*
   * returns the XML Element tag for the XMLEleObject.
   *
   * @return the name by which XMLReader knows the XMLEleObject (the
   * Element tag).
   */
  public String getTag() {
    return XML_TAG;
  }

  /**
   * tells the XMLEleObject that no more setValue or setObject calls will
   * be made; thus, it can do any error checking that it needs.
   *
   * @return null, if it has received everything it needs or an error
   * string if something isn't correct.
   */
  public String doneXML() {
//    findBounds();
    return null;
  }

  /**
   * register the Screen with the XMLReader and read in the layout
   * description.
   *
   * @param layout is a File containing the layout description in XML.
   */
  static public void init(File layout) {
    String errReport;
    XMLReader.registerFactory(XML_TAG, new LayoutFactory());
    Section.init();
    if (layout.exists() && layout.canRead()) {
      errReport = XMLReader.parseDocument(layout);
      if (errReport != null) {
        JOptionPane.showMessageDialog( (Component)null,
                                      errReport,
                                      "Description Error",
                                      JOptionPane.ERROR_MESSAGE);
      }
      else {
        DispatcherPanel.findBounds();
        DispPanel.ThePanel.layoutDescribed();
        DispatcherPanel.distribute(DispPanel.ThePanel.getBounds());
        CTCcanvas.Surface.setScreen(DispatcherPanel);
        Block.startUp();
        LockedDecoders.BlackList.pruneSingles();
        JmriPrefixManager.readBack();
        GridTile.InitializationDone();
      }
    }
    else {
      JOptionPane.showMessageDialog( (Component)null,
                                    layout + " does not exist",
                                    "Missing Description",
                                    JOptionPane.ERROR_MESSAGE);
    }
  }
}

/**
 * is a Class known only to the Layout class for creating Layouts from
 * an XML document.  Its purpose is to pick up the number of rows and
 * columns and create the Layout with those dimensions.  Unfortunately,
 * it has more detailed knowledge of Layout than it probably should.
 */
class LayoutFactory
    implements XMLEleFactory {
  private String RowSpec;
  private String ColSpec;

  /*
   * tells the factory that an XMLEleObject is to be created.  Thus,
   * its contents can be set from the information in an XML Element
   * description.
   */
  public void newElement() {
    RowSpec = null;
    ColSpec = null;
  }

  /**
   * gives the factory an initialization value for the created XMLEleObject.
   *
   * @param tag is the name of the attribute.
   * @param value is it value.
   *
   * @return null if the tag:value are accepted; otherwise, an error
   * string.
   */
  public String addAttribute(String tag, String value) {
    String errorCode = null;
    if (tag.equals(Screen.RowTag)) {
      if (RowSpec == null) {
        RowSpec = new String(value);
      }
      else {
        errorCode = new String(tag + " appears to be duplicated in " +
                               Screen.XML_TAG);
      }
    }
    else if (tag.equals(Screen.ColTag)) {
      if (ColSpec == null) {
        ColSpec = new String(value);
      }
      else {
        errorCode = new String(tag + " appears to be duplicated in " +
                               Screen.XML_TAG);
      }
    }
    else {
      System.out.println(tag + " is not a known attribute for " +
                         Screen.XML_TAG);
      log.warn(tag + " is not a known attribute for " +
               Screen.XML_TAG);
    }
    return errorCode;
  }

  /**
   * tells the factory that the attributes have been seen; therefore,
   * return the XMLEleObject created.
   *
   * @return the newly created XMLEleObject or null (if there was a problem
   * in creating it).
   */
  public XMLEleObject getObject() {
    if ( (RowSpec != null) && (ColSpec != null)) {
      int rows = Integer.parseInt(RowSpec) + 1;
      int cols = Integer.parseInt(ColSpec) + 1;
      if (rows < 2) {
        System.out.println(
            "The number of rows in the layout must be greater than 0");
        log.warn("The number of rows in the layout must be greater than 0");
        RowSpec = null;
      }
      else if (cols < 2) {
        System.out.println(
            "The number of columns in the layout must be greater than 0");
        log.warn("The number of columns in the layout must be greater than 0");
        ColSpec = null;
      }
      else {
        // The root layout must be set before its constituents; otherwise,
        // the constituents will be added to the current Layout.
        Screen newLayout = new Screen(rows, cols);
        Screen.DispatcherPanel = newLayout;
        return newLayout;
      }
    }
    return null;
  }

  /**
   * returns the XML Element tag for the XMLEleObject.
   *
   * @return the object described by the XML element
   */
  public String getTag() {
    return Screen.XML_TAG;
  }

  static org.apache.log4j.Logger log = org.apache.log4j.Logger.
      getLogger(
      LayoutFactory.class.getName());
}