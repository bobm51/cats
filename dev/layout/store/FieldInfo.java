/* Name: FieldInfo.java
 *
 * What;
 * is a data structure for holding the information needed for
 * defining fields in a record.  It creates meta-records.
 * The data structure is:
 * <ul>
 * <li>
 *   A mandatory flag.  When true, it means that the Field cannot be deleted.
 *   The flag cannot be changed.
 * <li>
 *   A key string.  This string is a mnenomic name for the Field.  It is used
 *   for identifying the field description.  It, also, cannot be changed.
 * <li>
 *   A visible flag.  If this flag is true, then the Field is used in the
 *   record.  If it is not set, then the Field is ignored.
 * <li>
 *   The label on the edit column for the field.
 * <li>
 *   An edit flag for protecting the contents from editing.
 * <li>
 *   A column width telling the table editor the preferred width.
 * <li>
 *   A SwingConstants names, describing the alignment in the JTable cell.
 * <li>
 *   The value of the field.
 * <li>
 *   The class of the value.  It is converted to a String when written to a
 *   file (and conversely from a String when read from a file) and placed in a
 *   drop down list.
 * </ul>
 * <p>
 * The editor needs to know which fields are user definable and which are used
 * by CATS.  The ones needed by CATS have the Mandatory flag set to true. In
 * addition, since the keys are used by CATS, they cannot be changed.  The
 * keys of user defined fields are created by an "f" followed by an integer.
 * The integer is 1 larger than the largest used in the key field.
 * <p>
 * To add a new property to a FieldInfo, do the following:
 * <ol>
 * <li>
 * create a Tag.  The Tag is used as the attribute identifier in the XML file
 * and in the property pair in the FieldInfo.
 * <li>
 * create a Label which is the column header for the field, when displayed
 * in the editor.
 * <li>
 * add a get method for retrieving the value of the property.
 * <li>
 * add a meta format record to MetaFieldStore
 * </ol>
 */
package cats.layout.store;

import cats.layout.xml.XMLEleFactory;
import cats.layout.xml.XMLEleObject;
import cats.layout.xml.XMLReader;

import java.util.Enumeration;

/**
 * is a data structure for holding the information needed for
 * defining fields in a record.  It creates meta-records.
 * The data structure is:
 * <ul>
 * <li>
 *   A mandatory flag.  When true, it means that the Field cannot be deleted.
 *   The flag cannot be changed.
 * <li>
 *   A key string.  This string is a mnenomic name for the Field.  It is used
 *   for identifying the field description.  It, also, cannot be changed.
 * <li>
 *   A visible flag.  If this flag is true, then the Field is used in the
 *   record.  If it is not set, then the Field is ignored.
 * <li>
 *   The label on the edit column for the field.
 * <li>
 *   An edit flag for protecting the contents from editing.
 * <li>
 *   A column width telling the table editor the preferred width.
 * <li>
 *   A SwingConstants names, describing the alignment in the JTable cell.
 * <li>
 *   The value of the field.
 * <li>
 *   The class of the value.  It is converted to a String when written to a
 *   file (and conversely from a String when read from a file) and placed in a
 *   drop down list.
 * </ul>
 * <p>
 * The editor needs to know which fields are user definable and which are used
 * by CATS.  The ones needed by CATS have the Mandatory flag set to true. In
 * addition, since the keys are used by CATS, they cannot be changed.  The
 * keys of user defined fields are created by an "f" followed by an integer.
 * The integer is 1 larger than the largest used in the key field.
 * <p>
 * To add a new property to a FieldInfo, do the following:
 * <ol>
 * <li>
 * create a Tag.  The Tag is used as the attribute identifier in the XML file
 * and in the property pair in the FieldInfo.
 * <li>
 * create a Label which is the column header for the field, when displayed
 * in the editor.
 * <li>
 * add a get method for retrieving the value of the property.
 * <li>
 * add a meta format record to MetaFieldStore
 * </ol>
 * <p>Title: CATS - Computer Automated Traffic System</p>
 * <p>Description: A program for dispatching trains on Pat Lana's
 * Crandic model railroad.
 * <p>Copyright: Copyright (c) 2004, 2010, 2011</p>
 * <p>Company: </p>
 * @author Rodney Black
 * @version $Revision$
 */

public class FieldInfo
    extends GenericRecord
    implements XMLEleObject {

  /**
   * is the default value for a narrow field.
   */
  public static final int NARROW_WIDTH = 50;

  /**
   * is the default value for a medium width field.
   */
  public static final int MEDIUM_WIDTH = 75;

  /**
   * is the default value for a normal, wide field.
   */
  public static final int WIDE_WIDTH = 125;

  /**
   * is the tag for identifying a FieldInfo Object in the XMl file.
   */
  public static final String XML_TAG = GenericRecord.EDITRECORD;

  /*
   * The following define Tags for identifying the properties in
   * the basic FieldInfo.
   */
  /**
   * is the XML tag for identifying the Field's mandatory flag.
   */
  public static final String MANDATORY_TAG = "FIELD_MANDATORY";

  /**
   * is the XML tag for identifying the Field's key.
   */
  public static final String KEY_TAG = "FIELD_KEY";

  /**
   * is the XML tag for identifying if the Field is used or not.
   */
  public static final String VISIBLE_TAG = "FIELD_VISIBLE";

  /**
   * is the tag for identifying the Field's Label.
   */
  public static final String LABEL_TAG = "FIELDLABEL";

  /**
   * is the String for controlling editing.
   */
  public static final String EDIT_TAG = "FIELD_EDIT";

  /**
   * is the width of each field in the record, when shown in the editor.
   */
  public static final String WIDTH_TAG = "FIELD_WIDTH";

  /**
   * is the text alignment in the JTable cell
   */
  public static final String ALIGNMENT_TAG = "ALIGNMENT";
  
  /**
   * is the value in the Field.
   */
  public static final String DEFAULT_TAG = "FIELD_DEFAULT";

  /**
   * is the name of the Class of values in the Field.
   */
  public static final String CLASS_TAG = "FIELD_CLASS";
  
  /**
   * is the String used to separate a field name from its class when
   * dumping the format.
   */
  public static final String FORMAT_SEPARATOR = ":";

  /*
   * The following are the labels that appear in edit screens for identifying
   * the FieldInfo properties.
   */

  /**
   * is the label for the Mandatory property.
   */
  public static final String MANDATORY_LABEL = "Mandatory";

  /**
   * is the label for the Field Name property.
   */
  public static final String KEY_LABEL = "Field Name";

  /**
   * is the label for the Visible property.
   */
  public static final String VISIBLE_LABEL = "Visible";

  /**
   * is the label for the Label property.
   */
  public static final String LABEL_LABEL = "Field Label";

  /**
   * is the label for the Edit property.
   */
  public static final String EDIT_LABEL = "Editable";

  /**
   * is the label for the column Width property.
   */
  public static final String WIDTH_LABEL = "Width";

  /**
   * is the label for the Alignment property.
   */
  public static final String ALIGNMENT_LABEL = "Alignment";
  
  /**
   * is the label for the Default value property.
   */
  public static final String DEFAULT_LABEL = "Default";

  /**
   * is the label for the Class of the value property.
   */
  public static final String CLASS_LABEL = "Class";

  /**
   * is the no argument constructor.
   */
  public FieldInfo() {
    super();
  }

  /**
   * the constructor.
   *
   * @param key is the name of the field.
   * @param visible is true if the field is being used.
   * @param label is the Label on the field.
   * @param edit is true if the field can be edited.
   * @param mandatory is true if the field cannot be removed.
   * @param width is the width of the field.
   * @param align is the name of the alignment value
   * @param fDefault is the default value of Objects in the Field.
   * @param fClass is the class of Objects in the Field.
   */
  public FieldInfo(String key, boolean visible, String label, boolean edit,
                   boolean mandatory, int width, String align, Object fDefault, Class<?> fClass) {
    add(new FieldPair(KEY_TAG, key));
    add(new FieldPair(VISIBLE_TAG, new Boolean(visible)));
    add(new FieldPair(LABEL_TAG, label));
    add(new FieldPair(EDIT_TAG, new Boolean(edit)));
    add(new FieldPair(MANDATORY_TAG, new Boolean(mandatory)));
    add(new FieldPair(WIDTH_TAG, new Integer(width)));
    add(new FieldPair(ALIGNMENT_TAG, align));
    add(new FieldPair(DEFAULT_TAG, fDefault));
    add(new FieldPair(CLASS_TAG, fClass));
  }

  /**
   * constructs a FieldInfo from a GenericRecord.  This constructor is
   * inherently unsafe because it performs a downcast.  The GenericRecord
   * being cloned should have the correct properties or subsequent uses of
   * the FieldInfo will be doomed.
   *
   * @param unSafeRecord is the GenericRecord being downcast.
   */
  public FieldInfo(GenericRecord unSafeRecord) {
    for (Enumeration<FieldPair> e = unSafeRecord.elements(); e.hasMoreElements(); ) {
      add(e.nextElement());
    }
    checkValue();
  }

  /**
   * retrieves the mandatory flag.
   *
   * @return true if the field cannot be deleted; false if it can.
   */
  public boolean getMandatory() {
    return ( (Boolean) findPair(MANDATORY_TAG).FieldValue).booleanValue();
  }

  /**
   * returns the internal name of the Field.
   *
   * @return the value of the key Field.
   */
  public String getKeyField() {
    return new String( (String) findPair(KEY_TAG).FieldValue);
  }

  /**
   * returns the visible flag.
   *
   * @return true if the Field is being used and false if it is not being
   * used.
   */
  public boolean getVisible() {
    return ( (Boolean) findPair(VISIBLE_TAG).FieldValue).booleanValue();
  }

  /**
   * returns the Label.  This is the string that labels the column
   * when the record is edited.
   *
   * @return the Label of the field.  It cannot be null.
   */
  public String getLabel() {
    return new String( (String) findPair(LABEL_TAG).FieldValue);
  }

  /**
   * returns the Edit flag.
   *
   * @return true if the editor will allow the user to change the value
   * of the field and false if it should not be changed.
   */
  public boolean getEdit() {
    return ( (Boolean) findPair(EDIT_TAG).FieldValue).booleanValue();
  }

  /**
   * is called to extract the width of the column in the editor.
   *
   * @return the width of the column.
   */
  public int getWidth() {
    FieldPair pair = findPair(WIDTH_TAG);
    if (pair.FieldValue.getClass().equals(Long.class)) {
      pair.FieldValue = new Integer(((Long)pair.FieldValue).intValue());
    }
    return ( ((Integer) pair.FieldValue).intValue());
  }

  /**
   * is called to extract the text alignment property.
   * 
   * @return the name of the constant from SwingConstants
   */
  public String getAlignment() {
      return new String((String) findPair(ALIGNMENT_TAG).FieldValue);
  }
  
  /**
   * is called to set the width of the column in the editor.
   *
   * @param width is the width.
   */
  public void setWidth(int width) {
    findPair(WIDTH_TAG).FieldValue = new Integer(width);
  }

  /**
   * is called to return the value of objects in the Field.
   * @return the default value of the Object being edited.
   */
  public Object getFieldDefault() {
    return findPair(DEFAULT_TAG).FieldValue;
  }

  /**
   * is called to return the name of the Class of objects in the Field.
   * @return the class of the Object being edited.
   */
  public Class<?> getFieldClass() {
    return (Class<?>) findPair(CLASS_TAG).FieldValue;
  }

  /**
   * creates a FieldPair (tagged value) from the default value.
   *
   * @return a new FieldPair using the FieldInfo name and DefaultValue.
   */
  public FieldPair createPair() {
    FieldPair pair = new FieldPair( (String) findPair(KEY_TAG).FieldValue,
        getFieldDefault());
    pair.verifyClass(getFieldClass());
    return pair;
  }

  /**
   * ensures that the Value property is the same class as required by a
   * Class name.
   *
   * @param classType is the Class that the Value property should
   * be an instance of.
   */
  public void checkValue(Class<?> classType) {
    FieldPair pair = findPair(DEFAULT_TAG);
    if (classType == null) {
        classType = String.class;
    }
    if (pair == null) {
      add(new FieldPair(DEFAULT_TAG, null));
    }
    else {
    	pair.verifyClass(classType);
    }
  }

  /**
   * ensures that the Value Property is the same class as required by the
   * Class name in the Class property.
   */
  public void checkValue() {
    checkValue( (Class<?>) findPair(CLASS_TAG).FieldValue);
  }

  /**
   * This method makes a copy of the FieldInfo, ensuring that the CLASS
   * field is a class type and not the String name of the type.
   * @return a new FieldInfo which is a copy of this one.
   */
  public FieldInfo copyFields() {
      return new FieldInfo(
              getKeyField(),
              getVisible(),
              getLabel(),
              getEdit(),
              getMandatory(),
              getWidth(),
              getAlignment(),
              getFieldDefault(),
              getFieldClass());
  }

  /**
   * This method converts a FieldInfo to a GenericRecord for editing
   * by a StoreTableModel.  A FieldInfo is a form of a GenericRecord
   * (tag:value pairs), but because of Java limitations on inheritance
   * with generics, the fields must be copied by hand.  In addition,
   * some conversions may be necessary with embedded classes.
   * 
   * @return a copy of the FieldInfo as a GenericRecord.
   */
//  public GenericRecord toGeneric() {
//      GenericRecord gen = new GenericRecord();
//      for (FieldPair element : this) {
//          gen.add(element);
//      }
//      return gen;
//  }

  /**
   * converts the Class name to an instance of the Class.
   *
   * @return the Class that the Field value belongs to.
   */
//  public Class<?> extractClass() {
//      return ClassSpec.toClass(getFieldClass());
//  }

  /**
   * ensures that the Value property is the same class as required by a
   * Class name.
   *
   * @param className is the name of the Class that the Value property should
   * be an instance of.
   */
//  public void checkValue(String className) {
//    FieldPair pair = findPair(DEFAULT_TAG);
//    if (className == null) {
//      className = ClassSpec.normalizeClassName(String.class.toString());
//    }
//    if (pair == null) {
//      add(new FieldPair(DEFAULT_TAG, null));
//    }
//    pair.verifyClass(ClassSpec.toClass(className));
//  }

  /**
   * constructs a String that is the value of the record key.  For
   * a FieldInfo, this is just the String that is the key.  For
   * higher level objects, it invokes a method in the StoredObject to
   * list the volatile data that is not stored in the GenericRecord. 
   * @param keyValue is the FieldPair containing the key.  This parameter
   * is not needed as the GenericRecord could find the key, but since it
   * is already in hand, it is passed in.
   * @return a String that contains inforamtion about the key.
   */
  protected String expandKey(FieldPair keyValue) {
    return new String(keyValue.toString());
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
    return new String("A " + XML_TAG + " cannot contain a text field ("
                      + eleValue + ").");
  }

  /*
   * is the method through which the object receives embedded Objects.
   *
   * @param objName is the name of the embedded object
   * @param objValue is the value of the embedded object
   *
   * @return null if the Object is acceptable or an error String
   * if it is not.
   */
  public String setObject(String objName, Object objValue) {
    String resultMsg = new String("A " + XML_TAG +
                                  " cannot contain an Element ("
                                  + objName + ").");
    return resultMsg;
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
    String resultMsg = null;
    if ( (getKeyField() == null) || (getKeyField().length() == 0)) {
      resultMsg = new String("Missing Name on a Field description.");
    }
    return resultMsg;
  }


  /**
   * registers a Factory for accepting a Store
   * with the XMLReader.
   *
   * @param tag is the XML Tag for identifying the kind of Generic Record.
   */
  static public void init(String tag) {
    XMLReader.registerFactory(tag, new FieldInfoFactory());
  }
}

  /**
   * is a Class known only to the GenericRecord class for creating Records
   * from an XML document.
   */
  class FieldInfoFactory
      implements XMLEleFactory {

    /**
     * is the GenericRecord being created.
     */
    private FieldInfo NewRecord;

    /**
     * is the constructor.
     */
    FieldInfoFactory() {
    }

    /*
     * tells the factory that an XMLEleObject is to be created.  Thus,
     * its contents can be set from the information in an XML Element
     * description.
     */
    public void newElement() {
      NewRecord = new FieldInfo();
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
/* @(#)FieldInfo.java */
