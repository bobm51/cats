/* Name: StoredObject.java
 *
 * What:
 *  This file is an interface implemented by the Objects described in
 *  AbstractStores.  It is used for automatically creating an instance
 *  of one of those objects and telling linking the newly created
 *  object to its decsription.
 */
package cats.layout.store;

/**
 *  This file is an interface implemented by the Objects described in
 *  AbstractStores.  It is used for automatically creating an instance
 *  of one of those objects and linking the newly created
 *  object to its description.
 * <p>Title: CATS - Computer Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2005, 2010</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */

public interface StoredObject {

  /**
   * is the method used to give an Object its initial description.
   *
   * @param description is the GenericRecord describing the Object.
   */
  public void linkDescription(GenericRecord description);

  /**
   * is invoked to tell the Object that the description should be updated.
   * This happens when a copy of the GenericRecord is changed.  The parameter
   * will usually not be the GenericRecord, so the StoredObject should pass
   * the changes along to its GenericRecord.
   *
   * @param description is the updated GenericRecord describing the changes.
   */
  public void updateDescription(GenericRecord description);

  /**
   * is invoked to request that the value of some field be changed.
   * @param change is the FieldPair of the change.  The FieldPair tag identifies
   * the field being changed.  The FieldPair value is the new value of the field.
   */
//  public void requestChange(FieldPair change);
  
  /**
   * is invoked to tell the Object that it is being deleted.  It should
   * clean up any associations it has with other Objects, then remove it
   * description from its Store.
   */
  public void destructor();
  
  /**
   * is invoked to retrieve internal values, formatted in a String
   * as tag=value subStrings.  The derived class decides which
   * values (and tags) it wants to expose.
   * @return a String containing "tag=value" substrings.
   */
  public String getHiddenValues();
}
/* @(#)StoredObject.java */