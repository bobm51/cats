/* Name: Section.java
 *
 * What:
 *   This class is the container for all the information within a section
 *   of track, as represented by a GridTIle.
 */

package cats.layout.items;

import cats.common.Sides;
import cats.gui.frills.*;
import cats.gui.GridTile;
import cats.gui.Screen;
import cats.trains.Train;
import cats.layout.xml.*;
import java.awt.event.MouseEvent;
import java.awt.Point;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

/**
 * is a container for all the information within a section of track, as
 * represented by a GridTile.
 * <p>
 * A section holds:
 * <ul>
 * <li>
 *     four edges, RIGHT, BOTTOM, LEFT, and TOP
 * <li>
 *     a list of Tracks which traverse the Section and terminate in the middle
 *     of an edge
 * <li>
 *     Signals for the terminations
 * <li>
 *     an optional station
 * <li>
 *     a label
 * <li>
 *     a filename containing an Image.  This should be mutually exclusive
 *     with all other items, but the mutual exclusion is not enforced.
 * </ul>
 *<p>
 * There are some constraints:
 * <ol>
 * <li>
 *     two tracks need not share a common edge
 * <li>
 *     non-points terminations may also be a block boundary
 * <li>
 *     Blocks may span multiple Sections
 * <li>
 *     A Section may contain Tracks from disjoint Blocks (if they don't share
 *     a common termination edge)
 * <li>
 *     Blocks have more information, such as signal discipline and detector
 *     address.
 * <li>
 *     Signals reside only on block boundaries, protecting entry into the block
 * <li>
 *     Signals have more detailed descriptions (such as number of heads)
 * <li>
 *     Points may be manual or automatic
 * <li>
 *     Points have a description of how to throw them
 * <li>
 *    Points may have a description of the conditions for indicating the
 *    user is throwing them (the computer can trigger the movement,
 *    depending upon the discipline on the block containing the turnout)
 * <li>
 *     Points may have a description of the feedback mechanism
 * <li>
 *     each displayable item within a Section has an Object that implements
 *    Frills
 * </ol>
 *<p>
 *
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2004, 2009, 2011</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */
public class Section
    implements XMLEleObject {

  /**
   * a constant value for indicating something is not in use.
   */
  public static final int UNUSED = -1;

  /**
   * is the XML Tag for Section objects.
   */
  public static final String XML_TAG = "SECTION";

  /**
   * is the XML attribute Tag for the X coordinate.
   */
  static final String X_TAG = "X";

  /**
   * is the XML attribute Tage for the Y coordinate.
   */
  static final String Y_TAG = "Y";

  /**
   * are the coordinates of the Section.
   */
  private Point Coordinates;

  /**
   * is the GridTile where the Section is being painted.
   */
  private GridTile MyTile;

  /**
   *   A list of all the Savable things in the Section.
   */
  private Vector<Itemizable> Contents;

  /**
   * The edges.
   */
  private SecEdge MyEdge[] = new SecEdge[Sides.EDGENAME.length];

  /**
   * The Station, if it exists.
   */
  private Depot MyDepot;

  /**
   * The descriptions of the Tracks.
   */
  private TrackGroup Rails;

  /**
   *  The information about the Section's name.
   */
  private SecName MySecName;

  /**
   * The information for painting an Image.
   */
  private ImageItem MyImage;

  /**
   * is the number of Tracks that reside in the Section.
   */
  private int NumTracks;

  /**
   * is the Current selected Track, if there is more than one.
   */
  private Track SelTrack;

  /**
   * is the Train holding the selected track in the Section.  It is
   * possible for there to be two non-intersecting Tracks, in which
   * case there could be two Trains or there will be ambiguity when
   * moving a Train.  Those problems need to be worked.
   */
  private Train MyTrain;

  /**
   * constructs the contents for a Grid.
   *
   * @param coordinates describe the location of the Section.
   */
  public Section(Point coordinates) {
    Contents = new Vector<Itemizable>();
    Coordinates = coordinates;
    NumTracks = 0;
  }

  /**
   * returns the Coordinates of the Section.
   *
   * @return where on the layout the Section resides.  This is not the
   * location on the Screen, but relative to other Sections.
   */
  public Point getCoordinates() {
    return Coordinates;
  }

  /**
   * retrieves the Section's GridTile.
   *
   * @return the GridTile that the Section writes to.
   */
  public GridTile getTile() {
    return MyTile;
  }

  /**
   * retrieves the requested SecEdge.
   *
   * @param edge is the SecEdge desired.
   *
   * @return the requested SecEdge or null.  null means that edge is invalid
   * or doesn't exist.
   *
   * @see SecEdge
   */
  public SecEdge getEdge(int edge) {
    if ( (edge >= 0) && (edge < Sides.EDGENAME.length)) {
      return MyEdge[edge];
    }
    return null;
  }

  /**
   * adds a SecEdge to the Section.
   *
   * @param newEdge is the SecEdge
   * @param edge is the Edge that it is being added to.
   */
  void replaceEdge(SecEdge newEdge, int edge) {
    if (MyEdge[edge] == null) {
      MyEdge[edge] = newEdge;
      Contents.add(newEdge);
    }
    else {
      log.warn("A SecEdge has multiple definitions in Section " +
               Coordinates.x + "," + Coordinates.y + ".");
    }
  }

  /**
   * retrieves the Section's name information.
   *
   * @return the SecName object containing the section's name information.
   *
   * @see SecName
   */
  public SecName getName() {
    return MySecName;
  }

  /**
   * replaces the Section Name field,
   *
   * @param newName is the new value.
   *
   * @see SecName
   */
  void replaceSecName(SecName newName) {
    if (MySecName == null) {
      MySecName = newName;
      Contents.add(newName);
    }
    else {
      log.warn("Multiple names have been defined for Section " +
               Coordinates.x + "," + Coordinates.y + ".");
    }
  }

  /**
   * replaces the station.
   *
   * @param station is the new station.
   *
   * @see Depot
   */
  void replaceStation(Depot station) {
    if (MyDepot == null) {
      MyDepot = station;
      Contents.add(station);
    }
    else {
      log.warn("Two depots have been defined for Section " +
               Coordinates.x + "," + Coordinates.y + ".");
    }
  }

  /**
   * replaces the Image information.
   *
   * @param image is the new Image file name.
   *
   * @see ImageItem
   */
  void replaceImage(ImageItem image) {
    if (MyImage == null) {
      MyImage = image;
      Contents.add(image);
    }
    else {
      log.warn("Multiple Images have been defined for Section " +
               Coordinates.x + "," + Coordinates.y + ".");
    }
  }

  /**
   * replaces the Tracks.
   *
   * @param group is the replacement group.
   */
  void replaceTrackGroup(TrackGroup group) {
    if (Rails == null) {
      Rails = group;
      Contents.add(group);
    }
    else {
      log.warn("Multiple TrackGroups have been defined for Section " +
               Coordinates.x + "," + Coordinates.y + ".");
    }
  }

  /**
   * sets up the block gaps and shows all icons on the printable screen.
   */
  public void showSec() {
    if (isVisible()) {
      MyTile = new GridTile(this);
      for (int side = 0; side < Sides.EDGENAME.length; ++side) {
        if ( (MyEdge[side] != null) && MyEdge[side].isBlock()) {
          MyTile.setSide(side, true);
        }
      }
      for (Enumeration<Itemizable> e = Contents.elements(); e.hasMoreElements(); ) {
        e.nextElement().install(MyTile);
      }
    }
  }

  /**
   * binds the SecEdges to their mates and across the Section.
   */
  public void linkEdges() {
    for (int edge = 0; edge < MyEdge.length; ++edge) {
      if (MyEdge[edge] != null) {
        MyEdge[edge].bind();
      }
    }
  }

  /**
   * determines if the Section contains anything to put on the Screen.  If
   * so, then a GridTile is allocated and given to all the sub-components.
   *
   * @return true if the Section contains visible track.
   */
  public boolean isVisible() {
    boolean visible = false;
    for (Enumeration<Itemizable> e = Contents.elements(); e.hasMoreElements(); ) {
      if ( e.nextElement().isVisible()) {
        visible = true;
        break;
      }
    }
    return visible;
  }

  /**
   * links the fed Indications to the feeder Indications.
   */
  public void wireSignals() {
    for (int edge = 0; edge < MyEdge.length; ++edge) {
      if (MyEdge[edge] != null) {
        MyEdge[edge].locateFeeder();
      }
    }
  }

  /**
   * handles the mouse button push, when nothing else in the GridTile
   * wants it.
   * <p>
   * For the left Mouse Button, if there is more than 1 Track, then the
   * routine sequences through the Tracks and selects the one that is
   * encountered just prior to the one currently selected.  A selection
   * method is called in it and if it responds to the selection method,
   * then it becomes the selected track.  If it does not respond, then
   * the selected track doesn't change.  This provides a round robin
   * track selection strategy, but does not change things if tracks
   * cannot be selected (for example, the Block is occupied).
   * Note the selection algorithm implementation requires
   * at least two Tracks to work.
   * <p>
   * For the right Mouse Button, a method in the enclosing Block is
   * invoked.  Though rare, this is a little non-deterministic because
   * there are 3 Track combinations where Tracks may not be in the same
   * Block (they don't share a common SecEdge).  In these rare cases, the
   * Block associated with the selected Track is given the method.
   *
   * @param me is the MouseEvent recording the button push/release.
   *
   * @see java.awt.event.MouseEvent
   * @see cats.layout.items.Track
   */
  public void sectionMouse(MouseEvent me) {
    Track previous = null;
    Track trk = null;
    int mods = me.getModifiers();
    if (SelTrack != null) {
      if ( (mods & MouseEvent.BUTTON3_MASK) != 0) {
        SelTrack.getBlock().blockMenu(me.getPoint());
      }
      else if ( (mods & MouseEvent.BUTTON1_MASK) != 0) {
        if ( (NumTracks > 1) && SelTrack.isUnLocked()) {
          for (Iterator<Track> i = Rails.getTracks(); i.hasNext(); ) {
            trk = (i.next());
            if ( (trk == SelTrack) && (previous != null)) {
              break;
            }
            previous = trk;
          }
          if (trk == SelTrack) {
            trk = previous;
          }
          // This won't work when there are points on opposite sides and
          // one is a PtsEdge and the other is an OSEdge.
          if (trk != null) {
        	  trk.selectTrack();
          }
        }
      }
    }
  }

  /**
   * changes the track which is the route through the Section.
   * @param trk is the Track that is the current route.
   */
  public void setSelTrack(Track trk) {
    SelTrack = trk;
  }
  
  /*********** The following methods involve Trains and moving them. ****/
  /**
   * determines if a Track leads from an edge in a direction.
   *
   * @param edge is the initial starting edge.  Null is the same as the center.
   *   so the result is if there is a SecEdge in the direction of travel.
   * @param direction is the direction of movement.
   *
   * @return the SecEdge that is reached by starting at edge and going
   *   in the direction of travel or null if you can't get there from here.
   */
  public SecEdge getPath(SecEdge edge, int direction) {
    SecEdge opposite;
    if (edge == null) {
      return getEdge(direction);
    }
    else if (MyEdge[edge.getEdge()] == edge) {
      if (edge.getEdge() == direction) {
        return edge;
      }
      opposite = edge.traverse();
      if ( (opposite != null) && (opposite.getEdge() == direction)) {
        return opposite;
      }
    }
    return null;
  }

  /**
   * determines if a Train is in the Section or not.
   *
   * @return true if a Train is in the Section and false if there is none.
   */
  public boolean hasTrain() {
    return MyTrain != null;
  }

  /**
   * retrieve any Train in the Section.
   *
   * @return the Train, if there is one; otherwise, null.
   */
  public Train getTrain()
  {
    return MyTrain;
  }

  /**
   * adds a Train to the Section.
   *
   * @param train is the Train being placed.  If null, then an existing
   * Train is removed.
   *
   * @return the station name (if any) where the train has moved to.
   */
  public String addTrain(Train train) {
    String result = null;
    Block selBlock;
    TrainFrill frill;
    if (MyTrain == null) {
      if (train != null) {
        MyTrain = train;
        frill = MyTrain.getIcon();
        if (MyTile != null) {
          MyTile.addFrill(frill);
        }
        if ((SelTrack != null) && ((selBlock = SelTrack.getBlock()) != null)) {
          if (selBlock.getVisible() && selBlock.isDarkTerritory()) {
            selBlock.occupyBlock(true);
          }
          selBlock.incrementTrains();
          result = selBlock.getStationName();
        }
        if (MyTile != null) {
          MyTile.requestUpdate();
        }
      }
    }
    else if (train == null) {
      if (MyTile != null) {
        MyTile.delFrill(MyTrain.getIcon());
      }
      if ((MyTrain != null) && (SelTrack != null) && ((selBlock = SelTrack.getBlock()) != null)) {
        if (selBlock.getVisible() && selBlock.isDarkTerritory()) {
          selBlock.occupyBlock(false);
        }
        selBlock.decrementTrains();
      }
      if (MyTile != null) {
        MyTile.requestUpdate();
      }
      MyTrain = null;
    }
    return result;
  }

  /**
   * is called to determine if a Section unambiguously contains a Station.
   * It unambiguously contains a Station if
   * <ul> it has only one track
   * <li> it has multiple tracks, but only one is "selected" (points are aligned
   * for one route)
   * </ul>
   * @return the Station associated with the Block containing the selected track.
   * A null String is a legal response, if the track is ambiguous or no Station
   * has been defined for the Block.
   */
  public String getStation() {
    Block selBlock;
    if ((SelTrack != null) && ((selBlock = SelTrack.getBlock()) != null)) {
      return selBlock.getStationName();
    }
    return null;
  }
  
  /**
   * describes where the Section is located.
   *
   * @return If the Section has a name,
   * then the name is returned.  If it doesn't and has coordinates on the
   * screen, then those coordinates are returned.  Otherwise, "unkown" is
   * returned.
   */
  public String toString() {
    Point pt;
    if (getName() != null) {
      return new String(getName().getName());
    }
    else if ((pt = getCoordinates()) != null) {
      return new String("(" + pt.x + "," + pt.y + ")");
    }
    return new String("unknown");
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
    return new String(XML_TAG + " XML elements do not have Text Fields.");
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
    String resultMsg = null;
    try {
      ( (Itemizable) objValue).addSelf(this);
    }
    catch (ClassCastException cce) {
      resultMsg = new String(objName + " is not a valid Element in a " + getTag()
                             + ".");
    }
    return resultMsg;
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
   * be made; thus, it can do any error chacking that it needs.
   *
   * @return null, if it has received everything it needs or an error
   * string if something isn't correct.
   */
  public String doneXML() {
    Track trk = null;
    int[] ends;
    SecEdge sEdge;

    // Be sure each Track has two SecEdges.
    if (Rails != null) {
      for (Iterator<Track> i = Rails.getTracks(); i.hasNext(); ) {
        trk = i.next();
        ends = trk.getTrackEnds();
        if (MyEdge[ends[0]] == null) {
          sEdge = new SecEdge(ends[0], null);
          sEdge.addSelf(this);
        }
        if (MyEdge[ends[1]] == null) {
          sEdge = new SecEdge(ends[1], null);
          sEdge.addSelf(this);
        }
        trk.setEdges(MyEdge[ends[0]], MyEdge[ends[1]]);
        if (MyEdge[ends[0]].setDestination(trk) |
            MyEdge[ends[1]].setDestination(trk)) {
          SelTrack = trk;
        }

        // The following are used once things get rolling.
        ++NumTracks;
      }
      if (SelTrack == null) {
        SelTrack = trk;
      }

    }
    return null;
  }

  /**
   * registers the factories for all contained objects with the XMLReader.
   */
  public static void init() {
    XMLReader.registerFactory(XML_TAG, new SectionFactory());
    SecName.init();
    SecEdge.init();
    Depot.init();
    TrackGroup.init();
    ImageItem.init();
  }

  static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(
      Section.class.getName());
}

/**
 * is a Class known only to the Section class for creating Sections from
 * an XML document.  Its purpose is to pick up the coordinates of the Section
 * and add it to the Screen.
 */
class SectionFactory
    implements XMLEleFactory {
  String RowAttr;
  String ColAttr;

  /*
   * tells the factory that an XMLEleObject is to be created.  Thus,
   * its contents can be set from the information in an XML Element
   * description.
   */
  public void newElement() {
    RowAttr = null;
    ColAttr = null;
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
  public String addAttribute(String tag, String value) {
    String resultMsg = null;
    if (tag.equals(Section.X_TAG)) {
      if (ColAttr == null) {
        ColAttr = new String(value);
      }
      else {
        resultMsg = new String(tag + " appears to be duplicated in " +
                               Section.XML_TAG);
      }
    }
    else if (tag.equals(Section.Y_TAG)) {
      if (RowAttr == null) {
        RowAttr = new String(value);
      }
      else {
        resultMsg = new String(tag + " appears to be duplicated in " +
                               Section.XML_TAG);
      }
    }
    else {
      resultMsg = new String(tag + " is not a valid attribute for a " +
                             Section.XML_TAG);
    }
    return resultMsg;
  }

  /*
   * tells the factory that the attributes have been seen; therefore,
   * return the XMLEleObject created.
   *
   * @return the newly created XMLEleObject or null (if there was a problem
   * in creating it).
   */
  public XMLEleObject getObject() {
    Section s;
    if ( (RowAttr != null) && (ColAttr != null)) {
      int row = Integer.parseInt(RowAttr);
      int col = Integer.parseInt(ColAttr);
      if (row < 1) {
        System.out.println(
            "Section row numbers must be greater than 0.");
        log.warn("Section row numbers must be greater than 0.");
        RowAttr = null;
      }
      else if (col < 1) {
        System.out.println(
            "Section column numbers must be greater than 0.");
        log.warn("Section column numbers must be greater than 0.");
        ColAttr = null;
      }
      else {
        if ( (s = Screen.DispatcherPanel.addSection(row, col)) == null) {
          System.out.println("The section at (" + RowAttr + "," + ColAttr +
                             ") is not on the layout.");
          log.warn("The section at (" + RowAttr + "," + ColAttr +
                   ") is not on the layout.");
        }
        else {
          log.debug("Reading row " + row + " column " + col);
          return s;
        }
      }
    }
    return null;
  }

  static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(
      SectionFactory.class.getName());
}
