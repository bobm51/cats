/* Name: FieldVector.java
 *
 * What;
 *  This file is a specialized class derived from Vector, that is a Vector
 *  of FieldInfo.
 */
package cats.layout.store;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import cats.layout.xml.XMLEleFactory;
import cats.layout.xml.XMLEleObject;
import cats.layout.xml.XMLReader;

/**
 *  This file is a specialized class derived from Vector, that is a Vector
 *  of FieldInfo.
 * <p>Title: CATS - Computer Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2004, 2010, 2011</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */

public class FieldVector
    extends Vector<FieldInfo> implements XMLEleObject {

  /**
   * is the tag in the XML file for identifying this instantiation of the
   * RecordVector.
   */
  private String XmlTag;

  /**
   * is the tag in the XML file for identifying Vector components
   */
  private String RecordTag;
  
  /**
   * is the no-argument constructor
   */
  public FieldVector() {
    super();
    XmlTag = "";
    RecordTag = GenericRecord.EDITRECORD;
  }

  /**
   * constructor with known size.
   *
   * @param count is the initial size.
   * @param tag is the XML tag
   */
  public FieldVector(int count, String tag) {
    super(count);
    RecordTag = GenericRecord.EDITRECORD;
    XmlTag = tag;
  }

  /**
   * sets the tag by which the RecordVector is identified in the
   * XML file.
   * @param tag is the XML tag
   * @param recTag is the XML tag on the composite records
   */
//  public FieldVector(String myTag, String recTag) {
//    XmlTag = myTag;
//    RecordTag = recTag;
//  }
  
  /**
   * the usual ctor
   * @param fields are the default field definitions
   * @param tag is the XML tag for this FieldVector
   */
  public FieldVector(FieldInfo[] fields, String tag) {
      XmlTag = tag;
      RecordTag = GenericRecord.EDITRECORD;
      loadFields(fields);
  }
  
  /**
   * loads the RecordStore, using FieldInfo fields as Fields, with defaults.
   *
   * @param seed is an array of Strings.  Each String is the key (FieldName)
   * of the resulting RecordStore.
   */
  public void loadFields(FieldInfo[] seed) {
    for (int key = 0; key < seed.length; ++key) {
      add(seed[key]);
    }
  }
  
  /**
   * this method extracts the names of all the visible fields from the
   * field description Vector.
   *
   * @return the FieldName property from each field in which the
   * FieldVisible flag is set.
   */
//  protected Vector<String> extractVisibleNames() {
//    FieldInfo info;
//    Vector<String> vec = new Vector<String>(size());
//    for (Enumeration<FieldInfo> e = elements(); e.hasMoreElements(); ) {
//      info = e.nextElement();
//      if (info.getVisible()) {
//        vec.add(info.getKeyField());
//      }
//    }
//    return vec;
//  }

  /**
   * returns a count of the number of visible Fields (columns).
   *
   * @return the number of FieldInfos in the EditControl Vector with the
   * Visible bit set.
   */
//  public int countVisible() {
//    int sum = 0;
//    FieldInfo info;
//    for (Enumeration<FieldInfo> e = elements(); e.hasMoreElements(); ) {
//      info = e.nextElement();
//      if (info.getVisible()) {
//        ++sum;
//      }
//    }
//    return sum;
//  }

  /**
   * creates a subset, containing only those elements with the Visible
   * Flag set.
   * @return A FieldVector containing the FieldInfos with the
   * Visible flag set.
   */
//  public FieldVector getVisibleInfos() {
//    FieldVector visible = new FieldVector(size());
//    FieldInfo info;
//    for (Enumeration<FieldInfo> e = elements(); e.hasMoreElements(); ) {
//      info = e.nextElement();
//      if (info.getVisible()) {
//        visible.add(info);
//      }
//    }
//    return visible;
//  }

  /**
   * searches the FieldInfos for one whose name matches the search name.
   *
   * @param match is the name of the FieldInfo being requested.  match should
   * not be null.
   *
   * @return the index of FieldInfo.  If not found, -1 is returned.
   */
//  public int getFieldIndex(String match) {
//    for (int index = 0; index < size(); ++index) {
//      if ( elementAt(index).getKeyField().equals(match)) {
//        return index;
//      }
//    }
//    return -1;
//  }

  /**
   * searches the FieldInfos for one whose name matches the search name.
   *
   * @param match is the name of the FieldInfo being requested.  match should
   * not be null.
   *
   * @return the FieldInfo if found; null, if not found.
   */
  public FieldInfo getFieldInfo(String match) {
//    int index = getFieldIndex(match);
//    if (index >= 0) {
//      return elementAt(index);
//    }
    String fieldName;
    for (FieldInfo element : this) {
      fieldName = element.getKeyField();
      if ((fieldName != null) && fieldName.equals(match)) {
        return element;
      }
    }
    return null;
  }

  /**
   * locates the index of the key field.
   *
   * @return the index of the FieldInfo whose KEY_TAG is "KEY_TAG".
   */
//  public int getKeyindex() {
//    FieldInfo info;
//    for (int iter = 0; iter < size(); ++iter) {
//      info = elementAt(iter);
//      if (getKeyField().equals(info.getKeyField())) {
//        return iter;
//      }
//    }
//    return 0;
//  }

  /**
   * determines the tag of the field containing the key.  To make the
   * algorithms work for all uses, the Key field tag is FieldInfo.KEY_TAG.
   *
   * @return the tag of the key field, the one that has unique values.  Null
   * should never be returned, but it will be if no key is found.
   */
  public String getKeyField() {
    FieldInfo info;
    String key;
    for (Enumeration<FieldInfo> e = elements(); e.hasMoreElements(); ) {
      info = e.nextElement();
      if (((key = info.getKeyField()) != null) && FieldInfo.KEY_TAG.equals(key)) {
        return (String) info.getFieldDefault();
      }
    }
    return null;
//    FieldInfo info;
//    for (Enumeration<FieldInfo> e = elements(); e.hasMoreElements(); ) {
//      info = e.nextElement();
//      if (info.getMandatory()) {
//        return info.getKeyField();
//      }
//    }
//    return null;
  }

  /**
   * creates a new GenericRecord, using the default values from each
   * FieldInfo.
   *
   * @param key is placed in the FieldPair in the key field.
   * @param tag is the tag for the GenericRecord.
   *
   * @return a GenericRecord of default values.
   */
  public GenericRecord createDefaultRecord(String tag, String key) {
//    int keyIndex = getKeyindex();
    String keyName = getKeyField();
    GenericRecord rec = new GenericRecord(tag, size());
    FieldInfo info;
    FieldPair pair;
    for (Enumeration<FieldInfo> e = elements(); e.hasMoreElements(); ) {
      info = e.nextElement();
      rec.add(pair = info.createPair());
      if (pair.FieldTag.equals(keyName)) {
        pair.FieldValue = key;
      }
//      if (pair.FieldTag.equals(GenericRecord.STORED_OBJECT)) {
//        ((StoredObject) pair.FieldValue).linkDescription(rec);
//      }
    }
//    if (keyIndex >= 0) {
//      rec.elementAt(keyIndex).FieldValue = key;
//    }
    return rec;

//    FieldInfo keyInfo = getFieldInfo(FieldInfo.KEY_TAG);
//    GenericRecord rec = new GenericRecord("", size());
//    FieldInfo info;
//    StoredObject newObject;
//    Class<? extends Object> objectClass;
//    for (Enumeration<FieldInfo> e = elements(); e.hasMoreElements(); ) {
//      info = e.nextElement();
//      if (info == keyInfo) {
//        objectClass = ClassSpec.toClass((String) info.findPair(FieldInfo.CLASS_TAG).FieldValue);
//        try {
//          newObject = (StoredObject) objectClass.newInstance();
//          newObject.linkDescription(rec);
//          rec.add(new FieldPair(FieldInfo.KEY_TAG, newObject));
//        }
//       catch (java.lang.IllegalAccessException iae) {
//         System.out.println("Illegal Access Exception creating " + objectClass);
//       }
//       catch (java.lang.InstantiationException ie) {
//       System.out.println("Instantiation Exception creating " + objectClass.toString());};
//      }
//      else {
//        rec.add(info.createPair());
//      }
//    }
//    return rec;
  }
  
  /**
   * This method starts by making a copy of the FieldVector.  Then it 
   * adjusts the CLASS fields of all contained FieldInfos to be classes,
   * rather than the String names of classes.  This copy is needed so that
   * the user can adjust column widths (the WIDTH field of each FieldInfo),
   * cancel the changes, and have the widths revert back to their previous
   * values.
   *   
   * @return a copy of the FieldVector
   * 
   */
  public FieldVector toFormatting() {
      FieldVector copy = new FieldVector(size(), getTag());
      for (Enumeration<FieldInfo> e = elements(); e.hasMoreElements(); ) {
          copy.add(e.nextElement().copyFields());
      }
      return copy;
  }
 
  /**
   * removes all data records (except the first).  Because there may be links between a field in
   * a data record and some other object, the records cannot just be left to
   * the Java garbage collector, so a destructor is called.
   * <p>
   * The data records in a FieldVector have no references to StoredObjects,
   * so this method can unconditionally remove all data records.  However,
   * the first FieldInfo is unique, it describes the link to the stored object,
   * and it must be in the FieldVector.  Since it is nor generated by designer and
   * must be preserved, it is copied from the previous incarnation.
   */
//  public void removeData() {
//    FieldInfo ref = elementAt(0);
//    clear();
//    add(ref);
//  }
  
  /**
   * is a method that runs through a whole RecordVector, fixing up each
   * GenericRecord to fit this format.
   * @param vec is the RecordVector being fixed up.
   */
//  public void syncUpVector(RecordVector<GenericRecord> vec) {
//      for (GenericRecord element : vec) {
//          syncUpRecord(element);
//      }
//  }
  
  /**
   * This method both verifies that a GenericRecord has all (and only all) the required
   * fields and that each field is of the appropriate class.
   * <p>
   * Each field that does not have a format description (e.g. a FieldInfo in this
   * FieldVector) is removed.
   * <p>
   * A field in the GenericRecord is created for each FieldInfo that is missing,
   * using the default value for FieldInfo.
   * <p>
   * The value of each field of the wrong class is replaced with the default value from
   * the FieldInfo.
   * 
   * @param rec is the GenericRecord being fixed up.
   */
//  public void syncUpRecord(GenericRecord rec) {
//      FieldInfo desc;
//      FieldPair pair;
//      // The first phase is to remove fields that do not have matching FieldInfos.
//      // For those that do match, this method ensures that the class of the value
//      // matches.
//      for (Iterator<FieldPair> element = rec.iterator(); element.hasNext(); ) {
//          pair = element.next();
//          desc = getFieldInfo(pair.FieldTag);
//          if (desc == null) {
//              element.remove();
//          }
//          else {
//              pair.verifyClass(desc.getFieldClass());
//          }
//      }
//      
//      // The second phase adds fields that are missing
//      for (Iterator<FieldInfo> element = iterator(); element.hasNext(); ) {
//          desc = element.next();
//          pair = rec.findPair(desc.getKeyField());
//          if (pair == null) {
//              rec.add(desc.createPair());
//          }
//      }
//  }

  /**
   * is a method that runs through a whole RecordVector, converting each
   * GenericRecord to fit this format.  The RecordVector should have been
   * read in  from an XML file, so all values are Strings.
   * @param vec is the RecordVector being converted.
   */
//  public void vectorFromXML(RecordVector<GenericRecord> vec) {
//    StoredObject so;
//    for (GenericRecord element : vec) {
//      recordFromXML(element);
//      if ((so = element.getActiveReference()) != null) {
//        so.linkDescription(element);
//      }
//      else {
//        log.warn("Missing StoredObject in a GenericRecord");
//      }
//    }
//  }
  
  /**
   * This method both verifies that a GenericRecord has all (and only all) the required
   * fields and that each field is of the appropriate class.  It is called to convert the
   * XML strings for each XML attribute into the appropriate class.
   * <p>
   * Each field that does not have a format description (e.g. a FieldInfo in this
   * FieldVector) is removed.
   * <p>
   * A field in the GenericRecord is created for each FieldInfo that that is missing,
   * using the default value for FieldInfo.
   * <p>
   * The value of each field is assumed to be a String representation of the real
   * value, created by "toName()" when the value was written to the XML file. 
   * 
   * @param rec is the GenericRecord being fixed up.
   */
  public void recordFromXML(GenericRecord rec) {
      FieldInfo desc;
      FieldPair pair;
      // The first phase is to remove fields that do not have matching FieldInfos.
      // For those that do match, this method converts from a String representation
      // of the value to the actual value.
      cleanseRecord(rec);
      
      // The second phase adds fields that are missing
      for (Iterator<FieldInfo> element = iterator(); element.hasNext(); ) {
          desc = element.next();
          pair = rec.findPair(desc.getKeyField());
          if (pair == null) {
              rec.add(desc.createPair());
          }
      }
  }

  /**
   * A method that walks through the fields on a GenericRecord and removes
   * all fields that are not defined in the FieldVector (i.e. do not have a matching
   * FieldInfo).  In addition, it ensures that the value for each field is of
   * the required type.
   * @param rec is the GenericRecord being cleaned up
   */
  public void cleanseRecord(GenericRecord rec) {
    FieldInfo desc;
    FieldPair pair;
    Class<?> requiredClass;
    for (Iterator<FieldPair> element = rec.iterator(); element.hasNext(); ) {
      pair = element.next();
      desc = getFieldInfo(pair.FieldTag);
      if (desc == null) {
        element.remove();
      }
      else {
        requiredClass = desc.getFieldClass();
        pair.verifyClass(requiredClass);
      }
    }
  }
  
  /**
   * constructs a list of the format store.  It drops the FieldInfos
   * that contain internal information.  The FieldInfos dropped are:
   * <ul>
   * <li>the reference to the StoredObject
   * <li>the edit status
   * </ul>
   * @return Fields as an Array list of string, with the format
   * records encoded as tag:value pairs in Strings
   */
  ArrayList<String> dumpFieldContents() {
    ArrayList<String> contents = new ArrayList<String>(size());
    String fieldTag;
    for (FieldInfo info : this) {
      fieldTag = info.getKeyField();
      if ((!GenericRecord.STORED_OBJECT.equals(fieldTag)) && (!GenericRecord.STATUS.equals(fieldTag))) {
        contents.add(info.toString());
      }
    }
    return contents;
  }

  /**
   * creates the database operation of a slice.  It selects
   * all FieldPairs, one from each record, that have a particular tag.
   * It adds the value from each pair, to the resulting list.
   * @param tag is the key on the field being requested
   * @return an ArrayList containing the values of all fields tagged
   */
//  public ArrayList<Object> getSlice(String tag) {
//    ArrayList<Object> result = new ArrayList<Object>(size());
//    for (Iterator<FieldInfo> iter = iterator(); iter.hasNext(); ) {
//      result.add(iter.next().findPair(tag).FieldValue);
//    }
//    return result;
//  }

  /**
   * searches the FieldVector for a FieldInfo whose key property has a
   * value of fieldTag
   * @param fieldTag
   * @return the FieldInfo if found; otherwise null.
   */
//  public FieldInfo findField(String fieldTag) {
//    FieldPair pair = new FieldPair(FieldInfo.KEY_TAG, fieldTag);
//    FieldInfo test;
//    for (Iterator<FieldInfo> iter = iterator(); iter.hasNext(); ) {
//      test = iter.next();
//      if (test.doesInclude(pair)) {
//        return test;
//      }
//    }
//    return null;
//  }
  
  /**
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
   * The embedded object should be a GenericRecord, in which all the value
   * pieces of all the FieldPairs are Strings.  The value pieces are
   * converted into the correct type when used (e.g. passed to the
   * editor).
   * <p>
   * The order of the FieldInfos in the FieldVector is important because it
   * dictates the order of the columns during editing.  Consequently, the
   * order is preserved.  When the FieldVector is added to the Store, the existing
   * FieldVector will be replaced with this one.
   * <p>
   * The fields that must be in each FieldInfo are known at the time the FieldInfo
   * is read from the XML file.  They are defined by the MetaFieldStore.
   * <p>
   * This method checks for duplicates FieldInfos with the same key.  The last
   * survives.
   *
   * @param objName is the name of the embedded object
   * @param objValue is the value of the embedded object
   *
   * @return null if the Object is acceptable or an error String
   * if it is not.
   */
  public String setObject(String objName, Object objValue) {
    String resultMsg = null;
    FieldPair pair;
    FieldInfo def;
    if (RecordTag.equals(objName)) {
        pair = ((GenericRecord) objValue).findPair(FieldInfo.KEY_TAG);
        if (pair == null) {
            resultMsg = new String(XmlTag + " has a malformed element missing a key tag: " + objName);
        }
        else {
            def = getFieldInfo((String) pair.FieldValue);
            if (def != null) {
                remove(def);
            }
            MetaFieldStore.FieldOfFields.recordFromXML((GenericRecord) objValue);
            def = new FieldInfo((GenericRecord) objValue);
            add(def);
        }
    }
    else {
      resultMsg = new String("A " + XmlTag + " cannot contain an Element ("
                             + objName + ").");
    }
    return resultMsg;
//    String resultMsg = null;
//    if (RecordTag.equals(objName)) {
//      add((FieldInfo) objValue);
//    }
//    else {
//      resultMsg = new String("A " + XmlTag + " cannot contain an Element ("
//                             + objName + ").");
//    }
//    return resultMsg;
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
   * be made; thus, it can do any error checking that it needs.
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
   * @param tag is the XML tag used for identifying FieldInfos
   * @param defFields is a structure used to hold the FieldInfos
   */
  public void init(String tag, FieldInfo[] defFields) {
//    XMLReader.registerFactory(XmlTag, new Factory(this, XmlTag));
    XMLReader.registerFactory(tag, new Factory(tag, defFields));
  }

  /**
   * is a Class known only to the RecordStore class for creating Fields
   * from an XML document.
   */
  class Factory
      implements XMLEleFactory {

    /**
     * is the product produced by the Factory.
     */
    private FieldVector Product;

    /**
     * is the XML Tag of the product.
     */
    private String PRODUCT_TAG;

    /**
     * are the initial fields
     */
    private final FieldInfo[] DEFAULT_FIELDS;

    /**
     * is the constructor.
     *
     * @param defFields is the product produced by the Factory.
     *
     *@param tag is the XML tag of the product.
     */
    Factory(String tag, FieldInfo[] defFields) {
      PRODUCT_TAG = tag;
      DEFAULT_FIELDS = defFields;
    }
//    Factory(FieldVector fieldVector, String tag) {
//      Product = fieldVector;
//      ProductTag = tag;
//    }

    /*
     * tells the factory that an XMLEleObject is to be created.  Thus,
     * its contents can be set from the information in an XML Element
     * description.
     */
    public void newElement() {
      Product = new FieldVector(DEFAULT_FIELDS, PRODUCT_TAG);
//      Product.removeData();
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

      resultMsg = new String("A " + PRODUCT_TAG + " XML Element cannot have a " +
                             tag +
                             " attribute.");
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
      return Product;
    }
  } 
  static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(FieldVector.class.getName());

}
/* @(#)FieldVector.java */
