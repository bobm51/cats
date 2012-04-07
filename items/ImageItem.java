/* Name: ImageItem.java
 *
 * What:
 *  ImageItem is the name of a file containing an Image to be painted in
 *  a GridTile.  This is pretty simplistic - no sizing is performed.
 */
package cats.layout.items;

import cats.gui.GridTile;
import cats.gui.frills.ImageFrill;
import cats.layout.xml.*;
import java.io.File;

/**
 * defines an Object that contains the name of an Image for painting on
 * a GridTile.
 *
 * <p>Title: CATS - Crandic Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2004, 2009</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */

public class ImageItem
    implements Itemizable {

  /**
   * The XML tag for identifying an Image file name.
   */
  static final String XML_TAG = "IMAGE";

  /**
   * The file name.
   */
  private String ImageName;

  /**
   * The ImageFrill for painting the Image.
   */
  private ImageFrill MyFrill;

  /**
   * The Section containing the ImageItem.
   */
  private Section ImageSection;

  /**
   * The GridTile where the Image is painted.
   */
  private GridTile ImageTile;

  /**
   * the Constructor.
   */
  public ImageItem() {
  }

  /**
   * retrieves the name of the Image file.
   *
   * @return the name of the file, if there is one.  Null should not be
   * returned because if the file name is null this Object should not exist.
   */
  public String getImageName() {
    return new String(ImageName);
  }

  /**
   * sets the name of the Image file.
   *
   * @param image is the name of the Image file.  Null should not be a
   * valid value.
   */
  public void setImageName(String image) {
    if ( (image == null) || (image.trim() == "")) {
      if (ImageSection != null) {
        ImageSection.replaceImage(null);
      }
    }
    else {
      ImageName = image;
    }
  }

  /*
   * tells the sub-component where its Section is, so that the sub-component
   * can replace itself and retrieve anything else it needs from the Section.
   */
  public void addSelf(Section sec) {
    sec.replaceImage(this);
    ImageSection = sec;
  }

  /**
   * retrieves the enclosing Section.
   * @return the Section containing the image.
   */
  public Section getSection() {
    return ImageSection;
  }

  /**
   * asks the sub-component to create a copy of itself.
   * @param sec is the Section wher ethe copy goes
   * @return an exact duplicate of the image.
   */
  public Itemizable copy(Section sec) {
    ImageItem newCopy = new ImageItem();
    if (ImageName != null) {
      newCopy.ImageName = new String(ImageName);
      newCopy.ImageSection = sec;
    }
    return newCopy;
  }

  /*
   * adds the SecName to the GridTile.
   */
  public void install(GridTile tile) {
    File fName;

    if ( (MyFrill == null) && (ImageName != null)) {
      if (ImageName != null) {
        fName = new File(ImageName);
        if (fName.exists()) {
          if (fName.canRead()) {
            MyFrill = new ImageFrill(ImageName);
            tile.requestUpdate();
          }
          else {
            log.warn("Cannot read Image file " + ImageName);
            System.out.println("Cannot read Image file " + ImageName);
          }
        }
        else {
          log.warn("Could not find Image file " + fName);
          System.out.println("Could not find Image file " + fName);
        }
      }

      ImageTile = tile;
      if (MyFrill != null) {
        ImageTile.addFrill(MyFrill);
      }
    }
  }

  /*
   * asks the sub-component if it has anything to paint on the Screen.
   *
   * @return true if it does and false if it doen't.
   */
  public boolean isVisible() {
    return true;
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
    ImageName = eleValue;
    return null;
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
    return new String("A " + XML_TAG + " cannot contain an Element ("
                      + objName + ").");
  }

  /*
   * returns the XML Element tag for the XMLEleObject.
   *
   * @return the name by which XMLReader knows the XMLEleObject (the
   * Element tag).
   */
  public String getTag() {
    return new String(XML_TAG);
  }

  /*
   * tells the XMLEleObject that no more setValue or setObject calls will
   * be made; thus, it can do any error checking that it needs.
   *
   * @return null, if it has received everything it needs or an error
   * string if something isn't correct.
   */
  public String doneXML() {
    if (ImageName == null) {
      log.warn("Missing file name for an " + XML_TAG + " XML Element.");
      System.out.println("Missing file name for an " + XML_TAG +
                         " XML Element.");
    }
    return null;
  }

  /**
   * registers a SecNameFactory with the XMLReader.
   */
  static public void init() {
    XMLReader.registerFactory(XML_TAG, new ImageFactory());
  }

  static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(
      ImageFrill.class.getName());
}

/**
 * is a Class known only to the ImageItem class for creating Depots from
 * an XML document.  Its purpose is to pick up the Image file name.
 */
class ImageFactory
    implements XMLEleFactory {

  /*
   * tells the factory that an XMLEleObject is to be created.  Thus,
   * its contents can be set from the information in an XML Element
   * description.
   */
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
  public String addAttribute(String tag, String value) {
    return new String("An " + ImageItem.XML_TAG +
                      " XML Element has no Attributes ("
                      + tag + ").");
  }

  /*
   * tells the factory that the attributes have been seen; therefore,
   * return the XMLEleObject created.
   *
   * @return the newly created XMLEleObject or null (if there was a problem
   * in creating it).
   */
  public XMLEleObject getObject() {
    return new ImageItem();
  }
}