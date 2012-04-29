/* Name: TaggedVector.java
 *
 * What;
 *  This class is a wrapper around a Vector.  The Vector is needed by the
 *  JTable and TableModel.  The added Tag is for identifying the GenericRecord
 *  containing the contents of the Vector.
 */
package cats.layout.store;

import java.util.Vector;
/**
 *  This class is a wrapper around a Vector.  The Vector is needed by the
 *  JTable and TableModel.  The added Tag is for identifying the GenericRecord
 *  containing the contents of the Vector.
 * <p>Title: CATS - Computer Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2005, 2010, 2011</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version 1.0
 */

public class TaggedVector extends Vector<Object> {

//  /**
//   * is the Tag used for identifying this Vector.
//   */
//  private GenericRecord VectorTag;
//
//  /**
//   * constructs a TaggedVector with a known initial size.
//   *
//   * @param s is the initial size.
//   */
//  public TaggedVector(int s) {
//    super(s);
//  }
//
//  /**
//   * sets the Tag Value.
//   *
//   * @param tag is the tag.
//   */
//  public void setTag(GenericRecord tag) {
//    VectorTag = tag;
//  }
//
//  /**
//   * retrieves the Tag.
//   *
//   * @return the Tag associated with the Vector contents.
//   */
//  public GenericRecord getTag() {
//    return VectorTag;
//  }
}
