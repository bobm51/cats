/* Name: RecordVector.java
 *
 * What;
 *  This file is a specialized class derived from Vector, that is a Vector
 *  of Vectors.  Each member Vector contains FieldPairs, where the Tag is
 *  the name of a Field and the Value piece is the Value of the Field.
 */
package cats.layout.store;

import java.util.Enumeration;
import java.util.Vector;
import cats.layout.store.FieldPair;
import cats.layout.xml.XMLEleFactory;
import cats.layout.xml.XMLEleObject;
import cats.layout.xml.XMLReader;

/**
 *  This file is a specialized class derived from Vector, that is a Vector
 *  of Vectors.  Each member Vector contains FieldPairs, where the Tag is
 *  the name of a Field and the Value piece is the Value of the Field.
 * <p>Title: CATS - Computer Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2005, 2010</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version 1.0
 */

/**
 * @param <T> is the type of elements contained in the RecordVector.
 * There are two choices - GenericRecord or FieldInfo
 */
public class RecordVector<T extends GenericRecord>
    extends Vector<GenericRecord> implements XMLEleObject {

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
   * is the filter used for determining how a GenericRecord is handled by
   * the StoreTableModel.
   */
  protected SelectionStrategy MyFilter;
  
  /**
   * constructor with known size.
   *
   * @param count is the initial size.
   */
  public RecordVector(int count) {
    super(count);
  }

  /**
   * null argument constructor
   */
//  public RecordVector() {
//    super();
//  }

  /**
   * sets the tag by which the RecordVector is identified in the
   * XML file.
   * @param tag is the XML tag
   * @param recTag is the XML tag on the composite records
   */
//  public RecordVector(String myTag, String recTag) {
//    XmlTag = new String(myTag);
//    RecordTag = new String(recTag);
//  }
  
  /**
   * constructs a new instance of a RecordVector
   * @param myTag is the TAG by which the RecordVector is known in the XML file
   */
  public RecordVector(String myTag) {
      XmlTag = new String(myTag);
      RecordTag = new String(GenericRecord.DATARECORD);
    }

  /**
   * creates and returns a copy of the RecordVector.  This is intended
   * for use in editing.  If the editing is canceled, the RecordVector
   * is discarded.
   * 
   * @return a copy of the RecordVector
   */
  public RecordVector<T> makeCopy() {
      RecordVector<T> r = new RecordVector<T>(XmlTag);
      for (GenericRecord element : this) {
          r.add(element.copyRecord());
      }
      return r;
  }

  /**
   * creates and returns a duplicate of the RecordVector.  Unlike makeCopy(),
   * the contents of the Vector are the same - not copies - of the original.
   * @return a duplicate of the RecordVector.  It is intended that the contents
   * be references to the same records.
   */
  public RecordVector<T> duplicate() {
    RecordVector<T> r = new RecordVector<T>(XmlTag);
    for (GenericRecord element : this) {
      r.add(element);
    }
    return r;
  }
  
  /**
   * sets the selection strategy
   * @param strat is the selection strategy
   */
  public void setStrategy(SelectionStrategy strat) {
      MyFilter = strat;
  }
  
  /**
   * searches the RecordVector for a Record with a FieldPair matching
   * request.  If one is found, the Record is returned.  Though it is
   * assumed that request is for a key field:value pair, it need not
   * be.  If start is null, searching begins on the first Record in
   * RecordVector.  If start is not null and start can be found, searching
   * resumes at the following Record.
   *
   * @param request is a FieldPair that is the search pattern (wild cards
   * are not supported).
   * @param start is a Record assumed to be in the RecordVector.  If it is
   * null, searching begins at the beginning.  Otherwise, searching begins
   * at the following Record.
   *
   * @return the first Record found that contains request or null if none
   * is found.
   */
  @SuppressWarnings("unchecked")
public T search(FieldPair request, GenericRecord start) {
    T first;
    Enumeration<GenericRecord> e = elements();

    if (start != null) {
      while (e.hasMoreElements()) {
        first = (T) e.nextElement();
        if (first == start) {
          break;
        }
      }
    }
    while (e.hasMoreElements()) {
      first = (T) e.nextElement();
      if (first.doesInclude(request)) {
        return first;
      }
    }
    return null;
  }

  /**
   * is called by the TableModel to query if a GenericRecord should be
   * displayed or not.
   * @param rec is the GenericRecord
   * @return true if it should be displayed and false if not.  The default is
   * true.
   */
  public boolean isVisible(T rec) {
      if (MyFilter != null) {
          return MyFilter.isSelected(rec);
      }
      return true;
  }

  /**
   * is called to hide a GenericRecord on the JTable
   * @param rec is the GenericRecord
   * @param reason is an optional String to place in the GenericRecord
   * status field.
   */
  public void hide(T rec, String reason) {
      if (MyFilter != null) {
          MyFilter.unselect(rec, reason);
      }
  }
  
  /**
   * is called to determine if a GenericRecord can be deleted or not.
   * @param rec is the GenericRecord
   * @return true if it can be deleted and false if not.  The default is
   * true.
   */
  public boolean isUnProtected(T rec) {
      if (MyFilter != null) {
          return MyFilter.isDeletable(rec);
      }
      return true;
  }

  /**
   * is called to determine if a specific field in a generic record
   * can be edited.
   * @param rec is the record in question
   * @param fieldTag is the tag on the field
   * @return true if it can be edited and false if not
   */
  public boolean isFieldEditable(T rec, String fieldTag) {
    if (MyFilter != null) {
      return MyFilter.isEditable(rec, fieldTag);
    }
    return true;
  }
  
  /**
   * is invoked to re-initialize the RecordStore
   */
//  void reinit() {
//      clear();
//  }
  
  /**
   * searches the Vector contents for a GenericRecord with a particular key.
   * If found, it is replaced with the paramater.  If not found, the parameter
   * is added.
   *
   * @param genRec is the GenericRecord being added or replacing a record.  It must
   * have been verified against the FieldInfo Store to contain all the fielsd.
   */
//  public void replaceRecord(GenericRecord genRec) {
//    FieldPair pair = genRec.getRecordKeyPair();
//    GenericRecord existing = search(pair, null);
//    if (existing != null) {
//      removeElement(existing);
//    }
//    add(genRec);
//  }
  
  /**
   * removes all data records.  Because there may be links between a field in
   * a data record and some other object, the records cannot just be left to
   * the Java garbage collector, so a destructor is called.
   */
//  public void removeData() {
//    StoredObject so;
//    GenericRecord rec;
//    for (Enumeration<GenericRecord> e = elements();
//         e.hasMoreElements(); ) {
//      rec = e.nextElement();
//      so = rec.getActiveReference();
//      if ( so != null)  {
//        so.destructor();
//      }
//      removeElement(rec);
//    }
//  }

  /**
   * converts all the value part of all the fields in all the GenericRecords
   * to the appropriate type 
   * @param format is the FieldVector containing the format information
   */
//  public void castVector(FieldVector format) {
//    for (Enumeration<GenericRecord> e = elements(); e.hasMoreElements(); ) {
//      e.nextElement().checkValues(format);
//    }
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
   * No validation is performed because the format of the GenreicRecords
   * is not known by the RecordVector.  The Store contains the format in
   * the FieldVector; thus, the record is validated when the RecordVector
   * is added to the Store.
   * 
   * @param objName is the name of the embedded object
   * @param objValue is the value of the embedded object
   *
   * @return null if the Object is acceptable or an error String
   * if it is not.
   */
  public String setObject(String objName, Object objValue) {
    String resultMsg = null;
    if (RecordTag.equals(objName)) {
      add((GenericRecord) objValue);
    }
    else {
      resultMsg = new String("A " + XmlTag + " cannot contain an Element ("
                             + objName + ").");
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
   */
//  public void init() {
//    XMLReader.registerFactory(XmlTag, new Factory(this, XmlTag));
//  }
  /**
   * registers a Factory for accepting a Store
   * with the XMLReader.
   * @param tag is the XML tag
   */
  public void init(String tag) {
    XMLReader.registerFactory(tag, new Factory(tag));
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
    private RecordVector<T> Product;

    /**
     * is the XML Tag of the product.
     */
    private String PRODUCT_TAG;

    /**
     * is the constructor.
     *
     * @param recordVector is the product produced by the Factory.
     *
     *@param tag is the XML tag of the product.
     */
//    Factory(RecordVector<T> recordVector, String tag) {
//      Product = recordVector;
//      ProductTag = tag;
//    }
    /**
     * is the constructor.
     *
     *@param tag is the XML tag of the product.
     */
    Factory(String tag) {
        PRODUCT_TAG = tag;
    }


    /*
     * tells the factory that an XMLEleObject is to be created.  Thus,
     * its contents can be set from the information in an XML Element
     * description.
     */
    public void newElement() {
//      Product.removeData();
      Product = new RecordVector<T>(PRODUCT_TAG);
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
 }
/* @(#)RecordVector.java */