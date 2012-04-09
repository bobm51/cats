/* Name: GenericRecord.java
 *
 * What;
 *  This file is a data structure for collecting <tag:value> pairs into
 *  a Vector.  The Vector then forms a database record.
 */
package cats.layout.store;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import cats.common.Constants;
import cats.layout.xml.XMLEleFactory;
import cats.layout.xml.XMLEleObject;
import cats.layout.xml.XMLReader;

/**
 *  This file is a data structure for collecting <tag:value> pairs into
 *  a Vector.  The Vector then forms a database record.
 * <p>Title: CATS - Computer Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2004, 2010, 2011</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */

public class GenericRecord
    extends Vector<FieldPair>
    implements XMLEleObject {

  /**
   * is the XML Tag for identifying data records.
   */
  public static final String DATARECORD = "DATARECORD";

  /**
   * is the XML Tag for identifying edit control records.
   */
  public static final String EDITRECORD = "EDITRECORD";

  /**
   * is the TAG in the XML file for identifying this kind of Record.
   */
  protected String XmlTag;
  
  /**
   * is the tag on the StoredObject reference field.  This field
   * is internal to CATS and links a GenericRecord to the routines
   * That manipulate its contents.
   */
  public static final String STORED_OBJECT = "STORED_OBJECT";

  
  /**
   * is the tag on the status field.  The status field contains the results from
   * running the GenericRecord through the editor.
   */
  public static final String STATUS = "EDIT_STATUS";

  /**
   * The common values of the status.  Note that these are passed as values
   * and not copied; thus, the "=" operator can be used for checking for
   * equality, rather than "equal()".
   */
  
  /*
   * the editor has deleted the GenericRecord
   */
  public static final String DELETED = "DELETED";
 
  /**
   * the editor has changed a field
   */
  public static final String CHANGED = "CHANGED";
  
  /**
   * the GenreicRecord has not been changed
   */
  public static final String UNCHANGED = "UNCHANGED";
  
  /**
   * the GenericRecord was just created
   */
  public static final String CREATED = "CREATED";
  
  /**
   * is another constructor.
   * @param tag is the XML tag for identifying the Records.
   * @param size is the initial size of the Vector.
   */
  public GenericRecord(String tag, int size) {
    super(size);
    XmlTag = tag;
  }

  /**
   * is the constructor.
   *
   * @param tag is the XML tag for identifying the Records.
   * Records.
   */
  public GenericRecord(String tag) {
    super();
    XmlTag = tag;
  }

  /**
   * is the constructor.
   */
  public GenericRecord() {
    super();
    XmlTag = "";
  }

  /**
   * replaces a FieldPair.  If the tag is not in the GenericRecord, it is
   * dropped.  
   *
   * @param newPair is the replacement FieldInfo.
   */
  public void replacePair(FieldPair newPair) {
//    boolean match = false;
    FieldPair oldPair = findPair(newPair.FieldTag);
//    for (Enumeration<FieldPair> exists = elements(); exists.hasMoreElements() && !match; ) {
//      oldPair = exists.nextElement();
//      if ( (oldPair.FieldTag != null) && (newPair.FieldTag != null) &&
//          (oldPair.FieldTag.equals(newPair.FieldTag))) {
//        oldPair.FieldValue = newPair.FieldValue;
//        match = true;
//      }
//    }
    if (oldPair != null) {
      oldPair.FieldValue = newPair.FieldValue;
    }
  }

  /**
   * replaces the value piece of a field pair.  If the field does not
   * exist, it is added.
   *
   * @param tag is the tag identifying the field.
   * @param value is the value for the field.
   * @return true if the value is replaced and false if it is not.
   * It would not be replaced if it the old and new values are the same.
   */
  public boolean replaceValue(String tag, Object value) {
    FieldPair pair = findPair(tag);
    boolean changed = true;
    if (pair == null) {
      add(new FieldPair(tag, value));
    }
    else {
      changed = !value.equals(pair.FieldValue);
      pair.FieldValue = value;
    }
    // This is the place to broadcast the change
    return changed;
  }

  /**
   * replaces the contents of the Vector.  If a Field with the same name
   * does not exist, nothing is replaced.
   *
   * @param newValues is a GenericRecord.  The value associated with
   * a FieldTag that matches an existing FieldTag, replaces the existing
   * value.
   */
//  public void replaceValues(GenericRecord newValues) {
//    FieldPair newPair;
//    FieldPair oldPair;
//    for (Enumeration<FieldPair> e = newValues.elements(); e.hasMoreElements(); ) {
//      newPair = e.nextElement();
//      if (null != (oldPair = findPair(newPair.FieldTag))) {
//        oldPair.FieldValue = newPair.FieldValue;
//      }
//    }
//  }

  /**
   * searches for a FieldPair with a particular tag.
   *
   * @param tag is the FieldTag being requested.  It may not be null
   * because all fields have non-null tags.
   *
   * @return the FieldPair with a matching tag or null, if none is found.
   */
  public FieldPair findPair(String tag) {
    FieldPair pair;
    if (tag != null) {
      for (Enumeration<FieldPair> e = elements(); e.hasMoreElements(); ) {
        if (tag.equals( (pair = e.nextElement()).FieldTag)) {
          return pair;
        }
      }
    }
    return null;
  }

  /**
   * searches for a FieldPair with a particular Tag, then returns the value
   * Field from the pair.
   *
   * @param tag is the tag for the desired Field.
   *
   * @return the Value of a selected Field in a GenericRecord.
   */
  public Object findValue(String tag) {
    FieldPair pair = findPair(tag);
    if (pair != null) {
      return pair.FieldValue;
    }
    return null;
  }

  /**
   * returns the Key Field - the active object defined by the GenericRecord.
   *
   * @return the active object.
   */
  public StoredObject getActiveReference() {
    return (StoredObject) findValue(STORED_OBJECT);
  }

  /**
   * sets the status of the GenericRecord.  Typically, it should not be
   * null or the empty String
   * @param status describes the status of the GenericRecord due to editing
   */
  public void setStatus(String status) {
    findPair(STATUS).FieldValue = status;
  }

  /**
   * retrieves the status of the GenericRecord, due to editing.  Typically,
   * it should not be null or the empty String.
   * @return the edit status
   */
  public String getStatus() {
    return (String) findValue(STATUS);
  }

  /**
   * locates the FieldPair containing the record key
   * @return the FieldPair containing the record key, which should never be null
   */
  public FieldPair getRecordKeyPair() {
    String keyTag = (String) findValue(FieldInfo.KEY_TAG);
    if (keyTag != null) {
      return findPair(keyTag);
    }
    return null;
  }

  /**
   * locates the FieldPair containing the record's key.  If found,
   * it returns the value of the record key.
   * @return the unique string used to identify the record.  Null
   * may be returned if the key pair does not exist or the key has
   * not been defined.  Though unlikely, the key could possibly be
   * the empty string (""), so clients should test for both null
   * and empty.
   */
  public String getRecordKey() {
    FieldPair pair = getRecordKeyPair();
    if (pair != null) {
      return (String) pair.FieldValue;
    }
    return null;
  }
  
  /**
   * is a predicate for testing if the GenericRecord contains a particular
   * FieldPair.
   *
   * @param request is the FieldPair being searched for.
   *
   * @return true if this GenericRecord has a FieldPair whose contents
   * match the request.  false is returned if it doesn't.  Multiple matches
   * should not happen because the Field Tags should be unique.
   */
  public boolean doesInclude(FieldPair request) {
    FieldPair pair = findPair(request.FieldTag);
    if (pair != null) {
      if (request.FieldValue == null) {
        return ( (pair.FieldValue == null) ? true : false);
      }
      if ( (pair.FieldValue != null) &&
          (request.FieldValue.equals(pair.FieldValue))) {
        return true;
      }
    }
    return false;
  }

  /**
   * converts the Vector of Field Pairs to a Vector of Values.
   * 
   * @param fieldTags is the list of tag names being selected
   * 
   * @return a TaggedVector which is the values from the
   * GenericRecord.
   */
//  public TaggedVector extractValues(ArrayList<Object> fieldTags) {
//    TaggedVector values = new TaggedVector(size());
//    FieldPair pair;
//    for (Iterator<Object> iter = fieldTags.iterator(); iter.hasNext(); ) {
//      if (null != (pair = findPair((String) iter.next()))) {
//        if ((pair.FieldValue != null) && ListSpec.class.isAssignableFrom(pair.FieldValue.getClass())) {
//          values.add(pair.FieldValue.toString());
//        }
//        else {
//          values.add(pair.FieldValue);
//        }
//      }
//    }
//    values.setTag(this);
//    return values;
//  }

  /**
   * converts the record to a String. Each FieldPair is a token in the String.
   * The tokens are separated by tab characters.  Each token is of the form
   * 'tag="value"'.  The key field is excluded because it is an internal
   * reference.
   * 
   * @return the GenericRecord as a String
   */
  public String valueString() {
    String result = new String();
    FieldPair pair;
    for (Iterator<FieldPair> iter = iterator(); ;){
      pair = iter.next();
      if (!FieldInfo.KEY_TAG.equals(STORED_OBJECT)) {
        result = result.concat(pair.toString());
      }
      if (!iter.hasNext()) {
        break;
      }
      result = result.concat(Constants.FS_STRING);
    }   
    return result;
  }

  /**
   * constructs a String that is the value of the record key.  For
   * a FieldInfo, this is just the String that is the key.  For
   * higher level objects, it invokes a method in the StoredObject to
   * list the volatile data that is not stored in the GenericRecord. 
   * @param keyValue is the FieldPair containing the key.  This parameter
   * is not needed as the GenericRecord could find the key, but since it
   * is already in hand, it is passed in.
   * @return a String that contains information about the key.
   */
  protected String expandKey(FieldPair keyValue) {
    return ((StoredObject)keyValue.FieldValue).getHiddenValues();
  }

  /**
   * validates the contents of the GenericRecord against the description
   * vector.  Every tag in the GenericRecord must have a description in the
   * format vector; every description in the format vector must have a
   * FieldPair in GenericRecord.  Furthermore, the value field of each
   * FieldPair must be of the type required by the format description.
   * @param format is the Vector of field descriptions
   */
//  public void checkValues(FieldVector format) {
//    FieldPair pair;
//    FieldInfo info;
//    String tag;
//    
//    // remove any fields without descriptors and set the class of those
//    // with descriptors
//    for (Iterator<FieldPair> iter = iterator(); iter.hasNext(); ) {
//      pair = iter.next();
//      info = format.getFieldInfo(pair.FieldTag);
//      if (info == null) {
//        iter.remove();
//      }
//      else {
//        pair.verifyClass(info.extractClass());
//      }
//    }
// 
//    // add any missing fields
//    for (Iterator<FieldInfo> iter = format.iterator(); iter.hasNext(); ) {
//      info = iter.next();
//      tag = info.getKeyField();
//      pair = findPair(tag);
//      if (pair == null) {
//        add(info.createPair());
//      }
//    }
//  }

  /**
   * This method casts the FieldValue part of each FieldPair in a GenericRecord
   * to the required type.
   *
   * @param format is a FieldVector that describes each field in the GenericRecord
   */
//  public void castRecord(FieldVector format) {
//    FieldPair pair;
//    Class<?> requiredClass;
//    String value;
//    for (Enumeration<FieldPair> e = elements(); e.hasMoreElements(); ) {
//      pair = e.nextElement();
//      requiredClass = ClassSpec.toClass(format.findClass(pair.FieldTag));
//      value = ((String) pair.FieldValue).trim();
//      if (Boolean.class.equals(requiredClass)) {
//        if (value.equals("")) {
//          value = "true";
//        }
//        pair.FieldValue = new Boolean(value);
//      }
//      else if (Integer.class.equals(requiredClass)) {
//        if (value.equals("")) {
//          value = "0";
//        }
//        pair.FieldValue = new Integer(value);
//      }
//      else if (ClassSpec.class.equals(requiredClass)) {
//        if (value.equals("")) {
//            value = String.class.toString();
//        }
//        pair.FieldValue = ClassSpec.normalizeClassName(value);
//      }
//    }
//  }

  /**
   * converts the record to a String. Each FieldPair is a token in the String.
   * The tokens are separated by tab characters.  Each token is of the form
   * 'tag="value"'.
   * 
   * @return the GenericRecord as a String
   */
  public String toString() {
    String result = new String();
    FieldPair pair;
    for (Iterator<FieldPair> iter = iterator(); ;){
      pair = iter.next();
      if (pair.FieldTag.equals(STORED_OBJECT)) {
//        result = result.concat(pair.toString());
        result = result.concat(expandKey(pair));
      }
      else {
        result = result.concat(pair.toString());
      }
      if (!iter.hasNext()) {
        break;
      }
      result = result.concat(Constants.FS_STRING);
    }   
    return result;
  }
  
  /**
   * makes a copy of this GenericRecord, except Saved is false.
   * The copy is intended to be used by the editor.
   * @return the copy
   */
  public GenericRecord copyRecord() {
      GenericRecord newRec = new GenericRecord(XmlTag, size());
      FieldPair pair;
      for (Enumeration<FieldPair> e = elements(); e.hasMoreElements(); ) {
          pair = e.nextElement();
          newRec.add(new FieldPair(pair.FieldTag, pair.FieldValue));
      }
      return newRec;
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
    return new String("A " + XmlTag + " cannot contain a text field ("
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
    return new String("A " + XmlTag + " cannot have an embedded object ("
                      + objName + ").");
  }

  /*
   * returns the XML Element tag for the XMLEleObject.
   *
   * @return the name by which XMLReader knows the XMLEleObject (the
   * Element tag).
   */
  public String getTag() {
    return new String(XmlTag);
  }

  /*
   * tells the XMLEleObject that no more setValue or setObject calls will
   * be made; thus, it can do any error chacking that it needs.
   *
   * @return null, if it has received everything it needs or an error
   * string if something isn't correct.
   */
  public String doneXML() {
    return null;
  }

  /**
   * registers a Factory for accepting a Store
   * with the XMLReader.
   *
   * @param tag is the XML Tag for identifying the kind of Generic Record.
   */
  static public void init(String tag) {
    XMLReader.registerFactory(tag, new RecordFactory(tag));
  }
}

/**
 * is a Class known only to the GenericRecord class for creating Records
 * from an XML document.
 */
class RecordFactory
    implements XMLEleFactory {

  /**
   * is the XML Tag of the product.
   */
  private String RecordTag;

  /**
   * is the GenericRecord being created.
   */
  private GenericRecord NewRecord;

  /**
   * is the constructor.
   *
   *@param tag is the XML tag of the Record.
   */
  RecordFactory(String tag) {
    RecordTag = tag;
  }

  /*
   * tells the factory that an XMLEleObject is to be created.  Thus,
   * its contents can be set from the information in an XML Element
   * description.
   */
  public void newElement() {
    NewRecord = new GenericRecord(RecordTag);
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
    NewRecord.add(new FieldPair(tag, value));
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
    return NewRecord;
  }
}
/* @(#)GenericRecord.java */
