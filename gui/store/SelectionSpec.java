/* What: SelectionSpec.java
 *
 * What:
 *  This file creates an abstract Selector whose selection list is created
 *  dynamically from one of the CATS stores.  The concrete instances know
 *  which store and the methods to use for retrieving the list.
 */
package cats.gui.store;

import cats.layout.store.RecordVector;
import java.util.Vector;

/**
 *  This file creates an abstract Selector whose selection list is created
 *  dynamically from one of the CATS stores.  The concrete instances know
 *  which store and the methods to use for retrieving the list.
 * <p>Title: designer</p>
 * <p>Description: A program for designing dispatcher panels</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */

public abstract class SelectionSpec
    extends ListSpec {

  /**
   * is the Vector that contains the elements from the Store over which the
   * selection is made.
   */
  protected RecordVector<?> FilteredList;

  /**
   * is the Tag used to select the FiedPair in each GenericRecord for
   * the label place in the Selection Dialog.
   */
  protected String SelectionTag;

  /**
   * is the no argument constructor.
   */
  public SelectionSpec() {
    this("");
  }

  /**
   * is the single argument constructor.
   *
   * @param spec is a dummy String.
   */
  public SelectionSpec(String spec) {
    super(spec);
  }

  /**
   * is the sub-class specific method for building the Vector of labels
   * in the Selection dialog.
   *
   * @return the Vector of Strings that forms the contents of the Selection
   * Dialog.
   */
  protected Vector<String> createSpecificList() {
    return createList(initializeList(), SelectionTag);
  }

  /**
   * sets up the List from the specific store.
   *
   * @return the GenericRecords from the specific Store that from which a
   * selection can be made.
   */
  protected abstract RecordVector<?> initializeList();
}
/* @(#)SelectionSpec.java */