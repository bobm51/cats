/* Name: TrackGroup.java
 *
 * What:
 *   This class is the container for describing all the Tracks in a Section.
 */

package cats.layout.items;

import cats.gui.GridTile;
import cats.layout.xml.*;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * is the container for describing all the Tracks in a Section.  Since each
 * Track bridges one edge to another and there are four edges, there can
 * be only 6 possible Tracks (4 things, taken 2 at a time).
 *
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */
public class TrackGroup
    implements Itemizable {

  /**
   *   The XML tag for recognizing the TrackGroup description.
   */
  public static final String XML_TAG = "TRACKGROUP";

  /**
   * the enclosing Section.
   */
  private Section GroupSection;

  /**
   * The Tracks that are in the Section.
   */
  private ArrayList<Track> Tracks;

  /**
   * constructs the Track from scratch.
   */
  public TrackGroup() {
    Tracks = new ArrayList<Track>();
  }

  /*
   * adds the icon for the Track to the GridTile
   */
  public void install(GridTile tile) {
    for (Iterator<Track> i = getTracks(); i.hasNext(); ) {
      i.next().install(tile);
    }
  }

  /*
   * asks the sub-component if it has anything to paint on the Screen.
   *
   * @return true if it does and false if it doen't.
   */
  public boolean isVisible() {
    Track trk;
    for (Iterator<Track> i = getTracks(); i.hasNext(); ) {
      trk = i.next();
      if (trk.getBlock() == null) {
        Point pt = GroupSection.getCoordinates();
        System.out.println("Found a track in Section (" + pt.x + "," +
                           pt.y + ") that is not in a Block.");
      }
      else {
        return trk.getBlock().getVisible();
      }
    }
    return false;
  }

  /**
   * generates an Iterator over the Tracks.
   *
   * @return an Iterator for stepping through the Tracks.
   */
  public Iterator<Track> getTracks() {
    return Tracks.iterator();
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
    return new String(XML_TAG + " XML Elements do not have Text Fields ("
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
    String resultMsg = null;
    if (Track.XML_TAG.equals(objName)) {
      Tracks.add((Track) objValue);
    }
    else {
      resultMsg = new String(XML_TAG +
                             " XML Elements do not have embedded objects ("
                             + objValue + ").");
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

  /*
   * tells the XMLEleObject that no more setValue or setObject calls will
   * be made; thus, it can do any error chacking that it needs.
   *
   * @return null, if it has received everything it needs or an error
   * string if something isn't correct.
   */
  public String doneXML() {
    int vertical = -1;
    int horizontal = -1;
    Track t;
//    if (Tracks.size() == 2) {
      for (int i = 0; i < Tracks.size(); ++i ) {
        t = Tracks.get(i);
        if (t.TrackType == Track.VERTICAL) {
          vertical = i;
        }
        else if (t.TrackType == Track.HORIZONTAL) {
          horizontal = i;
        }
//        else {
//          break;
//        }
      }
      if ((vertical != -1) && (horizontal != -1)) {
        XTrack v = new XTrack(Tracks.get(vertical).TrkSpeed);
        XTrack h = new XTrack(Tracks.get(horizontal).TrkSpeed);
        v.copy(Tracks.get(vertical));
        h.copy(Tracks.get(horizontal));
        v.setCrossTrack(h);
        h.setCrossTrack(v);
        Tracks.set(vertical, v);
        Tracks.set(horizontal, h);
      }
//    }
    return null;
  }

  /*
   * tells the sub-component where its Section is, so that the sub-component
   * can replace itself and retrieve anything else it needs from the Section.
   */
  public void addSelf(Section sec) {
    GroupSection = sec;
    sec.replaceTrackGroup(this);
  }

  /**
   * registers a TrackGroupFactory with the XMLReader.
   */
  static public void init() {
    XMLReader.registerFactory(XML_TAG, new TrackGroupFactory());
    Track.init();
  }
}

/**
 * is a Class known only to the TrackGroup class for creating TrackGroups from
 * an XML document.
 */
class TrackGroupFactory
    implements XMLEleFactory {

  /**
   * the TrackGroup being created.
   */
  private TrackGroup TGroup;

  /*
   * tells the factory that an XMLEleObject is to be created.  Thus,
   * its contents can be set from the information in an XML Element
   * description.
   */
  public void newElement() {
    TGroup = new TrackGroup();
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
    String resultMsg = new String(TrackGroup.XML_TAG +
                                  " XML Elements do not have " + tag +
                                  "attributes.");
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
    return TGroup;
  }
}